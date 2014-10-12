package com.pewpewpew.user.makemychoice;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by User on 12/10/14.
 */
public class PostActivity extends ActionBarActivity {
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
