package com.abhijitm.wardrobe.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.abhijitm.wardrobe.ActMain;
import com.abhijitm.wardrobe.R;
import com.abhijitm.wardrobe.models.Garment;

import io.realm.Realm;

/**
 * Created by Abhijit on 03-07-2016.
 */
public class MorningAlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "MorningAlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "<><><> onReceive called");
        // create notification
        createNotification(context);
    }

    private void createNotification(Context context) {
        // prepare Intent
        Intent intent = new Intent(context, ActMain.class);
        intent.putExtra(ActMain.EXTRA_FROM_NOTIF, true);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 100, intent, PendingIntent.FLAG_ONE_SHOT);

        // build notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setColor(context.getResources().getColor(R.color.colorAccent))
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText("Today's combination is here")
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent);
        // notify
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(1000, builder.build());

    }
}
