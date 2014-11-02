package com.pewpewpew.user.makemychoice;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

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
                .add(R.id.detail_container, new DetailFragment(),"detail")
                .commit();

    }

    @Override
    public void onBackPressed() {
        // Remove overlaying image if it exists
        if (findViewById(R.id.expanded_image_container).getVisibility() == View.VISIBLE){
            DetailFragment f = (DetailFragment) getSupportFragmentManager().findFragmentByTag("detail");
            f.toggleVisibility();
        }else {
            super.onBackPressed();
        }
    }
}
