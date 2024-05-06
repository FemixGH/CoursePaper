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

    private DatabaseReference mDataBase;
    private RegistrationFragmentBinding binding;

    private String USER_KEY = "User";


    public RegistrationFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = RegistrationFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        mDataBase = FirebaseDatabase.getInstance().getReference(USER_KEY);

        binding.registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.emailEditText.getText().toString().isEmpty() ||
                        binding.passwordEditText.getText().toString().isEmpty() ||
                        binding.usernameEditText.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "Fields can not be empty", Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(binding.emailEditText.getText().toString(), binding.passwordEditText.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                HashMap<String, String> userInfo = new HashMap<>();
                                userInfo.put("email", binding.emailEditText.getText().toString());
                                userInfo.put("password", binding.passwordEditText.getText().toString());
                                FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(userInfo);

                                MainFragment mainFragment = new MainFragment();
                                getActivity().getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.fragment_container, mainFragment)
                                        .commit();
                            }
                        }
                    });

                }
            }
        });

        return view;
    }

}
