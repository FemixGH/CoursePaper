package com.example.coursepaper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SubThemeAdapter extends RecyclerView.Adapter<ViewHolderSubTheme> {

    public SubThemeAdapter(Context context, List<SubTheme> subThemeList) {
        this.context = context;
        this.subThemeList = subThemeList;
    }

    Context context;
    List<SubTheme> subThemeList;

    @NonNull
    @Override
    public ViewHolderSubTheme onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolderSubTheme(LayoutInflater.from(context).inflate(R.layout.subtheme, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderSubTheme holder, int position) {
        holder.subThemeView.setText(subThemeList.get(position).getSubTheme());
    }

    @Override
    public int getItemCount() {
        return subThemeList.size();
    }
}
