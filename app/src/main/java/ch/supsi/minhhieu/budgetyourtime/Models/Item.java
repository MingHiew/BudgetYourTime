package ch.supsi.minhhieu.budgetyourtime.Models;


import java.util.Calendar;


/**
 * Created by acer on 18/07/2016.
 */
public class Item {

    private int id;
    private String description;
    private int budget;
    private String location;
    public Calendar date;



    public Calendar startTime = Calendar.getInstance();
    public Calendar endTime = Calendar.getInstance();
    private long duration;

    public Item() {
        startTime.add(Calendar.HOUR_OF_DAY, 1);
        startTime.set(Calendar.MINUTE, 0);
        endTime.add(Calendar.HOUR_OF_DAY, 2);
        endTime.set(Calendar.MINUTE, 0);
    }


    public Item(int id, Calendar mDate, long dateStart, long dateEnd, String mLocation, String mDescription, long mDuration, int mBudget) {
        this.id = id;
        this.date = mDate;
        this.startTime.setTimeInMillis(dateStart);
        this.endTime.setTimeInMillis(dateEnd);
        this.location = mLocation;
        this.description = mDescription;
        this.duration = mDuration;
        this.budget = mBudget;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public long getStartTime() {
        return startTime.getTimeInMillis();
    }

    public long getEndTime() {
        return endTime.getTimeInMillis();
    }

    public int getBudget() {
        return budget;
    }

    public String getLocation() {
        return location;
    }

    public long getDuration() {
        return duration;
    }

    public Calendar getDate() {
        return date;
    }

    public void setStartTime(Calendar startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(Calendar endTime) {
        this.endTime = endTime;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }
}