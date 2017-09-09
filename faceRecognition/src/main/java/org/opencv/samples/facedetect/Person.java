package org.opencv.samples.facedetect;

import android.provider.Settings;
import android.util.JsonReader;

import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * Created by Robi on 09/09/2017.
 */

public class Person {

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
        }

        jsonReader.endObject();
        return person;
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
        mImageBase64 = mImageBase64;
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
}
