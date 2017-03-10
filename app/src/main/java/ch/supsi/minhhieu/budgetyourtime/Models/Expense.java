package ch.supsi.minhhieu.budgetyourtime.Models;


import org.joda.time.DateTimeZone;
import org.joda.time.MutableDateTime;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import ch.supsi.minhhieu.budgetyourtime.Utils.CalendarUtils;


/**
 * Created by acer on 18/07/2016.
 */
public class Expense {

    private long id;
    private String description;
    private long budget;
    private String location;
    public MutableDateTime date;
    public MutableDateTime startTime;
    public MutableDateTime endTime;
    private long duration;
    public int weatherID;
    public String locality;
    public Expense() {
    }


    public Expense(MutableDateTime mDate, long dateStart, long dateEnd, String mLocation, String mDescription, long mBudget, int mWeatherID, String mLocality) {
        DateTimeZone timeZone = DateTimeZone.getDefault();
        this.date = new MutableDateTime();
        this.startTime = new MutableDateTime();
        this.endTime = new MutableDateTime();
        this.date = mDate;
        this.startTime.setTime(timeZone.convertUTCToLocal(dateStart));
        this.endTime.setTime(timeZone.convertUTCToLocal(dateEnd));
        this.location = mLocation;
        this.description = mDescription;
        if(endTime.getHourOfDay() == 0){
            this.duration = 24 - startTime.getHourOfDay();
        } else {
            this.duration = endTime.getHourOfDay() - startTime.getHourOfDay();
        }
        this.budget = mBudget;
        this.weatherID = mWeatherID;
        this.locality = mLocality;
    }

    public Expense(long mId, MutableDateTime mDate, long dateStart, long dateEnd, String mLocation, String mDescription, long mBudget, int mWeatherID, String mLocality) {
        this.id = mId;
        DateTimeZone timeZone = DateTimeZone.getDefault();
        this.date = new MutableDateTime();
        this.startTime = new MutableDateTime();
        this.endTime = new MutableDateTime();
        this.date = mDate;
        this.startTime.setTime(timeZone.convertUTCToLocal(dateStart));
        this.endTime.setTime(timeZone.convertUTCToLocal(dateEnd));
        this.location = mLocation;
        this.description = mDescription;
        if(endTime.getHourOfDay() == 0){
            this.duration = 24 - startTime.getHourOfDay();
        } else {
            this.duration = endTime.getHourOfDay() - startTime.getHourOfDay();
        }
        this.budget = mBudget;
        this.weatherID = mWeatherID;
        this.locality = mLocality;
    }

    public Expense(long mId, MutableDateTime mDate, long dateStart, long dateEnd, String mLocation, String mDescription, long duration, long mBudget, int mWeatherID, String mLocality) {
        this.id = mId;
        DateTimeZone timeZone = DateTimeZone.getDefault();
        this.date = new MutableDateTime();
        this.startTime = new MutableDateTime();
        this.endTime = new MutableDateTime();
        this.date = mDate;
        this.startTime.setTime(timeZone.convertUTCToLocal(dateStart));
        this.endTime.setTime(timeZone.convertUTCToLocal(dateEnd));
        this.location = mLocation;
        this.description = mDescription;
        this.duration = duration;
        this.budget = mBudget;
        this.weatherID = mWeatherID;
        this.locality = mLocality;
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

    public int getWeatherID() {
        return weatherID;
    }

    public String getLocality() {
        return locality;
    }
}