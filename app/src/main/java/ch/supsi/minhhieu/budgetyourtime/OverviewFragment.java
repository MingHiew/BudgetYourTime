package ch.supsi.minhhieu.budgetyourtime;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
public class OverviewFragment extends Fragment implements BudgetActions{

    @BindView(R.id.budget_overview_list)
    @Nullable ListView budgetList;

    private DBHelper db;
    private BudgetAdapter budgetAdapter;
    private Budget budget = new Budget();
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
        final List<Budget> list = db.getAllBudgets();
        budgetAdapter = new BudgetAdapter(getActivity(),list,db);
        budgetList.setAdapter(budgetAdapter);
        budgetList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                budget = list.get(position);
                bundle.putString("budgetName",budget.name);
                bundle.putLong("budgetID",budget.getId());
                openItemListFragment(bundle);
            }
        });

        budgetList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                budget = list.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                String[] budgetEdit = {"Change name","Change amount", "Delete budget"};
                builder.setTitle("Budget Edit Options:");
                builder.setItems(budgetEdit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                break;
                            case 1:
                                break;
                            case 2:
                                break;
                        }
                    }
                });
                builder.show();
                return false;
            }
        });
        return view;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.add_budget_button, menu);
    }

    public void openItemListFragment(Bundle args){
        ItemDetailListFragment newFragment = new ItemDetailListFragment();
        newFragment.setArguments(args);
        android.support.v4.app.FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container,newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
