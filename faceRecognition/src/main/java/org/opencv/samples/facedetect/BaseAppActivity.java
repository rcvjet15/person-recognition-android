package org.opencv.samples.facedetect;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.opencv.samples.facedetect.data.RecognitionDbHelper;

/**
 * Created by Robi on 07/09/2017.
 */

// Base Activity that implemenets global menu. All other app activities must extend from this activity
public abstract class BaseAppActivity extends Activity {

    protected static boolean firstTime = true;

    protected RecognitionDbHelper dbHelper;
    protected SQLiteDatabase db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        doAtStartUp();

        // Don't show back button on action bar for MainActivity
        if (this instanceof MainActivity == false) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (this instanceof SettingsActivity == false){
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.main, menu);
            return super.onCreateOptionsMenu(menu);
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
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

    protected void showStayOrLeavePromptDialog(Context context, String msg, String title){
        AlertDialog dialog = new AlertDialog.Builder(context).create();
        if (title != null && title.length() > 0){
            dialog.setTitle(title);
        }

        dialog.setMessage(msg);
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });

        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });


        dialog.show();
    }

    /**
     * Cancel method that will be called when cancel button is clicked.
     * @param view View parameter that clicked button sends.
     */
    protected void cancel(View view){
        showStayOrLeavePromptDialog(this, "Do you want to leave?", "Changes not saved");
    }


    /**
     * Method that will be called only when app is entered.
     */
    public void doAtStartUp(){
        if (firstTime){
            dbHelper = new RecognitionDbHelper(this);
            Settings.fetchSettingsFromDb(db, dbHelper);
            firstTime = false;
        }
    }
}
