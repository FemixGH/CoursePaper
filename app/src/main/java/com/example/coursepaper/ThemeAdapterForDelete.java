package com.example.coursepaper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ThemeAdapterForDelete extends RecyclerView.Adapter<ThemeAdapterForDelete.ViewHolder> {

    private Context context;
    private List<Theme> themeList;
    private ThemeClickCallback themeClickCallback;

    public ThemeAdapterForDelete(Context context, List<Theme> themeList, ThemeClickCallback themeClickCallback) {
        this.context = context;
        this.themeList = themeList;
        this.themeClickCallback = themeClickCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.theme, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.themeView.setText(themeList.get(position).getTheme());
        holder.itemView.setOnClickListener(v -> themeClickCallback.onThemeClick(themeList.get(position)));

    }

    @Override
    public int getItemCount() {
        return themeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView themeView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            themeView = itemView.findViewById(R.id.theme_name);
        }
    }

    public interface ThemeClickCallback {
        void onThemeClick(Theme theme);
    }
}
