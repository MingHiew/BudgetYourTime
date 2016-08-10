package ch.supsi.minhhieu.budgetyourtime;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import ch.supsi.minhhieu.budgetyourtime.Helpers.DBHelper;
import ch.supsi.minhhieu.budgetyourtime.Models.Budget;
import ch.supsi.minhhieu.budgetyourtime.Utils.LocalizableEnum;
import ch.supsi.minhhieu.budgetyourtime.Utils.RecurUtils;
import ch.supsi.minhhieu.budgetyourtime.Utils.RecurUtils.Recur;
import ch.supsi.minhhieu.budgetyourtime.Utils.RecurUtils.RecurInterval;

public class AddEditBudgetActivity extends Activity {

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

    private int budgetID, typeOfDialog;
    private String nameText, typeText, amountText;
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
        setContentView(R.layout.activity_add_edit_budget);
        ButterKnife.bind(this);
        db = DBHelper.getInstance(this);

        Intent intent = getIntent();
        typeOfDialog = intent.getIntExtra("typeOfDialog",2);
        budgetID = (int) intent.getLongExtra("budgetID",1);
        if(typeOfDialog == ADD_NEW_BUDGET){
            addEditBudgetTitle.setText("Add New Budget");
        } else if (typeOfDialog == EDIT_BUDGET){
            addEditBudgetTitle.setText("Edit Budget");
        }
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
                    Toast.makeText(AddEditBudgetActivity.this, getResources().getString(R.string.error_edit_amount), Toast.LENGTH_SHORT).show();
                } else {
                    switch (typeOfDialog) {
                        case ADD_NEW_BUDGET:
                            budget = new Budget(nameText,Integer.parseInt(amountText),typeText);
                            db.addNewBudget(budget);
                            Toast.makeText(AddEditBudgetActivity.this, getResources().getString(R.string.save_budget), Toast.LENGTH_SHORT).show();
                            break;

                        case EDIT_BUDGET:
                            break;
                    }
                }
                AddEditBudgetActivity.this.setResult(RESULT_OK);
                finish();
            }
        });
    }

}
