package com.pewpewpew.user.makemychoice;


import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;

/**
 * Created by User on 04/10/14.
 */
public class DetailFragment  extends Fragment{
    private static final String TAG = "DetailFragment_debug";
    private Post mPost;
    public DetailFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Intent intent = getActivity().getIntent();
        String postId = intent.getStringExtra(MainFragment.KEY_POST_ID);
        Post post = ParseObject.createWithoutData(Post.class, postId);
//        Log.i(TAG, ""+post.isDataAvailable());
        final View v = inflater.inflate(R.layout.fragment_detail, container, false);
        post.fetchIfNeededInBackground(new GetCallback<Post>() {
            @Override
            public void done(final Post thisPost, ParseException e) {
                if(e == null) {
                    mPost = thisPost; // handle for post to get post data somewhere
                    //Get title
                    String postTitle = thisPost.getTitle();
                    TextView titleTextView = (TextView) v.findViewById(R.id.post_title);
//                    Log.i(TAG, "Title: "+postTitle);
                    titleTextView.setText(postTitle);

                    // Get image
                    ParseFile parseFile = thisPost.getImage();

                    if(parseFile== null){
                        Log.i(TAG, "No image!");
                    }else{
                        ParseImageView imageView = (ParseImageView) v.findViewById(R.id.post_image);
                        imageView.setParseFile(thisPost.getImage());
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Click to show high res view
                                toggleVisibility();
                            }
                        });
                        imageView.loadInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] bytes, ParseException e) {
                                Log.i(TAG, "Image Loaded!");
                            }
                        });
                    }

                    //Get post body
                    // TODO-  trim post body down to max of a few lines/ characters, click to expand,
                    String postBody = thisPost.getBody();
                    TextView bodyTextView = (TextView) v.findViewById(R.id.post_body);
                    bodyTextView.setText(postBody);

                }else{
                    Log.i(TAG, "Parse Exception");
                }


            }
        });

        return v;
    }
    public void toggleVisibility(){
        LinearLayout container = (LinearLayout) getActivity().findViewById(R.id.expanded_image_container);

        if(container.getVisibility() == View.GONE){
            container.setVisibility(View.VISIBLE);
            ParseImageView parseImageView = (ParseImageView) getActivity().findViewById(R.id.expanded_image_view);
            if(parseImageView.getDrawable() == null){
                Log.i(TAG, "Setting Image File.");
                parseImageView.setParseFile(mPost.getImage());
                parseImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        toggleVisibility();
                    }
                });
                // TODO - Set on touch listener to do zooming and stuff?
//                parseImageView.setOnTouchListener(new View.OnTouchListener() {
//                    @Override
//                    public boolean onTouch(View view, MotionEvent motionEvent) {
//                        // Set image to disappear on touch, so you can't scroll down when the image is on?? Ideally should do like a gallery view
//                        if (motionEvent.getAction()== MotionEvent.ACTION_DOWN) {
//                            toggleVisibility();
//                        }
//                        return true;
//                    }
//                });
                parseImageView.loadInBackground();
            }

        }else{
            container.setVisibility(View.GONE);

            // Unload image??
        }
    }
}
