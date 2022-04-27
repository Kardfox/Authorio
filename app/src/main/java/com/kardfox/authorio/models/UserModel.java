package com.kardfox.authorio.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import com.kardfox.authorio.bd.UserDB;

import java.util.Locale;

public class UserModel {
    public String id;
    public String name;
    public String surname;
    public String description;
    public String email;
    public String photo;
    public String token;

    public UserModel(String id, String name, String surname, String description, String email, String photo, String token) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.description = description;
        this.email = email;
        this.photo = photo;
        this.token = token;
    }

    public static UserModel get(SQLiteDatabase db) {
        Cursor c = db.rawQuery("SELECT * FROM user", null);
        c.moveToFirst();

        UserModel user = new UserModel(c.getString(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5), c.getString(6));
        c.close();

        return user;
    }

    public boolean insert(SQLiteDatabase db) {
        ContentValues values = new ContentValues();

        values.put(UserDB.colId, this.id);
        values.put(UserDB.colName, this.name);
        values.put(UserDB.colSurname, this.surname);
        values.put(UserDB.colDescription, this.description);
        values.put(UserDB.colEmail, this.email);
        values.put(UserDB.colPhoto, this.photo);
        values.put(UserDB.colToken, this.token);

        long result = db.insert(UserDB.tableName, null, values);
        return result != -1;
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(),"UserDB(id=%d, name=%s, surname=%s, email=%s, token=%s)", id, name, surname, email, token);
    }
}