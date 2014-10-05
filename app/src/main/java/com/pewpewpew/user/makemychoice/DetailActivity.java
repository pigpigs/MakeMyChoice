package com.pewpewpew.user.makemychoice;


import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by User on 04/10/14.
 */
public class DetailActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO- make container fragment, add detail fragment
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.detail_container, new DetailFragment())
                .commit();

    }
}
