package com.kardfox.authorio.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kardfox.authorio.MainActivity;
import com.kardfox.authorio.R;
import com.kardfox.authorio.models.ChapterModel;
import com.kardfox.authorio.models.ResponseBookModel;
import com.kardfox.authorio.server_client.Server.Response;
import com.kardfox.authorio.views.ChapterView;
import com.kardfox.authorio.views.NotificationView;
import com.kardfox.authorio.write.AddChapterFragment;

import org.json.JSONException;
import org.json.JSONObject;

public class InfoBookFragment extends Fragment {
    public InfoBookFragment() {}

    MainActivity activity;
    String book_id;
    public InfoBookFragment(MainActivity activity, String book_id) {
        this.activity = activity;
        this.book_id = book_id;
    }

    @Override
    public void onStart() {
        super.onStart();
        activity.showBar();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info_book, container, false);

        TextView title = view.findViewById(R.id.bookTitle);
        TextView author = view.findViewById(R.id.bookAuthor);
        TextView tags = view.findViewById(R.id.textTags);
        TextView description = view.findViewById(R.id.bookDescription);
        TextView rating = view.findViewById(R.id.textRating);

        Button buttonAdd = view.findViewById(R.id.buttonAddChapter);
        buttonAdd.setOnClickListener(_view -> {
            activity.changeFragment(new AddChapterFragment(activity, book_id), new int[] {R.anim.slide_right_enter, R.anim.slide_left_exit});
            activity.hideBar();
        });

        LinearLayout chapters = view.findViewById(R.id.chapters);

        JSONObject json = new JSONObject();
        try {
            json.put("book_id", book_id);
        } catch (JSONException ignored) {}

        Response response = activity.request(json, activity.URLs.book_get);
        if (response.code != 200) return null;

        ResponseBookModel book = activity.gson.fromJson(response.response, ResponseBookModel.class);

        title.setText(book.book.title);
        author.setText(book.book.username);
        tags.setText(book.book.tags);
        description.setText(book.book.description);
        rating.setText(String.valueOf(book.book.raiting));

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        for (ChapterModel chapter : book.chapters) {
            ChapterView chapterView = new ChapterView(getContext());
            chapters.addView(chapterView.setData(activity, chapter.title, book_id));
        }

        chapters.addView(new NotificationView.NotificationViewNull(getContext(), book.chapters.length > 0? "" : getString(R.string.nullPlaceholder)).getView(), layoutParams);

        if (book.is_author) buttonAdd.setVisibility(View.VISIBLE);

        return view;
    }
}
