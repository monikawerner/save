package com.example.monika.mybookcatalog;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

public class EditorActivity extends AppCompatActivity {


    /**
     * EditText fields to enter information about book
     */
    private EditText mTitleEditText;
    private EditText mAuthorEditText;
    private EditText mPublishingHouse;
    private EditText mPublicationDate;
    private EditText mBookLocalization;

    /**
     * Spinner to set the book genre
     */
    private Spinner mGenreSpinner;

    private ImageView mImage;

    /**
     * * Genre of the book. The possible values are:
     * 0 for unknown genre, 1 for thriller, 2 for guide, 3 for novel,
     * 4 for biography, 5 for kids, 6 for fantasy, 7 for romance, 8 for scientific.
     */
    private int mGenre = BookEntry.GENRE_UNKNOWN;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);


        // Find all relevant views that we will need to read user input from
        mTitleEditText = findViewById(R.id.title_edit_text);
        mAuthorEditText = findViewById(R.id.author_edit_text);
        mGenreSpinner = findViewById(R.id.spinner_genre);
        mPublishingHouse = findViewById(R.id.publishing_house_edit_text);
        mPublicationDate = findViewById(R.id.publication_date_edit_text);
        mBookLocalization = findViewById(R.id.location_edit_text);

        setupSpinner();
    }

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genreSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_genre_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genreSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mGenreSpinner.setAdapter(genreSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mGenreSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.genre_thriller))) {
                        mGenre = BookEntry.GENRE_THRILLER; // Thriller
                    } else if (selection.equals(getString(R.string.genre_guide))) {
                        mGenre = BookEntry.GENRE_GUIDE; // Guide
                    } else if (selection.equals(getString(R.string.genre_novel))) {
                        mGenre = BookEntry.GENRE_NOVEL; // Novel
                    } else if (selection.equals(getString(R.string.genre_biography))) {
                        mGenre = BookEntry.GENRE_BIOGRAPHY; // Biography
                    } else if (selection.equals(getString(R.string.genre_for_kids))) {
                        mGenre = BookEntry.GENRE_FOR_KIDS; // For kids
                    } else if (selection.equals(getString(R.string.genre_fantasy))) {
                        mGenre = BookEntry.GENRE_FANTASY; // Fantasy
                    } else if (selection.equals(getString(R.string.genre_romance))) {
                        mGenre = BookEntry.GENRE_ROMANCE; // Romance
                    } else if (selection.equals(getString(R.string.genre_scientific))) {
                        mGenre = BookEntry.GENRE_SCIENTIFIC; // Scientific
                    } else {
                        mGenre = BookEntry.GENRE_UNKNOWN; // Unknown
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGenre = BookEntry.GENRE_UNKNOWN; // Unknown
            }
        });
    }

    private void insertBook() {

        String titleString = mTitleEditText.getText().toString().trim();
        String authorString = mAuthorEditText.getText().toString().trim();
        String publishingHouseString = mPublishingHouse.getText().toString().trim();
        String publicationDateString = mPublicationDate.getText().toString().trim();
        int publicationDate = Integer.parseInt(publicationDateString);
        String locationString = mBookLocalization.getText().toString().trim();

        BookDbHelper mDbHelper = new BookDbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_TITLE, titleString);
        values.put(BookEntry.COLUMN_AUTHOR, authorString);
        values.put(BookEntry.COLUMN_GENRE, mGenre);
        values.put(BookEntry.COLUMN_PUBLISHING_HOUSE, publishingHouseString);
        values.put(BookEntry.COLUMN_PUBLICATION_DATE, publicationDate);
        values.put(BookEntry.COLUMN_LOCALIZATION, locationString);


        long newRowId = db.insert(BookEntry.TABLE_NAME, null, values);

        if (newRowId == -1) {
            Toast.makeText(this, "Error with saving", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Book saved with row ID " + newRowId, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save book into database
                insertBook();
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Do nothing for now
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

