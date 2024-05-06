package com.example.coursepaper;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment {

    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, container, false);

        List<Theme> items = new ArrayList<>();
        List<SubTheme> subThemes1 = new ArrayList<>();
        subThemes1.add(new SubTheme("города", "Author1", "Author2", R.drawable.avatar1, R.drawable.avatar2));
        subThemes1.add(new SubTheme("парки", "Author3", "Author4", R.drawable.avatar1, R.drawable.avatar2));
        subThemes1.add(new SubTheme("дороги", "Author5", "Author6", R.drawable.avatar1, R.drawable.avatar2));
        items.add(new Theme("Инфраструктура", subThemes1));

        List<SubTheme> subThemes2 = new ArrayList<>();
        subThemes2.add(new SubTheme("экзистенциализм", "Author7", "Author8", R.drawable.avatar1, R.drawable.avatar2));
        subThemes2.add(new SubTheme("реляцивизм", "Author9", "Author10", R.drawable.avatar1, R.drawable.avatar2));
        subThemes2.add(new SubTheme("космоцентризм", "Author11", "Author12", R.drawable.avatar1, R.drawable.avatar2));
        items.add(new Theme("Философия", subThemes2));

        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        ThemeAdapter adapter = new ThemeAdapter((MainActivity) getActivity(), getActivity().getApplicationContext(), items);
        recyclerView.setAdapter(adapter);

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
}


