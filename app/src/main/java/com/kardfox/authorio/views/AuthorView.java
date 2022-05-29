package com.kardfox.authorio.views;

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


public class AuthorView extends FrameLayout {
    private final View view;

    public AuthorView(Context context) {
        super(context);
        view = inflate(context, R.layout.author_view, null);
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

    public static class Author {
        public static UserModel[] authors = null;

        public static void updateList(UserModel user, MainActivity activity) {
            try {
                JSONObject json = new JSONObject();
                json.put("user_id", user.id);

                Server.Response response = activity.request(json, activity.URLs.love_authors);
                if (response == null) return;
                authors = activity.gson.fromJson(response.response, UserModel[].class);
            } catch (Exception exception) {
                Log.e(MainActivity.LOG_TAG, exception.getLocalizedMessage());
            }
        }
    }
}
