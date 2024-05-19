package com.example.coursepaper;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class SubThemeAdapter extends RecyclerView.Adapter<ViewHolderSubTheme> {

    private AppCompatActivity context;
    private List<SubTheme> subThemeList;

    public SubThemeAdapter(AppCompatActivity context, List<SubTheme> subThemeList) {
        this.context = context;
        this.subThemeList = subThemeList;
    }

    @NonNull
    @Override
    public ViewHolderSubTheme onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolderSubTheme(LayoutInflater.from(context).inflate(R.layout.subtheme, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderSubTheme holder, int position) {
        holder.subThemeView.setText(subThemeList.get(position).getSubTheme());

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users/" + user.getUid());
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String username = snapshot.child("username").getValue(String.class);
                    String imageUrl = snapshot.child("imageUrl").getValue(String.class);

                    holder.firstAuthorName.setText(username);
                    Glide.with(context)
                            .load(imageUrl)
                            .into(holder.firstAuthorImg);


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // обработка ошибки
                }
            });
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                SubTheme selectedSubTheme = subThemeList.get(position);



                Bundle bundle = new Bundle();
                bundle.putString("themeName", selectedSubTheme.getSubTheme());
                bundle.putString("subTheme", selectedSubTheme.getSubTheme());

                MainWindowFragment mainWindowFragment = new MainWindowFragment();
                mainWindowFragment.setArguments(bundle);

                context.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, mainWindowFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return subThemeList.size();
    }
}


