package com.example.dfrank.med_manager2.data;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by dfrank on 3/22/18.
 */

public class MedManagerContract {

    private static final String Content = "content://";
    public static final String Authority = "com.example.dfrank.med_manager2";
    public static final String PATH_MEDICATIONS = "medication";
    public static final String PATH_MEDICATION_ID = "medication/#";
    public static final Uri Base_Content_Uri = Uri.parse(Content + Authority);

    public static abstract class MedManagerEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Base_Content_Uri.buildUpon()
                .appendPath(PATH_MEDICATIONS).build();
        public static final String Table_Name = "medication";
        public static final String COLUMN_TITLE = "medication";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_INTERVAL = "interval";
        public static final String COLUMN_TIME = "time";
        public static final String COLUMN_START_DATE = "start_date";
        public static final String COLUMN_END_DATE = "end_date";
        public static final String COLUMN_INTERVAL_NO = "interval_no";
        public static final String COLUMN_INTERVAL_TYPE = "interval_type";
        public static final String COLUMN_ACTIVE = "active";



        public static final String CREATE_TABLE = "CREATE TABLE " + Table_Name + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_TITLE + " TEXT , "
                + COLUMN_DESCRIPTION + " TEXT, "
                + COLUMN_INTERVAL + " INTEGER, "
                + COLUMN_INTERVAL_NO + " INTEGER, "
                + COLUMN_INTERVAL_TYPE + " INTEGER, "
                + COLUMN_TIME + " TEXT , "
                + COLUMN_START_DATE + " TEXT , "
                + COLUMN_ACTIVE + " TEXT, "
                + COLUMN_END_DATE + " TEXT);";


        static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + Authority + "/" + PATH_MEDICATIONS;

        static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + Authority + "/" + PATH_MEDICATIONS;
    }


    public static String getColumnString(Cursor cursor, String columnName) {
        return cursor.getString(cursor.getColumnIndex(columnName));
    }


}
