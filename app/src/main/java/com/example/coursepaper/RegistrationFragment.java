package com.example.coursepaper;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.coursepaper.databinding.RegistrationFragmentBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegistrationFragment extends Fragment {

    private RegistrationFragmentBinding binding;

    public RegistrationFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = RegistrationFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = binding.emailEditText.getText().toString();
                String password = binding.passwordEditText.getText().toString();
                String username = binding.usernameEditText.getText().toString();
                if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
                    Toast.makeText(getActivity(), "Fields can not be empty", Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    String userId = task.getResult().getUser().getUid();
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users/" + userId);
                                    databaseReference.setValue(new User(username, email));

                                    MainFragment mainFragment = new MainFragment();
                                    getActivity().getSupportFragmentManager()
                                            .beginTransaction()
                                            .replace(R.id.fragment_container, mainFragment)
                                            .commit();
                                } else {
                                    Toast.makeText(getActivity(), "Registration failed", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        return view;
    }

    public static class User {
        public String username;
        public String email;

        public User() {
        }

        public User(String username, String email) {
            this.username = username;
            this.email = email;
        }
    }
}

