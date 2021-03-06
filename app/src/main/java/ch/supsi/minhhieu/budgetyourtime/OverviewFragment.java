package ch.supsi.minhhieu.budgetyourtime;

import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ch.supsi.minhhieu.budgetyourtime.CustomAdapters.BudgetOverviewAdapter;
import ch.supsi.minhhieu.budgetyourtime.CustomAdapters.ExpenseOverviewAdapter;
import ch.supsi.minhhieu.budgetyourtime.Helpers.DBHelper;
import ch.supsi.minhhieu.budgetyourtime.Models.Budget;
import ch.supsi.minhhieu.budgetyourtime.Models.Expense;

/**
 * A placeholder fragment containing a simple view.
 */
public class OverviewFragment extends Fragment implements BudgetActions{


    @BindView(R.id.budget_overview_title)
    TextView budgetOverviewTitle;
    @BindView(R.id.expense_overview_title)
    TextView expenseOverviewTitle;
    @BindView(R.id.budget_overview_list)
    @Nullable
    RecyclerView budgetList;
    @BindView(R.id.expense_overview_list)
    @Nullable
    RecyclerView expenseList;

    private DBHelper db;
    //private BudgetOverviewAdapter budgetOverviewAdapter;
    private ExpenseOverviewAdapter expenseOverviewAdapter;
    FloatingActionButton fab;
    private static int FAB_HIDE_TIMEOUT = 1000;


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
        fab = (FloatingActionButton)getActivity().findViewById(R.id.add_new_activity);
        fab.show();
        ButterKnife.bind(this,view);
        final List<Budget> list = db.getAllBudgets();
        if(list.size()!=0){
            budgetOverviewTitle.setText("Your Budget Overview");
        }
        /**budgetOverviewAdapter = new BudgetOverviewAdapter(getActivity(),list,db, listener);
            budgetList.setAdapter(budgetOverviewAdapter);
            budgetList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                budget = list.get(position);
                bundle.putString("budgetName",budget.name);
                bundle.putLong("budgetID",budget.getId());
                openItemListFragment(bundle);
            }
        });**/

        RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(getActivity());
        RecyclerView.LayoutManager mLayoutManager2 = new LinearLayoutManager(getActivity());
        budgetList.setLayoutManager(mLayoutManager1);
        expenseList.setLayoutManager(mLayoutManager2);
        budgetList.setAdapter(new BudgetOverviewAdapter(getActivity(), list, db,
                new BudgetOverviewAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(Budget b) {
                                Bundle bundle = new Bundle();
                                //budget = list.get(position);
                                bundle.putString("budgetName",b.name);
                                bundle.putLong("budgetID",b.getId());
                                openItemListFragment(bundle);
                        }
            },
                new BudgetOverviewAdapter.OnItemLongClickListener(){

                    @Override
                    public void onItemLongClick(final Budget b) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        String[] budgetEdit = {"Change name","Change amount", "Delete budget"};
                        builder.setTitle("Budget Edit Options:");
                        builder.setItems(budgetEdit, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                updateBudget(which, b.getId());
                            }
                        });
                        builder.show();
                    }
                }
        ));
        /**budgetList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                budget = list.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                String[] budgetEdit = {"Change name","Change amount", "Delete budget"};
                builder.setTitle("Budget Edit Options:");
                builder.setItems(budgetEdit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateBudget(which, budget.getId());
                    }
                });
                builder.show();
                return false;
            }
        });**/

        final List<Expense> expenseOverviewList = db.getLatestThreeExpenseRecords();
        if(expenseOverviewList.size()!=0){
            expenseOverviewTitle.setText("Recent Expenses");
        }
        expenseOverviewAdapter = new ExpenseOverviewAdapter(getActivity(),expenseOverviewList,db);
        expenseList.setAdapter(expenseOverviewAdapter);
        return view;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.add_budget_button, menu);
    }

    public void openItemListFragment(Bundle args){
        ExpenseDetailListFragment newFragment = new ExpenseDetailListFragment();
        newFragment.setArguments(args);
        android.support.v4.app.FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container,newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void updateBudget(final int which,final long budgetID){
        // get prompts.xml view
        if(which != 2) {
            createBudgetUpdateDialog(which,budgetID);
        } else {
            createBudgetDeleteDialog(which,budgetID);
        }
    }

    public void createBudgetUpdateDialog(final int which,final long budgetID){
        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView = li.inflate(R.layout.budget_edit_prompt, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getActivity());

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final TextView dialogTitle = ButterKnife.findById(promptsView, R.id.budget_edit_tile);
        final EditText userInput = ButterKnife.findById(promptsView, R.id.editTextDialogUserInput);
        switch (which) {
            case 0:
                dialogTitle.setText("Type New Budget Name:");
                userInput.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
            case 1:
                dialogTitle.setText("Type New Budget Amount in Hours:");
                userInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
        }

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                switch (which) {
                                    case 0:
                                        String newName = userInput.getText().toString().trim();
                                        db.updateBudgetName(newName,budgetID);
                                        break;
                                    case 1:
                                        int newAmount = Integer.parseInt(userInput.getText().toString().trim());
                                        db.updateBudgetAmount(newAmount,budgetID);
                                        break;
                                }

                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        alertDialogBuilder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                updateBudgetFragment();
            }
        });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public void createBudgetDeleteDialog(final int which, final long budgetID){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Delete Budget");
        builder.setMessage("All the actitvities under this budget will be deleted. Do you want to proceed?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            int i = 0;
            public void onClick(DialogInterface dialog, int which) {
                 i = db.deleteBudget(budgetID);
                if (i != 0){
                    Toast.makeText(getActivity(), R.string.delete_budget_ok, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    Toast.makeText(getActivity(), R.string.delete_budget_failed, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                updateBudgetFragment();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void updateBudgetFragment(){
        OverviewFragment newFragment = new OverviewFragment();
        android.support.v4.app.FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container,newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
