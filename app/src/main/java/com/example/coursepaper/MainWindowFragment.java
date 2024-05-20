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


//        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        String subTheme = sharedPreferences.getString("subTheme", "");
        String realTheme = sharedPreferences.getString("mainTheme", "");


        Log.d("DIRECTORY", realTheme + " " + subTheme);
        // Initialize TextViews
        collapsedText = view.findViewById(R.id.collapsed_text);
        expandedText = view.findViewById(R.id.expanded_text);

        // Initialize LinearLayout
        textContainer = view.findViewById(R.id.text_container);

        // Initialize GestureDetector
        gestureDetector = new GestureDetector(getActivity(), new GestureListener());

        // Set OnTouchListener for LinearLayout
        textContainer.setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event);
            return true;
        });
        // Initialize TextViews for the right LinearLayout
        rightCollapsedText = view.findViewById(R.id.right_collapsed_text);
        rightExpandedText = view.findViewById(R.id.right_expanded_text);

        // Initialize LinearLayout for the right container
        rightTextContainer = view.findViewById(R.id.right_text_container);
        firstAuthorName = view.findViewById(R.id.first_author_name);

        // Set OnTouchListener for the right LinearLayout
        rightTextContainer.setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event);
            return true;
        });

        // Get discussion from Firebase
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Discussions").child(realTheme).child(subTheme).child("comments");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // Итерируемся по всем комментариям в snapshot
                for (DataSnapshot commentSnapshot : snapshot.getChildren()) {
                    // Получаем значения полей authorId и text
                    String authorId = commentSnapshot.child("authorId").getValue(String.class);
                    String text = commentSnapshot.child("text").getValue(String.class);
                    collapsedText.setText(text);
                    expandedText.setText(text);
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(authorId);

                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String authorName = snapshot.child("username").getValue(String.class);
                            firstAuthorName.setText(authorName);
                            Log.d("AUTHOR_NAME", authorName);
                            // вы можете использовать authorName здесь
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("Firebase", "Failed to read value.", error.toException());
                        }
                    });
                    // Выводим текст комментария в лог
                    Log.d("COMMENTS", text);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Обрабатываем ошибку
                Log.e("Firebase", "Failed to read value.", error.toException());
            }
        });

        addTextButton = view.findViewById(R.id.add_text_button);
        addTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTextToSubtheme();
            }
        });
        Button editTextButton = view.findViewById(R.id.edit_text_button);
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
                                    // Добавить текст в субтему
                                    Map<String, Object> comment = new HashMap<>();
                                    comment.put("authorId", userId);
                                    comment.put("text", text);
                                    DatabaseReference newCommentRef = subthemeRef.push();
                                    newCommentRef.setValue(comment);
                                    lastAddedCommentRef = newCommentRef;
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
                    // Обновить текст в субтеме
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
