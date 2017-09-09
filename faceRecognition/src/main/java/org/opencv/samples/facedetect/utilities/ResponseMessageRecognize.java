package org.opencv.samples.facedetect.utilities;

import org.opencv.samples.facedetect.Person;

/**
 * Created by Robi on 09/09/2017.
 */

public class ResponseMessageRecognize implements IResponseMessage{
    private Person mPerson;
    private String mImageBase64;
    private int mStatus;
    private String mResponseMsg;


    public ResponseMessageRecognize(Person person, String imageBase64, int status, String responseMsg){
        mPerson = person;
        mImageBase64 = imageBase64;
        mStatus = status;
        mResponseMsg = responseMsg;
    }

    public Person getPerson(){
        return mPerson;
    }

    public String getImageBase64(){
        return mImageBase64;
    }

    public int getStatus(){
        return mStatus;
    }

    public String getResponseMsg(){
        return mResponseMsg;
    }
}