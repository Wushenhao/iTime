package com.example.itime.model;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

public class Date implements Serializable {
    private String Name,Detail,Message;
    private byte[] Cover;

    public Date(String name,String detail, String message, byte[] cover) {
        Name = name;
        Detail = detail;
        Message=message;
        Cover = cover;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDetail() {
        return Detail;
    }

    public void setDetail(String detail) {
        Detail = detail;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public byte[] getCover() {
        return Cover;
    }

    public void setCover(byte[] cover) {
        Cover = cover;
    }
}
