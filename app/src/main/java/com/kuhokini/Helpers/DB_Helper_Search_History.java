package com.kuhokini.Helpers;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import java.util.ArrayList;

public class DB_Helper_Search_History extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "search_history.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_SEARCH_HISTORY = "search_history";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_SEARCH_QUERY = "search_query";
    private static final String COLUMN_TIMESTAMP = "timestamp";

    public DB_Helper_Search_History(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_SEARCH_HISTORY + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_SEARCH_QUERY + " TEXT, " +
                COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP" +
                ")";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrades here
    }

    public void addSearchQuery(String recentSearchModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Check if the search query already exists
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_SEARCH_HISTORY + " WHERE " + COLUMN_SEARCH_QUERY + " = ?",
                new String[]{recentSearchModel}
        );

        if (cursor.getCount() > 0) {
            // If the query exists, do not insert it again
            cursor.close();
            db.close();
            return;
        }

        cursor.close();

        //If does not exist insert the new search query
        ContentValues values = new ContentValues();
        values.put(COLUMN_SEARCH_QUERY, recentSearchModel);

        db.insert(TABLE_SEARCH_HISTORY, null, values);
        db.close();
    }

    public ArrayList<String> getAllSearchQueries() {
        ArrayList<String> searchQueries = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SEARCH_HISTORY, null);
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String searchQuery = cursor.getString(cursor.getColumnIndex(COLUMN_SEARCH_QUERY));
                searchQueries.add(searchQuery);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return searchQueries;
    }

    public void deleteSearchQuery(String searchQuery) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_SEARCH_HISTORY + " WHERE " + COLUMN_SEARCH_QUERY + "='" + searchQuery + "'");
        db.close();
    }

    public void resetDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Drop existing tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SEARCH_HISTORY);
        // Recreate tables
        onCreate(db);
        db.close();
    }
}
