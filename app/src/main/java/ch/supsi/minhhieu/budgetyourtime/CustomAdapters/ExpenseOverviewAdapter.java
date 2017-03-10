package ch.supsi.minhhieu.budgetyourtime.CustomAdapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ch.supsi.minhhieu.budgetyourtime.Helpers.DBHelper;
import ch.supsi.minhhieu.budgetyourtime.Models.Budget;
import ch.supsi.minhhieu.budgetyourtime.Models.Expense;
import ch.supsi.minhhieu.budgetyourtime.R;
import ch.supsi.minhhieu.budgetyourtime.Utils.CalendarUtils;
import ch.supsi.minhhieu.budgetyourtime.Utils.Utils;

/**
 * Created by acer on 06/09/2016.
 */
public class ExpenseOverviewAdapter extends RecyclerView.Adapter<ExpenseOverviewAdapter.ExpenseOverviewViewHolder> {

    private Context context;
    private List<Expense> list;
    //private LayoutInflater inflater;
    private DBHelper db;


    /**public ExpenseOverviewAdapter(Context context, List<Expense> list, DBHelper db) {
        this.context = context;
        this.list = list;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.db = db;
    }**/

    public ExpenseOverviewAdapter(Context context, List<Expense> list, DBHelper db) {
        this.context = context;
        this.db = db;
        this.list = list;
    }

    /**@Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }**/

    @Override
    public ExpenseOverviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.expense_list_row_overview, parent, false);

        return new ExpenseOverviewViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ExpenseOverviewViewHolder viewHolder, int position) {
        Expense i = list.get(position);
        Budget b = db.getBudget(i.getBudget());
        int duration = (int)i.getDuration();
        int weatherID = i.getWeatherID();
        switch (duration){
            case 1:
                viewHolder.overviewTimeLayout.setBackgroundColor(context.getResources().getColor(R.color.blue_dark));
                break;
            case 2:
                viewHolder.overviewTimeLayout.setBackgroundColor(context.getResources().getColor(R.color.red));
                break;
            case 3:
                viewHolder.overviewTimeLayout.setBackgroundColor(context.getResources().getColor(R.color.f_green));
                break;
            case 4:
                viewHolder.overviewTimeLayout.setBackgroundColor(context.getResources().getColor(R.color.purple));
                break;
            default:
                viewHolder.overviewTimeLayout.setBackgroundColor(context.getResources().getColor(R.color.iron));

        }

        String spent = String.valueOf(duration);
        viewHolder.overviewTimeSpent.setText(spent);
        if(Integer.parseInt(spent) <= 1){
            viewHolder.overviewTimeUnit.setText("Hr");
        } else {
            viewHolder.overviewTimeUnit.setText("Hrs");
        }
        viewHolder.budgetName.setText(b.name);

        viewHolder.overviewExpenseDescription.setText(i.getDescription());
        if(!i.getLocation().equals("Unknown"))viewHolder.overviewExpenseLocation.setText("at "+i.getLocation());
        viewHolder.overviewExpenseDate.setText(CalendarUtils.toDayStringAbbrev(context,i.getDate().getMillis()));
        viewHolder.overviewExpenseStarttime.setText(CalendarUtils.toTimeString(context, i.getStartTime()));
        viewHolder.overviewExpenseEndtime.setText(CalendarUtils.toTimeString(context, i.getEndTime()));
        if (weatherID == -1){
            viewHolder.overviewWeatherDesc.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.overviewWeatherDesc.setImageResource(Utils.getArtResourceForWeatherCondition(weatherID));
        }
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    /**@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        Expense i = list.get(position);
        Budget b = db.getBudget(i.getBudget());
        if (view == null) {
            view = inflater.inflate(R.layout.expense_list_row_overview,null);
            ExpenseOverviewViewHolder viewHolder = new ExpenseOverviewViewHolder(view);
            view.setTag(viewHolder);
        }

        ExpenseOverviewViewHolder viewHolder = (ExpenseOverviewViewHolder) view.getTag();
        int duration = (int)i.getDuration();
        int weatherID = i.getWeatherID();
        switch (duration){
            case 1:
                viewHolder.overviewTimeLayout.setBackgroundColor(context.getResources().getColor(R.color.blue_dark));
                break;
            case 2:
                viewHolder.overviewTimeLayout.setBackgroundColor(context.getResources().getColor(R.color.red));
                break;
            case 3:
                viewHolder.overviewTimeLayout.setBackgroundColor(context.getResources().getColor(R.color.f_green));
                break;
            case 4:
                viewHolder.overviewTimeLayout.setBackgroundColor(context.getResources().getColor(R.color.purple));
                break;
            default:
                viewHolder.overviewTimeLayout.setBackgroundColor(context.getResources().getColor(R.color.iron));

        }

        String spent = String.valueOf(duration);
        viewHolder.overviewTimeSpent.setText(spent);
        if(Integer.parseInt(spent) <= 1){
            viewHolder.overviewTimeUnit.setText("Hr");
        } else {
            viewHolder.overviewTimeUnit.setText("Hrs");
        }
        viewHolder.budgetName.setText(b.name);

        viewHolder.overviewExpenseDescription.setText(i.getDescription());
        viewHolder.overviewExpenseLocation.setText("at "+i.getLocation());
        viewHolder.overviewExpenseDate.setText(CalendarUtils.toDayStringAbbrev(context,i.getDate().getMillis()));
        viewHolder.overviewExpenseStarttime.setText(CalendarUtils.toTimeString(context, i.getStartTime()));
        viewHolder.overviewExpenseEndtime.setText(CalendarUtils.toTimeString(context, i.getEndTime()));
        if (weatherID == -1){
            viewHolder.overviewWeatherDesc.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.overviewWeatherDesc.setImageResource(Utils.getArtResourceForWeatherCondition(weatherID));
        }

        return view;
    }**/

    public class ExpenseOverviewViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.overview_expense_budget_name)
        TextView budgetName;
        @BindView(R.id.overview_expense_timelayout)
        LinearLayout overviewTimeLayout;
        @BindView(R.id.overview_time_spent)
        TextView overviewTimeSpent;
        @BindView(R.id.overview_time_unit)
        TextView overviewTimeUnit;
        @BindView(R.id.overview_expense_description)
        TextView overviewExpenseDescription;
        @BindView(R.id.overview_expense_location)
        TextView overviewExpenseLocation;
        @BindView(R.id.overview_expense_date)
        TextView overviewExpenseDate;
        @BindView(R.id.overview_expense_starttime)
        TextView overviewExpenseStarttime;
        @BindView(R.id.overview_expense_endtime)
        TextView overviewExpenseEndtime;
        @BindView(R.id.overview_weather_desc)
        ImageView overviewWeatherDesc;

        public ExpenseOverviewViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        //public ExpenseOverviewViewHolder(View view) {
        //    ButterKnife.bind(this,view);
        //}
    }

}
