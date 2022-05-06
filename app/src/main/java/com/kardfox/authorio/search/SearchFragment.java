package com.kardfox.authorio.search;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kardfox.authorio.MainActivity;
import com.kardfox.authorio.MainActivity.Section;
import com.kardfox.authorio.R;
import com.kardfox.authorio.main.LoveAuthorView;
import com.kardfox.authorio.models.NotificationModel;
import com.kardfox.authorio.models.NotificationModel.NotificationTypes;
import com.kardfox.authorio.models.UserModel;
import com.kardfox.authorio.server_client.Client;
import com.kardfox.authorio.server_client.Server;

import org.json.JSONObject;

import java.net.URL;

public class SearchFragment extends Fragment {
    LinearLayout userContainer = null;
    public SearchFragment() {
        // Required empty public constructor
    }

    public static void showNotificationObject(MainActivity activity, NotificationModel notification) {
        if (notification.object_type == NotificationTypes.NEW_NOTE.intType) {
            // TODO: make new_note
        } else if (notification.object_type == NotificationTypes.NEW_BOOK.intType) {
            // TODO: make new_book
        } else if (notification.object_type == NotificationTypes.NEW_CHAPTER.intType) {
            // TODO: make new_chapter
        } else if (notification.object_type == NotificationTypes.NEW_LOVER.intType) {
            InfoUserFragment infoUserFragment = new InfoUserFragment(notification.object_id);

            activity.setSelected(Section.SEARCH);
            activity.changeFragment(infoUserFragment, new int[]{R.anim.slide_right_enter, R.anim.slide_left_exit});
        } else if (notification.object_type == NotificationTypes.NEW_COMMENTARY.intType) {
            // TODO: make new_commentary
        } else if (notification.object_type == NotificationTypes.NEW_HATER.intType) {
            // TODO: make new_hater
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        EditText editSearch = view.findViewById(R.id.editSearch);
        userContainer = view.findViewById(R.id.searchAuthors);
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                search(editable.toString());
            }
        });
        return view;
    }

    private UserModel[] loadUsers(String text) {
        Server.Response response = null;

        String name = String.format("%s%%", text.split(" ")[0]);
        String surname = String.format("%s |", text);
        surname = String.format("%s%%", surname.split(" ")[1]);

        try {
            JSONObject json = new JSONObject();
            json.put("name", name);
            json.put("surname", surname);

            URL url = new URL(Server.URLs.get_user);
            Client.Post post = new Client.Post(url, json);
            post.execute();

            response = post.get();
        } catch (Exception exception) {
            Log.e(MainActivity.LOG_TAG, exception.getMessage());
        }

        if (response != null && response.code == 200) {
            Gson gson = new GsonBuilder().create();

            return gson.fromJson(response.response, UserModel[].class);
        } else {
            return null;
        }
    }

    private void search(String text) {
        MainActivity activity = (MainActivity) getActivity();
        if (text.equals("")) {
            userContainer.removeAllViews();
            return;
        }
        UserModel[] users = loadUsers(text);
        LinearLayout.LayoutParams layoutParamsUsers = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        userContainer.removeAllViews();
        for (UserModel user : users) {
            LoveAuthorView loveAuthorView = new LoveAuthorView(getContext());
            loveAuthorView.setData(user.photo, user.name);

            View loveAuthorV = loveAuthorView.getView();
            loveAuthorV.setOnClickListener(view -> {
                InfoUserFragment fInfoUser = new InfoUserFragment(user.id);
                activity.changeFragment(fInfoUser, new int[]{R.anim.slide_right_enter, R.anim.slide_left_exit});
                activity.setSelected(Section.SEARCH);
            });
            loveAuthorV.setPadding(15, 0, 0, 0);
            userContainer.addView(loveAuthorV, layoutParamsUsers);
        }
    }
}