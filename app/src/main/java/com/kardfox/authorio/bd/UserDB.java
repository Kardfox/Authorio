package com.kardfox.authorio.bd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;

import java.util.Locale;

import com.google.gson.*;


public class UserDB extends SQLiteOpenHelper {
    public static final String tableName = "user";

    public static final String colId = "id";
    public static final String colName = "name";
    public static final String colSurname = "surname";
    public static final String colDescription = "description";
    public static final String colEmail = "email";
    public static final String colPhoto = "photo";
    public static final String colToken = "token";

    public static final String CREATE_TABLE = String.format(
            "CREATE TABLE IF NOT EXISTS %s (%s);", tableName,
            String.format("%s INT NOT NULL, %s TEXT NOT NULL, %s TEXT NOT NULL, %s TEXT NOT NULL, %s TEXT NOT NULL, %s TEXT NOT NULL, %s TEXT NOT NULL",
                    colId,
                    colName,
                    colSurname,
                    colDescription,
                    colEmail,
                    colPhoto,
                    colToken)
    );

    private Context context;

    public UserDB(String dbName, Context context) {
        super(context, dbName, null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(String.format("DELETE TABLE IF EXISTS %s;", tableName));
        onCreate(db);
    }
}
