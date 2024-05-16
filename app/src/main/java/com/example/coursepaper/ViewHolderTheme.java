package com.example.coursepaper;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
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

        // Создаем новый Bundle и добавляем themeName и subThemes
        Bundle bundle = new Bundle();
        bundle.putString("themeName", selectedTheme.getTheme()); //добавляю в bundle
        Log.d("ME", selectedTheme.getTheme());
        bundle.putParcelableArrayList("subThemes", (ArrayList<? extends Parcelable>) selectedTheme.getSubThemes());

        // Сохраняем themeName в SharedPreferences
        SharedPreferences sharedPreferences = mainActivity.getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("mainTheme", selectedTheme.getTheme());
        editor.apply();


        // Создаем новый экземпляр SubThemeFragment и добавляем Bundle в качестве аргументов
        SubThemeFragment subThemeFragment = new SubThemeFragment();
        subThemeFragment.setArguments(bundle);


        // Заменяем текущий фрагмент на SubThemeFragment
        mainActivity.replaceFragment(subThemeFragment);
    }

}


