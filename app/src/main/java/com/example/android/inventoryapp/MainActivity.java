package com.example.android.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.BookContract.BookEntry;
import com.example.android.inventoryapp.data.BookDbHelper;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    // The Id of the BookCursorLoader.
    private static final int BOOK_CURSOR_LOADER_ID = 0;
    // Declare a new instance of the BookDbHelper class.
    private BookDbHelper dbHelper;
    // Declare a new instance of the BookCursorAdapter.
    private BookCursorAdapter bookAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new BookDbHelper(this);

        // Find the ListView which will be populated with the book data and set the emptyView.
        final ListView bookListView = findViewById(R.id.list_view);
        View emptyView = findViewById(R.id.empty_view);
        bookListView.setEmptyView(emptyView);

        // Initialize the Adapter to create a list item for each row of book data in the Cursor.
        bookAdapter = new BookCursorAdapter(this, null);
        // Attach the adapter to the ListView.
        bookListView.setAdapter(bookAdapter);

        // Initialise the BookCursorLoader.
        getLoaderManager().initLoader(BOOK_CURSOR_LOADER_ID, null, this);

        // Set onItemClickListener on the ListView to open the EditorActivity.
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ContentUris contentUris = new ContentUris();
                Uri currentBookUri = contentUris.withAppendedId(BookEntry.CONTENT_URI, i);
                Intent startEditorActivity = new Intent(MainActivity.this, EditorActivity.class);
                startEditorActivity.setData(currentBookUri);
                startEditorActivity.putExtra("BOOK_ID", i);
                startActivity(startEditorActivity);
            }
        });
    }

    // Insert hard coded data into the books table.
    public void insertBook() {
        // Get the database in write mode.
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        // Create a ContentValues object where column names are the keys and the book's attributes are the values.
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_PRODUCT_NAME, "Sundiver");
        values.put(BookEntry.COLUMN_AUTHOR_NAME, "David Brin");
        values.put(BookEntry.COLUMN_PRICE, "50");
        values.put(BookEntry.COLUMN_QUANTITY, 1);
        values.put(BookEntry.COLUMN_SUPPLIER_NAME, "Nemira");
        values.put(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER, "+40 721 747 464");
        getContentResolver().insert(BookEntry.CONTENT_URI, values);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu.
        switch (item.getItemId()) {
            // Respond to a click on the "Insert test data" menu option.
            case R.id.insert_test_data:
                insertBook();
                return true;

            // Respond to a click on the "Delete everything" menu option.
            case R.id.delete_everything:
                deleteEverything();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {

        switch (loaderId) {
            case BOOK_CURSOR_LOADER_ID:
                String[] projection = {BookEntry._ID,
                        BookEntry.COLUMN_PRODUCT_NAME,
                        BookEntry.COLUMN_AUTHOR_NAME,
                        BookEntry.COLUMN_QUANTITY};
                return new CursorLoader(this, BookEntry.CONTENT_URI, projection, null, null, null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        bookAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        bookAdapter.swapCursor(null);
    }

    // Helper method to delete all the books from the database.
    public void deleteEverything() {

        int rowsDeleted = getContentResolver().delete(BookEntry.CONTENT_URI, null, null);
        // Show a toast message depending on whether or not the delete was successful.
        if (rowsDeleted == 0) {
            // If no rows were deleted, then there was an error with the delete.
            Toast.makeText(this, "Delete unsuccessful", Toast.LENGTH_LONG).show();
        } else {
            // Otherwise, the delete was successful and we can display a toast.
            Toast.makeText(this, "Delete successful", Toast.LENGTH_LONG).show();
        }
    }
}