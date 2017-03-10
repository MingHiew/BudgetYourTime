package ch.supsi.minhhieu.budgetyourtime.CustomAdapters;

/**
 * Created by acer on 14/08/2016.
 */

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
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
public class BudgetDetailAdapter extends RecyclerView.Adapter<BudgetDetailAdapter.OverviewViewHolder> {

    private Context context;
    private List<Budget> list;
    //private LayoutInflater inflater;
    private DBHelper db;
    private long startDate, endDate;
    private final OnItemClickListener OnClickListener;
    //private final OnItemLongClickListener OnLongClickListener;
    private final StringBuilder sb = new StringBuilder();


    public BudgetDetailAdapter(Context context, List<Budget> list, DBHelper db, long startDate, long endDate,
                               OnItemClickListener onClickListener) {
        this.context = context;
        this.list = list;
        //inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.db = db;
        this.startDate = startDate;
        this.endDate = endDate;
        this.OnClickListener = onClickListener;
        //this.OnLongClickListener = onLongClickListener;
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
    public OverviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.budget_list_row, parent, false);
        return new OverviewViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(OverviewViewHolder viewHolder, int position) {
        Budget b = list.get(position);
        BudgetRecord br = db.getBudgetRecordByInterval(b,startDate,endDate);

        viewHolder.budgetListTitle.setText(b.name);
        RecurUtils.RecurInterval interval = b.getRecur().interval;
        switch (interval){
            case WEEKLY:
                viewHolder.budgetListAdmount.setText(String.valueOf(br.amount)+" hours/week");
                break;
            case MONTHLY:
                viewHolder.budgetListAdmount.setText(String.valueOf(br.amount)+" hours/month");
                break;
        }
        StringBuilder sb = this.sb;

        sb.setLength(0);
        sb.append(DateUtils.formatDateRange(context, br.startDate, br.endDate,
                DateUtils.FORMAT_SHOW_DATE|DateUtils.FORMAT_ABBREV_MONTH));
        viewHolder.budgetListTimeframe.setText(sb.toString());
        if(br.spent == 1){
            viewHolder.budgetConsumed.setText("Spent: "+String.valueOf(br.spent)+ " hour");
        } else {
            viewHolder.budgetConsumed.setText("Spent: "+String.valueOf(br.spent)+ " hours");
        }
        if (br.balance == 1 || br.balance == 0){
            viewHolder.budgetRemained.setText("Remaining Balance: "+String.valueOf(br.balance)+" hour");
        } else {
            viewHolder.budgetRemained.setText("Remaining Balance: "+String.valueOf(br.balance)+" hours");
        }

        viewHolder.budgetStatusBar.setMax((int)br.amount);
        viewHolder.budgetStatusBar.setProgress((int)br.spent);
        viewHolder.bind(list.get(position), OnClickListener);
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public void refresh(List<Budget> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    /**@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        Budget b = list.get(position);
        BudgetRecord br = db.getBudgetRecordByInterval(b,startDate,endDate);
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
        sb.append(DateUtils.formatDateRange(context, br.startDate, br.endDate,
                DateUtils.FORMAT_SHOW_DATE|DateUtils.FORMAT_ABBREV_MONTH));
        viewHolder.budgetListTimeframe.setText(sb.toString());
        if(br.spent == 1){
            viewHolder.budgetConsumed.setText("Spent: "+String.valueOf(br.spent)+ " hour");
        } else {
            viewHolder.budgetConsumed.setText("Spent: "+String.valueOf(br.spent)+ " hours");
        }
        if (br.balance == 1){
            viewHolder.budgetRemained.setText("Remaining Balance: "+String.valueOf(br.balance)+" hour");
        } else {
            viewHolder.budgetRemained.setText("Remaining Balance: "+String.valueOf(br.balance)+" hours");
        }

        viewHolder.budgetStatusBar.setMax(b.amount);
        viewHolder.budgetStatusBar.setProgress((int)br.spent);

        return view;
    }**/

    public static class OverviewViewHolder extends RecyclerView.ViewHolder{
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

        public OverviewViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        /**public OverviewViewHolder(View view) {
            ButterKnife.bind(this,view);
        }**/
        public void bind(final Budget b, final OnItemClickListener listener1) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener1.onItemClick(b);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
       //             listener2.onItemLongClick(b);
                    return true;
                }
            });
        }
    }
    public interface OnItemClickListener {
        void onItemClick(Budget budget);
    }

    /*public interface OnItemLongClickListener {
        void onItemLongClick(Budget budget);
    }*/
}
