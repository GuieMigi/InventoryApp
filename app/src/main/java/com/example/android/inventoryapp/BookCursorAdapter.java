package com.example.android.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.inventoryapp.data.BookContract.BookEntry;

public class BookCursorAdapter extends CursorAdapter {

    public BookCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    // Makes a new blank list item view. No data is set (or bound) to the views yet.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
    }

    // This method binds the book data (in the current row pointed to by cursor) to the given list item layout.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout.
        TextView bookTitleTextView = view.findViewById(R.id.book_title);
        TextView bookAuthorTextView = view.findViewById(R.id.book_author);
        TextView bookQuantityTextView = view.findViewById(R.id.quantity);

        // Read the book attributes from the Cursor for the current book.
        String titleString = cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME));
        String authorString = cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_AUTHOR_NAME));
        String quantityString = cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY));

        // If the author name is an empty string or null, then use some default text
        // that says "Unknown author", so the TextView isn't blank.
        if (TextUtils.isEmpty(authorString)) {
            authorString = "Unknown author";
        }

        // Update the TextViews with the attributes of the current book.
        bookTitleTextView.setText(titleString);
        bookAuthorTextView.setText(authorString);
        bookQuantityTextView.setText(quantityString);
    }
}
