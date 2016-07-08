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
 * <br>This class contains static helper methods for the app.
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

    /**
     * This method asks for permission in case of Android Marshmallow (API 23)
     * @param context Context
     * @param permissions Which permission to ask
     * @param requestCode Request code
     * @return Whether permission already granted
     */
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

    /**
     * This method generates a new random integer between 0 and size-1
     * @param size Number of items in adapter
     * @return Random integer
     */
    public static int getRandomNumber(int size) {
        Random random = new Random();
        return random.nextInt(size);
    }

    /**
     * This method checks if morning alarm is set
     * @param context Context
     * @return Whether alarm was set or not
     */
    public static boolean isMorningAlarmSet(Context context) {
        return context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
                .getBoolean(KEY_MORNING_ALARM, false);
    }

    /**
     * This method set a flag for morning alarm
     * @param context Context
     * @param value Whether alarm is set or not
     */
    public static void setMorningAlarmSet(Context context, boolean value) {
        context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(KEY_MORNING_ALARM, value)
                .commit();
    }

    /**
     * This method sets an alarm that triggers every morning at 6AM.
     * Note that this may not be exactly at 6AM in order to save battery.
     * @param context Context
     */
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
