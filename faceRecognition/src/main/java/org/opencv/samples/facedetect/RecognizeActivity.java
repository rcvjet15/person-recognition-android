package org.opencv.samples.facedetect;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.samples.facedetect.utilities.ImageUtils;
import org.opencv.samples.facedetect.utilities.ResponseMessage;
import org.opencv.samples.facedetect.utilities.RecognizeJsonUtils;
import org.opencv.samples.facedetect.utilities.ResponseMessageRecognize;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Robi on 04/09/2017.
 */

public class RecognizeActivity extends BaseAppActivity {

    private Bitmap mPhoto;
    // File that will get image after camera takes picture
    private File mCurrentImageFile;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    // private static final int CAMERA_REQUEST = 1888;

    ImageView mImageView;
    Button mButtonCancel, mButtonSend;
    ImageButton mButtonStart;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognize);

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
            try{
                mCurrentImageFile = ImageUtils.createImageFile(this);
            }
            catch (IOException e){
                e.printStackTrace();
                Toast.makeText(this, "Unable to create image path.", Toast.LENGTH_SHORT).show();
            }

            if (mCurrentImageFile != null){
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        mCurrentImageFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE){
            switch (resultCode){
                case Activity.RESULT_OK:
                    try{
                        mPhoto = ImageUtils.setupPictureVertical(mCurrentImageFile.getAbsolutePath());

                        RoundedBitmapDrawable roundBitmap = RoundedBitmapDrawableFactory.create(getResources(), mPhoto);
                        roundBitmap.setCircular(true);
                        mImageView.setImageDrawable(roundBitmap);

                    }
                    catch (IOException e){
                        showAlertDialog(this, e.getMessage(), "Photo Setup Error");
                    }

                    break;
                case Activity.RESULT_CANCELED:
                    // Go to parent activity if no picture was already taken
                    if (mImageView.getDrawable() == null){
                        finish();
                    }
                    break;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    public void sendPicture(View view){
        RecognizeTask task = new RecognizeTask(this);
        task.execute(mPhoto);
    }

    // Called after recognize task is finished
    private void onBackgroundTaskCompleted(ResponseMessageRecognize result){
        if (result.getStatus() == 1){
            Person recognizedPerson =  result.getPerson();
            // Check if server contains profile-pic (It must contain otherwise it won't be able to recognize
            // but just in case :D)
            if (result.getImageBase64() != null && result.getImageBase64().length() > 0){
                recognizedPerson.setImageBase64(result.getImageBase64());
            }
            else{
                Toast.makeText(this, "Server did not return profile picture.", Toast.LENGTH_LONG).show();
            }

            Context context = this;
            Class destinationClass = PersonActivity.class;
            Intent intent = new Intent(context, destinationClass);
            intent.putExtra(Intent.ACTION_INSERT, recognizedPerson);
            startActivity(intent);
        }
    }

    @Override
    public void cancel(View view){
        super.cancel(view);
    }

    private class RecognizeTask extends AsyncTask<Bitmap, String, ResponseMessage> {

        private String mErrorMsg;
        private URL mUrl;
        private HttpURLConnection mHttpUrlConnection;
        private DataOutputStream mRequest;
        private ProgressDialog mProgressDialog;
        private RecognizeActivity mCallingActivity;
        private List<ResponseMessage> mResponseMessageList;

        private String mCrlf = "\r\n";
        private String mTwoHyphens = "--";
        private String mBoundary = "*****";

        public RecognizeTask(RecognizeActivity activity){
            mCallingActivity = activity;
            initializeProgressDialog();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog.show();
        }

        @Override
        protected ResponseMessage doInBackground(Bitmap... params) {
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

                publishProgress("Parsing server response...");
                RecognizeJsonUtils jsonUtils = new RecognizeJsonUtils();
                List<ResponseMessage> messageList = jsonUtils.readJsonStream(responseStream);

                if (messageList.size() > 0){
                    return messageList.get(0);
                }
                else{
                    throw new Exception("Server returned empty response.");
                }
            }
            catch (ConnectException e){
                // Simulate server response
                return new ResponseMessageRecognize(null, null, -1, "Server is not available.");
            }
            catch (MalformedURLException e){
                return new ResponseMessageRecognize(null, null, -1, "Recognize URL is invalid.");
            }
            catch (IOException e){
                return new ResponseMessageRecognize(null, null, -1, String.format("%s\n%s", e.getMessage(), "Server Error."));
            }
            catch (Exception e){
                return new ResponseMessageRecognize(null, null, -1, String.format("%s\n%s", e.getMessage(), "Unknown Error."));
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
        protected void onPostExecute(ResponseMessage result) {
            super.onPostExecute(result);
            hideProgressDialog();

            // Show alert dialog if status is not 1
            ResponseMessageRecognize responseMsgObj = (ResponseMessageRecognize)result;
            if (responseMsgObj.getStatus() != 1){
                showAlertDialog(mCallingActivity, responseMsgObj.getResponseMsg(), "Error");
            }

            // Callback method, pass result to calling activity
            mCallingActivity.onBackgroundTaskCompleted((ResponseMessageRecognize) result);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            if (mErrorMsg != null && mErrorMsg.length() > 0)
                Toast.makeText(mCallingActivity, mErrorMsg, Toast.LENGTH_SHORT).show();

            if (mHttpUrlConnection != null) {
                mHttpUrlConnection.disconnect();
            }

            hideProgressDialog();
        }

        private void setupPostHeaders() throws MalformedURLException, IOException {
            mUrl = new URL(Settings.UriFactory(Settings.UriType.RECOGNIZE).toString());

            mHttpUrlConnection = (HttpURLConnection) mUrl.openConnection();
            mHttpUrlConnection.setUseCaches(false);
            mHttpUrlConnection.setDoOutput(true);
            mHttpUrlConnection.setRequestMethod("POST");
            mHttpUrlConnection.setRequestProperty("Connection", "Keep-Alive");
            mHttpUrlConnection.setRequestProperty("Cache-Control", "no-cache");
            mHttpUrlConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + mBoundary);
        }

        private void setupPostBody() throws IOException{
            byte[] imageByteArray = convertImageToByteArray(mPhoto);

            mRequest = new DataOutputStream(mHttpUrlConnection.getOutputStream());
            mRequest.writeBytes(mTwoHyphens + mBoundary + mCrlf);
            mRequest.writeBytes("Content-Disposition: form-data; name=\"" + Person.PROFILE_PIC_SUBMIT_PARAM + "\";filename=" + mCurrentImageFile.getName() + ";" + mCrlf);
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

        private void checkServerAvailability() throws ConnectException {
            Boolean available = null;
            try{
                // Test with this URL because RECOGNIZE uri takes POST request
                URL url = new URL(Settings.UriFactory(Settings.UriType.PEOPLE_API).toString());
                mHttpUrlConnection = (HttpURLConnection) url.openConnection();
                mHttpUrlConnection.setConnectTimeout(10000);
                mHttpUrlConnection.connect();
                available = (mHttpUrlConnection.getResponseCode() == 200);

            }
            catch (IOException e){
                available = false;
            }
            finally {
                if (mHttpUrlConnection != null){
                    mHttpUrlConnection.disconnect();
                }
            }

            if (!available){
                throw new ConnectException("Server cannot be reached.");
            }
        }

        private void initializeProgressDialog(){
            mProgressDialog = new ProgressDialog(mCallingActivity);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setTitle("Please wait...");
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setButton(ProgressDialog.BUTTON_NEUTRAL, "Cancel",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            stopTask();
                            dialog.dismiss();
                        }
                    });

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
    }
}
