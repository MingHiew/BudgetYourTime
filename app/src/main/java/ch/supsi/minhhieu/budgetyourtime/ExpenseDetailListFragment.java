package ch.supsi.minhhieu.budgetyourtime;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;


import com.bignerdranch.android.multiselector.ModalMultiSelectorCallback;
import com.bignerdranch.android.multiselector.MultiSelector;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ch.supsi.minhhieu.budgetyourtime.CustomAdapters.ExpenseAdapter;
import ch.supsi.minhhieu.budgetyourtime.Helpers.DBHelper;
import ch.supsi.minhhieu.budgetyourtime.Models.Expense;


/**
 * A simple {@link Fragment} subclass.
 */
public class ExpenseDetailListFragment extends Fragment {

    private static final String TAG = "crimeListFragment";
    @BindView(R.id.expense_detaillistview)
    ListView expenseList;
    private MultiSelector mMultiSelector = new MultiSelector();
    /**private ModalMultiSelectorCallback mDeleteMode = new ModalMultiSelectorCallback(mMultiSelector) {

        @Override
        public boolean onCreateActionMode(android.support.v7.view.ActionMode actionMode, Menu menu) {
            super.onCreateActionMode(actionMode, menu);
            getActivity().getMenuInflater().inflate(R.menu.expense_list_context_menu, menu);
            return true;
        }

        @Override
        public boolean onActionItemClicked(android.support.v7.view.ActionMode actionMode, MenuItem menuItem) {
            if (menuItem.getItemId()==  R.id.deleteTransaction){
                // Need to finish the action mode before doing the following,
                // not after. No idea why, but it crashes.
                actionMode.finish();

                for (int i = list.size(); i >= 0; i--) {
                    if (mMultiSelector.isSelected(i, 0)) {
                        Expense e = list.get(i);
                        db.deleteExpense(e);
                        expenseList.getAdapter().notifyDataSetChanged();
                        Bundle bundle = new Bundle();
                        //budget = list.get(position);
                        bundle.putString("budgetName",budgetName);
                        bundle.putLong("budgetID",budgetID);
                        openItemListFragment(bundle);
                    }
                }

                mMultiSelector.clearSelections();
                return true;

            }
            return false;
        }
    };**/

    public long budgetID;
    List<Expense> list = new ArrayList<>();
    private DBHelper db;
    private ExpenseAdapter expenseAdapter;
    private String budgetName;
    FloatingActionButton fab;


    public ExpenseDetailListFragment() {
        // Required empty public constructor
    }

    /**@Override
    public void onActivityCreated(Bundle savedInstanceState) {

        if (mMultiSelector != null) {
            Bundle bundle = savedInstanceState;
            if (bundle != null) {
                mMultiSelector.restoreSelectionStates(bundle.getBundle(TAG));
            }

            if (mMultiSelector.isSelectable()) {
                if (mDeleteMode != null) {
                    mDeleteMode.setClearOnPrepare(false);
                    ((AppCompatActivity) getActivity()).startSupportActionMode(mDeleteMode);
                }

            }
        }

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBundle(TAG, mMultiSelector.saveSelectionStates());
        super.onSaveInstanceState(outState);
    }**/


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = DBHelper.getInstance(this.getContext());
        Bundle bundle = getArguments();
        if(bundle!= null) {
            budgetID = bundle.getLong("budgetID");
            budgetName = bundle.getString("budgetName");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_expense_detail_list, container, false);
        ButterKnife.bind(this,view);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle("Budget Detail: "+budgetName);
        fab = (FloatingActionButton)getActivity().findViewById(R.id.add_new_activity);
        fab.hide();
        list = db.getExpensesByBudget(budgetID);
        //RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        //expenseList.setLayoutManager(mLayoutManager);
        if (list.size()>0) {
            expenseAdapter = new ExpenseAdapter(getActivity(),list, db,budgetName);
            expenseList.setAdapter(expenseAdapter);
            expenseList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            expenseList.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
                private int number = 0;
                private ArrayList<Expense> listToDelete = new ArrayList<Expense>();
                private ArrayList<Integer> checkedPosition = expenseAdapter.getCheckedPositions();
                @Override
                public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                    if(checked){
                        number++;
                        listToDelete.add(db.getExpense((int) id));
                        checkedPosition.add(position);
                    }
                    else{
                        number--;
                        listToDelete.remove(db.getExpense((int) id));
                        checkedPosition.remove(checkedPosition.indexOf(position));
                    }
                    mode.setTitle(number + " selected");
                    expenseAdapter.notifyDataSetChanged();
                }

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    MenuInflater inflater = mode.getMenuInflater();
                    inflater.inflate(R.menu.expense_list_context_menu, menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.deleteTransaction:
                            for(int i=0; i<listToDelete.size(); i++){
                                db.deleteExpense(listToDelete.get(i));
                            }
                            expenseAdapter.refresh(db.getExpensesByBudget(budgetID));
                            mode.finish(); // Action picked, so close the CAB
                            return true;
                        default:
                            return false;
                    }
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {
                    checkedPosition.clear();
                    expenseAdapter.notifyDataSetChanged();
                    number=0;
                    listToDelete.clear();

                }
            });
        }

        return view;
    }

    /**public class ExpenseViewHolder extends SwappingHolder implements View.OnLongClickListener {

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

        String previousRange;
        private final StringBuilder sb1 = new StringBuilder();
        private final StringBuilder sb2 = new StringBuilder();

        public ExpenseViewHolder(View itemView) {
            super(itemView, mMultiSelector);
            ButterKnife.bind(this,itemView);
            itemView.setLongClickable(true);
            itemView.setOnLongClickListener(this);
        }

        public void bindExpense(Expense i,Expense previousI,Cursor brCursor) {

            int duration = (int)i.getDuration();
            int weatherID = i.getWeatherID();
            switch (duration){
                case 1:
                    timeLayout.setBackgroundColor(getActivity().getResources().getColor(R.color.blue_dark));
                    break;
                case 2:
                    timeLayout.setBackgroundColor(getActivity().getResources().getColor(R.color.red));
                    break;
                case 3:
                    timeLayout.setBackgroundColor(getActivity().getResources().getColor(R.color.f_green));
                    break;
                case 4:
                    timeLayout.setBackgroundColor(getActivity().getResources().getColor(R.color.purple));
                    break;
                default:
                    timeLayout.setBackgroundColor(getActivity().getResources().getColor(R.color.iron));

            }

            String spent = String.valueOf(duration);
            timeSpentl.setText(spent);
            if(Integer.parseInt(spent) <= 1){
                timeUnit.setText("Hr");
            } else {
                timeUnit.setText("Hrs");
            }

            StringBuilder sb1 = this.sb1;

            sb1.setLength(0);
            sb1.append(DateUtils.formatDateRange(getContext(), brCursor.getLong(0), brCursor.getLong(1),
                    DateUtils.FORMAT_SHOW_DATE|DateUtils.FORMAT_SHOW_YEAR));
            String dateRange = sb1.toString();
            if(previousI != null) {
                Cursor previousC = db.getTimeRangeforExpense(previousI.getBudget(),previousI.getDate().getMillis());
                sb2.setLength(0);
                sb2.append(DateUtils.formatDateRange(getContext(), previousC.getLong(0), previousC.getLong(1),
                        DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR));
                StringBuilder sb2 = this.sb2;
                previousRange = sb2.toString();

                if(dateRange.equals(previousRange)){
                    timeRange.setVisibility(View.GONE);
                } else {
                    timeRange.setVisibility(View.VISIBLE);
                    timeRange.setText(dateRange);
                }
            } else {
                timeRange.setVisibility(View.VISIBLE);
                timeRange.setText(dateRange);
            }

            expenseDescription.setText(i.getDescription());
            expenseLocation.setText(i.getLocation());
            expenseDate.setText(CalendarUtils.toDayStringAbbrev(getContext(),i.getDate().getMillis()));
            expenseStarttime.setText(CalendarUtils.toTimeString(getContext(), i.getStartTime()));
            expenseEndtime.setText(CalendarUtils.toTimeString(getContext(), i.getEndTime()));
            if (weatherID == -1){
                weatherDesc.setVisibility(View.INVISIBLE);
            } else {
                weatherDesc.setImageResource(Utils.getArtResourceForWeatherCondition(weatherID));
            }
        }

        @Override
        public boolean onLongClick(View v) {
            ((AppCompatActivity) getActivity()).startSupportActionMode(mDeleteMode);
           // if (!mMultiSelector.isSelectable()) { // (3)
            mMultiSelector.setSelected(this, true);
            return true;
            //}
            //return false;
        }
        public ExpenseViewHolder(View view) {
         ButterKnife.bind(this,view);
         }
    }

    public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseViewHolder> {

        //private Context context;
        private List<Expense> list;
        //private LayoutInflater inflater;
        private DBHelper db;
        //private String budgetName;
        //private ArrayList<Integer> checkedPositions = new ArrayList<Integer>();
        //private MultiSelector mMultiSelector = new MultiSelector();

        //String previousRange;
        private final StringBuilder sb1 = new StringBuilder();
        private final StringBuilder sb2 = new StringBuilder();

        public ExpenseAdapter(List<Expense> list, DBHelper db) {
            this.list = list;
            this.db = db;
        }

        /**@Override
        public int getCount() {
        return list.size();
        }

         @Override
         public Object getItem(int position) {
         return list.get(position);
         }

        @Override
        public ExpenseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_list_row, parent, false);
            return new ExpenseViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ExpenseAdapter.ExpenseViewHolder viewHolder, int position) {
            Expense i = list.get(position);
            Expense previousI = position>0?list.get(position-1):null;
            Cursor brCursor = db.getTimeRangeforExpense(i.getBudget(),i.getDate().getMillis());
            viewHolder.bindExpense(i,previousI,brCursor);
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
        viewHolder.expenseLocation.setText(i.getLocation());
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

    }**/

}
