package com.example.techsitepro.Models;

public class Comment {
    private String id;
    private String comment;
    private String publisherid;

    public Comment(String id, String comment, String publisherid) {
        this.id = id;
        this.comment = comment;
        this.publisherid = publisherid;
    }

    public Comment() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPublisherid() {
        return publisherid;
    }

    public void setPublisherid(String publisherid) {
        this.publisherid = publisherid;
    }
}
