package com.example.coursepaper;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ViewHolderSubTheme extends RecyclerView.ViewHolder {

    TextView subThemeView, firstAuthorName, secondAuthorName;
    ImageView firstAuthorImg, secondAuthorImg;


    public ViewHolderSubTheme(@NonNull View itemView) {
        super(itemView);
        subThemeView = itemView.findViewById(R.id.subtheme_name);
        firstAuthorName = itemView.findViewById(R.id.first_author_text);
        secondAuthorName = itemView.findViewById(R.id.second_author_text);
        firstAuthorImg = itemView.findViewById(R.id.first_author_img);
        secondAuthorImg = itemView.findViewById(R.id.second_author_img);



    }
}
