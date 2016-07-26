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
    private Calendar date;



    public final Calendar startTime = Calendar.getInstance();
    public final Calendar endTime = Calendar.getInstance();
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

    /*public Item(String mDescription, int mBudget, String mLocation, Calendar mStartTime, Calendar mEndTime, String mDuration) {
        this.mDescription = mDescription;
        this.mBudget = mBudget;
        this.mLocation = mLocation;
        this.mStartTime = mStartTime;
        this.mEndTime = mEndTime;
        this.mDuration = mDuration;
    }*/

    /*public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getmDescription() {
        return mDescription;
    }

    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public int getmBudget() {
        return mBudget;
    }

    public void setmBudget(int mBudget) {
        this.mBudget = mBudget;
    }

    public String getmLocation() {
        return mLocation;
    }

    public void setmLocation(String mLocation) {
        this.mLocation = mLocation;
    }

    public Long getmDuration() {
        return mDuration;
    }

    public void setmDuration(Long mDuration) {
        this.mDuration = mDuration;
    }

    public DateTime getmDate() {
        return mDate;
    }

    public void setmDate(DateTime mDate) {
        this.mDate = mDate;
    }

    public void setmStartTime(DateTime mStartTime) {
        this.mStartTime = mStartTime;
    }

    public void setmEndTime(DateTime mEndTime) {
        this.mEndTime = mEndTime;
    }

    public DateTime getmStartTime() {
        return mStartTime;
    }

    public DateTime getmEndTime() {
        return mEndTime;
    }*/
}