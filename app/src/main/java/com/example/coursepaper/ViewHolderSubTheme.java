package com.example.coursepaper;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ViewHolderSubTheme extends RecyclerView.ViewHolder {

    TextView subThemeView;
    public ViewHolderSubTheme(@NonNull View itemView) {
        super(itemView);
        subThemeView = itemView.findViewById(R.id.subtheme_name);

    }
}
