package com.example.coursepaper;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.coursepaper.databinding.MainWindowFragmentBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class MainWindowFragment extends Fragment {

    private MainWindowFragmentBinding binding;
    private String themeName;
    private TextView collapsedText;
    private TextView expandedText;
    private LinearLayout textContainer;
    private GestureDetector gestureDetector;
    private TextView rightCollapsedText;
    private TextView rightExpandedText;
    private LinearLayout rightTextContainer;
    private boolean isLeftContainerExpanded;
    private boolean isRightContainerExpanded;
    private boolean isAnimating;
    private String selectedSubtheme;
    private TextView firstAuthorName;
    private Button addTextButton;
    private DatabaseReference lastAddedCommentRef;
    private Button editTextButton;
    private Button addTextButtonSecond;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = MainWindowFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        DrawerLayout drawer = ((MainActivity) getActivity()).getDrawerLayout();

        Bundle bundle = getArguments();
        if (bundle != null) {
            themeName = bundle.getString("themeName");
            ArrayList<SubTheme> subThemes = bundle.getParcelableArrayList("subThemes");
        }

        isLeftContainerExpanded = false;
        isRightContainerExpanded = false;
        isAnimating = false;

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        String subTheme = sharedPreferences.getString("subTheme", "");
        String realTheme = sharedPreferences.getString("mainTheme", "");

        Log.d("DIRECTORY", realTheme + " " + subTheme);

        collapsedText = binding.collapsedText;
        expandedText = binding.expandedText;
        textContainer = binding.textContainer;
        rightCollapsedText = binding.rightCollapsedText;
        rightExpandedText = binding.rightExpandedText;
        rightTextContainer = binding.rightTextContainer;
        firstAuthorName = binding.firstAuthorName;
        addTextButton = binding.addTextButton;
        editTextButton = binding.editTextButton;
        addTextButtonSecond = binding.addTextButtonSecond;

        gestureDetector = new GestureDetector(getActivity(), new GestureListener());


        textContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });

        rightTextContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Discussions").child(realTheme).child(subTheme).child("comments");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot commentSnapshot : snapshot.getChildren()) {
                    String authorId = commentSnapshot.child("authorId").getValue(String.class);
                    String text = commentSnapshot.child("text").getValue(String.class);
                    collapsedText.setText(text);
                    expandedText.setText(text);

                    DatabaseReference authorReference = FirebaseDatabase.getInstance().getReference("users").child(authorId);
                    authorReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String authorName = snapshot.child("username").getValue(String.class);
                            firstAuthorName.setText(authorName);
                            Log.d("AUTHOR_NAME", authorName);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("Firebase", "Failed to read value.", error.toException());
                        }
                    });

                    Log.d("COMMENTS", text);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Failed to read value.", error.toException());
            }
        });

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean isAdmin = snapshot.child("isAdmin").getValue(boolean.class);
                    if (isAdmin) {
                        addTextButton.setVisibility(View.VISIBLE);
                        editTextButton.setVisibility(View.VISIBLE);
                        addTextButtonSecond.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("Firebase", "Failed to read value.", error.toException());
                }
            });
        }

        addTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTextToSubtheme();
            }
        });

        addTextButtonSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTextToSubthemeSecond();
            }
        });

        editTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editLastAddedText();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private String getMainThemeNameFromSharedPreferences() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("mainTheme", "");
    }

    private String getSubThemeNameFromSharedPreferences() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("subTheme", "");
    }

    private void addTextToSubtheme() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            return;
        }
        String userId = currentUser.getUid();

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isAdmin = snapshot.child("isAdmin").getValue(boolean.class);
                if (!isAdmin) {
                    addTextButton.setVisibility(View.GONE);
                    editTextButton.setVisibility(View.GONE);
                    return;
                }

                DatabaseReference subthemeRef = FirebaseDatabase.getInstance().getReference("Discussions").child(getMainThemeNameFromSharedPreferences()).child(getSubThemeNameFromSharedPreferences()).child("comments");
                subthemeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                        dialogBuilder.setTitle("Добавить текст");

                        final EditText input = new EditText(getActivity());
                        input.setInputType(InputType.TYPE_CLASS_TEXT);
                        dialogBuilder.setView(input);

                        dialogBuilder.setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String text = input.getText().toString();
                                if ((!TextUtils.isEmpty(text)) && snapshot.hasChild("0")) {
                                    snapshot.getRef().child("0").child("text").setValue(text);
                                    snapshot.getRef().child("0").child("authorId").setValue(userId);
                                }
                            }
                        });

                        dialogBuilder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // Отмена
                            }
                        });

                        AlertDialog b = dialogBuilder.create();
                        b.show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Обработать ошибку
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Обработать ошибку
            }
        });
    }


    private void editLastAddedText() {
        if (lastAddedCommentRef == null) {
            return;
        }

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setTitle("Редактировать текст");

        final EditText input = new EditText(getActivity());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        lastAddedCommentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String currentText = snapshot.child("text").getValue(String.class);
                input.setText(currentText);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Обработать ошибку
            }
        });
        dialogBuilder.setView(input);

        dialogBuilder.setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String newText = input.getText().toString();
                if (!TextUtils.isEmpty(newText)) {
                    lastAddedCommentRef.child("text").setValue(newText);
                }
            }
        });

        dialogBuilder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Отмена
            }
        });

        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    private void addTextToSubthemeSecond() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            return;
        }
        String userId = currentUser.getUid();

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isAdmin = snapshot.child("isAdmin").getValue(boolean.class);
                if (!isAdmin) {
                    addTextButton.setVisibility(View.GONE);
                    editTextButton.setVisibility(View.GONE);
                    addTextButtonSecond.setVisibility(View.GONE);
                    return;
                }

                DatabaseReference subthemeRef = FirebaseDatabase.getInstance().getReference("Discussions").child(getMainThemeNameFromSharedPreferences()).child(getSubThemeNameFromSharedPreferences()).child("comments");
                subthemeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                        dialogBuilder.setTitle("Добавить текст");

                        final EditText input = new EditText(getActivity());
                        input.setInputType(InputType.TYPE_CLASS_TEXT);
                        dialogBuilder.setView(input);

                        dialogBuilder.setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String text = input.getText().toString();
                                if (!TextUtils.isEmpty(text)) {
                                    Map<String, Object> comment = new HashMap<>();
                                    comment.put("authorId", userId);
                                    comment.put("text", text);
                                    DatabaseReference newCommentRef = subthemeRef.push();
                                    newCommentRef.setValue(comment);
                                    lastAddedCommentRef = newCommentRef;

                                    // Обновляем второго автора во втором комментарии
                                    if (snapshot.getChildrenCount() >= 2) {
                                        Iterator<DataSnapshot> iterator = snapshot.getChildren().iterator();
                                        DataSnapshot firstCommentSnapshot = iterator.next();
                                        DataSnapshot secondCommentSnapshot = iterator.next();
                                        if (secondCommentSnapshot.child("authorId").getValue(String.class).isEmpty()) {
                                            secondCommentSnapshot.getRef().child("authorId").setValue(userId);
                                            secondCommentSnapshot.getRef().child("text").setValue(text);
                                        }
                                    }
                                }
                            }
                        });

                        dialogBuilder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // Отмена
                            }
                        });

                        AlertDialog b = dialogBuilder.create();
                        b.show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Обработать ошибку
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Обработать ошибку
            }
        });
    }


    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (!isAnimating && e2.getX() - e1.getX() > 100 && !isRightContainerExpanded) {
                expandedText.setVisibility(View.VISIBLE);
                collapsedText.setVisibility(View.GONE);

                if (isLeftContainerExpanded && rightExpandedText.getVisibility() == View.VISIBLE) {
                    rightExpandedText.setVisibility(View.GONE);
                    rightCollapsedText.setVisibility(View.VISIBLE);
                }

                AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
                alphaAnimation.setDuration(500);

                alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        isAnimating = true;
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        isLeftContainerExpanded = true;
                        isRightContainerExpanded = false;
                        isAnimating = false;
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });

                expandedText.startAnimation(alphaAnimation);
            } else if (!isAnimating && e1.getX() - e2.getX() > 100 && !isLeftContainerExpanded) {
                rightExpandedText.setVisibility(View.VISIBLE);
                rightCollapsedText.setVisibility(View.GONE);

                if (isRightContainerExpanded && expandedText.getVisibility() == View.VISIBLE) {
                    expandedText.setVisibility(View.GONE);
                    collapsedText.setVisibility(View.VISIBLE);
                }

                AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
                alphaAnimation.setDuration(500);

                alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        isAnimating = true;
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        isLeftContainerExpanded = false;
                        isRightContainerExpanded = true;
                        isAnimating = false;
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });

                rightExpandedText.startAnimation(alphaAnimation);
            } else if ((e2.getX() - e1.getX() > 100 && isRightContainerExpanded) || (e1.getX() - e2.getX() > 100 && isLeftContainerExpanded)) {
                if (isLeftContainerExpanded) {
                    expandedText.setVisibility(View.GONE);
                    collapsedText.setVisibility(View.VISIBLE);
                } else if (isRightContainerExpanded) {
                    rightExpandedText.setVisibility(View.GONE);
                    rightCollapsedText.setVisibility(View.VISIBLE);
                }

                isLeftContainerExpanded = false;
                isRightContainerExpanded = false;
            }

            return true;
        }
    }
}

