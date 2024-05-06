package com.example.coursepaper;

import android.os.Parcel;
import android.os.Parcelable;

public class SubTheme implements Parcelable {
    String subTheme;

    public SubTheme(String subTheme) {
        this.subTheme = subTheme;
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
