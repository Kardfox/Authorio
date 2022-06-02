package com.kardfox.authorio.views;

import android.content.Context;
import android.view.View;
import android.widget.Button;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.kardfox.authorio.R;

public class ChapterView extends ConstraintLayout {
    private final View view;

    public ChapterView(Context context) {
        super(context);
        view = inflate(context, R.layout.chapter_view, null);
    }

    public View setData(String chapterTitle, String bookId) {
        Button buttonToChapter = view.findViewById(R.id.buttonToChapter);
        buttonToChapter.setText(chapterTitle);
        buttonToChapter.setOnClickListener(_view -> {});

        return view;
    }
}
