package ch.supsi.minhhieu.budgetyourtime;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SpinnerAdapter;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ch.supsi.minhhieu.budgetyourtime.CustomAdapters.BudgetPagerAdapter;
import ch.supsi.minhhieu.budgetyourtime.Helpers.DBHelper;
import ch.supsi.minhhieu.budgetyourtime.Utils.Period;


public class BudgetDetailPagerFragment extends Fragment {

    @BindView(R.id.budget_detail_viewpager)
    ViewPager pager;

    private DBHelper db;
    private boolean isWeekly;
    List<Period> prdList;
    private Toolbar toolbar=null;
    private String[] budget_type={"Weekly","Monthly"};
    FloatingActionButton fab;

    public BudgetDetailPagerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = DBHelper.getInstance(this.getContext());
        Bundle bundle = getArguments();
        if (bundle != null) {
            isWeekly = bundle.getBoolean("isWeekly");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.from(getActivity()).inflate(R.layout.fragment_budget_detail, container, false);
        ButterKnife.bind(this,view);
        setHasOptionsMenu(true);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();


        actionBar.setTitle("Budget Detail View");
        if(isWeekly){
            prdList = db.getHistoricalWeeklyIntervals();
        } else {
            prdList = db.getHistoricalMonthlyIntervals();
        }
        pager.setAdapter(buildPagerAdapter(prdList));
        return view;
    }

    private BudgetPagerAdapter buildPagerAdapter(List<Period> prdList){
        return(new BudgetPagerAdapter(getActivity(), getChildFragmentManager(),prdList));

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.budget_detail_view, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_budget_type:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Budget Type").setItems(budget_type, new DialogInterface.OnClickListener() {
                    BudgetDetailPagerFragment frag = new BudgetDetailPagerFragment();
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    Bundle args = new Bundle();

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                args.putBoolean("isWeekly",true);
                                frag.setArguments(args);
                                ft.replace(R.id.fragment_container,frag);
                                ft.commit();
                                break;
                            case 1:
                                args.putBoolean("isWeekly",false);
                                frag.setArguments(args);
                                ft.replace(R.id.fragment_container,frag);
                                ft.commit();
                                break;
                        }
                    }
                }).create().show();;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

    }

}
