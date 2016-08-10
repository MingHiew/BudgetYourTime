package ch.supsi.minhhieu.budgetyourtime.Utils;

import android.content.Context;
import android.support.v4.util.Pools;
import android.text.format.DateUtils;
import android.provider.Settings;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.text.DateFormat;

/**
 * Created by acer on 25/07/2016.
 */
public class CalendarUtils {

    public static final long NO_TIME_MILLIS = -1;
    public static final String TIMEZONE_UTC = "UTC";
    public static final String PREF_WEEK_START = "weekStart";
    public static final String PREF_CALENDAR_EXCLUSIONS = "calendarExclusions";

    public static int sWeekStart = Calendar.SUNDAY;

    public static boolean isNotTime(long timeMillis) {
        return timeMillis == NO_TIME_MILLIS;
    }


    public static long today() {
        DateOnlyCalendar calendar = DateOnlyCalendar.today();
        long timeMillis = calendar.getTimeInMillis();
        calendar.recycle();
        return timeMillis;
    }


    public static String toDayString(Context context, long timeMillis) {
        return DateUtils.formatDateTime(context, timeMillis,
                DateUtils.FORMAT_SHOW_WEEKDAY |
                        DateUtils.FORMAT_SHOW_DATE |
                        DateUtils.FORMAT_SHOW_YEAR);
    }


    public static String toTimeString(Context context, long timeMillis) {
        return DateUtils.formatDateTime(context, timeMillis, DateUtils.FORMAT_SHOW_TIME);
    }

    public static long getNearestHourAndMinutes(){
        Date whateverDateYouWant = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(whateverDateYouWant);

        int unroundedMinutes = calendar.get(Calendar.MINUTE);
        int mod = unroundedMinutes % 30;
        calendar.add(Calendar.MINUTE, mod < 16 ? -mod : (30-mod));
        return calendar.getTimeInMillis();
    }

    public static Calendar startOfDay(Calendar c) {
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c;
    }

    public static Calendar endOfDay(Calendar c) {
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        return c;
    }
    public static DateFormat getShortDateFormat(Context context) {
        return android.text.format.DateFormat.getDateFormat(context);
    }

    public static DateFormat getLongDateFormat(Context context) {
        return android.text.format.DateFormat.getLongDateFormat(context);
    }

    public static DateFormat getMediumDateFormat(Context context) {
        return android.text.format.DateFormat.getMediumDateFormat(context);
    }

    public static DateFormat getTimeFormat(Context context) {
        return android.text.format.DateFormat.getTimeFormat(context);
    }

    public static boolean is24HourFormat(Context context) {
        return "24".equals(Settings.System.getString(context.getContentResolver(), Settings.System.TIME_12_24));
    }

    public static void zeroSeconds(Calendar dateTime) {
        dateTime.set(Calendar.SECOND, 0);
        dateTime.set(Calendar.MILLISECOND, 0);
    }
    @SuppressWarnings("ConstantConditions")
    public static boolean sameMonth(long first, long second) {
        if (isNotTime(first) || isNotTime(second)) {
            return false; // not comparable
        }
        DateOnlyCalendar firstCalendar = DateOnlyCalendar.fromTime(first);
        DateOnlyCalendar secondCalendar = DateOnlyCalendar.fromTime(second);
        boolean same = firstCalendar.sameMonth(secondCalendar);
        firstCalendar.recycle();
        secondCalendar.recycle();
        return same;
    }

    public static long toLocalTimeZone(long utcTimeMillis) {
        return convertTimeZone(TimeZone.getTimeZone(TIMEZONE_UTC), TimeZone.getDefault(),
                utcTimeMillis);
    }

    public static long toUtcTimeZone(long localTimeMillis) {
        return convertTimeZone(TimeZone.getDefault(), TimeZone.getTimeZone(TIMEZONE_UTC),
                localTimeMillis);
    }

    private static long convertTimeZone(TimeZone fromTimeZone, TimeZone toTimeZone, long timeMillis) {
        DateOnlyCalendar fromCalendar = DateOnlyCalendar.obtain();
        fromCalendar.setTimeZone(fromTimeZone);
        fromCalendar.setTimeInMillis(timeMillis);
        DateOnlyCalendar toCalendar = DateOnlyCalendar.obtain();
        toCalendar.setTimeZone(toTimeZone);
        toCalendar.set(fromCalendar.get(Calendar.YEAR),
                fromCalendar.get(Calendar.MONTH),
                fromCalendar.get(Calendar.DAY_OF_MONTH),
                fromCalendar.get(Calendar.HOUR_OF_DAY),
                fromCalendar.get(Calendar.MINUTE),
                fromCalendar.get(Calendar.SECOND));
        long localTimeMillis = toCalendar.getTimeInMillis();
        fromCalendar.recycle();
        toCalendar.recycle();
        return localTimeMillis;

    }

    private static class DateOnlyCalendar extends GregorianCalendar {

        private static Pools.SimplePool<DateOnlyCalendar> sPools = new Pools.SimplePool<>(5);

        private static DateOnlyCalendar obtain() {
            DateOnlyCalendar instance = sPools.acquire();
            return instance == null ? new DateOnlyCalendar() : instance;
        }

        public static DateOnlyCalendar today() {
            return fromTime(System.currentTimeMillis());
        }

        public static DateOnlyCalendar fromTime(long timeMillis) {
            if (timeMillis < 0) {
                return null;
            }
            DateOnlyCalendar dateOnlyCalendar = DateOnlyCalendar.obtain();
            dateOnlyCalendar.setTimeInMillis(timeMillis);
            dateOnlyCalendar.stripTime();
            //noinspection WrongConstant
            dateOnlyCalendar.setFirstDayOfWeek(sWeekStart);
            return dateOnlyCalendar;
        }

        private DateOnlyCalendar() {
            super();
        }

        public boolean monthBefore(DateOnlyCalendar other) {
            int day = other.get(DAY_OF_MONTH);
            other.set(DAY_OF_MONTH, 1);
            boolean before = getTimeInMillis() < other.getTimeInMillis();
            other.set(DAY_OF_MONTH, day);
            return before;
        }

        public boolean monthAfter(DateOnlyCalendar other) {
            int day = other.get(DAY_OF_MONTH);
            other.set(DAY_OF_MONTH, other.getActualMaximum(DAY_OF_MONTH));
            boolean after = getTimeInMillis() > other.getTimeInMillis();
            other.set(DAY_OF_MONTH, day);
            return after;
        }

        public boolean sameMonth(DateOnlyCalendar other) {
            return get(YEAR) == other.get(YEAR) && get(MONTH) == other.get(MONTH);
        }

        void stripTime() {
            set(Calendar.HOUR_OF_DAY, 0);
            set(Calendar.MINUTE, 0);
            set(Calendar.SECOND, 0);
            set(Calendar.MILLISECOND, 0);
        }

        void recycle() {
            setTimeZone(TimeZone.getDefault());
            sPools.release(this);
        }
    }
}
