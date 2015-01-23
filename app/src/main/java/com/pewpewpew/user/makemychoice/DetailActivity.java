package com.pewpewpew.user.makemychoice;


import android.app.Activity;
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

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by User on 04/10/14.
 */
public class DetailActivity extends ActionBarActivity implements ViewPager.OnPageChangeListener{
    private static final String TAG = "DetailActivity_debug";
    public static final String KEY_POST_ID = "post_id_key";
    public static final String KEY_TYPE = "type_key";

    public static final int EDIT_TYPE_POST = 8888;
    public static final int EDIT_TYPE_OUTCOME = 8887;
    public static final int REQUEST_EDIT_POST = 8889;
//    static iString mPostID; removed to prevent config change problems
    private static final int id_action_edit = 8890;
    private static final int id_action_delete = 8891;

    private DetailPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        if(ParseUser.getCurrentUser().getUsername().equals(getIntent().getStringExtra(MainFragment.KEY_POST_OP))){
            Log.i(TAG, "same user, allow edits");
            MenuItem mi = menu.add(Menu.NONE, id_action_edit, Menu.NONE, "Edit");
            mi.setIcon(R.drawable.ic_action_edit);
            mi.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

            MenuItem mi2 = menu.add(Menu.NONE, id_action_delete, Menu.NONE, "Delete Post");

        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == DetailActivity.REQUEST_EDIT_POST){
            if(resultCode == Activity.RESULT_OK){
                Log.i(TAG, "Post was edited, refreshing data now");
                DetailFragment f = (DetailFragment) getFragmentFromPager(R.id.pager,0);
                f.refreshData();
            }
        }else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public Fragment getFragmentFromPager(int pager, int i) {
        return getSupportFragmentManager().findFragmentByTag("android:switcher:" + pager + ":" + i);
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
                startActivityForResult(intent, REQUEST_EDIT_POST);
//                startActivity(intent);
            }else{
                Log.i(TAG, "Attempting to edit outcome");
                try {
                    Post post = ParseObject.createWithoutData(Post.class, getIntent().getStringExtra(MainFragment.KEY_POST_ID));
                    post.fetch();
                     boolean hasOutcome = post.getOutcome() !=null;
                    if(!hasOutcome){
                        // No outcome yet, startActivity to create a new one to this post
                        Intent intent = new Intent(DetailActivity.this, PostActivity.class);
                        intent.putExtra(KEY_POST_ID, getIntent().getStringExtra(MainFragment.KEY_POST_ID));
                        intent.putExtra("isOutcome",true);
//                        startActivityForResult(intent, REQUEST_EDIT_POST);
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
        }else if(id == id_action_delete){
            Log.i(TAG, "Deleting post");
            deletePost(getIntent().getStringExtra(MainFragment.KEY_POST_ID));
            // Delete stuff linked to post as well
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void deletePost(String postID){
        Log.i(TAG, "Deleting post with ID: " + postID);
        Post post = ParseObject.createWithoutData(Post.class, postID);
        // Delete all comments
        ParseQuery<Comment> commentParseQuery = ParseQuery.getQuery(Comment.class);
        commentParseQuery.whereEqualTo("parentPost", post);
        commentParseQuery.findInBackground(new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> comments, ParseException e) {
                if(e ==null) {
                    Log.i(TAG, "Deleting... " + comments.get(0).toString());
                    ParseObject.deleteAllInBackground(comments, null);
                }else{
                    Log.i(TAG, "Error while deleting post, Code " + e.getCode());
                }
            }
        });

        // Delete all follow relations?
        ParseQuery<ParseObject> followQuery = ParseQuery.getQuery("Follow");
        followQuery.whereEqualTo("postID", postID);
        followQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> relations, ParseException e) {
                if(e ==null) {
                    ParseObject.deleteAllInBackground(relations, null);
                }else{
                    Log.i(TAG, "Error while deleting post, Code " + e.getCode());
                }
            }
        });

        // Delete outcome related, dont have to delete outcome comments since it's the same for now
        post.fetchIfNeededInBackground(new GetCallback<Post>() {

            @Override
            public void done(Post post, ParseException e) {
                post.getOutcome().deleteInBackground();

            }
        });

        // Delete post
        post.deleteInBackground();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
//        mPostID = getIntent().getStringExtra(MainFragment.KEY_POST_ID);
        // ViewPager and its adapters use support library
        // fragments, so use getSupportFragmentManager.
        mPagerAdapter = new DetailPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setOnPageChangeListener(this);
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

    @Override
    public void onPageScrolled(int i, float v, int i2) {

    }

    @Override
    public void onPageSelected(int i) {
        if(i == 0){
            setTitle("[ POST ]");
        }else{
            setTitle("[ OUTCOME ]");
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

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

    public static void followPost(String postId){
//        ParseUser.getCurrentUser().put("followedPost",);
        // UI changes?
        final String id = postId;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Follow"); // Follower relationship
        query.whereEqualTo("from", ParseUser.getCurrentUser());         // from the current user
        query.whereEqualTo("to", ParseObject.createWithoutData(Post.class, id)); // to the current post
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if(parseObjects.size() ==0){
                    ParseObject followed = new ParseObject("Follow");
                    followed.put("from", ParseUser.getCurrentUser());
                    followed.put("to", ParseObject.createWithoutData(Post.class, id));
                    followed.put("postID", id);
                    followed.saveInBackground();
                }else{
                    Log.i(TAG, "Duplicate detected dududududu");
                }
            }
        });


    }


}
