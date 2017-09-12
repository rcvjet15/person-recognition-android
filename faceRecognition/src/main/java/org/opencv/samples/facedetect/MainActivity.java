package org.opencv.samples.facedetect;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import java.text.SimpleDateFormat;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v4.content.SharedPreferencesCompat;
import android.support.v4.graphics.BitmapCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.opencv.samples.facedetect.utilities.RecognizeJsonUtils;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class MainActivity extends BaseAppActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void faceRecognition(View view){
        Context context = this;
        Class destinationClass = RecognizeActivity.class;
        Intent intent = new Intent(context, destinationClass);
        startActivity(intent);
    }

    public void addUser(View view){
        showAlertDialog(this, "Not Implemented!", "Error");
        return;
    }

    public void gallery(View view){
        showAlertDialog(this, "Not Implemented!", "Error");
        return;
    }

    public void people(View view){
        showAlertDialog(this, "Not Implemented!", "Error");
        return;
    }

    public void aboutUs(View view){
        Context context = this;
        Class destinationClass = AboutUsActivity.class;
        Intent intent = new Intent(context, destinationClass);
        startActivity(intent);
    }
}