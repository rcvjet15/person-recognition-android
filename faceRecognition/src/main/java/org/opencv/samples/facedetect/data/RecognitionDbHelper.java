package org.opencv.samples.facedetect.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;
import org.opencv.samples.facedetect.Settings;
import org.opencv.samples.facedetect.data.PersonContract.*;
import org.opencv.samples.facedetect.data.SettingContract.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.StreamHandler;

/**
 * Created by Robi on 10/09/2017.
 */

public class RecognitionDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "recognition.db";

    // Increment on each database migration
    private static final int DATABASE_VERSION = 2;

    public RecognitionDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when database is created for the first time. This is where the creation of tables
     * and the initial population of the tables should happen.
     * @param db The database
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

         /*
         * SQL statement for creating person table
         */
        final String SQL_CREATE_PERSON_TABLE =
                "CREATE TABLE " + PersonEntry.TABLE_NAME + " (" +
                        PersonEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        PersonEntry.COLUMN_FIRST_NAME + " TEXT NOT NULL, " +
                        PersonEntry.COLUMN_LAST_NAME + " TEXT NOT NULL, " +
                        PersonEntry.COLUMN_AGE + " INTEGER, " +
                        PersonEntry.COLUMN_EMAIL + " TEXT NOT NULL, " +
                        PersonEntry.COLUMN_PROFILE_PIC + " BLOB NOT NULL, " +
                        PersonEntry.COLUMN_STATUS + " INTEGER, " +

                        // Store datetime as long value. To retrieve it successfully use cursor.getLong() because of int value overflow.
                        PersonEntry.COLUMN_VALID_FROM + " INT NOT NULL)";


        db.execSQL(SQL_CREATE_PERSON_TABLE);

        /*
         * SQL statement for creating setting table
         */
        final String SQL_CREATE_SETTING_TABLE =
                "CREATE TABLE " + SettingEntry.TABLE_NAME + " (" +
                        SettingEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        SettingEntry.COLUMN_KEY + " TEXT NOT NULL, " +
                        SettingEntry.COLUMN_VALUE + " TEXT NOT NULL, " +
                        SettingEntry.COLUMN_DESCRIPTION + " TEXT)";

        db.execSQL(SQL_CREATE_SETTING_TABLE);


    }

    /**
     * This method runs only if database version incremented.
     * It discards all data
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + PersonEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SettingEntry.TABLE_NAME);
        onCreate(db);
    }
}
