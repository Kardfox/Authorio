package com.kardfox.authorio.search;

import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kardfox.authorio.MainActivity;
import com.kardfox.authorio.R;
import com.kardfox.authorio.main.NotificationView;
import com.kardfox.authorio.models.NotificationModel;
import com.kardfox.authorio.models.UserModel;
import com.kardfox.authorio.server_client.Client;
import com.kardfox.authorio.server_client.Server;

import org.json.JSONObject;

import java.net.URL;

public class InfoUserFragment extends Fragment {
    private String authorId;

    private UserModel author;
    private NotificationModel[] notifications;

    private boolean subscribe;

    public InfoUserFragment(String authorId) {
        this.authorId = authorId;
    }
    InfoUserFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info_user, container, false);

        ImageView authorPhoto = view.findViewById(R.id.authorPhotoInfo);
        TextView authorName = view.findViewById(R.id.authorNameInfo);
        TextView authorDescription = view.findViewById(R.id.authorDescription);

        LinearLayout notificationContainer = view.findViewById(R.id.notificationsContainerInfo);

        loadInfo();
        if (author != null) {
            byte[] imageBytes = Base64.decode(author.photo, 0);
            authorPhoto.setImageBitmap(BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length));

            authorName.setText(String.format("%s %s", author.name, author.surname));
            authorDescription.setText(author.description);
        } else {
            Toast.makeText(getContext(), "Server error", Toast.LENGTH_LONG).show();
        }

        loadHistory();
        if (notifications != null) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            for (NotificationModel notification : notifications) {
                switch (notification.object_type) {
                    case 5:
                        notification.text = String.format("%s\n%s", notification.title, notification.text); break;
                    default:
                        notification.text = notification.title; break;
                }
                NotificationView notificationView = new NotificationView(getContext());
                notificationView.setData(notification, (view1) -> {});

                View noteV = notificationView.getView();
                noteV.setPadding(0, 20, 0, 0);

                notificationContainer.addView(noteV, layoutParams);
            }
            String textForNullView = notifications.length > 0? "" : getString(R.string.nullPlaceholder);
            NotificationView.NotificationViewNull viewNull = new NotificationView.NotificationViewNull(getContext(), textForNullView);
            notificationContainer.addView(viewNull.getView());
        }

        return view;
    }

    public void loadInfo() {
        Server.Response response = null;

        try {
            JSONObject json = new JSONObject();
            json.put("id", authorId);

            URL url = new URL(Server.URLs.get_user);
            Client.Post post = new Client.Post(url, json);
            post.execute();

            response = post.get();
        } catch (Exception exception) {
            Log.e(MainActivity.LOG_TAG, exception.getMessage());
        }

        if (response != null && response.code == 200) {
            Gson gson = new GsonBuilder().create();
            author = gson.fromJson(response.response, UserModel[].class)[0];
        }
    }


    public void loadHistory() {
        Server.Response response = null;

        try {
            JSONObject json = new JSONObject();
            json.put("author_id", authorId);

            URL url = new URL(Server.URLs.notifications_long);
            Client.Post post = new Client.Post(url, json);
            post.execute();

            response = post.get();
        } catch (Exception exception) {
            Log.e(MainActivity.LOG_TAG, exception.getMessage());
        }

        if (response != null && response.code == 200) {
            Gson gson = new GsonBuilder().create();
            notifications = gson.fromJson(response.response, NotificationModel[].class);
        }
    }
}