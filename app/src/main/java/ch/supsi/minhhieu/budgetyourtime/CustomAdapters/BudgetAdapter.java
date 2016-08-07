package ch.supsi.minhhieu.budgetyourtime.CustomAdapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ch.supsi.minhhieu.budgetyourtime.Helpers.DBHelper;
import ch.supsi.minhhieu.budgetyourtime.Models.Budget;
import ch.supsi.minhhieu.budgetyourtime.R;

/**
 * Created by acer on 03/08/2016.
 */
public class BudgetAdapter extends BaseAdapter{

    private Context context;
    private List<Budget> list;
    private LayoutInflater inflater;
    private DBHelper db;
    private int bugetType;

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

        Budget t = list.get(position);

        if (view == null) {
            view = inflater.inflate(R.layout.budget_list_row,null);
            ViewHolder viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }

        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.budgetListTitle.setText(t.getName());
        bugetType = t.getBudgetType();

        if (bugetType == 0){
            viewHolder.budgetListAdmount.setText(String.valueOf(t.getAmount())+" hours/week");
        } else if (bugetType == 1){
            viewHolder.budgetListAdmount.setText(String.valueOf(t.getAmount())+" hours/month");
        }

        return view;
    }

    public static class ViewHolder{
        @BindView(R.id.budget_list_title)
        TextView budgetListTitle;
        //@BindView(R.id.budget_list_timeframe)
        //TextView budgetListTimeframe;
        @BindView(R.id.budget_list_amount)
        TextView budgetListAdmount;
        //@BindView(R.id.budget_consumed)
        //TextView budgetConsumed;
        //@BindView(R.id.budget_remained)
        //TextView budgetRemained;
        //@BindView(R.id.btn_previous_timeframe)
        //ImageView btnPreviousTimeframe;
        //@BindView(R.id.btn_next_timeframe)
        //ImageView btnNextTimeframe;

        public ViewHolder(View view) {
            ButterKnife.bind(this,view);
        }
    }
}
