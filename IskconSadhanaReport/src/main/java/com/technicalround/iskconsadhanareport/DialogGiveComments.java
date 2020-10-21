package com.technicalround.iskconsadhanareport;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * Created by Ashu Pathak on 1/16/2016.
 */
public class DialogGiveComments extends Dialog {

    InterfaceTaskCompleted listener;
    Context context;
    View callerView;
    String sndrName;
    int rcvrId;

    public DialogGiveComments(Context context_1, String sndrName_1,
                              InterfaceTaskCompleted listener_1, View callerView_1,
                              int rcvrId_1) {
        super(context_1);
        listener = listener_1;
        //context = context_1;
        callerView = callerView_1;
        sndrName = sndrName_1;
        rcvrId = rcvrId_1;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            //Log.d("ISS", "In GiveComments to rcvrId:" + rcvrId);
            setContentView(R.layout.fragment_dialog_time_picker);

            Button buttonOk = (Button) findViewById(R.id.buttonOk);
            Button buttonCancel = (Button) findViewById(R.id.buttonCancel);
            buttonOk.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //Download Inbox messages
                    if (BuildConfig.DEBUG) Log.d("ISS", "Download Messages");
                    DialogGiveComments.this.dismiss();
                }
            });
            buttonCancel.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //mReadyListener.cancelled();
                    DialogGiveComments.this.dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
