package com.pewpewpew.user.makemychoice;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import junit.framework.Test;

import java.util.Random;

/**
 * Created by User on 24/9/14.
 */
public class MainFragment extends Fragment {
    private static final String TAG = "MainFragment_Debug";
    private ParseQueryAdapter<ParseObject> mAdapter;

    public MainFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Sort options and stuff

        inflater.inflate(R.menu.main_fragment, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // dev option to add new data onto Parse database
        int id = item.getItemId();
        if (id == R.id.action_addData){
            addNewData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addNewData() {
        // Adds new data to parse database
        String[] sampleData = new String[]{"Should I ask this cute girl out?","What should I do with this cute little thing?",
                "Should I listen in Java class?","Destiny or Borderlands Prequel?","Macs or KFC?","Should I slim down for her?",
                "Should I beat up the bully?","Not sure which headphones I should buy.. MMC!","MAKE MY CHOICE NANANANANNAA","Which guy do I go on a date with?",
                "[DEV POST] Make Our Choice! Should we have different colors for posts with outcome and posts that are inactive for a few days?"
        };
        Random numGen = new Random(); // only instantiate once to prevent reseeding??
        int idx = numGen.nextInt(sampleData.length); //nextInt is exclusive of topvalue, add 1 to make it inclusive

        ParseObject newPost = new ParseObject("Post");
        newPost.put("title", sampleData[idx]);
        int samplePoints=numGen.nextInt(1338);
        newPost.put("points", samplePoints );
        Log.i(TAG,"New Data: "+sampleData[idx]+" - "+ samplePoints);
        newPost.saveInBackground();
//        mAdapter.notifyDataSetChanged();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Inflate custom view here and instantiate
        View view = inflater.inflate(R.layout.fragment_main,container, false);

        // TODO - bundle in stuff to retain instance on config change etc etc

        ListView listView = (ListView) view.findViewById(R.id.listView_main);

        ParseQueryAdapter.QueryFactory<ParseObject> factory = new ParseQueryAdapter.QueryFactory<ParseObject>() {
            @Override
            public ParseQuery<ParseObject> create() {
                ParseQuery<ParseObject> query = new ParseQuery("Post");

                query.orderByDescending("createdAt");
                // TODO - Order by points, where datetime <= 5 days ago
                return query;
            }
        };
        mAdapter = new ParseQueryAdapter<ParseObject>(getActivity(), factory){
            @Override
            public View getItemView(ParseObject post, View v, ViewGroup parent) {

                if (v == null){
                    v= View.inflate(getContext(),R.layout.list_post_item, null);
                }

                ((TextView)v.findViewById(R.id.post_title)).setText(post.getString("title"));
                // TODO - Check for titles that are too big, truncate and add ...more to the end

                TextView postData = (TextView)v.findViewById(R.id.post_data);
                // Get timeSince, numComments and numPoints for post data
                Log.i(TAG, post.getCreatedAt().toString());
                String timeSince = Utility.getTimeSince(post.getCreatedAt());

                // Upvotes on post
                // Have to start thinking about how to avoid same person from upvoting one post multiple times
                String points = String.valueOf(post.getInt("points"));
                //Number of comments, use increment on the field every time there is a new post
                String numComments = String.valueOf(post.getInt("numComments"));

                postData.setText(String.format(
                        getString(R.string.format_post_data) , // String format from xml
                        timeSince,
                        // TODO - add formatting for plurality for numComments and points
                        numComments,
                        points
                ));
                return v;
            }
        };
        listView.setAdapter(mAdapter);
        return view; //return own view here
    }
}
