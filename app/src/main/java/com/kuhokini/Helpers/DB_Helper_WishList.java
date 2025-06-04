package com.kuhokini.Helpers;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import com.kuhokini.Models.WishListModel;

import java.util.ArrayList;

public class DB_Helper_WishList extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "wish_list.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_SEARCH_HISTORY = "wish_list";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_HOTEL_ID = "hotel_id";
    private static final String COLUMN_PROVIDED_NAME = "provided_name";

    public DB_Helper_WishList(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_SEARCH_HISTORY + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_HOTEL_ID + " TEXT, " +
                COLUMN_PROVIDED_NAME + " TEXT " +
                ")";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrades here
    }

    public void addWishList(WishListModel wishListModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_HOTEL_ID, wishListModel.getId());
        values.put(COLUMN_PROVIDED_NAME, wishListModel.getName());

        db.insert(TABLE_SEARCH_HISTORY, null, values);
        db.close();
    }

    public ArrayList<WishListModel> getAllWishList() {
        ArrayList<WishListModel> wishListArray = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SEARCH_HISTORY, null);
        if (cursor.moveToFirst()) {
            do {

                WishListModel wishListModel = new WishListModel();

                @SuppressLint("Range") String hotelId = cursor.getString(cursor.getColumnIndex(COLUMN_HOTEL_ID));
                @SuppressLint("Range") String providedRoom = cursor.getString(cursor.getColumnIndex(COLUMN_PROVIDED_NAME));

                wishListModel.setId(hotelId);
                wishListModel.setName(providedRoom);
                wishListArray.add(wishListModel);


            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return wishListArray;
    }

    public void deleteSearchQuery(String providedRoom) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_SEARCH_HISTORY + " WHERE " + COLUMN_PROVIDED_NAME + "='" + providedRoom + "'");
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

    public boolean isHotelInWishlist(String hotelId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT 1 FROM " + TABLE_SEARCH_HISTORY + " WHERE " + COLUMN_HOTEL_ID + " = ? LIMIT 1",
                new String[]{hotelId}
        );
        boolean exists = (cursor.getCount() > 0); // Check if at least one row exists
        cursor.close();
        db.close();
        return exists;
    }
}
