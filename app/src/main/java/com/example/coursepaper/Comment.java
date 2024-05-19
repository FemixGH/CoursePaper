package com.example.coursepaper;

public class Comment {
    public String authorId;

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String text;

    public Comment(String authorId, String text) {
        this.authorId = authorId;
        this.text = text;
    }

}
