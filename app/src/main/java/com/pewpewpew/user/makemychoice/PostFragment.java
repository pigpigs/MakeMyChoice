package com.pewpewpew.user.makemychoice;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by User on 27/10/14.
 */
public class PostFragment extends Fragment {
    private String mCurrentPhotoPath; //bundle for config changes
    private final String TAG = "PostFragment_debug";
    private static final int REQUEST_IMAGE_CAPTURE = 123;
    public PostFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_text_post, container, false);
        Button button = (Button)v.findViewById(R.id.button_take_picture);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ImageView postImageView= (ImageView) getActivity().findViewById(R.id.post_image);
//                postImageView.setVisibility(View.INVISIBLE);
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (cameraIntent.resolveActivity(getActivity().getPackageManager())!=null){
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageCache(getActivity());
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        Log.i(TAG, "Error Creating File!!! No Space??");
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(photoFile));
                        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
                    }
                }else{
                    Toast.makeText(getActivity(), "Please check your camera", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "No camera to run");
                }
            }
        });
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE){
            if (resultCode == Activity.RESULT_OK){
                // TODO - Rotate and re-save file into portrait (if this takes off, for performance) The best solution will be to override the camera taking process and rotate it upon saving
                if(mCurrentPhotoPath!=null){
                    final ImageView postImageView= (ImageView) getActivity().findViewById(R.id.post_image);
                    postImageView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                        // Ensure the view has enough time to change before BitmapWorkerTask runs!
                        @Override
                        public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                            // NOTE - this might not work on some devices, have to double check
                            postImageView.removeOnLayoutChangeListener(this);
                            Log.e(TAG, "W:" + postImageView.getWidth() + " H:"+postImageView.getHeight());
                            ((Callback) getActivity()).onImageCreated(mCurrentPhotoPath);
                            BitmapWorkerTask task = new BitmapWorkerTask(mCurrentPhotoPath,postImageView);
                            task.execute();
                        }
                    });
                    // Set it as visible to take up necessary space for bitmap computation
                    postImageView.setVisibility(View.INVISIBLE);

                    //TODO - Implement cache clearing if this takes off!

                }
            }
        }
    }

    private File createImageCache(Context context) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalCacheDir();

        Log.i(TAG, "StorageDir: "+storageDir);

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }



    public interface Callback{
        public void onImageCreated(String imagePath);
    }
}
