package com.kardfox.authorio.login;

import android.content.res.Resources;
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
import com.kardfox.authorio.server_client.Client;
import com.kardfox.authorio.server_client.Server;

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

        Button buttonLogIn = view.findViewById(R.id.buttonLogIn);
        Button buttonSwitchToSignUp = view.findViewById(R.id.buttonSwitchToSignUp);

        Resources resources = getResources();

        buttonLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

                    Server.Response response = login(json);

                    if (response != null) {
                        switch (response.code) {
                            case 200:
                                GsonBuilder builder = new GsonBuilder();
                                Gson gson = builder.create();

                                UserModel user = gson.fromJson(response.response, UserModel.class);
                                activity.saveUser(user);
                                break;
                            case 403:
                                textPasswordError.setText(R.string.wrongPassword);
                                break;
                            case 404:
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
            }
        });

        buttonSwitchToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int[] anim = {R.anim.slide_right_enter, R.anim.slide_left_exit};
                activity.changeFragment(R.id.accountContainer, activity.fSignUp, "signUp", anim);
            }
        });

        return view;
    }

    private Server.Response login(JSONObject json) {
        Server.Response response = null;
        try {
            Client.Post post = new Client.Post(new URL(Server.URLs.login), json);
            post.execute();

            response = post.get();
        } catch (Exception exception) {
            textEmailError.setText(R.string.appError);
        }

        return response;
    }
}