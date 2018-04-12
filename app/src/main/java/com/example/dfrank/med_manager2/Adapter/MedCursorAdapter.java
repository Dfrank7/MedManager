package com.example.dfrank.med_manager2.Adapter;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.dfrank.med_manager2.Medication;
import com.example.dfrank.med_manager2.R;
import com.example.dfrank.med_manager2.activities.AddMedication;
import com.example.dfrank.med_manager2.data.MedManagerContract;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by dfrank on 4/6/18.
 */

public class MedCursorAdapter extends RecyclerView.Adapter<MedCursorAdapter.viewHolder> {

    TextDrawable textDrawable;
    private int idIndex;
    ArrayList<Medication> medications = new ArrayList<>();
    int id;
    private ColorGenerator mColorGenerator = ColorGenerator.DEFAULT;

    private Cursor cursor;
    private Context context;
    public MedCursorAdapter(Context context){
        this.context = context;
    }

    @Override
    public MedCursorAdapter.viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.meditem, parent, false);

        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(MedCursorAdapter.viewHolder holder, int position) {
        idIndex = cursor.getColumnIndex(MedManagerContract.MedManagerEntry._ID);
        int titleIndex = cursor.getColumnIndex(MedManagerContract.MedManagerEntry.COLUMN_TITLE);
        int descriptionIndex = cursor.getColumnIndex(MedManagerContract.MedManagerEntry.COLUMN_DESCRIPTION);
        int intervalIndex = cursor.getColumnIndex(MedManagerContract.MedManagerEntry.COLUMN_INTERVAL);
        int intervalNoIndex = cursor.getColumnIndex(MedManagerContract.MedManagerEntry.COLUMN_INTERVAL_NO);
        int startDateIndex =cursor.getColumnIndex(MedManagerContract.MedManagerEntry.COLUMN_START_DATE);
        int endDateIndex = cursor.getColumnIndex(MedManagerContract.MedManagerEntry.COLUMN_END_DATE);
        int intervalTypeIndex = cursor.getColumnIndex(MedManagerContract.MedManagerEntry.COLUMN_INTERVAL_TYPE);

        cursor.moveToPosition(position);

        id = cursor.getInt(idIndex);
        String title = cursor.getString(titleIndex);
        String description = cursor.getString(descriptionIndex);
        String startDate = cursor.getString(startDateIndex);
        String endDate = cursor.getString(endDateIndex);
        String interval = cursor.getString(intervalIndex);
        String intervalNo = cursor.getString(intervalNoIndex);
        String intervalType = cursor.getString(intervalTypeIndex);

        medications.add(new Medication(title, description,startDate,endDate,interval,intervalNo,
                intervalType));

        String date = startDate+ " - "+ endDate;

        holder.itemView.setTag(id);

        holder.MedTitle.setText(title);
        holder.MedDate.setText(date);
        setReminderTitle(title, holder.CircularImage);
        setInterval(interval, intervalNo,intervalType,holder.MedInterval);

    }

    public int getId() {
        return id;
    }

    @Override
    public int getItemCount() {
        if (cursor == null) {
            return 0;
        }
        return cursor.getCount();
    }

    public Cursor swapCursor(Cursor mcursor){
        if (cursor==mcursor){
            return null;
        }
        Cursor temp = cursor;
        this.cursor = mcursor;

        if (mcursor!=null){
            notifyDataSetChanged();
        }
        return temp;
    }

    public void setReminderTitle(String title, ImageView imageView) {
        String letter = "A";

        if(title != null && !title.isEmpty()) {
            letter = title.substring(0, 1);
        }

        int color = mColorGenerator.getRandomColor();

        // Create a circular icon consisting of  a random background colour and first letter of title
        textDrawable = TextDrawable.builder()
                .buildRound(letter, color);
        imageView.setImageDrawable(textDrawable);
    }


    private void setInterval(String interval, String intervalNo, String intervalType, TextView text){
        if (interval.equals("true")){
            text.setText("Every " + intervalNo + " " + intervalType + "(s)");
        }else {
            text.setText("Reapeat off");
        }
    }


    public class viewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.thumbnail_image) ImageView CircularImage;
        @BindView(R.id.recycle_title) TextView MedTitle;
        @BindView(R.id.recycle_date) TextView MedDate;
        @BindView(R.id.recycle_repeat_info) TextView MedInterval;
        public viewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position!=RecyclerView.NO_POSITION){
                        int hid = cursor.getColumnIndex(MedManagerContract.MedManagerEntry._ID);
                        cursor.moveToPosition(position);
                        int id = cursor.getInt(hid);
                        Uri currentUri = ContentUris.withAppendedId(MedManagerContract.MedManagerEntry.CONTENT_URI,id);
//                        Toast.makeText(view.getContext(), currentUri.toString(),Toast.LENGTH_SHORT).show();
//                        Toast.makeText(view.getContext(), String.valueOf(id), Toast.LENGTH_SHORT).show();
                        //medications.clear();
                        Intent intent = new Intent(view.getContext(), AddMedication.class)
//                                .putExtra("id", id)
//                                .putExtra("title", medications.get(position).getTitle())
//                                .putExtra("description", medications.get(position).getDescription())
//                                .putExtra("startDate", medications.get(position).getStartDate())
//                                .putExtra("endDate", medications.get(position).getEndDate())
//                                .putExtra("interval", medications.get(position).getInterval())
//                                .putExtra("intervalNo", medications.get(position).getIntervalNo())
//                                .putExtra("intervalType",medications.get(position).getIntervalType())
                                .setData(currentUri);
                        view.getContext().startActivity(intent);
                    }
                }
            });
        }
    }


}
