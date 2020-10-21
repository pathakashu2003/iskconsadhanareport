package com.technicalround.iskconsadhanareport;

/**
 * Created by Ashu Pathak on 12/26/2015.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class ExecuteHttpPost extends AsyncTask<String, Void, String> {
    static int runningProgressBarCount = 0;
    private TextView statusField;//,roleField;
    private Context context;
    private InterfaceTaskCompleted interfaceForTasks;
    private boolean isValidResponse = false;
    private ProgressBar progressBarHttp;

    //ProgressDialog loading;
    public ExecuteHttpPost(Context context, InterfaceTaskCompleted interfaceForTasks,
                           ProgressBar progressBarHttp) {
        this.context = context;
        this.interfaceForTasks = interfaceForTasks;
        this.progressBarHttp = progressBarHttp;
        if (progressBarHttp != null) {
            progressBarHttp.setVisibility(View.VISIBLE);
            runningProgressBarCount++;
        }
    }

    public static String executeHttpPostRequest(String link, String contents) throws Exception {
        URL url = new URL(link.replace(" ", "%20"));
        if (BuildConfig.DEBUG) {
            Log.d(AppPreferences.TAG, "Link: " + url.toString() + " " + contents);
        }
        URLConnection urlConnection = url.openConnection();
        urlConnection.setDoOutput(true);

        OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
        out.write(contents.getBytes());
        out.flush();

        InputStream is = new BufferedInputStream(urlConnection.getInputStream());
        BufferedReader in = new BufferedReader(new InputStreamReader(is));

        StringBuffer response = new StringBuffer("");
        String line = "";

        while ((line = in.readLine()) != null) {
            response.append(line);
            break;
        }
        in.close();
        //Log.d(AppPreferences.TAG, "Post response:" + response.toString());
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

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //loading = ProgressDialog.show(context,"Please wait...","Syncing with server",false,true);
    }

    @Override
    protected String doInBackground(String... arg0) {
        String link = arg0[0];
        String response;
        //Check connectivity
        if ((!BuildConfig.DEBUG) && (!isInternetConnected(this.context))) {
            //Toast.makeText(this.context,"No Internet Connection !!", Toast.LENGTH_SHORT).show();
            //Log.d(AppPreferences.TAG, "no internet connection.");
            if (progressBarHttp != null) {
                runningProgressBarCount--;
            }
            return "Error0:";// No Internet Connection
        }
        try {
            response = executeHttpPostRequest(link, arg0[1]);
            isValidResponse = true;
        } catch (Exception e) {
            response = "Exception: " + e.toString();//getMessage();
        }
        return response;
    }

    @Override
    protected void onPostExecute(String response) {
        if (progressBarHttp != null) {
            runningProgressBarCount--;
            if (runningProgressBarCount <= 0) progressBarHttp.setVisibility(View.INVISIBLE);
        }
        if (interfaceForTasks != null) {
            try {
                interfaceForTasks.onHttpResponse(response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
