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

import com.kardfox.authorio.bd.UserDB;
import com.kardfox.authorio.login.LogInFragment;
import com.kardfox.authorio.login.SignUpFragment;
import com.kardfox.authorio.main.MainFragment;
import com.kardfox.authorio.models.UserModel;
import com.kardfox.authorio.search.SearchFragment;
import com.kardfox.authorio.write.WriteFragment;

public class MainActivity extends AppCompatActivity {
    public static final String LOG_TAG = "Authorio";
    public final String LOG_TAG_MAIN = "Authorio";

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        fManager = getSupportFragmentManager();

        fLogIn = new LogInFragment();
        fSignUp = new SignUpFragment();

        fMain = new MainFragment();
        fSearch = new SearchFragment();
        fWrite = new WriteFragment();

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

        selected = 0;

        toMain = findViewById(R.id.buttonToMain);
        toSearch = findViewById(R.id.buttonToSearch);
        toWrite = findViewById(R.id.buttonToWrite);

        FragmentTransaction fTransaction = fManager.beginTransaction();
        fTransaction.add(R.id.fragmentContainer, fMain, "switch");
        fTransaction.commit();

        toMain.setOnClickListener(view -> {
            switchTo(Section.MAIN);
        });

        toSearch.setOnClickListener(view -> {
            switchTo(Section.SEARCH);
        });

        toWrite.setOnClickListener(view -> {
            switchTo(Section.WRITE);
        });
    }

    public void changeFragment(Fragment newFragment, int[] enter_exit) {
        FragmentTransaction fTransaction = fManager.beginTransaction();

        Log.i(this.LOG_TAG_MAIN, "Switching!");

        fTransaction.setCustomAnimations(enter_exit[0], enter_exit[1]);
        fTransaction.replace(R.id.fragmentContainer, newFragment, "switch");
        fTransaction.addToBackStack(null).commit();
    }

    public void switchTo(Section section) {
        int[] drawables = new int[] {R.drawable.main_draw_sel, R.drawable.search_draw_sel, R.drawable.write_book_sel};
        ImageButton[] buttons = new ImageButton[] {toMain, toSearch, toWrite};
        Fragment[] fragments = new Fragment[] {fMain, fSearch, fWrite};

        int toSection = section.ordinal();

        buttons[toSection].setBackground(AppCompatResources.getDrawable(this, drawables[toSection]));

        if (selected > toSection) {
            changeFragment(fragments[toSection], new int[] {R.anim.slide_left_enter, R.anim.slide_right_exit});
        } else if (selected < toSection) {
            changeFragment(fragments[toSection], new int[] {R.anim.slide_right_enter, R.anim.slide_left_exit});
        }

        disableNotSelected();

        selected = toSection;
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

    private void disableNotSelected() {
        switch (selected) {
            case 0:
                toMain.setBackground(AppCompatResources.getDrawable(this, R.drawable.main_draw)); break;
            case 1:
                toSearch.setBackground(AppCompatResources.getDrawable(this, R.drawable.search_draw)); break;
            case 2:
                toWrite.setBackground(AppCompatResources.getDrawable(this, R.drawable.write_book));
        }
    }

    public enum Section {
        MAIN(0),
        SEARCH(1),
        WRITE(2);

        Section(int section) {}
    }
}