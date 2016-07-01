package com.abhijitm.wardrobe.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.abhijitm.wardrobe.FragBottom;
import com.abhijitm.wardrobe.FragTop;

/**
 * Created by Abhijit on 01-07-2016.
 */
public class AdapterBottom extends FragmentStatePagerAdapter {

    public AdapterBottom(FragmentManager fm) {
        super(fm);
    }

    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position
     */
    @Override
    public Fragment getItem(int position) {
        return FragBottom.newInstance(position);
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return 5;
    }
}
