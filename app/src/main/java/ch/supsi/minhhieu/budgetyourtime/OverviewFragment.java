package ch.supsi.minhhieu.budgetyourtime;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ch.supsi.minhhieu.budgetyourtime.CustomAdapters.BudgetAdapter;
import ch.supsi.minhhieu.budgetyourtime.Helpers.DBHelper;
import ch.supsi.minhhieu.budgetyourtime.Models.Budget;

/**
 * A placeholder fragment containing a simple view.
 */
public class OverviewFragment extends Fragment {

    @BindView(R.id.budget_overview_list)
    @Nullable ListView budgetList;



    //@BindView(R.id.testTV)
    //TextView testTV;
    private DBHelper db;
    private BudgetAdapter budgetAdapter;
    public static OverviewFragment newInstance() {
        return new OverviewFragment();
    }

    public OverviewFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = DBHelper.getInstance(this.getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_overview, container, false);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle("Overview");
        ButterKnife.bind(this,view);
        List<Budget> list = db.getAllBudgets();
        //List<Budget> list = db.getBudgetListByPeriod(1471194000000L,1471798799999L);
        budgetAdapter = new BudgetAdapter(getActivity(),list,db);
        budgetList.setAdapter(budgetAdapter);
        //testTV.setText(output);
        return view;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }
}
