package org.opencv.samples.facedetect;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Robi on 04/09/2017.
 */

public class CaptureImageActivity extends BaseAppActivity {

    private String mCurrentPhotoPath;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    // private static final int CAMERA_REQUEST = 1888;

    ImageView mImageView;
    Button mButtonCancel, mButtonSend;
    ImageButton mButtonStart;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_image);

        mImageView = (ImageView) this.findViewById(R.id.imageFromCamera);
        mButtonStart = (ImageButton) this.findViewById(R.id.takePictureFromCamera);
        mButtonSend = (Button) this.findViewById(R.id.sendPictureBtn);
        mButtonCancel = (Button) this.findViewById(R.id.cancelSendBtn);

        // Show camera onCreate
        mButtonStart.performClick();
    }

    public void takePicture(View view){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Make sure that device has camera to handle intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null){
            // Create the File where the photo should go
            File photoFile = null;
            try{
                photoFile = createImageFile();
            }
            catch (IOException e){
                e.printStackTrace();
                Toast.makeText(this, "Unable to create image path.", Toast.LENGTH_SHORT).show();
            }

            if (photoFile != null){
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    public void sendPicture(View view){

    }

    public void cancelSend(View view){
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE){
            switch (resultCode){
                case Activity.RESULT_OK:
                    Bitmap mPhoto = BitmapFactory.decodeFile(mCurrentPhotoPath);
                    BitmapDrawable bd = new BitmapDrawable(getResources(), mPhoto);
//                    mImageView.setBackgroundDrawable(bd);
                    mImageView.setImageBitmap(mPhoto);

                    try{
                        // Check image rotation and rotate it so it is always vertical
                        ExifInterface exif = new ExifInterface(mCurrentPhotoPath);
                        int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
                        switch(exifOrientation){
                            case ExifInterface.ORIENTATION_NORMAL:
                                mImageView.setRotation(0);
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_90:
                                mImageView.setRotation(90);
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_180:
                                mImageView.setRotation(180);
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_270:
                                mImageView.setRotation(270);
                                break;
                        }
                    }
                    catch (IOException e){

                    }

                    break;
                case Activity.RESULT_CANCELED:
                    // Go to parent activity if no picture was already taken
                    if (mImageView.getDrawable() == null){
                        mButtonCancel.performClick();
                    }
                    break;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private File createImageFile() throws IOException {
        // Create image file name
        String timestamp = new SimpleDateFormat("yyyy_mm_dd_HH_mm_ss").format(new Date());
        String imageFileName = "JPEG_" + timestamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
}
