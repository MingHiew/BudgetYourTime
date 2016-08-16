package ch.supsi.minhhieu.budgetyourtime;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.multidex.MultiDex;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ch.supsi.minhhieu.budgetyourtime.Helpers.DBHelper;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.add_new_activity)
    FloatingActionButton fab_new_activity;

    private static final String TAG = "HomeActivity";
    DBHelper db;
    Toolbar toolbar = null;
    public static final int OVERVIEW_FRAGMENT = 1;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        db = DBHelper.getInstance(this);
        ButterKnife.bind(this);
        fab_new_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewItem();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, OverviewFragment.newInstance())
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_budget_button, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_budget) {
            createNewBudget();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.toolbar
                .setTitle(R.string.title_fragment_overview);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_overview) {
            OverviewFragment overviewFragment = new OverviewFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container,overviewFragment);
            fragmentTransaction.commit();
            fab_new_activity.show();
        } else if (id == R.id.nav_budget_details) {
            BudgetDetailPagerFragment budgetDetailFragment = new BudgetDetailPagerFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container,budgetDetailFragment);
            fragmentTransaction.commit();
            fab_new_activity.hide();
        } else if (id == R.id.nav_stats) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void createNewBudget() {
        Intent budgetIntent = new Intent(HomeActivity.this,AddEditBudgetActivity.class);
        budgetIntent.putExtra("typeOfDialog",AddEditBudgetActivity.ADD_NEW_BUDGET);
        startActivityForResult(budgetIntent,AddEditBudgetActivity.ADD_NEW_BUDGET);
    }

    private void createNewItem(){
        Intent itemIntent = new Intent(HomeActivity.this, AddEditItemActivity.class);
        itemIntent.putExtra("typeOfDialog",AddEditItemActivity.ADD_NEW_ITEM);
        startActivityForResult(itemIntent,AddEditItemActivity.ADD_NEW_ITEM);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case AddEditBudgetActivity.ADD_NEW_BUDGET:
                OverviewFragment overviewFragment = new OverviewFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container,overviewFragment);
                fragmentTransaction.commit();
                break;
            case AddEditItemActivity.ADD_NEW_ITEM:
                OverviewFragment overviewFragment1 = new OverviewFragment();
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container,overviewFragment1);
                fragmentTransaction.commit();
                break;

        }

    }
}
