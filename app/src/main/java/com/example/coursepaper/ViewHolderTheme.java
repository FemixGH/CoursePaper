package com.example.coursepaper;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ViewHolderTheme extends RecyclerView.ViewHolder implements View.OnClickListener {

    TextView themeView;
    MainActivity mainActivity;
    private List<Theme> list;

    public ViewHolderTheme(MainActivity mainActivity, View itemView, List<Theme> list) { // добавляем список list в конструктор
        super(itemView);
        this.mainActivity = mainActivity;
        themeView = itemView.findViewById(R.id.theme_name);
        itemView.setOnClickListener(this);
        this.list = list; // инициализируем список list
    }

    @Override
    public void onClick(View v) {
        int position = getAdapterPosition();
        Theme selectedTheme = list.get(position);
        SubThemeFragment subThemeFragment = new SubThemeFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("subThemes", (ArrayList<? extends Parcelable>) selectedTheme.getSubThemes());
        subThemeFragment.setArguments(bundle);
        mainActivity.replaceFragment(subThemeFragment);
    }
}


