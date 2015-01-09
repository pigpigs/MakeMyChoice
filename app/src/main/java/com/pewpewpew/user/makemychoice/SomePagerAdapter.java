package com.pewpewpew.user.makemychoice;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by User on 09/1/15.
 */
public class SomePagerAdapter extends FragmentPagerAdapter {
    public SomePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Fragment getItem(int position) {
        return new DetailFragment();
    }
}
