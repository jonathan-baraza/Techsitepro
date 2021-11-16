package com.example.techsitepro.Models;

public class Notification {
    private String id;
    private String notification;
    private String postid;
    private String publisherid;
    private String ispost;

    public Notification(String id, String notification, String postid, String publisherid, String ispost) {
        this.id = id;
        this.notification = notification;
        this.postid = postid;
        this.publisherid = publisherid;
        this.ispost = ispost;
    }

    public Notification() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getPublisherid() {
        return publisherid;
    }

    public void setPublisherid(String publisherid) {
        this.publisherid = publisherid;
    }

    public String getIspost() {
        return ispost;
    }

    public void setIspost(String ispost) {
        this.ispost = ispost;
    }
}
