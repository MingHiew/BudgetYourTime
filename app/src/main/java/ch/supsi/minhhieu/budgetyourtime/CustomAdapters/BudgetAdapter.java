package ch.supsi.minhhieu.budgetyourtime.CustomAdapters;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.os.Build;
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
public class BudgetAdapter extends BaseAdapter{

    private Context context;
    private List<Budget> list;
    private LayoutInflater inflater;
    private DBHelper db;
    private int bugetType;

    private final StringBuilder sb = new StringBuilder();


    public BudgetAdapter(Context context, List<Budget> list, DBHelper db) {
        this.context = context;
        this.list = list;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.db = db;
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
        BudgetRecord latestBR = db.getLatestInterval(b);
        if (view == null) {
            view = inflater.inflate(R.layout.budget_list_row,null);
            ViewHolder viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }

        ViewHolder viewHolder = (ViewHolder) view.getTag();
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

        viewHolder.budgetStatusBar.setMax(b.amount);
        viewHolder.budgetStatusBar.setProgress((int)latestBR.spent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ObjectAnimator animation = ObjectAnimator.ofInt(viewHolder.budgetStatusBar, "progress",0,(int) (Math.abs(latestBR.spent) - 1));
            animation.setDuration(Math.min(1200,(latestBR.spent * 100) / Math.abs(b.amount))*12); // max 1,2 seconds
            animation.setInterpolator(new DecelerateInterpolator());
            animation.start();
        }
        /*viewHolder.budgetStatusBar.setMax(b.amount);
        viewHolder.budgetStatusBar.setProgress((int)latestBR.spent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ObjectAnimator animation = ObjectAnimator.ofInt(viewHolder.budgetStatusBar, "progress",0,(int) (Math.abs(latestBR.spent) - 1));
            animation.setDuration(Math.min(1200,(latestBR.spent * 100) / Math.abs(b.amount))*12); // max 1,2 seconds
            animation.setInterpolator(new DecelerateInterpolator());
            animation.start();
        }
        if(latestBR.spent < b.amount){
            viewHolder.budgetStatusBar.getProgressDrawable().setColorFilter(convertView.getResources().getColor(R.color.f_green), PorterDuff.Mode.SRC_IN);

        } else {
            viewHolder.budgetStatusBar.setMax((int)latestBR.spent);
            viewHolder.budgetStatusBar.setProgress(b.amount);
            viewHolder.budgetStatusBar.getProgressDrawable().setColorFilter(convertView.getResources().getColor(R.color.orange), PorterDuff.Mode.SRC_IN);

        }*/
        return view;
    }

    public static class ViewHolder{
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


        public ViewHolder(View view) {
            ButterKnife.bind(this,view);
        }
    }
}
