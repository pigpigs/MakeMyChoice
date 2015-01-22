package com.pewpewpew.user.makemychoice;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.parse.RefreshCallback;

import java.util.Arrays;
import java.util.List;

/**
 * Created by User on 04/10/14.
 */

/**
 * Detail of the post.
 *
 * Future - Edit option for users, better looking comments
 */
public class DetailFragment  extends Fragment{
    private static final String TAG = "DetailFragment_debug";
    private static final int REQUEST_REPLY = 101;
    private Post mPost;
    ParseQueryAdapter<Comment> commentsAdapter;
    public DetailFragment(){}



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Intent intent = getActivity().getIntent();
        final String postId = intent.getStringExtra(MainFragment.KEY_POST_ID);
        Post post = ParseObject.createWithoutData(Post.class, postId);
//        Log.i(TAG, ""+post.isDataAvailable());

        final View v = inflater.inflate(R.layout.fragment_detail_test, container, false);
        ListView listView = (ListView) v.findViewById(R.id.listView_comments);
        View headerView = inflater.inflate(R.layout.fragment_detail_header, listView, false);
        listView.addHeaderView(headerView);

        // Buttons
        ImageButton replyButton = (ImageButton) v.findViewById(R.id.button_reply);
        replyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replyToPost(postId); // the argument is currently useless, considering whether should implement another method for replying to comment
            }
        });

        // Heart Button for following - Check if user has followed the post. if yes, heart should be lit up, and will unfav onClick
        final ImageButton heartButton = (ImageButton) v.findViewById(R.id.button_heart);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Follow"); // Follower relationship
        query.whereEqualTo("from", ParseUser.getCurrentUser());         // from the current user
        query.whereEqualTo("to", ParseObject.createWithoutData(Post.class, postId)); // to the current post
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if(parseObjects.size()!=0 ){
                    // User has followed post, unfollow
                    heartButton.setSelected(true);
                }
            }
        });

        heartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Follow"); // Follower relationship
                query.whereEqualTo("from", ParseUser.getCurrentUser());         // from the current user
                query.whereEqualTo("to", ParseObject.createWithoutData(Post.class, postId)); // to the current post
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> parseObjects, ParseException e) {
                        heartButton.setSelected(!heartButton.isSelected()); //flip selected state
                        if(parseObjects.size()!=0 ){
                            // User has followed post, unfollow
                            Log.i(TAG, "Unfollowing post");
                            parseObjects.get(0).deleteInBackground();

                        }else{
                            Log.i(TAG, "Following post");
                            DetailActivity.followPost(postId);
                        }
                    }
                });

            }
        });

        // TODO - speed up loading of data with datastore

        post.fetchIfNeededInBackground(new GetCallback<Post>() {
            @Override
            public void done(final Post thisPost, ParseException e) {
                if(e == null) {
                    mPost = thisPost; // handle for post to get post data somewhere

                    // Trigger a callback if there is an outcome for the current post
                    Outcome outcome = thisPost.getOutcome();
                    if(outcome != null){
                        OutcomeFragment f = (OutcomeFragment) getActivity().getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager + ":" + 1);
                        f.inflateViews(outcome.getObjectId());
                    }else{
                        Log.i(TAG, "no outcome");

                    }

                    //Get title
                    String postTitle = thisPost.getTitle();
                    TextView titleTextView = (TextView) v.findViewById(R.id.post_title);
//                    Log.i(TAG, "Title: "+postTitle);
                    titleTextView.setText(postTitle);

                    //Get post body
                    String postBody = thisPost.getBody();
                    TextView bodyTextView = (TextView) v.findViewById(R.id.post_body);
                    bodyTextView.setText(postBody);

                    // Get image
                    ParseFile parseFile = thisPost.getImage();

                    if(parseFile== null){
                        Log.i(TAG, "No image!");
                    }else{
                        final View inflatedView = ((ViewStub) v.findViewById(R.id.detail_imagestub)).inflate();
                        ParseImageView imageView = (ParseImageView) inflatedView.findViewById(R.id.post_image);
                        imageView.setParseFile(thisPost.getImage());
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // FIXME -  Instead of this create a new fragment with a parseimageview programmatically
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





                    // Retrieve comments for the current Post
                    // comments can only be queried after the post info has been downloaded, so this should be inside the done method as well (which is here)
                    ParseQueryAdapter.QueryFactory<Comment> factory = new ParseQueryAdapter.QueryFactory<Comment>() {
                        @Override
                        public ParseQuery<Comment> create() {
                            ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);
                            query.whereEqualTo("parentPost", mPost);
                            return query;
                        }
                    };
                    // FUTURE- Posts to have polls for the choices, comments will indicate which choice the user chose
                    commentsAdapter =
                            new ParseQueryAdapter<Comment>(getActivity(), factory){
                                @Override
                                public View getItemView(Comment comment, View v, ViewGroup parent) {
                                    if (v == null){
                                        v = View.inflate(getContext(),R.layout.list_comment_item, null);
                                    }
//                                    Log.i(TAG, "Comment: "+ comment);
                                    ((TextView) v.findViewById(R.id.comment_body)).setText(comment.getBody());

                                    String username = comment.getUserStr();

                                    ((TextView) v.findViewById(R.id.comment_meta)).setText(
                                            String.format(" %s | %s ",
                                                    username == null? "username":username,
                                                    Utility.getTimeSince(comment.getCreatedAt())
                                            )
                                    );
                                    return v;
                                }
                            };
                    commentsAdapter.addOnQueryLoadListener(new ParseQueryAdapter.OnQueryLoadListener<Comment>() {
                        @Override
                        public void onLoading() {
                            // Trigger loading UI??
                            v.findViewById(R.id.detail_progressBar).setVisibility(View.VISIBLE);
                            v.findViewById(R.id.detail_noComments).setVisibility(View.GONE);
                        }

                        @Override
                        public void onLoaded(List<Comment> comments, Exception e) {
                            v.findViewById(R.id.detail_progressBar).setVisibility(View.GONE);
                            if(e == null){
                                if(comments.size() == 0){
                                    v.findViewById(R.id.detail_noComments).setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    });
                    ((ListView) v.findViewById(R.id.listView_comments)).setAdapter(commentsAdapter);
                }else{
                    Log.i(TAG, "Parse Exception");
                }
            }
        });



        return v;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
            // Comment saving implemented here because we need the reference to post
        if(requestCode == REQUEST_REPLY){
            if(resultCode == Activity.RESULT_OK){
                String commentBody = data.getStringExtra(CommentDialogFragment.KEY_REPLY);
                Log.i(TAG, "Reply received: " + commentBody);
                Comment newComment = new Comment();
                newComment.setBody(commentBody);
                newComment.setPost(mPost);
                newComment.setCurrentUser();
                newComment.saveInBackground();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Lazy method to create dialog fragment for replying
     *
     * Copy paste this method when implementing comment replying
     * @param postId
     */
    private void replyToPost(String postId) {

        CommentDialogFragment fragment = new CommentDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(CommentDialogFragment.KEY_REPLY,postId);
        // Set Args for fragment and commit
        fragment.setArguments(bundle);
        fragment.setTargetFragment(this, REQUEST_REPLY);
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();

        fragment.show(ft, "CommentDialogFragment");
//        ft.add(R.id.detail_container,fragment)
//                .commit();
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
                // TODO - Set on touch listener to do zooming and stuff? find out how it's normally done
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

    // for PagerAdapter, not sure if needed but wtv
    public static Fragment newInstance(int i) {
        DetailFragment f = new DetailFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", i);
        f.setArguments(args);


        return f;
    }
    public void refreshData(){
        mPost.refreshInBackground(new RefreshCallback() {
            @Override
            public void done(ParseObject object, ParseException e) {
                Post thisPost = (Post) object;
                //Get title
                String postTitle = thisPost.getTitle();
                TextView titleTextView = (TextView) getActivity().findViewById(R.id.post_title);
//                    Log.i(TAG, "Title: "+postTitle);
                titleTextView.setText(postTitle);

                //Get post body
                String postBody = thisPost.getBody();
                TextView bodyTextView = (TextView) getActivity().findViewById(R.id.post_body);
                bodyTextView.setText(postBody);

            }
        });
        commentsAdapter.notifyDataSetChanged();
        commentsAdapter.loadObjects();

    }
}
