package com.kardfox.authorio.write;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;
import com.kardfox.authorio.MainActivity;
import com.kardfox.authorio.R;

import org.json.JSONException;
import org.json.JSONObject;

import com.kardfox.authorio.server_client.Server.Response;


public class AddNoteFragment extends Fragment {
    public AddNoteFragment() {}

    MainActivity activity;
    public AddNoteFragment(MainActivity activity) {
        this.activity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_note, container, false);

        EditText editTextNote = view.findViewById(R.id.editTextNote);
        Button buttonUpload = view.findViewById(R.id.buttonUpload);

        buttonUpload.setOnClickListener(_view -> {
            String noteText = editTextNote.getText().toString();
            if (noteText.length() < 10) return;

            JSONObject json = new JSONObject();
            try {
                json.put("text", noteText);
            } catch (JSONException ignored) {}

            Response response = activity.request(json, activity.URLs.add_note);
            if (response.code != 200) return;

            activity.changeFragment(activity.fWrite, new int[] {R.anim.slide_right_enter, R.anim.slide_left_exit});
            TabLayout.Tab tab = activity.fWrite.tabLayout.getTabAt(1);
            tab.select();
            activity.showBar();
        });

        return view;
    }
}
