package ch.supsi.minhhieu.budgetyourtime.Models;


import java.util.Calendar;
import java.util.concurrent.TimeUnit;


/**
 * Created by acer on 18/07/2016.
 */
public class Item {

    private long id;
    private String description;
    private long budget;
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


    public Item(Calendar mDate, long dateStart, long dateEnd, String mLocation, String mDescription, long mDuration, long mBudget) {
        this.date = mDate;
        this.startTime.setTimeInMillis(dateStart);
        this.endTime.setTimeInMillis(dateEnd);
        this.location = mLocation;
        this.description = mDescription;
        this.duration = mDuration;
        this.budget = mBudget;
    }

    public Item(int mId, Calendar mDate, long dateStart, long dateEnd, String mLocation, String mDescription, long mDuration, long mBudget) {
        this.id = mId;
        this.date = mDate;
        this.startTime.setTimeInMillis(dateStart);
        this.endTime.setTimeInMillis(dateEnd);
        this.location = mLocation;
        this.description = mDescription;
        this.duration = mDuration;
        this.budget = mBudget;
    }

    public Item(Calendar mDate, long dateStart, long dateEnd, String mLocation, String mDescription,  long mBudget) {
        this.date = mDate;
        this.startTime.setTimeInMillis(dateStart);
        this.endTime.setTimeInMillis(dateEnd);
        this.location = mLocation;
        this.description = mDescription;
        this.duration = endTime.getTimeInMillis() - startTime.getTimeInMillis();
        this.budget = mBudget;
    }

    public long getId() {
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

    public long getBudget() {
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

    }