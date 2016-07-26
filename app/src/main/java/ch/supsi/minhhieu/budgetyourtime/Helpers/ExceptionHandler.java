package ch.supsi.minhhieu.budgetyourtime.Helpers;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.PrintWriter;
import java.io.StringWriter;

import ch.supsi.minhhieu.budgetyourtime.R;
import ch.supsi.minhhieu.budgetyourtime.Utils.Constants;

/**
 * Created by acer on 21/07/2016.
 */
public class ExceptionHandler
        implements Thread.UncaughtExceptionHandler {

    public ExceptionHandler(Context context) {
        mContext = context;
        mHost = context;
    }

    /**
     * Basic constructor
     * @param context Context / activity.
     * @param host The class where the exception originates. Used to get the class name
     *             for Logcat.
     */
    public ExceptionHandler(Context context, Object host) {
        mContext = context;
        mHost = host;
    }

    private final String LINE_SEPARATOR = "\n";
    private Context mContext;
    private Object mHost;

    public Context getContext() {
        return mContext;
    }

    public void handle(Exception ex, String errorMessage) {
        this.handle((Throwable) ex, errorMessage);
    }

    public void handle(Throwable t, String errorMessage) {
        errorMessage = "Error " + errorMessage;

        Log.e(getLogcat(), errorMessage + ": " + t.getLocalizedMessage());
        t.printStackTrace();
        showMessage(errorMessage);
    }

    private String getLogcat() {
        if (mHost != null) {
            return mHost.getClass().getSimpleName();
        } else {
            return "unknown";
        }
    }

    public void showMessage(int resourceId) {
        showMessage(getContext().getString(resourceId));
    }

    public void showMessage(final String message) {
        showMessage(message, Toast.LENGTH_SHORT);
    }
    /**
     * Display a toast message.
     * @param message Message text to display.
     * @param length Length of display. See Toast.Long and Toast.Short.
     * reference: http://stackoverflow.com/questions/18705945/android-cant-create-handler-inside-thread-that-has-not-called-looper-prepare
     */
    public void showMessage(final String message, final int length) {
        if (this.mContext == null) return;

        Handler h = new Handler(Looper.getMainLooper());
        h.post(new Runnable() {
            public void run() {
                try {
                    Toast.makeText(mContext, message, length).show();
                } catch (Exception e) {
                    Log.e(getLogcat(), "Error showing toast: " + e.getMessage());
                }
            }
        });
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        StringWriter stackTrace = new StringWriter();
        throwable.printStackTrace(new PrintWriter(stackTrace));

        StringBuilder errorReport = new StringBuilder();
        errorReport.append("************ FEEDBACK ************\n\n");
        errorReport.append("Please tell us what happened. Thank you!");
        errorReport.append(LINE_SEPARATOR);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append(LINE_SEPARATOR);

        errorReport.append("************ APP DETAILS ************\n\n");
        String version = getAppVersionInformation();
        errorReport.append(version);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append(LINE_SEPARATOR);

        errorReport.append("************ CAUSE OF ERROR ************\n\n");
        errorReport.append(stackTrace.toString());

        errorReport.append("\n************ DEVICE INFORMATION ***********\n");
        errorReport.append("Brand: ");
        errorReport.append(Build.BRAND);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Device: ");
        errorReport.append(Build.DEVICE);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Model: ");
        errorReport.append(Build.MODEL);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Id: ");
        errorReport.append(Build.ID);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Product: ");
        errorReport.append(Build.PRODUCT);
        errorReport.append(LINE_SEPARATOR);

        errorReport.append("\n************ FIRMWARE ************\n");
        errorReport.append("SDK: ");
        errorReport.append(Build.VERSION.SDK);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Release: ");
        errorReport.append(Build.VERSION.RELEASE);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Incremental: ");
        errorReport.append(Build.VERSION.INCREMENTAL);
        errorReport.append(LINE_SEPARATOR);

//        Intent intent = new Intent(context, ExceptionHandlerActivity.class);
//        intent.putExtra("error", errorReport.toString());
//        intent.setFlags (Intent.FLAG_ACTIVITY_NEW_TASK); // required when starting from Application
//        context.startActivity(intent);

//        Log.e(getLogcat(), errorReport.toString());
//        showMessage(errorReport.toString());

//        Intent intent = new Intent ();
//        intent.setAction ("com.mydomain.SEND_LOG"); // see step 5.
//        intent.setFlags (Intent.FLAG_ACTIVITY_NEW_TASK); // required when starting from Application
//        context.startActivity(intent);

        sendEmail(errorReport.toString());

//        android.os.Process.killProcess(android.os.Process.myPid());
//        System.exit(10);
        System.exit(1);
    }

    // private

    private void sendEmail(String text) {
        Intent intent = new Intent (Intent.ACTION_SEND);
        intent.setType("plain/text");

        intent.putExtra(Intent.EXTRA_SUBJECT, "Unexpected Exception Log");
//        intent.putExtra (Intent.EXTRA_STREAM, Uri.parse("file://" + fullName));
        intent.putExtra(Intent.EXTRA_TEXT, text); // do this so some email clients don't complain about empty body.
        // Title for the app selector
//        intent.putExtra(Intent.EXTRA_TITLE, "The app has crashed");
//        context.startActivity(intent);

        Intent chooser = Intent.createChooser(intent, mContext.getString(R.string.unhandled_crash));
        mContext.startActivity(chooser);
    }

    private String getAppVersionInformation() {
        if (mContext == null) return "";

        String result = "";
        try {
            String version = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName;
            result += "Version: " + version;
            result += LINE_SEPARATOR;

            String build = Integer.toString(mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionCode);
            result += "Build: " + build;
        } catch (Exception ex) {
            result = "Could not retrieve version information.";
        }
        return result;
    }
}
