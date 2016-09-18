package ch.supsi.minhhieu.budgetyourtime;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ch.supsi.minhhieu.budgetyourtime.CustomAdapters.BudgetDetailAdapter;
import ch.supsi.minhhieu.budgetyourtime.CustomAdapters.BudgetOverviewAdapter;
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
    @Nullable
    RecyclerView budgetDetailList;

    private long startDate;
    private long endDate;
    private DBHelper db;
    private BudgetDetailAdapter budgetDetailAdapter;
    private Budget budget = new Budget();

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
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        budgetDetailList.setLayoutManager(mLayoutManager);

        budgetDetailList.setAdapter(new BudgetDetailAdapter(getActivity(),list,db, startDate, endDate,
                new BudgetDetailAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Budget b) {
                        Bundle bundle = new Bundle();
                        //budget = list.get(position);
                        bundle.putString("budgetName",b.name);
                        bundle.putLong("budgetID",b.getId());
                        openItemListFragment(bundle);
                    }
                },
                new BudgetDetailAdapter.OnItemLongClickListener(){
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
        }));
        /**budgetDetailList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                budget = list.get(position);
                bundle.putString("budgetName",budget.name);
                bundle.putLong("budgetID",budget.getId());
                openItemListFragment(bundle);
            }
        });

        budgetDetailList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
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
        return view;
    }



    public void openItemListFragment(Bundle args) {
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
        updateBudgetFragment();
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
            public void onClick(DialogInterface dialog, int which) {
                int i = db.deleteBudget(budgetID);
                if (i != 0){
                    Toast.makeText(getActivity(), R.string.delete_item_ok, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    Toast.makeText(getActivity(), R.string.delete_item_failed, Toast.LENGTH_SHORT).show();
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
        BudgetDetailPagerFragment newFragment = new BudgetDetailPagerFragment();
        android.support.v4.app.FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container,newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
