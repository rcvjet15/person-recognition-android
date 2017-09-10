package org.opencv.samples.facedetect.utilities;

import android.net.Uri;

/**
 * Created by Robi on 04/09/2017.
 */

public class SettingsUtils {
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
}
