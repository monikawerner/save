package com.example.monika.mybookcatalog.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Database helper for KidsStore app. Manages database creation and version management.
 */
public class BookDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "books.db";
    public static final int DATABASE_VERSION = 1;
    private static final String SQL_DELETE_PRODUCTS_ENTRIES =
            "DROP TABLE IF EXISTS " + BookContract.BookEntry.TABLE_NAME;

    public BookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_BOOKS_TABLE = "CREATE TABLE " + BookContract.BookEntry.TABLE_NAME + " ("
                + BookContract.BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BookContract.BookEntry.COLUMN_TITLE + " TEXT NOT NULL, "
                + BookContract.BookEntry.COLUMN_AUTHOR + " TEXT NOT NULL, "
                + BookContract.BookEntry.COLUMN_IMAGE + " BLOB, "
                + BookContract.BookEntry.COLUMN_GENRE + " INTEGER DEFAULT 0, "
                + BookContract.BookEntry.COLUMN_PUBLISHING_HOUSE + " TEXT, "
                + BookContract.BookEntry.COLUMN_PUBLICATION_DATE + " INTEGER, "
                + BookContract.BookEntry.COLUMN_LOCALIZATION + " TEXT);";

        db.execSQL(SQL_CREATE_BOOKS_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_PRODUCTS_ENTRIES);
        onCreate(db);
    }
}


