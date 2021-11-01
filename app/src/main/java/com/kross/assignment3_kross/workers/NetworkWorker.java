package com.kross.assignment3_kross.workers;

import android.net.Uri;
import android.util.Log;

import com.kross.assignment3_kross.MainActivity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkWorker implements Runnable {
    private String strURL;
    private CompletionHandler callback;

    public NetworkWorker(String strURL, CompletionHandler callback) {
        this.strURL = strURL;
        this.callback = callback;
    }

    @Override
    public void run() {
        Uri dataUri = Uri.parse(strURL);
        String urlToUse = dataUri.toString();

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.d("NetworkWorker", "--Invalid response: " + conn.getResponseCode());
                callback.getResult("!");
                return;
            }

            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line = "";
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

            reader.close();
            is.close();
            conn.disconnect();

            Log.d("NetworkWorker", "--" + "closed connection");

            callback.getResult(sb.toString());
        } catch(Exception ex) {
            //AlertWorker.ok()
            Log.d("NetworkWorker", "--An Error occurred: " + ex.getMessage());
            callback.getResult("!");
        }
    }
}
