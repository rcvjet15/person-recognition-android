package org.opencv.samples.facedetect.utilities;

import android.os.Message;
import android.util.JsonReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Robi on 09/09/2017.
 */

public class JsonUtils {
    // Class for parsing JSON received from server in CaptureImageActivity
    public class CaptureImageJsonParser{

        public List<Message> readJsonStream(InputStream responseStream) throws IOException{
            JsonReader jsonReader = new JsonReader(new InputStreamReader(responseStream, "UTF-8"));

            try{
                return readMessagesArray(jsonReader);
            }
            finally {
                jsonReader.close();
            }
        }

        public List<Message> readMessagesArray(JsonReader reader) throws IOException{
            List<Message> messages = new ArrayList<Message>();

            reader.beginArray();
            while (reader.hasNext()){
                messages.add(readMessage(reader));
            }
            reader.endArray();
            return messages;
        }

        public Message readMessage(JsonReader reader) throws IOException{
            int status = -100;

        }



    }
}
