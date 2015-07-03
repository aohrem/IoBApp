package de.ifgi.iobapp.api;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class DeleteJSONClient extends AsyncTask<String, Void, String> {

    private final static String TAG = "DeleteJSONClient";
    private GetJSONListener getJSONListener;

    public DeleteJSONClient(GetJSONListener listener) {
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
            HttpDelete httpDelete = new HttpDelete(url);

            response = httpclient.execute(httpDelete);

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