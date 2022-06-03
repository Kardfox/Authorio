package com.kardfox.authorio.write;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kardfox.authorio.MainActivity;
import com.kardfox.authorio.R;
import com.kardfox.authorio.search.InfoBookFragment;
import com.kardfox.authorio.server_client.Server;

import org.json.JSONException;
import org.json.JSONObject;

public class AddChapterFragment extends Fragment {
    public AddChapterFragment() {}

    MainActivity activity;
    String bookId;
    public AddChapterFragment(MainActivity activity, String bookId) {
        this.activity = activity;
        this.bookId = bookId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_chapter, container, false);

        EditText textChapter = view.findViewById(R.id.editTextChapter);
        Button buttonUploadChapter = view.findViewById(R.id.buttonUploadChapter);
        buttonUploadChapter.setOnClickListener(_view -> {
            String text = textChapter.getText().toString();
            if (text.length() < 1) return;;

            String[] chapter = text.split("\n", 2);
            if (chapter.length < 2) {
                Toast.makeText(activity, "Введите первой строчкой название главы", Toast.LENGTH_LONG).show();
                return;
            }
            JSONObject json = new JSONObject();
            try {
                json.put("book_id", bookId);
                json.put("title", chapter[0]);
                json.put("text", chapter[1]);
            } catch (JSONException ignored) {}

            Server.Response response = activity.request(json, activity.URLs.add_chapter);
            if (response.code != 200) return;

            activity.changeFragment(new InfoBookFragment(activity, bookId), new int[] {R.anim.slide_left_enter, R.anim.slide_right_exit});
            activity.showBar();
        });

        return view;
    }
}
