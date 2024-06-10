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
    private TextView secondAuthorName;
    private Button addTextButton;
    private DatabaseReference lastAddedCommentRef;
    Button editTextButton;

    private Button addTextButton2;
    private Button editTextButton2;

    String firstAuthorTextText = "";
    String secondAuthorTextText = "";




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_window_fragment, container, false);

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

        collapsedText = view.findViewById(R.id.collapsed_text);
        expandedText = view.findViewById(R.id.expanded_text);
        textContainer = view.findViewById(R.id.text_container);
        gestureDetector = new GestureDetector(getActivity(), new GestureListener());
        textContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });

        rightCollapsedText = view.findViewById(R.id.right_collapsed_text);
        rightExpandedText = view.findViewById(R.id.right_expanded_text);
        rightTextContainer = view.findViewById(R.id.right_text_container);
        firstAuthorName = view.findViewById(R.id.first_author_name);
        secondAuthorName = view.findViewById(R.id.second_author_name);
        rightTextContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });

        addTextButton = view.findViewById(R.id.add_text_button);
        addTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTextToSubtheme();
            }
        });

        editTextButton = view.findViewById(R.id.edit_text_button);
        editTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editLastAddedText();
            }
        });

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Discussions").child(realTheme).child(subTheme).child("comments");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Получить ссылки на текстовые поля для первого и второго авторов
                TextView firstAuthorText = view.findViewById(R.id.collapsed_text);
                TextView firstAuthorTextExtended = view.findViewById(R.id.expanded_text);
                TextView secondAuthorText = view.findViewById(R.id.right_collapsed_text);
                TextView secondAuthorTextExtended = view.findViewById(R.id.right_expanded_text);

                // Инициализировать текст для первого и второго авторов

                for (DataSnapshot commentSnapshot : snapshot.getChildren()) {
                    String authorId = commentSnapshot.child("authorId").getValue(String.class);
                    String text = commentSnapshot.child("text").getValue(String.class);

                    // Проверить, является ли текущий комментарий текстом первого или второго автора
                    if (commentSnapshot.getKey().equals("firstAuthorText")) {
                        firstAuthorTextText = text;
                    } else if (commentSnapshot.getKey().equals("secondAuthorText")) {
                        secondAuthorTextText = text;
                    }

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(authorId);
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String authorName = snapshot.child("username").getValue(String.class);
                            if (firstAuthorTextText != "") {
                                firstAuthorName.setText(authorName);
                                Log.d("AUTHOR_NAME", authorName);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("Firebase", "Failed to read value.", error.toException());
                        }
                    });
                    Log.d("COMMENTS", text);
                }

                // Установить текст для первого и второго авторов
                firstAuthorText.setText(firstAuthorTextText);
                firstAuthorTextExtended.setText(firstAuthorTextText);
                secondAuthorText.setText(secondAuthorTextText);
                secondAuthorTextExtended.setText(secondAuthorTextText);
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
//                        editTextButton.setVisibility(View.VISIBLE);
                        addTextButton2.setVisibility(View.VISIBLE);
//                        editTextButton2.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("Firebase", "Failed to read value.", error.toException());
                }
            });
        }
        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("Discussions").child("test").child("subtest").child("comments").child("secondAuthorText");
        databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String authorId = snapshot.child("authorId").getValue(String.class);

                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(authorId);
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String authorName = snapshot.child("username").getValue(String.class);
                        if (secondAuthorTextText != ""){
                        secondAuthorName.setText(authorName);
                        Log.d("AUTHOR_NAME", authorName);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Firebase", "Failed to read value.", error.toException());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Failed to read value.", error.toException());
            }
        });

        addTextButton2 = view.findViewById(R.id.add_text_button2);
        editTextButton2 = view.findViewById(R.id.edit_text_button2);
        addTextButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTextToSubtheme2();
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
    private String getSubThemeNameFromSharedPreferences(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("subTheme", "");
    }
    private void addTextToSubtheme() {
        // Получить текущего пользователя
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            // Пользователь не авторизован
            return;
        }
        String userId = currentUser.getUid();

        // Проверить, является ли текущий пользователь администратором
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isAdmin = snapshot.child("isAdmin").getValue(boolean.class);
                if (!isAdmin) {
                    // Текущий пользователь не является администратором
                    return;
                }

                // Проверить, есть ли уже текст в субтеме
                DatabaseReference subthemeRef = FirebaseDatabase.getInstance().getReference("Discussions").child(getMainThemeNameFromSharedPreferences()).child(getSubThemeNameFromSharedPreferences()).child("comments");
                subthemeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // Показать диалоговое окно для ввода текста
                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                        dialogBuilder.setTitle("Добавить текст");

                        final EditText input = new EditText(getActivity());
                        input.setInputType(InputType.TYPE_CLASS_TEXT);
                        dialogBuilder.setView(input);

                        dialogBuilder.setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String text = input.getText().toString();
                                if (!TextUtils.isEmpty(text)) {
                                    // Добавить текст в субтему для первого автора
                                    Map<String, Object> firstAuthorText = new HashMap<>();
                                    firstAuthorText.put("authorId", userId);
                                    firstAuthorText.put("text", text);
                                    subthemeRef.child("firstAuthorText").setValue(firstAuthorText);
                                    lastAddedCommentRef = subthemeRef.child("firstAuthorText"); // Use lastAddedCommentRef here
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
            // Нет последнего добавленного комментария
            return;
        }

        // Показать диалоговое окно для редактирования текста
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
                    // Обновить текст в субтеме для первого автора
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
    private void addTextToSubtheme2() {
        // Get the current user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            // The user is not authenticated
            return;
        }
        String userId = currentUser.getUid();

        // Check if the current user is an admin
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isAdmin = snapshot.child("isAdmin").getValue(boolean.class);
                if (!isAdmin) {
                    // The current user is not an admin
                    return;
                }

                // Check if there is already text in the subtheme
                DatabaseReference subthemeRef = FirebaseDatabase.getInstance().getReference("Discussions").child(getMainThemeNameFromSharedPreferences()).child(getSubThemeNameFromSharedPreferences()).child("comments");
                subthemeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // Show a dialog for entering the text
                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                        dialogBuilder.setTitle("Добавить текст");

                        final EditText input = new EditText(getActivity());
                        input.setInputType(InputType.TYPE_CLASS_TEXT);
                        dialogBuilder.setView(input);

                        dialogBuilder.setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String text = input.getText().toString();
                                if (!TextUtils.isEmpty(text)) {
                                    // Add the text to the subtheme for the second author
                                    Map<String, Object> secondAuthorText = new HashMap<>();
                                    secondAuthorText.put("authorId", userId);
                                    secondAuthorText.put("text", text);
                                    subthemeRef.child("secondAuthorText").setValue(secondAuthorText);
                                    lastAddedCommentRef = subthemeRef.child("secondAuthorText"); // Use lastAddedCommentRef here
                                }
                            }
                        });
                        dialogBuilder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // Cancel
                            }
                        });

                        AlertDialog b = dialogBuilder.create();
                        b.show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle the error
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error
            }
        });
    }



    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            // Check swipe direction, container states, and animation state
            if (!isAnimating && e2.getX() - e1.getX() > 100 && !isRightContainerExpanded) {
                // Swipe right to left on the left container

                // Reset the right container if it's visible
                if (isLeftContainerExpanded && rightExpandedText.getVisibility() == View.VISIBLE) {
                    rightExpandedText.setVisibility(View.GONE);
                    rightCollapsedText.setVisibility(View.VISIBLE);
                }

                // Set visibility of TextViews for the left container
                expandedText.setVisibility(View.VISIBLE);
                collapsedText.setVisibility(View.GONE);

                // Create animation for the left container
                AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
                alphaAnimation.setDuration(500); // You can adjust the duration of the animation as needed

                // Set animation listener for the left container
                alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        // Animation start
                        isAnimating = true;
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        // Animation end
                        isLeftContainerExpanded = true;
                        isRightContainerExpanded = false;
                        isAnimating = false;
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        // Animation repeat
                    }
                });

                // Start animation for the left container
                expandedText.startAnimation(alphaAnimation);

            } else if (!isAnimating && e1.getX() - e2.getX() > 100 && !isLeftContainerExpanded) {
                // Swipe left to right on the right container

                // Reset the left container if it's visible
                if (isRightContainerExpanded && expandedText.getVisibility() == View.VISIBLE) {
                    expandedText.setVisibility(View.GONE);
                    collapsedText.setVisibility(View.VISIBLE);
                }

                // Set visibility of TextViews for the right container
                rightExpandedText.setVisibility(View.VISIBLE);
                rightCollapsedText.setVisibility(View.GONE);

                // Create animation for the right container
                AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
                alphaAnimation.setDuration(500); // You can adjust the duration of the animation as needed

                // Set animation listener for the right container
                alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        // Animation start
                        isAnimating = true;
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        // Animation end
                        isLeftContainerExpanded = false;
                        isRightContainerExpanded = true;
                        isAnimating = false;
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        // Animation repeat
                    }
                });

                // Start animation for the right container
                rightExpandedText.startAnimation(alphaAnimation);

            } else if ((e2.getX() - e1.getX() > 100 && isRightContainerExpanded) || (e1.getX() - e2.getX() > 100 && isLeftContainerExpanded)) {
                // Swipe in the wrong direction or swipe on an already expanded container

                // Set visibility of TextViews for the expanded container
                if (isLeftContainerExpanded) {
                    expandedText.setVisibility(View.GONE);
                    collapsedText.setVisibility(View.VISIBLE);
                } else if (isRightContainerExpanded) {
                    rightExpandedText.setVisibility(View.GONE);
                    rightCollapsedText.setVisibility(View.VISIBLE);
                }

                // Reset flags for tracking container states
                isLeftContainerExpanded = false;
                isRightContainerExpanded = false;

            }

            return true;
        }
    }

}
