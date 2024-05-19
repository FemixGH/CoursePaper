package com.example.coursepaper;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class SubTheme implements Parcelable {

    private List<Comment> comments;
    private String subTheme;

    public SubTheme(String subTheme) {
        this.subTheme = subTheme;
        this.comments = new ArrayList<>();
    }

    public String getSubTheme() {
        return subTheme;
    }

    public void setSubTheme(String subTheme) {
        this.subTheme = subTheme;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(subTheme);
        dest.writeList(comments);
    }

    protected SubTheme(Parcel in) {
        subTheme = in.readString();
        comments = new ArrayList<>();
        in.readList(comments, Comment.class.getClassLoader());
    }

    public static final Parcelable.Creator<SubTheme> CREATOR = new Parcelable.Creator<SubTheme>() {
        @Override
        public SubTheme createFromParcel(Parcel in) {
            return new SubTheme(in);
        }

        @Override
        public SubTheme[] newArray(int size) {
            return new SubTheme[size];
        }
    };
}
