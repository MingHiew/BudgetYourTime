package ch.supsi.minhhieu.budgetyourtime.CustomAdapters;

/**
 * Created by acer on 14/08/2016.
 */

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Build;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ch.supsi.minhhieu.budgetyourtime.Helpers.DBHelper;
import ch.supsi.minhhieu.budgetyourtime.Models.Budget;
import ch.supsi.minhhieu.budgetyourtime.Models.BudgetRecord;
import ch.supsi.minhhieu.budgetyourtime.R;
import ch.supsi.minhhieu.budgetyourtime.Utils.RecurUtils;

/**
 * Created by acer on 03/08/2016.
 */
public class BudgetDetailAdapter extends BaseAdapter {

    private Context context;
    private List<Budget> list;
    private LayoutInflater inflater;
    private DBHelper db;
    private long startDate, endDate;
    private final StringBuilder sb = new StringBuilder();


    public BudgetDetailAdapter(Context context, List<Budget> list, DBHelper db, long startDate, long endDate) {
        this.context = context;
        this.list = list;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.db = db;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).getId();
    }


    public void refresh(List<Budget> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        Budget b = list.get(position);
        BudgetRecord latestBR = db.getBudgetRecordByInterval(b,startDate,endDate);
        if (view == null) {
            view = inflater.inflate(R.layout.budget_list_row,null);
            OverviewViewHolder viewHolder = new OverviewViewHolder(view);
            view.setTag(viewHolder);
        }

        OverviewViewHolder viewHolder = (OverviewViewHolder) view.getTag();
        viewHolder.budgetListTitle.setText(b.name);
        RecurUtils.RecurInterval interval = b.getRecur().interval;
        switch (interval){
            case WEEKLY:
                viewHolder.budgetListAdmount.setText(String.valueOf(b.amount)+" hours/week");
                break;
            case MONTHLY:
                viewHolder.budgetListAdmount.setText(String.valueOf(b.amount)+" hours/month");
                break;
        }
        StringBuilder sb = this.sb;

        sb.setLength(0);
        sb.append(DateUtils.formatDateRange(context, latestBR.startDate, latestBR.endDate,
                DateUtils.FORMAT_SHOW_DATE|DateUtils.FORMAT_ABBREV_MONTH));
        viewHolder.budgetListTimeframe.setText(sb.toString());
        if(latestBR.spent == 1){
            viewHolder.budgetConsumed.setText("Spent: "+String.valueOf(latestBR.spent)+ " hour");
        } else {
            viewHolder.budgetConsumed.setText("Spent: "+String.valueOf(latestBR.spent)+ " hours");
        }
        if (latestBR.balance == 1){
            viewHolder.budgetRemained.setText("Remaining Balance: "+String.valueOf(latestBR.balance)+" hour");
        } else {
            viewHolder.budgetRemained.setText("Remaining Balance: "+String.valueOf(latestBR.balance)+" hours");
        }

        viewHolder.budgetStatusBar.setMax(b.amount);
        viewHolder.budgetStatusBar.setProgress((int)latestBR.spent);

        return view;
    }

    public static class OverviewViewHolder{
        @BindView(R.id.budget_list_title)
        TextView budgetListTitle;
        @BindView(R.id.budget_list_timeframe)
        TextView budgetListTimeframe;
        @BindView(R.id.budget_list_amount)
        TextView budgetListAdmount;
        @BindView(R.id.budget_consumed)
        TextView budgetConsumed;
        @BindView(R.id.budget_remained)
        TextView budgetRemained;
        @BindView(R.id.budget_consumption_bar)
        ProgressBar budgetStatusBar;


        public OverviewViewHolder(View view) {
            ButterKnife.bind(this,view);
        }
    }
}
