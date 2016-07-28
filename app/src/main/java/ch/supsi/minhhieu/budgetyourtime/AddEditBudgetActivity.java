package ch.supsi.minhhieu.budgetyourtime;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.Window;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import ch.supsi.minhhieu.budgetyourtime.Helpers.DBHelper;

public class AddEditBudgetActivity extends Activity {

    @BindView(R.id.budget_name)
    EditText budgetName;
    @BindView(R.id.spinner_budget_type)
    Spinner budgetType;
    @BindView(R.id.budget_amount)
    EditText budgetAmount;
    @BindView(R.id.budget_description)
    EditText budgetDescription;
    @BindView(R.id.add_edit_budget_title)
    TextView addEditBudgetTitle;

    public static final int ADD_NEW_BUDGET = 1;
    public static final int EDIT_BUDGET = 2;
    DBHelper db;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_edit_budget);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        db = DBHelper.getInstance(this);
        //ButterKnife.bind(this);
        //budgetAmount.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
    }

    private void initLook() {
    }

}
