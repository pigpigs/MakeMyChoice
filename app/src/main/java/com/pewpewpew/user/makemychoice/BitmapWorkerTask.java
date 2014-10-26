package com.pewpewpew.user.makemychoice;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

/**
 * Created by User on 26/10/14.
 */
public class BitmapWorkerTask extends AsyncTask<Void, Void, Bitmap> {
    private String mCurrentPhotoPath;
    private ImageView mImageView;
    private String TAG = "BitmapWorkerTask_debug";
    public BitmapWorkerTask(String path, ImageView iv){
        mCurrentPhotoPath = path;
        mImageView = iv;
    }
    @Override
    protected Bitmap doInBackground(Void... voids) {

        int reqWidth = mImageView.getWidth();
        int reqHeight = mImageView.getHeight();
        //Decode and scale image
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, options);
//        int imageHeight = options.outHeight;
//        int imageWidth = options.outWidth;
        options.inSampleSize = calculateInSampleSize(options,reqWidth, reqHeight);
        options.inJustDecodeBounds = false;


        Bitmap imageBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath,options);


        //TODO -ROTATE IMAGE TO PORTRAIT
        return imageBitmap;
    }

    @Override
    protected void onPostExecute(Bitmap imageBitmap) {
        mImageView.setVisibility(View.VISIBLE);
        mImageView.setImageBitmap(imageBitmap);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
