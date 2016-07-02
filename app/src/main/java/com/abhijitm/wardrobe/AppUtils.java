package com.abhijitm.wardrobe;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

/**
 * Created by Abhijit on 30-06-2016.
 */
public class AppUtils {

    /**
     * Set Toolbar as ActionBar and set various properties
     *
     * @param appCompatActivity Activity instance (this)
     * @param toolbarId Toolbar resource id (R.id.act_name_toolbar)
     * @param title Title to be displayed on Toolbar. Pass null to keep default title from manifest
     * @param enableBack To enable back caret button or not
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
        if (ContextCompat.checkSelfPermission(context,
                permissions)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            /*if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                    permissions)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permissions.

            } else*/
            {

                // No explanation needed, we can request the permissions.

                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{permissions},
                        requestCode);
                result = false;

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            result = true;
        }
        return result;

    }
}
