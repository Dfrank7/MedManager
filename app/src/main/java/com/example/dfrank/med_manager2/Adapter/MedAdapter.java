package com.example.dfrank.med_manager2.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.dfrank.med_manager2.R;
import com.example.dfrank.med_manager2.data.MedManagerContract;

/**
 * Created by dfrank on 4/10/18.
 */

public class MedAdapter extends CursorAdapter {

    private TextView mTitleText, mDateAndTimeText, mRepeatInfoText;
    private ImageView mActiveImage , mThumbnailImage;
    private ColorGenerator mColorGenerator = ColorGenerator.DEFAULT;
    private TextDrawable mDrawableBuilder;
    public MedAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.meditem, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        mTitleText = view.findViewById(R.id.recycle_title);
        mDateAndTimeText = view.findViewById(R.id.recycle_date);
        mRepeatInfoText =  view.findViewById(R.id.recycle_repeat_info);
        mThumbnailImage = view.findViewById(R.id.thumbnail_image);

        int idIndex = cursor.getColumnIndex(MedManagerContract.MedManagerEntry._ID);
        int titleIndex = cursor.getColumnIndex(MedManagerContract.MedManagerEntry.COLUMN_TITLE);
        int descriptionIndex = cursor.getColumnIndex(MedManagerContract.MedManagerEntry.COLUMN_DESCRIPTION);
        int intervalIndex = cursor.getColumnIndex(MedManagerContract.MedManagerEntry.COLUMN_INTERVAL);
        int intervalNoIndex = cursor.getColumnIndex(MedManagerContract.MedManagerEntry.COLUMN_INTERVAL_NO);
        int startDateIndex =cursor.getColumnIndex(MedManagerContract.MedManagerEntry.COLUMN_START_DATE);
        int endDateIndex = cursor.getColumnIndex(MedManagerContract.MedManagerEntry.COLUMN_END_DATE);
        int intervalTypeIndex = cursor.getColumnIndex(MedManagerContract.MedManagerEntry.COLUMN_INTERVAL_TYPE);

        String title = cursor.getString(titleIndex);
        String description = cursor.getString(descriptionIndex);
        String startDate = cursor.getString(startDateIndex);
        String endDate = cursor.getString(endDateIndex);
        String interval = cursor.getString(intervalIndex);
        String intervalNo = cursor.getString(intervalNoIndex);
        String intervalType = cursor.getString(intervalTypeIndex);

        String date = startDate+ " - "+ endDate;


        setReminderTitle(title);
        setReminderDateTime(date);
        setReminderRepeatInfo(interval, intervalNo, intervalType);
        //setActiveImage(active);

    }

    public void setReminderTitle(String title) {
        mTitleText.setText(title);
        String letter = "A";

        if(title != null && !title.isEmpty()) {
            letter = title.substring(0, 1);
        }

        int color = mColorGenerator.getRandomColor();

        // Create a circular icon consisting of  a random background colour and first letter of title
        mDrawableBuilder = TextDrawable.builder()
                .buildRound(letter, color);
        mThumbnailImage.setImageDrawable(mDrawableBuilder);
    }

    // Set date and time views
    public void setReminderDateTime(String datetime) {
        mDateAndTimeText.setText(datetime);
    }

    // Set repeat views
    public void setReminderRepeatInfo(String repeat, String repeatNo, String repeatType) {
        if(repeat.equals("true")){
            mRepeatInfoText.setText("Every " + repeatNo + " " + repeatType + "(s)");
        }else if (repeat.equals("false")) {
            mRepeatInfoText.setText("Repeat Off");
        }
    }
}
