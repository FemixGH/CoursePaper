package com.example.coursepaper;

import java.util.List;

public class ThemeWithSubthemes {
    private String theme;
    private List<SubTheme> subThemes;

    public ThemeWithSubthemes(String theme, List<SubTheme> subThemes) {
        this.theme = theme;
        this.subThemes = subThemes;
    }

    public String getTheme() {
        return theme;
    }

    public List<SubTheme> getSubThemes() {
        return subThemes;
    }
}
