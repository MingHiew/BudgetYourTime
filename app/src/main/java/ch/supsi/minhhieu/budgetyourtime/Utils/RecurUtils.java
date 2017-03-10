package ch.supsi.minhhieu.budgetyourtime.Utils;

import android.content.Context;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;
import org.joda.time.MutableDateTime;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import ch.supsi.minhhieu.budgetyourtime.R;

/**
 * Created by acer on 09/08/2016.
 */
public class RecurUtils {

    private static final long DAY_IN_MS = 24*60*60*1000L;

    public enum RecurInterval implements LocalizableEnum {
        WEEKLY(R.string.recur_interval_weekly){
            @Override
            public Period next(long startDate) {
                long endDate = 0;
                //Calendar c = Calendar.getInstance();
                //c.setFirstDayOfWeek(Calendar.MONDAY);
                //c.setTimeInMillis(startDate);
                //DateTimeZone timeZone = DateTimeZone.getDefault();
                MutableDateTime dt = new MutableDateTime();
                dt.setMillis(startDate);
                //startDate = CalendarUtils.startOfDay(c).getTimeInMillis();
                //c.add(Calendar.DAY_OF_MONTH, 6);
                //endDate = CalendarUtils.endOfDay(c).getTimeInMillis();
                startDate = CalendarUtils.startOfDay(dt).getMillis();
                dt.addDays(6);
                endDate = CalendarUtils.endOfDay(dt).getMillis();
                return new Period(PeriodType.CUSTOM, startDate, endDate);
            }
        },
        MONTHLY(R.string.recur_interval_monthly){
            @Override
            public Period next(long startDate) {
                //Calendar c = Calendar.getInstance();
                //c.setTimeInMillis(startDate);
                //startDate = CalendarUtils.startOfDay(c).getTimeInMillis();
                //c.add(Calendar.MONTH, 1);
                //c.add(Calendar.DAY_OF_MONTH, -1);
                //long endDate = CalendarUtils.endOfDay(c).getTimeInMillis();
                //DateTimeZone timeZone = DateTimeZone.getDefault();
                MutableDateTime dt = new MutableDateTime();
                dt.setMillis(startDate);
                startDate = CalendarUtils.startOfDay(dt).getMillis();
                dt.addMonths(1);
                dt.addDays(-1);
                long endDate = CalendarUtils.endOfDay(dt).getMillis();
                return new Period(PeriodType.CUSTOM, startDate, endDate);
            }
        };

        private final int titleId;

        RecurInterval(int titleId) {
            this.titleId = titleId;
        }

        @Override
        public int getTitleId() {
            return titleId;
        }

        public Period next(long startDate) {
            throw new UnsupportedOperationException();
        }
    }

    public enum RecurPeriod implements LocalizableEnum {
        EXACTLY_TIMES(R.string.recur_exactly_n_times){
            @Override
            public String toSummary(Context context, long param) {
                return String.format(context.getString(R.string.recur_exactly_n_times_summary), param);
            }
            @Override
            public Period[] repeat(RecurInterval interval, long startDate, long periodParam) {
                LinkedList<Period> periods = new LinkedList<>();
                while (periodParam-- > 0) {
                    Period p = interval.next(startDate);
                    startDate = p.end+1;
                    periods.add(p);
                }
                return periods.toArray(new Period[periods.size()]);
            }
        };

        private final int titleId;

        RecurPeriod(int titleId) {

            this.titleId = titleId;
        }


        @Override
        public int getTitleId() {
            return titleId;
        }

        public abstract String toSummary(Context context, long param);

        public abstract Period[] repeat(RecurInterval interval, long startDate, long periodParam);

    }

    public static abstract class Recur implements Cloneable {

        public final RecurInterval interval;
        public long startDate;
        public RecurPeriod period;
        public long periodParam;

        protected Recur(RecurInterval interval, HashMap<String, String> values) {
            this.interval = interval;
            switch (interval){
                case WEEKLY:
                    //Calendar c = Calendar.getInstance();
                    //c.setFirstDayOfWeek(Calendar.MONDAY);
                    //c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                    //DateTimeZone timeZone = DateTimeZone.getDefault();
                    MutableDateTime dt = new MutableDateTime();
                    dt.setDayOfWeek(DateTimeConstants.MONDAY);
                    //this.startDate = c.getTimeInMillis();
                    this.startDate = dt.getMillis();
                    break;
                case MONTHLY:
                    //Calendar d = Calendar.getInstance();
                    //d.set(Calendar.DAY_OF_MONTH, 1);
                    //this.startDate = d.getTimeInMillis();
                    //DateTimeZone timeZone1 = DateTimeZone.getDefault();
                    MutableDateTime dt1 = new MutableDateTime();
                    dt1.setDayOfMonth(1);
                    this.startDate = dt1.getMillis();
                    break;
            }
            this.startDate = getLong(values, "startDate");
            this.period = RecurPeriod.EXACTLY_TIMES;
            this.periodParam = 400;
        }

        public Recur(RecurInterval interval) {
            this.interval = interval;
            switch (interval){
                case WEEKLY:
                    //Calendar c = Calendar.getInstance();
                    //c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                    //this.startDate = c.getTimeInMillis();
                    //DateTimeZone timeZone = DateTimeZone.getDefault();
                    MutableDateTime dt = new MutableDateTime();
                    dt.setDayOfWeek(DateTimeConstants.MONDAY);
                    this.startDate = dt.getMillis();
                    break;
                case MONTHLY:
                    //Calendar d = Calendar.getInstance();
                    //d.set(Calendar.DAY_OF_MONTH, 1);
                    //this.startDate = d.getTimeInMillis();
                    //DateTimeZone timeZone1 = DateTimeZone.getDefault();
                    MutableDateTime dt1 = new MutableDateTime();
                    dt1.setDayOfMonth(1);
                    this.startDate = dt1.getMillis();
                    break;
            }
            this.period = RecurPeriod.EXACTLY_TIMES;
            this.periodParam = 400;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(interval.name()).append(",");
            sb.append("startDate=").append(startDate).append(",");
            sb.append("period=").append(period.name()).append(",");
            sb.append("periodParam=").append(periodParam).append(",");
            toString(sb);
            return sb.toString();
        }

        protected void toString(StringBuilder sb) {
            // do nothing
        }

        @Override
        public Recur clone() {
            try {
                return (Recur)super.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }

        public String toString(Context context) {
            DateFormat df = CalendarUtils.getShortDateFormat(context);
            return context.getString(R.string.recur_repeat_starts_on) + " " + df.format(new Date(startDate)) + ", " + context.getString(interval.titleId) + ", " + period.toSummary(context, periodParam);
        }

    }

    public static class Weekly extends Recur {

        //private final EnumSet<DayOfWeek> days;

        protected Weekly(HashMap<String, String> values) {
            super(RecurInterval.WEEKLY, values);
        }

        public Weekly() {
            super(RecurInterval.WEEKLY);
        }
    }

    public static class Monthly extends Recur {

        protected Monthly(HashMap<String, String> values) {
            super(RecurInterval.MONTHLY, values);
        }

        public Monthly() {
            super(RecurInterval.MONTHLY);
        }

    }

    public static Recur createFromExtraString(String extra) {
        String[] a = extra.split(",");
        RecurInterval interval = RecurInterval.valueOf(a[0]);
        HashMap<String, String> values = Utils.toMap(a);
        switch (interval) {
            case WEEKLY:
                return new Weekly(values);
            case MONTHLY:
                return new Monthly(values);
        }
        return null;
    }

    public static Recur createRecur(RecurInterval interval) {

        switch (interval) {
            case WEEKLY:
                return new Weekly();
            case MONTHLY:
                return new Monthly();
        }
        return null;
    }



    private static long getLong(HashMap<String, String> values, String string) {
        return Long.parseLong(values.get(string));
    }

    private static int getInt(HashMap<String, String> values, String string) {
        return Integer.parseInt(values.get(string));
    }

    public static Period[] periods(Recur recur) {
        RecurInterval interval = recur.interval;
        RecurPeriod period = recur.period;
        return period.repeat(interval, recur.startDate, recur.periodParam);

    }

}
