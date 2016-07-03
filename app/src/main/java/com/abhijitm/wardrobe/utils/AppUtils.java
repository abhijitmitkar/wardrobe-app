package com.abhijitm.wardrobe.utils;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.util.Calendar;
import java.util.Random;

/**
 * Created by Abhijit on 30-06-2016.
 */
public class AppUtils {

    private static final String TAG = "AppUtils";
    private static final String SHARED_PREFERENCES = "com.abhijitm.wardrobe.shared_preferences";
    private static final String KEY_MORNING_ALARM = "key_morning_alarm";

    /**
     * Set Toolbar as ActionBar and set various properties
     *
     * @param appCompatActivity Activity instance (this)
     * @param toolbarId         Toolbar resource id (R.id.act_name_toolbar)
     * @param title             Title to be displayed on Toolbar. Pass null to keep default title from manifest
     * @param enableBack        To enable back caret button or not
     */
    public static void setUpToolbar(AppCompatActivity appCompatActivity, int toolbarId, String title, boolean enableBack) {
        Toolbar toolbar = (Toolbar) appCompatActivity.findViewById(toolbarId);
        appCompatActivity.setSupportActionBar(toolbar);
        if (appCompatActivity.getSupportActionBar() != null) {
            appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(enableBack);
            if (title != null) {
                appCompatActivity.getSupportActionBar().setTitle(title);
            }
        }
    }

    /**
     * This method generates a unique ID for DB objects
     *
     * @param className Class name for which ID is generated
     * @return A unique ID which is a combination of class name and timestamp
     */
    public static String generateId(String className) {
        return className.toLowerCase() + "_" + System.currentTimeMillis();
    }

    public static boolean checkForPermissions_API23(Context context, String permissions, int requestCode) {
        boolean result;
        if (ContextCompat.checkSelfPermission(context, permissions) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{permissions}, requestCode);
            result = false;
        } else {
            result = true;
        }
        return result;
    }

    public static int getRandomNumber(int size) {
        Random random = new Random();
        return random.nextInt(size);
    }

    public static boolean isMorningAlarmSet(Context context) {
        return context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
                .getBoolean(KEY_MORNING_ALARM, false);
    }

    public static void setMorningAlarmSet(Context context, boolean value) {
        context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(KEY_MORNING_ALARM, value)
                .commit();
    }

    public static void setMorningAlarm(Context context) {
        Log.i(TAG, "<><><> setMorningAlarm");
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, MorningAlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 6); // 6AM

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent);

        setMorningAlarmSet(context, true);
    }
}
