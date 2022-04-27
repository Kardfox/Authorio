package com.kardfox.authorio.server_client;

import android.os.AsyncTask;
import android.util.Log;

import com.kardfox.authorio.MainActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import java.nio.charset.StandardCharsets;


public class Client {
    public static class Post extends AsyncTask<Void, Void, Server.Response> {
        private final URL url;
        private final JSONObject json;

        public Post(URL url, JSONObject json) {
            super();
            this.url = url;
            this.json = json;
        }

        @Override
        protected Server.Response doInBackground(Void... none) {
            StringBuilder response = new StringBuilder();
            int responseCode = 500;

            try {
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json; utf-8");
                connection.setRequestProperty("Accept", "application/json");
                connection.setDoOutput(true);

                connection.setConnectTimeout(5000);

                OutputStream oServerStream = connection.getOutputStream();

                byte[] data = json.toString().getBytes(StandardCharsets.UTF_8);
                oServerStream.write(data, 0, data.length);
                oServerStream.flush();
                oServerStream.close();

                responseCode = connection.getResponseCode();

                BufferedReader iServerStream;
                if (responseCode == 200) {
                    iServerStream = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                } else {
                    iServerStream = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                }

                String inputLine;
                while ((inputLine = iServerStream.readLine()) != null) {
                    response.append(inputLine);
                }

                iServerStream.close();
                connection.disconnect();

            } catch (IOException exception) {
                response.append(exception.getMessage());
            }

            return new Server.Response(responseCode, response.toString());
        }
    }

}



