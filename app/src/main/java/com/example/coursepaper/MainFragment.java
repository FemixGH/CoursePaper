package com.example.coursepaper;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coursepaper.databinding.MainFragmentBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment {

    private MainFragmentBinding binding;
    private ThemeAdapter themeAdapter;
    private List<Theme> themeList;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    private boolean isCurrentUserAdmin = false;
    private List<AlertDialog> openDialogs = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = MainFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();


        binding.recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));

        themeList = new ArrayList<>();
        themeAdapter = new ThemeAdapter((MainActivity) getActivity(), getContext(), themeList);
        binding.recyclerview.setAdapter(themeAdapter);

        ImageButton addThemeButton = binding.addThemeButton;
        binding.addThemeButton.setOnClickListener(v -> showAddThemeDialog());

        ImageButton deleteThemeButton = binding.deleteThemeButton;
        binding.deleteThemeButton.setOnClickListener(v -> showDeleteThemeDialog());

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
                        addThemeButton.setVisibility(View.VISIBLE);
                        addThemeButton.setEnabled(true);
                        deleteThemeButton.setVisibility(View.VISIBLE);
                        deleteThemeButton.setEnabled(true);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Failed to fetch user data from Firebase", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            addThemeButton.setVisibility(View.GONE);
            addThemeButton.setEnabled(false);
            deleteThemeButton.setVisibility(View.GONE);
            deleteThemeButton.setEnabled(false);
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("Discussions");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                themeList.clear();
                for (DataSnapshot themeSnapshot : snapshot.getChildren()) {
                    String themeName = themeSnapshot.getKey();
                    List<SubTheme> subThemeList = new ArrayList<>();
                    for (DataSnapshot subThemeSnapshot : themeSnapshot.getChildren()) {
                        String subThemeName = subThemeSnapshot.getKey();

                        Iterable<DataSnapshot> childrenSnapshots = subThemeSnapshot.getChildren();
                        String firstAuthorName = childrenSnapshots.iterator().next().getKey();
                        String secondAuthorName = childrenSnapshots.iterator().next().getKey();

                        int firstAuthorImg = R.drawable.avatar1;
                        int secondAuthorImg = R.drawable.avatar2;
                        subThemeList.add(new SubTheme(subThemeName, firstAuthorName, secondAuthorName, firstAuthorImg, secondAuthorImg));
                    }
                    themeList.add(new Theme(themeName, subThemeList));
                }
                themeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load data from Firebase", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        themeAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }




    private void addThemeToFirebase(final String themeName) {
        databaseReference.child(themeName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    snapshot.getRef().setValue("");
                    Toast.makeText(getContext(), "Theme added successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Theme already exists", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to add theme", Toast.LENGTH_SHORT).show();
            }
        });
    }






    private void showDeleteThemeDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.delete_theme_dialog, null);
        dialogBuilder.setView(dialogView);

        RecyclerView recyclerView = dialogView.findViewById(R.id.recyclerview_delete);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new ThemeAdapterForDelete(getContext(), themeList, theme -> {
            AlertDialog.Builder confirmDialogBuilder = new AlertDialog.Builder(getContext());
            confirmDialogBuilder.setTitle("Delete Theme");
            confirmDialogBuilder.setMessage("Are you sure you want to delete this theme?");
            confirmDialogBuilder.setPositiveButton("Yes", (dialog, which) -> {
                AlertDialog deleteThemeDialog = (AlertDialog) dialog;
                deleteThemeFromFirebase(theme, deleteThemeDialog);
            });
            confirmDialogBuilder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
            AlertDialog confirmDialog = confirmDialogBuilder.create();
            registerDialog(confirmDialog);
            confirmDialog.show();
        }));

        dialogBuilder.setTitle("Delete Theme");
        dialogBuilder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog deleteThemeDialog = dialogBuilder.create();
        registerDialog(deleteThemeDialog);
        deleteThemeDialog.show();
    }






    private void deleteThemeFromFirebase(Theme theme, AlertDialog deleteThemeDialog) {
        databaseReference.child(theme.getTheme()).removeValue().addOnSuccessListener(aVoid -> {
            Toast.makeText(getContext(), "Theme deleted successfully", Toast.LENGTH_SHORT).show();
            int index = themeList.indexOf(theme);
            if (index != -1) {
                themeList.remove(index);
                themeAdapter.notifyItemRemoved(index);
            }
            dismissAllDialogs();  // Dismiss all dialogs after theme deletion
        }).addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to delete theme", Toast.LENGTH_SHORT).show());
    }

    private void showAddThemeDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.add_theme_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText themeNameEditText = dialogView.findViewById(R.id.theme_name_edit_text);
        dialogBuilder.setTitle("Add New Theme");
        dialogBuilder.setPositiveButton("Add", (dialog, whichButton) -> {
            String themeName = themeNameEditText.getText().toString().trim();
            if (!themeName.isEmpty()) {
                addThemeToFirebase(themeName);
            } else {
                Toast.makeText(getContext(), "Theme name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
        dialogBuilder.setNegativeButton("Cancel", (dialog, whichButton) -> dialog.dismiss());

        AlertDialog b = dialogBuilder.create();
        registerDialog(b);
        b.show();
    }




    private void registerDialog(AlertDialog dialog) {
        openDialogs.add(dialog);
        dialog.setOnDismissListener(d -> openDialogs.remove(dialog));
    }

    private void dismissAllDialogs() {
        for (AlertDialog dialog : new ArrayList<>(openDialogs)) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }





}


