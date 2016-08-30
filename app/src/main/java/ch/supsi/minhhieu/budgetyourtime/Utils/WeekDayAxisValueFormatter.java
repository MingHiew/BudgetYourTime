package ch.supsi.minhhieu.budgetyourtime.Utils;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.AxisValueFormatter;

/**
 * Created by acer on 24/08/2016.
 */
public class WeekDayAxisValueFormatter implements AxisValueFormatter {

    protected String[] mWeekDays = new String[]{
            "","Mon","Tue","Wed","Thu","Fri","Sat","Sun",""
    };

    private BarLineChartBase<?> chart;

    public WeekDayAxisValueFormatter(BarLineChartBase<?> chart) {
        this.chart = chart;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return mWeekDays[(int)value];
    }

    @Override
    public int getDecimalDigits() {
        return 0;
    }
}
