package ch.supsi.minhhieu.budgetyourtime;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class BudgetDetailListFragment extends Fragment {

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
        List<Budget> list = db.getBudgetListByPeriod(startDate,endDate);
        budgetDetailAdapter = new BudgetDetailAdapter(getActivity(),list,db, startDate, endDate);
        budgetDetailList.setAdapter(budgetDetailAdapter);

        return view;
    }



}
