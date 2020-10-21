package com.technicalround.iskconsadhanareport;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

//import android.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentOverview#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentOverview extends Fragment implements InterfaceTaskCompleted {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private Context context;
    private ProgressBar httpProgressBar;
    private InterfaceTaskCompleted mListener;
    private Button buttonDataSync;

    public FragmentOverview() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentOverview.
     */
    public static FragmentOverview newInstance(String param1, String param2) {
        FragmentOverview fragment = new FragmentOverview();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        context = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_overview, container, false);
        try {
            //User defined tasks
            TextView textViewWelcome = (TextView) view.findViewById(R.id.textViewWelcome);
            textViewWelcome.setText("Welcome " + mParam1);

            textViewWelcome = (TextView) view.findViewById(R.id.textViewSadhanaScore);
            textViewWelcome.setText(context.getText(R.string.GuidesEmail) + AppPreferences.user.counselor +
                    context.getText(R.string.FriendsEmail) + AppPreferences.user.friend);

            TextView textViewNews = (TextView) view.findViewById(R.id.textViewNews);
            textViewNews.setText(AppPreferences.savedNews);

            httpProgressBar = (ProgressBar) view.findViewById(R.id.httpProgressBar);
            buttonDataSync = (Button) view.findViewById(R.id.buttonDataSync);
            buttonDataSync.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Log.d("ISS", "In downloadFriendsList");
                    if (AppPreferences.user.id < 1) {
                        Toast.makeText(getContext(), "GUEST user can't SYNC data on SERVER", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //Disable this button until response comes
                    buttonDataSync.setEnabled(false);
                    buttonDataSync.setText("WAIT");


                }
            });

            //HomeOverview page count increase
            AppPreferences.homePageCount++;
            if((AppPreferences.homePageCount == 1) && (AppPreferences.user.id > 0)){
                String link = "";
                //if(AppPreferences.syncedSadhanaIndex==0)
                {
                    link = context.getString(R.string.WEB_ROOT) + "devotee.php?act=friends";
                    String data = "email=" + AppPreferences.user.email;
                    new ExecuteHttpPost(context, FragmentOverview.this, httpProgressBar).execute(link, data);
                }
                //Download Inbox messages if friend or counselor set
                if(!(AppPreferences.user.counselor.isEmpty() && AppPreferences.user.friend.isEmpty())) {
                    Log.d("ISS", "Download Messages");
                    link = context.getString(R.string.WEB_ROOT) + "message.php?act=fetch";
                    String data = "rcvrId=" + AppPreferences.user.id;
                    new ExecuteHttpPost(context, FragmentOverview.this, httpProgressBar).execute(link, data);
                }

                //Download sadhana of all friends and oneself
                if (BuildConfig.DEBUG) Log.d("ISS", "Download Sadhana after syncedSadhanaIndex" + AppPreferences.syncedSadhanaIndex);
                link = context.getString(R.string.WEB_ROOT) + "sadhana.php?act=fetchAllSadhana";
                String data = "email=" + AppPreferences.user.email + "&startId=" + AppPreferences.syncedSadhanaIndex;
                new ExecuteHttpPost(context, FragmentOverview.this, httpProgressBar).execute(link, data);

                //Download News
                Log.d("ISS", "Download News");
                link = context.getString(R.string.WEB_ROOT) + "devotee.php?act=news";
                data = "rcvrId=" + AppPreferences.user.id;
                new ExecuteHttpPost(context, FragmentOverview.this, httpProgressBar).execute(link, data);

                //Auto upload any pending sadhana
                int totalPending = DatabaseActions.queryCountUnSyncedSadhanaEntries(AppPreferences.user.id);
                if (totalPending > 0) {
                    SadhanaEntry[] outSadhanaEntry = new SadhanaEntry[3];
                    for (int i = 0; i < 3; i++) {
                        outSadhanaEntry[i] = new SadhanaEntry();
                    }
                    int entriesRead = DatabaseActions.queryRecentUnSyncedSadhanaEntries
                            (AppPreferences.user.id, outSadhanaEntry);
                    if (entriesRead > 0) {
                        int minEntries = (entriesRead > 3) ? 3 : entriesRead;
                        Toast.makeText(context, "Uploading " + minEntries + " out of "
                                + totalPending + " Sadhana Entries Pending", Toast.LENGTH_SHORT).show();
                        for (int i = 0; i < minEntries; i++) {
                            try {
                                sendSadhanaEntryToServer(outSadhanaEntry[i], httpProgressBar);
                            } catch (Exception e) {
                                Toast.makeText(context, "Error:sendSadhanaEntryHttp", Toast.LENGTH_SHORT).show();
                                Log.d("ISS", "sendSadhanaEntryHttp Exception: " + e.toString());
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    // You can Rename method, update argument and hook method into UI event
    public void onButtonPressed(String response) {
        if (mListener != null) {
            mListener.onFragmentInteraction(response);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (InterfaceTaskCompleted) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    /*
    public interface OnFragmentInteractionListener {
        // You can Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }
   */
    private void sendSadhanaEntryToServer(SadhanaEntry outSadhanaEntry, ProgressBar httpProgressBar_1) throws Exception {
        if (outSadhanaEntry.usId > 0) {
            String link = getContext().getString(R.string.WEB_ROOT) + "sadhana.php?act=add";
            String data = "usId=" + outSadhanaEntry.usId + "&date=" + outSadhanaEntry.date;
            //Sleep
            data += "&tSleep=" + outSadhanaEntry.tSleep;
            //Wake up
            data += "&tWakeup=" + outSadhanaEntry.tWakeup;
            //Chant time
            data += "&tChant=" + outSadhanaEntry.tChant;
            data += "&chantRnd=" + outSadhanaEntry.chantRnd;
            //Morning chanting
            data += "&chantMorn=" + outSadhanaEntry.chantMorn;
            //Chant quality
            data += "&chantQ=" + outSadhanaEntry.chantQ;
            //Mangal Arti
            data += "&mangal=" + outSadhanaEntry.mangal;
            //Hearing
            data += "&tHear=" + outSadhanaEntry.tHear;
            //Reading
            data += "&tRead=" + outSadhanaEntry.tRead;
            //Service
            data += "&tService=" + outSadhanaEntry.tService;
            //Time Waste
            data += "&tDayRest=" + outSadhanaEntry.tDayRest;
            //Calculate score
            data += "&score=" + outSadhanaEntry.score + "&colorBitmap=" + outSadhanaEntry.colorBitmap
                    + "&dateToday=" + FragmentFillSadhana.dateIndexForToday();
            //Execute HTTP Link, confirm if score
            //Send Data to Server

            new ExecuteHttpPost(getContext(), this, httpProgressBar_1).execute(link, data);
        }
        /*
        else {
            Toast toast = Toast.makeText(getContext(), "GUEST user can't save data on INTERNET SERVER", Toast.LENGTH_SHORT);
            toast.show();
            //Log.d("ISS", "Guest not allowed to save data on INTERNET SERVER.");
        }*/
    }

    //Callback function handler
    @Override
    public void onFragmentInteraction(String response) {
    }

    //Callback function handler
    @Override
    public void onHttpResponse(String response) {
        try {
            //RE-enable this button as response came
            buttonDataSync.setEnabled(true);
            buttonDataSync.setText("SYNC");
            //Toast toast = Toast.makeText(getContext(), response, Toast.LENGTH_SHORT);
            //toast.show();
            //Log.d("ISS", "get friends response" + response);
            //Check whether successful progress
            if (response.startsWith("SuccessFriendList:")) {
                Toast.makeText(context, "Downloaded Friend List", Toast.LENGTH_SHORT).show();
                String jsonResult = response.substring("SuccessFriendList:".length());
                //Clear Existing friend list
                DatabaseActions.removeAllFriends(AppPreferences.user.id);
                //Parse JSON
                try {
                    JSONArray jsonDataArray = new JSONArray(jsonResult);
                    //Log.d("ISS", "friendslist json length"+jsonDataArray.length());
                    JSONObject jsonDataSingle;
                    if (jsonDataArray.length() > 0) {
                        for (int i = 0; i < jsonDataArray.length(); i++) {
                            jsonDataSingle = jsonDataArray.getJSONObject(i);
                            DevoteeEntry.saveJsonToFriendEntry(AppPreferences.user.id, jsonDataSingle, (short) 0);
                            //Log.d("ISS", "friend json item retrieved");
                        }
                    } else {
                        //Toast.makeText(context,"Error:"+jsonResult, Toast.LENGTH_SHORT).show();
                        Log.d("ISS", "no Json object found");
                    }
                } catch (Exception e) {
                    Toast.makeText(context, "Error:" + jsonResult, Toast.LENGTH_SHORT).show();
                    Log.d("ISS", "Exception: " + e.toString());
                    e.printStackTrace();
                }
            } else if (response.startsWith("SuccessFriendsSadhana:")) {
                String jsonResult = response.substring("SuccessFriendsSadhana:".length());
                if (jsonResult.isEmpty()) {
                    Toast.makeText(context, "No Sadhana Update Found", Toast.LENGTH_SHORT).show();
                    Log.d("ISS", "No Sadhana Update Found");
                    return;
                } else {
                    Toast.makeText(context, "Downloaded Friends' Sadhana", Toast.LENGTH_SHORT).show();
                }
                //Parse JSON
                try {
                    JSONArray jsonDataArray = new JSONArray(jsonResult);
                    Log.d("ISS", "Sadhana Entries Rcvd:" + jsonDataArray.length());
                    JSONObject jsonDataSingle;
                    int jsonAutoId, jsonAutoIdMax = 0;
                    if (jsonDataArray.length() > 0) {
                        for (int i = 0; i < jsonDataArray.length(); i++) {
                            jsonDataSingle = jsonDataArray.getJSONObject(i);
                            jsonAutoId = SadhanaEntry.saveJsonToSadhanaEntry(jsonDataSingle);
                            //Track jsonAutoIdMax
                            if (jsonAutoId > jsonAutoIdMax) jsonAutoIdMax = jsonAutoId;
                        }
                        //Save jsonAutoIdMax to AppPreferences for future data update
                        AppPreferences.saveMaxJsonIndex(jsonAutoIdMax);
                        /*
                        //update friends list here only when sadhana data got changed
                        String link = context.getString(R.string.WEB_ROOT) + "devotee.php?act=friends&email="
                                + AppPreferences.user.email;
                        new ExecuteHttpGet(context, FragmentOverview.this,
                                httpProgressBar).execute(link);
                         */
                    } else {
                        //Toast.makeText(context,"Error:"+jsonResult, Toast.LENGTH_SHORT).show();
                        Log.d("ISS", "no sadhana  object found");
                    }
                } catch (Exception e) {
                    Toast.makeText(context, "Error:" + jsonResult, Toast.LENGTH_SHORT).show();
                    Log.d("ISS", "Exception: " + e.toString());
                    e.printStackTrace();
                }
            } else if (response.startsWith("SuccessAddSadhana:")) {
                //Toast.makeText(getContext(), response, Toast.LENGTH_SHORT).show();
                int dateIndex = Integer.valueOf(response.substring("SuccessAddSadhana:".length()));
                //Mark entry synced
                DatabaseActions.markSadhanaEntrySynced(AppPreferences.user.id, dateIndex);
                Toast.makeText(getContext(), ActivityMainDevotee.printDateFromDateIndex(dateIndex)
                        + " Entry Saved on Server", Toast.LENGTH_SHORT).show();
            } else if (response.startsWith("SuccessFetchMessages:")) {
                String jsonResult = response.substring("SuccessFetchMessages:".length());
                if (jsonResult.isEmpty()) {
                    Toast.makeText(context, "No New message in Inbox", Toast.LENGTH_SHORT).show();
                    return;
                }
                //Parse JSON
                try {
                    JSONArray jsonDataArray = new JSONArray(jsonResult);
                    Log.d("ISS", "messageslist json length" + jsonDataArray.length());
                    Toast.makeText(context, jsonDataArray.length() + " New messages in Inbox", Toast.LENGTH_SHORT).show();
                    JSONObject jsonDataSingle;
                    if (jsonDataArray.length() > 0) {
                        for (int i = 0; i < jsonDataArray.length(); i++) {
                            jsonDataSingle = jsonDataArray.getJSONObject(i);
                            int autoId = MessageEntry.saveJsonToMessageEntry(jsonDataSingle);
                            if (autoId >= 0) {
                                Log.d("ISS", "delete Message from server");
                                String link = context.getString(R.string.WEB_ROOT) + "message.php?act=delete";
                                String data = "autoId=" + autoId;
                                new ExecuteHttpPost(context, null, null).execute(link, data);
                            }
                        }
                    } else {
                        //Toast.makeText(context,"Error:"+jsonResult, Toast.LENGTH_SHORT).show();
                        Log.d("ISS", "no Json object found");
                    }
                } catch (Exception e) {
                    Toast.makeText(context, "Error:" + jsonResult, Toast.LENGTH_SHORT).show();
                    Log.d("ISS", "Exception: " + e.toString());
                    e.printStackTrace();
                }
            } else if (response.startsWith("SuccessNews:")) {
                //Toast.makeText(getContext(), response, Toast.LENGTH_SHORT).show();
                String news = response.substring("SuccessNews:".length());
                TextView textViewNews = (TextView) getActivity().findViewById(R.id.textViewNews);
                textViewNews.setText(news);
                //Save jsonAutoIdMax to AppPreferences for future data update
                AppPreferences.saveNews(news);
            } else if (response.startsWith("Error0:"))
                Toast.makeText(context, "No Internet Connection !",
                        Toast.LENGTH_SHORT).show();
            else if (response.startsWith("Exception:"))
                Toast.makeText(context, "Could not connect to Server, Try after some time",
                        Toast.LENGTH_SHORT).show();
            else if (response.startsWith("Error:"))
                Toast.makeText(context, "Could not connect to Server, Try after some time",
                        Toast.LENGTH_SHORT).show();
            else Toast.makeText(context, response, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.d("ISS", "OverviewHttpResponse: " + e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void onTimePickSelection(View view, int combinedTime, boolean delayIgnoreDialog) {
    }

}
