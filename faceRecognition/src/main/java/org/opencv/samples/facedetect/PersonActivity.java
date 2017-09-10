package org.opencv.samples.facedetect;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Robi on 10/09/2017.
 */

public class PersonActivity extends BaseAppActivity {
    private Person mPerson;
    private ImageView mImageView;
    private TextView mFirstName;
    private TextView mLastName;
    private TextView mAge;
    private TextView mEmail;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_show);

        mImageView = (ImageView) findViewById(R.id.profilePic);
        mFirstName = (TextView) findViewById(R.id.firstName);
        mLastName = (TextView) findViewById(R.id.firstName);
        mAge = (TextView) findViewById(R.id.firstName);
        mEmail = (TextView) findViewById(R.id.firstName);
    }
}
