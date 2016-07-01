package com.abhijitm.wardrobe;

import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.abhijitm.wardrobe.adapters.AdapterBottom;
import com.abhijitm.wardrobe.adapters.AdapterTop;
import com.abhijitm.wardrobe.models.Garment;

public class ActMain extends AppCompatActivity {

    private ViewPager viewPagerTop;
    private ViewPager viewPagerBottom;
    private FloatingActionButton fab;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        context = this;

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

        // adapters
        viewPagerTop.setAdapter(new AdapterTop(getSupportFragmentManager()));
        viewPagerBottom.setAdapter(new AdapterBottom(getSupportFragmentManager()));

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

    private void showOptions(String type) {
        new AlertDialog.Builder(context)
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
                .show();
    }
}
