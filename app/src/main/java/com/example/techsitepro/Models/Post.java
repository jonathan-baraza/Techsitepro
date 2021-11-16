package com.example.techsitepro.Models;

public class Post {
    private String id;
    private String imageurl;
    private String description;
    private String publisherid;

    public Post(String id, String imageurl, String description, String publisherid) {
        this.id = id;
        this.imageurl = imageurl;
        this.description = description;
        this.publisherid = publisherid;
    }

    public Post() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublisherid() {
        return publisherid;
    }

    public void setPublisherid(String publisherid) {
        this.publisherid = publisherid;
    }
}
