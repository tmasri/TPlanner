package com.example.t.tplanner;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 2;
    public static final String DB_NAME = "Events";
    public static final String DB_TABLE = "schedule";
    public static final int DB_VERSION = 1;
    private static final String CREATE_TABLE = "CREATE TABLE " + DB_TABLE +
            " (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
            "title TEXT NOT NULL," +
            "description TEXT NOT NULL," +
            "startDate TEXT," +
            "endDate TEXT," +
            "startTime TEXT," +
            "endTime TEXT," +
            "contacts TEXT NOT NULL);";

    public DBHelper(Context c) {
        super(c, DB_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void deleteAll(SQLiteDatabase db) {
        db.delete(DB_TABLE, null, null);
    }
}
