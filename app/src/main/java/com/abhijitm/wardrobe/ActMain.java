package com.abhijitm.wardrobe;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.abhijitm.wardrobe.models.Garment;
import com.abhijitm.wardrobe.utils.AppUtils;
import com.abhijitm.wardrobe.utils.MediaHelper;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class ActMain extends AppCompatActivity {

    private static final String TAG = "ActMain";
    public static final String EXTRA_FROM_NOTIF = "extra_from_notif";
    private ViewPager viewPagerTop;
    private ViewPager viewPagerBottom;
    private Context context;
    private RealmResults<Garment> listTops;
    private RealmResults<Garment> listBottoms;
    private AdapterGarments adapterTops;
    private AdapterGarments adapterBottoms;
    private int selectedType = -1;
    private boolean isFromNotif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        context = this;
        isFromNotif = getIntent().getBooleanExtra(EXTRA_FROM_NOTIF, false);

        // initialize views
        AppUtils.setUpToolbar(this, R.id.actMain_toolbar, null, false);
        viewPagerTop = (ViewPager) findViewById(R.id.actMain_viewpagerTop);
        viewPagerBottom = (ViewPager) findViewById(R.id.actMain_viewpagerBottom);
        findViewById(R.id.actMain_fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFabOptions(view);
            }
        });

        // query Garments of type 'top' from realm
        listTops = Realm.getDefaultInstance().where(Garment.class)
                .equalTo(Garment.COL_TYPE, Garment.TYPE_TOP)
                .findAllAsync();
        listTops.addChangeListener(new RealmChangeListener<RealmResults<Garment>>() {
            @Override
            public void onChange(RealmResults<Garment> results) {
                adapterTops.notifyDataSetChanged();
                // shuffle if activity opened from notification
                if (isFromNotif) {
                    shuffle();
                } else {
                    viewPagerTop.setCurrentItem(results.size(), true);
                }
            }
        });
        // create and set adapter for tops
        adapterTops = new AdapterGarments(getSupportFragmentManager(), listTops);
        viewPagerTop.setAdapter(adapterTops);

        // query Garments of type 'bottom' from realm
        listBottoms = Realm.getDefaultInstance().where(Garment.class)
                .equalTo(Garment.COL_TYPE, Garment.TYPE_BOTTOM)
                .findAllAsync();
        listBottoms.addChangeListener(new RealmChangeListener<RealmResults<Garment>>() {
            @Override
            public void onChange(RealmResults<Garment> results) {
                adapterBottoms.notifyDataSetChanged();

                // shuffle if activity opened from notification
                if (isFromNotif) {
                    shuffle();
                } else {
                    viewPagerBottom.setCurrentItem(results.size(), true);
                }
            }
        });
        // create and set adapter for tops
        adapterBottoms = new AdapterGarments(getSupportFragmentManager(), listBottoms);
        viewPagerBottom.setAdapter(adapterBottoms);

        // set listeners on viewpagers
//        setViewPagerListeners();

        // set morning alarm if not set
//        AppUtils.setMorningAlarmSet(context, false);
        if (!AppUtils.isMorningAlarmSet(context)) {
            AppUtils.setMorningAlarm(context);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        // check for favourites
//        checkIfFavourite();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuMain_shuffle:
                shuffle();
                return true;
            case R.id.menuMain_favourite:
//                saveAsFavourite();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showFabOptions(View view) {
        PopupMenu popupMenu = new PopupMenu(context, view, GravityCompat.END);
        popupMenu.inflate(R.menu.menu_options_garment);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menuGarment_top:
                        showOptions(Garment.TYPE_TOP);
                        return true;
                    case R.id.menuGarment_bottom:
                        showOptions(Garment.TYPE_BOTTOM);
                        return true;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void showOptions(final int type) {
        selectedType = type;

        new AlertDialog.Builder(context)
                .setTitle("Get photo using")
                .setItems(R.array.media_options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                // camera
                                startCameraProcess();
                                break;
                            case 1:
                                // image picker
                                startPickerProcess();
                                break;
                        }
                    }
                })
                .create()
                .show();
    }

    private void startCameraProcess() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (AppUtils.checkForPermissions_API23(context, Manifest.permission.WRITE_EXTERNAL_STORAGE, MediaHelper.PERMISSION_CAMERA)) {
                //If permission is already granted.
                MediaHelper.startCamera(context, MediaHelper.REQUEST_CODE_CAMERA);
            }
        } else {
            MediaHelper.startCamera(context, MediaHelper.REQUEST_CODE_CAMERA);
        }
    }

    private void startPickerProcess() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (AppUtils.checkForPermissions_API23(context, Manifest.permission.WRITE_EXTERNAL_STORAGE, MediaHelper.PERMISSION_IMAGE_PICKER)) {
                //If permission is already granted.
                MediaHelper.startPicker(context);
            }
        } else {
            MediaHelper.startPicker(context);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MediaHelper.PERMISSION_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    MediaHelper.startCamera(context, MediaHelper.REQUEST_CODE_CAMERA);//Starting camera in Marshmallow.
                }
                break;
            case MediaHelper.PERMISSION_IMAGE_PICKER:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    MediaHelper.startPicker(context);//Starting image picker in Marshmallow.
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MediaHelper.REQUEST_CODE_CAMERA && resultCode == RESULT_OK) {
            Log.i(TAG, "ActMain.onActivityResult camera url + " + MediaHelper.mCurrentPhotoPath);
            saveToDB(Garment.SOURCE_CAMERA, MediaHelper.mCurrentPhotoPath);

        } else if (requestCode == MediaHelper.REQUEST_CODE_PICKER_PRE_KITKAT && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            String path = uri.toString();
            Log.i(TAG, "ActMain.onActivityResult pre kitkat url + " + path);
            saveToDB(Garment.SOURCE_PICKER, path);

        } else if (requestCode == MediaHelper.REQUEST_CODE_PICKER_POST_KITKAT && resultCode == RESULT_OK) {
            Uri uri = MediaHelper.checkForUriPermission_API19(context, data);
            String path = uri.toString();
            Log.i(TAG, "ActMain.onActivityResult post kitkat url + " + path);
            saveToDB(Garment.SOURCE_PICKER, path);
        }
    }

    private void saveToDB(final int source, final String filepath) {
        Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                Garment garment = bgRealm.createObject(Garment.class);
                garment.setId(AppUtils.generateId(Garment.CLASS_NAME));
                garment.setFilepath(filepath);
                garment.setType(selectedType);
                garment.setSource(source);
            }
        });
    }

    private void shuffle() {
        if (listTops.size() > 0 && listBottoms.size() > 0) {
            isFromNotif = false;
            int currentTop = viewPagerTop.getCurrentItem();
            int currentBottom = viewPagerBottom.getCurrentItem();
            int randomTop = AppUtils.getRandomNumber(listTops.size());
            int randomBottom = AppUtils.getRandomNumber(listBottoms.size());
            while (randomTop == currentTop) randomTop = AppUtils.getRandomNumber(listTops.size());
            while (randomBottom == currentBottom)
                randomBottom = AppUtils.getRandomNumber(listBottoms.size());

            viewPagerTop.setCurrentItem(randomTop, true);
            viewPagerBottom.setCurrentItem(randomBottom, true);
        }
    }

    /*private void saveAsFavourite() {
        if (listTops.size() > 0 && listBottoms.size() > 0) {
            int currentTop = viewPagerTop.getCurrentItem();
            int currentBottom = viewPagerBottom.getCurrentItem();
            final Garment garmentTop = listTops.get(currentTop);
            final Garment garmentBottom = listBottoms.get(currentBottom);
            Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm bgRealm) {
                    Favourite favourite = bgRealm.createObject(Favourite.class);
                    favourite.setId(AppUtils.generateId(Favourite.CLASS_NAME));
                    favourite.setTop(garmentTop);
                    favourite.setBottom(garmentBottom);
                }
            });
        }
    }*/

    /*private void setViewPagerListeners() {
        viewPagerTop.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                checkIfFavourite();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPagerBottom.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                checkIfFavourite();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }*/

    /*private void checkIfFavourite() {
        if (listTops.size() > 0 && listBottoms.size() > 0) {
            int currentTop = viewPagerTop.getCurrentItem();
            int currentBottom = viewPagerBottom.getCurrentItem();
            String topId = listTops.get(currentTop).getId();
            String bottomId = listBottoms.get(currentBottom).getId();

            long favouritesFound = Realm.getDefaultInstance()
                    .where(Favourite.class)
                    .equalTo(Favourite.COL_TOP, topId)
                    .equalTo(Favourite.COL_BOTTOM, bottomId)
                    .count();

            if (favouritesFound > 0) {
                Toast.makeText(context, "Favourite!", Toast.LENGTH_SHORT).show();
            }
        }
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // close realm instance
        Realm.getDefaultInstance().close();
    }
}
