package org.opencv.samples.facedetect.utilities;

import android.util.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by Robi on 09/09/2017.
 */

public abstract class JsonUtils {

    public abstract List<ResponseMessage> readJsonStream(InputStream responseStream) throws IOException;

    protected abstract List<ResponseMessage> readMessagesArray(JsonReader reader) throws IOException;

    protected abstract ResponseMessage readMessage(JsonReader reader) throws IOException;
}

