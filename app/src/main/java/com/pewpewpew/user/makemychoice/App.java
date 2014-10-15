package com.pewpewpew.user.makemychoice;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Created by User on 15/10/14.
 */
public class App extends Application{
    static String PARSE_APPLICATION_ID = "bUeHCuWuE5uOmvq8zNoBHQnyPKgmiwydAgCyPJmb";
    static String PARSE_CLIENT_KEY = "iyWexA1bv6ntSDuCejd3KNj7uweNAKzWFC6UdN5c";
    @Override
    public void onCreate() {
        super.onCreate();
        ParseObject.registerSubclass(Post.class);
        Parse.initialize(this, PARSE_APPLICATION_ID, PARSE_CLIENT_KEY);
    }
}
