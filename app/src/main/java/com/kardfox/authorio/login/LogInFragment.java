package com.kardfox.authorio.login;

import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kardfox.authorio.MainActivity;
import com.kardfox.authorio.R;
import com.kardfox.authorio.models.UserModel;
import com.kardfox.authorio.server_client.Server.Response;

import org.json.JSONObject;

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LogInFragment extends Fragment {
    private MainActivity activity;

    private EditText editEmail;
    private TextView textEmailError;

    private EditText editPassword;
    private TextView textPasswordError;

    public LogInFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        activity = (MainActivity) getActivity();

        editEmail = view.findViewById(R.id.editEmail);
        textEmailError = view.findViewById(R.id.textEmailError);

        editPassword = view.findViewById(R.id.editPassword);
        textPasswordError = view.findViewById(R.id.textPasswordError);
        editPassword.setText("");

        Button buttonLogIn = view.findViewById(R.id.buttonLogIn);
        Button buttonSwitchToSignUp = view.findViewById(R.id.buttonSwitchToSignUp);

        buttonLogIn.setOnClickListener(view1 -> {
            textEmailError.setText("");
            textPasswordError.setText("");

            String email = editEmail.getText().toString();
            String password = editPassword.getText().toString();

            if (password.length() < 8) {
                textPasswordError.setText(R.string.shortPassword);
                return;
            }

            if (email.length() > 0) {
                Pattern pattern = Pattern.compile("^([a-z0-9_-]+\\.)*[a-z0-9_-]+@[a-z0-9_-]+(\\.[a-z0-9_-]+)*\\.[a-z]{2,6}$");
                Matcher matcher = pattern.matcher(email);

                if (!matcher.matches()) {
                    textEmailError.setText(R.string.invalidEmail);
                    return;
                }
            } else {
                textEmailError.setText(R.string.emailIsEmpty);
                return;
            }

            JSONObject json = new JSONObject();
            try {
                json.put("email", email);
                json.put("device", Build.MODEL);
                json.put("password", password);

                Response response = activity.request(json, activity.URLs.login);

                if (response != null) {
                    switch (response.code) {
                        case 200:
                            UserModel user = activity.gson.fromJson(response.response, UserModel.class);
                            editEmail.setText("");
                            editPassword.setText("");
                            activity.saveUser(user);
                            break;
                        case 403:
                            textPasswordError.setText(R.string.wrongPassword);
                            break;
                        case 409:
                            textEmailError.setText(R.string.userNotFound);
                            break;
                        default:
                            textEmailError.setText(R.string.serverError);
                    }
                } else {
                    throw new Exception();
                }
             } catch (Exception exception) {
                textEmailError.setText(R.string.appError);
            }
        });

        buttonSwitchToSignUp.setOnClickListener(view12 -> {
            int[] anim = {R.anim.slide_right_enter, R.anim.slide_left_exit};
            activity.changeFragment(activity.fSignUp, anim);
        });

        return view;
    }
}