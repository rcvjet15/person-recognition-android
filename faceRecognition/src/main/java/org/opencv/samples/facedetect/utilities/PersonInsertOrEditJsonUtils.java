package org.opencv.samples.facedetect.utilities;

import android.util.JsonReader;
import android.util.JsonToken;

import org.opencv.samples.facedetect.Person;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Robi on 09/09/2017.
 */


/**
 * Created by Robi on 13/09/2017.
 */

public class PersonInsertOrEditJsonUtils extends JsonUtils {

    @Override
    public List<ResponseMessage> readJsonStream(InputStream responseStream) throws IOException {
        JsonReader jsonReader = new JsonReader(new InputStreamReader(responseStream, "UTF-8"));

        try{
            return readMessagesArray(jsonReader);
        }
        finally {
            jsonReader.close();
        }
    }

    @Override
    protected List<ResponseMessage> readMessagesArray(JsonReader reader) throws IOException{
        List<ResponseMessage> messages = new ArrayList<ResponseMessage>();

        reader.beginArray();
        while (reader.hasNext()){
            messages.add(readMessage(reader));
        }
        reader.endArray();
        return messages;
    }

    @Override
    protected ResponseMessage readMessage(JsonReader reader) throws IOException{
        int status = -100;
        Person person = null;
        String imageBase64 = null;
        String serverResponseMsg = null;

        reader.beginObject();
        while (reader.hasNext()){
            String name = reader.nextName();

            if (name.equals("status")){
                status = reader.nextInt();
            }
            else if (name.equals("message")){
                serverResponseMsg = reader.nextString();
            }
            else {
                reader.skipValue();
            }
        }

        reader.endObject();
        return new ResponseMessagePersonInsertOrEdit(status, serverResponseMsg);
    }
}
