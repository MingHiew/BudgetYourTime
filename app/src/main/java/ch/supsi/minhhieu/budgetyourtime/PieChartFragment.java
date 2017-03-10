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

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ch.supsi.minhhieu.budgetyourtime.Helpers.DBHelper;
import ch.supsi.minhhieu.budgetyourtime.Models.Budget;


/**
 * A simple {@link Fragment} subclass.
 */
public class PieChartFragment extends Fragment {

    @BindView(R.id.piechart1)
    PieChart pieChart;

    private Typeface mTfLight;
    private Typeface mTfRegular;
    private Typeface mTfXBItalic;
    private DBHelper db;
    private String[] chartType = new String[]{"Piechart: Consumption by Budget",
            "Barchart: Weekday Overview","Barchart: Weekday Overview by Location"};
    private String[] chartFilter = new String[]{"This Week","All Time"};
    private boolean isWeekFilter = true;
    public PieChartFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = DBHelper.getInstance(this.getContext());
        Bundle bundle = getArguments();
        if (bundle != null) {
            isWeekFilter = bundle.getBoolean("isWeekFilter");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_piechart, container, false);
        ButterKnife.bind(this,view);
        setHasOptionsMenu(true);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        mTfLight = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Light.ttf");
        mTfRegular = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Regular.ttf");
        mTfXBItalic = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-ExtraBoldItalic.ttf");

        pieChart.setUsePercentValues(true);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);

        pieChart.setCenterTextTypeface(mTfLight);
        if(isWeekFilter == true) {
            pieChart.setCenterText("Time Consumption by Budget\nThis Week");
        } else {
            pieChart.setCenterText("Time Consumption by Budget\nAll Time");
        }
        actionBar.setTitle("Chart: "+pieChart.getCenterText());
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);

        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);

        pieChart.setHoleRadius(58f);
        pieChart.setTransparentCircleRadius(61f);

        pieChart.setDrawCenterText(true);

        setData(isWeekFilter);

        pieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        Legend l = pieChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        // entry label styling
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setEntryLabelTypeface(mTfRegular);
        pieChart.setEntryLabelTextSize(12f);
        pieChart.setDescription("");
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.pie_chart_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_chart_type1:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Choose Chart").setItems(chartType, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                        switch (which) {
                            case 0:
                                PieChartFragment piechartFragment = new PieChartFragment();
                                fragmentTransaction.replace(R.id.fragment_container, piechartFragment);
                                fragmentTransaction.commit();
                                break;
                            case 1:
                                BarChartFragment barChartFragment = new BarChartFragment();
                                fragmentTransaction.replace(R.id.fragment_container, barChartFragment);
                                fragmentTransaction.commit();
                                break;
                            case 2:
                                BarchartByLocationFragment barChartFragment1 = new BarchartByLocationFragment();
                                fragmentTransaction.replace(R.id.fragment_container, barChartFragment1);
                                fragmentTransaction.commit();
                                break;
                        }
                    }
                }).create().show();
                break;
            case R.id.action_chart_filter:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                builder1.setTitle("Choose Filter").setItems(chartFilter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                        Bundle args = new Bundle();
                        PieChartFragment piechartFragment = new PieChartFragment();

                        switch (which) {
                            case 0:
                                args.putBoolean("isWeekFilter",true);
                                piechartFragment.setArguments(args);
                                fragmentTransaction.replace(R.id.fragment_container, piechartFragment);
                                fragmentTransaction.commit();
                                break;
                            case 1:
                                args.putBoolean("isWeekFilter",false);
                                piechartFragment.setArguments(args);
                                fragmentTransaction.replace(R.id.fragment_container, piechartFragment);
                                fragmentTransaction.commit();
                                break;
                        }
                    }
                }).create().show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setData(boolean isWeekFilter) {

        ArrayList<PieEntry> entries = new ArrayList<>();
        List<Budget> list = db.getAllBudgets();
        if(list.size()!=0) {
            if(isWeekFilter == false) {
                for (Budget b : list){
                    int totalSpent = db.getTotalConsumptionHistory(b.getId());
                    if(totalSpent > 0) {
                        String budgetName = b.name;
                        entries.add(new PieEntry(totalSpent, budgetName));
                    }
                }
            } else {
                for (Budget b : list){
                    int totalSpent = db.getTotalConsumptionThisWeek(b.getId());
                    if(totalSpent > 0) {
                        String budgetName = b.name;
                        entries.add(new PieEntry(totalSpent, budgetName));
                    }
                }
            }
            PieDataSet dataSet = new PieDataSet(entries, "");
            dataSet.setSliceSpace(3f);
            dataSet.setSelectionShift(5f);
            // add a lot of colors

            ArrayList<Integer> colors = new ArrayList<>();

            for (int c : ColorTemplate.MATERIAL_COLORS)
                colors.add(c);

            for (int c : ColorTemplate.JOYFUL_COLORS)
                colors.add(c);

            for (int c : ColorTemplate.PASTEL_COLORS)
                colors.add(c);

            colors.add(ColorTemplate.getHoloBlue());

            dataSet.setColors(colors);
            //dataSet.setSelectionShift(0f);

            PieData data = new PieData(dataSet);
            data.setValueFormatter(new PercentFormatter());
            data.setValueTextSize(11f);
            data.setValueTextColor(Color.BLACK);
            data.setValueTypeface(mTfXBItalic);
            pieChart.setData(data);

            // undo all highlights
            pieChart.highlightValues(null);

            pieChart.invalidate();
        }
    }

}
