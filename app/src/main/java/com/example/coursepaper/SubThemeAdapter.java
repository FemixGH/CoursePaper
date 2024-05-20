package com.example.coursepaper;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.List;

public class SubThemeAdapter extends RecyclerView.Adapter<ViewHolderSubTheme> {

    private AppCompatActivity context;
    private List<SubTheme> subThemeList;
    private String mainThemeName;
    List<String> authorIds = new ArrayList<>();

    public SubThemeAdapter(AppCompatActivity context, List<SubTheme> subThemeList, String mainThemeName) {
        this.context = context;
        this.subThemeList = subThemeList;
        this.mainThemeName = mainThemeName;
    }

    @NonNull
    @Override
    public ViewHolderSubTheme onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolderSubTheme(LayoutInflater.from(context).inflate(R.layout.subtheme, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderSubTheme holder, int position) {
        holder.subThemeView.setText(subThemeList.get(position).getSubTheme());




        SubTheme selectedSubTheme = subThemeList.get(position);
        Log.d("MAINTHEME",  mainThemeName);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            DatabaseReference commentsRef = databaseReference.child("Discussions")
                    .child(mainThemeName).child(selectedSubTheme.getSubTheme()).child("comments");



            commentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Loop through the comments and get the authorId for each one
                    for (DataSnapshot commentSnapshot : dataSnapshot.getChildren()) {
                        String authorId = commentSnapshot.child("authorId").getValue(String.class);
                        System.out.println("Author ID: " + authorId);
                        authorIds.add(authorId);
                    }

                    // Get the username and avatar URL for each author ID
                    for (String authorId : authorIds) {
                        DatabaseReference userRef = databaseReference.child("users").child(authorId);
                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String username = dataSnapshot.child("username").getValue(String.class);
                                String avatarUrl = dataSnapshot.child("imageUrl").getValue(String.class);

                                System.out.println("Username: " + username);
                                System.out.println("Avatar URL: " + avatarUrl);

                                holder.firstAuthorName.setText(username);
                                Glide.with(context)
                                        .load(avatarUrl)
                                        .into(holder.firstAuthorImg);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                System.out.println("The read failed: " + databaseError.getCode());
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                SubTheme selectedSubTheme = subThemeList.get(position);

                SharedPreferences sharedPreferences = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("subTheme", subThemeList.get(position).getSubTheme());
                editor.apply();


                Log.d("SUBTHEMEADAPTER",mainThemeName + subThemeList.get(position).getSubTheme());

                MainWindowFragment mainWindowFragment = new MainWindowFragment();


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

    public void updateSubThemeList(List<SubTheme> newSubThemeList) {
        subThemeList.clear();
        subThemeList.addAll(newSubThemeList);
        notifyDataSetChanged();
    }

}


