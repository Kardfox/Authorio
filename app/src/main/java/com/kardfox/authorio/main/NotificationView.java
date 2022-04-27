package com.kardfox.authorio.main;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kardfox.authorio.MainActivity;
import com.kardfox.authorio.R;
import com.kardfox.authorio.models.NotificationModel;
import com.kardfox.authorio.models.UserModel;
import com.kardfox.authorio.server_client.Client;
import com.kardfox.authorio.server_client.Server;

import org.json.JSONObject;

import java.net.URL;



public class NotificationView extends ConstraintLayout {
    private final View view;

    public NotificationView(Context context) {
        super(context);
        view = inflate(getContext(), R.layout.notification_view, null);
    }

    public void setData(String image, String name, String text, String datetime) {
        ImageView authorPhoto = view.findViewById(R.id.authorPhotoNote);
        TextView authorName = view.findViewById(R.id.authorNameNote);
        TextView textNote = view.findViewById(R.id.noteText);
        TextView dateNote = view.findViewById(R.id.noteDate);

        byte[] imageBytes = Base64.decode(image, 0);

        authorPhoto.setImageBitmap(BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length));
        authorName.setText(name);
        textNote.setText(text);
        dateNote.setText(datetime);
    }

    public View getView() { return view; }

    public static class Notification {
        public static NotificationModel[] notifications;

        public static void updateList(UserModel user) {
            Server.Response response = null;

            try {
                JSONObject json = new JSONObject();

                URL url = new URL(String.format(Server.URLs.notifications_read, Server.URLs.main, user.token));
                Client.Post post = new Client.Post(url, json);
                post.execute();

                response = post.get();
            } catch (Exception exception) {
                Log.e(MainActivity.LOG_TAG, exception.getMessage());
            }

            if (response != null && response.code == 200) {
                Gson gson = new GsonBuilder().create();
                notifications = gson.fromJson(response.response, NotificationModel[].class);
            } else {
                notifications = null;
            }
        }
    }

    public static class NotificationViewNull extends FrameLayout {
        private final View view;

        public NotificationViewNull(Context context, String text) {
            super(context);
            view = inflate(getContext(), R.layout.notification_view_null, null);
            if (!text.isEmpty()) {
                ((TextView)view.findViewById(R.id.notificationNullText)).setText(text);
            }
        }

        public View getView() { return view; }
    }
}
