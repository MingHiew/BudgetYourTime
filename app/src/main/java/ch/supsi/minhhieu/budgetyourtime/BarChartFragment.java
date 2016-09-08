package ch.supsi.minhhieu.budgetyourtime;


import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ch.supsi.minhhieu.budgetyourtime.Helpers.DBHelper;
import ch.supsi.minhhieu.budgetyourtime.Models.Budget;
import ch.supsi.minhhieu.budgetyourtime.Utils.WeekDayAxisValueFormatter;

/**
 * A simple {@link Fragment} subclass.
 */
public class BarChartFragment extends Fragment {

    @BindView(R.id.barchart1)
    BarChart barChart;

    private Typeface mTfLight;
    private Typeface mTfRegular;
    private Typeface mTfXBItalic;
    private DBHelper db;
    private String[] chartType = new String[]{"Piechart: Consumption by Budget",
            "Barchart: Weekday Overview","Barchart: Weekday Overview by Location"};

    public BarChartFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = DBHelper.getInstance(this.getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_barchart, container, false);
        ButterKnife.bind(this,view);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setHasOptionsMenu(true);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        mTfLight = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Light.ttf");
        mTfRegular = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Regular.ttf");
        mTfXBItalic = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-ExtraBoldItalic.ttf");

        actionBar.setTitle("Chart: Weekday Overview");

        barChart.setDescription("");
        barChart.setMaxVisibleValueCount(40);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(false);
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(false);
        barChart.setHighlightFullBarEnabled(false);

        AxisValueFormatter xAxisFormatter = new WeekDayAxisValueFormatter(barChart);

        XAxis xl = barChart.getXAxis();
        xl.setPosition(XAxisPosition.BOTTOM);
        xl.setTypeface(mTfXBItalic);
        xl.setTextColor(R.color.green);
        xl.setDrawAxisLine(true);
        xl.setDrawGridLines(true);
        xl.setCenterAxisLabels(false);
        xl.setGranularity(1f);
        xl.setLabelCount(7);
        xl.setAxisMinValue(0);
        xl.setAxisMaxValue(8);
        xl.setValueFormatter(xAxisFormatter);

        YAxis yl = barChart.getAxisLeft();
        yl.setTypeface(mTfLight);
        yl.setDrawAxisLine(true);
        yl.setDrawGridLines(true);
        yl.setAxisMinValue(0f);

        YAxis yr = barChart.getAxisRight();
        yr.setTypeface(mTfLight);
        yr.setDrawAxisLine(true);
        yr.setDrawGridLines(false);
        yr.setAxisMinValue(0f);

        Legend l = barChart.getLegend();
        l.setTextColor(R.color.black);
        l.setPosition(LegendPosition.ABOVE_CHART_CENTER);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setFormSize(10f);
        l.setTextSize(12f);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        setData();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.chart_type, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_chart_type:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Choose Chart").setItems(chartType, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                        switch (which){
                            case 0:
                                PieChartFragment piechartFragment = new PieChartFragment();
                                fragmentTransaction.replace(R.id.fragment_container,piechartFragment);
                                fragmentTransaction.commit();
                                break;
                            case 1:
                                BarChartFragment barChartFragment = new BarChartFragment();
                                fragmentTransaction.replace(R.id.fragment_container,barChartFragment);
                                fragmentTransaction.commit();
                                break;
                            case 2:
                                BarchartByLocationFragment barChartFragment1 = new BarchartByLocationFragment();
                                fragmentTransaction.replace(R.id.fragment_container,barChartFragment1);
                                fragmentTransaction.commit();
                                break;

                        }
                    }
                }).create().show();
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    public void setData(){
        ArrayList<BarEntry> entries = new ArrayList<>();
        BarDataSet set;
        List<Budget> list = db.getAllBudgets();
        for (int i = 1; i <= 7; i++){
            float[] yVal = new float[list.size()];
            for (int f = 0; f < list.size();f++){
                Budget b = list.get(f);
                int val = db.getWeekDayConsumption(b.getId(),i);
                yVal[f] = (float)val;
            }
            entries.add(new BarEntry(i,yVal));
        }

        if (barChart.getData() != null &&
                barChart.getData().getDataSetCount() > 0) {
            set = (BarDataSet) barChart.getData().getDataSetByIndex(0);
            set.setValues(entries);
            barChart.getData().notifyDataChanged();
            barChart.notifyDataSetChanged();
        } else {
            set = new BarDataSet(entries, "");
            set.setColors(getColors(list.size()));
            String[] labels = new String[list.size()];
            for (int m = 0; m < labels.length;m++){
                Budget b = list.get(m);
                labels[m] = b.name;
            }
            set.setStackLabels(labels);

            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set);

            BarData data = new BarData(dataSets);
            data.setValueTextColor(Color.WHITE);
            data.setHighlightEnabled(true);

            barChart.setData(data);
        }
        barChart.setFitBars(true);
        barChart.invalidate();
    }

    private int[] getColors(int number) {

        int[] colors = new int[number];
        ArrayList<Integer> allColors = new ArrayList<Integer>();

        for (int c : ColorTemplate.MATERIAL_COLORS)
            allColors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            allColors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            allColors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            allColors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            allColors.add(c);

        for (int i = 0; i < colors.length; i++) {
            colors[i] = allColors.get(i);
        }

        return colors;
    }
}
