package com.technicalround.iskconsadhanareport;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

/**
 * Created by Ashu Pathak on 1/16/2016.
 */
public class DialogTimePicker extends Dialog {

    InterfaceTaskCompleted listener;
    //Context context;
    Spinner spinnerHourList, spinnerMinuteList, spinnerAmPmList;
    View callerView;
    private Integer hourIdx;
    private Integer minuteIdx;
    private Integer amPmIdx;
    private Integer combinedTimeIdx, defaultCombinedTimeIdx, elementId;
    private String combinedString;

    public DialogTimePicker(Context context_1, Integer elementId_1,
                            InterfaceTaskCompleted listener_1, View callerView_1,
                            Integer defaultCombinedTimeIdx_1) {
        super(context_1);
        listener = listener_1;
        //context = context_1;
        callerView = callerView_1;
        defaultCombinedTimeIdx = defaultCombinedTimeIdx_1;
        elementId = elementId_1;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            //Log.d("ISS", "In TimePicker");
            setContentView(R.layout.fragment_dialog_time_picker);
            spinnerHourList = (Spinner) findViewById(R.id.spinnerHour);
            spinnerMinuteList = (Spinner) findViewById(R.id.spinnerMinute);
            spinnerAmPmList = (Spinner) findViewById(R.id.spinnerAmPm);

            amPmIdx = defaultCombinedTimeIdx / 120;
            hourIdx = (defaultCombinedTimeIdx - amPmIdx * 120) / 10;
            minuteIdx = (defaultCombinedTimeIdx % 10);
            try {
                spinnerHourList.setSelection(hourIdx);
                spinnerMinuteList.setSelection(minuteIdx);
                spinnerAmPmList.setSelection(amPmIdx);
            } catch (Exception e) {
                spinnerHourList.setSelection(0);
                spinnerMinuteList.setSelection(0);
                spinnerAmPmList.setSelection(0);
            }

            Button buttonOk = (Button) findViewById(R.id.buttonOk);
            Button buttonCancel = (Button) findViewById(R.id.buttonCancel);
            buttonOk.setOnClickListener(new android.view.View.OnClickListener() {
                public void onClick(View v) {
                    hourIdx = spinnerHourList.getSelectedItemPosition();
                    minuteIdx = spinnerMinuteList.getSelectedItemPosition();
                    amPmIdx = spinnerAmPmList.getSelectedItemPosition();
                    combinedTimeIdx = hourIdx * 10 + minuteIdx + amPmIdx * 120;

                    listener.onTimePickSelection(callerView, combinedTimeIdx, true);
                    DialogTimePicker.this.dismiss();
                }
            });
            buttonCancel.setOnClickListener(new android.view.View.OnClickListener() {
                public void onClick(View v) {
                    //mReadyListener.cancelled();
                    DialogTimePicker.this.dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
