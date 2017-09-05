package org.opencv.samples.facedetect;

import android.net.Uri;

/**
 * Created by Robi on 04/09/2017.
 */

public class Settings {
    private static Uri.Builder mUriBuilder = null;

    public static Uri.Builder getUriBuilder(){
        if (mUriBuilder  == null){
            mUriBuilder  = new Uri.Builder();
            mUriBuilder .scheme("http")
                    .encodedAuthority("192.168.1.92:5000")
                    .appendPath("recognize");
        }
        return mUriBuilder ;
    }
}
