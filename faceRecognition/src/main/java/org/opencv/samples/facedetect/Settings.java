package org.opencv.samples.facedetect;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.widget.ContentFrameLayout;

/**
 * Created by Robi on 04/09/2017.
 */

public class Settings {
    public static final String KEY_SCHEME = "scheme";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_PORT = "port";
    public static final String KEY_REST_API_SUB_PATH = "rest_api_sub_path";
    public static final String KEY_RECOGNIZE_PATH = "recognize_path";
    public static final String KEY_CREATE_PATH = "create_path";

    public enum UriType { SERVER_ADDR, CREATE, RECOGNIZE, PEOPLE_API }

    private static String sScheme = "http";
    private static String sAddress = "192.168.1.30";
    private static int sPort = 4000;
    private static String sRestApiSubPath = "people-api";
    private static String sRecognizePath = "recognize";
    private static String sCreatePath = "create";

    public static Uri UriFactory(UriType uriType){
        Uri.Builder uriBuilder = new Uri.Builder()
                .scheme(sScheme)
                .encodedAuthority(String.format("%s:%d", sAddress, sPort));

        switch (uriType){
            case SERVER_ADDR: {
                break;
            }
            case PEOPLE_API: {
                uriBuilder.appendPath(sRestApiSubPath);
                break;
            }
            case CREATE: {
                uriBuilder.appendPath(sRestApiSubPath)
                        .appendPath(sCreatePath);
                break;
            }
            case RECOGNIZE: {
                uriBuilder.appendPath(sRestApiSubPath)
                        .appendPath(sRecognizePath);
                break;
            }
            default: {
                break;
            }
        }

        return uriBuilder.build();
    }

    /**
     * Update settings from db
     */
    public static void updateSettingsFromDb(){

    }
}
