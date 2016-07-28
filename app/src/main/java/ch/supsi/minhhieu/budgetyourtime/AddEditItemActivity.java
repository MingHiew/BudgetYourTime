package ch.supsi.minhhieu.budgetyourtime;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.widget.DatePicker;
import android.content.Context;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.droidparts.widget.ClearableEditText;


import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import ch.supsi.minhhieu.budgetyourtime.Models.Item;
import ch.supsi.minhhieu.budgetyourtime.Utils.CalendarUtils;

public class AddEditItemActivity extends FragmentActivity {

    @BindView(R.id.act_starttime)
    TextView startTime;
    @BindView(R.id.act_endtime)
    TextView endTime;
    @BindView(R.id.activityDate)
    TextView activityDate;

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
        presetViews();
        setupViews();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }
    private void presetViews(){
        if (activityDate == null) return;
        final long today = CalendarUtils.today();
        activityDate.setText(CalendarUtils.toDayString(AddEditItemActivity.this, today));
        startTime.setText(CalendarUtils.toTimeString(AddEditItemActivity.this,
                CalendarUtils.getNearestHourAndMinutes()));
        endTime.setText(CalendarUtils.toTimeString(AddEditItemActivity.this,
                CalendarUtils.getNearestHourAndMinutes()+3600000));;

    }
    private void setupViews() {
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
    }

    private void setDate() {
    }

    /*private void setTime(boolean start){
        EditText label = start ? startTime : endTime;
        Calendar dateTime = start ? mItem.startTime : mItem.endTime;
        int unroundedMinutes = dateTime.get(Calendar.MINUTE);
        int mod = unroundedMinutes % 15;
        dateTime.add(Calendar.MINUTE, mod < 8 ? -mod : (15-mod));
        label.setText(CalendarUtils.toTimeString(AddEditItemActivity.this, dateTime.getTimeInMillis()));
    }*/
    private void showDatePicker() {
        final Calendar date = Calendar.getInstance();
        new DatePickerDialog(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date.set(year, monthOfYear, dayOfMonth);
                activityDate.setText(CalendarUtils.toDayString(AddEditItemActivity.this, date.getTimeInMillis()));
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
                } else {
                    endTime.setText(CalendarUtils.toTimeString(AddEditItemActivity.this,
                            date.getTimeInMillis()));
                }
            }
        }, date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE), false).show();
    }

    private void setTitle() {
    }
}