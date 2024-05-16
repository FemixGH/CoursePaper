package com.example.coursepaper;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment {

    private RecyclerView recyclerView;
    private ThemeAdapter themeAdapter;
    private List<Theme> themeList;
    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, container, false);

        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        themeList = new ArrayList<>();
        themeAdapter = new ThemeAdapter((MainActivity) getActivity(), getContext(), themeList);
        recyclerView.setAdapter(themeAdapter);

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
    }

    @Override
    public void onPause() {
        super.onPause();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }
}


