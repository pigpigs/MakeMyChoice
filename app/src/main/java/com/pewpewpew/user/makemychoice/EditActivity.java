package com.pewpewpew.user.makemychoice;


import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Created by User on 17/1/15.
 */
public class EditActivity extends ActionBarActivity{
    private static final String TAG = "EditActivity_debug";
    static int type;
    static String postID;
    static String outcomeID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_edit_post);
        postID = getIntent().getStringExtra(DetailActivity.KEY_POST_ID);
        Log.i(TAG, "POST ID: "+postID);
        type = getIntent().getIntExtra(DetailActivity.KEY_TYPE, 0);

        // Set EditText with old content
        switch (type) {
            case DetailActivity.EDIT_TYPE_POST:
                Post post = ParseObject.createWithoutData(Post.class, postID);
                post.fetchIfNeededInBackground(new GetCallback<Post>() {
                    @Override
                    public void done(Post thisPost, ParseException e) {
                        ((EditText) findViewById(R.id.post_title_editText)).setText(thisPost.getTitle());
                        ((EditText) findViewById(R.id.post_mainBody_editText)).setText(thisPost.getBody());
                    }
                });
                break;

            case DetailActivity.EDIT_TYPE_OUTCOME:
                Post post2 = ParseObject.createWithoutData(Post.class, postID);

                post2.fetchIfNeededInBackground(new GetCallback<Post>() {
                    @Override
                    public void done(Post thisPost, ParseException e) {
                        if(thisPost.getOutcome()!=null) {
                            thisPost.getOutcome().fetchIfNeededInBackground(new GetCallback<Outcome>() {
                                @Override
                                public void done(Outcome outcome, ParseException e) {
                                    outcomeID = outcome.getObjectId();
                                    ((EditText) findViewById(R.id.post_title_editText)).setText(outcome.getTitle());
                                    ((EditText) findViewById(R.id.post_mainBody_editText)).setText(outcome.getBody());
                                }
                            });
                        }else{
                            // User is trying to add a new outcome, set a flag to check during saving
                            Log.i(TAG, "Adding outcome to post");
                        }

                    }
                });
                break;
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.post_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final String newTitleStr = ((EditText) findViewById(R.id.post_title_editText)).getText().toString();
        final String newBodyStr = ((EditText) findViewById(R.id.post_mainBody_editText)).getText().toString();
        switch(item.getItemId()){
            case R.id.action_submit:
                if(type == DetailActivity.EDIT_TYPE_POST) {
                    ParseQuery<Post> query = ParseQuery.getQuery("Post");
                    query.getInBackground(postID, new GetCallback<Post>() {
                        @Override
                        public void done(Post post, ParseException e) {
                            post.setTitle(newTitleStr);
                            post.setBody(newBodyStr);
                            post.saveInBackground();

                            // Set result code and refresh on activity result?
                            EditActivity.this.finish();
                        }
                    });
                }else if(type == DetailActivity.EDIT_TYPE_OUTCOME){
                    ParseQuery<Outcome> query = ParseQuery.getQuery("Outcome");
                    query.getInBackground(outcomeID, new GetCallback<Outcome>() {
                        @Override
                        public void done(Outcome outcome, ParseException e) {
                            outcome.setTitle(newTitleStr);
                            outcome.setBody(newBodyStr);
                            outcome.saveInBackground();

                            EditActivity.this.finish();
                        }
                    });
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
