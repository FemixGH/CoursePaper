package com.example.coursepaper;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.coursepaper.databinding.MainWindowFragmentBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;
import java.util.Objects;

public class MainWindowFragment extends Fragment {

    private MainWindowFragmentBinding binding;
    private String themeName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = MainWindowFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        DrawerLayout drawer = ((MainActivity) getActivity()).getDrawerLayout();

        Bundle bundle = getArguments();
        if (bundle != null) {
            themeName = bundle.getString("themeName");
        }
        Log.d("Main", Objects.requireNonNull(themeName));

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        String mainTheme = sharedPreferences.getString("mainTheme", "");

        Log.d("DIRECTORY", mainTheme);

        // Get discussion from Firebase
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Discussions/" + mainTheme + "/" + themeName);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("TESTING", snapshot.toString());
                Iterator<DataSnapshot> iterator = snapshot.getChildren().iterator();
                if (iterator.hasNext()) {
                    // Assign the first author from the first child node
                    DataSnapshot firstChild = iterator.next();
                    String firstAuthor = (String) firstChild.getValue() + ": ";
                    String firstName = (String) firstChild.getKey();
                    binding.firstAuthorName.setText(firstName);
                    binding.firstAuthorExplanation.setText(firstAuthor);
                    Log.d("FirstAuthor", firstAuthor);
                }
                if (iterator.hasNext()) {
                    // Assign the second author from the next child node
                    DataSnapshot secondChild = iterator.next();
                    String secondAuthor = (String) secondChild.getValue() + ": ";
                    String secondName = (String) secondChild.getKey();
                    binding.secondAuthorName.setText(secondName);
                    binding.secondAuthorExplanation.setText(secondAuthor);
                    Log.d("SecondAuthor", secondAuthor);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors
                Log.e("FirebaseError", "Error fetching data", error.toException());
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
}
