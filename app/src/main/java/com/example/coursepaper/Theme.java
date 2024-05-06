package com.example.coursepaper;

import java.util.List;

public class Theme {
    String theme;
    List<SubTheme> subThemes;

    public Theme(String theme, List<SubTheme> subThemes) {
        this.theme = theme;
        this.subThemes = subThemes;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public List<SubTheme> getSubThemes() {
        return subThemes;
    }

    public void setSubThemes(List<SubTheme> subThemes) {
        this.subThemes = subThemes;
    }
}

