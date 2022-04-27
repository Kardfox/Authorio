package com.kardfox.authorio.search;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kardfox.authorio.MainActivity;
import com.kardfox.authorio.MainActivity.Section;
import com.kardfox.authorio.R;
import com.kardfox.authorio.models.NotificationModel;
import com.kardfox.authorio.models.NotificationModel.NotificationTypes;

public class SearchFragment extends Fragment {
    public SearchFragment() {
        // Required empty public constructor
    }

    public static void search(MainActivity activity, NotificationModel notification) {
        if (notification.object_type == NotificationTypes.NEW_NOTE.intType) {
            // TODO: make new_note
        } else if (notification.object_type == NotificationTypes.NEW_BOOK.intType) {
            // TODO: make new_book
        } else if (notification.object_type == NotificationTypes.NEW_CHAPTER.intType) {
            // TODO: make new_chapter
        } else if (notification.object_type == NotificationTypes.NEW_LOVER.intType) {
            InfoUserFragment infoUserFragment = new InfoUserFragment(notification.object_id);

            activity.switchTo(Section.SEARCH);
            activity.changeFragment(infoUserFragment, new int[] {R.anim.slide_right_enter, R.anim.slide_left_exit});
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }
}