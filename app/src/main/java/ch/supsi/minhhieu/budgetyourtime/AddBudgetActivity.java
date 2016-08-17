package ch.supsi.minhhieu.budgetyourtime;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import ch.supsi.minhhieu.budgetyourtime.Helpers.DBHelper;
import ch.supsi.minhhieu.budgetyourtime.Models.Budget;
import ch.supsi.minhhieu.budgetyourtime.Utils.LocalizableEnum;
import ch.supsi.minhhieu.budgetyourtime.Utils.RecurUtils;
import ch.supsi.minhhieu.budgetyourtime.Utils.RecurUtils.Recur;
import ch.supsi.minhhieu.budgetyourtime.Utils.RecurUtils.RecurInterval;
import ch.supsi.minhhieu.budgetyourtime.Utils.Utils;

public class AddBudgetActivity extends Activity {

    @BindView(R.id.budget_name)
    EditText budgetName;
    @BindView(R.id.spinner_budget_type)
    Spinner budgetType;
    @BindView(R.id.budget_amount)
    EditText budgetAmount;
    @BindView(R.id.budget_act_title)
    TextView addEditBudgetTitle;
    @BindView(R.id.save_budget)
    ImageView saveBudget;

    public static final int ADD_NEW_BUDGET = 1;
    public static final int EDIT_BUDGET = 2;
    DBHelper db;

    private String nameText, typeText, amountText;
    private ProgressDialog loadingSpinner;

    private String LOG;
    private Budget budget = new Budget();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_budget);
        ButterKnife.bind(this);
        db = DBHelper.getInstance(this);
        addEditBudgetTitle.setText("Add New Budget");
        addSpinnerItems(budgetType, new RecurInterval[]{RecurInterval.WEEKLY, RecurInterval.MONTHLY});
        setOnclickSave();

    }

    private void addSpinnerItems(Spinner spinner, LocalizableEnum[] a) {
        int length = a.length;
        SpinnerItem[] items = new SpinnerItem[length];
        for (int i=0; i<length; i++) {
            LocalizableEnum x = a[i];
            String title = getString(x.getTitleId());
            String value = x.name();
            items[i] = new SpinnerItem(title, value);
        }
        ArrayAdapter<SpinnerItem> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    protected RecurInterval getRecurInterval(Object item) {
        return RecurInterval.valueOf(((SpinnerItem)item).value);
    }

    private static class SpinnerItem {
        public final String title;
        public final String value;

        public SpinnerItem(String title, String value) {
            //super();
            this.title = title;
            this.value = value;
        }

        @Override
        public String toString() {
            return title;
        }

    }

    private void setOnclickSave() {
        saveBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameText = budgetName.getText().toString().trim();
                amountText = budgetAmount.getText().toString().trim();
                RecurInterval interval = getRecurInterval(budgetType.getSelectedItem());
                Recur r = RecurUtils.createRecur(interval);
                typeText = r.toString();
                if(amountText.equals("")){
                    Toast.makeText(AddBudgetActivity.this, getResources().getString(R.string.error_edit_amount), Toast.LENGTH_SHORT).show();
                } else {
                    loadingSpinner = new ProgressDialog(AddBudgetActivity.this);
                    loadingSpinner.setMessage("Creating Budget...");
                    loadingSpinner.show();
                    StringBuilder sb1 = new StringBuilder();
                    StringBuilder sb2 = new StringBuilder();
                    StringBuilder sb3 = new StringBuilder();
                    sb1.append("name:").append(nameText);
                    sb2.append("amount:").append(amountText);
                    sb3.append("type:").append(typeText);
                    CreateBudgetAsyncTask task = new CreateBudgetAsyncTask();
                    task.execute(sb1.toString(),sb2.toString(),sb3.toString());
                }
            }
        });
    }

    public class CreateBudgetAsyncTask extends AsyncTask<String, Void, Void>{

        @Override
        protected Void doInBackground(String... params) {
            HashMap<String, String> budgetValueMap = Utils.toMap1(params);
            budget = new Budget(budgetValueMap.get("name")
                                ,Integer.parseInt(budgetValueMap.get("amount"))
                                ,budgetValueMap.get("type"));
            db.addNewBudget(budget);
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);


        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(AddBudgetActivity.this, getResources().getString(R.string.save_budget), Toast.LENGTH_SHORT).show();
            AddBudgetActivity.this.setResult(RESULT_OK);
            finish();
        }
    }

}
