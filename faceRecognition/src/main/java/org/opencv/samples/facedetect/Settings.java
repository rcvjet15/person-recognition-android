package org.opencv.samples.facedetect;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import org.opencv.samples.facedetect.data.RecognitionDbHelper;
import org.opencv.samples.facedetect.data.SettingContract.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Robi on 04/09/2017.
 */

public class Settings {
    public static final String KEY_SCHEME = "scheme";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_PORT = "port";
    public static final String KEY_REST_API_SUB_PATH = "rest_api_sub_path";
    public static final String KEY_RECOGNIZE_PATH = "recognize_path";
    public static final String KEY_PEOPLE_PATH = "people_path";
    public static final String KEY_CREATE_PATH = "create_path";

    public enum UriType { SERVER_ADDR, CREATE, RECOGNIZE, PEOPLE}

    private static String sScheme = "http";
    private static String sAddress = "robi-web.ddns.net";
    private static long sPort = 4000;
    private static String sApiSubPath = "api";
    private static String sPeoplePath = "people";
    private static String sRecognizePath = "recognize";
    private static String sPersonPath = "person";
    private static String sCreatePath = "create";

    public static String getScheme(){
        return sScheme;
    }

    public static void setScheme(String scheme){
        sScheme = scheme;
    }

    public static String getAddress(){
        return sAddress;
    }

    public static void setAddress(String address){
        sAddress = address;
    }

    public static Long getPort(){
        return sPort;
    }

    public static void setPort(Long port){
        sPort = port;
    }

    public static String getRestApiSubPath(){
        return sApiSubPath;
    }

    public static void setRestApiSubPath(String apiSubPath){
        sApiSubPath = apiSubPath;
    }

    public static String getRecognizePath(){
        return sRecognizePath;
    }

    public static void setRecognizePath(String recognizePath){
        sRecognizePath = recognizePath;
    }

    public static String getCreatePath(){
        return sCreatePath;
    }

    public static void setCreatePath(String createPath){
        sCreatePath = createPath;
    }

    public static Uri UriFactory(UriType uriType){
        Uri.Builder uriBuilder = new Uri.Builder()
                .scheme(sScheme)
                .encodedAuthority(String.format("%s:%d", sAddress, sPort))
                .appendPath(sApiSubPath);

        switch (uriType){
            case SERVER_ADDR: {
                break;
            }
            case PEOPLE: {
                uriBuilder.appendPath(sPeoplePath);
                break;
            }
            case RECOGNIZE: {
                uriBuilder.appendPath(sRecognizePath);
                break;
            }
            default: {
                break;
            }
        }

        return uriBuilder.build();
    }

    /**
     * Method that is called after db migration to insert default settings data
     */
    public static void saveSettings(SQLiteDatabase db, RecognitionDbHelper dbHelper){

        Map<String, String> settingsMap = new HashMap<String, String>();
        settingsMap.put(Settings.KEY_SCHEME, sScheme);
        settingsMap.put(Settings.KEY_ADDRESS, sAddress);
        settingsMap.put(Settings.KEY_PORT, String.valueOf(sPort));
        settingsMap.put(Settings.KEY_REST_API_SUB_PATH, sApiSubPath);
        settingsMap.put(Settings.KEY_RECOGNIZE_PATH, sRecognizePath);
        settingsMap.put(Settings.KEY_PEOPLE_PATH, sPeoplePath);

        db = dbHelper.getWritableDatabase();
        db.beginTransaction();

        // Second argument get number of deleted rows
        long rows = db.delete(SettingEntry.TABLE_NAME, "1", null);

        // Create iterator
        Iterator<Map.Entry<String, String>> iterator = settingsMap.entrySet().iterator();
        String key, value;
        long id;
        try{
            // Loop through each key, value pair and create insert clause
            while (iterator.hasNext()){
                Map.Entry<String, String> setting = iterator.next();
                key = setting.getKey();
                value = setting.getValue();
                ContentValues contentSettings = new ContentValues();
                contentSettings.put(SettingEntry.COLUMN_KEY, key);
                contentSettings.put(SettingEntry.COLUMN_VALUE, value);
                id = db.insert(SettingEntry.TABLE_NAME, null, contentSettings);
            }

            db.setTransactionSuccessful();
        }
        catch (Exception e){

        }
        finally {
            db.endTransaction();
            db.close();
        }
    }

    /**
     * Update settings from db
     */
    public static void fetchSettingsFromDb(SQLiteDatabase db, RecognitionDbHelper dbHelper){

        db = dbHelper.getReadableDatabase();

        // Define projection that specifies which columns will be selected
        String[] projection = {
                SettingEntry.COLUMN_KEY,
                SettingEntry.COLUMN_VALUE
        };

        Cursor cursor = db.query(
                SettingEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        try{
            String key, value;
            while (cursor.moveToNext()){
                key = cursor.getString(
                        cursor.getColumnIndexOrThrow(SettingEntry.COLUMN_KEY)
                );

                value = cursor.getString(
                        cursor.getColumnIndexOrThrow(SettingEntry.COLUMN_VALUE)
                );

                switch (key){
                    case Settings.KEY_SCHEME:
                        Settings.setScheme(value);
                        break;
                    case Settings.KEY_ADDRESS:
                        Settings.setAddress(value);
                        break;
                    case Settings.KEY_PORT:
                        Settings.setPort(Long.parseLong(value));
                        break;
                    case Settings.KEY_REST_API_SUB_PATH:
                        Settings.setRestApiSubPath(value);
                        break;
                    case Settings.KEY_RECOGNIZE_PATH:
                        Settings.setRecognizePath(value);
                        break;
                }
            }

        }
        catch (IllegalArgumentException e){
            Log.d("FETCH SETTINGS", e.getMessage());
        }
        finally {
            cursor.close();
        }
    }
}
