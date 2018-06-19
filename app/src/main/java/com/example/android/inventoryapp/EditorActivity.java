package com.example.android.inventoryapp;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.example.android.inventoryapp.data.BookContract.BookEntry;


public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    // The Id of the BookCursorLoader.
    private static final int BOOK_CURSOR_LOADER_ID = 1;
    // EditText field to enter the book's title.
    private EditText bookTitleEditText;
    // EditText field to enter the book's author.
    private EditText bookAuthorEditText;
    // EditText field to enter the book's price.
    private EditText bookPriceEditText;
    // EditText field to enter the book's quantity.
    private EditText bookQuantityEditText;
    // EditText field to enter the book's provider.
    private EditText bookSupplierNameEditText;
    // EditText field to enter the book's provider phone number.
    private EditText bookSupplierPhoneNumberEditText;
    // The variable that stores the id of the clicked pet.
    private long currentBookId;
    // The variable that stores the Uri for the clicked pet.
    private Uri currentBookUri;
    // The variable used to check if the user has changed anything in the form.
    private boolean bookChanged = false;
    // TODO: Check if the listener works like this or needs modifications.
    // OnTouchListener that listens for any user touches on a View.
    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            bookChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Initialize the EditTexts.
        bookTitleEditText = findViewById(R.id.book_title_edit_text);
        bookAuthorEditText = findViewById(R.id.book_author_edit_text);
        bookPriceEditText = findViewById(R.id.book_price_edit_text);
        bookQuantityEditText = findViewById(R.id.book_quantity_edit_text);
        bookSupplierNameEditText = findViewById(R.id.book_supplier_name_edit_text);
        bookSupplierPhoneNumberEditText = findViewById(R.id.book_supplier_phone_number_edit_text);

        // Get the Id of the clicked book from the Intent.
        currentBookId = getIntent().getLongExtra("BOOK_ID", 0);
        // Get the Uri of the clicked book from the Intent.
        currentBookUri = getIntent().getData();

        // Set onTouchListeners on the EditTexts.
        bookTitleEditText.setOnTouchListener(touchListener);
        bookAuthorEditText.setOnTouchListener(touchListener);
        bookPriceEditText.setOnTouchListener(touchListener);
        bookQuantityEditText.setOnTouchListener(touchListener);
        bookSupplierNameEditText.setOnTouchListener(touchListener);
        bookSupplierPhoneNumberEditText.setOnTouchListener(touchListener);

        // Check if the currentBookUri is null.
        if (currentBookUri == null) {
            // This is a new book so we need to set the Activity's title to "Add a book".
            setTitle("Add a book");
            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            invalidateOptionsMenu();
        } else setTitle("Edit book");

        // Initialize the CursorLoader.
        getLoaderManager().initLoader(BOOK_CURSOR_LOADER_ID, null, this);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new book, hide the "Delete" menu item.
        if (currentBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.delete_book);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu.
        switch (item.getItemId()) {
            // Respond to a click on the "Insert test data" menu option.
            case R.id.save_book:
                return true;

            // Respond to a click on the "Delete everything" menu option.
            case R.id.delete_book:
                return true;

            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the pet hasn't changed, continue with navigating up to the parent activity.
                if (!bookChanged) {
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }
                // TODO: Implement the discard button listener.
                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that changes should be discarded.
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // If the uri is null then we are adding a new book therefore no database call is required.
        if (currentBookUri == null) {
            return null;
        } else {
            String[] projection = new String[]{
                    BookEntry._ID,
                    BookEntry.COLUMN_PRODUCT_NAME,
                    BookEntry.COLUMN_AUTHOR_NAME,
                    BookEntry.COLUMN_PRICE,
                    BookEntry.COLUMN_QUANTITY,
                    BookEntry.COLUMN_SUPPLIER_NAME,
                    BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER
            };
            return new CursorLoader(this, currentBookUri, projection, null, null, null);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor.
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        // Move to the start of the Cursor.
        cursor.moveToFirst();

        // Get the index of each column.
        int bookTitleIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME);
        int bookAuthorIndex = cursor.getColumnIndex(BookEntry.COLUMN_AUTHOR_NAME);
        int bookPriceIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRICE);
        int bookQuantityIndex = cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY);
        int bookSupplierIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NAME);
        int bookSupplierPhoneNumberIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

        // Use that index to extract the String or Int value at the current row the cursor is on.
        String bookTitleString = cursor.getString(bookTitleIndex);
        String bookAuthorString = cursor.getString(bookAuthorIndex);
        double bookPriceDouble = cursor.getDouble(bookPriceIndex);
        int bookQuantityInteger = cursor.getInt(bookQuantityIndex);
        String bookSupplierString = cursor.getString(bookSupplierIndex);
        String bookSupplierPhoneNumberString = cursor.getString(bookSupplierPhoneNumberIndex);

        // Set The stored values inside the EditTexts.
        bookTitleEditText.setText(bookTitleString);
        bookAuthorEditText.setText(bookAuthorString);
        bookPriceEditText.setText(String.valueOf(bookPriceDouble));
        bookQuantityEditText.setText(String.valueOf(bookQuantityInteger));
        bookSupplierNameEditText.setText(bookSupplierString);
        bookSupplierPhoneNumberEditText.setText(bookSupplierPhoneNumberString);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Clear the values from the EditTexts.
        bookTitleEditText.getText().clear();
        bookAuthorEditText.getText().clear();
        bookPriceEditText.getText().clear();
        bookQuantityEditText.getText().clear();
        bookSupplierNameEditText.getText().clear();
        bookSupplierPhoneNumberEditText.getText().clear();
    }
}
