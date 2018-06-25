package com.example.android.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.example.android.inventoryapp.data.BookContract.BookEntry;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    // The Id of the BookCursorLoader.
    private static final int BOOK_CURSOR_LOADER_ID = 0;
    // Declare a new instance of the BookCursorAdapter.
    private BookCursorAdapter bookAdapter;

    private Uri currentBookUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup FloatingActionButton to open EditorActivity.
        FloatingActionButton floatingActionButton = findViewById(R.id.floating_action_button);
        // Set onClickListener on the floatingActionButton to open the EditorActivity.
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startEditor = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(startEditor);
            }
        });

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
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                ContentUris contentUris = new ContentUris();
                currentBookUri = contentUris.withAppendedId(BookEntry.CONTENT_URI, id);
                Intent startEditorActivity = new Intent(MainActivity.this, EditorActivity.class);
                startEditorActivity.setData(currentBookUri);
                startEditorActivity.putExtra("BOOK_ID", id);
                startActivity(startEditorActivity);
            }
        });
    }

    // Insert hard coded data into the books table.
    public void insertBook() {
        // Create a ContentValues object where column names are the keys and the book's attributes are the values.
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_PRODUCT_NAME, "Sundiver");
        values.put(BookEntry.COLUMN_AUTHOR_NAME, "David Brin");
        values.put(BookEntry.COLUMN_PRICE, "49.99");
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
                showDeleteEverythingConfirmationDialog();
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
                        BookEntry.COLUMN_PRICE,
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

    public void showDeleteEverythingConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, Click listeners for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete everything?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User clicked the "Delete" button, so delete the book.
                deleteEverything();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User clicked the "Cancel" button, so dismiss the dialog.
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });
        // Create and show the AlertDialog.
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void sellBook() {

        if (currentBookUri != null) {
            String[] projection = new String[]{
                    BookEntry._ID,
                    BookEntry.COLUMN_PRODUCT_NAME,
                    BookEntry.COLUMN_AUTHOR_NAME,
                    BookEntry.COLUMN_PRICE,
                    BookEntry.COLUMN_QUANTITY,
                    BookEntry.COLUMN_SUPPLIER_NAME,
                    BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER
            };

            Cursor cursor = getContentResolver().query(currentBookUri, projection, null, null, null);
            // Bail early if the cursor is null or there is less than 1 row in the cursor.
            if (cursor == null || cursor.getCount() < 1) {
                return;
            }
            // Move to the first row in the cursor.
            cursor.moveToFirst();
            // Get the current quantity for the book.
            int currentBookQuantity = cursor.getInt(cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY));
            // If the quantity is 0 then show a Toast that the book is no longer available.
            if (currentBookQuantity == 0) {
                Toast.makeText(this, "The book is no longer available", Toast.LENGTH_LONG).show();
            } else {
                // The book is still available so decrease the quantity by 1.
                currentBookQuantity--;
                // Create a new ContentValues object.
                ContentValues values = new ContentValues();
                // Store the new quantity for the book.
                values.put(BookEntry.COLUMN_QUANTITY, currentBookQuantity);
                // Update the book with the new quantity.
                int rowsModified = getContentResolver().update(currentBookUri, values, null, null);

                // Check if the update was successful.
                if (rowsModified == -1) {
                    Toast.makeText(this, "Error selling book", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Book sold", Toast.LENGTH_LONG).show();
                }
            }
            // Close the cursor to free the resources.
            cursor.close();
        }
    }
}