package ch.supsi.minhhieu.budgetyourtime;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.support.v4.app.DialogFragment;
import android.widget.EditText;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.joda.time.DateTimeZone;
import org.joda.time.MutableDateTime;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import ch.supsi.minhhieu.budgetyourtime.Helpers.DBHelper;
import ch.supsi.minhhieu.budgetyourtime.Models.Item;
import ch.supsi.minhhieu.budgetyourtime.Utils.CalendarUtils;
import co.geeksters.googleplaceautocomplete.lib.CustomAutoCompleteTextView;

public class AddEditItemActivity extends FragmentActivity {

    @BindView(R.id.item_act_title)
    TextView itemActivityTitle;
    @BindView(R.id.spinner_budget_name)
    Spinner budgetName;
    @BindView(R.id.save_item)
    ImageView saveItem;
    @BindView(R.id.act_starttime)
    TextView startTime;
    @BindView(R.id.act_endtime)
    TextView endTime;
    @BindView(R.id.activityDate)
    TextView activityDate;
    @BindView(R.id.locationAutocomplete)
    CustomAutoCompleteTextView autocompleteView;
    @BindView(R.id.item_description)
    EditText itemDescription;

    public static final int ADD_NEW_ITEM = 3;
    public static final int EDIT_ITEM = 4;
    private static MutableDateTime mDate = new MutableDateTime();

    DBHelper db;

    private DialogFragment timeFragment;
    private int mBudget, itemID,typeOfDialog;
    private long mStartTime, mEndTime;
    private String locationText, descriptionText;
    private Item mItem = new Item();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_edit_item);
        ButterKnife.bind(this);
        Intent intent = getIntent();

        db = DBHelper.getInstance(this);
        typeOfDialog = intent.getIntExtra("typeOfDialog",2);
        itemID = (int) intent.getLongExtra("itemID", 0);
        presetAddNewActivityViews();
        setupViewsBehaviours();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }
    private void presetAddNewActivityViews(){
        if(typeOfDialog == ADD_NEW_ITEM){
            itemActivityTitle.setText("Your New Activity");
            if (activityDate == null) return;
            long today = MutableDateTime.now().getMillis();
            activityDate.setText(CalendarUtils.toDayString(AddEditItemActivity.this, today));
            mStartTime = CalendarUtils.getNearestHourAndMinutes();
            startTime.setText(CalendarUtils.toTimeString(AddEditItemActivity.this, mStartTime));
            mEndTime = CalendarUtils.getNearestHourAndMinutes()+3600000;
            endTime.setText(CalendarUtils.toTimeString(AddEditItemActivity.this,mEndTime));
        } else if(typeOfDialog == EDIT_ITEM){
            itemActivityTitle.setText("Edit Your Activity");
            mItem = db.getItem(itemID);
            activityDate.setText(CalendarUtils.toDayString(AddEditItemActivity.this,mItem.getDate().getMillis()));
            mStartTime = mItem.getStartTime();
            startTime.setText(CalendarUtils.toTimeString(AddEditItemActivity.this,mStartTime));
            mEndTime = mItem.getEndTime();
            endTime.setText(CalendarUtils.toTimeString(AddEditItemActivity.this,mEndTime));
            autocompleteView.setText(mItem.getLocation());
            itemDescription.setText(mItem.getDescription());
        }

    }
    private void setupViewsBehaviours() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            SpinnerAdapter mSpinnerAdapter = new SimpleCursorAdapter(this,
                    android.R.layout.simple_list_item_1,
                    db.getAllBudgetCursor(true),
                    new String[] { db.KEY_NAME },
                    new int[] { android.R.id.text1 },
                    0);
            budgetName.setAdapter(mSpinnerAdapter);
        }

        //Preset the date
        activityDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });
        //Preset the hour and minute
        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker(true);
            }
        });
        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker(false);
            }
        });
        autocompleteView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String description = (String) parent.getItemAtPosition(position).toString();
            }
        });

        budgetName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mBudget = (int) id;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        setOnclickSaveButton();

    }

    private void showDatePicker() {
        timeFragment = new DatePickerFragment();
        timeFragment.show(getSupportFragmentManager(), "timePicker");

    }

    private void showTimePicker(final boolean start) {
        //final Calendar date = Calendar.getInstance();
        //final DateTimeZone timeZone = DateTimeZone.getDefault();
        final MutableDateTime dt = new MutableDateTime();
        new CustomTimePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                //date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                //date.set(Calendar.MINUTE, minute);
                dt.setHourOfDay(hourOfDay);
                dt.setMinuteOfHour(minute);
                if (start) {
                    //mStartTime = date.getTimeInMillis();
                    mStartTime = dt.getMillis();
                    startTime.setText(CalendarUtils.toTimeString(AddEditItemActivity.this, mStartTime));
                } else {
                    //mEndTime = date.getTimeInMillis();
                    mEndTime = dt.getMillis();
                    endTime.setText(CalendarUtils.toTimeString(AddEditItemActivity.this,
                            mEndTime));
                }
            }
        }, dt.getHourOfDay(),dt.getMinuteOfHour(),false).show();
        //date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE), false).show();
    }

    private void setOnclickSaveButton() {
        saveItem.setOnClickListener(new View.OnClickListener() {
            boolean returnval = false;
            @Override
            public void onClick(View v) {
                locationText = autocompleteView.getText().toString().trim();
                descriptionText = itemDescription.getText().toString().trim();
                Boolean validTime = ValidateTime(mStartTime,mEndTime);
                if (validTime == false){
                    Toast.makeText(AddEditItemActivity.this, R.string.invalid_time, Toast.LENGTH_SHORT).show();
                } else{
                    if(typeOfDialog == ADD_NEW_ITEM){
                        mItem = new Item(mDate,mStartTime,mEndTime,locationText,descriptionText,mBudget);
                        returnval = db.addItem(mItem);
                        if (returnval == true){
                            AddEditItemActivity.this.setResult(RESULT_OK);
                            Toast.makeText(AddEditItemActivity.this, R.string.create_item, Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(AddEditItemActivity.this, R.string.invalid_date, Toast.LENGTH_SHORT).show();
                        }
                    } else if (typeOfDialog == EDIT_ITEM){
                        long duration = mEndTime - mStartTime;
                        mItem = new Item(itemID,mDate,mStartTime,mEndTime,locationText,descriptionText,mBudget);
                        returnval = db.editItem(mItem);
                        if (returnval == true){
                            AddEditItemActivity.this.setResult(RESULT_OK);
                            Toast.makeText(AddEditItemActivity.this, R.string.save_item, Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                }
            }
        });
    }

    private boolean ValidateTime (long startTime, long endTime){
        if (TimeUnit.MILLISECONDS.toHours(startTime) < TimeUnit.MILLISECONDS.toHours(endTime)){
            return true;
        } else {return false;}
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "AddEditItem Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://ch.supsi.minhhieu.budgetyourtime/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "AddEditItem Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://ch.supsi.minhhieu.budgetyourtime/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();

    }

    public void updateDate(long dt){
        activityDate.setText(CalendarUtils.toDayString(AddEditItemActivity.this, dt));

    }

    public class DatePickerFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {

        public DatePickerFragment() {
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            DateTimeZone timeZone = DateTimeZone.getDefault();
            final MutableDateTime dt = new MutableDateTime(timeZone);
            int year = dt.getYear();
            int month = dt.getMonthOfYear();
            int day = dt.getDayOfMonth();

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month -1, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            mDate.setYear(year);
            mDate.setMonthOfYear(month+1);
            mDate.setDayOfMonth(day);
            updateDate(mDate.getMillis());
        }
    }
}
