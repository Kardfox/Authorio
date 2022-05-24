package com.kardfox.authorio.write;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kardfox.authorio.MainActivity;
import com.kardfox.authorio.R;
import com.kardfox.authorio.models.BookModel;
import com.kardfox.authorio.models.NotificationModel;
import com.kardfox.authorio.server_client.Client;
import com.kardfox.authorio.server_client.Server;

import org.json.JSONObject;

import java.net.URL;


public class WriteFragment extends Fragment {
    public static class BookList extends  Fragment {
        public BookList(String user_id) {
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

            if (books != null) {
                for (BookModel book: books) {

                }
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.books_list, container, false);
        }
    }

    public static class NotesList extends Fragment {
        public NotesList() {}

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.notes_list, container, false);
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

        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        FragmentManager fManager = activity.getSupportFragmentManager();

        BookList bookList = new BookList("");
        NotesList notesList = new NotesList();

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