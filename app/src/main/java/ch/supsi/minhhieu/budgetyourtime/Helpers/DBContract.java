package ch.supsi.minhhieu.budgetyourtime.Helpers;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by acer on 09/09/2016.
 */
public class DBContract {

    public static final String CONTENT_AUTHORITY = "ch.supsi.minhhieu.budgetyourtime";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://com.example.android.sunshine.app/weather/ is a valid path for
    // looking at weather data. content://com.example.android.sunshine.app/givemeroot/ will fail,
    // as the ContentProvider hasn't been given any information on what to do with "givemeroot".
    // At least, let's hope not.  Don't be that dev, reader.  Don't be that dev.
    public final static String PATH_EXPENSE = "expense";
    public static final String PATH_BUDGET = "budget";
    public static final String PATH_BUDGETRECOURD = "budgetrecord";

    public static final class BudgetEntry implements BaseColumns{
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_BUDGET).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BUDGET;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BUDGET;

        public final static String TABLE_NAME = "budget";
        public final static String COLUMN_NAME = "name";
        public final static String COLUMN_AMOUNT = "amount";
        public final static String COLUMN_RECUR = "recur";

        public static Uri buildBudgetUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class BudgetRecordEntry implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_BUDGETRECOURD).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BUDGETRECOURD;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BUDGETRECOURD;

        public final static String TABLE_NAME = "budgetrecord";
        public final static String COLUMN_STARTDATE = "startdate";
        public final static String COLUMN_ENDDATE = "enddate";
        public final static String COLUMN_BUDGET = "budget";
        public final static String COLUMN_SPENT = "spent";
        public final static String COLUMN_BALANCE = "balance";
        public final static String COLUMN_AMOUNT = "amount";

        public static Uri buildBudgetRecordUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
    public static final class ExpenseEntry implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_EXPENSE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EXPENSE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EXPENSE;

        public final static String TABLE_NAME = "expense";
        public final static String COLUMN_BUDGET = "budget";
        public final static String COLUMN_DATE = "date";
        public final static String COLUMN_STARTTIME = "starttime";
        public final static String COLUMN_ENDTIME = "endtime";
        public final static String COLUMN_LOCATION = "location";
        public final static String COLUMN_DESCRIPTION = "description";
        public final static String COLUMN_DURATION = "duration";
        public final static String COLUMN_WEATHER = "weather";
        public final static String COLUMN_LOCALITY = "locality";

        public static Uri buildExpenseUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }
}
