package com.kardfox.authorio.write;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kardfox.authorio.MainActivity;
import com.kardfox.authorio.R;


public class WriteFragment extends Fragment {

    MainActivity activity;

    public WriteFragment() {}

    public WriteFragment(MainActivity activity) {
        this.activity = activity;
        setUserData();
    }

    void setUserData() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_write, container, false);
    }
}