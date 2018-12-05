package project.util;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.springframework.stereotype.Service;
import project.model.PatientPayload;

import java.io.StringReader;

@Service
public class JsonConverterHelper {
    public <T extends PatientPayload> T parseJsonData(String data, Class<T> type){
        Gson gson = new Gson();
        JsonReader reader = new JsonReader(new StringReader(data));
        reader.setLenient(true);
        return gson.fromJson(reader, type);
    }
}
