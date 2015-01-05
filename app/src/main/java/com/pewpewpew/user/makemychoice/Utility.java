package com.pewpewpew.user.makemychoice;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;

import java.io.IOException;
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

    /**
     *
     * @param mImagePath, path of the image file
     * @return subsampled,rotated bitmap
     */
    public static Bitmap makeParseBitmap(String mImagePath){
        // Consider making this run in an AsyncTask

        //Decode and scale image
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mImagePath,options);
//        int curWidth = options.outWidth;
//        int curHeight = options.outHeight;
//        Log.i(TAG, "Dimensions (Source): " + curWidth + " X " + curHeight);
        //TODO - Consider using device width + set height instead?
        options.inSampleSize = BitmapWorkerTask.calculateInSampleSize(options,1032, 600);
        options.inJustDecodeBounds = false;

        //Rotate Image
        int rotationInDegrees=0;
        try {
            ExifInterface exif = new ExifInterface(mImagePath);
            int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            rotationInDegrees = exifToDegrees(rotation);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "Rotated by: " + rotationInDegrees);
        Matrix matrix = new Matrix();
        // Don't even get why you just use back rotationInDegrees @@
        matrix.postRotate(rotationInDegrees);
        Bitmap temp = BitmapFactory.decodeFile(mImagePath,options);
//        Log.i(TAG, "Dimensions (Sampled): " + temp.getWidth()+ " X " + temp.getHeight());
        Bitmap rotatedBitmap = Bitmap.createBitmap(temp, 0, 0, temp.getWidth(), temp.getHeight(), matrix, true);
//        Log.i(TAG, "Dimensions (Rotated): " + rotatedBitmap.getWidth() + " X " + rotatedBitmap.getHeight());
        return rotatedBitmap;
    }

    private static int exifToDegrees(int exifOrientation) {
        // Get orientation of image from metadata of image file
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }
        return 0;
    }
}
