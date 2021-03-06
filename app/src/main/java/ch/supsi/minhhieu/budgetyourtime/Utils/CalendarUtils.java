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

    public static String toDayString(Context context, long timeMillis) {
        return DateUtils.formatDateTime(context, timeMillis,
                DateUtils.FORMAT_SHOW_WEEKDAY |
                        DateUtils.FORMAT_SHOW_DATE |
                        DateUtils.FORMAT_ABBREV_MONTH |
                        DateUtils.FORMAT_SHOW_YEAR);
    }

    public static String toDayStringAbbrev(Context context, long timeMillis) {
        return DateUtils.formatDateTime(context, timeMillis,DateUtils.FORMAT_SHOW_WEEKDAY |
                                                            DateUtils.FORMAT_SHOW_DATE |
                                                            DateUtils.FORMAT_ABBREV_MONTH);
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
        MutableDateTime dt = new MutableDateTime();

        int rawMinutes = dt.getMinuteOfHour();
        int modular = rawMinutes % 60;
        dt.addMinutes(modular < 31 ? -modular : (60 - modular));

        return dt.getMillis();
    }

    public static MutableDateTime today() {
        MutableDateTime dt = MutableDateTime.now();
        dt.setHourOfDay(0);
        dt.setMinuteOfHour(0);
        dt.setSecondOfMinute(0);
        dt.setMillisOfSecond(0);
        return dt;
    }

    public static MutableDateTime startOfDay(MutableDateTime dt) {
        dt.setHourOfDay(0);
        dt.setMinuteOfHour(0);
        dt.setSecondOfMinute(0);
        dt.setMillisOfSecond(0);
        return dt;
    }

    public static MutableDateTime endOfDay(MutableDateTime dt) {
        dt.setHourOfDay(23);
        dt.setMinuteOfHour(59);
        dt.setSecondOfMinute(59);
        dt.setMillisOfSecond(999);

        return dt;
    }

    public static DateFormat getShortDateFormat(Context context) {
        return android.text.format.DateFormat.getDateFormat(context);
    }

}
