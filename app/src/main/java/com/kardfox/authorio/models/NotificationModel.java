package com.kardfox.authorio.models;

public class NotificationModel {
    public int user_id;
    public String title;
    public String text;
    public String object_id;
    public int object_type;
    public String datetime;
    public String author_photo;
    public String author_name;


    public NotificationModel(int user_id, String author_photo, String author_name, String text, String object_id, int object_type, String datetime) {
        this.user_id = user_id;
        this.author_photo = author_photo;
        this.author_name = author_name;
        this.title = title;
        this.text = text;
        this.object_id = object_id;
        this.object_type = object_type;
        this.datetime = datetime;
    }
}
