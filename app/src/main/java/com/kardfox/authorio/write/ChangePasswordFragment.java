package com.kardfox.authorio.write;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kardfox.authorio.MainActivity;
import com.kardfox.authorio.R;
import com.kardfox.authorio.server_client.Server;

import org.json.JSONException;
import org.json.JSONObject;

public class ChangePasswordFragment extends Fragment {
    public ChangePasswordFragment() {}

    MainActivity activity;

    public ChangePasswordFragment(MainActivity activity) {
        this.activity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);

        EditText editOldPassword = view.findViewById(R.id.editOldPassword);
        EditText editNewPassword = view.findViewById(R.id.editNewPassword);

        TextView textErrors = view.findViewById(R.id.textChangePassErrors);

        Button sendChanges = view.findViewById(R.id.buttonChangePasswordFr);
        activity.hideBar();
        sendChanges.setOnClickListener(_view -> {
            String oldPassword = editOldPassword.getText().toString();
            String newPassword = editNewPassword.getText().toString();

            if (oldPassword.length() < 8 || newPassword.length() < 8) {
                textErrors.setText(getString(R.string.shortPassword));
                return;
            }

            if (oldPassword.equals(newPassword)) {
                textErrors.setText(getString(R.string.passwordsAreSame));
                return;
            }

            JSONObject json = new JSONObject();
            try {
                json.put("new_password", newPassword);
                json.put("old_password", oldPassword);
            } catch (JSONException ignored) {}
            Server.Response response = activity.request(json, activity.URLs.change_password);

            if (response.code == 403) {
                textErrors.setText(R.string.wrongPassword);
                return;
            }

            Toast.makeText(activity, R.string.successfulPasswordEdit, Toast.LENGTH_LONG).show();
            activity.changeFragment(activity.fWrite.changeProfileFragment, new int[] {R.anim.slide_left_enter, R.anim.slide_right_exit});
            activity.showBar();
        });

        return view;
    }
}
