package com.abhijitm.wardrobe;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.abhijitm.wardrobe.models.Garment;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class ActMain extends AppCompatActivity {

    private ViewPager viewPagerTop;
    private ViewPager viewPagerBottom;
    private FloatingActionButton fab;
    private Context context;
    private Realm realm;
    private RealmResults<Garment> listTops;
    private RealmResults<Garment> listBottoms;
    private AdapterGarments adapterTops;
    private AdapterGarments adapterBottoms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        context = this;

        // get realm instance
        realm = Realm.getDefaultInstance();

        // initialize views
        // toolbar
        AppUtils.setUpToolbar(this, R.id.actMain_toolbar, null, false);
        // viewpager top
        viewPagerTop = (ViewPager) findViewById(R.id.actMain_viewpagerTop);
        // viewpager bottom
        viewPagerBottom = (ViewPager) findViewById(R.id.actMain_viewpagerBottom);
        // fab
        fab = (FloatingActionButton) findViewById(R.id.actMain_fab);
        fab.setOnClickListener(onClickListener);

        // query Garments of type 'top' from realm
        listTops = realm.where(Garment.class)
                .equalTo(Garment.COL_TYPE, Garment.TYPE_TOP)
                .findAllAsync();
        listTops.addChangeListener(new RealmChangeListener<RealmResults<Garment>>() {
            @Override
            public void onChange(RealmResults<Garment> results) {
                adapterTops.notifyDataSetChanged();
            }
        });
        // create adapter for tops
        adapterTops = new AdapterGarments(getSupportFragmentManager(), listTops);
        // set adapter for tops
        viewPagerTop.setAdapter(adapterTops);

        // query Garments of type 'bottom' from realm
        listBottoms = realm.where(Garment.class)
                .equalTo(Garment.COL_TYPE, Garment.TYPE_BOTTOM)
                .findAllAsync();
        listBottoms.addChangeListener(new RealmChangeListener<RealmResults<Garment>>() {
            @Override
            public void onChange(RealmResults<Garment> results) {
                adapterBottoms.notifyDataSetChanged();
            }
        });
        // create adapter for tops
        adapterBottoms = new AdapterGarments(getSupportFragmentManager(), listBottoms);
        // set adapter for tops
        viewPagerBottom.setAdapter(adapterBottoms);
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
                return true;
            case R.id.menuMain_favourite:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
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
    };

    private void showOptions(final String type) {

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                Garment garment = bgRealm.createObject(Garment.class);
                String id = AppUtils.generateId(Garment.CLASS_NAME);
                garment.setId(id);
                garment.setFilepath(id);
                garment.setType(type);
            }
        });


        /*new AlertDialog.Builder(context)
                .setTitle("Choose image from")
                .setItems(R.array.media_options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                // camera

                                break;
                            case 1:
                                // gallery

                                break;
                        }
                    }
                })
                .create()
                .show();*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // close realm instance
        realm.close();
    }
}
