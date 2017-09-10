package org.opencv.samples.facedetect;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

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
        }
    }

    private void setupInsertActivity(Bundle extras){
        setContentView(R.layout.activity_person_show);

        mImageView = (ImageView) findViewById(R.id.profilePicShow);
        mFirstName = (TextView) findViewById(R.id.firstNameShow);
        mLastName = (TextView) findViewById(R.id.lastNameShow);
        mAge = (TextView) findViewById(R.id.ageShow);
        mEmail = (TextView) findViewById(R.id.emailShow);
        mFaceEncoding = (TextView) findViewById(R.id.faceEncodingShow);

        mPerson = extras.getParcelable(Intent.ACTION_INSERT);

        mImageView.setImageBitmap(mPerson.ConvertImageBase64ToBitmap());
        mFirstName.setText(String.format("First name: %s", mPerson.getFirstName()));
        mLastName.setText(String.format("Last name: %s", mPerson.getLastName()));
        mAge.setText(String.format("Age: %d", mPerson.getAge()));
        mEmail.setText(String.format("Email: %s", mPerson.getEmail()));
        mFaceEncoding.setText(String.format("Face encoding: %s", mPerson.getFaceEncoding()));

        this.getActionBar().setTitle(String.format("%s %s", mPerson.getFirstName(), mPerson.getLastName()));
    }
}
