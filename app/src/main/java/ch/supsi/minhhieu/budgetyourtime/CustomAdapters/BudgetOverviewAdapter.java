package ch.supsi.minhhieu.budgetyourtime.CustomAdapters;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageView;
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
import ch.supsi.minhhieu.budgetyourtime.Utils.RecurUtils.RecurInterval;
/**
 * Created by acer on 03/08/2016.
 */
public class BudgetOverviewAdapter extends RecyclerView.Adapter<BudgetOverviewAdapter.BudgetOverviewViewHolder> {

    private Context context;
    private List<Budget> list;
    //private LayoutInflater inflater;
    private DBHelper db;
    private final OnItemClickListener OnClickListener;
    private final OnItemLongClickListener OnLongClickListener;
    private final StringBuilder sb = new StringBuilder();


    public BudgetOverviewAdapter(Context context, List<Budget> list, DBHelper db,
                                 OnItemClickListener listener1,OnItemLongClickListener listener2) {
        this.context = context;
        this.list = list;
        //inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.db = db;
        this.OnClickListener = listener1;
        this.OnLongClickListener = listener2;
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
    public BudgetOverviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.budget_list_row, parent, false);
        return new BudgetOverviewViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(BudgetOverviewViewHolder viewHolder, int position) {
        Budget b = list.get(position);
        BudgetRecord latestBR = db.getLatestInterval(b);
        viewHolder.budgetListTitle.setText(b.name);
        RecurInterval interval = b.getRecur().interval;
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
        if(latestBR.spent <= b.amount) {
            viewHolder.budgetStatusBar.setMax((int)latestBR.amount);
            viewHolder.budgetStatusBar.setProgress((int) latestBR.spent);
        } else if (latestBR.spent > b.amount){
            viewHolder.budgetStatusBar.setBackgroundColor(this.context.getResources().getColor(R.color.red));
            viewHolder.budgetStatusBar.setDrawingCacheBackgroundColor(this.context.getResources().getColor(R.color.colorPrimaryDark));
            viewHolder.budgetStatusBar.setMax((int) latestBR.spent);
            viewHolder.budgetStatusBar.setProgress(b.amount);

        }
        viewHolder.bind(list.get(position), OnClickListener, OnLongClickListener);
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

        Budget b = list.get(position);
        BudgetRecord latestBR = db.getLatestInterval(b);
        if (view == null) {
            view = inflater.inflate(R.layout.budget_list_row,null);
            BudgetOverviewViewHolder viewHolder = new BudgetOverviewViewHolder(view);
            view.setTag(viewHolder);
        }

        BudgetOverviewViewHolder viewHolder = (BudgetOverviewViewHolder) view.getTag();
        viewHolder.budgetListTitle.setText(b.name);
        RecurInterval interval = b.getRecur().interval;
        switch (interval){
            case WEEKLY:
                viewHolder.budgetListAdmount.setText(String.valueOf(latestBR.amount)+" hours/week");
                break;
            case MONTHLY:
                viewHolder.budgetListAdmount.setText(String.valueOf(latestBR.amount)+" hours/month");
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
        if(latestBR.spent <= b.amount) {
            viewHolder.budgetStatusBar.setMax(b.amount);
            viewHolder.budgetStatusBar.setProgress((int) latestBR.spent);
        } else if (latestBR.spent > b.amount){
            viewHolder.budgetStatusBar.setBackgroundColor(this.context.getResources().getColor(R.color.red));
            viewHolder.budgetStatusBar.setDrawingCacheBackgroundColor(this.context.getResources().getColor(R.color.colorPrimaryDark));
            viewHolder.budgetStatusBar.setMax((int) latestBR.spent);
            viewHolder.budgetStatusBar.setProgress(b.amount);

        }

        return view;
    }**/

    public static class BudgetOverviewViewHolder extends RecyclerView.ViewHolder{
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



        public BudgetOverviewViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        /**public OverviewViewHolder(View view) {
            ButterKnife.bind(this,view);
        }**/
        public void bind(final Budget b, final OnItemClickListener listener1,final OnItemLongClickListener listener2) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener1.onItemClick(b);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener2.onItemLongClick(b);
                    return true;
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Budget budget);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(Budget budget);
    }

}
