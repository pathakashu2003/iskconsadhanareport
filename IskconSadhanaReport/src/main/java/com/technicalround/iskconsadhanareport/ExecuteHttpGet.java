package com.technicalround.iskconsadhanareport;

/**
 * Created by Ashu Pathak on 12/26/2015.
 */

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class ExecuteHttpGet extends AsyncTask<String, Void, String> {
    private TextView statusField;//,roleField;
    private Context context;
    private InterfaceTaskCompleted listener;
    //private int byGetOrPost = 0;
    private boolean isValidResponse = false;
    private ProgressBar httpProgressBar;

    public ExecuteHttpGet(Context context, InterfaceTaskCompleted listener_1,
                          ProgressBar httpProgressBar_1) {
        this.context = context;
        this.listener = listener_1;
        this.httpProgressBar = httpProgressBar_1;
        if (httpProgressBar != null) httpProgressBar.setVisibility(View.VISIBLE);
    }

    public static String executeHttpGetRequest(String link) throws Exception {
        URL url = new URL(link.replace(" ", "%20"));
        if (BuildConfig.DEBUG) {
            Log.d("ISS", "Link: " + url.toString());
        }
        URLConnection urlConnection = url.openConnection();
        InputStream is = new BufferedInputStream(urlConnection.getInputStream());
        BufferedReader in = new BufferedReader(new InputStreamReader(is));

        StringBuffer response = new StringBuffer("");
        String line = "";

        while ((line = in.readLine()) != null) {
            response.append(line);
            break;
        }
        in.close();
        //Log.d("ISS", response.toString());
        return response.toString();
    }

    public static boolean isInternetConnected(Context context) {
        try {
            ConnectivityManager cm =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    protected void onPreExecute() {

    }

    @Override
    protected String doInBackground(String... arg0) {
        String link = arg0[0];
        String response;
        //Check connectivity
        if ((!BuildConfig.DEBUG) && (!isInternetConnected(this.context))) {
            //Toast.makeText(this.context,"No Internet Connection !!", Toast.LENGTH_SHORT).show();
            //Log.d("ISS", "no internet connection.");
            return "Error0:";// No Internet Connection
        }
        try {
            response = executeHttpGetRequest(link);
            isValidResponse = true;
        } catch (Exception e) {
            response = "Exception: " + e.toString();//getMessage();
        }
        return response;
    }

    @Override
    protected void onPostExecute(String response) {
        if (httpProgressBar != null) httpProgressBar.setVisibility(View.INVISIBLE);
        if (this.listener != null)
            this.listener.onHttpResponse(response);
    }
}
