package com.kardfox.authorio.views;

import android.content.Context;
import android.view.View;
import android.widget.Button;

import com.kardfox.authorio.MainActivity;
import com.kardfox.authorio.R;
import com.kardfox.authorio.models.ChapterModel;
import com.kardfox.authorio.search.ChapterFragment;
import com.kardfox.authorio.server_client.Server.Response;

import org.json.JSONException;
import org.json.JSONObject;

public class ChapterView extends View {
    private final Button button;

    public ChapterView(Context context) {
        super(context);
        button = (Button) inflate(context, R.layout.chapter_view, null);
    }

    public Button setData(MainActivity activity, String chapterTitle, String bookId) {
        button.setText(chapterTitle);
        button.setOnClickListener(_view -> {
            JSONObject json = new JSONObject();
            try {
                json.put("title", chapterTitle);
                json.put("book_id", bookId);
            } catch (JSONException ignored) {}

            Response response = activity.request(json, activity.URLs.get_chapter);
            ChapterModel chapter = activity.gson.fromJson(response.response, ChapterModel.class);
            if (response.code != 200) return;
            ChapterFragment readChapter = new ChapterFragment(chapter.text);

            activity.changeFragment(readChapter, new int[] {R.anim.slide_right_enter, R.anim.slide_left_exit});
            activity.hideBar();
        });

        return button;
    }
}
