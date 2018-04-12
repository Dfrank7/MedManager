package com.example.dfrank.med_manager2;

/**
 * Created by dfrank on 4/8/18.
 */

public class Medication {
    private String Title;
    private String Description;
    private String startDate;
    private String endDate;
    private String interval;
    private String intervalType;
    private String intervalNo;

    public Medication(String title, String description, String startDate, String endDate,
                      String interval, String intervalType, String intervalNo) {
        Title = title;
        Description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.interval = interval;
        this.intervalType = intervalType;
        this.intervalNo = intervalNo;
    }

    public String getTitle() {
        return Title;
    }

    public String getDescription() {
        return Description;
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
}
