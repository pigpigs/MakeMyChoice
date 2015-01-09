package com.pewpewpew.user.makemychoice;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;

/**
 * Created by User on 04/10/14.
 */
public class DetailActivity extends ActionBarActivity {
    private static final String TAG = "DetailActivity_debug";
    private DetailPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO- make container fragment, add detail fragment
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // ViewPager and its adapters use support library
        // fragments, so use getSupportFragmentManager.
        mPagerAdapter = new DetailPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mPagerAdapter);

//        getSupportFragmentManager()
//                .beginTransaction()
//                .add(R.id.detail_container, new DetailFragment(),"detail")
//                .commit();

    }

    @Override
    public void onBackPressed() {
        // Remove overlaying image if it exists
        // TODO - check if it's the right tab? or make sure that the image preview cannot be on before switching tabs.
        if (findViewById(R.id.expanded_image_container).getVisibility() == View.VISIBLE){
            // note - This method abuses the way view pager works and sets the fragments' tags. Another solution: http://stackoverflow.com/questions/8785221/retrieve-a-fragment-from-a-viewpager
            DetailFragment f = (DetailFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager + ":" + 0);
//            DetailFragment f = (DetailFragment) getSupportFragmentManager().findFragmentByTag("detail");
            f.toggleVisibility();
        }else {
            super.onBackPressed();
        }
    }


    private int NUM_ITEMS = 2;
    private class DetailPagerAdapter extends FragmentPagerAdapter{
        private DetailPagerAdapter(FragmentManager fm){
            super(fm);

        }
        @Override
        public Fragment getItem(int i) {
            switch(i){
                case 0:
                    Log.i(TAG, "showing detailfragment");
                    return DetailFragment.newInstance(i);
                case 1:
                    Log.i(TAG, "showing detailfragment 2");
                    return new Fragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }
    }

}
