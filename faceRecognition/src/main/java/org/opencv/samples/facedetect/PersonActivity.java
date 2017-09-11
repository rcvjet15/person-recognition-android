package org.opencv.samples.facedetect;

import android.animation.FloatEvaluator;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.widget.ViewSwitcher;
import org.opencv.samples.facedetect.data.PersonContract.*;

import org.opencv.samples.facedetect.data.PersonContract;

/**
 * Created by Robi on 10/09/2017.
 */

public class PersonActivity extends BaseAppActivity {

    public enum ActivityType {
        ACTION_CREATE(1),
        ACTION_SHOW(2),
        ACTION_UPDATE(3),
        ACTION_DELETE(4);

        private int mActivityTypeIndex;

        private ActivityType(int activityTypeIndex) { this.mActivityTypeIndex = activityTypeIndex; }

        // Get enum from int value
        public static ActivityType getActivityType(int activityTypeIndex){
            for (ActivityType at : ActivityType.values()){
                if (at.mActivityTypeIndex == activityTypeIndex) return at;
            }
            throw new IllegalArgumentException("Activity Type not found!");
        }
    }
    // Action to be perfomed sent in Intent
    public static final String ACTION_PERSON_DATA = "person";

    // Action type that will be sent through intent. Based on this parameter, proper view layout will be shown
    public static final String ACTION_PERSON = "person-action";

    private Person mPerson;
    private ImageView mImageView;
    private TextView mFirstName;
    private TextView mLastName;
    private TextView mAge;
    private TextView mEmail;
    private TextView mFaceEncoding;
    private ViewFlipper mActionFooterFlipper;
    private LinearLayout mShowFooter;
    private LinearLayout mInsertFooter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Bundle bundleExtras = intent.getExtras();

        if (bundleExtras != null){

            // Cannot edit person data on insert, called from RecognizeActivity
            if (bundleExtras.containsKey(Intent.ACTION_INSERT)){
                setupInsertActivity(bundleExtras);
            }
            else {

            }

            if (mPerson != null){ this.getActionBar().setTitle(String.format("%s %s", mPerson.getFirstName(), mPerson.getLastName())); }
        }
    }

    private void setupInsertActivity(Bundle extras){
        setContentView(R.layout.activity_person_show);

        mInsertFooter = (LinearLayout) findViewById(R.id.insertFooter);
        mInsertFooter.setVisibility(View.VISIBLE);

        mImageView = (ImageView) findViewById(R.id.profilePicShow);
        mFirstName = (TextView) findViewById(R.id.firstNameShow);
        mLastName = (TextView) findViewById(R.id.lastNameShow);
        mAge = (TextView) findViewById(R.id.ageShow);
        mEmail = (TextView) findViewById(R.id.emailShow);
        mFaceEncoding = (TextView) findViewById(R.id.faceEncodingShow);

        mPerson = extras.getParcelable(Intent.ACTION_INSERT);

        mImageView.setImageBitmap(mPerson.convertImageBase64ToBitmap());
        mFirstName.setText(String.format("%s %s", mFirstName.getText(), mPerson.getFirstName()));
        mLastName.setText(String.format("%s %s", mLastName.getText(), mPerson.getLastName()));
        mAge.setText(String.format("%s %d", mAge.getText(), mPerson.getAge()));
        mEmail.setText(String.format("%s %s", mEmail.getText(), mPerson.getEmail()));
        mFaceEncoding.setText(String.format("%s %s", mFaceEncoding.getText(), mPerson.getFaceEncoding()));
    }

    @Override
    public void cancel(View view){
        super.cancel(view);
    }

    public void insert(View view){
        try{
            db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(PersonEntry.COLUMN_FIRST_NAME, mPerson.getFirstName());
            values.put(PersonEntry.COLUMN_LAST_NAME, mPerson.getLastName());
            values.put(PersonEntry.COLUMN_AGE, mPerson.getAge());
            values.put(PersonEntry.COLUMN_EMAIL, mPerson.getEmail());
            values.put(PersonEntry.COLUMN_STATUS, mPerson.getStatus());
            values.put(PersonEntry.COLUMN_PROFILE_PIC, mPerson.convertImageBase64ToByteArray());
            values.put(PersonEntry.COLUMN_VALID_FROM, System.currentTimeMillis());
            long newRowId = db.insert(PersonEntry.TABLE_NAME, null, values);

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
        catch (SQLiteException e){
            showAlertDialog(this, e.getMessage(), "Database Error");
        }
    }
}
