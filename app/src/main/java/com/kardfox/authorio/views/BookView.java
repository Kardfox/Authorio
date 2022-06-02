package com.kardfox.authorio.views;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.kardfox.authorio.R;
import com.kardfox.authorio.models.ResponseBookModel;

public class BookView extends ConstraintLayout {
    private final View view;

    public BookView(@NonNull Context context) {
        super(context);
        view = inflate(context, R.layout.book_view, null);
    }

    public void setData(ResponseBookModel.BookModel book) {
        TextView bookNameView = view.findViewById(R.id.bookName);
        TextView authorNameView = view.findViewById(R.id.authorNameView);
        TextView bookTagsView = view.findViewById(R.id.bookTags);
        TextView upload_date = view.findViewById(R.id.upload_date);
        TextView rating = view.findViewById(R.id.textRatingView);

        bookNameView.setText(book.title);
        authorNameView.setText(book.username);
        bookTagsView.setText(book.tags);
        upload_date.setText(book.upload_date);
        rating.setText(String.valueOf(book.raiting));
    }
    public View getView() { return view; }
}
