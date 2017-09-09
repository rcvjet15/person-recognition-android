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

    public abstract List<IReponseMessage> readJsonStream(InputStream responseStream) throws IOException;

    public abstract List<IReponseMessage> readMessagesArray(JsonReader reader) throws IOException;

    public abstract IReponseMessage readMessage(JsonReader reader) throws IOException;
}


// Class for parsing JSON received from server in CaptureImageActivity
public class RecognizeJsonUtils extends JsonUtils{

    @Override
    public List<IResponseMessage> readJsonStream(InputStream responseStream) throws IOException{
        JsonReader jsonReader = new JsonReader(new InputStreamReader(responseStream, "UTF-8"));

        try{
            return readMessagesArray(jsonReader);
        }
        finally {
            jsonReader.close();
        }
    }

    @Override
    public List<IResponseMessage> readMessagesArray(JsonReader reader) throws IOException{
        List<IResponseMessage> messages = new ArrayList<IResponseMessage>();

        reader.beginArray();
        while (reader.hasNext()){
            messages.add(readMessage(reader));
        }
        reader.endArray();
        return messages;
    }

    public IResponseMessage readMessage(JsonReader reader) throws IOException{
        int status = -100;
        Person person = null;
        String imageBase64 = null;

        reader.beginObject();
        while (reader.hasNext()){
            String name = reader.nextName();

            if (name.equals("person")){
                person = Person.createFromJson(reader);
            }
            else if (name.equals("profile-pic")){
                imageBase64 = reader.nextString();
            }
            else if (name.equals("status")){
                status = reader.nextInt();
            }
            else {
                reader.skipValue();
            }

            reader.endObject();
            return new ResponseMessageRecognize(person, imageBase64, status);
        }
    }



}
