package org.opencv.samples.facedetect;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.opencv.samples.facedetect.data.RecognitionDbHelper;

/**
 * Created by Robi on 07/09/2017.
 */

// Base Activity that implemenets global menu. All other app activities must extend from this activity
public class BaseAppActivity extends Activity {

    protected RecognitionDbHelper mDbHelper;
    SQLiteDatabase mDb;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDbHelper = new RecognitionDbHelper(this);

        // Don't show back button on action bar for MainActivity
        if (this instanceof MainActivity == false) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    protected void showAlertDialog(Context context, String msg, String title){
        AlertDialog dialog = new AlertDialog.Builder(context).create();
        if (title != null && title.length() > 0){
            dialog.setTitle(title);
        }

        dialog.setMessage(msg);
        dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        dialog.show();
    }
}
