package com.kardfox.authorio.write;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kardfox.authorio.MainActivity;
import com.kardfox.authorio.R;
import com.kardfox.authorio.models.NotificationModel;
import com.kardfox.authorio.search.SearchFragment;
import com.kardfox.authorio.server_client.Server;

import org.json.JSONException;
import org.json.JSONObject;

public class AddBookFragment extends Fragment {
    public AddBookFragment() {}

    MainActivity activity;

    public AddBookFragment(MainActivity activity) {
        this.activity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_book, container, false);

        EditText editTitle = view.findViewById(R.id.editBookTitle);
        EditText editDescription = view.findViewById(R.id.editBookDescription);
        EditText editTags = view.findViewById(R.id.editTags);

        TextView textErrors = view.findViewById(R.id.textBookErrors);

        Button buttonAdd = view.findViewById(R.id.buttonAdd);
        activity.hideBar();
        buttonAdd.setOnClickListener(_view -> {
            String title = editTitle.getText().toString();
            String description = editDescription.getText().toString();
            String tags = editTags.getText().toString().trim().replace("\n", "");

            if (title.length() < 1 || description.length() < 1 || tags.length() < 1) {
                textErrors.setText(R.string.emptyEntries);
                return;
            }

            if (tags.split("#").length > 10) {
                textErrors.setText(R.string.tooManyTags);
                return;
            }

            JSONObject json = new JSONObject();
            try {
                json.put("title", title);
                json.put("description", description);
                json.put("tags", tags);
            } catch (JSONException ignored) {}

            Server.Response response = activity.request(json, activity.URLs.add_book);
            if (response.code != 200) return;
            NotificationModel notification = new NotificationModel();
            notification.object_id = response.response;
            notification.object_type = NotificationModel.NotificationTypes.NEW_BOOK.intType;
            SearchFragment.showNotificationObject(activity, notification);
            activity.showBar();
        });

        return view;
    }
}
