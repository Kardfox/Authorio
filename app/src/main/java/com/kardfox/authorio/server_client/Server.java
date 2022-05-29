package com.kardfox.authorio.server_client;

import androidx.annotation.NonNull;

import org.json.JSONObject;

import java.util.Locale;

public class Server {
    public static class URLs {
        public final String main               = "http://192.168.1.112:5000";

        //auth
        public final String login              = String.format("%s/login", main);
        public final String signup             = String.format("%s/signup", main);
        public final String logout             = String.format("%s/logout", main);

        //book
        public final String books_get          = String.format("%s/books/get", main);

        //notes
        public final String notes_get          = String.format("%s/notes/get", main);

        //user
        public final String love_authors;
        public final String unsubscribe;
        public final String subscribe;
        public final String get_user = String.format("%s/users/get", main);
        public final String get_lovers;

        //notify
        public final String notifications_read;
        public final String notifications_long = String.format("%s/notifications/long/get", main);

        public URLs(String token) {
            love_authors = String.format("%s/users/get/love_authors/%s", main, token);
            unsubscribe = String.format("%s/users/delete/love_author/%s", main, token);
            get_lovers = String.format("%s/users/get/lovers/%s", main, token);
            subscribe = String.format("%s/users/add/love_author/%s", main, token);
            notifications_read = String.format("%s/notifications/read/%s", main, token);
        }
    }

    public static class Response {
        public int code;
        public String response;

        public Response(int code, String response) {
            this.code = code;
            this.response = response;
        }

        @NonNull
        @Override
        public String toString() {
            return String.format(Locale.getDefault(), "Request(code: %d, response: %s)", code, response);
        }
    }
}
