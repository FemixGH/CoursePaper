package com.example.coursepaper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SubThemeAdapter extends RecyclerView.Adapter<ViewHolderSubTheme> {

    Context context;
    List<SubTheme> subThemeList;

    public SubThemeAdapter(Context context, List<SubTheme> subThemeList) {
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
        holder.firstAuthorName.setText(subThemeList.get(position).getFirstAuthorName());
        holder.secondAuthorName.setText(subThemeList.get(position).getSecondAuthorName());
        holder.firstAuthorImg.setImageResource(subThemeList.get(position).getFirstAuthorImg());
        holder.secondAuthorImg.setImageResource(subThemeList.get(position).getSecondAuthorImg());
    }

    @Override
    public int getItemCount() {
        return subThemeList.size();
    }
}

