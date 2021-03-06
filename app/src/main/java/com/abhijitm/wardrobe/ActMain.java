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

import com.abhijitm.wardrobe.models.Favourite;
import com.abhijitm.wardrobe.models.Garment;
import com.abhijitm.wardrobe.utils.AppUtils;
import com.abhijitm.wardrobe.utils.MediaHelper;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class ActMain extends AppCompatActivity {

    private static final String TAG = "ActMain";
    public static final String EXTRA_FROM_NOTIF = "extra_from_notif";
    public static final String SAVED_TOP_POSITION = "saved_top_position";
    public static final String SAVED_BOTTOM_POSITION = "saved_bottom_position";
    private ViewPager viewPagerTop;
    private ViewPager viewPagerBottom;
    private Context context;
    private RealmResults<Garment> listTops;
    private RealmResults<Garment> listBottoms;
    private AdapterGarments adapterTops;
    private AdapterGarments adapterBottoms;
    private int selectedType = -1;
    private boolean isFromNotif;
    private Menu menu;
    private boolean isFavourite;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);
        context = this;

        // check if this Activity was opened from notification
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

                if (isFromNotif) {
                    // shuffle if activity opened from notification
                    shuffle();
                } else if (savedInstanceState != null) {
                    // if screen is rotated, restore position
                    int savedTop = savedInstanceState.getInt(SAVED_TOP_POSITION, -1);
                    if (savedTop != -1) viewPagerTop.setCurrentItem(savedTop);
                } else {
                    // scroll to newly added top
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

                if (isFromNotif) {
                    // shuffle if activity opened from notification
                    shuffle();
                } else if (savedInstanceState != null) {
                    // if screen is rotated, restore position
                    int savedBottom = savedInstanceState.getInt(SAVED_BOTTOM_POSITION, -1);
                    if (savedBottom != -1) viewPagerBottom.setCurrentItem(savedBottom);
                } else {
                    // scroll to newly added bottom
                    viewPagerBottom.setCurrentItem(results.size(), true);
                }
            }
        });
        // create and set adapter for tops
        adapterBottoms = new AdapterGarments(getSupportFragmentManager(), listBottoms);
        viewPagerBottom.setAdapter(adapterBottoms);

        // set listeners on viewpagers
        setViewPagerListeners();

        // set morning alarm if not set
        if (!AppUtils.isMorningAlarmSet(context)) {
            AppUtils.setMorningAlarm(context);
        }

        // set listener to Favourites
        setFavouriteListener();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        // to check favourite after device rotations
        checkIfFavourite();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuMain_shuffle:
                shuffle();
                return true;
            case R.id.menuMain_favourite:
                setOrUnsetFavourite();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This methods show options menu to choose 'Top' or 'Bottom'
     *
     * @param view View object returned from OnClickListener
     */
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

    /**
     * This method shows a dialog to choose from 'Camera' or 'Gallery'
     *
     * @param type Garment.TYPE_TOP or Garment.TYPE_BOTTOM
     */
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

    /**
     * This method starts the camera process by checking for permission
     */
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

    /**
     * This method starts the image picker process by checking for permission
     */
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
                    // Start camera if permission granted in Marshmallow
                    MediaHelper.startCamera(context, MediaHelper.REQUEST_CODE_CAMERA);
                }
                break;
            case MediaHelper.PERMISSION_IMAGE_PICKER:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Start image picker if permission granted in Marshmallow
                    MediaHelper.startPicker(context);
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

    /**
     * This method saves the image to the DB
     *
     * @param source   Garment.SOURCE_CAMERA or Garment.SOURCE_PICKER
     * @param filepath Image path to be saved
     */
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

    /**
     * This method shuffles the top and bottom garments
     * so that a particular combination does not repeat
     * in consecutive shuffles
     */
    private void shuffle() {
        if (listTops.size() > 0 && listBottoms.size() > 0) {
            // set flag to false so that it wont shuffle again
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

    /**
     * This method sets or unsets a particular combination as favourite.
     * Only works if there is atleast one top and one bottom.
     */
    private void setOrUnsetFavourite() {
        if (listTops.size() > 0 && listBottoms.size() > 0) {
            int currentTop = viewPagerTop.getCurrentItem();
            int currentBottom = viewPagerBottom.getCurrentItem();
            final String topId = listTops.get(currentTop).getId();
            final String bottomId = listBottoms.get(currentBottom).getId();

            if (isFavourite) {
                // if favourite, delete the DB entry
                Realm.getDefaultInstance()
                        .executeTransactionAsync(new Realm.Transaction() {
                            @Override
                            public void execute(Realm bgRealm) {
                                bgRealm.where(Favourite.class)
                                        .equalTo(Favourite.COL_TOP, topId)
                                        .equalTo(Favourite.COL_BOTTOM, bottomId)
                                        .findFirst()
                                        .deleteFromRealm();
                            }
                        });

            } else {
                // else, create a DB entry
                Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm bgRealm) {
                        Favourite favourite = bgRealm.createObject(Favourite.class);
                        favourite.setId(AppUtils.generateId(Favourite.CLASS_NAME));
                        favourite.setTop(topId);
                        favourite.setBottom(bottomId);
                    }
                });
            }
        }
    }

    /**
     * This method sets listeners on horizontal swipes to check
     * if current combination is marked as favourite or not.
     */
    private void setViewPagerListeners() {
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
    }

    /**
     * This method checks if currently visible combination is marked as favourite or not.
     * Only works if there is atleast one top and one bottom.
     */
    private void checkIfFavourite() {
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
                if (menu != null)
                    menu.findItem(R.id.menuMain_favourite).setIcon(R.drawable.ic_favorite_white_24dp);
                isFavourite = true;
            } else {
                if (menu != null)
                    menu.findItem(R.id.menuMain_favourite).setIcon(R.drawable.ic_favorite_border_white_24dp);
                isFavourite = false;
            }
        }
    }

    /**
     * This method sets a listener on the DB class Favourite to observe for changes.
     * If changes are found, checkIfFavourite is called.
     */
    private void setFavouriteListener() {
        Realm.getDefaultInstance()
                .where(Favourite.class)
                .findAllAsync()
                .addChangeListener(new RealmChangeListener<RealmResults<Favourite>>() {
                    @Override
                    public void onChange(RealmResults<Favourite> favourites) {
                        checkIfFavourite();
                    }
                });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Current positions are saved before screen orientation changes
        if (listTops.size() > 0 && listBottoms.size() > 0) {
            outState.putInt(SAVED_TOP_POSITION, viewPagerTop.getCurrentItem());
            outState.putInt(SAVED_BOTTOM_POSITION, viewPagerBottom.getCurrentItem());
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // close realm instance
        Realm.getDefaultInstance().close();
    }
}
