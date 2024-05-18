package com.example.coursepaper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.coursepaper.databinding.SettingsFragmentBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class SettingsFragment extends Fragment {

    private SettingsFragmentBinding binding;
    private final int PICK_IMAGE_REQUEST = 1;
    private Uri filePath;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    private ActivityResultLauncher<Intent> imageLauncher;
    private AlertDialog progressDialog;
    private boolean isImageSelected = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = SettingsFragmentBinding.inflate(inflater, container, false);

        // Initialize Firebase Storage and Firebase Auth
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        // Set the current username
        if (firebaseUser != null) {
            String uid = firebaseUser.getUid();
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReference("users").child(uid);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String currentUsername = "Текущее имя пользователя: " + snapshot.child("username").getValue(String.class);
                        binding.currentUsername.setText(currentUsername);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle error
                }
            });
        }

        // Initialize the image picker launcher
        imageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            filePath = data.getData();
                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                                binding.profileImageView.setImageBitmap(bitmap);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

        // Set the save changes button click listener
        binding.saveChangesButton.setOnClickListener(view -> {
            String newUsername = binding.newUsernameEditText.getText().toString().trim();
            String currentUsername = binding.currentUsername.getText().toString().split(":")[1].trim();
            boolean isUsernameChanged = !newUsername.equals(currentUsername);

            if (!newUsername.isEmpty()) {
                if (firebaseUser != null) {
                    String uid = firebaseUser.getUid();
                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                    DatabaseReference databaseReference = firebaseDatabase.getReference("users").child(uid);
                    databaseReference.child("username").setValue(newUsername);
                }
            }

            if (filePath != null) {
                uploadImage();
            }

            if (isUsernameChanged && filePath != null) {
                Toast.makeText(getActivity(), "Username and Profile picture updated", Toast.LENGTH_SHORT).show();
            } else if (isUsernameChanged) {
                Toast.makeText(getActivity(), "Username updated", Toast.LENGTH_SHORT).show();
                binding.currentUsername.setText("Текущее имя пользователя: " +newUsername);
            } else if (filePath != null) {
                Toast.makeText(getActivity(), "Profile picture updated", Toast.LENGTH_SHORT).show();
            }
        });

        binding.chooseImageButton.setOnClickListener(view -> selectImage());

        binding.home.setOnClickListener(view -> {
            MainFragment mainFragment = new MainFragment();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, mainFragment)
                    .addToBackStack(null)
                    .commit();
        });



        return binding.getRoot();
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        imageLauncher.launch(Intent.createChooser(intent, "Select your image"));
    }

    private void uploadImage() {
        if (filePath != null && firebaseUser != null) {
            // Create a progress dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Uploading image");
            builder.setCancelable(false);

            // Set up the layout for the progress bar
            LinearLayout layout = new LinearLayout(getActivity());
            layout.setOrientation(LinearLayout.VERTICAL);
            ProgressBar progressBar = new ProgressBar(getActivity());
            layout.addView(progressBar);
            builder.setView(layout);

            // Create and show the dialog
            progressDialog = builder.create();
            progressDialog.show();

            String imageName = firebaseUser.getUid();

            storageReference.child("image").child(imageName).putFile(filePath).addOnSuccessListener(taskSnapshot -> {
                // Dismiss the dialog
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                Toast.makeText(getActivity(), "image uploaded", Toast.LENGTH_SHORT).show();
                //update image url in the database
                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                    DatabaseReference databaseReference = firebaseDatabase.getReference("users").child(firebaseUser.getUid());
                    databaseReference.child("imageUrl").setValue(imageUrl);
                });
            });

        }

    }

    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

}



