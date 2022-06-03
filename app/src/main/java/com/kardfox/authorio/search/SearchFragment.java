package com.kardfox.authorio.search;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kardfox.authorio.MainActivity;
import com.kardfox.authorio.MainActivity.Section;
import com.kardfox.authorio.R;
import com.kardfox.authorio.models.ResponseBookModel;
import com.kardfox.authorio.views.AuthorView;
import com.kardfox.authorio.models.NotificationModel;
import com.kardfox.authorio.models.NotificationModel.NotificationTypes;
import com.kardfox.authorio.models.UserModel;
import com.kardfox.authorio.server_client.Client;
import com.kardfox.authorio.server_client.Server;
import com.kardfox.authorio.views.BookView;

import org.json.JSONObject;

import java.net.URL;
import java.util.Locale;

public class SearchFragment extends Fragment {
    LinearLayout userContainer = null;
    LinearLayout booksSearched = null;

    public SearchFragment() { }

    MainActivity activity;

    public SearchFragment(MainActivity activity) {
        this.activity = activity;
    }

    public static void showNotificationObject(MainActivity activity, NotificationModel notification) {
        if (notification.object_type == NotificationTypes.NEW_NOTE.intType) {
            // TODO: make new_note
        } else if (notification.object_type == NotificationTypes.NEW_BOOK.intType) {
            InfoBookFragment infoBookFragment = new InfoBookFragment(activity, notification.object_id);

            activity.setSelected(Section.SEARCH);
            activity.changeFragment(infoBookFragment, new int[] {R.anim.slide_left_enter, R.anim.slide_right_exit});
        } else if (notification.object_type == NotificationTypes.NEW_CHAPTER.intType) {

        } else if (notification.object_type == NotificationTypes.NEW_LOVER.intType) {
            InfoUserFragment infoUserFragment = new InfoUserFragment(activity, notification.object_id);

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
        booksSearched = view.findViewById(R.id.booksSearched);
        editSearch.setOnEditorActionListener((_view, actionId, event) -> {
            if (actionId == 6) {
                search(editSearch.getText().toString());

                return true;
            }

            return false;
        });
        return view;
    }

    private UserModel[] loadUsers(String text) {
        String name = String.format("%s%%", text);
        String surname = String.format("%s%%", text);

        try {
            JSONObject json = new JSONObject();
            json.put("name", name);
            json.put("surname", surname);

            Server.Response response = activity.request(json, activity.URLs.get_user);
            if (response.code != 200) return null;

            return activity.gson.fromJson(response.response, UserModel[].class);
        } catch (Exception exception) {
            Log.e(MainActivity.LOG_TAG, exception.getLocalizedMessage());
        }
        return null;
    }

    private ResponseBookModel.BookModel[] loadBooks(String text) {
        String title = String.format("%s%%", text);
        String tags = String.format("%%%s%%", text.toLowerCase(Locale.ROOT));
        try {
            JSONObject json = new JSONObject();
            json.put("title", title);
            json.put("tags", tags);

            Server.Response response = activity.request(json, activity.URLs.books_get);
            if (response.code != 200) return null;

            return activity.gson.fromJson(response.response, ResponseBookModel.BookModel[].class);
        } catch (Exception exception) {
            Log.e(MainActivity.LOG_TAG, exception.getLocalizedMessage());
        }
        return null;
    }

    private void search(String searchText) {
        if (searchText.equals("")) return;
        UserModel[] users = loadUsers(searchText);
        ResponseBookModel.BookModel[] books = loadBooks(searchText);


        LinearLayout.LayoutParams layoutParamsUsers = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        userContainer.removeAllViews();
        if (users != null) {
            for (UserModel user : users) {
                AuthorView authorView = new AuthorView(getContext());
                authorView.setData(user.photo, user.name);

                View loveAuthorV = authorView.getView();
                loveAuthorV.setOnClickListener(view -> {
                    InfoUserFragment fInfoUser = new InfoUserFragment(activity, user.id);
                    activity.changeFragment(fInfoUser, new int[]{R.anim.slide_right_enter, R.anim.slide_left_exit});
                    activity.setSelected(Section.SEARCH);
                });
                loveAuthorV.setPadding(15, 0, 0, 0);
                userContainer.addView(loveAuthorV, layoutParamsUsers);
            }
        }

        booksSearched.removeAllViews();
        if (books != null) {
            for (ResponseBookModel.BookModel book : books) {
                BookView bookView = new BookView(getContext());
                bookView.setData(book);

                View bookV = bookView.getView();
                bookV.setOnClickListener(_view -> {
                    NotificationModel notification = new NotificationModel();
                    notification.object_id = book.id;
                    notification.object_type = NotificationModel.NotificationTypes.NEW_BOOK.intType;
                    SearchFragment.showNotificationObject(activity, notification);
                    activity.showBar();
                });
                booksSearched.addView(bookV);
            }
        }
    }
}