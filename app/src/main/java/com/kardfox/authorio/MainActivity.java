package com.kardfox.authorio;



import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kardfox.authorio.bd.UserDB;
import com.kardfox.authorio.login.LogInFragment;
import com.kardfox.authorio.login.SignUpFragment;
import com.kardfox.authorio.main.MainFragment;
import com.kardfox.authorio.models.UserModel;
import com.kardfox.authorio.search.SearchFragment;
import com.kardfox.authorio.server_client.Client;
import com.kardfox.authorio.server_client.Server;
import com.kardfox.authorio.write.WriteFragment;

import org.json.JSONObject;

import java.net.URL;

public class MainActivity extends AppCompatActivity {
    public static final String LOG_TAG = "Authorio";

    public Gson gson = new GsonBuilder().create();

    public UserModel GLOBAL_USER = null;

    private SQLiteDatabase db;

    private FragmentManager fManager;

    public LogInFragment fLogIn;
    public SignUpFragment fSignUp;

    public MainFragment fMain;
    public SearchFragment fSearch;
    public WriteFragment fWrite;

    private ImageButton toMain;
    private ImageButton toSearch;
    private ImageButton toWrite;

    public FrameLayout bottomNavigation;

    public int selected;

    public Server.URLs URLs = new Server.URLs();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        if (request(new JSONObject(), URLs.main) == null) return;

        UserDB readDB = new UserDB("User.db", getApplicationContext());
        db = readDB.getReadableDatabase();

        GLOBAL_USER = UserModel.get(db);

        fManager = getSupportFragmentManager();

        fLogIn = new LogInFragment();
        fSignUp = new SignUpFragment();

        if (GLOBAL_USER == null) {
            setContentView(R.layout.activity_main_not_login);

            FragmentTransaction fTransaction = fManager.beginTransaction();
            fTransaction.add(R.id.fragmentContainer, fLogIn, "switch");
            fTransaction.commit();
            return;
        }

        initMain();
    }

    public void initMain() {
        setContentView(R.layout.activity_main);

        bottomNavigation = findViewById(R.id.bottomNavigation);

        fMain = null;

        fMain = new MainFragment();
        fSearch = new SearchFragment(this);
        fWrite = new WriteFragment(this);

        URLs.setUrls(GLOBAL_USER.token);

        selected = 0;

        toMain = findViewById(R.id.buttonToMain);
        toSearch = findViewById(R.id.buttonToSearch);
        toWrite = findViewById(R.id.buttonToWrite);

        FragmentTransaction fTransaction = fManager.beginTransaction();
        fTransaction.add(R.id.fragmentContainer, fMain, "switch");
        fTransaction.commit();

        toMain.setOnClickListener(view -> switchTo(Section.MAIN));

        toSearch.setOnClickListener(view -> switchTo(Section.SEARCH));

        toWrite.setOnClickListener(view -> switchTo(Section.WRITE));
    }

    public void changeFragment(Fragment newFragment, int[] enter_exit) {
        FragmentTransaction fTransaction = fManager.beginTransaction();

        fTransaction.setCustomAnimations(enter_exit[0], enter_exit[1]);
        fTransaction.replace(R.id.fragmentContainer, newFragment, "switch");
        fTransaction.addToBackStack(null).commit();
    }

    public void setSelected(Section section) {
        selected = section.ordinal();
        int[] drawables = new int[] {R.drawable.main_draw_sel, R.drawable.search_draw_sel, R.drawable.write_book_sel};
        ImageButton[] buttons = new ImageButton[] {toMain, toSearch, toWrite};
        disable();
        buttons[selected].setBackground(AppCompatResources.getDrawable(this, drawables[selected]));
    }

    public void switchTo(Section section) {
        Fragment[] fragments = new Fragment[] {fMain, fSearch, fWrite};

        int toSection = section.ordinal();

        if (selected > toSection) {
            changeFragment(fragments[toSection], new int[] {R.anim.slide_left_enter, R.anim.slide_right_exit});
        } else if (selected < toSection) {
            changeFragment(fragments[toSection], new int[] {R.anim.slide_right_enter, R.anim.slide_left_exit});
        }
        setSelected(section);
    }

    public void saveUser(UserModel user) {
        GLOBAL_USER = user;
        GLOBAL_USER.insert(db);

        Animation animSlideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        ConstraintLayout root = findViewById(R.id.fragmentContainer);
        root.startAnimation(animSlideDown);

        initMain();
    }

    public void logOut() {
        UserModel.delete(db);
        request(new JSONObject(), URLs.logout);
        setContentView(R.layout.activity_main_not_login);
        Animation animSlideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
        ConstraintLayout root = findViewById(R.id.fragmentContainer);
        root.startAnimation(animSlideUp);
        changeFragment(fLogIn, new int[] {R.anim.slide_up, R.anim.slide_down});
    }

    public void changeUser(UserModel newUser) {
        UserModel.delete(db);
        newUser.insert(db);
        GLOBAL_USER = newUser;
    }

    private void disable() {
        toMain.setBackground(AppCompatResources.getDrawable(this, R.drawable.main_draw));
        toSearch.setBackground(AppCompatResources.getDrawable(this, R.drawable.search_draw));
        toWrite.setBackground(AppCompatResources.getDrawable(this, R.drawable.write_book));
    }

    public enum Section {
        MAIN(0),
        SEARCH(1),
        WRITE(2);

        Section(int section) {}
    }

    public Server.Response request(JSONObject json, String strUrl) {
        Server.Response response = null;

        try {
            URL url = new URL(strUrl);
            Client.Post post = new Client.Post(url, json);
            post.execute();

            response = post.get();
        } catch (Exception exception) {
            Log.e(MainActivity.LOG_TAG, exception.getLocalizedMessage());
        }

        if (response != null) {
            if (response.code == 500) {
                Toast.makeText(this, "Server error", Toast.LENGTH_LONG).show();
                setNotConnection();
            } else if (response.code == 404) {
                Toast.makeText(this, "Authentication error", Toast.LENGTH_LONG).show();
                logOut();
            }
        }
        return response;
    }

    public int getStateBar() {
        return bottomNavigation.getVisibility();
    }

    public void hideBar() {
        if (getStateBar() == View.VISIBLE) {
            Animation animSlideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
            bottomNavigation.startAnimation(animSlideDown);
            bottomNavigation.setVisibility(View.INVISIBLE);
        }
    }

    public void showBar() {
        if (getStateBar() == View.INVISIBLE) {
            bottomNavigation.setVisibility(View.VISIBLE);
            Animation animSlideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
            bottomNavigation.startAnimation(animSlideUp);
        }
    }

    private void setNotConnection() {
        setContentView(R.layout.not_connection);
    }
}