package org.opencv.samples.facedetect.utilities;

import android.app.Notification;
import android.support.v4.app.NotificationCompat;
import android.util.JsonReader;

import org.opencv.samples.facedetect.Person;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Robi on 09/09/2017.
 */

public abstract class JsonUtils {

    public abstract List<IResponseMessage> readJsonStream(InputStream responseStream) throws IOException;

    protected abstract List<IResponseMessage> readMessagesArray(JsonReader reader) throws IOException;

    protected abstract IResponseMessage readMessage(JsonReader reader) throws IOException;
}

