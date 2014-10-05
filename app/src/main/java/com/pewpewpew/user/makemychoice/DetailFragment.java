package com.pewpewpew.user.makemychoice;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by User on 04/10/14.
 */
public class DetailFragment  extends Fragment{
    public DetailFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Intent intent = getActivity().getIntent();
        String post_title = intent.getStringExtra(MainFragment.KEY_POST_TITLE);
        View v = inflater.inflate(R.layout.fragment_detail, container, false);
        TextView post_title_textView = (TextView)v.findViewById(R.id.post_title);
        post_title_textView.setText(post_title);
        return v;
    }
}
