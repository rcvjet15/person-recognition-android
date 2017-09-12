package org.opencv.samples.facedetect;

import android.app.Activity;
import android.content.Intent;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.samples.facedetect.utilities.ImageUtils;
import org.opencv.samples.facedetect.utilities.ResponseMessageRecognize;

import java.io.File;
import java.io.IOException;

/**
 * Created by Robi on 10/09/2017.
 */

public class PersonActivity extends BaseAppActivity {

    private Person mPerson;
    private ImageView mImageView;
    private TextView mFirstNameText;
    private TextView mLastNameText;
    private TextView mAgeText;
    private TextView mEmailText;
    private TextView mFaceEncodingText;
    private LinearLayout mShowFooter;
    private LinearLayout mActionFooter;
    private Button mSaveBtn;
    private EditText mFirstNameEdit;
    private EditText mLastNameEdit;
    private EditText mAgeEdit;
    private EditText mEmailEdit;
    private EditText mFaceEncodingEdit;
    private Bitmap mPhoto;
    private String mCurrentPhotoPath;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Bundle bundleExtras = intent.getExtras();

        if (bundleExtras != null){

            // Called from RecognizeActivity to save recognized person
            if (bundleExtras.containsKey(Intent.ACTION_INSERT)){
                setupInsertActivity(bundleExtras);
            }
            else if (bundleExtras.containsKey(Intent.ACTION_INSERT_OR_EDIT)){
                setupInsertOrEditActivity(bundleExtras);
            }

            if (mPerson != null){ this.getActionBar().setTitle(String.format("%s %s", mPerson.getFirstName(), mPerson.getLastName())); }
        }
    }

    /**
     * Sets up activity for saving Person after person is recognized.
     * @param extras
     */
    private void setupInsertActivity(Bundle extras){
        setContentView(R.layout.activity_person_insert_or_show);

        mActionFooter = (LinearLayout) findViewById(R.id.actionFooter);
        mActionFooter.setVisibility(View.VISIBLE);

        mSaveBtn = (Button) findViewById(R.id.saveBtn);

        mImageView = (ImageView) findViewById(R.id.profilePicShow);
        mFirstNameText = (TextView) findViewById(R.id.firstNameShow);
        mLastNameText = (TextView) findViewById(R.id.lastNameShow);
        mAgeText = (TextView) findViewById(R.id.ageShow);
        mEmailText = (TextView) findViewById(R.id.emailShow);
        mFaceEncodingText = (TextView) findViewById(R.id.faceEncodingShow);

        mPerson = extras.getParcelable(Intent.ACTION_INSERT);

        mImageView.setImageBitmap(mPerson.convertImageBase64ToBitmap());
        mFirstNameText.setText(String.format("%s %s", mFirstNameText.getText(), mPerson.getFirstName()));
        mLastNameText.setText(String.format("%s %s", mLastNameText.getText(), mPerson.getLastName()));
        mAgeText.setText(String.format("%s %d", mAgeText.getText(), mPerson.getAge()));
        mEmailText.setText(String.format("%s %s", mEmailText.getText(), mPerson.getEmail()));
        mFaceEncodingText.setText(String.format("%s %s", mFaceEncodingText.getText(), mPerson.getFaceEncoding()));
    }

    /**
     * Sets up activity for creating or updating Person and sending person data to server.
     * @param extras
     */
    private void setupInsertOrEditActivity(Bundle extras){
        setContentView(R.layout.activity_person_insert_or_edit);

        mActionFooter = (LinearLayout) findViewById(R.id.actionFooter);
        mActionFooter.setVisibility(View.VISIBLE);

        mSaveBtn = (Button) findViewById(R.id.saveBtn);

        mImageView = (ImageView) findViewById(R.id.profilePicShow);
        mFirstNameEdit = (EditText) findViewById(R.id.firstNameInput);
        mLastNameEdit = (EditText) findViewById(R.id.lastNameInput);
        mAgeEdit = (EditText) findViewById(R.id.ageInput);
        mEmailEdit = (EditText) findViewById(R.id.emailInput);
    }

    @Override
    public void cancel(View view){
        super.cancel(view);
    }

    public void save(View view){
        try{
            long newRowId = mPerson.saveToDb(db, dbHelper);

            if (newRowId > 0){
                Toast.makeText(this, String.format("Successfully created %s %s", mPerson.getFirstName(), mPerson.getLastName()), Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, String.format("Error creating %s %s", mPerson.getFirstName(), mPerson.getLastName()), Toast.LENGTH_SHORT).show();
            }

            // Go to MainActivity with destroying all activities between destination and this
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        catch (SQLException e){
            showAlertDialog(this, e.getMessage(), e.getClass().toString());
        }
    }

    public void takePicture(View view){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Make sure that device has camera to handle intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null){
            // Create the File where the photo should go
            File photoFile = null;
            try{
                photoFile = ImageUtils.createImageFile(this);
                mCurrentPhotoPath = photoFile.getAbsolutePath();
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE){
            switch (resultCode){
                case Activity.RESULT_OK:
                    try{
                        mPhoto = ImageUtils.setupPictureVertical(mCurrentPhotoPath);
                        mImageView.setImageBitmap(mPhoto);
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

    // Called after async task is finished
    private void onBackgroundTaskCompleted(ResponseMessageRecognize result){
        if (result.getStatus() == 1){
            mSaveBtn.performClick();
        }
    }



//    private class RecognizeTask extends AsyncTask<Bitmap, String, IResponseMessage> {
//
//        private final String SUCCESS_MSG = "Success!";
//        private final String RECOGNIZE_IMAGE_SUBMIT_NAME = "profile-pic";
//        private String mErrorMsg;
//        private URL mUrl;
//        private HttpURLConnection mHttpUrlConnection;
//        private DataOutputStream mRequest;
//        private ProgressDialog mProgressDialog;
//        private RecognizeActivity mCallingActivity;
//        private List<IResponseMessage> mResponseMessageList;
//
//        private String mCrlf = "\r\n";
//        private String mTwoHyphens = "--";
//        private String mBoundary = "*****";
//
//        public RecognizeTask(RecognizeActivity activity){
//            mCallingActivity = activity;
//            initializeProgressDialog();
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            mProgressDialog.show();
//        }
//
//        @Override
//        protected IResponseMessage doInBackground(Bitmap... params) {
//            try {
//                publishProgress("Checking server availability...");
//
//                // Throws not found exception if server is not available
//                checkServerAvailability();
//
//                publishProgress("Setting up headers...");
//                setupPostHeaders();
//
//                publishProgress("Setting up body...");
//                setupPostBody();
//
//                publishProgress("Sending request...");
//                InputStream responseStream = new BufferedInputStream(mHttpUrlConnection.getInputStream());
//
//                publishProgress("Parsing server response...");
//                RecognizeJsonUtils jsonUtils = new RecognizeJsonUtils();
//                List<IResponseMessage> messageList = jsonUtils.readJsonStream(responseStream);
//
//                if (messageList.size() > 0){
//                    return messageList.get(0);
//                }
//                else{
//                    throw new Exception("Server returned empty response.");
//                }
//            }
//            catch (ConnectException e){
//                // Simulate server response
//                return new ResponseMessageRecognize(null, null, -1, "Server is not available.");
//            }
//            catch (MalformedURLException e){
//                return new ResponseMessageRecognize(null, null, -1, "Recognize URL is invalid.");
//            }
//            catch (IOException e){
//                return new ResponseMessageRecognize(null, null, -1, String.format("%s\n%s", e.getMessage(), "Server Error."));
//            }
//            catch (Exception e){
//                return new ResponseMessageRecognize(null, null, -1, String.format("%s\n%s", e.getMessage(), "Unknown Error."));
//            }
//            finally {
//                if (mHttpUrlConnection != null){
//                    mHttpUrlConnection.disconnect();
//                }
//            }
//        }
//
//        @Override
//        protected void onProgressUpdate(String... messages) {
//            super.onProgressUpdate(messages);
//            mProgressDialog.setMessage(messages[0]);
//        }
//
//        @Override
//        protected void onPostExecute(IResponseMessage result) {
//            super.onPostExecute(result);
//            hideProgressDialog();
//
//            // Show alert dialog if status is not 1
//            ResponseMessageRecognize responseMsgObj = (ResponseMessageRecognize)result;
//            if (responseMsgObj.getStatus() != 1){
//                showAlertDialog(mCallingActivity, responseMsgObj.getResponseMsg(), "Error");
//            }
//
//            // Callback method, pass result to calling activity
//            mCallingActivity.onBackgroundTaskCompleted((ResponseMessageRecognize) result);
//        }
//
//        @Override
//        protected void onCancelled() {
//            super.onCancelled();
//            if (mErrorMsg != null && mErrorMsg.length() > 0)
//                Toast.makeText(mCallingActivity, mErrorMsg, Toast.LENGTH_SHORT).show();
//
//            if (mHttpUrlConnection != null) {
//                mHttpUrlConnection.disconnect();
//            }
//
//            hideProgressDialog();
//        }
//
//        private void setupPostHeaders() throws MalformedURLException, IOException {
//            mUrl = new URL(Settings.UriFactory(Settings.UriType.RECOGNIZE).toString());
//
//            mHttpUrlConnection = (HttpURLConnection) mUrl.openConnection();
//            mHttpUrlConnection.setUseCaches(false);
//            mHttpUrlConnection.setDoOutput(true);
//            mHttpUrlConnection.setRequestMethod("POST");
//            mHttpUrlConnection.setRequestProperty("Connection", "Keep-Alive");
//            mHttpUrlConnection.setRequestProperty("Cache-Control", "no-cache");
//            mHttpUrlConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + mBoundary);
//        }
//
//        private void setupPostBody() throws IOException{
//            File file = new File(mCurrentPhotoPath);
//            byte[] imageByteArray = convertImageToByteArray(mPhoto);
//
//            mRequest = new DataOutputStream(mHttpUrlConnection.getOutputStream());
//            mRequest.writeBytes(mTwoHyphens + mBoundary + mCrlf);
//            mRequest.writeBytes("Content-Disposition: form-data; name=\"" + RECOGNIZE_IMAGE_SUBMIT_NAME + "\";filename=" + file.getName() + ";" + mCrlf);
//            mRequest.writeBytes("Content-Type: image/jpeg" + mCrlf);
//            mRequest.writeBytes(mCrlf);
//            mRequest.write(imageByteArray);
//            mRequest.writeBytes(mCrlf);
//            mRequest.writeBytes(mTwoHyphens + mBoundary + mTwoHyphens);
//            mRequest.flush();
//            mRequest.close();
//        }
//
//        private byte[] convertImageToByteArray(Bitmap img){
//            ByteArrayOutputStream bos = new ByteArrayOutputStream();
//            mPhoto.compress(Bitmap.CompressFormat.JPEG, 100, bos);
//            return bos.toByteArray();
//        }
//
//        private void checkServerAvailability() throws ConnectException {
//            Boolean available = null;
//            try{
//                // Test with this URL because RECOGNIZE uri takes POST request
//                URL url = new URL(Settings.UriFactory(Settings.UriType.PEOPLE_API).toString());
//                mHttpUrlConnection = (HttpURLConnection) url.openConnection();
//                mHttpUrlConnection.setConnectTimeout(10000);
//                mHttpUrlConnection.connect();
//                available = (mHttpUrlConnection.getResponseCode() == 200);
//
//            }
//            catch (IOException e){
//                available = false;
//            }
//            finally {
//                if (mHttpUrlConnection != null){
//                    mHttpUrlConnection.disconnect();
//                }
//            }
//
//            if (!available){
//                throw new ConnectException("Server cannot be reached.");
//            }
//        }
//
//        private void initializeProgressDialog(){
//            mProgressDialog = new ProgressDialog(mCallingActivity);
//            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//            mProgressDialog.setTitle("Please wait...");
//            mProgressDialog.setMessage("Loading...");
//            mProgressDialog.setButton(ProgressDialog.BUTTON_NEUTRAL, "Cancel",
//                    new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            stopTask();
//                            dialog.dismiss();
//                        }
//                    });
//
//            mProgressDialog.setIndeterminate(true); // Loading amount is not measured
//            mProgressDialog.setCanceledOnTouchOutside(false);
//        }
//
//        private void hideProgressDialog(){
//            if (mProgressDialog.isShowing()){
//                mProgressDialog.dismiss();
//            }
//        }
//
//        private void stopTask(){
//            if (this != null && !isCancelled()){
//                this.cancel(true);
//            }
//        }
//    }
}
