package com.example.monika.mybookcatalog.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class BookContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.mybookcatalog";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_BOOKS = "books";

    private BookContract() {}

    public static final class BookEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);

        /**
         * The MIME type of the CONTENT_URI for a list of products.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        /**
         * The MIME type of the CONTENT_URI for a single product.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        public static final String _ID = BaseColumns._ID;
        public static final String TABLE_NAME = "books";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_GENRE = "genre";
        public static final String COLUMN_PUBLISHING_HOUSE = "publishing house";
        public static final String COLUMN_PUBLICATION_DATE = "publication date";
        public static final String COLUMN_LOCALIZATION = "localization";


        /**
         * Possible values for the genre of the book.
         */
        public static final int GENRE_UNKNOWN = 0;
        public static final int GENRE_THRILLER = 1;
        public static final int GENRE_GUIDE = 2;
        public static final int GENRE_NOVEL = 3;
        public static final int GENRE_BIOGRAPHY = 4;
        public static final int GENRE_FOR_KIDS = 5;
        public static final int GENRE_FANTASY = 6;
        public static final int GENRE_ROMANCE = 7;
        public static final int GENRE_SCIENTIFIC = 8;

        /**
         * Returns whether or not the given genre is unknown, thriller, guide, novel, biography,
         * for_kids, fantasy, romance, scientific
         */
        public static boolean isValidGenre(int genre) {
            if (genre == GENRE_UNKNOWN || genre == GENRE_THRILLER || genre == GENRE_GUIDE
                    || genre == GENRE_NOVEL || genre == GENRE_BIOGRAPHY || genre == GENRE_FOR_KIDS
                    || genre ==GENRE_FANTASY || genre == GENRE_ROMANCE || genre == GENRE_SCIENTIFIC) {
                return true;
            }
            return false;
        }

    }
}