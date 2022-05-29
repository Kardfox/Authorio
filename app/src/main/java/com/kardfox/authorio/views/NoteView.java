package com.kardfox.authorio.views;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.kardfox.authorio.R;
import com.kardfox.authorio.models.NoteModel;

public class NoteView extends ConstraintLayout {
    private final View view;

    public NoteView(Context context) {
        super(context);
        view = inflate(context, R.layout.note_view, null);
    }

    public void setData(NoteModel note) {
        TextView authorName = view.findViewById(R.id.authorName);
        TextView textNote = view.findViewById(R.id.textNote);
        TextView datetime = view.findViewById(R.id.upload_date);

        authorName.setText(note.authorname);
        textNote.setText(note.text);
        datetime.setText(note.datetime);
    }

    public View getView() { return view; }
}
