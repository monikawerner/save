package com.example.monika.mybookcatalog;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.monika.mybookcatalog.data.BookContract;


/**
 * Displays list of products that were entered and stored in the app.
 */

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final int PRODUCT_LOADER = 0;
    BookCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        /** Setup FAB to open EditorActivity */
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });


        /**Find the ListView which will be populated with the product data */
        ListView listView = findViewById(R.id.list);

        /** Find and set empty view on the ListView, so that it only shows when the list has 0 items. */
        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);

        mCursorAdapter = new BookCursorAdapter(this, null);
        listView.setAdapter(mCursorAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                Uri currentProductUri = ContentUris.withAppendedId(BookContract.BookEntry.CONTENT_URI, id);
                intent.setData(currentProductUri);
                startActivity(intent);

            }
        });
        getLoaderManager().initLoader(PRODUCT_LOADER, null, this);

    }


    private void insertBook() {

        ContentValues values = new ContentValues();
        values.put(BookContract.BookEntry.COLUMN_TITLE, "Van Gogh Życie");
        values.put(BookContract.BookEntry.COLUMN_AUTHOR, "Steven Naifeh, Gregory White Smith");
        values.put(BookContract.BookEntry.COLUMN_GENRE, BookContract.BookEntry.GENRE_BIOGRAPHY);
        values.put(BookContract.BookEntry.COLUMN_PUBLISHING_HOUSE, "Świat Książki");
        values.put(BookContract.BookEntry.COLUMN_PUBLICATION_DATE, 2017);
        values.put(BookContract.BookEntry.COLUMN_LOCALIZATION, "dom");
        Uri newUri = getContentResolver().insert(BookContract.BookEntry.CONTENT_URI, values);

    }

    /**
     * Helper method to delete all products in the database.
     */
    private void deleteAllProducts() {
        int rowsDeleted = getContentResolver().delete(BookContract.BookEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from products database");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            /** Respond to a click on the "Insert dummy product" menu option */
            case R.id.action_insert_dummy_data:
                insertBook();
                Toast.makeText(this, "Book inserted", Toast.LENGTH_SHORT).show();
                return true;
            /** Respond to a click on the "Delete" menu option */
            case R.id.action_delete_all_entries:
                showDeleteConfirmationDialog();
                Toast.makeText(this, "all books deleted", Toast.LENGTH_SHORT).show();
                return true;

            /** Respond to a click on the "Add a new product" menu option */
            case R.id.action_add:
                Intent intentEditProduct = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intentEditProduct);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle args) {
        String[] projection = {
                BookContract.BookEntry._ID,
                BookContract.BookEntry.COLUMN_TITLE,
                BookContract.BookEntry.COLUMN_AUTHOR,
                BookContract.BookEntry.COLUMN_PUBLISHING_HOUSE,
                BookContract.BookEntry.COLUMN_PUBLICATION_DATE,
                BookContract.BookEntry.COLUMN_LOCALIZATION};

        return new CursorLoader(this, BookContract.BookEntry.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);

    }
    /**
     * Prompt the user to confirm that they want to delete this book.
     */
    private void showDeleteConfirmationDialog() {

        /** Create an AlertDialog.Builder and set the message, and click listeners for the positive
         * and negative buttons on the dialog */
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_all_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteAllProducts();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        /** Create and show the AlertDialog */
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}


}

