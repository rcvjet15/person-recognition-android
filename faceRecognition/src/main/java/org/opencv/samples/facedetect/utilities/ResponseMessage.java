package org.opencv.samples.facedetect.utilities;

/**
 * Created by Robi on 09/09/2017.
 */

public abstract class ResponseMessage {
    private int mStatus;
    private String mResponseMsg;

    protected ResponseMessage(int status, String responseMsg){
        mStatus = status;
        mResponseMsg = responseMsg;
    }

    public int getStatus(){
        return mStatus;
    }

    public String getResponseMsg(){
        return mResponseMsg;
    }
}
