package com.kardfox.authorio.write;

import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.tabs.TabLayout;
import com.kardfox.authorio.MainActivity;
import com.kardfox.authorio.R;

import com.kardfox.authorio.models.CountLovers;
import com.kardfox.authorio.search.InfoUserFragment;
import com.kardfox.authorio.server_client.Server.Response;

import org.json.JSONException;
import org.json.JSONObject;



public class WriteFragment extends Fragment {
    MainActivity activity;

    View view = null;

    public ChangeProfileFragment changeProfileFragment;

    public WriteFragment() {}

    public WriteFragment(MainActivity activity) {
        this.activity = activity;
    }

    private void setData(View view) {
        TextView authorNameInfo = view.findViewById(R.id.authorNameInfo);
        authorNameInfo.setText(String.format("%s %s", activity.GLOBAL_USER.name, activity.GLOBAL_USER.surname));
        TextView authorDescription = view.findViewById(R.id.authorDescription);
        authorDescription.setText(activity.GLOBAL_USER.description);
        TextView loversCount = view.findViewById(R.id.loversCount);

        JSONObject json = new JSONObject();
        try {
            json.put("id", activity.GLOBAL_USER.id);
        } catch (JSONException ignored) {}
        Response response = activity.request(json, activity.URLs.get_lovers);
        if (response.code != 200) return;

        CountLovers countLovers = activity.gson.fromJson(response.response, CountLovers.class);
        loversCount.setText(String.format("%s %s", InfoUserFragment.getStrCount(countLovers.lovers.length), getString(R.string.lovers)));

        ShapeableImageView authorPhoto = view.findViewById(R.id.authorPhoto);
        byte[] imageBytes = Base64.decode(activity.GLOBAL_USER.photo, 0);
        authorPhoto.setImageBitmap(BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length));

        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        FragmentManager fManager = activity.getSupportFragmentManager();

        InfoUserFragment.BookList bookList = new InfoUserFragment.BookList(activity.GLOBAL_USER.id, activity);
        InfoUserFragment.NotesList notesList = new InfoUserFragment.NotesList(activity.GLOBAL_USER.id, activity);

        FragmentTransaction fTransaction = fManager.beginTransaction();
        fTransaction.add(R.id.itemsContainer, bookList, "switch");
        fTransaction.commit();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                Log.d(MainActivity.LOG_TAG, String.format("SELECTED AUTHOR TAB^ %d", position));

                FragmentTransaction fTransaction = fManager.beginTransaction();
                if (position == 0) {
                    fTransaction.replace(R.id.itemsContainer, bookList, "switch");
                } else {
                    fTransaction.replace(R.id.itemsContainer, notesList, "switch");
                }
                fTransaction.commit();
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) { }

            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        Button buttonLogOut = view.findViewById(R.id.buttonLogOut);
        buttonLogOut.setOnClickListener(_view -> activity.logOut());

        Button buttonChangeProfile = view.findViewById(R.id.buttonChangeProfile);
        buttonChangeProfile.setOnClickListener(_view -> {
            changeProfileFragment = new ChangeProfileFragment(activity);
            activity.changeFragment(changeProfileFragment, new int[] {R.anim.slide_right_enter, R.anim.slide_left_exit});
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (view != null && activity != null) setData(view);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_write, container, false);
        return view;
    }
}