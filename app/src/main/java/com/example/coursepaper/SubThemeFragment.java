    package com.example.coursepaper;

    import android.content.Context;
    import android.content.SharedPreferences;
    import android.os.Bundle;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.EditText;
    import android.widget.ImageButton;
    import android.widget.Toast;

    import androidx.annotation.NonNull;
    import androidx.appcompat.app.AlertDialog;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.fragment.app.Fragment;
    import androidx.recyclerview.widget.LinearLayoutManager;
    import androidx.recyclerview.widget.RecyclerView;

    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.auth.FirebaseUser;
    import com.google.firebase.database.DataSnapshot;
    import com.google.firebase.database.DatabaseError;
    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.database.FirebaseDatabase;
    import com.google.firebase.database.ValueEventListener;

    import java.util.ArrayList;
    import java.util.List;

    public class SubThemeFragment extends Fragment {

        private RecyclerView recyclerView;
        private ImageButton addSubThemeButton;
        private List<SubTheme> subThemes;
        private DatabaseReference databaseReference;
        private FirebaseAuth firebaseAuth;
        private boolean isCurrentUserAdmin = false;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_sub_theme, container, false);

            recyclerView = view.findViewById(R.id.recyclerview);
            addSubThemeButton = view.findViewById(R.id.add_sub_theme_button);

            firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser currentFirebaseUser = firebaseAuth.getCurrentUser();
            if (currentFirebaseUser != null) {
                String userId = currentFirebaseUser.getUid();
                DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("users/" + userId);
                userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        if (user != null && user.isAdmin) {
                            isCurrentUserAdmin = true;
                            addSubThemeButton.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Failed to fetch user data from Firebase", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            Bundle arguments = getArguments();
            if (arguments != null) {
                subThemes = arguments.getParcelableArrayList("subThemes");
            } else {
                subThemes = new ArrayList<>();
            }

            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(new SubThemeAdapter((AppCompatActivity) getActivity(), subThemes));

            addSubThemeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAddSubThemeDialog();
                }
            });

            return view;
        }

        @Override
        public void onResume() {
            super.onResume();
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        @Override
        public void onPause() {
            super.onPause();
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        private void showAddSubThemeDialog() {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
            LayoutInflater inflater = getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.add_sub_theme_dialog, null);
            dialogBuilder.setView(dialogView);

            final EditText subThemeNameEditText = dialogView.findViewById(R.id.sub_theme_name_edit_text);
            dialogBuilder.setTitle("Add New Sub-Theme");
            dialogBuilder.setPositiveButton("Add", (dialog, whichButton) -> {
                String subThemeName = subThemeNameEditText.getText().toString().trim();
                if (!subThemeName.isEmpty()) {
                    addSubThemeToFirebase(subThemeName);
                } else {
                    Toast.makeText(getContext(), "Sub-theme name cannot be empty", Toast.LENGTH_SHORT).show();
                }
            });
            dialogBuilder.setNegativeButton("Cancel", (dialog, whichButton) -> dialog.dismiss());

            AlertDialog b = dialogBuilder.create();
            b.show();
        }

        private void addSubThemeToFirebase(String subThemeName) {
            FirebaseUser currentFirebaseUser = firebaseAuth.getCurrentUser();
            if (currentFirebaseUser != null) {
                String currentUserId = currentFirebaseUser.getUid();

                // Create a new SubTheme object
                SubTheme newSubTheme = new SubTheme(subThemeName);

                // Add a comment with authorId and text
                Comment newComment = new Comment(currentUserId, "This is a new sub-theme");
                newSubTheme.getComments().add(newComment);

                // Add subTheme field to the newSubTheme object
                newSubTheme.setSubTheme(subThemeName);

                String mainThemeName = getMainThemeNameFromSharedPreferences();
                databaseReference = FirebaseDatabase.getInstance().getReference("Discussions/" + mainThemeName + "/" + subThemeName);
                databaseReference.setValue(newSubTheme);
                Toast.makeText(getContext(), "Sub-theme added successfully", Toast.LENGTH_SHORT).show();
            }
        }




        private String getMainThemeNameFromSharedPreferences() {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
            return sharedPreferences.getString("mainTheme", "");
        }
    }


