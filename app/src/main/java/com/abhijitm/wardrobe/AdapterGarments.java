package com.abhijitm.wardrobe;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.abhijitm.wardrobe.models.Garment;

import io.realm.RealmResults;

/**
 * Created by Abhijit on 01-07-2016.
 */
public class AdapterGarments extends FragmentStatePagerAdapter {

    private RealmResults<Garment> listGarments;

    public AdapterGarments(FragmentManager fm) {
        super(fm);
    }

    public AdapterGarments(FragmentManager supportFragmentManager, RealmResults<Garment> listGarments) {
        super(supportFragmentManager);
        this.listGarments = listGarments;
    }

    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position
     */
    @Override
    public Fragment getItem(int position) {
        return FragGarment.newInstance(listGarments.get(position).getId());
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return listGarments.size();
    }
}
