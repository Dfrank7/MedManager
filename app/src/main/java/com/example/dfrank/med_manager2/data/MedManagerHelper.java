package com.example.dfrank.med_manager2.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by dfrank on 3/22/18.
 */

public class MedManagerHelper extends SQLiteOpenHelper {
    public static final int DbVersion = 1;
    public static final String DbName = "medication_manager";
    public MedManagerHelper(Context context) {
        super(context, DbName, null, DbVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(MedManagerContract.MedManagerEntry.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
