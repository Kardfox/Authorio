package com.kardfox.authorio.server_client;

import androidx.annotation.NonNull;

import java.util.Locale;

public class Server {
    public static class URLs {
        public static final String main               = "http://192.168.56.1:5000/";

        //auth
        public static final String login              = String.format("%s/login", main);
        public static final String signup             = String.format("%s/signup", main);
        public static final String logout             = String.format("%s/logout", main);

        //user
        public static final String love_authors       = "%s/users/get/love_authors/%s";
        public static final String get_user           = String.format("%s/users/get", main);

        //notify
        public static final String notifications_read = "%s/notifications/read/%s";
        public static final String notifications_long = String.format("%s/notifications/long/get", main);
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
