package com.example.t.tplanner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import org.json.JSONException;
import org.json.JSONObject;

public class DBHandler {

    private DBHelper db;
    private SQLiteDatabase dbUpdate, read;
    private ContentValues cv;

    public DBHandler(Context c, String type) {
        db = new DBHelper(c);
        if (type.equals("w") || type.equals("write") || type.equals("writable")) {
            dbUpdate = db.getWritableDatabase();
        } else if (type.equals("r") || type.equals("read") || type.equals("readable")) {
            dbUpdate = db.getReadableDatabase();
        }
        cv = new ContentValues();
    }

    public void add(String key, String value) {

        cv.put(key, value);
        dbUpdate.insert(DBHelper.DB_TABLE, null, cv);

    }

    public void update(int id, String key, String value) {

        cv.put(key, value);
        dbUpdate.update(DBHelper.DB_TABLE, cv, "id=?", new String[]{id+""});

    }

    public JSONObject[] get(String date) {

        String query = "SELECT * FROM "+ DBHelper.DB_TABLE + " WHERE startDate=?";
        read = db.getReadableDatabase();
        String[] args = new String[]{date};
        Cursor cursor = read.rawQuery(query, args);
        JSONObject[] result = null;
        JSONObject value = null;

        if (cursor.moveToFirst()) {
            result = new JSONObject[size(date)];
            int i = 0;
            do {
                try {
                    value = new JSONObject();
                    value.put("id", cursor.getString(cursor.getColumnIndex("id")));
                    value.put("title", cursor.getString(cursor.getColumnIndex("title")));
                    value.put("description", cursor.getString(cursor.getColumnIndex("description")));
                    value.put("time", cursor.getString(cursor.getColumnIndex("startTime")));
                    result[i] = value;
                    i++;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        return result;

    }

    public JSONObject getId(int id) {

        String query = "SELECT * FROM " + DBHelper.DB_TABLE + " WHERE id=?";
        read = db.getReadableDatabase();
        String[] args = new String[]{id+""};
        Cursor cursor = read.rawQuery(query, args);

        JSONObject value = null;

        if (cursor.moveToFirst()) {
            value = new JSONObject();
            try {
                value.put("title", cursor.getString(cursor.getColumnIndex("title")));
                value.put("startDate", cursor.getString(cursor.getColumnIndex("startDate")));
                value.put("time", cursor.getString(cursor.getColumnIndex("startTime")));
                value.put("endDate", cursor.getString(cursor.getColumnIndex("endDate")));
                value.put("endTime", cursor.getString(cursor.getColumnIndex("endTime")));
                value.put("desc", cursor.getString(cursor.getColumnIndex("description")));
                value.put("contacts", cursor.getString(cursor.getColumnIndex("contacts")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return value;

    }

    public int size(String date) {

        String query = "SELECT * FROM "+ DBHelper.DB_TABLE + " WHERE startDate=?";
        read = db.getReadableDatabase();
        String[] args = new String[]{date};
        Cursor cursor = read.rawQuery(query, args);
        int count = cursor.getCount();
        cursor.close();
        return count;

    }

    public void deleteToday(String today) {

        // delete all the entries that start today
        dbUpdate.delete(DBHelper.DB_TABLE, "startDate=?", new String[]{today});

    }

    public void deleteId(int id) {
        dbUpdate.delete(DBHelper.DB_TABLE, "id=?", new String[]{id+""});
    }

    public void deleteAll() {
        read = db.getWritableDatabase();
        db.deleteAll(read);
    }

    public void close() {
        dbUpdate.close();
    }
}
