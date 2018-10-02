package com.example.monika.mybookcatalog;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.monika.mybookcatalog.data.BookContract;

public class BookCursorAdapter extends CursorAdapter {


    /**
     * Constructs a new ProductCursorAdapter.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        RecyclerView.ViewHolder holder = new RecyclerView.ViewHolder(view);
        view.setTag(holder);
        return view;
    }


    /**
     * This method binds the product data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name of the current product can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {
        RecyclerView.ViewHolder holder = (RecyclerView.ViewHolder) view.getTag();

        String title = cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_TITLE));
        String author = cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_AUTHOR));
        String publishingHouse = cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PUBLISHING_HOUSE));
        final int publicationDateInt = cursor.getInt(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PUBLICATION_DATE));
        String publicationDate = String.valueOf(publicationDateInt);
        String localization = cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_LOCALIZATION));

        holder.titleView.setText(title);
        holder.authorView.setText(author);
        holder.publishingHouseView.setText(publishingHouse);
        holder.publicationDateView.setText(publicationDate);
        holder.localizationView.setText(localization);


        final String id = String.valueOf(cursor.getInt(cursor.getColumnIndex(BookContract.BookEntry._ID)));

    }
        private static class ViewHolder {
            private TextView titleView;
            private TextView authorView;
            private TextView publishingHouseView;
            private TextView publicationDateView;
            private TextView localizationView;

            private ViewHolder(View view) {
                titleView = view.findViewById(R.id.title);
                authorView = view.findViewById(R.id.author);
                publishingHouseView = view.findViewById(R.id.publishing_house);
                publicationDateView = view.findViewById(R.id.publication_date);
                localizationView = view.findViewById(R.id.localization);
            }
        }

    }

