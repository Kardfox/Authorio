package com.kardfox.authorio.write;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.imageview.ShapeableImageView;
import com.kardfox.authorio.MainActivity;
import com.kardfox.authorio.R;
import com.kardfox.authorio.models.UserModel;
import com.kardfox.authorio.server_client.Server.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class ChangeProfileFragment extends Fragment {
    MainActivity activity;

    ShapeableImageView authorPhoto;

    JSONObject changes = new JSONObject();

    ActivityResultLauncher<Intent> PickImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null && data.getData() != null) {
                        Uri selectedImageUri = data.getData();
                        Bitmap selectedImageBitmap;
                        try {
                            selectedImageBitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), selectedImageUri);
                            authorPhoto.setImageBitmap(selectedImageBitmap);

                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            selectedImageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            byte[] byteArray = stream.toByteArray();
                            changes.put("photo", Base64.encodeToString(byteArray, 0));
                            Log.d(MainActivity.LOG_TAG, ": ");
                        } catch (IOException | JSONException e) {
                            Log.e(MainActivity.LOG_TAG, e.getLocalizedMessage());
                        }
                    }
                }
            });

    public ChangeProfileFragment() {}
    public ChangeProfileFragment(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_profile, container, false);

        authorPhoto = view.findViewById(R.id.authorPhotoCP);
        byte[] imageBytes = Base64.decode(activity.GLOBAL_USER.photo, 0);
        authorPhoto.setImageBitmap(BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length));
        authorPhoto.setOnClickListener(_view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);

            PickImage.launch(intent);
        });

        EditText editName = view.findViewById(R.id.editNameCP);
        editName.setText(activity.GLOBAL_USER.name);

        EditText editSurname = view.findViewById(R.id.editSurnameCP);
        editSurname.setText(activity.GLOBAL_USER.surname);

        EditText editDescription = view.findViewById(R.id.editDescription);
        editDescription.setText(activity.GLOBAL_USER.description);

        Button changePassword = view.findViewById(R.id.buttonChangePassword);
        changePassword.setOnClickListener(_view -> {
            ChangePasswordFragment changePasswordFragment = new ChangePasswordFragment(activity);
            activity.changeFragment(changePasswordFragment, new int[] {R.anim.slide_right_enter, R.anim.slide_left_exit});
        });

        Button saveChanges = view.findViewById(R.id.buttonSaveChanges);
        activity.hideBar();
        saveChanges.setOnClickListener(_view -> {
            try {
                String newName = editName.getText().toString();
                String newSurname = editSurname.getText().toString();
                String newDescription = editDescription.getText().toString();

                changes.put("name", newName.equals(activity.GLOBAL_USER.name)? null : newName);
                changes.put("surname", newSurname.equals(activity.GLOBAL_USER.surname)? null : newSurname);
                changes.put("description", newDescription.equals(activity.GLOBAL_USER.description)? null : newDescription);
            } catch (JSONException e) {
                Log.e(MainActivity.LOG_TAG, e.getLocalizedMessage());
            }

            Response response = activity.request(changes, activity.URLs.change_user);
            if (response.code != 200) return;
            UserModel newUser = activity.gson.fromJson(response.response, UserModel.class);
            activity.changeUser(newUser);
            activity.changeFragment(activity.fWrite, new int[] {R.anim.slide_left_enter, R.anim.slide_right_exit});
            activity.showBar();
        });

        return view;
    }
}