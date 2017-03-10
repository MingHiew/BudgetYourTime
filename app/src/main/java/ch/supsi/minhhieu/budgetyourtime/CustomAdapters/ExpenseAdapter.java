package ch.supsi.minhhieu.budgetyourtime.CustomAdapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bignerdranch.android.multiselector.MultiSelector;
import com.bignerdranch.android.multiselector.ModalMultiSelectorCallback;
import com.bignerdranch.android.multiselector.SwappingHolder;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ch.supsi.minhhieu.budgetyourtime.Helpers.DBHelper;
import ch.supsi.minhhieu.budgetyourtime.Models.Expense;
import ch.supsi.minhhieu.budgetyourtime.R;
import ch.supsi.minhhieu.budgetyourtime.Utils.CalendarUtils;
import ch.supsi.minhhieu.budgetyourtime.Utils.Utils;

/**
 * Created by acer on 03/08/2016.
 */
public class ExpenseAdapter extends BaseAdapter {

    private Context context;
    private List<Expense> list;
    private LayoutInflater inflater;
    private DBHelper db;
    private String budgetName;
    private ArrayList<Integer> checkedPositions = new ArrayList<Integer>();
    private MultiSelector mMultiSelector = new MultiSelector();

    String previousRange;
    private final StringBuilder sb1 = new StringBuilder();
    private final StringBuilder sb2 = new StringBuilder();
    public ExpenseAdapter(Context context, List<Expense> list, DBHelper db, String budgetName) {
        this.context = context;
        this.list = list;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.db = db;
        this.budgetName = budgetName;
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    /**@Override
    public ExpenseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_list_row, parent, false);
        return new ExpenseViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ExpenseViewHolder viewHolder, int position) {
        Expense i = list.get(position);
        Expense previousI = position>0?list.get(position-1):null;
        Cursor brCursor = db.getTimeRangeforExpense(i.getBudget(),i.getDate().getMillis());

        int duration = (int)i.getDuration();
        int weatherID = i.getWeatherID();
        switch (duration){
            case 1:
                viewHolder.timeLayout.setBackgroundColor(context.getResources().getColor(R.color.blue_dark));
                break;
            case 2:
                viewHolder.timeLayout.setBackgroundColor(context.getResources().getColor(R.color.red));
                break;
            case 3:
                viewHolder.timeLayout.setBackgroundColor(context.getResources().getColor(R.color.f_green));
                break;
            case 4:
                viewHolder.timeLayout.setBackgroundColor(context.getResources().getColor(R.color.purple));
                break;
            default:
                viewHolder.timeLayout.setBackgroundColor(context.getResources().getColor(R.color.iron));

        }

        String spent = String.valueOf(duration);
        viewHolder.timeSpentl.setText(spent);
        if(Integer.parseInt(spent) <= 1){
            viewHolder.timeUnit.setText("Hr");
        } else {
            viewHolder.timeUnit.setText("Hrs");
        }

        StringBuilder sb1 = this.sb1;

        sb1.setLength(0);
        sb1.append(DateUtils.formatDateRange(context, brCursor.getLong(0), brCursor.getLong(1),
                DateUtils.FORMAT_SHOW_DATE|DateUtils.FORMAT_SHOW_YEAR));
        String dateRange = sb1.toString();
        if(previousI != null) {
            Cursor previousC = db.getTimeRangeforExpense(previousI.getBudget(),previousI.getDate().getMillis());
            sb2.setLength(0);
            sb2.append(DateUtils.formatDateRange(context, previousC.getLong(0), previousC.getLong(1),
                    DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR));
            StringBuilder sb2 = this.sb2;
            previousRange = sb2.toString();

            if(dateRange.equals(previousRange)){
                viewHolder.timeRange.setVisibility(View.GONE);
            } else {
                viewHolder.timeRange.setVisibility(View.VISIBLE);
                viewHolder.timeRange.setText(dateRange);
            }
        } else {
            viewHolder.timeRange.setVisibility(View.VISIBLE);
            viewHolder.timeRange.setText(dateRange);
        }

        viewHolder.expenseDescription.setText(i.getDescription());
        viewHolder.expenseLocation.setText(i.getLocation());
        viewHolder.expenseDate.setText(CalendarUtils.toDayStringAbbrev(context,i.getDate().getMillis()));
        viewHolder.expenseStarttime.setText(CalendarUtils.toTimeString(context, i.getStartTime()));
        viewHolder.expenseEndtime.setText(CalendarUtils.toTimeString(context, i.getEndTime()));
        if (weatherID == -1){
            viewHolder.weatherDesc.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.weatherDesc.setImageResource(Utils.getArtResourceForWeatherCondition(weatherID));
        }
    }**/

    @Override
    public long getItemId(int position) {
        return list.get(position).getId();
    }

    /**@Override
    public int getItemCount() {
        return list.size();
    }**/

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        Expense i = list.get(position);
        Expense previousI = position>0?list.get(position-1):null;
        Cursor brCursor = db.getTimeRangeforExpense(i.getBudget(),i.getDate().getMillis());
        if (view == null) {
            view = inflater.inflate(R.layout.expense_list_row,null);
            ExpenseViewHolder viewHolder = new ExpenseViewHolder(view);
            view.setTag(viewHolder);
        }

        ExpenseViewHolder viewHolder = (ExpenseViewHolder) view.getTag();
        int duration = (int)i.getDuration();
        int weatherID = i.getWeatherID();
        switch (duration){
            case 1:
                viewHolder.timeLayout.setBackgroundColor(context.getResources().getColor(R.color.blue_dark));
                break;
            case 2:
                viewHolder.timeLayout.setBackgroundColor(context.getResources().getColor(R.color.red));
                break;
            case 3:
                viewHolder.timeLayout.setBackgroundColor(context.getResources().getColor(R.color.f_green));
                break;
            case 4:
                viewHolder.timeLayout.setBackgroundColor(context.getResources().getColor(R.color.purple));
                break;
            default:
                viewHolder.timeLayout.setBackgroundColor(context.getResources().getColor(R.color.iron));

        }

        String spent = String.valueOf(duration);
        viewHolder.timeSpentl.setText(spent);
        if(Integer.parseInt(spent) <= 1){
            viewHolder.timeUnit.setText("Hr");
        } else {
            viewHolder.timeUnit.setText("Hrs");
        }

        StringBuilder sb1 = this.sb1;

        sb1.setLength(0);
        sb1.append(DateUtils.formatDateRange(context, brCursor.getLong(0), brCursor.getLong(1),
                DateUtils.FORMAT_SHOW_DATE|DateUtils.FORMAT_SHOW_YEAR));
        String dateRange = sb1.toString();
        if(previousI != null) {
            Cursor previousC = db.getTimeRangeforExpense(previousI.getBudget(),previousI.getDate().getMillis());
            sb2.setLength(0);
            sb2.append(DateUtils.formatDateRange(context, previousC.getLong(0), previousC.getLong(1),
                    DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR));
            StringBuilder sb2 = this.sb2;
            previousRange = sb2.toString();

            if(dateRange.equals(previousRange)){
                viewHolder.timeRange.setVisibility(View.GONE);
            } else {
                viewHolder.timeRange.setVisibility(View.VISIBLE);
                viewHolder.timeRange.setText(dateRange);
            }
        } else {
            viewHolder.timeRange.setVisibility(View.VISIBLE);
            viewHolder.timeRange.setText(dateRange);
        }

        viewHolder.expenseDescription.setText(i.getDescription());
        if(!i.getLocation().equals("Unknown"))viewHolder.expenseLocation.setText("at "+i.getLocation());
        viewHolder.expenseDate.setText(CalendarUtils.toDayStringAbbrev(context,i.getDate().getMillis()));
        viewHolder.expenseStarttime.setText(CalendarUtils.toTimeString(context, i.getStartTime()));
        viewHolder.expenseEndtime.setText(CalendarUtils.toTimeString(context, i.getEndTime()));
        if (weatherID == -1){
            viewHolder.weatherDesc.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.weatherDesc.setImageResource(Utils.getArtResourceForWeatherCondition(weatherID));
        }
        return view;
    }

    public ArrayList<Integer> getCheckedPositions() {
        return checkedPositions;
    }

    public void refresh(List<Expense> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public class ExpenseViewHolder {

        @BindView(R.id.time_range)
        TextView timeRange;
        @BindView(R.id.expense_timelayout)
        LinearLayout timeLayout;
        @BindView(R.id.time_spent)
        TextView timeSpentl;
        @BindView(R.id.time_unit)
        TextView timeUnit;
        @BindView(R.id.expense_description)
        TextView expenseDescription;
        @BindView(R.id.expense_location)
        TextView expenseLocation;
        @BindView(R.id.expense_date)
        TextView expenseDate;
        @BindView(R.id.expense_starttime)
        TextView expenseStarttime;
        @BindView(R.id.expense_endtime)
        TextView expenseEndtime;
        @BindView(R.id.weather_desc)
        ImageView weatherDesc;

        public ExpenseViewHolder(View itemView) {
            //super(itemView, mMultiSelector);
            ButterKnife.bind(this,itemView);
            //itemView.setLongClickable(true);
            //itemView.setOnLongClickListener(this);
        }

    /**@Override
        public boolean onLongClick(View v) {
            ((AppCompatActivity) context).startSupportActionMode(mDeleteMode);
            mMultiSelector.setSelected(this, true);
            return true;
        }
        public ExpenseViewHolder(View view) {
            ButterKnife.bind(this,view);
        }**/
    }

}
