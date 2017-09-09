package org.opencv.samples.facedetect;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.MutableContextWrapper;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
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

import org.opencv.samples.facedetect.utilities.SettingsUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Robi on 04/09/2017.
 */

public class CaptureImageActivity extends BaseAppActivity {

    private Bitmap mPhoto;
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

        RecognizeTask task = new RecognizeTask(this);
        task.execute(mPhoto);
    }

    public void cancelSend(View view){
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE){
            switch (resultCode){
                case Activity.RESULT_OK:
                    mPhoto = BitmapFactory.decodeFile(mCurrentPhotoPath);
//                    BitmapDrawable bd = new BitmapDrawable(getResources(), mPhoto);
//                    mImageView.setBackgroundDrawable(bd);


                    try{
                        float degrees = 0;
                        // Check image rotation and rotate it so it is always vertical
                        ExifInterface exif = new ExifInterface(mCurrentPhotoPath);
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

                        Matrix matrix = new Matrix();
                        matrix.postRotate(degrees);
                        mPhoto = Bitmap.createBitmap(mPhoto, 0, 0, mPhoto.getWidth(), mPhoto.getHeight(), matrix, true);
                        mImageView.setImageBitmap(mPhoto);
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

    private class RecognizeTask extends AsyncTask<Bitmap, String, String> {

        private final String SUCCESS_MSG = "Success!";
        private final String RECOGNIZE_IMAGE_SUBMIT_NAME = "file";
        private String mErrorMsg;
        private URL mUrl;
        private HttpURLConnection mHttpUrlConnection;
        private DataOutputStream mRequest;
        private ProgressDialog mProgressDialog;
        private Activity mCallingActivity;

        private String mCrlf = "\r\n";
        private String mTwoHyphens = "--";
        private String mBoundary = "*****";

        public RecognizeTask(Activity activity){
            mCallingActivity = activity;
            initializeProgressDialog();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(Bitmap... params) {
            try {
                publishProgress("Checking server availability...");

                // Throws not found exception if server is not available
                checkServerAvailability();

                publishProgress("Setting up headers...");
                setupPostHeaders();

                publishProgress("Setting up body...");
                setupPostBody();

                publishProgress("Sending request...");
                InputStream responseStream = new BufferedInputStream(mHttpUrlConnection.getInputStream());
                BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(responseStream));

                publishProgress("Parsing server response...");
                String line = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ((line = responseStreamReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                responseStreamReader.close();
                String response = stringBuilder.toString();
                return response;
            }
            catch (Resources.NotFoundException e){
                return "Server is not available.";
            }
            catch (MalformedURLException e){
                return "Recognize URL is invalid.";
            }
            catch (IOException e){
                return String.format("%s\n%s", e.getMessage(), "Server Error.");
            }
            catch (Exception e){
                return e.getMessage();
            }
            finally {
                if (mHttpUrlConnection != null){
                    mHttpUrlConnection.disconnect();
                }
            }
        }

        @Override
        protected void onProgressUpdate(String... messages) {
            super.onProgressUpdate(messages);
            mProgressDialog.setMessage(messages[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            hideProgressDialog();
            if (result != SUCCESS_MSG){
                showAlertDialog(result, "Error");
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Toast.makeText(CaptureImageActivity.this, mErrorMsg, Toast.LENGTH_SHORT).show();
            hideProgressDialog();
        }

        private void setupPostHeaders() throws MalformedURLException, IOException {
            mUrl = new URL(SettingsUtils.UriFactory(SettingsUtils.UriType.RECOGNIZE).toString());

            mHttpUrlConnection = (HttpURLConnection) mUrl.openConnection();
            mHttpUrlConnection.setUseCaches(false);
            mHttpUrlConnection.setDoOutput(true);
            mHttpUrlConnection.setRequestMethod("POST");
            mHttpUrlConnection.setRequestProperty("Connection", "Keep-Alive");
            mHttpUrlConnection.setRequestProperty("Cache-Control", "no-cache");
            mHttpUrlConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + mBoundary);
        }

        private void setupPostBody() throws IOException{
            File file = new File(mCurrentPhotoPath);
            byte[] imageByteArray = convertImageToByteArray(mPhoto);

            mRequest = new DataOutputStream(mHttpUrlConnection.getOutputStream());
            mRequest.writeBytes(mTwoHyphens + mBoundary + mCrlf);
            mRequest.writeBytes("Content-Disposition: form-data; name=\"" + RECOGNIZE_IMAGE_SUBMIT_NAME + "\";filename=" + file.getName() + ";" + mCrlf);
            mRequest.writeBytes("Content-Type: image/jpeg" + mCrlf);
            mRequest.writeBytes(mCrlf);
            mRequest.write(imageByteArray);
            mRequest.writeBytes(mCrlf);
            mRequest.writeBytes(mTwoHyphens + mBoundary + mTwoHyphens);
            mRequest.flush();
            mRequest.close();
        }

        private byte[] convertImageToByteArray(Bitmap img){
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            mPhoto.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            return bos.toByteArray();
        }

        private void checkServerAvailability() throws Resources.NotFoundException {
            HttpURLConnection conn = null;
            Boolean available = null;
            try{
                // Test with this URL because RECOGNIZE uri takes POST request
                URL url = new URL(SettingsUtils.UriFactory(SettingsUtils.UriType.PEOPLE_API).toString());
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(10000);
                conn.connect();
                available = (conn.getResponseCode() == 200);

            }
            catch (IOException e){
                available = false;
            }
            finally {
                if (conn != null){
                    conn.disconnect();
                }
            }

            if (!available){
                throw new Resources.NotFoundException("Server cannot be reached.");
            }
        }

        private void initializeProgressDialog(){
            mProgressDialog = new ProgressDialog(mCallingActivity);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setTitle("Please wait...");
            mProgressDialog.setMessage("Loading...");

            mProgressDialog.setIndeterminate(true); // Loading amount is not measured
            mProgressDialog.setCanceledOnTouchOutside(false);
        }

        private void hideProgressDialog(){
            if (mProgressDialog.isShowing()){
                mProgressDialog.dismiss();
            }
        }

        private void stopTask(){
            if (this != null && !isCancelled()){
                this.cancel(true);
            }
        }

        private void showAlertDialog(String msg, String title){
            AlertDialog dialog = new AlertDialog.Builder(mCallingActivity).create();
            if (title != null && title.length() > 0){
                dialog.setTitle(title);
            }

            dialog.setMessage(msg);
            dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            dialog.show();
        }
    }
}
