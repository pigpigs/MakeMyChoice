package com.pewpewpew.user.makemychoice;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

/**
 * Created by User on 24/9/14.
 */
public class MainFragment extends Fragment {
    public MainFragment(){

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
                // Order by points, where datetime less than 5 days ago
                return query;
            }
        };
        ParseQueryAdapter<ParseObject> adapter = new ParseQueryAdapter<ParseObject>(getActivity(), factory){
            @Override
            public View getItemView(ParseObject object, View v, ViewGroup parent) {

                if (v == null){
                    v= View.inflate(getContext(),R.layout.list_post_item, null);
                }
//                ((TextView)v.findViewById(R.id.textView)) .setText(object.getString("foo"));
//                ((TextView) v.findViewById(R.id.textView2)).setText(object.getCreatedAt().toString());
                return v;
            }
        };
        listView.setAdapter(adapter);
        return view; //return own view here
    }
}
