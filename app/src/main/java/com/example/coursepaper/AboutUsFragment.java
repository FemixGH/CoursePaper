package com.example.coursepaper;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class AboutUsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.about_us_fragment, container, false);

        // Find the exit_button using its ID
        Button exitButton = view.findViewById(R.id.exit_button);

        // Set an OnClickListener on the exit_button
        exitButton.setOnClickListener(v -> {
            // Navigate to MainFragment
            MainFragment mainFragment = new MainFragment();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, mainFragment)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    // Method to navigate to MainFragment

}
