package com.example.android.inventoryapp.data;

import android.provider.BaseColumns;

public class BookContract {

    public static final class BookEntry implements BaseColumns {

        // Table name.
        public static final String TABLE_NAME = "books";
        // The _Id column.
        public static final String _ID = BaseColumns._ID;
        // The product_name column.
        public static final String COLUMN_PRODUCT_NAME = "product_name";
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