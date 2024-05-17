package com.example.coursepaper;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.coursepaper.databinding.EnterFragmentBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class EnterFragment extends Fragment {

    private EnterFragmentBinding binding;

    public EnterFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = EnterFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.loginButton.setOnClickListener(view1 -> {
            String email = binding.emailEditText.getText().toString();
            String password = binding.passwordEditText.getText().toString();
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(getActivity(), "Fields can not be empty", Toast.LENGTH_SHORT).show();
            } else {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                MainFragment mainFragment = new MainFragment();
                                getActivity().getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.fragment_container, mainFragment)
                                        .commit();
                            } else {
                                Toast.makeText(getActivity(), "Login failed", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        binding.registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegistrationFragment registrationFragment = new RegistrationFragment();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, registrationFragment)
                        .commit();
            }
        });

        return view;
    }
}

