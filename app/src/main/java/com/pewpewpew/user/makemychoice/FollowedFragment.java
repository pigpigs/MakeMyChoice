package com.pewpewpew.user.makemychoice;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by User on 19/1/15.
 */
public class FollowedFragment extends Fragment {
    private static final String TAG = "FollowedFragment_debug";
    private ParseQueryAdapter<Post> mAdapter;
    public static final String KEY_POST_ID = "post_id";
    public static final String KEY_POST_OP = "post_op_key";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Inflate custom view here and instantiate
        final View view = inflater.inflate(R.layout.fragment_followed,container, false);

        // TODO - bundle in stuff to retain instance on config change etc etc

        ListView listView = (ListView) view.findViewById(R.id.listView_main);

        ParseQueryAdapter.QueryFactory<Post> factory = new ParseQueryAdapter.QueryFactory<Post>() {
            @Override
            public ParseQuery<Post> create() {
                // Get all user followed posts
                ParseQuery<Post> query = ParseQuery.getQuery(Post.class);

                ParseQuery<ParseObject> innerQuery = ParseQuery.getQuery("Follow");
                innerQuery.whereEqualTo("from", ParseUser.getCurrentUser());
                //Add a constraint to the query that requires a particular key's value matches a value for a key in the results of another ParseQuery
                query.whereMatchesKeyInQuery("objectId", "postID", innerQuery);
                // order by descending date added
                query.orderByDescending("createdAt");
                return query;
            }
        };
        // This is actually the exact same as the on used in MainFragment, can probably abstract it into a new class
        mAdapter = new ParseQueryAdapter<Post>(getActivity(), factory){
            @Override
            public View getItemView(Post post, View v, ViewGroup parent) {

                if (v == null){
                    v= View.inflate(getContext(),R.layout.list_post_item, null);
                }

                ((TextView)v.findViewById(R.id.post_title)).setText(post.getTitle());

                TextView postTimeSince = (TextView)v.findViewById(R.id.post_time);
                // Get timeSince, numComments and numPoints for post data

                String timeSince = Utility.getTimeSince(post.getCreatedAt());
                postTimeSince.setText(timeSince);

                //Number of comments, use increment on the field every time there is a new post
                // TODO- implement numComments
                TextView postNumComments = (TextView)v.findViewById(R.id.post_comment);
                String numComments = "0";
                postNumComments.setText("0 comments");


                // Get the username string. This was added as loading classes seem to take super long on Parse. Call getUser when needed.
                TextView postUsername = (TextView)v.findViewById(R.id.post_user);

                String username = post.getUserStr();
                postUsername.setText(username);

                if(post.getOutcome() != null){
                    v.setBackgroundColor(getResources().getColor(R.color.list_item_outcome));
                }
                return v;
            }
        };

//        mAdapter.addOnQueryLoadListener(new ParseQueryAdapter.OnQueryLoadListener<Post>() {
//            @Override
//            public void onLoading() {
//                Log.i(TAG, "Loading...");
//                view.findViewById(R.id.main_progressBar).setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onLoaded(List<Post> posts, Exception e) {
//                Log.i(TAG, "Loaded.");
//                view.findViewById(R.id.main_progressBar).setVisibility(View.GONE);
//            }
//        });
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(getActivity(), DetailActivity.class);
                // Pass in post ID here to retrieve anything extra
                ParseQueryAdapter adapter = (ParseQueryAdapter) adapterView.getAdapter();
                Post post = (Post) adapter.getItem(i);

                intent.putExtra(KEY_POST_ID, post.getObjectId());
                intent.putExtra(KEY_POST_OP, post.getUserStr());
                startActivity(intent);
            }
        });
        TextView tv = (TextView) view.findViewById(R.id.emptyListView);
        listView.setEmptyView(tv);
        return view; //return own view here
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_refresh){
            Log.i(TAG, "refreshing data");
            mAdapter.notifyDataSetChanged();
            mAdapter.loadObjects();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static Fragment newInstance(int i) {
        FollowedFragment f = new FollowedFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", i);
        f.setArguments(args);


        return f;
    }
}
