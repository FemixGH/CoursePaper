package com.example.coursepaper;

import android.os.Parcel;
import android.os.Parcelable;

public class SubTheme implements Parcelable {
    public String getFirstAuthorName() {
        return firstAuthorName;
    }

    public void setFirstAuthorName(String firstAuthorName) {
        this.firstAuthorName = firstAuthorName;
    }

    public String getSecondAuthorName() {
        return secondAuthorName;
    }

    public void setSecondAuthorName(String secondAuthorName) {
        this.secondAuthorName = secondAuthorName;
    }

    public int getFirstAuthorImg() {
        return firstAuthorImg;
    }

    public void setFirstAuthorImg(int firstAuthorImg) {
        this.firstAuthorImg = firstAuthorImg;
    }

    public int getSecondAuthorImg() {
        return secondAuthorImg;
    }

    public void setSecondAuthorImg(int secondAuthorImg) {
        this.secondAuthorImg = secondAuthorImg;
    }

    String subTheme;
    String firstAuthorName;
    String secondAuthorName;
    int firstAuthorImg;
    int secondAuthorImg;

    public SubTheme(String subTheme, String firstAuthorName, String secondAuthorName, int firstAuthorImg, int secondAuthorImg) {
        this.subTheme = subTheme;
        this.firstAuthorName = firstAuthorName;
        this.secondAuthorName = secondAuthorName;
        this.firstAuthorImg = firstAuthorImg;
        this.secondAuthorImg = secondAuthorImg;
    }

    protected SubTheme(Parcel in) {
        subTheme = in.readString();
    }

    public static final Parcelable.Creator<SubTheme> CREATOR = new Creator<SubTheme>() {
        @Override
        public SubTheme createFromParcel(Parcel in) {
            return new SubTheme(in);
        }

        @Override
        public SubTheme[] newArray(int size) {
            return new SubTheme[size];
        }
    };

    public String getSubTheme() {
        return subTheme;
    }

    public void setSubTheme(String subTheme) {
        this.subTheme = subTheme;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(subTheme);
    }
}
