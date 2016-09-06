package ch.supsi.minhhieu.budgetyourtime;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
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
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import ch.supsi.minhhieu.budgetyourtime.Helpers.DBHelper;
import ch.supsi.minhhieu.budgetyourtime.Models.Item;
import ch.supsi.minhhieu.budgetyourtime.Utils.CalendarUtils;
import ch.supsi.minhhieu.budgetyourtime.Utils.Utils;
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
    private String locationText, descriptionText, mLocality;
    private Item mItem = new Item();
    private ProgressDialog loadingSpinner;

    private int weatherid = -1;

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
            weatherid = mItem.getWeatherID();
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

        final MutableDateTime dt = new MutableDateTime();
        new CustomTimePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                dt.setHourOfDay(hourOfDay);
                dt.setMinuteOfHour(minute);
                if (start) {
                    mStartTime = dt.getMillis();
                    startTime.setText(CalendarUtils.toTimeString(AddEditItemActivity.this, mStartTime));
                } else {
                    mEndTime = dt.getMillis();
                    endTime.setText(CalendarUtils.toTimeString(AddEditItemActivity.this,
                            mEndTime));
                }
            }
        }, dt.getHourOfDay(),dt.getMinuteOfHour(),false).show();
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
                        fetchWeatherIDandLocality();
                        mItem = new Item(mDate,mStartTime,mEndTime,locationText,descriptionText,mBudget,weatherid,mLocality);
                        returnval = db.addItem(mItem);
                        if (returnval == true){
                            AddEditItemActivity.this.setResult(RESULT_OK);
                            Toast.makeText(AddEditItemActivity.this, R.string.create_item, Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(AddEditItemActivity.this, R.string.invalid_date, Toast.LENGTH_SHORT).show();
                        }
                    } else if (typeOfDialog == EDIT_ITEM){
                        fetchWeatherIDandLocality();
                        Item newItem = new Item(itemID,mDate,mStartTime,mEndTime,locationText,descriptionText,mBudget,weatherid,mLocality);
                        if(newItem.getBudget() == mItem.getBudget()){
                            returnval = db.editItem(false,newItem);
                        } else {
                            returnval = db.editItem(true,newItem);
                        }
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

    private void fetchWeatherIDandLocality(){
        if(locationText!=null) {
            mLocality = autocompleteView.googlePlace.getLocality();
            FetchWeatherAsyncTask task = new FetchWeatherAsyncTask();
            try {
                weatherid = task.execute(mLocality, String.valueOf(mDate.getMillis())).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
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

    @SuppressLint("ValidFragment")
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

    public class FetchWeatherAsyncTask extends AsyncTask<String, Void, Integer> {

        private final String LOG_TAG = FetchWeatherAsyncTask.class.getSimpleName();
        @Override
        protected Integer doInBackground(String... params) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            int result = -1;
            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            String format = "json";
            String units = "metric";
            int numDays = 7;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                final String FORECAST_BASE_URL =
                        "http://api.openweathermap.org/data/2.5/forecast/daily?";
                final String QUERY_PARAM = "q";
                final String FORMAT_PARAM = "mode";
                final String UNITS_PARAM = "units";
                final String DAYS_PARAM = "cnt";
                final String APPID_PARAM = "APPID";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, params[0])
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .appendQueryParameter(UNITS_PARAM, units)
                        .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                        .appendQueryParameter(APPID_PARAM, BuildConfig.f6c1a6ebbe85d2b7400d2f3044c49bed)
                        .build();

                URL url = new URL(builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                forecastJsonStr = buffer.toString();
                result = getWeatherDataFromJson(forecastJsonStr, Long.parseLong(params[1]));
            } catch (IOException e) {
                //Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        //Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            return result;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        private int getWeatherDataFromJson(String forecastJsonStr, long dt)
                throws JSONException {

            final String OWM_LIST = "list";
            final String OWM_WEATHER = "weather";
            final String OWM_WEATHER_ID = "id";
            final String OWM_DATE = "dt";
            MutableDateTime today = new MutableDateTime(dt);
            long todayDate = CalendarUtils.startOfDay(today).getMillis();
            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

            int resultInt = -1;
            for(int i = 0; i < weatherArray.length(); i++) {
                long forecastDate;
                int weatherID;

                JSONObject dayForecast = weatherArray.getJSONObject(i);

                JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                forecastDate = dayForecast.getLong(OWM_DATE);
                weatherID = weatherObject.getInt(OWM_WEATHER_ID);
                MutableDateTime jsonday = new MutableDateTime();
                jsonday.setMillis(forecastDate*1000);
                long jsonDate = CalendarUtils.startOfDay(jsonday).getMillis();
                if(todayDate == jsonDate){
                    resultInt =  weatherID;
                }
            }

            return resultInt;

        }
    }
}
