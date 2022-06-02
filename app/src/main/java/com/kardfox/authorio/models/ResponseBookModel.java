package com.kardfox.authorio.models;

public class ResponseBookModel extends Model {
    public static class BookModel extends Model {
        public String id;
        public String title;
        public String user_id;
        public String username;
        public String tags;
        public double raiting;
        public String upload_date;
        public String description;
    }

    public BookModel book;
    public ChapterModel[] chapters;
    public boolean is_author;
}
