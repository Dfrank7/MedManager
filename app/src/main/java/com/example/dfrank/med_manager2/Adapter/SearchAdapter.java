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

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.dfrank.med_manager2.Medication;
import com.example.dfrank.med_manager2.R;
import com.example.dfrank.med_manager2.activities.AddMedication;
import com.example.dfrank.med_manager2.data.MedManagerContract;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by dfrank on 4/15/18.
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.viewHolder> {
    TextDrawable textDrawable;
    List<Medication> medications;
    Context context;
    Cursor cursor;
    private ColorGenerator mColorGenerator = ColorGenerator.DEFAULT;
    public SearchAdapter(List<Medication> medications, Context context){
        this.context = context;
        this.medications = medications;
    }
    @Override
    public SearchAdapter.viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.meditem, parent, false);

        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchAdapter.viewHolder holder, int position) {

        String title = medications.get(position).getTitle();
        String interval = medications.get(position).getInterval();
        String intervalNo = medications.get(position).getIntervalNo();
        String intervalType = medications.get(position).getIntervalType();
        String startDate = medications.get(position).getStartDate();
        String endDate = medications.get(position).getEndDate();
        setReminderTitle(title,holder.CircularImage);
        holder.MedTitle.setText(title);
        String date = startDate+ " - "+ endDate;
        holder.MedDate.setText(date);
        setInterval(interval, intervalNo,intervalType,holder.MedInterval);
    }

    @Override
    public int getItemCount() {
        return medications.size();
    }

    public void setFilter(List<Medication> newList){
        medications = new ArrayList<>();
        medications.addAll(newList);
        notifyDataSetChanged();
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
        @BindView(R.id.thumbnail_image)
        ImageView CircularImage;
        @BindView(R.id.recycle_title)
        TextView MedTitle;
        @BindView(R.id.recycle_date)
        TextView MedDate;
        @BindView(R.id.recycle_repeat_info)
        TextView MedInterval;

        public viewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position!=RecyclerView.NO_POSITION){
                        int hid = medications.get(position).getId();
                        Uri currentUri = ContentUris.withAppendedId(MedManagerContract.MedManagerEntry.CONTENT_URI,hid);
//                        Toast.makeText(view.getContext(), currentUri.toString(),Toast.LENGTH_SHORT).show();
//                        Toast.makeText(view.getContext(), String.valueOf(id), Toast.LENGTH_SHORT).show();
                        //medications.clear();
                        Intent intent = new Intent(view.getContext(), AddMedication.class)
                                .setData(currentUri);
                        view.getContext().startActivity(intent);
                    }
                }
            });

        }
    }
}
