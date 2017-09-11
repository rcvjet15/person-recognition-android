package org.opencv.samples.facedetect.data;

import android.provider.BaseColumns;

/**
 * Created by Robi on 11/09/2017.
 */

public final class SettingContract {

    private SettingContract() {}

    public class SettingEntry implements BaseColumns{
        public static final String TABLE_NAME = "setting";
        public static final String COLUMN_KEY = "key";
        public static final String COLUMN_VALUE = "value";
        public static final String COLUMN_DESCRIPTION = "display_value";
    }
}
