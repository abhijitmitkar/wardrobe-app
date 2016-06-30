package com.abhijitm.wardrobe;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class ActMain extends AppCompatActivity {

    private ViewPager viewPagerTop;
    private ViewPager viewPagerBottom;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        // initialize views
        // toolbar
        AppUtils.setUpToolbar(this, R.id.actMain_toolbar, null, false);
        // viewpager top
        viewPagerTop = (ViewPager) findViewById(R.id.actMain_viewpagerTop);
        // viewpager bottom
        viewPagerBottom = (ViewPager) findViewById(R.id.actMain_viewpagerBottom);
        // fab
        fab = (FloatingActionButton) findViewById(R.id.actMain_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

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
}
