package com.example.android.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class BookContract {

    // The content authority is a name for the entire content provider.
    public static final String CONTENT_AUTHORITY = "com.example.android.inventoryapp";
    // The base content uri that contains the scheme and the content authority.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    // The path for the books table.
    public static final String PATH_BOOKS = "books";

    public static final class BookEntry implements BaseColumns {

        // The content URI to access the book data in the provider.
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);
        // The MIME type of the CONTENT_URI for a list of books.
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + PATH_BOOKS;
        // The MIME type of the CONTENT_URI for a single book.
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + PATH_BOOKS;
        // Table name.
        public static final String TABLE_NAME = "books";
        // The _Id column.
        public static final String _ID = BaseColumns._ID;
        // The product_name column.
        public static final String COLUMN_PRODUCT_NAME = "product_name";
        // The author_name column.
        public static final String COLUMN_AUTHOR_NAME = "author_name";
        // The price column.
        public static final String COLUMN_PRICE = "price";
        // The quantity column.
        public static final String COLUMN_QUANTITY = "quantity";
        // The supplier_name column.
        public static final String COLUMN_SUPPLIER_NAME = "supplier_name";
        // The supplier_phone_number column.
        public static final String COLUMN_SUPPLIER_PHONE_NUMBER = "supplier_phone_number";
    }
}