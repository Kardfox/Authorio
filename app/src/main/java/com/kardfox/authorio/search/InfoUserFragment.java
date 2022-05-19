package com.kardfox.authorio.search;

import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kardfox.authorio.MainActivity;
import com.kardfox.authorio.R;
import com.kardfox.authorio.views.NotificationView;
import com.kardfox.authorio.models.CountLovers;
import com.kardfox.authorio.models.NotificationModel;
import com.kardfox.authorio.models.UserModel;
import com.kardfox.authorio.server_client.Client;
import com.kardfox.authorio.server_client.Server;

import org.json.JSONObject;

import java.net.URL;
import java.util.Locale;

public class InfoUserFragment extends Fragment {
    private String authorId;

    Button buttonSubscribe;
    TextView subscribersCount;
    View view;

    private String userToken;

    private UserModel author;
    private NotificationModel[] notifications;

    private boolean subscribe;

    private int count;

    public InfoUserFragment(String authorId) {
        this.authorId = authorId;
    }
    InfoUserFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_info_user, container, false);

        userToken = ((MainActivity) getActivity()).GLOBAL_USER.token;

        ImageView authorPhoto = view.findViewById(R.id.authorPhotoInfo);
        TextView authorName = view.findViewById(R.id.authorNameInfo);
        TextView authorDescription = view.findViewById(R.id.authorDescription);
        buttonSubscribe = view.findViewById(R.id.buttonSubscribe);

        LinearLayout notificationContainer = view.findViewById(R.id.notificationsContainerInfo);

        loadInfo();
        if (author != null) {
            byte[] imageBytes = Base64.decode(author.photo, 0);
            authorPhoto.setImageBitmap(BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length));

            authorName.setText(String.format("%s %s", author.name, author.surname));
            authorDescription.setText(author.description);

            if (subscribe) {
                subscribe(false);
            } else {
                unsubscribe(false);
            }
        } else {
            Toast.makeText(getContext(), "Server error", Toast.LENGTH_LONG).show();
        }

        loadHistory();
        if (notifications != null) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            for (NotificationModel notification : notifications) {
                if (notification.object_type == 5) {
                    notification.text = String.format("%s\n%s", notification.title, notification.text);
                } else {
                    notification.text = notification.title;
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

    void subscribe(boolean send) {
        buttonSubscribe.setBackgroundColor(view.getContext().getColor(R.color.second_pink));

        if (send) {
            try {
                JSONObject json = new JSONObject();
                json.put("author_id", authorId);

                URL url = new URL(String.format(Server.URLs.subscribe, Server.URLs.main, userToken));
                Client.Post post = new Client.Post(url, json);
                post.execute();

                buttonSubscribe.setText(getStrCount(++count));
            } catch (Exception exception) {
                Log.e(MainActivity.LOG_TAG, exception.getMessage());
            }
        }

        buttonSubscribe.setOnClickListener(view2 -> unsubscribe(true));
    }

    void unsubscribe(boolean send) {
        buttonSubscribe.setBackgroundColor(view.getContext().getColor(R.color.main_blue));

        if (send) {
            try {
                JSONObject json = new JSONObject();
                json.put("author_id", authorId);

                URL url = new URL(String.format(Server.URLs.unsubscribe, Server.URLs.main, userToken));
                Client.Post post = new Client.Post(url, json);
                post.execute();

                buttonSubscribe.setText(getStrCount(--count));
            } catch (Exception exception) {
                Log.e(MainActivity.LOG_TAG, exception.getMessage());
            }
        }

        buttonSubscribe.setOnClickListener(view2 -> subscribe(true));
    }

    public String getStrCount(int count) {
        if (count >= 1000000) return String.format(Locale.ROOT, "%.1fM", count / 1000000.0);
        else if (count >= 1000) return String.format(Locale.ROOT , "%.1fK", count / 1000.0);
        else return String.valueOf(count);
    }

    public void loadInfo() {
        Server.Response response = null;

        try {
            JSONObject json = new JSONObject();
            Gson gson = new GsonBuilder().create();
            json.put("id", authorId);

            URL url = new URL(Server.URLs.get_user);
            Client.Post post = new Client.Post(url, json);
            post.execute();

            response = post.get();
            if (response != null && response.code == 200) {
                author = gson.fromJson(response.response, UserModel[].class)[0];
            }

            url = new URL(String.format(Server.URLs.get_lovers, Server.URLs.main, userToken));
            post = new Client.Post(url, json);
            post.execute();

            response = post.get();
            if (response != null && response.code == 200) {
                CountLovers lovers = gson.fromJson(response.response, CountLovers.class);

                if (lovers.mirror == 1) {
                    buttonSubscribe.setBackgroundColor(view.getContext().getColor(R.color.gray));
                    buttonSubscribe.setEnabled(false);
                }

                subscribe = lovers.subscribe > 0;
                count = lovers.lovers.length;
                if (count < 100000) {
                    buttonSubscribe.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    buttonSubscribe.setPadding(0, 0, 5, 0);
                }
                buttonSubscribe.setText(getStrCount(count));
            }
        } catch (Exception exception) {
            Log.e(MainActivity.LOG_TAG, exception.getMessage());
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