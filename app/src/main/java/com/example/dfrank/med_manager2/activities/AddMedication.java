package com.example.dfrank.med_manager2.activities;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dfrank.med_manager2.Adapter.MedCursorAdapter;
import com.example.dfrank.med_manager2.R;

import java.util.Calendar;

import com.example.dfrank.med_manager2.data.MedManagerContract;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import reminder.AlarmScheduler;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by dfrank on 4/6/18.
 */

public class AddMedication extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,
LoaderManager.LoaderCallbacks<Cursor>{

    private static final long milMinute = 60000L;
    private static final long milHour = 3600000L;
    private static final long milDay = 86400000L;
    private static final long milWeek = 604800000L;
    private static final long milMonth = 2592000000L;

    private Calendar mCalendar;
    private int errorColor;
    private int mYear, mMonth, mHour, mMinute, mDay;
    private long mRepeatTime;
    private Switch mRepeatSwitch;
    private String mTitle;
    private String mTime;
    private String mDescription;
    private String mStartDate;
    private String mEndDate;
    private String mRepeat;
    private String mRepeatNo;
    private String mRepeatType;
    public String mActive;
    private String errorString;
    private ForegroundColorSpan foregroundColorSpan;
    private SpannableStringBuilder spannableStringBuilder;


    private Uri mCurrentReminderUri;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.reminder_title) EditText title;
    @BindView(R.id.description) EditText description;
    @BindView(R.id.set_startDate) TextView startDate;
    @BindView(R.id.set_endDate) TextView endDate;
    @BindView(R.id.set_repeat) TextView repeat;
    @BindView(R.id.repeat_switch) Switch sRepeat;
    @BindView(R.id.set_repeat_no) TextView repeatNo;
    @BindView(R.id.set_repeat_type) TextView repeatType;
    @BindView(R.id.set_time) TextView mTimeText;
    private boolean mVehicleHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mVehicleHasChanged = true;
            return false;
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addmedication);
        ButterKnife.bind(this);
        errorString = getString(R.string.errorString);
        errorColor = ContextCompat.getColor(getApplicationContext(), R.color.colorAccent);
        foregroundColorSpan = new ForegroundColorSpan(errorColor);
        spannableStringBuilder = new SpannableStringBuilder(errorString);
        spannableStringBuilder.setSpan(foregroundColorSpan, 0, errorString.length(), 0);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add Medication");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //getting uri from intent
        Intent intent = getIntent();
        mCurrentReminderUri = intent.getData();

        if (mCurrentReminderUri==null){
            getSupportActionBar().setTitle("Add Medication");
            invalidateOptionsMenu();

        }else {
            getSupportActionBar().setTitle("Edit Medication");
            getLoaderManager().restartLoader(0,null,this);
        }

        // Initialize default values
        mActive = "true";
        mRepeat = "true";
        mRepeatNo = Integer.toString(1);
        mRepeatType = "Hour";

        mCalendar = Calendar.getInstance();
        mHour = mCalendar.get(Calendar.HOUR_OF_DAY);
        mMinute = mCalendar.get(Calendar.MINUTE);
        mYear = mCalendar.get(Calendar.YEAR);
        mMonth = mCalendar.get(Calendar.MONTH) + 1;
        mDay = mCalendar.get(Calendar.DATE);

        mStartDate = mDay + "/" + mMonth + "/" + mYear;
        mEndDate = mDay + "/" + mMonth + "/" + mYear;
        mTime = mHour + ":" + mMinute;


        // Setup TextViews using reminder values
            startDate.setText(mStartDate);
            endDate.setText(mEndDate);
            repeatNo.setText(mRepeatNo);
            mTimeText.setText(mTime);
            repeatType.setText(mRepeatType);
            repeat.setText("Every " + mRepeatNo + " " + mRepeatType + "(s)");
    }

    public void setStartDate(View v){

        //Setting up start date
        Calendar now = Calendar.getInstance();
        DatePickerDialog datePickerDialog =
                DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        monthOfYear ++;
                        mDay = dayOfMonth;
                        mMonth = monthOfYear;
                        mYear = year;
                        mStartDate = dayOfMonth + "/" + monthOfYear + "/" + year;
                        startDate.setText(mStartDate);
                    }
                                             },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show(getFragmentManager(), "Datepickerdialog");
    }

    public void setEndDate(View v){

        //Setting up End date
        Calendar now = Calendar.getInstance();
        DatePickerDialog datePickerDialog =
                DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        monthOfYear ++;
                        mDay = dayOfMonth;
                        mMonth = monthOfYear;
                        mYear = year;
                        mEndDate = dayOfMonth + "/" + monthOfYear + "/" + year;
                        endDate.setText(mEndDate);
                    }
                    },
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
        datePickerDialog.show(getFragmentManager(), "Datepickerdialog");
    }

    public void setTime(View v){


        //Setting Start time;
        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {

                mHour = hourOfDay;
                mMinute = minute;
                if (minute < 10) {
                    mTime = hourOfDay + ":" + "0" + minute;
                } else {
                    mTime = hourOfDay + ":" + minute;
                }
                mTimeText.setText(mTime);
            }
            }
                ,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                false
        );
        tpd.setThemeDark(false);
        tpd.show(getFragmentManager(), "Timepickerdialog");
    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        monthOfYear ++;
        mDay = dayOfMonth;
        mMonth = monthOfYear;
        mYear = year;
    }


    public void onSwitchRepeat(View view) {
        boolean on = ((Switch) view).isChecked();
        if (on) {
            mRepeat = "true";
            repeat.setText("Every " + mRepeatNo + " " + mRepeatType + "(s)");
        } else {
            mRepeat = "false";
            repeat.setText(R.string.repeat_off);
        }
    }

    public void selectRepeatType(View v){
        final String[] items = new String[4];

        items[0] = "Hour";
        items[1] = "Day";
        items[2] = "Week";
        items[3] = "Month";

        // Create List Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Type");
        builder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

                mRepeatType = items[item];
                repeatType.setText(mRepeatType);
                repeat.setText("Every " + mRepeatNo + " " + mRepeatType + "(s)");
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void setRepeatNo(View v){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Enter Number");

        // Create EditText box to input repeat number
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        alert.setView(input);
        alert.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        if (input.getText().toString().length() == 0) {
                            mRepeatNo = Integer.toString(1);
                            repeatNo.setText(mRepeatNo);
                            repeat.setText("Every " + mRepeatNo + " " + mRepeatType + "(s)");
                        }
                        else {
                            mRepeatNo = input.getText().toString().trim();
                            repeatNo.setText(mRepeatNo);
                            repeat.setText("Every " + mRepeatNo + " " + mRepeatType + "(s)");
                        }
                    }
                });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // do nothing
            }
        });
        alert.show();
    }

    private void saveReminder() {
        mTitle = title.getText().toString().trim();
        mDescription = description.getText().toString().trim();

        mCalendar.set(Calendar.MONTH, --mMonth);
        mCalendar.set(Calendar.YEAR, mYear);
        mCalendar.set(Calendar.DAY_OF_MONTH, mDay);
        mCalendar.set(Calendar.HOUR_OF_DAY, mHour);
        mCalendar.set(Calendar.MINUTE, mMinute);
        mCalendar.set(Calendar.SECOND, 0);

        if (mRepeatType.equals("Hour")) {
            mRepeatTime = Integer.parseInt(mRepeatNo) * milHour;
        } else if (mRepeatType.equals("Day")) {
            mRepeatTime = Integer.parseInt(mRepeatNo) * milDay;
        } else if (mRepeatType.equals("Week")) {
            mRepeatTime = Integer.parseInt(mRepeatNo) * milWeek;
        } else if (mRepeatType.equals("Month")) {
            mRepeatTime = Integer.parseInt(mRepeatNo) * milMonth;
        }


        if (mTitle.isEmpty() || mDescription.isEmpty()) {
            title.setError(spannableStringBuilder);
            description.setError(spannableStringBuilder);
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MedManagerContract.MedManagerEntry.COLUMN_TITLE, mTitle);
            contentValues.put(MedManagerContract.MedManagerEntry.COLUMN_DESCRIPTION, mDescription);
            contentValues.put(MedManagerContract.MedManagerEntry.COLUMN_TIME, mTime);
            contentValues.put(MedManagerContract.MedManagerEntry.COLUMN_START_DATE, mStartDate);
            contentValues.put(MedManagerContract.MedManagerEntry.COLUMN_END_DATE, mEndDate);
            contentValues.put(MedManagerContract.MedManagerEntry.COLUMN_INTERVAL, mRepeat);
            contentValues.put(MedManagerContract.MedManagerEntry.COLUMN_INTERVAL_NO, mRepeatNo);
            contentValues.put(MedManagerContract.MedManagerEntry.COLUMN_INTERVAL_TYPE, mRepeatType);
            contentValues.put(MedManagerContract.MedManagerEntry.COLUMN_ACTIVE, mActive);

            Uri newUri = getContentResolver().insert(MedManagerContract.MedManagerEntry.CONTENT_URI, contentValues);
            if (newUri != null) {
                toast(getString(R.string.insertSuccess));




                finish();
            } else {
                toast(getString(R.string.insertFailure));
            }
            // Set up calender for creating the notification
            long selectedTimestamp =  mCalendar.getTimeInMillis();
            if (mActive.equals("true")) {
                if (mRepeat.equals("true")) {
                    new AlarmScheduler().setRepeatAlarm(getApplicationContext(), selectedTimestamp, mCurrentReminderUri, mRepeatTime);
                } else if (mRepeat.equals("false")) {
                    new AlarmScheduler().setAlarm(getApplicationContext(), selectedTimestamp, newUri);
                }
            }


            }



    }

    private void updateMedication(){
        mTitle = title.getText().toString().trim();
        mDescription = description.getText().toString().trim();

        if (mTitle.isEmpty() || mDescription.isEmpty()) {
            title.setError(spannableStringBuilder);
            description.setError(spannableStringBuilder);
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MedManagerContract.MedManagerEntry.COLUMN_TITLE, mTitle);
            contentValues.put(MedManagerContract.MedManagerEntry.COLUMN_DESCRIPTION, mDescription);
            contentValues.put(MedManagerContract.MedManagerEntry.COLUMN_TIME, mTime);
            contentValues.put(MedManagerContract.MedManagerEntry.COLUMN_START_DATE, mStartDate);
            contentValues.put(MedManagerContract.MedManagerEntry.COLUMN_END_DATE, mEndDate);
            contentValues.put(MedManagerContract.MedManagerEntry.COLUMN_INTERVAL, mRepeat);
            contentValues.put(MedManagerContract.MedManagerEntry.COLUMN_INTERVAL_NO, mRepeatNo);
            contentValues.put(MedManagerContract.MedManagerEntry.COLUMN_INTERVAL_TYPE, mRepeatType);
            contentValues.put(MedManagerContract.MedManagerEntry.COLUMN_ACTIVE, mActive);

            int newId = getContentResolver().update(mCurrentReminderUri,
                    contentValues, null, null);

            if (newId == 0) {
                toast("Failed to update");
            } else {
                long selectedTimestamp =  mCalendar.getTimeInMillis();
                if (mRepeat.equals("true")) {
                    new AlarmScheduler().setRepeatAlarm(getApplicationContext(), selectedTimestamp, mCurrentReminderUri, mRepeatTime);
                } else if (mRepeat.equals("false")) {
                    new AlarmScheduler().setAlarm(getApplicationContext(), selectedTimestamp, mCurrentReminderUri);
                }
                toast("Update Successful");
                finish();
            }
        }
    }
    private void deleteMedication(){
        mActive = "false";
        //deleting medication
        new AlarmScheduler().cancelAlarm(getApplicationContext(), mCurrentReminderUri);
        int rowsDeleted = getContentResolver().delete(mCurrentReminderUri, null, null);
        if (rowsDeleted == 0) {
            // If no rows were deleted, then there was an error with the delete.
            Toast.makeText(this, "Failed to delete",
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the delete was successful and we can display a toast.
            Toast.makeText(this, "Delete Successful",
                    Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you really want to delete this Medication ");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the reminder.
                deleteMedication();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the reminder.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_medication, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new reminder, hide the "Delete" menu item.
        if (mCurrentReminderUri == null) {
            MenuItem menuItem = menu.findItem(R.id.discard_reminder);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.saveMedication:
                if (mCurrentReminderUri==null) {
                    saveReminder();
                }else {
                    updateMedication();
                }
                break;
            case R.id.discard_reminder:
                showDeleteConfirmationDialog();
        }
        return true;
    }

    //Toast Method
    private void toast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {


        return new AsyncTaskLoader<Cursor>(this) {
            Cursor mTaskData;

            @Override
            protected void onStartLoading() {
                if (mTaskData != null) {
                    // Delivers any previously loaded data immediately
                    deliverResult(mTaskData);
                } else {
                    // Force a new load
                    forceLoad();
                }
            }


            @Override
            public Cursor loadInBackground() {

                try {
                    return getContentResolver().query(mCurrentReminderUri,
                            null,
                            null,
                            null,
                            null);

                } catch (Exception e) {
                    Log.e("TAG", "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }
            public void deliverResult(Cursor data) {
                mTaskData = data;
                super.deliverResult(data);
            }
        };

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(MedManagerContract.MedManagerEntry._ID);
            int titleIndex = cursor.getColumnIndex(MedManagerContract.MedManagerEntry.COLUMN_TITLE);
            int timeIndex = cursor.getColumnIndex(MedManagerContract.MedManagerEntry.COLUMN_TIME);
            int descriptionIndex = cursor.getColumnIndex(MedManagerContract.MedManagerEntry.COLUMN_DESCRIPTION);
            int intervalIndex = cursor.getColumnIndex(MedManagerContract.MedManagerEntry.COLUMN_INTERVAL);
            int intervalNoIndex = cursor.getColumnIndex(MedManagerContract.MedManagerEntry.COLUMN_INTERVAL_NO);
            int startDateIndex = cursor.getColumnIndex(MedManagerContract.MedManagerEntry.COLUMN_START_DATE);
            int endDateIndex = cursor.getColumnIndex(MedManagerContract.MedManagerEntry.COLUMN_END_DATE);
            int intervalTypeIndex = cursor.getColumnIndex(MedManagerContract.MedManagerEntry.COLUMN_INTERVAL_TYPE);

            //cursor.moveToPosition(position);

            int id = cursor.getInt(idIndex);
            String mmtitle = cursor.getString(titleIndex);
            String mmdesc = cursor.getString(descriptionIndex);
            String mmstart = cursor.getString(startDateIndex);
            String mmEnd = cursor.getString(endDateIndex);
            String mmTime = cursor.getString(timeIndex);
            String mmintervalNo = cursor.getString(intervalNoIndex);
            String mmintervalType = cursor.getString(intervalTypeIndex);

            title.setText(mmtitle);
            description.setText(mmdesc);
            startDate.setText(mmstart);
            mTimeText.setText(mmTime);
            endDate.setText(mmEnd);
            repeatNo.setText(mmintervalNo);
            repeatType.setText(mmintervalType);
            repeat.setText("Every " + mmintervalType + " " + mmintervalNo + "(s)");

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
