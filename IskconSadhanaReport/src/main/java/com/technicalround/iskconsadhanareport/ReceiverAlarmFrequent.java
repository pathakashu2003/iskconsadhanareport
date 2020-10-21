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
import android.widget.Toast;

public class ReceiverAlarmFrequent extends BroadcastReceiver {

    public static AppPreferences userPreferences;
    public DatabaseActions databaseSadhanaControl;
    Context rcvContext;

    public ReceiverAlarmFrequent() {
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
        Toast.makeText(context, "ISKCON Sadhana Sharing: Reminder", Toast.LENGTH_SHORT).show();
        if (BuildConfig.DEBUG) Log.d("ISS", "ReceiverAlarmFrequent");
        //throw new UnsupportedOperationException("Not yet implemented");
        //Create Notification
        createNotification(context, "ISS:Frequent", "Please remove this alarm", 12345);

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
        /*
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(context, notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
        */
    }

}
