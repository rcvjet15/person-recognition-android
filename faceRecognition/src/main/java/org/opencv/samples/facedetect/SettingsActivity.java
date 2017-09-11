package org.opencv.samples.facedetect;

import android.app.Activity;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.io.InvalidObjectException;
import java.util.Set;

/**
 * Created by Robi on 04/09/2017.
 */

public class SettingsActivity extends BaseAppActivity {

    private Settings mSettings;
    private Spinner mScheme;
    private EditText mAddress;
    private EditText mPort;
    private EditText mRestApiSubPath;
    private EditText mRecognizePath;
    private EditText mCreatePath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Scheme spinner setup
        mScheme = (Spinner) findViewById(R.id.schemeSpinner);
        // Array adapter that will be filled with string array and a scheme spinner
        ArrayAdapter<CharSequence> schemeAdapter = ArrayAdapter.createFromResource(this,
                R.array.scheme_array, android.R.layout.simple_spinner_dropdown_item);
        // Layout that will be used when the list of choice appears
        schemeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mScheme.setAdapter(schemeAdapter);

        mAddress = (EditText) findViewById(R.id.addressInput);
        mPort = (EditText) findViewById(R.id.portInput);
        mRestApiSubPath = (EditText) findViewById(R.id.restApiSubPathInput);
        mRecognizePath = (EditText) findViewById(R.id.recognizeInput);
        mCreatePath = (EditText) findViewById(R.id.createInput);

        // Fetch settings from db
        Settings.fetchSettingsFromDb(db, dbHelper);

        /*
         * Update activity edit texts from Settings properties
         */
        // Select proper item based on value
        int spinnerPosition = schemeAdapter.getPosition(Settings.getScheme());
        mScheme.setSelection(spinnerPosition);
        mAddress.setText(Settings.getAddress());
        mPort.setText(Settings.getPort().toString());
        mRestApiSubPath.setText(Settings.getRestApiSubPath());
        mRecognizePath.setText(Settings.getRecognizePath());
        mCreatePath.setText(Settings.getCreatePath());

    }

    @Override
    public void cancel(View view){
        super.cancel(view);
    }

    public void save(View view){
        if (!validateData()) return;

        Settings.setScheme(mScheme.getSelectedItem().toString());
        Settings.setAddress(mAddress.getText().toString());
        Settings.setPort(Long.parseLong(mPort.getText().toString()));
        Settings.setRestApiSubPath(mRestApiSubPath.getText().toString());
        Settings.setRecognizePath(mRecognizePath.getText().toString());
        Settings.setCreatePath(mCreatePath.getText().toString());

        // Save settings into database
        Settings.saveSettings(db, dbHelper);

        Toast.makeText(this, "Settings successfully saved!", Toast.LENGTH_SHORT).show();
        finish();
    }

    private boolean validateData() {
        boolean valid = true;
        String errorMsg = "";
        if (mAddress.getText().length() == 0){
            errorMsg = String.format("%s cannot be empty.", this.getString(R.string.address_label));
            valid = false;
        }
        else if (mPort.getText().length() == 0){
            errorMsg = String.format("%s cannot be empty.", this.getString(R.string.port_label));
            valid = false;
        }
        else if (mRestApiSubPath.getText().length() == 0){
            errorMsg = String.format("%s cannot be empty.", this.getString(R.string.rest_api_sub_path_label));
            valid = false;
        }
        else if (mRecognizePath.getText().length() == 0){
            errorMsg = String.format("%s cannot be empty.", this.getString(R.string.recognize_path_label));
            valid = false;
        }
        else if (mCreatePath.getText().length() == 0){
            errorMsg = String.format("%s cannot be empty.", this.getString(R.string.create_path_label));
            valid = false;
        }

        if (!valid) { showAlertDialog(this, errorMsg, "Settings Error"); }

        return valid;
    }
}
