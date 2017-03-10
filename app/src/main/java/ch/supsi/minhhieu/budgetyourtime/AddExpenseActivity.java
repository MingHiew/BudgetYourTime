package ch.supsi.minhhieu.budgetyourtime;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;

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
import ch.supsi.minhhieu.budgetyourtime.Helpers.DBContract;
import ch.supsi.minhhieu.budgetyourtime.Helpers.DBHelper;
import ch.supsi.minhhieu.budgetyourtime.Models.Expense;
import ch.supsi.minhhieu.budgetyourtime.Utils.CalendarUtils;
import co.geeksters.googleplaceautocomplete.lib.CustomAutoCompleteTextView;

public class AddExpenseActivity extends FragmentActivity implements ConnectionCallbacks, OnConnectionFailedListener {

    @BindView(R.id.item_exp_title)
    TextView itemExpenseTitle;
    @BindView(R.id.spinner_budget_name)
    Spinner budgetName;
    @BindView(R.id.save_item)
    ImageView saveItem;
    @BindView(R.id.exp_starttime)
    TextView startTime;
    @BindView(R.id.exp_endtime)
    TextView endTime;
    @BindView(R.id.expenseDate)
    TextView expenseDate;
    @BindView(R.id.locationAutocomplete)
    CustomAutoCompleteTextView autocompleteView;
    @BindView(R.id.exp_description)
    EditText expDescription;

    public static final int ADD_NEW_EXPENSE = 3;
    private static MutableDateTime mDate = new MutableDateTime();

    DBHelper db;

    private DialogFragment timeFragment;
    private int mBudget, typeOfDialog;
    private long mStartTime, mEndTime;
    private String locationText, descriptionText, mLocality;
    private Expense mExpense = new Expense();
    private ProgressDialog loadingSpinner1, loadingSpinner2;

    private int weatherid = -1;
    public static final String AUTOLOCATION = "location_autodetect";

    private GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_edit_expense);
        ButterKnife.bind(this);
        Intent intent = getIntent();

        db = DBHelper.getInstance(this);
        typeOfDialog = intent.getIntExtra("typeOfDialog", 2);
        int expenseID = (int) intent.getLongExtra("itemID", 0);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .enableAutoManage(this, 0, this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        presetAddNewActivityViews();
        setupViewsBehaviours();

        // Create an instance of GoogleAPIClient.
        //if (mGoogleApiClient == null) {
        //}
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
        /**Action viewAction = Action.newAction(
         Action.TYPE_VIEW, // TODO: choose an action type.
         "AddEditItem Page", // TODO: Define a title for the content shown.
         // TODO: If you have web page content that matches this app activity's content,
         // make sure this auto-generated web page URL is correct.
         // Otherwise, set the URL to null.
         Uri.parse("http://host/path"),
         // TODO: Make sure this auto-generated app URL is correct.
         Uri.parse("android-app://ch.supsi.minhhieu.budgetyourtime/http/host/path")
         );
         AppIndex.AppIndexApi.start(mGoogleApiClient, viewAction);**/
    }

    @Override
    public void onStop() {
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        /**Action viewAction = Action.newAction(
         Action.TYPE_VIEW, // TODO: choose an action type.
         "AddEditItem Page", // TODO: Define a title for the content shown.
         // TODO: If you have web page content that matches this app activity's content,
         // make sure this auto-generated web page URL is correct.
         // Otherwise, set the URL to null.
         Uri.parse("http://host/path"),
         // TODO: Make sure this auto-generated app URL is correct.
         Uri.parse("android-app://ch.supsi.minhhieu.budgetyourtime/http/host/path")
         );
         AppIndex.AppIndexApi.end(mGoogleApiClient, viewAction);**/
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();

    }

    public void updateDate(long dt) {
        expenseDate.setText(CalendarUtils.toDayString(AddExpenseActivity.this, dt));
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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
            return new DatePickerDialog(getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, this, year, month - 1, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            mDate.setYear(year);
            mDate.setMonthOfYear(month + 1);
            mDate.setDayOfMonth(day);
            updateDate(CalendarUtils.startOfDay(mDate).getMillis());
        }
    }

    private void presetAddNewActivityViews() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            SpinnerAdapter mSpinnerAdapter = new SimpleCursorAdapter(this,
                    android.R.layout.simple_list_item_1,
                    db.getAllBudgetCursor(true),
                    new String[]{DBContract.BudgetEntry.COLUMN_NAME},
                    new int[]{android.R.id.text1},
                    0);
            budgetName.setAdapter(mSpinnerAdapter);
        }
        itemExpenseTitle.setText("Add New Expense");
        if (expenseDate == null) return;
        long today = MutableDateTime.now().getMillis();
        expenseDate.setText(CalendarUtils.toDayString(AddExpenseActivity.this, today));
        mDate.setDate(today);
        mStartTime = CalendarUtils.getNearestHourAndMinutes();
        startTime.setText(CalendarUtils.toTimeString(AddExpenseActivity.this, mStartTime));
        mEndTime = CalendarUtils.getNearestHourAndMinutes() + 3600000;
        endTime.setText(CalendarUtils.toTimeString(AddExpenseActivity.this, mEndTime));

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplication());
        boolean AutoLoc = sharedPref.getBoolean(AUTOLOCATION,Boolean.parseBoolean("false"));
        if(AutoLoc == true) {
            guessCurrentPlace();
        }
    }

    private void setupViewsBehaviours() {

        //Preset the date
        expenseDate.setOnClickListener(new View.OnClickListener() {
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
        new CustomTimePickerDialog(this, AlertDialog.THEME_TRADITIONAL, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                dt.setHourOfDay(hourOfDay);
                dt.setMinuteOfHour(minute);
                if (start) {
                    mStartTime = dt.getMillis();
                    startTime.setText(CalendarUtils.toTimeString(AddExpenseActivity.this, mStartTime));
                } else {
                    //if (dt.getHourOfDay() == 0) {
                    //    MutableDateTime dt1 = CalendarUtils.endOfDay(dt);
                    //    mEndTime = dt1.getMillis();
                    //} else {
                    mEndTime = dt.getMillis();
                    //}
                    endTime.setText(CalendarUtils.toTimeString(AddExpenseActivity.this,
                            mEndTime));
                }
            }
        }, dt.getHourOfDay(), dt.getMinuteOfHour(), false).show();

    }

    private void setOnclickSaveButton() {
        saveItem.setOnClickListener(new View.OnClickListener() {
            boolean returnval = false;

            @Override
            public void onClick(View v) {

                locationText = autocompleteView.getText().toString().trim().isEmpty() ? "Unknown" : autocompleteView.getText().toString().trim();
                descriptionText = expDescription.getText().toString().trim();
                Boolean validTime = ValidateTime(mStartTime, mEndTime);

                if (validTime == false) {
                    Toast.makeText(AddExpenseActivity.this, R.string.invalid_time, Toast.LENGTH_SHORT).show();
                } else {
                    if (typeOfDialog == ADD_NEW_EXPENSE) {
                        loadingSpinner1 = new ProgressDialog(AddExpenseActivity.this);
                        loadingSpinner1.setMessage("Adding expense...");
                        loadingSpinner1.show();
                        if (!locationText.equals("Unknown")) fetchWeatherIDandLocality();
                        mExpense = new Expense(mDate, mStartTime, mEndTime, locationText, descriptionText, mBudget, weatherid, mLocality);
                        returnval = db.addExpense(mExpense);
                        if (returnval == true) {
                            AddExpenseActivity.this.setResult(RESULT_OK);
                            Toast.makeText(AddExpenseActivity.this, R.string.create_item, Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(AddExpenseActivity.this, R.string.invalid_date, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }

    private void fetchWeatherIDandLocality() {
        if (locationText != null && !locationText.isEmpty()) {
            //if (typeOfDialog == ADD_NEW_EXPENSE) {
            //mLocality = autocompleteView.googlePlace.getLocality();
            //}
            FetchWeatherAsyncTask task1 = new FetchWeatherAsyncTask();
            FetchHistoricalWeatherAsyncTask task2 = new FetchHistoricalWeatherAsyncTask();
            long today = CalendarUtils.today().getMillis();
            if (mDate.getMillis() >= today) {
                try {
                    weatherid = task1.execute(mLocality, String.valueOf(mDate.getMillis())).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    long dt = mDate.getMillis();
                    weatherid = task2.execute(mLocality, String.valueOf(dt)).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean ValidateTime(long startTime, long endTime) {

        if (TimeUnit.MILLISECONDS.toHours(startTime) < TimeUnit.MILLISECONDS.toHours(endTime)) {
            return true;
        } else {
            return false;
        }
    }

    private void guessCurrentPlace() {

        String provider = Settings.Secure.getString(getApplication().getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if(!provider.contains("gps")) { //if gps is disabled
            Toast.makeText(getBaseContext(), "Please turn on GPS for location auto-detection!", Toast.LENGTH_SHORT).show();
        } else {

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            //loadingSpinner2 = new ProgressDialog(AddExpenseActivity.this);
            //loadingSpinner2.setMessage("Fetching Location...");
            //loadingSpinner2.show();
            PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi.getCurrentPlace(mGoogleApiClient, null);
            result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
                @Override
                public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
                    FetchLocalityAsyntask task = new FetchLocalityAsyntask();
                    PlaceLikelihood placeLikelihood = likelyPlaces.get(0);
                    String currentLocation = "";
                    if (placeLikelihood != null && placeLikelihood.getPlace() != null && !TextUtils.isEmpty(placeLikelihood.getPlace().getName())) {
                        currentLocation = placeLikelihood.getPlace().getName() + ", " + placeLikelihood.getPlace().getAddress();
                        try {
                            mLocality = task.execute(placeLikelihood.getPlace().getId()).get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                        //currentLocation += "Percent change of being there: " + (int) (placeLikelihood.getLikelihood() * 100) + "%";
                    }
                    autocompleteView.setText(currentLocation);

                    likelyPlaces.release();
                }
            });
        }
        //loadingSpinner2.dismiss();
    }

    public class FetchWeatherAsyncTask extends AsyncTask<String, Void, Integer> {

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

    public class FetchHistoricalWeatherAsyncTask extends AsyncTask<String, Void, Integer> {

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
            String type = "hour";
            long start = Long.parseLong(params[1])/1000;
            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                final String FORECAST_BASE_URL =
                        "http://api.openweathermap.org/data/2.5/forecast/daily?";
                final String QUERY_PARAM = "q";
                final String FORMAT_PARAM = "mode";
                final String UNITS_PARAM = "units";
                final String TYPE_PARAM = "type";
                final String START_PARAM = "start";
                final String CNT_PARAM = "cnt";
                final String APPID_PARAM = "APPID";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, params[0])
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .appendQueryParameter(UNITS_PARAM, units)
                        .appendQueryParameter(TYPE_PARAM, type)
                        .appendQueryParameter(START_PARAM,String.valueOf(start))
                        .appendQueryParameter(CNT_PARAM,"1")
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
                result = getWeatherDataFromJson(forecastJsonStr);
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

        private int getWeatherDataFromJson(String forecastJsonStr)
                throws JSONException {

            final String OWM_LIST = "list";
            final String OWM_WEATHER = "weather";
            final String OWM_WEATHER_ID = "id";
            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

            int weatherID;
            int resultInt = -1;
            JSONObject dayForecast = weatherArray.getJSONObject(0);

            JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
            weatherID = weatherObject.getInt(OWM_WEATHER_ID);
            resultInt = weatherID;
            return resultInt;

        }
    }

    private class FetchLocalityAsyntask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String result = "";
            // Will contain the raw JSON response as a string.
            String placeDetail = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                final String FORECAST_BASE_URL =
                        "https://maps.googleapis.com/maps/api/place/details/json?";
                final String PLACEID_PARAM = "placeid";
                final String KEY_PARAM = "key";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(PLACEID_PARAM, params[0])
                        .appendQueryParameter(KEY_PARAM, "AIzaSyDDjT9ri_FVK40KVzxmvsYBhKv10BfHu_0")
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
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                placeDetail = buffer.toString();
                result = getLocalityDataFromJson(placeDetail);
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

        private String getLocalityDataFromJson(String forecastJsonStr)
                throws JSONException {

            JSONObject placeDetailJson = new JSONObject(forecastJsonStr);
            StringBuilder resultStr = new StringBuilder();
            String country, city;
            JSONArray addressComponents = placeDetailJson.getJSONObject("result").getJSONArray("address_components");
            for (int i = 0; i < addressComponents.length(); i++) {
                JSONArray typesArray = addressComponents.getJSONObject(i).getJSONArray("types");
                for (int j = 0; j < typesArray.length(); j++) {

                    if (typesArray.get(j).toString().equalsIgnoreCase("locality")) {
                        city = addressComponents.getJSONObject(i).getString("long_name");
                        resultStr.append(city).append(",");
                    }
                    if (typesArray.get(j).toString().equalsIgnoreCase("country")) {
                        country = addressComponents.getJSONObject(i).getString("long_name");
                        resultStr.append(country);
                    }
                }
            }
            return resultStr.toString();
        }
    }

}
