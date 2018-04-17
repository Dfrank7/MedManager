package com.example.dfrank.med_manager2.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.dfrank.med_manager2.Medication;

import java.util.ArrayList;
import java.util.List;

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

    public List<Medication> getAllMedication() {
        // array of columns to fetch
        String[] columns = {
                MedManagerContract.MedManagerEntry._ID,
                MedManagerContract.MedManagerEntry.COLUMN_TITLE,
                MedManagerContract.MedManagerEntry.COLUMN_DESCRIPTION,
                MedManagerContract.MedManagerEntry.COLUMN_TIME,
                MedManagerContract.MedManagerEntry.COLUMN_START_DATE,
                MedManagerContract.MedManagerEntry.COLUMN_END_DATE,
                MedManagerContract.MedManagerEntry.COLUMN_INTERVAL,
                MedManagerContract.MedManagerEntry.COLUMN_INTERVAL_TYPE,
                MedManagerContract.MedManagerEntry.COLUMN_INTERVAL_NO
        };
        // sorting orders
        List<Medication> medicationList = new ArrayList<Medication>();

        SQLiteDatabase db = this.getReadableDatabase();


        Cursor cursor = db.query(MedManagerContract.MedManagerEntry.Table_Name, //Table to query
                columns,    //columns to return
                null,        //columns for the WHERE clause
                null,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                null); //The sort order


        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Medication medication = new Medication();
                medication.setId(cursor.getInt(cursor.getColumnIndex(MedManagerContract.
                MedManagerEntry._ID)));
                medication.setTitle(cursor.getString(cursor.getColumnIndex(MedManagerContract.
                        MedManagerEntry.COLUMN_TITLE)));
                medication.setDescription(cursor.getString(cursor.getColumnIndex(MedManagerContract.
                        MedManagerEntry.COLUMN_DESCRIPTION)));
                medication.setTime(cursor.getString(cursor.getColumnIndex(MedManagerContract.
                        MedManagerEntry.COLUMN_TIME)));
                medication.setStartDate(cursor.getString(cursor.getColumnIndex(MedManagerContract.
                        MedManagerEntry.COLUMN_START_DATE)));
                medication.setEndDate(cursor.getString(cursor.getColumnIndex(MedManagerContract.
                        MedManagerEntry.COLUMN_END_DATE)));
                medication.setInterval(cursor.getString(cursor.getColumnIndex(MedManagerContract.
                        MedManagerEntry.COLUMN_INTERVAL)));
                medication.setIntervalType(cursor.getString(cursor.getColumnIndex(MedManagerContract.
                        MedManagerEntry.COLUMN_INTERVAL_TYPE)));
                medication.setIntervalNo(cursor.getString(cursor.getColumnIndex(MedManagerContract.
                        MedManagerEntry.COLUMN_INTERVAL_NO)));
                // Adding medication record to list
                medicationList.add(medication);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        // return medication list
        return medicationList;
    }
}
