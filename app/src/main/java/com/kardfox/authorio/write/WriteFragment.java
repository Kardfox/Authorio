package com.kardfox.authorio.write;

import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kardfox.authorio.MainActivity;
import com.kardfox.authorio.R;
import com.kardfox.authorio.models.BookModel;
import com.kardfox.authorio.models.CountLovers;
import com.kardfox.authorio.models.NoteModel;
import com.kardfox.authorio.models.NotificationModel;
import com.kardfox.authorio.search.InfoUserFragment;
import com.kardfox.authorio.server_client.Client;
import com.kardfox.authorio.server_client.Server;
import com.kardfox.authorio.views.BookView;
import com.kardfox.authorio.views.NoteView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;


public class WriteFragment extends Fragment {
    public static class BookList extends Fragment {
        String user_id;

        public BookList(String user_id) {
            this.user_id = user_id;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Server.Response response = null;

            try {
                JSONObject json = new JSONObject();

                json.put("user_id", user_id);

                URL url = new URL(Server.URLs.books_get);
                Client.Post post = new Client.Post(url, json);
                post.execute();

                response = post.get();
            } catch (Exception exception) {
                Log.e(MainActivity.LOG_TAG, exception.getMessage());
            }

            BookModel[] books;
            if (response != null && response.code == 200) {
                Gson gson = new GsonBuilder().create();
                books = gson.fromJson(response.response, BookModel[].class);
            } else {
                books = null;
            }

            View view = inflater.inflate(R.layout.books_list, container, false);
            LinearLayout booksList = view.findViewById(R.id.booksList);

            if (books != null) {
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                for (BookModel book: books) {
                    BookView bookView = new BookView(getContext());
                    bookView.setData(book);
                    booksList.addView(bookView.getView(), layoutParams);
                }
            }

            return view;
        }
    }

    public static class NotesList extends Fragment {
        String user_id;

        public NotesList(String user_id) {
            this.user_id = user_id;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Server.Response response = null;

            try {
                JSONObject json = new JSONObject();

                json.put("user_id", user_id);

                URL url = new URL(Server.URLs.notes_get);
                Client.Post post = new Client.Post(url, json);
                post.execute();

                response = post.get();
            } catch (Exception exception) {
                Log.e(MainActivity.LOG_TAG, exception.getMessage());
            }

            NoteModel[] notes;
            if (response != null && response.code == 200) {
                Gson gson = new GsonBuilder().create();
                notes = gson.fromJson(response.response, NoteModel[].class);
            } else {
                notes = null;
            }

            View view = inflater.inflate(R.layout.notes_list, container, false);
            LinearLayout notesList = view.findViewById(R.id.notesList);

            if (notes != null) {
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                for (NoteModel note: notes) {
                    NoteView noteView = new NoteView(getContext());
                    noteView.setData(note);
                    notesList.addView(noteView.getView(), layoutParams);
                }
            }

            return view;
        }
    }

    MainActivity activity;

    public WriteFragment() {}

    public WriteFragment(MainActivity activity) {
        this.activity = activity;
        setUserData();
    }

    void setUserData() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_write, container, false);

        TextView authorNameInfo = view.findViewById(R.id.authorNameInfo);
        authorNameInfo.setText(String.format("%s %s", activity.GLOBAL_USER.name, activity.GLOBAL_USER.surname));
        TextView authorDescription = view.findViewById(R.id.authorDescription);
        authorDescription.setText(activity.GLOBAL_USER.description);
        TextView loversCount = view.findViewById(R.id.loversCount);

        String url = String.format(Server.URLs.get_lovers, Server.URLs.main, activity.GLOBAL_USER.token);
        JSONObject json = new JSONObject();
        try {
            json.put("id", activity.GLOBAL_USER.id);
        } catch (JSONException ignored) {}
        String response = activity.request(json, url);
        if (response == null) return null;

        CountLovers countLovers = activity.gson.fromJson(response, CountLovers.class);
        loversCount.setText(String.format("%s %s", InfoUserFragment.getStrCount(countLovers.lovers.length), getString(R.string.lovers)));

        ImageView authorPhoto = view.findViewById(R.id.authorPhoto);
        byte[] imageBytes = Base64.decode(activity.GLOBAL_USER.photo, 0);
        authorPhoto.setImageBitmap(BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length));

        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        FragmentManager fManager = activity.getSupportFragmentManager();

        BookList bookList = new BookList(activity.GLOBAL_USER.id);
        NotesList notesList = new NotesList(activity.GLOBAL_USER.id);

        FragmentTransaction fTransaction = fManager.beginTransaction();
        fTransaction.add(R.id.itemsContainer, bookList, "switch");
        fTransaction.commit();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                Log.d(MainActivity.LOG_TAG, String.format("SELECTED AUTHOR TAB^ %d", position));

                if (position == 0) {
                    FragmentTransaction fTransaction = fManager.beginTransaction();
                    fTransaction.replace(R.id.itemsContainer, bookList, "switch");
                    fTransaction.commit();
                } else {
                    FragmentTransaction fTransaction = fManager.beginTransaction();
                    fTransaction.replace(R.id.itemsContainer, notesList, "switch");
                    fTransaction.commit();
                }
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) { }

            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        return view;
    }
}