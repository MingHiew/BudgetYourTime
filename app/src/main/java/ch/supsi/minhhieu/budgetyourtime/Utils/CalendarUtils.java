package ch.supsi.minhhieu.budgetyourtime.Utils;

import android.content.Context;
import android.text.format.DateUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.MutableDateTime;
import java.text.DateFormat;

/**
 * Created by acer on 25/07/2016.
 */
public class CalendarUtils {

    public static final long NO_TIME_MILLIS = -1;
    public static boolean isNotTime(long timeMillis) {
        return timeMillis == NO_TIME_MILLIS;
    }

    //public static long today() {
        //DateOnlyCalendar calendar = DateOnlyCalendar.today();
        //long timeMillis = calendar.getTimeInMillis();
        //calendar.recycle();
        //return timeMillis;
        //DateTimeZone timeZone = DateTimeZone.getDefault();
    //    MutableDateTime dt = MutableDateTime.now();
    //    return dt.getMillis();

    //}


    public static String toDayString(Context context, long timeMillis) {
        return DateUtils.formatDateTime(context, timeMillis,
                DateUtils.FORMAT_SHOW_WEEKDAY |
                        DateUtils.FORMAT_SHOW_DATE |
                        DateUtils.FORMAT_SHOW_YEAR);
    }


    public static String toTimeString(Context context, long timeMillis) {
        return DateUtils.formatDateTime(context, timeMillis, DateUtils.FORMAT_SHOW_TIME);
    }

    public static String millisToDate(long millis){

        return DateFormat.getDateInstance(DateFormat.AM_PM_FIELD).format(millis);
        //return DateFormat.getDateInstance(DateFormat.TIMEZONE_FIELD).format(millis);
        //You can use DateFormat.LONG instead of SHORT

    }

    public static long getNearestHourAndMinutes(){
        //Date whateverDateYouWant = new Date();
        //Calendar calendar = Calendar.getInstance();
        //calendar.setTime(whateverDateYouWant);

        //DateTimeZone timeZone = DateTimeZone.getDefault();
        MutableDateTime dt = new MutableDateTime();

        int rawMinutes = dt.getMinuteOfHour();
        int modular = rawMinutes % 30;
        dt.addMinutes(modular < 16 ? -modular : (30 - modular));

        return dt.getMillis();
        //int unroundedMinutes = calendar.get(Calendar.MINUTE);
        //int mod = unroundedMinutes % 30;
        //calendar.add(Calendar.MINUTE, mod < 16 ? -mod : (30-mod));
        //return calendar.getTimeInMillis();
    }

    public static MutableDateTime startOfDay(MutableDateTime dt) {
        dt.setHourOfDay(0);
        dt.setMinuteOfHour(0);
        dt.setSecondOfMinute(0);
        dt.setMillisOfSecond(0);
        //c.set(Calendar.HOUR_OF_DAY, 0);
        //c.set(Calendar.MINUTE, 0);
        //c.set(Calendar.SECOND, 0);
        //c.set(Calendar.MILLISECOND, 0);
        return dt;
    }

    public static MutableDateTime endOfDay(MutableDateTime dt) {
        dt.setHourOfDay(23);
        dt.setMinuteOfHour(59);
        dt.setSecondOfMinute(59);
        dt.setMillisOfSecond(999);
        //c.set(Calendar.HOUR_OF_DAY, 23);
        //c.set(Calendar.MINUTE, 59);
        //c.set(Calendar.SECOND, 59);
        //c.set(Calendar.MILLISECOND, 999);
        return dt;
    }
    public static DateFormat getShortDateFormat(Context context) {
        return android.text.format.DateFormat.getDateFormat(context);
    }

    /*private static class DateOnlyCalendar extends GregorianCalendar {

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
    }*/
}
