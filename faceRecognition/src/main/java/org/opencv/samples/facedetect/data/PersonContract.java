package org.opencv.samples.facedetect.data;

import android.provider.BaseColumns;

/**
 * Created by Robi on 10/09/2017.
 */

public final class PersonContract {
    // Prevent accidental instantiation
    private PersonContract() {}

    // todo: Add face necoding. After person is created, get fom server face encoding and store it
    public static class PersonEntry implements BaseColumns {
        public static final String TABLE_NAME = "person";
        public static final String COLUMN_FIRST_NAME = "first_name";
        public static final String COLUMN_LAST_NAME = "last_name";
        public static final String COLUMN_AGE = "age";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_PROFILE_PIC = "profile_pic";
        public static final String COLUMN_STATUS = "status";
        public static final String COLUMN_VALID_FROM = "valid_from";
    }
}
