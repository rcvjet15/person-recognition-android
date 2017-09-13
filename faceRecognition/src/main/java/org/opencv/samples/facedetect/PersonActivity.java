package org.opencv.samples.facedetect;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.samples.facedetect.utilities.PersonInsertOrEditJsonUtils;
import org.opencv.samples.facedetect.utilities.ResponseMessage;
import org.opencv.samples.facedetect.utilities.ImageUtils;
import org.opencv.samples.facedetect.utilities.RecognizeJsonUtils;
import org.opencv.samples.facedetect.utilities.ResponseMessagePersonInsertOrEdit;
import org.opencv.samples.facedetect.utilities.ResponseMessageRecognize;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

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
    private File mCurrentImageFile;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    // Flag to check if person picture was taken.
    private boolean imageTaken = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Bundle bundleExtras = intent.getExtras();

        if (bundleExtras != null){

            // Called from RecognizeActivity to save recognized person
            if (bundleExtras.containsKey(Intent.ACTION_INSERT)){
                setupInsertActivity(bundleExtras);
                this.getActionBar().setTitle(String.format("%s %s", mPerson.getFirstName(), mPerson.getLastName()));
            }
            else if (bundleExtras.containsKey(Intent.ACTION_INSERT_OR_EDIT)){
                setupInsertOrEditActivity(bundleExtras);
                this.getActionBar().setTitle("Add Person");
            }
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

        // Set profile picture as circle
        setProfilePicture();
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

        mSaveBtn = (Button) findViewById(R.id.saveBtn);

        mImageView = (ImageView) findViewById(R.id.profilePicShow);
        mFirstNameEdit = (EditText) findViewById(R.id.firstNameInput);
        mLastNameEdit = (EditText) findViewById(R.id.lastNameInput);
        mAgeEdit = (EditText) findViewById(R.id.ageInput);
        mEmailEdit = (EditText) findViewById(R.id.emailInput);

        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.person_icon);
        RoundedBitmapDrawable roundBitmap = RoundedBitmapDrawableFactory.create(getResources(), icon );
        roundBitmap.setCircular(true);
        mImageView.setImageDrawable(roundBitmap);

        mPerson = new Person();
    }

    @Override
    public void cancel(View view){
        super.cancel(view);
    }

    /**
     * Called only when activity is opened after person is recognized
     * @param view
     */
    public void insert(View view){
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

    public void save(View view){

        if (!validateData()){ return; }

        mPerson.setImageBase64(ImageUtils.convertBitmapToBase64(mPhoto));
        mPerson.setFirstName(mFirstNameEdit.getText().toString().trim());
        mPerson.setLastName(mLastNameEdit.getText().toString().trim());
        mPerson.setAge(Integer.parseInt(mAgeEdit.getText().toString().trim()));
        mPerson.setEmail(mEmailEdit.getText().toString().trim());

        SetupPersonTask task = new SetupPersonTask(this);
        task.execute();
    }

    public void takePicture(View view){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Make sure that device has camera to handle intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null){
            try{
                // Store in File object taken image
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

    private boolean validateData() {
        boolean valid = true;
        String errorMsg = "";
        if (mFirstNameEdit.getText().length() == 0){
            errorMsg = String.format("%s cannot be empty.", this.getString(R.string.first_name_label));
            valid = false;
        }
        else if (mLastNameEdit.getText().length() == 0){
            errorMsg = String.format("%s cannot be empty.", this.getString(R.string.last_name_label));
            valid = false;
        }
        else if (mAgeEdit.getText().length() == 0){
            errorMsg = String.format("%s cannot be empty.", this.getString(R.string.age_label));
            valid = false;
        }
        else if (mEmailEdit.getText().length() == 0){
            errorMsg = String.format("%s cannot be empty.", this.getString(R.string.email_label));
            valid = false;
        }
        else if (mCurrentImageFile == null){
            errorMsg = "Profile picture is missing.";
            valid = false;
        }

        if (!valid) { showAlertDialog(this, errorMsg, "Settings Error"); }

        return valid;
    }

    /**
     * Method that takes Person's profile picture in Base64 and converts it bitmap that has 8 times less pixels than original
     * and sets that bitmap on image view as circle
     */
    private void setProfilePicture(){
        byte[] photoByteArr = ImageUtils.convertImageBase64ToByteArray(mPerson.getImageBase64());
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        mPhoto = BitmapFactory.decodeByteArray(photoByteArr, 0, photoByteArr.length, options);
        RoundedBitmapDrawable roundBitmap = RoundedBitmapDrawableFactory.create(getResources(), mPhoto);
        roundBitmap.setCircular(true);
        mImageView.setImageDrawable(roundBitmap);
    }

    private class SetupPersonTask extends AsyncTask<Void, String, ResponseMessage> {

        private String mErrorMsg;
        private URL mUrl;
        private HttpURLConnection mHttpUrlConnection;
        private DataOutputStream mRequest;
        private ProgressDialog mProgressDialog;
        private PersonActivity mCallingActivity;
        private List<ResponseMessage> mResponseMessageList;

        private String mCrlf = "\r\n";
        private String mTwoHyphens = "--";
        private String mBoundary = "*****";

        public SetupPersonTask(PersonActivity activity){
            mCallingActivity = activity;
            initializeProgressDialog();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog.show();
        }

        @Override
        protected ResponseMessagePersonInsertOrEdit doInBackground(Void... params) {
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
                PersonInsertOrEditJsonUtils jsonUtils = new PersonInsertOrEditJsonUtils();
                List<ResponseMessage> messageList = jsonUtils.readJsonStream(responseStream);

                if (messageList.size() > 0){

                    ResponseMessagePersonInsertOrEdit respMsg = (ResponseMessagePersonInsertOrEdit)messageList.get(0);

                    if (respMsg.getStatus() == 1){
                        publishProgress("Storing person...");

                        long id = mPerson.saveToDb(db, dbHelper);

                        if (id > 0){
                            mPerson.setId(id);
                            return new ResponseMessagePersonInsertOrEdit(1, String.format("Successfully created %s %s", mPerson.getFirstName(), mPerson.getLastName()));
                        }

                        throw new SQLException(String.format("Error adding %s %s to local database.", mPerson.getFirstName(), mPerson.getLastName()));
                    }
                    else{
                        throw new IllegalArgumentException(respMsg.getResponseMsg());
                    }
                }
                else{
                    throw new Exception("Server returned empty response.");
                }
            }
            catch (ConnectException e){
                // Simulate server response
                return new ResponseMessagePersonInsertOrEdit(-1, "Server is not available.");
            }
            catch (SQLException e){
                return new ResponseMessagePersonInsertOrEdit(-1, e.getMessage());
            }
            catch (MalformedURLException e){
                return new ResponseMessagePersonInsertOrEdit(-1, "Recognize URL is invalid.");
            }
            catch (IllegalArgumentException e){
                return new ResponseMessagePersonInsertOrEdit(-1, e.getMessage());
            }
            catch (IOException e){
                return new ResponseMessagePersonInsertOrEdit(-1, String.format("%s\n%s", e.getMessage(), "Server Error."));
            }
            catch (Exception e){
                return new ResponseMessagePersonInsertOrEdit(-1, String.format("%s\n%s", e.getMessage(), "Unknown Error."));
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

            if (result.getStatus() > 0){
                Toast.makeText(mCallingActivity, String.format("Successfully created %s %s", mPerson.getFirstName(), mPerson.getLastName()), Toast.LENGTH_SHORT).show();

                // Go to MainActivity with destroying all activities between destination and this
                Intent intent = new Intent(mCallingActivity, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
            else{
                // Show alert dialog if status is not 1
                ResponseMessagePersonInsertOrEdit responseMsgObj = (ResponseMessagePersonInsertOrEdit)result;
                if (responseMsgObj.getStatus() != 1){
                    showAlertDialog(mCallingActivity, responseMsgObj.getResponseMsg(), "Error");
                }
            }
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
            mUrl = new URL(Settings.UriFactory(Settings.UriType.PEOPLE_API).toString());

            mHttpUrlConnection = (HttpURLConnection) mUrl.openConnection();
            mHttpUrlConnection.setUseCaches(false);
            mHttpUrlConnection.setDoOutput(true);
            mHttpUrlConnection.setRequestMethod("POST");
            mHttpUrlConnection.setRequestProperty("Connection", "Keep-Alive");
            mHttpUrlConnection.setRequestProperty("Cache-Control", "no-cache");
            mHttpUrlConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + mBoundary);
        }

        private void setupPostBody() throws IOException{
            byte[] imageByteArray = ImageUtils.convertBitmapToByteArray(mPhoto);

            mRequest = new DataOutputStream(mHttpUrlConnection.getOutputStream());
            // Profile picture
            mRequest.writeBytes(mTwoHyphens + mBoundary + mCrlf);
            mRequest.writeBytes("Content-Disposition: form-data; name=\"" + Person.PROFILE_PIC_SUBMIT_PARAM + "\";filename=" + mCurrentImageFile.getName() + ";" + mCrlf);
            mRequest.writeBytes("Content-Type: image/jpeg" + mCrlf);
            mRequest.writeBytes(mCrlf);
            mRequest.write(imageByteArray);
            mRequest.writeBytes(mCrlf);

            // First name
            mRequest.writeBytes(mTwoHyphens + mBoundary + mCrlf);
            mRequest.writeBytes("Content-Disposition: form-data; name=\"" + Person.FIRST_NAME_SUBMIT_PARAM + "\";" + mCrlf);
            mRequest.writeBytes(mCrlf);
            mRequest.writeBytes(mPerson.getFirstName());
            mRequest.writeBytes(mCrlf);

            // Last name
            mRequest.writeBytes(mTwoHyphens + mBoundary + mCrlf);
            mRequest.writeBytes("Content-Disposition: form-data; name=\"" + Person.LAST_NAME_SUBMIT_PARAM + "\";" + mCrlf);
            mRequest.writeBytes(mCrlf);
            mRequest.writeBytes(mPerson.getLastName());
            mRequest.writeBytes(mCrlf);

            // Age
            mRequest.writeBytes(mTwoHyphens + mBoundary + mCrlf);
            mRequest.writeBytes("Content-Disposition: form-data; name=\"" + Person.AGE_SUBMIT_PARAM + "\";" + mCrlf);
            mRequest.writeBytes(mCrlf);
            mRequest.writeBytes(String.valueOf(mPerson.getAge()));
            mRequest.writeBytes(mCrlf);

            // Email
            mRequest.writeBytes(mTwoHyphens + mBoundary + mCrlf);
            mRequest.writeBytes("Content-Disposition: form-data; name=\"" + Person.EMAIL_SUBMIT_PARAM + "\";" + mCrlf);
            mRequest.writeBytes(mCrlf);
            mRequest.writeBytes(String.valueOf(mPerson.getEmail()));
            mRequest.writeBytes(mCrlf);

            mRequest.writeBytes(mTwoHyphens + mBoundary + mTwoHyphens);
            mRequest.flush();
            mRequest.close();
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
