package org.opencv.samples.facedetect;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class PeopleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
    }
/*  Kada bude potrebno uzeti podatke iz text polja

    EditText name = (EditText) findViewById(R.id.person_name);
    String personName = name.getText().toString();

    EditText surname = (EditText) findViewById(R.id.person_surname);
    String personSurname = surname.getText().toString();

    EditText email = (EditText) findViewById(R.id.person_email);
    String personEmail = email.getText().toString(); */
}
