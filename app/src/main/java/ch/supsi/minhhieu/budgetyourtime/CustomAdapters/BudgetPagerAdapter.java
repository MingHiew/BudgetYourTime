package ch.supsi.minhhieu.budgetyourtime.CustomAdapters;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ch.supsi.minhhieu.budgetyourtime.BudgetDetailListFragment;
import ch.supsi.minhhieu.budgetyourtime.Helpers.DBHelper;
import ch.supsi.minhhieu.budgetyourtime.R;
import ch.supsi.minhhieu.budgetyourtime.Utils.Period;

/**
 * Created by acer on 12/08/2016.
 */
public class BudgetPagerAdapter extends FragmentStatePagerAdapter {

    private Context ctxt=null;
    private List<Period> list;
    private DBHelper db;
    private static final String KEY_STARTDATE = "startdate";
    private static final String KEY_ENDDATE = "enddate";
    public BudgetPagerAdapter(Context ctxt, FragmentManager fm,List<Period> list) {
        super(fm);
        this.ctxt=ctxt;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Fragment getItem(int position) {
        Period pr = list.get(position);
        Bundle bundle = new Bundle();
        BudgetDetailListFragment frag = new BudgetDetailListFragment();
        bundle.putLong(KEY_STARTDATE,pr.start);
        bundle.putLong(KEY_ENDDATE,pr.end);
        frag.setArguments(bundle);
        return frag;
    }
}
