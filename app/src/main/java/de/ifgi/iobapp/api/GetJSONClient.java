package de.ifgi.iobapp.api;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class GetJSONClient extends AsyncTask<String, Void, String> {

    private final static String TAG = "GetJSONClient";
    GetJSONListener getJSONListener;

    public GetJSONClient(GetJSONListener listener) {
        this.getJSONListener = listener;
    }

    @Override
    protected String doInBackground(String... url) {
        return connect(url[0]);
    }

    public static String connect(String url) {
        HttpClient httpclient = new DefaultHttpClient();

        HttpResponse response;
        try {
            HttpGet httpGet = new HttpGet(url);
            response = httpclient.execute(httpGet);

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