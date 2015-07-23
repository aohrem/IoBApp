package de.ifgi.iobapp.api;


import android.app.Activity;
import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;

import de.ifgi.iobapp.model.Message;
import de.ifgi.iobapp.model.MessageComparator;
import de.ifgi.iobapp.model.Notification;
import de.ifgi.iobapp.persistance.InternalStorage;
import de.ifgi.iobapp.persistance.NotificationManager;

public class MessageGetListener implements GetJSONClient.GetJSONListener {
    private static final String TAG = "MessageGetListener";

    private final Context mContext;

    public MessageGetListener(Context context) {
        mContext = context;
    }

    @Override
    public void onRemoteCallComplete(String jsonString) {
        if (jsonString != null) {
            JSONArray jsonResult = null;
            try {
                jsonResult = (JSONArray) new JSONTokener(jsonString).nextValue();
                Log.d(TAG, jsonResult.toString());

                if (jsonResult != null) {
                    MessageJSONParser messageJsonParser = new MessageJSONParser();
                    ArrayList<Message> messages = messageJsonParser.parseMessages(jsonResult);
                    Log.d(TAG, messages.toString());

                    int messageId = getLastMessageId(messages);
                    ArrayList<Notification> notifications = new ArrayList<Notification>();
                    try {
                        notifications = (ArrayList<Notification>) InternalStorage.readObject(
                                mContext, NotificationManager.INTERNAL_STORAGE_KEY);

                        for (Notification notification : notifications) {
                            IoBAPI ioBAPI = new IoBAPI(new NotificationGetListener(notification,
                                    mContext));
                            ioBAPI.getMessageInGeofence(messageId, notification.getGeofenceId());
                        }

                    } catch (IOException e) {
                        Log.e(TAG, "" + e.getMessage());
                    } catch (ClassNotFoundException e) {
                        Log.e(TAG, "" + e.getMessage());
                    }
                }

            } catch (JSONException e) {
                Log.e(TAG, e.getMessage() + "");
            } catch (ParseException e) {
                Log.e(TAG, e.getMessage() + "");
            } catch (ClassCastException e) {
                Log.e(TAG, e.getMessage() + "");
            }
        }
    }

    private int getLastMessageId(ArrayList<Message> messages) {
        if (messages.size() == 0) {
            return 0;
        }
        else {
            Collections.sort(messages, new MessageComparator());
            int lastIndex = messages.size() - 1;
            Message lastMessage = messages.get(lastIndex);
            return lastMessage.getId();
        }
    }
}
