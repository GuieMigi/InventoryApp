package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.BookContract.BookEntry;

public class BookCursorAdapter extends CursorAdapter {

    public BookCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    // Makes a new blank list item view. No data is set (or bound) to the views yet.
    @Override
    public View newView(final Context context, final Cursor cursor, ViewGroup viewGroup) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
    }

    // This method binds the book data (in the current row pointed to by cursor) to the given list item layout.
    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        // Find individual views that we want to modify in the list item layout.
        TextView bookTitleTextView = view.findViewById(R.id.book_title);
        TextView bookPriceTextView = view.findViewById(R.id.book_price);
        TextView bookQuantityTextView = view.findViewById(R.id.quantity);
        ImageView saleButton = view.findViewById(R.id.sale_button);

        // Read the book attributes from the Cursor for the current book.
        String titleString = cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME));
        String priceString = cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_PRICE));
        final String quantityString = cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY));
        final long currentBookId = cursor.getInt(cursor.getColumnIndex(BookEntry._ID));
        final Uri currentBookUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, currentBookId);

        // Update the TextViews with the attributes of the current book.
        bookTitleTextView.setText(titleString);
        bookPriceTextView.setText("Price: " + priceString);
        bookQuantityTextView.setText("Stock: " + quantityString);

        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sellBook(context, currentBookUri, quantityString);
            }
        });
    }

    private void sellBook(Context context, Uri currentBookUri, String quantityString) {

        int currentBookQuantity = Integer.valueOf(quantityString);
        // If the quantity is 0 then show a Toast that the book is no longer available.
        if (currentBookQuantity == 0) {
            Toast.makeText(context, "The book is no longer available", Toast.LENGTH_LONG).show();
        } else {
            // The book is still available so decrease the quantity by 1.
            currentBookQuantity--;
            // Create a new ContentValues object.
            ContentValues values = new ContentValues();
            // Store the new quantity for the book.
            values.put(BookEntry.COLUMN_QUANTITY, currentBookQuantity);
            // Update the book with the new quantity.
            int rowsModified = context.getContentResolver().update(currentBookUri, values, null, null);

            // Check if the update was successful.
            if (rowsModified == -1) {
                Toast.makeText(context, "Error selling book", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "Book sold", Toast.LENGTH_LONG).show();
            }
        }
    }
}
