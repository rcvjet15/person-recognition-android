package org.opencv.samples.facedetect.utilities;

import org.opencv.samples.facedetect.Person;

/**
 * Created by Robi on 09/09/2017.
 */

public class ResponseMessageRecognize extends ResponseMessage {
    private Person mPerson;
    private String mImageBase64;

    public ResponseMessageRecognize(Person person, String imageBase64, int status, String responseMsg){
        super(status, responseMsg);

        mPerson = person;
        mImageBase64 = imageBase64;
    }

    public Person getPerson(){
        return mPerson;
    }

    public String getImageBase64(){
        return mImageBase64;
    }


}