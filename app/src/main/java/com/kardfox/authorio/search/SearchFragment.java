package com.kardfox.authorio.search;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kardfox.authorio.MainActivity;
import com.kardfox.authorio.MainActivity.Section;
import com.kardfox.authorio.R;
import com.kardfox.authorio.models.NotificationModel;
import com.kardfox.authorio.models.NotificationModel.NotificationTypes;

public class SearchFragment extends Fragment {
    public SearchFragment() {
        // Required empty public constructor
    }

    public static void showNotificationObject(MainActivity activity, NotificationModel notification) {
        if (notification.object_type == NotificationTypes.NEW_NOTE.intType) {
            // TODO: make new_note
        } else if (notification.object_type == NotificationTypes.NEW_BOOK.intType) {
            // TODO: make new_book
        } else if (notification.object_type == NotificationTypes.NEW_CHAPTER.intType) {
            // TODO: make new_chapter
        } else if (notification.object_type == NotificationTypes.NEW_LOVER.intType) {
            InfoUserFragment infoUserFragment = new InfoUserFragment(notification.object_id);

            activity.setSelected(Section.SEARCH);
            activity.changeFragment(infoUserFragment, new int[] {R.anim.slide_right_enter, R.anim.slide_left_exit});
        } else if (notification.object_type == NotificationTypes.NEW_COMMENTARY.intType) {
            // TODO: make new_commentary
        } else if (notification.object_type == NotificationTypes.NEW_HATER.intType) {
            // TODO: make new_hater
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        EditText editSearch = view.findViewById(R.id.editSearch);
        LinearLayout searchContainer = view.findViewById(R.id.searchContainer);
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                TextView child = new TextView(getContext());
                child.setText(editable);
                searchContainer.addView(child);
            }
        });
        return view;
    }

    private void search(String text) {
        
    }
}