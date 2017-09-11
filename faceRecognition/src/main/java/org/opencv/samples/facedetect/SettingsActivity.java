package org.opencv.samples.facedetect;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewFlipper;

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
    }

    @Override
    public void cancel(View view){
        super.cancel(view);
    }

    public void save(View view){
        Settings.setScheme(mScheme.getSelectedItem().toString());
        Settings.setAddress(mAddress.getText().toString());
        Settings.setPort(Long.parseLong(mPort.getText().toString()));
        Settings.setRestApiSubPath(mRestApiSubPath.getText().toString());
        Settings.setRecognizePath(mRecognizePath.getText().toString());
        Settings.setCreatePath(mCreatePath.getText().toString());

        // Save settings into database
        Settings.saveSettings(db, dbHelper);
    }

}
