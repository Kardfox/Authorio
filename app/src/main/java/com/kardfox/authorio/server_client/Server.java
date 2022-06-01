package com.kardfox.authorio.server_client;

import androidx.annotation.NonNull;

import org.json.JSONObject;

import java.util.Locale;

public class Server {
    public static class URLs {
        public String main = "http://192.168.56.1:5000";

        //auth
        public String login  = String.format("%s/login", main);
        public String signup = String.format("%s/signup", main);
        public String logout;

        //book
        public String books_get = String.format("%s/books/get", main);

        //notes
        public final String notes_get = String.format("%s/notes/get", main);

        //user
        public String love_authors;
        public String unsubscribe;
        public String subscribe;
        public String get_user = String.format("%s/users/get", main);
        public String get_lovers;
        public String change_user;
        public String change_password;

        //notify
        public String notifications_read;

        public void setUrls(String token) {
            logout = String.format("%s/logout/%s", main, token);
            love_authors = String.format("%s/users/get/love_authors/%s", main, token);
            unsubscribe = String.format("%s/users/delete/love_author/%s", main, token);
            get_lovers = String.format("%s/users/get/lovers/%s", main, token);
            change_user = String.format("%s/user/change/%s", main, token);
            change_password = String.format("%s/user/change_password/%s", main, token);
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
