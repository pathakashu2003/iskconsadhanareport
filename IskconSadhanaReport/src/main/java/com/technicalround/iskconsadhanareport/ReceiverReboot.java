package com.technicalround.iskconsadhanareport;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ReceiverReboot extends BroadcastReceiver {
    public ReceiverReboot() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Log.d("ISS", "ReceiverReboot:onReceive");
        SetAlarms.setReminderBeforeSleep(context);
        if (BuildConfig.DEBUG) {
            //TODO:Remove it
            //SetAlarms.setReminderFrequently(context, 1);//Every Minute alarm for testing
        }

        //throw new UnsupportedOperationException("Not yet implemented");
    }
}
