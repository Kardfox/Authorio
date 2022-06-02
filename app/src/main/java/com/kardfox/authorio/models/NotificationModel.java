package com.kardfox.authorio.models;


public class NotificationModel extends Model {
    public int user_id;
    public String title;
    public String text;
    public String object_id;
    public int object_type;
    public String datetime;
    public String author_photo;
    public String author_name;

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
