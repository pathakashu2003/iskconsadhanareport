package com.technicalround.iskconsadhanareport;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONObject;

public class ReceiverReminderNight extends BroadcastReceiver implements InterfaceTaskCompleted {
    public static AppPreferences userPreferences;
    public DatabaseActions databaseSadhanaControl;
    Context rcvContext;

    public ReceiverReminderNight() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        userPreferences = new AppPreferences(context);
        //Database controller init
        databaseSadhanaControl = new DatabaseActions(context);
        DatabaseActions.open();

        rcvContext = context;
        //Toast.makeText(context, "ReceiverReminderNight: Broadcast received", Toast.LENGTH_SHORT).show();
        Log.d("ISS", "ReceiverReminderNight");

        //Create Notification
        createNotification(context, "Sadhana Sharing Reminder", "Please fill today's Sadhana!", 12345);

        //Download Inbox messages
        Log.d("ISS", "RcvrNight:Download Messages");
        String link = context.getString(R.string.WEB_ROOT) + "message.php?act=fetch";
        String data = "rcvrId=" + userPreferences.user.id;
        new ExecuteHttpPost(context, ReceiverReminderNight.this, null).execute(link, data);

    }

    public void createNotification(Context context, String title, String text, int notiId) {
        //Define sound URI
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        //Create Notification
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setSmallIcon(R.drawable.noti_icon2);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(text);
        mBuilder.setSound(soundUri); //This sets the sound to play

        Intent resultIntent = new Intent(context, ActivityLogin.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(ActivityLogin.class);

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        /*
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 3, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT|PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.setContentIntent(pendingIntent);
        */
        mBuilder.setAutoCancel(true);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // notificationID allows you to update the notification later on.
        mNotificationManager.notify(notiId, mBuilder.build());

    }

    //Callback function handler
    @Override
    public void onFragmentInteraction(String response) {
    }

    //Callback function handler
    @Override
    public void onHttpResponse(String response) {
        try {
            //Log.d("ISS", "get friends response" + response);
            //Check whether successful progress
            if (response.startsWith("SuccessFetchMessages:")) {
                String jsonResult = response.substring("SuccessFetchMessages:".length());
                if (jsonResult.isEmpty()) {
                    return;
                }
                //Parse JSON
                try {
                    JSONArray jsonDataArray = new JSONArray(jsonResult);
                    Log.d("ISS", "RcvrNight:messageslist json length " + jsonDataArray.length());
                    JSONObject jsonDataSingle;
                    if (jsonDataArray.length() > 0) {
                        for (int i = 0; i < jsonDataArray.length(); i++) {
                            jsonDataSingle = jsonDataArray.getJSONObject(i);
                            int autoId = MessageEntry.saveJsonToMessageEntry(jsonDataSingle);

                            if (autoId >= 0) {
                                Log.d("ISS", "RcvrNight:delete Message from server");
                                String link = rcvContext.getString(R.string.WEB_ROOT) + "message.php?act=delete";
                                String data = "autoId=" + autoId;
                                new ExecuteHttpPost(rcvContext, null, null).execute(link, data);
                            }
                        }
                        //Create Notification
                        createNotification(rcvContext, jsonDataArray.length() + " New Message Received",
                                "Open message from Guide.", 12345);
                    } else {
                        //Toast.makeText(context,"Error:"+jsonResult, Toast.LENGTH_SHORT).show();
                        Log.d("ISS", "RcvrNight:no Json object found");
                    }
                } catch (Exception e) {
                    Log.d("ISS", "RcvrNight:Exception: " + e.toString());
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            Log.d("ISS", "RcvrNight:OverviewHttpResponse: " + e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void onTimePickSelection(View view, int combinedTime, boolean delayIgnoreDialog) {
    }

}
