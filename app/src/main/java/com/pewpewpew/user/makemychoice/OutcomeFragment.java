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

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.RefreshCallback;

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
    static Activity mContext;
    static View mView;
    static String postId;
    public OutcomeFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mContext = getActivity();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Intent intent = getActivity().getIntent();
        postId = intent.getStringExtra(MainFragment.KEY_POST_ID);

//        Post post = ParseObject.createWithoutData(Post.class, postId);


        final View v = inflater.inflate(R.layout.fragment_detail_stub, container, false);
        mView = v;




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

    private static void toggleVisibility(){
        LinearLayout container = (LinearLayout) mContext.findViewById(R.id.expanded_image_container);

        if(container.getVisibility() == View.GONE){
            container.setVisibility(View.VISIBLE);
            ParseImageView parseImageView = (ParseImageView) mContext.findViewById(R.id.expanded_image_view);
            if(parseImageView.getDrawable() == null){
                Log.i(TAG, "Setting Image File.");
                parseImageView.setParseFile(mPost.getImage());
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

    public static void inflateViews(String outcomeID){
        Log.i(TAG, "outcomeid is " + outcomeID);

        // Remove the placeholder
        mView.findViewById(R.id.detail_placeholder).setVisibility(View.GONE);

        final View inflatedView = ((ViewStub) mView.findViewById(R.id.detail_stub)).inflate();
        final ListView listView = (ListView) inflatedView.findViewById(R.id.listView_comments);



        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View headerView = inflater.inflate(R.layout.fragment_detail_header, listView, false);
        listView.addHeaderView(headerView);


        Post post = ParseObject.createWithoutData(Post.class, postId);
        post.fetchIfNeededInBackground(new GetCallback<Post>() {
            @Override
            public void done(final Post thisPost, ParseException e) {
                mPost = thisPost;
//                //Get title
//                String postTitle = "[OUTCOME] " + thisPost.getTitle();


                // Retrieve comments for the current Post
                ParseQueryAdapter.QueryFactory<Comment> factory = new ParseQueryAdapter.QueryFactory<Comment>() {
                    @Override
                    public ParseQuery<Comment> create() {
                        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);
                        query.whereEqualTo("parentPost", thisPost);
                        return query;
                    }
                };
                // FUTURE- Posts to have polls for the choices, comments will indicate which choice the user chose
                ParseQueryAdapter<Comment> commentsAdapter =
                        new ParseQueryAdapter<Comment>(mContext, factory){
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
        if(getActivity().findViewById(R.id.detail_stub) == null) {
            // Refresh data as per normal if there was an outcome
            mOutcome.refreshInBackground(new RefreshCallback() {
                @Override
                public void done(ParseObject parseObject, ParseException e) {
                    Outcome thisOutcome = (Outcome) parseObject;

                    // Get Title
                    String title = thisOutcome.getTitle();
                    TextView titleTextView = (TextView) mView.findViewById(R.id.post_title);
                    titleTextView.setText(title);

                    //Get  outcome body
                    String outcomeBody = thisOutcome.getBody();
                    Log.i(TAG, "outcome body: " + outcomeBody);
                    TextView bodyTextView = (TextView) mView.findViewById(R.id.post_body);
                    bodyTextView.setText(outcomeBody);
                }
            });
        }else{
            // If there was no outcome, run inflateViews
            Post post = ParseObject.createWithoutData(Post.class, postId);
            post.fetchIfNeededInBackground(new GetCallback<Post>() {
                @Override
                public void done(Post thisPost, ParseException e) {
                    thisPost.getOutcome().fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject parseObject, ParseException e) {
                            inflateViews(parseObject.getObjectId());
                        }
                    });
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
