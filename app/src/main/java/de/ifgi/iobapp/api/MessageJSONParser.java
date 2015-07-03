package de.ifgi.iobapp.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.ifgi.iobapp.model.Message;

public class MessageJSONParser {
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    public MessageJSONParser() {

    }

    public ArrayList<Message> parseMessages(JSONArray jsonResult) throws JSONException, ParseException {
        ArrayList<Message> messages = new ArrayList<Message>();

        for (int i = 0; i < jsonResult.length(); i++) {
            JSONObject jsonObject = jsonResult.getJSONObject(i);

            int id = jsonObject.getInt("message_id");
            String deviceId = jsonObject.getString("device_id");
            String latString = jsonObject.getString("lat");
            String lonString = jsonObject.getString("lon");
            String timestampString = jsonObject.getString("time");

            if (latString.equals("null")
                    || lonString.equals("null")
                    || timestampString.equals("null")) {
                continue;
            }

            double lat = jsonObject.getDouble("lat");
            double lon = jsonObject.getDouble("lon");

            DateFormat format = new SimpleDateFormat(DATE_FORMAT);
            Date timestamp = format.parse(timestampString);

            Message message = new Message(id, deviceId, lat, lon, timestamp);
            messages.add(message);
        }

        return messages;
    }
}
