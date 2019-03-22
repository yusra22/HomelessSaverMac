package com.uyr.yusara.homelesssavermac.Modal;

public class Notification
{

    public String from,type;

    public Notification() { }

    public Notification(String from, String type) {
        this.from = from;
        this.type = type;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
