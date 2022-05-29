package com.kardfox.authorio.search;

import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kardfox.authorio.MainActivity;
import com.kardfox.authorio.R;
import com.kardfox.authorio.models.BookModel;
import com.kardfox.authorio.models.NoteModel;
import com.kardfox.authorio.views.BookView;
import com.kardfox.authorio.views.NoteView;
import com.kardfox.authorio.views.NotificationView;
import com.kardfox.authorio.models.CountLovers;
import com.kardfox.authorio.models.NotificationModel;
import com.kardfox.authorio.models.UserModel;
import com.kardfox.authorio.server_client.Client;
import com.kardfox.authorio.server_client.Server;
import com.kardfox.authorio.write.WriteFragment;

import org.json.JSONObject;

import java.net.URL;
import java.util.Locale;

public class InfoUserFragment extends Fragment {
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

    private String authorId;

    Button buttonSubscribe;
    View view;

    private String userToken;

    private UserModel author;

    private boolean subscribe;

    private int count;

    MainActivity activity;

    public InfoUserFragment(MainActivity activity, String authorId) {
        this.authorId = authorId;
        this.activity = activity;
    }

    InfoUserFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_info_user, container, false);

        userToken = activity.GLOBAL_USER.token;

        ImageView authorPhoto = view.findViewById(R.id.authorPhotoInfo);
        TextView authorName = view.findViewById(R.id.authorNameInfo);
        TextView authorDescription = view.findViewById(R.id.authorDescription);
        buttonSubscribe = view.findViewById(R.id.buttonSubscribe);


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

        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        FragmentManager fManager = activity.getSupportFragmentManager();

        WriteFragment.BookList bookList = new WriteFragment.BookList(authorId);
        WriteFragment.NotesList notesList = new WriteFragment.NotesList(authorId);

        FragmentTransaction fTransaction = fManager.beginTransaction();
        fTransaction.add(R.id.itemsContainer, bookList, "switch");
        fTransaction.commit();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();

                FragmentTransaction fTransaction = fManager.beginTransaction();
                if (position == 0) {
                    fTransaction.replace(R.id.itemsContainer, bookList, "switch");
                } else {
                    fTransaction.replace(R.id.itemsContainer, notesList, "switch");
                }
                fTransaction.commit();
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) { }

            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

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

    public static String getStrCount(int count) {
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
                    ((MainActivity) getActivity()).switchTo(MainActivity.Section.WRITE);
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
}