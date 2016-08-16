package ch.supsi.minhhieu.budgetyourtime.Models;


import org.joda.time.DateTimeZone;
import org.joda.time.MutableDateTime;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import ch.supsi.minhhieu.budgetyourtime.Utils.CalendarUtils;


/**
 * Created by acer on 18/07/2016.
 */
public class Item {

    private long id;
    private String description;
    private long budget;
    private String location;
    public MutableDateTime date;
    private String startinghour;
    private String endinghour;

    public MutableDateTime startTime;
    public MutableDateTime endTime;
    private long duration;

    public Item() {
        //startTime.add(Calendar.HOUR_OF_DAY, 1);
        //startTime.set(Calendar.MINUTE, 0);
        //endTime.add(Calendar.HOUR_OF_DAY, 2);
        //endTime.set(Calendar.MINUTE, 0);
    }


    public Item(MutableDateTime mDate, long dateStart, long dateEnd, String mLocation, String mDescription, long mDuration, long mBudget) {
        DateTimeZone timeZone = DateTimeZone.getDefault();
        this.date = new MutableDateTime();
        this.startTime = new MutableDateTime();
        this.endTime = new MutableDateTime();
        this.date = mDate;
        this.startTime.setTime(timeZone.convertUTCToLocal(dateStart));
        this.endTime.setTime(timeZone.convertUTCToLocal(dateEnd));
        this.startinghour = startTime.toString();
        this.endinghour = endTime.toString();
        this.location = mLocation;
        this.description = mDescription;
        this.duration = mDuration;
        this.budget = mBudget;
    }

    public Item(int mId, MutableDateTime mDate, long dateStart, long dateEnd, String mLocation, String mDescription, long mDuration, long mBudget) {
        this.id = mId;
        DateTimeZone timeZone = DateTimeZone.getDefault();
        this.date = new MutableDateTime();
        this.startTime = new MutableDateTime();
        this.endTime = new MutableDateTime();
        this.date = mDate;
        this.startTime.setTime(timeZone.convertUTCToLocal(dateStart));
        this.endTime.setTime(timeZone.convertUTCToLocal(dateEnd));
        this.startinghour = startTime.toString();
        this.endinghour = endTime.toString();
        this.location = mLocation;
        this.description = mDescription;
        this.duration = mDuration;
        this.budget = mBudget;
    }

    public Item(MutableDateTime mDate, long dateStart, long dateEnd, String mLocation, String mDescription,  long mBudget) {
        DateTimeZone timeZone = DateTimeZone.getDefault();
        this.date = new MutableDateTime();
        this.startTime = new MutableDateTime();
        this.endTime = new MutableDateTime();
        this.date = mDate;
        this.startTime.setTime(timeZone.convertUTCToLocal(dateStart));
        this.endTime.setTime(timeZone.convertUTCToLocal(dateEnd));
        this.startinghour = startTime.toString();
        this.endinghour = endTime.toString();
        this.location = mLocation;
        this.description = mDescription;
        this.duration = endTime.getMillis() - startTime.getMillis();
        this.budget = mBudget;
    }

    public long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public long getStartTime() {
        return startTime.getMillis();
    }

    public long getEndTime() {
        return endTime.getMillis();
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

    public MutableDateTime getDate() {
        return date;
    }

    public String getStartinghour() {
        return startinghour;
    }

    public String getEndinghour() {
        return endinghour;
    }
}