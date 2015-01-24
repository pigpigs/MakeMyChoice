package com.pewpewpew.user.makemychoice;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by User on 12/10/14.
 */
public class PostActivity extends ActionBarActivity implements PostFragment.Callback{
    private static final String TAG = "PostActivity_debug";

    private static String mImagePath;

    // Activity where user submits content. Inflate a different fragment depending on Image Post or
    // Text Post
    // 1) ActionBar with submit action, confirms whether the user wants to discard the message when exiting
    // 2) Checks for title and stuff, soft char limit on title
    // 3) Title in actionbar is changed to submit new post!
    // 4) Check if it retains content after multitasking/config change, maybe just needs bundling in

    public PostActivity(){

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_post);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.post_container, new PostFragment())
                    .commit();
        }

    }

    @Override
    public void onBackPressed() {
        String titleStr = ((EditText) findViewById(R.id.post_title_editText)).getText().toString();
        String bodyStr = ((EditText)findViewById(R.id.post_mainBody_editText)).getText().toString();
        if( (titleStr!=null) || (bodyStr != null) ){
            new AlertDialog.Builder(this)
                    .setMessage("Are you sure you want to exit?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            setResult(RESULT_CANCELED);
                            finish();
//                            PostActivity.super.onBackPressed();
                        }
                    })
                    .setNegativeButton("No",null)
                    .create()
                    .show();

        }
//        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_submit:
                if(!getIntent().getBooleanExtra("isOutcome", false)) {
                    String titleStr = ((EditText) findViewById(R.id.post_title_editText)).getText().toString();
                    titleStr = titleStr.replace("\n", "");
                    Log.i(TAG, titleStr);
                    // Check title string length constraints
                    if (titleStr.length() == 0) {
                        Toast.makeText(this, "Please enter a title.", Toast.LENGTH_SHORT).show();
                    } else if (titleStr.length() > 150) {
                        Toast.makeText(this, "Title can have maximum of 150 characters. You have " + titleStr.length() + ".", Toast.LENGTH_SHORT).show();
                    } else {
                        // TODO- make the whole saving thing an asynctask that then runs saveeventually on the parse object, mainly to prevent bitmap lagging up
                        // Make a new Post object
                        Post newPost = new Post();
                        newPost.setTitle(titleStr);

                        String bodyStr = ((EditText) findViewById(R.id.post_mainBody_editText)).getText().toString();
                        if (bodyStr != null) {
                            newPost.setBody(bodyStr);
                        }

                        // Save the Image if there is one
                        if (mImagePath != null) {
                            Log.i(TAG, "Picture path: "+mImagePath);
                            //Decode and scale image
                            Bitmap bmp = Utility.makeParseBitmap(mImagePath);

                            // Output bitmap and save to server
                            // TODO - callback to delete image once it's done, can use Parse's savecallback in saveinbackground
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                            byte[] data = stream.toByteArray();
                            newPost.setImage(data);
                            mImagePath = null; // clear imagepath as static vars dont get cleared in onDestroy
                        }

                        // Set result
                        Toast.makeText(this, "Submitting Post...", Toast.LENGTH_SHORT).show();

                        // Proceed if no image
                        newPost.setCurrentUser();
                        newPost.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e==null) {
                                    Toast.makeText(getApplicationContext(), "Post Submitted!", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(getApplicationContext(), "Error: "+ e.getCode(),Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


                        setResult(RESULT_OK);
                        finish();

                    }
                }else{
                    // Create new outcome post
                    String titleStr = ((EditText) findViewById(R.id.post_title_editText)).getText().toString();
                    titleStr = titleStr.replace("\n", "");
                    Log.i(TAG, titleStr);
                    // Check title string length constraints
                    if (titleStr.length() == 0) {
                        Toast.makeText(this, "Please enter a title.", Toast.LENGTH_SHORT).show();
                    } else if (titleStr.length() > 150) {
                        Toast.makeText(this, "Title can have maximum of 150 characters. You have " + titleStr.length() + ".", Toast.LENGTH_SHORT).show();
                    } else {
                        // make new outcome object
                        Outcome outcome= new Outcome();
                        outcome.setTitle(titleStr);

                        String bodyStr = ((EditText) findViewById(R.id.post_mainBody_editText)).getText().toString();
                        if (bodyStr != null) {
                            outcome.setBody(bodyStr);
                        }

                        // Save the Image if there is one
                        if (mImagePath != null) {
                            //Decode and scale image
                            Bitmap bmp = Utility.makeParseBitmap(mImagePath);

                            // Output bitmap and save to server
                            // TODO - callback to delete image once it's done, can use Parse's savecallback in saveinbackground
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                            byte[] data = stream.toByteArray();
                            outcome.setImage(data);
                            mImagePath = null; // clear imagepath as static vars dont get cleared in onDestroy
                        }

                        // Proceed if no image
//                        outcome.setCurrentUser();
                        Log.i(TAG, "attempting to save new outcome");
                        outcome.saveInBackground();
                        String postID = getIntent().getStringExtra(DetailActivity.KEY_POST_ID);
                        Post post = ParseObject.createWithoutData(Post.class, postID);
                        post.setOutcome(outcome);
                        post.saveInBackground();

                        // Set result
                        Toast.makeText(this, "Outcome submitted!", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();

                    }
                }


            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.post_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onImageCreated(String imagePath) {
        mImagePath = imagePath;
        Log.i(TAG, "Image Created Callback: "+imagePath);
    }


}
