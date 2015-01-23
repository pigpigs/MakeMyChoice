package com.pewpewpew.user.makemychoice;

/**
 * Created by User on 11/1/15.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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

import java.util.List;

/**
 * The outcome fragment will currently use the same layout as the DetailFragment.
 *
 * TODO
 * 1) Make outcome parse class, link to comments
 *
 * Future - polls
 */
public class OutcomeFragment extends Fragment {

    private static final String TAG = "OutcomeFragment_debug";
    private static final int REQUEST_REPLY = 101;
    private static Post mPost;
    private static Outcome mOutcome;
    static String postId;
    public OutcomeFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Intent intent = getActivity().getIntent();
        postId = intent.getStringExtra(MainFragment.KEY_POST_ID);

        final View v = inflater.inflate(R.layout.fragment_detail_stub, container, false);



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
        // todo - change the postid to that of the outcome's?
        bundle.putString(CommentDialogFragment.KEY_REPLY,postId);
        // Set Args for fragment and commit
        fragment.setArguments(bundle);
        fragment.setTargetFragment(this, REQUEST_REPLY);
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();

        fragment.show(ft, "CommentDialogFragment");
//        ft.add(R.id.detail_container,fragment)
//                .commit();
    }

    private void toggleVisibility(){
        LinearLayout container = (LinearLayout) getView().findViewById(R.id.expanded_image_container);

        if(container.getVisibility() == View.GONE){
            container.setVisibility(View.VISIBLE);
            ParseImageView parseImageView = (ParseImageView) getView().findViewById(R.id.expanded_image_view);
            if(parseImageView.getDrawable() == null){
                Log.i(TAG, "Setting Image File.");
                parseImageView.setParseFile(mOutcome.getImage());
                parseImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        toggleVisibility();
                    }
                });

                parseImageView.loadInBackground();
            }

        }else{
            container.setVisibility(View.GONE);

            // Unload image??
        }
    }

    public void inflateViews(String outcomeID){
        Log.i(TAG, "outcomeid is " + outcomeID);

        // Remove the placeholder
        getView().findViewById(R.id.detail_placeholder).setVisibility(View.GONE);

        final View inflatedView = ((ViewStub) getView().findViewById(R.id.detail_stub)).inflate();
        final ListView listView = (ListView) inflatedView.findViewById(R.id.listView_comments);



        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View headerView = inflater.inflate(R.layout.fragment_detail_header, listView, false);
        listView.addHeaderView(headerView);


        // Buttons
        ImageButton replyButton = (ImageButton) inflatedView.findViewById(R.id.button_reply);
        replyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replyToPost(postId); // the argument is currently useless, considering whether should implement another method for replying to comment
            }
        });

        // Heart Button for following - Check if user has followed the post. if yes, heart should be lit up, and will unfav onClick
        final ImageButton heartButton = (ImageButton) inflatedView.findViewById(R.id.button_heart);

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

        Post post = ParseObject.createWithoutData(Post.class, postId);
        post.fetchIfNeededInBackground(new GetCallback<Post>() {
            @Override
            public void done(final Post thisPost, ParseException e) {
                mPost = thisPost;

                // Get meta data
                ((TextView) inflatedView.findViewById(R.id.detail_metadata)).setText(
                        getActivity().getString(R.string.detail_metadata,
                                Utility.getTimeSince(thisPost.getCreatedAt()), // timeSince
                                thisPost.getUserStr(), // Username
                                thisPost.getNumComments()
                        ));

                // Retrieve comments for the current Post
                ParseQueryAdapter.QueryFactory<Comment> factory = new ParseQueryAdapter.QueryFactory<Comment>() {
                    @Override
                    public ParseQuery<Comment> create() {
                        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);
                        query.whereEqualTo("parentPost", thisPost);
                        query.orderByDescending("createdAt");
                        return query;
                    }
                };
                // FUTURE- Posts to have polls for the choices, comments will indicate which choice the user chose
                ParseQueryAdapter<Comment> commentsAdapter =
                        new ParseQueryAdapter<Comment>(getActivity(), factory){
                            @Override
                            public View getItemView(Comment comment, View v, ViewGroup parent) {
                                if (v == null){
                                    v = View.inflate(getContext(),R.layout.list_comment_item, null);
                                }
//                                    Log.i(TAG, "Comment: "+ comment);
                                ((TextView) v.findViewById(R.id.comment_body)).setText(comment.getBody());
                                ((TextView) v.findViewById(R.id.comment_meta)).setText(
                                        String.format(" %s | %s ",
                                                "username",
                                                Utility.getTimeSince(comment.getCreatedAt())
                                        )
                                );
                                return v;
                            }
                        };

                listView.setAdapter(commentsAdapter);
            }
        });


        Outcome outcome = ParseObject.createWithoutData(Outcome.class, outcomeID);
        outcome.fetchIfNeededInBackground(new GetCallback<Outcome>() {
            @Override
            public void done(Outcome thisOutcome, ParseException e) {
                mOutcome = thisOutcome;
                // Get Title
                String title = thisOutcome.getTitle();
                TextView titleTextView = (TextView) headerView.findViewById(R.id.post_title);
                titleTextView.setText(title);

                //Get  outcome body
                String outcomeBody = thisOutcome.getBody();
                TextView bodyTextView = (TextView) headerView.findViewById(R.id.post_body);
                bodyTextView.setText(outcomeBody);

                // Retrieve image if any
                ParseFile parseFile = thisOutcome.getImage();

                if(parseFile== null){
                    Log.i(TAG, "No image!");
                }else{
                    View stub = ((ViewStub)headerView.findViewById(R.id.detail_imagestub)).inflate();
                    ParseImageView imageView = (ParseImageView) stub.findViewById(R.id.post_image);
                    imageView.setParseFile(parseFile);
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
            }
        });
    }

    public void refreshData(){
        if(getView().findViewById(R.id.detail_stub) == null) {
            // Refresh data as per normal if there was an outcome
            mOutcome.refreshInBackground(new RefreshCallback() {
                @Override
                public void done(ParseObject parseObject, ParseException e) {
                    Outcome thisOutcome = (Outcome) parseObject;

                    // Get Title
                    String title = thisOutcome.getTitle();
                    TextView titleTextView = (TextView) getView().findViewById(R.id.post_title);
                    titleTextView.setText(title);

                    //Get  outcome body
                    String outcomeBody = thisOutcome.getBody();
                    Log.i(TAG, "outcome body: " + outcomeBody);
                    TextView bodyTextView = (TextView) getView().findViewById(R.id.post_body);
                    bodyTextView.setText(outcomeBody);
                }
            });
        }else{
            // If there was no outcome, run inflateViews
            Post post = ParseObject.createWithoutData(Post.class, postId);
            post.fetchIfNeededInBackground(new GetCallback<Post>() {
                @Override
                public void done(Post thisPost, ParseException e) {
                    if(thisPost.getOutcome() != null) {
                        // if it now has an outcome inflate the views
                        thisPost.getOutcome().fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject parseObject, ParseException e) {
                                inflateViews(parseObject.getObjectId());
                            }
                        });
                    }
                }
            });
        }
    }
    // for PagerAdapter, not sure if needed but wtv
    public static Fragment newInstance(int i) {
        OutcomeFragment f = new OutcomeFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", i);
        f.setArguments(args);

        return f;
    }
}
