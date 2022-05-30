package com.kardfox.authorio.views;

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
    public static MainActivity activity;

    public NotificationView(Context context) {
        super(context);
        view = inflate(context, R.layout.notification_view, null);
        activity = (MainActivity) context;
    }

    public void setData(NotificationModel notificationModel, OnClickListener listener) {
        ImageView authorPhoto = view.findViewById(R.id.authorPhotoNote);
        TextView authorName = view.findViewById(R.id.authorNameNote);
        TextView textNote = view.findViewById(R.id.noteText);
        TextView dateNote = view.findViewById(R.id.noteDate);

        byte[] imageBytes = Base64.decode(notificationModel.author_photo, 0);

        authorPhoto.setImageBitmap(BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length));
        authorName.setText(notificationModel.author_name);
        textNote.setText(notificationModel.text);
        dateNote.setText(notificationModel.datetime);

        this.getView().setOnClickListener(listener);
    }

    public View getView() { return view; }

    public static class Notification {
        public static NotificationModel[] notifications = null;

        public static void updateList(UserModel user, MainActivity activity) {
            try {
                Server.Response response = activity.request(new JSONObject(), NotificationView.activity.URLs.notifications_read);
                if (response.code != 200) return;
                notifications = activity.gson.fromJson(response.response, NotificationModel[].class);
            } catch (Exception exception) {
                Log.e(MainActivity.LOG_TAG, exception.getLocalizedMessage());
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
