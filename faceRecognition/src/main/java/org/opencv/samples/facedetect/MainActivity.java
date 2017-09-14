package org.opencv.samples.facedetect;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.view.View;

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

    public void addPerson(View view){
        Context context = this;
        Class destinationClass = PersonActivity.class;
        Intent intent = new Intent(context, destinationClass);
        // If update feature is added, then crate two constants that will define insert or edit code
        intent.putExtra(Intent.ACTION_INSERT_OR_EDIT, "insert");
        startActivity(intent);
    }

    public void gallery(View view){
        showAlertDialog(this, "Not Implemented!", "Error");
        return;
    }

    public void people(View view){
        Context context = this;
        Class destinationClass = PeopleActivity.class;
        Intent intent = new Intent(context, destinationClass);
        startActivity(intent);
    }

    public void aboutUs(View view){
        Context context = this;
        Class destinationClass = AboutUsActivity.class;
        Intent intent = new Intent(context, destinationClass);
        startActivity(intent);
    }
}