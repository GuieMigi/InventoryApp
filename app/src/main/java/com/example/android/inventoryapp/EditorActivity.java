package com.example.android.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
    // The button used to order from the supplier.
    private Button orderButton;
    // The variable that stores the book quantity.
    private int bookQuantity;
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

        // Initialize the Views.
        bookTitleEditText = findViewById(R.id.book_title_edit_text);
        bookAuthorEditText = findViewById(R.id.book_author_edit_text);
        bookPriceEditText = findViewById(R.id.book_price_edit_text);
        bookQuantityEditText = findViewById(R.id.book_quantity_edit_text);
        bookSupplierNameEditText = findViewById(R.id.book_supplier_name_edit_text);
        bookSupplierPhoneNumberEditText = findViewById(R.id.book_supplier_phone_number_edit_text);
        orderButton = findViewById(R.id.order_button);
        Button minusButton = findViewById(R.id.minus_button);
        Button plusButton = findViewById(R.id.plus_button);

        // Get the Id of the clicked book from the Intent.
        currentBookId = getIntent().getLongExtra("BOOK_ID", 0);
        // Get the Uri of the clicked book from the Intent.
        currentBookUri = getIntent().getData();

        // Set onTouchListeners on the Views.
        bookTitleEditText.setOnTouchListener(touchListener);
        bookAuthorEditText.setOnTouchListener(touchListener);
        bookPriceEditText.setOnTouchListener(touchListener);
        bookQuantityEditText.setOnTouchListener(touchListener);
        bookSupplierNameEditText.setOnTouchListener(touchListener);
        bookSupplierPhoneNumberEditText.setOnTouchListener(touchListener);
        minusButton.setOnTouchListener(touchListener);
        plusButton.setOnTouchListener(touchListener);

        // Check if the currentBookUri is null.
        if (currentBookUri == null) {
            // This is a new book so we need to set the Activity's title to "Add a book".
            setTitle("Add a book");
            orderButton.setVisibility(View.GONE);
            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            invalidateOptionsMenu();
        } else setTitle("Edit book");

        // Set onClickListener on the minusButton to decrease the quantity.
        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bookQuantity == 0) {
                    Toast.makeText(EditorActivity.this, "The quantity cannot have a negative value", Toast.LENGTH_LONG).show();
                    return;
                }
                if (bookQuantity > 0) {
                    // Check if the bookQuantityEditText is not empty.
                    if (!TextUtils.isEmpty(bookQuantityEditText.getText().toString())) {
                        // Update the bookQuantity with the value from the bookQuantityEditText.
                        bookQuantity = Integer.parseInt(bookQuantityEditText.getText().toString().trim());
                    }
                    // Decrease the bookQuantity by 1.
                    bookQuantity--;
                    // Display the new bookQuantity in the bookQuantityEditText.
                    bookQuantityEditText.setText(String.valueOf(bookQuantity));
                }
            }
        });

        // Set onClickListener on the plusButton to increase the quantity.
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bookQuantity >= 50) {
                    Toast.makeText(EditorActivity.this, "The quantity is limited to 50", Toast.LENGTH_LONG).show();
                } else {
                    // Check if the bookQuantityEditText is not empty.
                    if (!TextUtils.isEmpty(bookQuantityEditText.getText().toString())) {
                        // Update the bookQuantity with the value from the bookQuantityEditText.
                        bookQuantity = Integer.parseInt(bookQuantityEditText.getText().toString().trim());
                    }
                    // Increase the bookQuantity by 1.
                    bookQuantity++;
                    // Display the new bookQuantity in the bookQuantityEditText.
                    bookQuantityEditText.setText(String.valueOf(bookQuantity));
                }
            }
        });

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
                // Save the book into the database.
                saveBook();
                return true;

            // Respond to a click on the "Delete everything" menu option.
            case R.id.delete_book:
                showDeleteConfirmationDialog();
                return true;

            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the pet hasn't changed, continue with navigating up to the parent activity.
                if (!bookChanged) {
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }
                // Show a dialog that notifies the user they have unsaved changes.
                showUnsavedChangesDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press.
        if (!bookChanged) {
            super.onBackPressed();
            return;
        }

        // Show dialog that there are unsaved changes.
        showUnsavedChangesDialog();
    }

    public void saveBook() {
        // Determine if this is a new or existing book by checking if currentBooktUri is null or not.
        if (currentBookUri == null) {
            // Read from input fields. Use trim to eliminate leading or trailing white space.
            String titleString = bookTitleEditText.getText().toString().trim();
            String authorString = bookAuthorEditText.getText().toString().trim();
            String priceString = bookPriceEditText.getText().toString().trim();
            String quantityString = bookQuantityEditText.getText().toString().trim();
            String supplierString = bookSupplierNameEditText.getText().toString().trim();
            String supplierPhoneNumberString = bookSupplierPhoneNumberEditText.getText().toString().trim();

            //Check if the required fields ahave been filled.
            if (TextUtils.isEmpty(titleString) || TextUtils.isEmpty(supplierString)) {
                Toast.makeText(this, "Please fill all the required fields!", Toast.LENGTH_LONG).show();
                return;
            }

            double priceDouble = 0;
            int quantityInt = 0;

            // Check if the priceString is empty.
            if (!TextUtils.isEmpty(priceString)) {
                priceDouble = Double.parseDouble(priceString);
            }
            // Check if the quantityString is empty.
            if (!TextUtils.isEmpty(quantityString)) {
                quantityInt = Integer.parseInt(quantityString);
            }

            // Check if the required fields are empty.
            if (TextUtils.isEmpty(priceString) || TextUtils.isEmpty(quantityString)) {
                Toast.makeText(this, "Please fill all the required fields", Toast.LENGTH_LONG).show();
                return;
                // Check if the price has a negative value.
            } else if (priceDouble < 0) {
                Toast.makeText(this, "The price cannot have a negative value", Toast.LENGTH_LONG).show();
                return;
                // Check if the quantity has a negative value.
            } else if (quantityInt < 0) {
                Toast.makeText(this, "The quantity cannot have a negative value", Toast.LENGTH_LONG).show();
                return;
            } else if (quantityInt > 50) {
                Toast.makeText(this, "The quantity is limited to 50", Toast.LENGTH_LONG).show();
                return;
            }

            // Create a ContentValues object where column names are the keys and book attributes from the editors are the values.
            ContentValues values = new ContentValues();
            values.put(BookEntry.COLUMN_PRODUCT_NAME, titleString);
            values.put(BookEntry.COLUMN_AUTHOR_NAME, authorString);
            values.put(BookEntry.COLUMN_PRICE, priceDouble);
            values.put(BookEntry.COLUMN_QUANTITY, quantityInt);
            values.put(BookEntry.COLUMN_SUPPLIER_NAME, supplierString);
            values.put(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER, supplierPhoneNumberString);
            // Call the ContentResolver to insert a new row in the database.
            Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);

            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, "Error saving book", Toast.LENGTH_LONG).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, "Book saved", Toast.LENGTH_LONG).show();
            }
            // Exit the activity.
            finish();
        } else {
            // Read from input fields. Use trim to eliminate leading or trailing white space.
            String titleString = bookTitleEditText.getText().toString().trim();
            String authorString = bookAuthorEditText.getText().toString().trim();
            String priceString = bookPriceEditText.getText().toString().trim();
            String quantityString = bookQuantityEditText.getText().toString().trim();
            String supplierString = bookSupplierNameEditText.getText().toString().trim();
            String supplierPhoneNumberString = bookSupplierPhoneNumberEditText.getText().toString().trim();

            double priceDouble = 0;
            int quantityInt = 0;

            // Check if the priceString is empty.
            if (!TextUtils.isEmpty(priceString)) {
                priceDouble = Double.parseDouble(priceString);
            }
            // Check if the quantityString is empty.
            if (!TextUtils.isEmpty(quantityString)) {
                quantityInt = Integer.parseInt(quantityString);
            }

            // Check if the required fields are empty.
            if (TextUtils.isEmpty(priceString) || TextUtils.isEmpty(quantityString)) {
                Toast.makeText(this, "Please fill all the required fields", Toast.LENGTH_LONG).show();
                return;
                // Check if the price has a negative value.
            } else if (priceDouble < 0) {
                Toast.makeText(this, "The price cannot have a negative value", Toast.LENGTH_LONG).show();
                return;
                // Check if the quantity has a negative value.
            } else if (quantityInt < 0) {
                Toast.makeText(this, "The quantity cannot have a negative value", Toast.LENGTH_LONG).show();
                return;
            } else if (quantityInt > 50) {
                Toast.makeText(this, "The quantity is limited to 50", Toast.LENGTH_LONG).show();
                return;
            }

            // Create a ContentValues object where column names are the keys and book attributes from the editors are the values.
            ContentValues values = new ContentValues();
            values.put(BookEntry.COLUMN_PRODUCT_NAME, titleString);
            values.put(BookEntry.COLUMN_AUTHOR_NAME, authorString);
            values.put(BookEntry.COLUMN_PRICE, priceDouble);
            values.put(BookEntry.COLUMN_QUANTITY, quantityInt);
            values.put(BookEntry.COLUMN_SUPPLIER_NAME, supplierString);
            values.put(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER, supplierPhoneNumberString);
            String selection = BookEntry._ID + "=?";
            String[] selectionArgs = new String[]{String.valueOf(currentBookId)};

            int rowsInserted = getContentResolver().update(currentBookUri, values, selection, selectionArgs);

            if (rowsInserted == -1) {
                Toast.makeText(this, "Error saving book", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Book saved", Toast.LENGTH_LONG).show();
            }
            // Exit the activity.
            finish();
        }
    }

    public void deleteBook() {

        if (currentBookUri != null) {
            // Call the ContentResolver to delete the book at the given content URI.
            int rowsDeleted = getContentResolver().delete(currentBookUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, "Error deleting book", Toast.LENGTH_LONG).show();
            } else {
                // Otherwise, the delete was successful and we can display a successful toast.
                Toast.makeText(this, "Book deleted", Toast.LENGTH_LONG).show();
            }
            // Exit the activity.
            finish();
        }
    }

    // Method used to create a “Discard changes” dialog.
    public void showUnsavedChangesDialog() {
        // Create an AlertDialog.Builder and set the message, Click listeners for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Discard you changes?");
        builder.setPositiveButton("Discard", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                NavUtils.navigateUpFromSameTask(EditorActivity.this);
            }
        });
        builder.setNegativeButton("Keep editing", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User clicked the "Keep editing" button, so dismiss the dialog and continue editing the pet.
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });
        // Create and show the AlertDialog.
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // Method used to create a "Delete confirmation" dialog.
    public void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, Click listeners for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete this book?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User clicked the "Delete" button, so delete the book.
                deleteBook();
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
        bookQuantity = cursor.getInt(bookQuantityIndex);
        String bookSupplierString = cursor.getString(bookSupplierIndex);
        final String bookSupplierPhoneNumberString = cursor.getString(bookSupplierPhoneNumberIndex);

        // Set the stored values inside the EditTexts.
        bookTitleEditText.setText(bookTitleString);
        bookAuthorEditText.setText(bookAuthorString);
        bookPriceEditText.setText(String.valueOf(bookPriceDouble));
        bookQuantityEditText.setText(String.valueOf(bookQuantity));
        bookSupplierNameEditText.setText(bookSupplierString);
        bookSupplierPhoneNumberEditText.setText(bookSupplierPhoneNumberString);

        // Set onClickListener on the orderButton to start a phone Intent.
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if the phone number is available.
                if (TextUtils.isEmpty(bookSupplierPhoneNumberString)) {
                    // Show a Toast if the number is not available.
                    Toast.makeText(EditorActivity.this, "The supplier's phone number is not available", Toast.LENGTH_LONG).show();
                } else {
                    // Start a new phone Intent.
                    Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
                    phoneIntent.setData(Uri.parse("tel: " + bookSupplierPhoneNumberString));
                    startActivity(phoneIntent);
                }
            }
        });
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