package com.pewpewpew.user.makemychoice;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseObject;

/**
 * Created by User on 12/10/14.
 */
public class PostActivity extends ActionBarActivity {
    private static final String TAG = "PostActivity_debug";
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
                    .add(R.id.post_container, new TextPostFragment())
                    .commit();
        }

    }

    @Override
    public void onBackPressed() {
        String titleStr = ((EditText) findViewById(R.id.post_title_editText)).getText().toString();
        String bodyStr = ((EditText)findViewById(R.id.post_mainBody_editText)).getText().toString();
        if(! (titleStr==null) || !(bodyStr == null)){
            new AlertDialog.Builder(this)
                    .setMessage("Are you sure you want to exit?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            PostActivity.super.onBackPressed();
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
                String titleStr = ((EditText) findViewById(R.id.post_title_editText)).getText().toString();
                titleStr = titleStr.replace("\n","");
                Log.i(TAG, titleStr);
                if (titleStr.length() == 0){
                    Toast.makeText(this,"Please enter a title.",Toast.LENGTH_SHORT).show();
                }else if(titleStr.length() > 150){
                    Toast.makeText(this,"Title can have maximum of 150 characters. You have "+titleStr.length()+".",Toast.LENGTH_SHORT).show();
                }else{
                    ParseObject newPost = new ParseObject("Post");
                    newPost.put("title", titleStr);
                    Log.i(TAG,"New Data: "+titleStr);

                    String bodyStr = ((EditText)findViewById(R.id.post_mainBody_editText)).getText().toString();
                    if (bodyStr!=null){
                        Log.i(TAG,"Body: "+bodyStr);
                        newPost.put("mainBody",bodyStr);
                    }
                    newPost.saveInBackground();
                    Toast.makeText(this,"Post submitted!",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this,MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
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

    public static class TextPostFragment extends Fragment{
        public TextPostFragment(){}

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_text_post, container, false);
            return v;
        }
    }
}
