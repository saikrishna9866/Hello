package com.thincovate.taskmanager.smartqs.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.thincovate.taskmanager.smartqs.R;
import com.thincovate.taskmanager.smartqs.common.NDConstants;
import com.thincovate.taskmanager.smartqs.database.TasksRepo;

public class SplashScreenActivity extends AppCompatActivity {
    private static final String TAG = SplashScreenActivity.class.getSimpleName();
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_spalsh_screen);
        @SuppressWarnings("UnusedAssignment") TasksRepo tasksRepo = new TasksRepo(getApplicationContext());

        SharedPreferences sharedPreferences = getSharedPreferences(NDConstants.PREFS_NAME, Activity.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        final boolean isFirstTime = sharedPreferences.getBoolean("isFirstTime", true);


        Thread splashThread = new Thread() {
            public void run() {

                try {
                    sleep(2 * 1000);

                    // Checking for first time launch - before calling setContentView()
                    if (isFirstTime) {
                        Log.e(TAG, "isFirstTimeLaunch ");
                        launchHomeScreen();
                    } else {
                        Intent intent = new Intent(getBaseContext(), MainActivity.class);
                        startActivity(intent);
                    }

                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        splashThread.start();

        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }

    private void launchHomeScreen() {
        editor.putBoolean("isFirstTime", false).apply();
        startActivity(new Intent(SplashScreenActivity.this, WelcomeActivity.class));
    }
}
