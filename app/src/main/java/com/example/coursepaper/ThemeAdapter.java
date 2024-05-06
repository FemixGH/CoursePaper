package com.example.coursepaper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ThemeAdapter extends RecyclerView.Adapter<ViewHolderTheme> {

    private MainActivity mainActivity;
    private List<Theme> themeList;
    private Context context;

    public ThemeAdapter(MainActivity mainActivity, Context context, List<Theme> themeList) {
        this.mainActivity = mainActivity;
        this.context = context;
        this.themeList = themeList;
    }

    @NonNull
    @Override
    public ViewHolderTheme onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.theme, parent, false);
        return new ViewHolderTheme(mainActivity, view, themeList); // передаем список themeList в конструктор ViewHolderTheme
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderTheme holder, int position) {
        holder.themeView.setText(themeList.get(position).getTheme());
    }

    @Override
    public int getItemCount() {
        return themeList.size();
    }
}


