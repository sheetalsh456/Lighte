package microsoft.lighteapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.widget.ImageView;

import com.microsoft.windowsazure.mobileservices.*;

/**
 * Created by Axle on 29/02/2016.
 */
public class SplashScreen extends Activity {

    private static int SPLASH_TIME_OUT = 3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        ImageView splashscr = (ImageView) findViewById(R.id.splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences pref = PreferenceManager
                        .getDefaultSharedPreferences(SplashScreen.this);
                if (pref.contains("KEY_E")) {
                    startActivity(new Intent(SplashScreen.this,
                            MainActivity.class));
                    finish();
                } else {
                    Intent i = new Intent(SplashScreen.this, Login.class);
                    startActivity(i);
                    finish();
                }

            }
        }, SPLASH_TIME_OUT);
    }

}
