package com.abhijitm.wardrobe;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Abhijit on 02-07-2016.
 */
public class MediaHelper {

    private static final String TAG = "MediaHelper";
    public static String mCurrentPhotoPath;
    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";
    public static final int PERMISSION_CAMERA = 100;
    public static final int PERMISSION_IMAGE_PICKER = 101;
    public static final int REQUEST_CODE_CAMERA = 200;
    public static final int REQUEST_CODE_PICKER_PRE_KITKAT = 201;
    public static final int REQUEST_CODE_PICKER_POST_KITKAT = 202;

    public static void startCamera(Context context, int REQUEST_CODE_CAPTURE_IMAGE) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = null;
        try {
            file = createImageFile(context);
            mCurrentPhotoPath = file.getAbsolutePath();
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
            ((Activity) context).startActivityForResult(takePictureIntent, REQUEST_CODE_CAPTURE_IMAGE);
        } catch (IOException e) {
            e.printStackTrace();
            file = null;
            mCurrentPhotoPath = null;
        }
    }

    public static void startPicker(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            ((Activity) context).startActivityForResult(Intent.createChooser(intent, "Select"), MediaHelper.REQUEST_CODE_PICKER_PRE_KITKAT);
        } else {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            ((Activity) context).startActivityForResult(intent, MediaHelper.REQUEST_CODE_PICKER_POST_KITKAT);
        }
    }

    private static File createImageFile(Context context) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp;
        File albumF = getPrivateAlbumDir(context);
        File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
        return imageF;
    }

    private static File getPrivateAlbumDir(Context context) {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            storageDir = new File(Environment.getExternalStorageDirectory(), context.getResources().getString(R.string.app_name));
            if (storageDir != null) {
                if (!storageDir.mkdirs()) {
                    if (!storageDir.exists()) {
                        Log.e(TAG, "failed to create directory");
                        return null;
                    }
                }
            }
        } else {
            Log.e(TAG, "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }

    public static Uri checkForUriPermission_API19(Context context, Intent data) {
        Uri originalUri = data.getData();
        if (Build.VERSION.SDK_INT > 19) {
            int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
            context.getContentResolver().takePersistableUriPermission(originalUri, takeFlags);
        }
        return originalUri;
    }

    public static String getFilePathFromUri(Context context, Uri uri) {
        if (Build.VERSION.SDK_INT < 11)
            return getRealPathFromURI_BelowAPI11(context, uri);

            // SDK >= 11 && SDK < 19
        else if (Build.VERSION.SDK_INT < 19)
            return getRealPathFromURI_API11to18(context, uri);

            // SDK > 19 (Android 4.4)
        else
            return getRealPathFromURI_API19(context, uri);
    }

    /**
     * source for below methods <br><br>
     * https://github.com/hmkcode/Android/blob/master/android-show-image-and-path/src/com/hmkcode/android/image/RealPathUtil.java
     */

    @SuppressLint("NewApi")
    private static String getRealPathFromURI_API19(Context context, Uri uri) {
        String filePath = "";
        String wholeID = DocumentsContract.getDocumentId(uri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = {MediaStore.Images.Media.DATA};

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{id}, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }


    @SuppressLint("NewApi")
    private static String getRealPathFromURI_API11to18(Context context, Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        String result = null;

        CursorLoader cursorLoader = new CursorLoader(
                context,
                contentUri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        if (cursor != null) {
            int column_index =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            result = cursor.getString(column_index);
        }
        return result;
    }

    private static String getRealPathFromURI_BelowAPI11(Context context, Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        int column_index
                = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

}
