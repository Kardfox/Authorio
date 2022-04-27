package com.kardfox.authorio.main;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kardfox.authorio.MainActivity;
import com.kardfox.authorio.R;
import com.kardfox.authorio.models.UserModel;
import com.kardfox.authorio.server_client.Client;
import com.kardfox.authorio.server_client.Server;

import org.json.JSONObject;

import java.net.URL;


public class LoveAuthorView extends FrameLayout {
    private final View view;

    public LoveAuthorView(Context context) {
        super(context);
        view = inflate(getContext(), R.layout.love_author_view, null);
    }

    public View getView() {
        return view;
    }

    public void setData(String image, String name) {
        ImageView authorPhoto = view.findViewById(R.id.authorPhotoInfo);
        TextView authorName = view.findViewById(R.id.authorName);

        byte[] imageBytes = Base64.decode(image, 0);
        authorPhoto.setImageBitmap(BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length));
        authorName.setText(name);
    }

    public static class LoveAuthor {
        public static UserModel[] loveAuthors;

        public static void updateList(UserModel user) {
            Server.Response response = null;

            try {
                JSONObject json = new JSONObject();
                json.put("user_id", user.id);

                URL url = new URL(String.format(Server.URLs.love_authors, Server.URLs.main, user.token));
                Client.Post post = new Client.Post(url, json);
                post.execute();

                response = post.get();
            } catch (Exception exception) {
                Log.e(MainActivity.LOG_TAG, exception.getMessage());
            }

            if (response != null && response.code == 200) {
                Gson gson = new GsonBuilder().create();

                loveAuthors = gson.fromJson(response.response, UserModel[].class);
            } else {
                loveAuthors = null;
            }
        }
    }
}
