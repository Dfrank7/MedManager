package com.example.dfrank.med_manager2;

/**
 * Created by dfrank on 4/8/18.
 */

public class Medication {
    private int id;
    private String Title;
    private String Description;
    private String time;
    private String startDate;
    private String endDate;
    private String interval;
    private String intervalType;
    private String intervalNo;

    public Medication(){}

    public Medication(int id,String title, String description, String time, String startDate,
                      String endDate, String interval, String intervalType, String intervalNo) {
        this.id = id;
        Title = title;
        Description = description;
        this.time = time;
        this.startDate = startDate;
        this.endDate = endDate;
        this.interval = interval;
        this.intervalType = intervalType;
        this.intervalNo = intervalNo;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return Title;
    }

    public String getDescription() {
        return Description;
    }

    public String getTime() {
        return time;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getInterval() {
        return interval;
    }

    public String getIntervalType() {
        return intervalType;
    }

    public String getIntervalNo() {
        return intervalNo;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public void setIntervalType(String intervalType) {
        this.intervalType = intervalType;
    }

    public void setIntervalNo(String intervalNo) {
        this.intervalNo = intervalNo;
    }
}
