package org.acme.utility;

import jakarta.json.*;
import org.acme.records.ServiceResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Util {

    public static JsonObject buildResponse (ServiceResponse responder) {
        Object message = responder.message();
        JsonObjectBuilder builder = Json.createObjectBuilder();

        if (message instanceof String) {
            builder.add("message", (String) message);
        } else if (message instanceof JsonStructure) {
            builder.add("message", (JsonStructure) message);
        } else if (message instanceof List<?>) {
            JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
            for (Object obj : (List<?>) message) {
                if (obj instanceof JsonStructure) {
                    arrayBuilder.add((JsonStructure) obj);
                } else {
                    arrayBuilder.add(obj.toString());
                }
            }
            builder.add("message", arrayBuilder.build());
        } else if (message instanceof Integer) {
            builder.add("message", (int) message);
        } else {
            builder.add("message", "Unsupported message type");
        }
        String status = responder.isSuccess() ? "success" : "error";
        return builder
                .add("status code", responder.statusCode())
                .add("status", status)
                .build();
    }
    public static JsonObject convertInputStreamToJson(InputStream inputStream)
    {
        try {
            String inputString = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            JsonReader jsonReader = Json.createReader(new StringReader(inputString));
            JsonObject object = jsonReader.readObject();
            jsonReader.close();
            return object;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static JsonObject convertObjectToJson(Object request){
        return Json.createReader(new StringReader(request.toString())).readObject();
    }

    public static String getTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
    }
    public static JsonArray convertListToJsonArray(List<String> roles) {
        JsonArrayBuilder builder = Json.createArrayBuilder();
        for (String role : roles) {
            builder.add(role);
        }
        return builder.build();
    }
}
