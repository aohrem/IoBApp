package de.ifgi.iobapp.api;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class PutJSONClient extends AsyncTask<String, Void, String> {

    private final static String TAG = "PutJSONClient";
    private GetJSONListener getJSONListener;
    private static ArrayList<NameValuePair> nameValuePairs;

    public PutJSONClient(GetJSONListener listener, ArrayList<NameValuePair> nameValuePairs) {
        this.getJSONListener = listener;
        this.nameValuePairs = nameValuePairs;
    }

    @Override
    protected String doInBackground(String... url) {
        return connect(url[0]);
    }

    public static String connect(String url) {
        HttpClient httpclient = new DefaultHttpClient();

        HttpResponse response;
        try {
            HttpPut httpPut = new HttpPut(url);
            UrlEncodedFormEntity encodedFormEntity = new UrlEncodedFormEntity(nameValuePairs);
            httpPut.setEntity(encodedFormEntity);

            response = httpclient.execute(httpPut);

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            String json = "", thisLine;

            while ((thisLine = reader.readLine()) != null) {
                json += thisLine;
            }

            return json;

        } catch (ClientProtocolException e) {
            Log.e(TAG, e.getMessage() + "");
        } catch (IOException e) {
            Log.e(TAG, e.getMessage() + "");
        }

        return null;
    }

    @Override
    protected void onPostExecute(String json ) {
        getJSONListener.onRemoteCallComplete(json);
    }

    public interface GetJSONListener {
        public void onRemoteCallComplete(String jsonFromNet);
    }

}