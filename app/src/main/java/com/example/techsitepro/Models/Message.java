package com.example.techsitepro.Models;

public class Message {
    private String id;
    private String senderid;
    private String recipientid;
    private String message;
    private String time;
    private String isphoto;
    private String delivered;


    public Message(String id, String senderid, String recipientid, String message, String time, String isphoto, String delivered) {
        this.id = id;
        this.senderid = senderid;
        this.recipientid = recipientid;
        this.message = message;
        this.time = time;
        this.isphoto = isphoto;
        this.delivered = delivered;
    }

    public Message() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSenderid() {
        return senderid;
    }

    public void setSenderid(String senderid) {
        this.senderid = senderid;
    }

    public String getRecipientid() {
        return recipientid;
    }

    public void setRecipientid(String recipientid) {
        this.recipientid = recipientid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getIsphoto() {
        return isphoto;
    }

    public void setIsphoto(String isphoto) {
        this.isphoto = isphoto;
    }

    public String getDelivered() {
        return delivered;
    }

    public void setDelivered(String delivered) {
        this.delivered = delivered;
    }
}