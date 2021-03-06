package com.example.android.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.inventoryapp.data.BookContract.BookEntry;

public class BookDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "book_store.db";
    public static final String SQL_CREATE_BOOKS_TABLE = "CREATE TABLE " + BookEntry.TABLE_NAME + " (" +
            BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            BookEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, " +
            BookEntry.COLUMN_AUTHOR_NAME + " TEXT , " +
            BookEntry.COLUMN_PRICE + " INTEGER NOT NULL DEFAULT 0, " +
            BookEntry.COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 0, " +
            BookEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, " +
            BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER + " TEXT " + ");";

    public BookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_BOOKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}