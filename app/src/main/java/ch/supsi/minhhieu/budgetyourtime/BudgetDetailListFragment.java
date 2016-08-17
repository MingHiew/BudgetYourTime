package ch.supsi.minhhieu.budgetyourtime;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ch.supsi.minhhieu.budgetyourtime.CustomAdapters.BudgetDetailAdapter;
import ch.supsi.minhhieu.budgetyourtime.Helpers.DBHelper;
import ch.supsi.minhhieu.budgetyourtime.Models.Budget;


/**
 * A simple {@link Fragment} subclass.
 */
public class BudgetDetailListFragment extends Fragment implements BudgetActions {

    @BindView(R.id.budget_detail_startdate)
    TextView budgetDetailStartDate;
    @BindView(R.id.budget_detail_enddate)
    TextView budgetDetailEndDate;
    @BindView(R.id.budget_detail_list)
    @Nullable ListView budgetDetailList;

    private long startDate;
    private long endDate;
    private DBHelper db;
    private BudgetDetailAdapter budgetDetailAdapter;
    private Budget budget = new Budget();


    private static final String KEY_POSITION="position";

    public BudgetDetailListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = DBHelper.getInstance(this.getContext());
        Bundle bundle = getArguments();
        if(bundle!= null) {
            startDate = bundle.getLong("startdate");
            endDate = bundle.getLong("enddate");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_budget_detail_list, container, false);
        ButterKnife.bind(this,view);
        budgetDetailStartDate.setText(DateUtils.formatDateTime(getContext(), startDate, DateUtils.FORMAT_SHOW_DATE));
        budgetDetailEndDate.setText(DateUtils.formatDateTime(getContext(), endDate, DateUtils.FORMAT_SHOW_DATE));
        final List<Budget> list = db.getBudgetListByPeriod(startDate,endDate);
        budgetDetailAdapter = new BudgetDetailAdapter(getActivity(),list,db, startDate, endDate);
        budgetDetailList.setAdapter(budgetDetailAdapter);
        budgetDetailList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                budget = list.get(position);
                bundle.putString("budgetName",budget.name);
                bundle.putLong("budgetID",budget.getId());
                openItemListFragment(bundle);
            }
        });
        return view;
    }




    @Override
    public void openItemListFragment(Bundle args) {
        ItemDetailListFragment newFragment = new ItemDetailListFragment();
        newFragment.setArguments(args);
        android.support.v4.app.FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container,newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
