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

    public enum NotificationTypes {
        NEW_NOTE(1),
        NEW_BOOK(2),
        NEW_CHAPTER(3),
        NEW_LOVER(4),
        NEW_COMMENTARY(5),
        NEW_HATER(6);

        public int intType;
        NotificationTypes(int intType) {
            this.intType = intType;
        }
    }
}
