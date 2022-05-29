package com.kardfox.authorio.views;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.kardfox.authorio.R;
import com.kardfox.authorio.models.BookModel;

public class BookView extends ConstraintLayout {
    private final View view;

    public BookView(@NonNull Context context) {
        super(context);
        view = inflate(context, R.layout.book_view, null);
    }

    public void setData(BookModel book) {
        TextView bookNameView = view.findViewById(R.id.bookName);
        TextView authorNameView = view.findViewById(R.id.authorNameView);
        TextView bookDescriptionView = view.findViewById(R.id.bookDescription);
        TextView upload_date = view.findViewById(R.id.upload_date);

        bookNameView.setText(book.title);
        authorNameView.setText(book.username);
        bookDescriptionView.setText(book.description);
        upload_date.setText(book.upload_date);
    }
    public View getView() { return view; }
}
