package com.technicalround.iskconsadhanareport;

import android.view.View;

/**
 * Created by ggne0439 on 12/30/2015.
 */
//Public Interface
public interface InterfaceTaskCompleted {

    //On completion of HTTP Link
    void onHttpResponse(String response);

    //On fragment action
    public void onFragmentInteraction(String response);

    //On TimePickSelction action
    public void onTimePickSelection(View callerView, int combinedTime, boolean delayIgnoreDialog);
}

