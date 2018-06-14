package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.inventoryapp.data.BookContract;
import com.example.android.inventoryapp.data.BookContract.BookEntry;
import com.example.android.inventoryapp.data.BookDbHelper;

import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity {

    BookDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new BookDbHelper(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayText();
    }

    // Insert hard coded data into the books table.
    public void insertBook(View view) {
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
        database.insert(BookEntry.TABLE_NAME, null, values);

        displayText();
    }

    // A temporary method that displays the content of the SQL table inside a TextView.
    private void displayText() {
        Cursor cursor = queryBooks();

        try {
            TextView displayTextTextView = findViewById(R.id.text_view);
            displayTextTextView.setText("This table contains " + cursor.getCount() + " books.\n\n");
            displayTextTextView.append(BookEntry._ID + " - " +
                    BookEntry.COLUMN_PRODUCT_NAME + " - " +
                    BookEntry.COLUMN_AUTHOR_NAME + " - " +
                    BookEntry.COLUMN_PRICE + " - " +
                    BookEntry.COLUMN_QUANTITY + " - " +
                    BookEntry.COLUMN_SUPPLIER_NAME + " - " +
                    BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER + "\n");

            // Get the index for each column
            int IdColumnIndex = cursor.getColumnIndex(BookEntry._ID);
            int productNameIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME);
            int authorNameIndex = cursor.getColumnIndex(BookEntry.COLUMN_AUTHOR_NAME);
            int priceIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRICE);
            int quantityIndex = cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY);
            int supplierNameIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneNumberIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

            // Go through all the returned rows in the cursor
            while (cursor.moveToNext()) {
                // Use the index to extract the String or Int value at the current row the cursor is on.
                int currentId = cursor.getInt(IdColumnIndex);
                String currentProductName = cursor.getString(productNameIndex);
                String currentAuthorName = cursor.getString(authorNameIndex);
                int currentPrice = cursor.getInt(priceIndex);
                int currentQuantity = cursor.getInt(quantityIndex);
                String currentSupplierName = cursor.getString(supplierNameIndex);
                String currentSupplierPhoneNumber = cursor.getString(supplierPhoneNumberIndex);

                // Display the values from each column of the current row in the TextView
                displayTextTextView.append("\n" + currentId + " - " +
                        currentProductName + " - " +
                        currentAuthorName + " - " +
                        currentPrice + " - " +
                        currentQuantity + " - " +
                        currentSupplierName + " - " +
                        currentSupplierPhoneNumber);
            }
        } finally {
            // Close the cursor to releases all the resources.
            cursor.close();
        }
    }

    private Cursor queryBooks() {
        // Get the database in read mode.
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_PRODUCT_NAME,
                BookEntry.COLUMN_AUTHOR_NAME,
                BookEntry.COLUMN_PRICE,
                BookEntry.COLUMN_QUANTITY,
                BookEntry.COLUMN_SUPPLIER_NAME,
                BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER
        };

        Cursor cursor = database.query(
                BookEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null);

        return cursor;
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
                return true;

            // Respond to a click on the "Delete everything" menu option.
            case R.id.delete_everything:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}