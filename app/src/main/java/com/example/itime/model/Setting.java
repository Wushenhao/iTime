package com.example.itime.model;

import java.io.Serializable;

public class Setting implements Serializable {
    private String Title,Message;
    private int CoverResourceId;

    public Setting(String title, String message, int coverResourceId) {
        Title = title;
        Message=message;
        CoverResourceId = coverResourceId;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
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
