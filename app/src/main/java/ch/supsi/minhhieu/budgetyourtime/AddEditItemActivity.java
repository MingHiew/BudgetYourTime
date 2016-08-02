package ch.supsi.minhhieu.budgetyourtime;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import java.util.Calendar;
import butterknife.BindView;
import butterknife.ButterKnife;
import ch.supsi.minhhieu.budgetyourtime.Helpers.DBHelper;
import ch.supsi.minhhieu.budgetyourtime.Models.Item;
import ch.supsi.minhhieu.budgetyourtime.Utils.CalendarUtils;
import co.geeksters.googleplaceautocomplete.lib.CustomAutoCompleteTextView;

public class AddEditItemActivity extends FragmentActivity {

    @BindView(R.id.item_act_title)
    TextView itemActivityTitle;
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

    public static final int ADD_NEW_ITEM = 1;
    public static final int EDIT_ITEM = 2;
    DBHelper db;
    private int itemID,typeOfDialog;
    private String locationText, descriptionText,startTimeText, endTimeText, dateText;
    private long duration;
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
        itemID = (int) intent.getLongExtra("itemId", 1);
        if (typeOfDialog == ADD_NEW_ITEM) {
            presetAddNewActivityViews();
        }
        setupViewsBehaviours();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }
    private void presetAddNewActivityViews(){
        if (activityDate == null) return;
        final long today = CalendarUtils.today();
        activityDate.setText(CalendarUtils.toDayString(AddEditItemActivity.this, today));
        mItem.date.setTimeInMillis(today);
        startTime.setText(CalendarUtils.toTimeString(AddEditItemActivity.this,
                CalendarUtils.getNearestHourAndMinutes()));
        mItem.startTime.setTimeInMillis(CalendarUtils.getNearestHourAndMinutes());
        endTime.setText(CalendarUtils.toTimeString(AddEditItemActivity.this,
                CalendarUtils.getNearestHourAndMinutes()+3600000));
        mItem.endTime.setTimeInMillis(CalendarUtils.getNearestHourAndMinutes()+3600000);
        //autocompleteView.setAdapter(new PlacesAutoCompleteAdapter(AddEditItemActivity.this, R.layout.location_autocomplete));


    }
    private void setupViewsBehaviours() {
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
                //Toast.makeText(AddEditItemActivity.this, description, Toast.LENGTH_SHORT).show();
            }
        });

        setOnclickSaveButton();

    }


    private void showDatePicker() {
        final Calendar date = Calendar.getInstance();
        new DatePickerDialog(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date.set(year, monthOfYear, dayOfMonth);
                activityDate.setText(CalendarUtils.toDayString(AddEditItemActivity.this, date.getTimeInMillis()));
                mItem.setDate(date);
            }
        }, date.get(Calendar.YEAR),
                date.get(Calendar.MONTH),
                date.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    private void showTimePicker(final boolean start) {
        final Calendar date = start ? mItem.startTime : mItem.endTime;

        new CustomTimePickerDialog(this, AlertDialog.THEME_TRADITIONAL, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                date.set(Calendar.MINUTE, minute);
                if (start) {
                    startTime.setText(CalendarUtils.toTimeString(AddEditItemActivity.this,
                            date.getTimeInMillis()));
                    mItem.setStartTime(date);
                } else {
                    endTime.setText(CalendarUtils.toTimeString(AddEditItemActivity.this,
                            date.getTimeInMillis()));
                    mItem.setEndTime(date);
                }
            }
        }, date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE), false).show();
    }

    private void setOnclickSaveButton() {
        saveItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationText = autocompleteView.getText().toString().trim();
                descriptionText = itemDescription.getText().toString().trim();
                duration = mItem.endTime.getTimeInMillis() - mItem.startTime.getTimeInMillis();
            }
        });
    }
}
