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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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

    public UserModel GLOBAL_USER;

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

    public int selected;

    public Server.URLs URLs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        fManager = getSupportFragmentManager();

        fLogIn = new LogInFragment();
        fSignUp = new SignUpFragment();

        fMain = new MainFragment();
        fSearch = new SearchFragment(this);
        fWrite = new WriteFragment(this);

        UserDB readDB = new UserDB("User.db", getApplicationContext());
        db = readDB.getReadableDatabase();

        boolean login = checkLogin(db);

        if (!login) {
            setContentView(R.layout.activity_main_not_login);

            FragmentTransaction fTransaction = fManager.beginTransaction();
            fTransaction.add(R.id.accountContainer, fLogIn, "switch");
            fTransaction.commit();
            return;
        }

        initMain();
    }

    public void initMain() {
        setContentView(R.layout.activity_main);

        GLOBAL_USER = UserModel.get(db);

        URLs = new Server.URLs(GLOBAL_USER.token);

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

        Log.i(MainActivity.LOG_TAG, "Switching!");

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

    private boolean checkLogin(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("SELECT * FROM user;", null);
        int count = cursor.getCount();
        cursor.close();

        return count > 0;
    }

    public void saveUser(UserModel user) {
        GLOBAL_USER = user;
        GLOBAL_USER.insert(db);

        Animation animSlideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        ConstraintLayout root = findViewById(R.id.accountContainer);
        root.startAnimation(animSlideDown);

        initMain();
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

    public String request(JSONObject json, String strUrl) {
        Server.Response response = null;

        try {
            URL url = new URL(strUrl);
            Client.Post post = new Client.Post(url, json);
            post.execute();

            response = post.get();
        } catch (Exception exception) {
            Log.e(MainActivity.LOG_TAG, exception.getMessage());
        }

        if (response != null && response.code == 200) {
            return response.response;
        } else if (response != null && response.code == 500) {
            Toast.makeText(this, "Server error", Toast.LENGTH_LONG).show();
            setNotConnection();
        }
        return null;
    }

    private void setNotConnection() {
        setContentView(R.layout.not_connection);
    }
}