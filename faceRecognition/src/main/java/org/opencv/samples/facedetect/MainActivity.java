package org.opencv.samples.facedetect;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void faceDetect(View view){
        Context context = this;
        Class destinationClass = FdActivity.class;
        Intent intent = new Intent(context, destinationClass);
        startActivity(intent);
    }

    public void addUser(View view){
        Context context = this;
        Class destinationClass = UsersActivity.class;
        Intent intent = new Intent(context, destinationClass);
        startActivity(intent);
    }

    public void aboutUs(View view){
        Context context = this;
        Class destinationClass = AboutUsActivity.class;
        Intent intent = new Intent(context, destinationClass);
        startActivity(intent);
    }

    public void serverPart(View view){

//        Intent intent = new Intent(this, serverPart.class);
//        startActivity(intent);
    }
}


