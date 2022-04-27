package com.kardfox.authorio.main;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.kardfox.authorio.MainActivity;
import com.kardfox.authorio.R;
import com.kardfox.authorio.models.NotificationModel;
import com.kardfox.authorio.models.UserModel;
import com.kardfox.authorio.search.InfoUserFragment;


public class MainFragment extends Fragment {
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        MainActivity activity = (MainActivity) context;
        LoveAuthorView.LoveAuthor.updateList(activity.GLOBAL_USER);
        NotificationView.Notification.updateList(activity.GLOBAL_USER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        LinearLayout loveAuthorsContainer = view.findViewById(R.id.loveAuthors);
        LinearLayout notesContainer = view.findViewById(R.id.notes);

        loadAuthors(loveAuthorsContainer);
        loadNotifications(notesContainer);

        return view;
    }

    public void loadAuthors(LinearLayout container) {
        if (LoveAuthorView.LoveAuthor.loveAuthors != null) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            for (UserModel loveAuthor : LoveAuthorView.LoveAuthor.loveAuthors) {
                LoveAuthorView loveAuthorView = new LoveAuthorView(getContext());
                loveAuthorView.setData(loveAuthor.photo, loveAuthor.name);

                View loveAuthorV = loveAuthorView.getView();
                loveAuthorV.setOnClickListener(view -> {
                    InfoUserFragment fInfoUser = new InfoUserFragment(loveAuthor.id);
                    MainActivity activity = ((MainActivity) getActivity());
                    activity.changeFragment(R.id.fragmentContainer, fInfoUser, "toInfo", new int[] {R.anim.slide_right_enter, R.anim.slide_left_exit});
                });
                loveAuthorV.setPadding(15, 0, 0, 0);
                container.addView(loveAuthorV, layoutParams);
            }
        } else {
            Toast.makeText(getContext(), "Server error", Toast.LENGTH_LONG).show();
        }
    }

    public void loadNotifications(LinearLayout container) {
        if (NotificationView.Notification.notifications != null) {
            for (NotificationModel notification : NotificationView.Notification.notifications) {
                String notificationText;
                switch (notification.object_type) {
                    case 1:
                        notificationText = notification.text; break;
                    case 5:
                        notificationText = String.format("%s\n%s", notification.title, notification.text); break;
                    default:
                        notificationText = notification.title; break;
                }
                NotificationView notificationView = new NotificationView(getContext());
                notificationView.setData(notification.author_photo, notification.author_name, notificationText, notification.datetime);

                View noteV = notificationView.getView();
                noteV.setPadding(0, 20, 0, 0);
                container.addView(noteV);
            }
            String textForNullView = NotificationView.Notification.notifications.length > 0? "" : getString(R.string.nullPlaceholder);
            NotificationView.NotificationViewNull viewNull = new NotificationView.NotificationViewNull(getContext(), textForNullView);
            container.addView(viewNull.getView());
        } else {
            Toast.makeText(getContext(), "Server error", Toast.LENGTH_LONG).show();
        }
    }
}