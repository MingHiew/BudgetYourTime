package ch.supsi.minhhieu.budgetyourtime.CustomAdapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import ch.supsi.minhhieu.budgetyourtime.Helpers.DBHelper;
import ch.supsi.minhhieu.budgetyourtime.Models.Item;
import ch.supsi.minhhieu.budgetyourtime.R;
import ch.supsi.minhhieu.budgetyourtime.Utils.CalendarUtils;

/**
 * Created by acer on 03/08/2016.
 */
public class ItemAdapter extends BaseAdapter {

    private Context context;
    private List<Item> list;
    private LayoutInflater inflater;
    private DBHelper db;
    private String budgetName;

    String previousRange;
    private final StringBuilder sb1 = new StringBuilder();
    private final StringBuilder sb2 = new StringBuilder();
    public ItemAdapter(Context context, List<Item> list, DBHelper db, String budgetName) {
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

    @Override
    public long getItemId(int position) {
        return list.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        Item i = list.get(position);
        Item previousI = position>0?list.get(position-1):null;
        Cursor brCursor = db.getTimeRangeforItem(i.getBudget(),i.getDate().getMillis());
        if (view == null) {
            view = inflater.inflate(R.layout.item_list_row,null);
            itemViewHolder viewHolder = new itemViewHolder(view);
            view.setTag(viewHolder);
        }

        itemViewHolder viewHolder = (itemViewHolder) view.getTag();
        int duration = (int)i.getDuration();
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
            Cursor previousC = db.getTimeRangeforItem(previousI.getBudget(),previousI.getDate().getMillis());
            sb2.setLength(0);
            sb2.append(DateUtils.formatDateRange(context, previousC.getLong(0), previousC.getLong(1),
                    DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR));
            StringBuilder sb2 = this.sb2;
            previousRange = sb2.toString();
        }
        if(dateRange.equals(previousRange)){
            viewHolder.timeRange.setVisibility(View.GONE);
        } else {
            viewHolder.timeRange.setVisibility(View.VISIBLE);
            viewHolder.timeRange.setText(dateRange);
        }

        viewHolder.budgetName1.setText(budgetName);
        viewHolder.itemDescription.setText(i.getDescription());
        viewHolder.itemLocation.setText(i.getLocation());
        viewHolder.itemDate.setText(CalendarUtils.toDayStringAbbrev(context,i.getDate().getMillis()));
        viewHolder.itemStarttime.setText("From "+CalendarUtils.toTimeString(context, i.getStartTime()));
        viewHolder.itemEndtime.setText("To "+CalendarUtils.toTimeString(context, i.getEndTime()));
        return view;
    }

    public class itemViewHolder {

        @BindView(R.id.time_range)
        TextView timeRange;
        @BindView(R.id.item_timelayout)
        LinearLayout timeLayout;
        @BindView(R.id.time_spent)
        TextView timeSpentl;
        @BindView(R.id.time_unit)
        TextView timeUnit;
        @BindView(R.id.budget_name1)
        TextView budgetName1;
        @BindView(R.id.item_description)
        TextView itemDescription;
        @BindView(R.id.item_location)
        TextView itemLocation;
        @BindView(R.id.item_date)
        TextView itemDate;
        @BindView(R.id.item_starttime)
        TextView itemStarttime;
        @BindView(R.id.item_endtime)
        TextView itemEndtime;

        public itemViewHolder(View view) {
            ButterKnife.bind(this,view);
        }
    }

}
