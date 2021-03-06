package com.kardfox.authorio.main;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.kardfox.authorio.MainActivity;
import com.kardfox.authorio.MainActivity.Section;
import com.kardfox.authorio.R;
import com.kardfox.authorio.models.NotificationModel;
import com.kardfox.authorio.models.UserModel;
import com.kardfox.authorio.search.InfoUserFragment;
import com.kardfox.authorio.search.SearchFragment;
import com.kardfox.authorio.views.AuthorView;
import com.kardfox.authorio.views.NotificationView;


public class MainFragment extends Fragment {
    public MainActivity activity = null;
    View view;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadAuthors();
        loadNotifications();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main, container, false);
        return view;
    }

    public void loadAuthors() {
        AuthorView.Author.updateList(activity.GLOBAL_USER, activity);
        LinearLayout container = view.findViewById(R.id.loveAuthors);
        if (AuthorView.Author.authors != null) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            for (UserModel loveAuthor : AuthorView.Author.authors) {
                AuthorView authorView = new AuthorView(getContext());
                authorView.setData(loveAuthor.photo, loveAuthor.name);

                View loveAuthorV = authorView.getView();
                loveAuthorV.setOnClickListener(view -> {
                    InfoUserFragment fInfoUser = new InfoUserFragment(activity, loveAuthor.id);
                    activity.changeFragment(fInfoUser, new int[] {R.anim.slide_right_enter, R.anim.slide_left_exit});
                    activity.setSelected(Section.SEARCH);
                });
                loveAuthorV.setPadding(15, 0, 0, 0);
                container.addView(loveAuthorV, layoutParams);
            }
        } else {
            container.removeAllViews();
        }
    }

    public void loadNotifications() {
        NotificationView.Notification.updateList(activity.GLOBAL_USER, activity);
        LinearLayout container = view.findViewById(R.id.notes);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        if (NotificationView.Notification.notifications != null) {
            for (NotificationModel notification : NotificationView.Notification.notifications) {
                if (notification.object_type == 5) {
                    notification.text = String.format("%s\n%s", notification.title, notification.text);
                } else {
                    notification.text = notification.title;
                }
                NotificationView notificationView = new NotificationView(getContext());

                View.OnClickListener listener = view -> SearchFragment.showNotificationObject(activity, notification);
                notificationView.setData(notification, listener);

                View noteV = notificationView.getView();
                noteV.setPadding(0, 20, 0, 0);
                container.addView(noteV);

                container.addView(new NotificationView.NotificationViewNull(getContext(), "").getView(), layoutParams);
            }
        } else {
            container.removeAllViews();
            container.addView(new NotificationView.NotificationViewNull(getContext(), getString(R.string.nullPlaceholder)).getView(), layoutParams);
        }
    }
}