package com.example.itime.model;

import java.io.Serializable;

public class Date implements Serializable {
    private String Name,Detail,Message;
    private int CoverResourceId;

    public Date(String name,String detail, String message, int coverResourceId) {
        Name = name;
        Detail = detail;
        Message=message;
        CoverResourceId = coverResourceId;
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

    public int getCoverResourceId() {
        return CoverResourceId;
    }

    public void setCoverResourceId(int coverResourceId) {
        CoverResourceId = coverResourceId;
    }
}
