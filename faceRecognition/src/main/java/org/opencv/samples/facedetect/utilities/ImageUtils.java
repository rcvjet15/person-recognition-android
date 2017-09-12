package org.opencv.samples.facedetect.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Robi on 12/09/2017.
 */

public class ImageUtils {

    public static Bitmap setupPictureVertical(String photoPath) throws IOException{
        float degrees = 0;
        // Check image rotation and rotate it so it is always vertical
        ExifInterface exif = new ExifInterface(photoPath);
        int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        switch(exifOrientation){
            case ExifInterface.ORIENTATION_NORMAL:
                degrees = 0;
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                degrees = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                degrees = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                degrees = 270;
                break;
        }

        // todo: Install Glide library for efficient Bitmap manipulation
        BitmapFactory.Options options = new BitmapFactory.Options();
        // Set image pixels 1/4 of original picture pixels
        options.inSampleSize = 4;
        Bitmap photo = BitmapFactory.decodeFile(photoPath, options);

        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        // Rotate it to set it vertical
        photo = Bitmap.createBitmap(photo, 0, 0, photo.getWidth(), photo.getHeight(), matrix, true);
        return photo;
    }

    /**
     * Creates image file.
     * @param context
     * @return
     * @throws IOException
     */
    public static File createImageFile(Context context) throws IOException {
        // Create image file name
        String timestamp = new SimpleDateFormat("yyyy_mm_dd_HH_mm_ss").format(new Date());
        String imageFileName = "JPEG_" + timestamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        return image;
    }
}
