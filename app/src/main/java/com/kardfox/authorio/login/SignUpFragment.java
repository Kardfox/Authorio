package com.kardfox.authorio.login;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.kardfox.authorio.MainActivity;
import com.kardfox.authorio.R;
import com.kardfox.authorio.models.UserModel;
import com.kardfox.authorio.server_client.Client;
import com.kardfox.authorio.server_client.Server;

import org.json.JSONObject;

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SignUpFragment extends Fragment {
    private MainActivity activity;

    private TextView textNameError;

    private TextView textEmailError;
    private TextView textPasswordError;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        activity = (MainActivity) getActivity();

        EditText editName = view.findViewById(R.id.editName);
        textNameError = view.findViewById(R.id.textNameError);

        EditText editSurname = view.findViewById(R.id.editSurname);
        TextView textSurnameError = view.findViewById(R.id.textSurnameError);

        EditText editEmail = view.findViewById(R.id.editEmail2);
        textEmailError = view.findViewById(R.id.textEmailError2);

        EditText editPassword = view.findViewById(R.id.editPassword2);
        textPasswordError = view.findViewById(R.id.textPasswordError2);

        Button buttonSignUp = view.findViewById(R.id.buttonSignUp);
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textNameError.setText("");
                textSurnameError.setText("");
                textEmailError.setText("");
                textPasswordError.setText("");

                String name = editName.getText().toString();
                String surname = editSurname.getText().toString();
                String email = editEmail.getText().toString();
                String password = editPassword.getText().toString();

                if (name.isEmpty()) {
                    textNameError.setText(R.string.nameIsEmpty);
                    return;
                }

                if (surname.isEmpty()) {
                    textSurnameError.setText(R.string.surnameIsEmpty);
                    return;
                }

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
                    json.put("name", name);
                    json.put("surname", surname);
                    json.put("email", email);
                    json.put("password", password);

                    Server.Response response = signUp(json);

                    if (response != null)
                        switch (response.code) {
                            case 200:
                                json.put("name", null);
                                json.put("surname", null);
                                json.put("device", Build.MODEL);

                                login(json);
                                break;
                            case 403:
                                textEmailError.setText(R.string.emailIsExists);
                                break;
                            default:
                                textNameError.setText(R.string.serverError);
                        }

                } catch (Exception exception) {
                    textEmailError.setText(R.string.appError);
                }
            }
        });

        Button buttonSwitchToLogIn = view.findViewById(R.id.buttonSwitchToLogIn);
        buttonSwitchToLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int[] anim = {R.anim.slide_left_enter, R.anim.slide_right_exit};
                activity.changeFragment(activity.fLogIn, anim);
            }
        });

        return view;
    }

    private Server.Response signUp(JSONObject json) {
        Server.Response response = null;
        try {
            Client.Post post = new Client.Post(new URL(Server.URLs.signup), json);
            post.execute();

            response = post.get();

        } catch (Exception exception) {
            textNameError.setText(R.string.appError);
        }

        return response;
    }

    private void login(JSONObject json) {
        try {
            Client.Post post = new Client.Post(new URL(Server.URLs.login), json);
            post.execute();

            Server.Response response = post.get();

            if (response.code == 200) {
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();

                UserModel user = gson.fromJson(response.response, UserModel.class);

                activity.saveUser(user);
            } else {
                textEmailError.setText(R.string.serverError);
            }

        } catch (Exception exception) {
            textNameError.setText(R.string.appError);
        }
    }
}