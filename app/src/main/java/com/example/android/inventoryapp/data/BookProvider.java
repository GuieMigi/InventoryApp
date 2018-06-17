package com.example.android.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.android.inventoryapp.data.BookContract.BookEntry;

public class BookProvider extends ContentProvider {

    // URI matcher code for the content URI for the books table.
    private static final int BOOKS = 100;
    // URI matcher code for the content URI for a single book in the books table.
    private static final int BOOK_ID = 101;
    // UriMatcher object to match a content URI to a corresponding code.
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS, BOOKS);
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS + "/#", BOOK_ID);
    }

    private BookDbHelper dbHelper;
    private Context mContext;

    @Override
    public boolean onCreate() {
        // Initialize a context variable to store the context.
        mContext = getContext();
        // Initialize a BookDbHelper object to gain access to the books database.
        dbHelper = new BookDbHelper(mContext);
        return true;
    }

    // Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor;
        int matcher = sUriMatcher.match(uri);

        switch (matcher) {
            case BOOKS:
                cursor = database.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case BOOK_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown Uri " + uri);
        }
        // Set notification URI on the Cursor.
        cursor.setNotificationUri(mContext.getContentResolver(), uri);
        return cursor;
    }

    // Returns the MIME type of data for the content URI.
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        final int matcher = sUriMatcher.match(uri);

        switch (matcher) {
            case BOOKS:
                return BookEntry.CONTENT_LIST_TYPE;

            case BOOK_ID:
                return BookEntry.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalStateException("Unknown Uri " + uri + "with match " + matcher);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        final int matcher = sUriMatcher.match(uri);

        switch (matcher) {
            case BOOKS:
                return insertBook(uri, contentValues);

            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    // Delete the data at the given selection and selection arguments.
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        final int matcher = sUriMatcher.match(uri);

        switch (matcher) {
            case BOOKS:
                return deleteBook(uri, selection, selectionArgs);

            case BOOK_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return deleteBook(uri, selection, selectionArgs);

            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    // Updates the data at the given selection and selection arguments, with the new ContentValues.
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        final int matcher = sUriMatcher.match(uri);

        switch (matcher) {
            case BOOKS:
                return updateBook(uri, values, selection, selectionArgs);

            case BOOK_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateBook(uri, values, selection, selectionArgs);

            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private Uri insertBook(Uri uri, ContentValues values) {
        // Check that the name is not null.
        String name = values.getAsString(BookEntry.COLUMN_PRODUCT_NAME);

        if (TextUtils.isEmpty(name)) {
            throw new IllegalArgumentException("The book requires a title");
        }

        // If the price is provided, check that it's greater than or equal to 0.
        Integer price = values.getAsInteger(BookEntry.COLUMN_PRICE);

        if (price != null && price < 0) {
            throw new IllegalArgumentException("The price cannot have a negative value");
        }

        // If the quantity is provided, check that it's greater than or equal to 0.
        Integer quantity = values.getAsInteger(BookEntry.COLUMN_QUANTITY);

        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException("The quantity cannot have a negative value");
        }

        // Check that the supplier name is not null.
        String supplierName = values.getAsString(BookEntry.COLUMN_SUPPLIER_NAME);

        if (TextUtils.isEmpty(supplierName)) {
            throw new IllegalArgumentException("The book requires a supplier name");
        }
        // Get the database in write mode.
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        // Insert a new row for book in the database, returning the ID of that new row.
        long id = database.insert(BookEntry.TABLE_NAME, null, values);

        // Show a toast message if the insertion was not successful.
        if (id == -1) {
            Toast.makeText(mContext, "Failed to insert row for " + uri, Toast.LENGTH_LONG).show();
        }
        // Notify all listeners that the data has changed for the book content URI.
        mContext.getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    private int deleteBook(Uri uri, String selection, String[] selectionArgs) {
        // Get the database in write mode.
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        // Track the number of rows that were deleted.
        int rowsDeleted = database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);

        if (rowsDeleted != 0) {
            // Notify all listeners that the data has changed for the book content URI.
            mContext.getContentResolver().notifyChange(uri, null);
        }
        // Returns the number of database rows affected by the update statement.
        return rowsDeleted;
    }

    private int updateBook(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // Check if the name of the book needs to be updated.
        if (values.containsKey(BookEntry.COLUMN_PRODUCT_NAME)) {
            // Check that the name of the book is not null.
            String name = values.getAsString(BookEntry.COLUMN_PRODUCT_NAME);
            if (TextUtils.isEmpty(name)) {
                throw new IllegalArgumentException("The book requires a title");
            }
        }

        // Check if the price of the book needs to be updated.
        if (values.containsKey(BookEntry.COLUMN_PRICE)) {
            // If the price is provided, check that it's greater than or equal to 0.
            Integer price = values.getAsInteger(BookEntry.COLUMN_PRICE);
            if (price != null && price < 0) {
                throw new IllegalArgumentException("The price cannot have a negative value");
            }
        }

        // Check if the quantity of the book needs to be updated.
        if (values.containsKey(BookEntry.COLUMN_QUANTITY)) {
            // If the quantity is provided, check that it's greater than or equal to 0.
            Integer quantity = values.getAsInteger(BookEntry.COLUMN_QUANTITY);
            if (quantity != null && quantity < 0) {
                throw new IllegalArgumentException("The quantity cannot have a negative value");
            }
        }

        // Check if the supplier name of the book needs to be updated.
        if (values.containsKey(BookEntry.COLUMN_SUPPLIER_NAME)) {
            // Check that the supplier name of the book is not null.
            String supplierName = values.getAsString(BookEntry.COLUMN_SUPPLIER_NAME);
            if (TextUtils.isEmpty(supplierName)) {
                throw new IllegalArgumentException("The book requires a supplier name");
            }
        }

        // If there are no values to update, then don't try to update the database.
        if (values.size() == 0) {
            return 0;
        }

        // Get the database in write mode.
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        // Perform the update on the database and get the number of rows affected.
        int rowsUpdated = database.update(BookEntry.TABLE_NAME, values, selection, selectionArgs);
        // If 1 or more rows were updated, then notify all listeners that the data at the given URI has changed.
        if (rowsUpdated != 0) {
            mContext.getContentResolver().notifyChange(uri, null);
        }
        // Returns the number of database rows affected by the update statement.
        return rowsUpdated;
    }
}