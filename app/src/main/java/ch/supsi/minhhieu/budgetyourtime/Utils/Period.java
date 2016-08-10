package ch.supsi.minhhieu.budgetyourtime.Utils;

/**
 * Created by acer on 09/08/2016.
 */
public class Period {
    public PeriodType type;
    public long start;
    public long end;

    public Period(PeriodType type, long start, long end) {
        this.type = type;
        this.start = start;
        this.end = end;
    }

    public boolean isSame(long start, long end) {
        return this.start == start && this.end == end;
    }

}
