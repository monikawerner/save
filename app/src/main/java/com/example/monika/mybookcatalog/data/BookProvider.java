package com.example.monika.mybookcatalog.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import static android.content.UriMatcher.NO_MATCH;

public class BookProvider extends ContentProvider {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = BookProvider.class.getSimpleName();
    /**
     * URI matcher code for the content URI for the product table
     */
    private static final int BOOKS = 100;
    /**
     * URI matcher code for the content URI for a single product in the products table
     */
    private static final int BOOKS_ID = 101;
    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(NO_MATCH);

    static {

        /** This URI is used to provide access to MULTIPLE rows of the table. */
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS, BOOKS);
        /** This URI is used to provide access to ONE single row of the table. */
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS + "/#", BOOKS_ID);
    }

    /**
     * Database helper object
     */
    private BookDbHelper mDbHelper;

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {

        mDbHelper = new BookDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                cursor = database.query(BookContract.BookEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case BOOKS_ID:
                selection = BookContract.BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(BookContract.BookEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return BookContract.BookEntry.CONTENT_LIST_TYPE;
            case BOOKS_ID:
                return BookContract.BookEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return insertProduct(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a product into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertProduct(Uri uri, ContentValues values) {
        /** Prevent adding invalid book's data to database */
        validateTitle(values);
        validateAuthor(values);
        validatePublicationDate(values);
        validateGenre(values);

        /** Get writable database */
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        /** Insert the new product with the given values */
        long id = database.insert(BookContract.BookEntry.TABLE_NAME, null, values);
        /** If the ID is -1, then the insertion failed. Log an error and return null. */
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);

        /** Return the new URI with the ID (of the newly inserted row) appended at the end */
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        /** Get writable database */
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        /** Track the number of rows that were deleted */
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                /** Delete all rows that match the selection and selection args */
                rowsDeleted = database.delete(BookContract.BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BOOKS_ID:
                /** Delete a single row given by the ID in the URI */
                selection = BookContract.BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(BookContract.BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        /** If 1 or more rows were deleted, then notify all listeners that the data at the given URI has changed */
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;

    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return updateProduct(uri, values, selection, selectionArgs);
            case BOOKS_ID:
                selection = BookContract.BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update products in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more products).
     * Return the number of rows that were successfully updated.
     */
    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.size() == 0) {
            return 0;
        }
        if (values.containsKey(BookContract.BookEntry.COLUMN_TITLE)) {
            validateTitle(values);
        }

        if (values.containsKey(BookContract.BookEntry.COLUMN_AUTHOR)) {
            validateAuthor(values);
        }

        if (values.containsKey(BookContract.BookEntry.COLUMN_GENRE)) {
            validateGenre(values);
        }

        if (values.containsKey(BookContract.BookEntry.COLUMN_PUBLICATION_DATE)) {
            validatePublicationDate(values);
        }


        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        /** Perform the update on the database and get the number of rows affected */
        int rowsUpdated = database.update(BookContract.BookEntry.TABLE_NAME, values, selection, selectionArgs);
        /**  If 1 or more rows were updated, then notify all listeners that the data at the given URI has changed */
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }


    /**
     * Helper methods to validate data put by a user in editor - used when insert and update
     **/

    private void validateTitle(ContentValues values) {
        String title = values.getAsString(BookContract.BookEntry.COLUMN_TITLE);
        if (TextUtils.isEmpty(title)) {
            throw new IllegalArgumentException("Book requires a title.");
        }
    }

    private void validateAuthor(ContentValues values) {
        String author = values.getAsString(BookContract.BookEntry.COLUMN_AUTHOR);
        if (TextUtils.isEmpty(author)) {
            throw new IllegalArgumentException("Book requires an author.");
        }
    }

    private void validateGenre(ContentValues values) {
        Integer genre = values.getAsInteger(BookContract.BookEntry.COLUMN_GENRE);
        if (genre != null && !BookContract.BookEntry.isValidGenre(genre)) {
            throw new IllegalArgumentException("Book requires valid genre");
        }
    }

    private void validatePublicationDate(ContentValues values) {
        Integer date = values.getAsInteger(BookContract.BookEntry.COLUMN_PUBLICATION_DATE);
        if (date != null && date < 1900) {
            throw new IllegalArgumentException("Please enter validate date");
        }
    }
}
