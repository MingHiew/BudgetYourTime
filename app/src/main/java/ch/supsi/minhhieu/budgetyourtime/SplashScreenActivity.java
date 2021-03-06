package ch.supsi.minhhieu.budgetyourtime;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashScreenActivity extends Activity {

    @BindView(R.id.splashscreen_appname)
    TextView appName;

    private static int SPLASH_TIME_OUT = 1500;
    private Typeface mTKaushanScript;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ButterKnife.bind(this);
        mTKaushanScript = Typeface.createFromAsset(getAssets(), "KaushanScript-Regular.ttf");
        appName.setTypeface(mTKaushanScript);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent i = new Intent(SplashScreenActivity.this, HomeActivity.class);
                startActivity(i);

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

}
