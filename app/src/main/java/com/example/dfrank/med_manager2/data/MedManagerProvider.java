package com.example.dfrank.med_manager2.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.dfrank.med_manager2.Medication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dfrank on 3/22/18.
 */

public class MedManagerProvider extends ContentProvider {
    MedManagerHelper medManagerHelper;
    Context context;
    public MedManagerProvider(){

    }
    private static final int MED_MANAGER = 100;
    private static final int MED_MANAGER_ID = 101;
    private static final String Content = "content://";


//    private static final String Authority = "com.example.dfrank.med_manager";

    private static UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(MedManagerContract.Authority, MedManagerContract.PATH_MEDICATIONS, MED_MANAGER);
        uriMatcher.addURI(MedManagerContract.Authority, MedManagerContract.PATH_MEDICATION_ID, MED_MANAGER_ID);
    }

    @Override
    public boolean onCreate() {
        medManagerHelper = new MedManagerHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        Cursor cursor = null;
        SQLiteDatabase database = medManagerHelper.getReadableDatabase();
        int match = uriMatcher.match(uri);
        switch (match) {
            case MED_MANAGER:
                cursor = database.query(MedManagerContract.MedManagerEntry.Table_Name, strings, s,
                        strings1, null, null, s1);
                break;
            case MED_MANAGER_ID:
                strings1 = new String[]{String.valueOf(ContentUris.parseId(uri))};
                s = MedManagerContract.MedManagerEntry._ID + "=?";
                cursor = database.query(MedManagerContract.MedManagerEntry.Table_Name, strings, s,
                        strings1, null, null, s1);
                break;
//            case MED_MANAGER_USER_ID:
//                strings1 = new String[]{String.valueOf(ContentUris.parseId(uri))};
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        int match = uriMatcher.match(uri);
        switch (match) {
            case MED_MANAGER:
                insertMedication(uri, contentValues);
                return uri;
            default:
                throw new IllegalArgumentException("insertion is not supported for " + uri);
        }
    }

    private Uri insertMedication(Uri uri, ContentValues cv) {
        Uri returnUri;
        SQLiteDatabase database = medManagerHelper.getWritableDatabase();
        long id = database.insert(MedManagerContract.MedManagerEntry.Table_Name, null, cv);
        if (id > 0) {
            returnUri = ContentUris.withAppendedId(uri, id);
        } else {
            throw new android.database.SQLException("Unsupported Uri");
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        SQLiteDatabase database = medManagerHelper.getWritableDatabase();
        int match = uriMatcher.match(uri);
        int id = 0;
        switch (match) {
            case MED_MANAGER:
                id = database.delete(MedManagerContract.MedManagerEntry.Table_Name, s, strings);
                if (id != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
            case MED_MANAGER_ID:
                s = MedManagerContract.MedManagerEntry._ID + "=?";
                strings = new String[]{String.valueOf(ContentUris.parseId(uri))};
                id = database.delete(MedManagerContract.MedManagerEntry.Table_Name, s, strings);
                if (id != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
        }
        return id;
    }


    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s,
                      @Nullable String[] strings) {
        SQLiteDatabase database = medManagerHelper.getWritableDatabase();
//        int id = database.update(MedManagerContract.MedManagerEntry.Table_Name,contentValues,s,strings);
//        getContext().getContentResolver().notifyChange(uri,null);
//        return id;
        int match = uriMatcher.match(uri);

        switch (match) {
            case MED_MANAGER_ID:
                s = MedManagerContract.MedManagerEntry._ID + "=?";
                strings = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateMedication(uri, contentValues, s, strings);
            default:
                throw new IllegalArgumentException("update is not supported for " + uri);
                //default:throw new IllegalArgumentException("update is not supported for "+uri);
        }
    }

    private int updateMedication(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        SQLiteDatabase database = medManagerHelper.getWritableDatabase();

        int rowsUpdated = database.update(MedManagerContract.MedManagerEntry.Table_Name
                , values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }




}
