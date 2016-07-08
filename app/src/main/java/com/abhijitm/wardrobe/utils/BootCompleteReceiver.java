package com.abhijitm.wardrobe.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Abhijit on 03-07-2016.
 * <br>This BroadcastReceiver is called when the device is rebooted.
 */
public class BootCompleteReceiver extends BroadcastReceiver {
    private static final String TAG = "BootCompleteReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        // set morning alarm again because device was restarted
        AppUtils.setMorningAlarm(context);
    }
}
