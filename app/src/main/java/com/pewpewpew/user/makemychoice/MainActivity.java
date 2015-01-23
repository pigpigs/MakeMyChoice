package com.pewpewpew.user.makemychoice;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;




public class MainActivity extends ActionBarActivity {
    private static final String TAG = "MainActivity_debug";
    private MainPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;
    private static final int REQUEST_NEW_POST = 88;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // using ParseUser.getCurrentUser to check user if logged in now.
        if(ParseUser.getCurrentUser() != null) {
            Log.i(TAG, "Current user logged in: " + ParseUser.getCurrentUser().getUsername());
            // User is logged in, proceed with Main Fragment
//            setContentView(R.layout.activity_main);
//            if (savedInstanceState == null) {
//                getSupportFragmentManager().beginTransaction()
//                        .add(R.id.some_container, new MainFragment())
//                        .commit();
//            }

            setContentView(R.layout.activity_main_pager);
            mPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
            mViewPager = (ViewPager) findViewById(R.id.pager_main);
            mViewPager.setAdapter(mPagerAdapter);
            Toast.makeText(MainActivity.this, "Welcome " + ParseUser.getCurrentUser().getUsername()+"!", Toast.LENGTH_SHORT).show();
        }else{
            // User not logged in, let them sign in or register
            setContentView(R.layout.activity_signup);
            ((Button) findViewById(R.id.signup_button)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String username = ((EditText) findViewById(R.id.username)).getText().toString();
                    String password = ((EditText) findViewById(R.id.password)).getText().toString();
                    String passwordAgain = ((EditText) findViewById(R.id.password_again)).getText().toString();
                    final TextView errorMessage = (TextView) findViewById(R.id.error_message);

                    if(username.length() == 0 || password.length() == 0 || passwordAgain.length() == 0){
                        errorMessage.setText("Please fill in all the fields.");
                        displayErrorToast();
                        return;
                    }

                    String illegalChars = ".*[^a-zA-Z0-9-_].*";
                    if(username.matches(illegalChars)){
                        errorMessage.setText("Illegal Characters in Username. (Only alphanumeric characters, dashes and hyphens are allowed!)");
                        displayErrorToast();
                        return;
                    }

                    if(username.length() > 15){
                        errorMessage.setText("Username too long! Maximum of 15 characters.)");
                        displayErrorToast();
                        return;
                    }
                    if(!password.equals(passwordAgain)){
                        errorMessage.setText("The password in both fields are not the same.");
                        displayErrorToast();
                        return;
                    }

                    if(password.length() <=7){
                        errorMessage.setText("Password is too short! (At least 8 characters)");
                        displayErrorToast();
                        return;
                    }

                    ParseUser user = new ParseUser();
                    user.setUsername(username);
                    user.setPassword(password);

                    user.signUpInBackground(new SignUpCallback() {
                        public void done(ParseException e) {
                            if (e == null) {
                                // Hooray! Let them use the app now.

                                setContentView(R.layout.activity_main_pager);
                                mPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
                                mViewPager = (ViewPager) findViewById(R.id.pager_main);
                                mViewPager.setAdapter(mPagerAdapter);
                                Toast.makeText(MainActivity.this, "Welcome " + ParseUser.getCurrentUser().getUsername()+"!", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.i(TAG, "Authentication Error: " + e.getMessage());
                                // Sign up didn't succeed. Look at the ParseException
                                // to figure out what went wrong

                                errorMessage.setText("Error: " + e.getMessage());

                            }
                        }
                    });

                    }

            });
        }
    }
    private void displayErrorToast(){
        Toast.makeText(getApplicationContext(), "An error has occurred.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case R.id.action_newPost:
                Intent intent = new Intent(MainActivity.this,PostActivity.class);
                startActivityForResult(intent,REQUEST_NEW_POST);
                return true;

            case R.id.action_signout:
                new AlertDialog.Builder(this)
                        .setMessage("Are you sure you want to exit?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ParseUser.logOut();
                                MainActivity.this.finish();

                            }
                        })
                        .setNegativeButton("No",null)
                        .create()
                        .show();

            default:
                return super.onOptionsItemSelected(item);
        }



    }

    private int NUM_ITEMS = 2;
    private class MainPagerAdapter extends FragmentPagerAdapter {
        private MainPagerAdapter(FragmentManager fm){
            super(fm);

        }
        @Override
        public Fragment getItem(int i) {
            switch(i){
                case 0:
                    return MainFragment.newInstance(i);
                case 1:
                    return FollowedFragment.newInstance(i);
            }
            return null;
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }
    }
}
