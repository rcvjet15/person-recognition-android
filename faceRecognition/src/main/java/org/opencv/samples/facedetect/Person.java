package org.opencv.samples.facedetect;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Settings;
import android.util.Base64;
import android.util.JsonReader;
import android.util.Log;
import android.widget.Toast;

import org.opencv.samples.facedetect.data.PersonContract;
import org.opencv.samples.facedetect.data.RecognitionDbHelper;

import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * Created by Robi on 09/09/2017.
 */

// Implements Parcelable interface that allows to pass Person object in Intent
public class Person implements Parcelable {

    public static Person createFromJson(JsonReader jsonReader) throws IOException{
        Person person = new Person();

        jsonReader.beginObject();
        while (jsonReader.hasNext()){
            String name = jsonReader.nextName();

            if (name.equals("first_name")){
                person.setFirstName(jsonReader.nextString());
            }
            else if (name.equals("last_name")){
                person.setLastName(jsonReader.nextString());
            }
            else if (name.equals("age")){
                person.setAge(jsonReader.nextInt());
            }
            else if (name.equals("email")){
                person.setEmail(jsonReader.nextString());
            }
            else if (name.equals("face_encoding")){
                person.setFaceEncoding(jsonReader.nextString());
            }
            else if (name.equals("status")){
                person.setStatus(jsonReader.nextInt());
            }
            else{
                jsonReader.skipValue();
            }
        }

        jsonReader.endObject();
        return person;
    }

    public Bitmap convertImageBase64ToBitmap(){
        if (mImageBase64 != null && mImageBase64.length() > 0){
            byte[] decodedString = Base64.decode(mImageBase64, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        }
        return null;
    }

    public byte[] convertImageBase64ToByteArray(){
        if (mImageBase64 != null && mImageBase64.length() > 0){
            return Base64.decode(mImageBase64, Base64.DEFAULT);
        }
        return null;
    }

    private String mFirstName;
    private String mLastName;
    private int mAge;
    private String mEmail;
    private String mFaceEncoding;
    private String mImageBase64;
    private int mStatus;
    private Timestamp mValidFrom;
    private Timestamp mValidTo;

    public String getFirstName(){
        return mFirstName;
    }

    public void setFirstName(String firstName){
        mFirstName = firstName;
    }

    public String getLastName(){
        return mLastName;
    }

    public void setLastName(String lastName){
        mLastName = lastName;
    }

    public int getAge(){
        return mAge;
    }

    public void setAge(int age){
        mAge = age;
    }

    public String getEmail(){
        return mEmail;
    }

    public void setEmail(String email){
        mEmail = email;
    }

    public String getFaceEncoding(){
        return mFaceEncoding;
    }

    public void setFaceEncoding(String faceEncoding){
        mFaceEncoding = faceEncoding;
    }

    public String getImageBase64(){
        return mImageBase64;
    }

    public void setImageBase64(String imageBase64){
        mImageBase64 = imageBase64;
    }

    public int getStatus(){
        return mStatus;
    }

    public void setStatus(int status){
        mStatus = status;
    }

    public Timestamp getValidFrom(){
        return mValidFrom;
    }

    public void setValidFrom(Timestamp validFrom){
        mValidFrom = validFrom;
    }

    public Timestamp getValidTo(){
        return mValidTo;
    }

    public void setValidTo(Timestamp validTo){
        mValidTo = validTo;
    }

    public Person(){
        mValidFrom = new Timestamp(System.currentTimeMillis());
    }


    // Parceable interface implemented methods
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mFirstName);
        dest.writeString(mLastName);
        dest.writeInt(mAge);
        dest.writeString(mEmail);
        dest.writeString(mFaceEncoding);
        dest.writeString(mImageBase64);
        dest.writeInt(mStatus);
    }

    public static final Parcelable.Creator<Person> CREATOR =
            new Parcelable.Creator<Person>() {
                public Person createFromParcel(Parcel in){
                    return new Person(in);
                }

                @Override
                public Person[] newArray(int size) {
                    return new Person[size];
                }
            };

    public Person(Parcel in){
        // Must be in same order as in writeToParcel
        mFirstName = in.readString();
        mLastName = in.readString();
        mAge = in.readInt();
        mEmail = in.readString();
        mFaceEncoding = in.readString();
        mImageBase64 = in.readString();
        mStatus = in.readInt();
    }

    public long saveToDb(SQLiteDatabase db, RecognitionDbHelper dbHelper) throws SQLiteException {

        db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PersonContract.PersonEntry.COLUMN_FIRST_NAME, mFirstName);
        values.put(PersonContract.PersonEntry.COLUMN_LAST_NAME, mLastName);
        values.put(PersonContract.PersonEntry.COLUMN_AGE, mAge);
        values.put(PersonContract.PersonEntry.COLUMN_EMAIL, mEmail);
        values.put(PersonContract.PersonEntry.COLUMN_STATUS, mStatus);
        values.put(PersonContract.PersonEntry.COLUMN_PROFILE_PIC, convertImageBase64ToByteArray());
        values.put(PersonContract.PersonEntry.COLUMN_VALID_FROM, System.currentTimeMillis());
        return  db.insert(PersonContract.PersonEntry.TABLE_NAME, null, values);
    }
}
