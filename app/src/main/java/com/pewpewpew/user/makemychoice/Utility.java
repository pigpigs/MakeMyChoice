package com.pewpewpew.user.makemychoice;

import android.util.Log;

import java.util.Date;

/**
 * Created by User on 04/10/14.
 */
public class Utility {

    private static final String TAG = "Utility_debug";

    public static String getTimeSince(Date dateCreated){
        // Takes in a date and calculates time elapsed since then in friendly strings.
        // TODO - Longer strings for tablets, shorter for phones, use xml string resources
        Date dateToday = new Date();
        long timeSinceInSecs = (dateToday.getTime() - dateCreated.getTime()) / 1000;
//        Log.i(TAG, "Time Since: "+timeSinceInSecs);
        if (timeSinceInSecs <=60){
            return "a minute ago";
        }else if(timeSinceInSecs < 60*60){
            int mins = (int) timeSinceInSecs/60;
            return mins+" minutes ago";
        }else if(timeSinceInSecs < 60*60*2){
            // Less than 2 hours, just return 1 hour
            return "an hour ago";
        }else if(timeSinceInSecs < 60*60*24){
            //Less than a day ago, return number of hours
            int hours = (int) timeSinceInSecs/(60*60);
            return hours+" hours ago";
        }else if(timeSinceInSecs < 60*60*24*2){
            return "yesterday";
        }else{
            int days = (int) timeSinceInSecs / (24*60*60);
            return days+" days ago";
        }

    }
}
