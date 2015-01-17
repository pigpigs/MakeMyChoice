package com.pewpewpew.user.makemychoice;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by User on 04/10/14.
 */
public class DetailActivity extends ActionBarActivity {
    private static final String TAG = "DetailActivity_debug";
    public static final String KEY_POST_ID = "post_id_key";
    public static final String KEY_TYPE = "type_key";
    public static final int EDIT_TYPE_POST = 8888;
    public static final int EDIT_TYPE_OUTCOME = 8887;
    public static int postHasOutcome = -1; // -1 = undefined, 0 = false, 1 = true, to avoid server data from being screwed up
    private static boolean hasOutcome;
    private DetailPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;
    private static final int id_action_edit = 8889;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        if(ParseUser.getCurrentUser().getUsername().equals(getIntent().getStringExtra(MainFragment.KEY_POST_OP))){
            Log.i(TAG, "same user, allow edits");
            MenuItem mi = menu.add(Menu.NONE, id_action_edit, Menu.NONE, "Edit");
            mi.setIcon(R.drawable.ic_action_edit);
            mi.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            DetailFragment f = (DetailFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager + ":" + 0);
            f.refreshData();
            OutcomeFragment f2 = (OutcomeFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager + ":" + 1);
            f2.refreshData();
            return true;
        }else if(id==id_action_edit){
            if(mViewPager.getCurrentItem() ==0){
                Log.i(TAG, "Attempting to edit post");
                // Bundle in post id, as other stuff might not have been loaded yet.
                Intent intent = new Intent(DetailActivity.this, EditActivity.class);
                intent.putExtra(KEY_POST_ID, getIntent().getStringExtra(MainFragment.KEY_POST_ID));
                intent.putExtra(KEY_TYPE, EDIT_TYPE_POST);
                startActivity(intent);
            }else{
                Log.i(TAG, "Attempting to edit outcome");
                try {
                    Post post = ParseObject.createWithoutData(Post.class, getIntent().getStringExtra(MainFragment.KEY_POST_ID));
                    post.fetch();
                    hasOutcome = post.getOutcome() !=null;
                    Log.i(TAG, "Outcome?? " + hasOutcome);
                    if(!hasOutcome){
                        // No outcome yet, startActivity to create a new one to this post
                        Intent intent = new Intent(DetailActivity.this, PostActivity.class);
                        intent.putExtra(KEY_POST_ID, getIntent().getStringExtra(MainFragment.KEY_POST_ID));
                        intent.putExtra("isOutcome",true);
                        startActivity(intent);
                    }else{
                        Intent intent = new Intent(DetailActivity.this, EditActivity.class);
                        intent.putExtra(KEY_POST_ID, getIntent().getStringExtra(MainFragment.KEY_POST_ID));
                        intent.putExtra(KEY_TYPE, EDIT_TYPE_OUTCOME);
                        startActivity(intent);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }


            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

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
    public boolean onPrepareOptionsMenu(Menu menu) {

        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public void onBackPressed() {
        // Remove overlaying image if it exists
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
                    return DetailFragment.newInstance(i);
                case 1:
                    return OutcomeFragment.newInstance(i);
            }
            return null;
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }
    }

}
