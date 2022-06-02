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
import com.kardfox.authorio.MainActivity;
import com.kardfox.authorio.R;
import com.kardfox.authorio.models.ResponseBookModel;
import com.kardfox.authorio.models.NoteModel;
import com.kardfox.authorio.views.BookView;
import com.kardfox.authorio.views.NoteView;
import com.kardfox.authorio.models.CountLovers;
import com.kardfox.authorio.models.UserModel;

import com.kardfox.authorio.server_client.Server.Response;
import com.kardfox.authorio.views.NotificationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class InfoUserFragment extends Fragment {
    public static class BookList extends Fragment {
        String user_id;
        MainActivity activity;

        public BookList() {}

        public BookList(String user_id, MainActivity activity) {
            this.user_id = user_id;
            this.activity = activity;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.books_list, container, false);
            if (activity == null) return view;

            ResponseBookModel.BookModel[] books = null;
            try {
                JSONObject json = new JSONObject();
                json.put("user_id", user_id);

                Response response = activity.request(json, activity.URLs.books_get);
                if (response.code != 200) return null;
                books = activity.gson.fromJson(response.response, ResponseBookModel.BookModel[].class);
            } catch (JSONException ignored) {}


            LinearLayout booksList = view.findViewById(R.id.booksList);

            if (books != null) {
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                for (ResponseBookModel.BookModel book: books) {
                    BookView bookView = new BookView(activity);
                    bookView.setData(book);
                    booksList.addView(bookView.getView(), layoutParams);
                }
                booksList.addView(new NotificationView.NotificationViewNull(getContext(), books.length > 0? "" : getString(R.string.nullPlaceholder)).getView());
            }

            return view;
        }
    }

    public static class NotesList extends Fragment {
        String user_id;
        MainActivity activity;

        public NotesList() {}

        public NotesList(String user_id, MainActivity activity) {
            this.user_id = user_id;
            this.activity = activity;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.notes_list, container, false);
            
            NoteModel[] notes = null;
            try {
                JSONObject json = new JSONObject();
                json.put("user_id", user_id);

                Response response = activity.request(json, activity.URLs.notes_get);
                if (response.code != 200) return null;
                notes = activity.gson.fromJson(response.response, NoteModel[].class);
            } catch (JSONException ignored) {}


            LinearLayout notesList = view.findViewById(R.id.notesList);

            if (notes != null) {
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                for (NoteModel note: notes) {
                    NoteView noteView = new NoteView(getContext());
                    noteView.setData(note);
                    notesList.addView(noteView.getView(), layoutParams);
                }
                notesList.addView(new NotificationView.NotificationViewNull(getContext(), notes.length > 0? "" : getString(R.string.nullPlaceholder)).getView());
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


    public InfoUserFragment() {}

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

        BookList bookList = new BookList(authorId, activity);
        NotesList notesList = new NotesList(authorId, activity);

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

                activity.request(json, activity.URLs.subscribe);

                buttonSubscribe.setText(getStrCount(++count));
            } catch (Exception exception) {
                Log.e(MainActivity.LOG_TAG, exception.getLocalizedMessage());
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

                activity.request(json, activity.URLs.unsubscribe);

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
        try {
            JSONObject json = new JSONObject();
            json.put("id", authorId);

            Response response = activity.request(json, activity.URLs.get_user);
            if (response.code != 200) return;
            author = activity.gson.fromJson(response.response, UserModel[].class)[0];

            response = activity.request(json, activity.URLs.get_lovers);

            CountLovers lovers = activity.gson.fromJson(response.response, CountLovers.class);

            if (lovers.mirror == 1) {
                activity.switchTo(MainActivity.Section.WRITE);
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

        } catch (Exception exception) {
            Log.e(MainActivity.LOG_TAG, exception.getMessage());
        }
    }
}