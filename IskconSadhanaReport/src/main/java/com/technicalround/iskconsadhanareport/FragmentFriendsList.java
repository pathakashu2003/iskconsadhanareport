package com.technicalround.iskconsadhanareport;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentFriendsList.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentFriendsList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentFriendsList extends Fragment implements InterfaceTaskCompleted {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    DevoteeEntry[] juniorsList, friendsList;
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private LinearLayout linearLayoutJuniors, linearLayoutFriends;
    private Context context;
    private ProgressBar httpProgressBar;
    private LayoutInflater inflater;
    private View viewFragFriendList;
    private Button buttonDownloadFriends;

    public FragmentFriendsList() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentFriendsList.
     */
    public static FragmentFriendsList newInstance(String param1, String param2) {
        FragmentFriendsList fragment = new FragmentFriendsList();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater_1, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        inflater = inflater_1;
        viewFragFriendList = inflater.inflate(R.layout.fragment_friends_list, container, false);
        //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
        context = getContext();
        try {
        /*
        //Fetch Juniors list
        linearLayoutJuniors = (LinearLayout) viewFragFriendList.findViewById(R.id.linearLayoutJuniors);
        juniorsList = buildFriendInfoList(linearLayoutJuniors, (short) 1);
        */
            //Fetch Friends list
            linearLayoutFriends = (LinearLayout) viewFragFriendList.findViewById(R.id.linearLayoutFriends);
            friendsList = buildFriendInfoList(linearLayoutFriends, (short) 0);

            httpProgressBar = (ProgressBar) viewFragFriendList.findViewById(R.id.httpProgressBar);
            httpProgressBar.setVisibility(View.INVISIBLE);
            buttonDownloadFriends = (Button) viewFragFriendList.findViewById(R.id.buttonDownloadFriends);
            buttonDownloadFriends.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Disable this button until response
                    buttonDownloadFriends.setEnabled(false);
                    buttonDownloadFriends.setText("Wait Please ...");
                    if (AppPreferences.user.id < 1) {
                        Toast.makeText(getContext(), "GUEST user can't SYNC data on SERVER", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String link = context.getString(R.string.WEB_ROOT) + "devotee.php?act=friends";
                    String data = "email=" + AppPreferences.user.email;
                    new ExecuteHttpPost(context, FragmentFriendsList.this, httpProgressBar).execute(link, data);

                /*
                    //Download sadhana of all friends and oneself
                    String link = context.getString(R.string.WEB_ROOT) + "sadhana.php?act=fetchAllSadhana&email="
                            + AppPreferences.user.email + "&startId=" + AppPreferences.syncedSadhanaIndex;
                    new ExecuteHttpGet(context, FragmentFriendsList.this,
                            httpProgressBar).execute(link);
                 */
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return viewFragFriendList;
    }

    // you can Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction("");
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
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

    /*
    private DevoteeEntry[] buildDevoteeList(TableLayout tableLayoutDevotees, short isJunior) {
        //Fetch juniors list
        DevoteeEntry[] devoteeList = DatabaseActions.fetchFriends(AppPreferences.user.id, isJunior);
        for(int i=0; i<devoteeList.length; i++) {
            addFriendRow(tableLayoutDevotees, devoteeList[i], isJunior);
        }
        return devoteeList;
    }
    private void addFriendRow(TableLayout tableLayoutDevotees, DevoteeEntry devotee, short isJunior) {
        TableRow tableRowFriend = new TableRow(context);
        Log.d("ISS", "TableRow Friend Entry Add:isJunior:" + isJunior);

        CheckBox checkBoxReportStatus = new CheckBox(context);
        boolean isReportingActive = false;
        if(isJunior==1) {
            if (devotee.reportStatus == DevoteeEntry.DEV_COUNSELOR_GETTING)
                isReportingActive = true;
        }
        else{
            if (devotee.reportStatus == DevoteeEntry.DEV_FRIEND_GETTING)
                isReportingActive = true;
        }
        checkBoxReportStatus.setChecked(isReportingActive);

        TextView tvName = new TextView(context);
        tvName.setText(devotee.name);
        tvName.setTextColor(Color.RED);

        TextView tvScore = new TextView(context);
        tvScore.setText(" Avg. Score:"+devotee.isJunior+"%");

        tableRowFriend.addView(checkBoxReportStatus);
        tableRowFriend.addView(tvName);
        tableRowFriend.addView(tvScore);
        tableLayoutDevotees.addView(tableRowFriend);
    }
    */
    private DevoteeEntry[] buildFriendInfoList(LinearLayout linearLayoutDevotees, short isJunior) {
        //Fetch juniors list
        if (BuildConfig.DEBUG) Log.d("ISS", "In buildFriendInfoList under CreateView");
        linearLayoutDevotees.removeAllViews();
        try {
            DevoteeEntry[] devoteeList = DatabaseActions.fetchFriends(AppPreferences.user.id, isJunior);
            for (int i = 0; i < devoteeList.length; i++) {
                addFriendInfoFrame(linearLayoutDevotees, devoteeList[i], isJunior);
            }
            return devoteeList;
        } catch (Exception e) {
            Log.d("ISS", e.toString());
            e.printStackTrace();
            return null;
        }
    }

    private void addFriendInfoFrame(LinearLayout linearLayoutDevotees, DevoteeEntry devotee, short isJunior) throws Exception {

        //Log.d("ISS", "TableRow Friend Entry Add:isJunior:" + isJunior);
        View frameView = inflater.inflate(R.layout.layout_friend_info, linearLayoutDevotees, false);

        boolean isReportingActive = false;
        if (isJunior == 1) {
            if (devotee.reportStatus == DevoteeEntry.DEV_COUNSELOR_GETTING)
                isReportingActive = true;
        } else {
            if (devotee.reportStatus == DevoteeEntry.DEV_FRIEND_GETTING)
                isReportingActive = true;
        }
        /*
        CheckBox checkBoxName = (CheckBox)frameView.findViewById(R.id.checkBoxName);
        checkBoxName.setChecked(isReportingActive);
        checkBoxName.setText(devotee.name);
        checkBoxName.setTextColor(Color.RED);
        */

        //Log.d("ISS", devotee.name);
        TextView textView = (TextView) frameView.findViewById(R.id.textViewName);
        //textView.setText(devotee.name+" (ID: "+devotee.id+")");
        textView.setText(devotee.name);

        textView = (TextView) frameView.findViewById(R.id.textViewCityCountry);
        textView.setText(devotee.city + "(" + devotee.country + ")");

        textView = (TextView) frameView.findViewById(R.id.textViewScore);
        textView.setText("Recent Score: " + devotee.score + "%");

        textView = (TextView) frameView.findViewById(R.id.textViewLastOnline);
        textView.setText("Last Online: " + ActivityMainDevotee.printDateFromDateIndex(devotee.lastOnline));

        ImageView imageViewPic = (ImageView) frameView.findViewById(R.id.imageViewPic);

        //Load profile pic if profile pic version is non-zero
        if(devotee.picVer != 0) {
            ClassImageLoader.displayUserImage(context.getString(R.string.WEB_ROOT),
                    imageViewPic, devotee.id, devotee.picVer);
        }

        linearLayoutDevotees.addView(frameView);
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
            buttonDownloadFriends.setEnabled(true);
            buttonDownloadFriends.setText("Update Friend List");
            //Toast toast = Toast.makeText(getContext(), response, Toast.LENGTH_SHORT);
            //toast.show();
            //Log.d("ISS", "get friends response" + response);
            //Check whether successful progress
            if (response.startsWith("SuccessFriendList:")) {
                Toast.makeText(context, "Download Friend List: Success", Toast.LENGTH_SHORT).show();
                String jsonResult = response.substring("SuccessFriendList:".length());
                //Clear Existing friend list
                DatabaseActions.removeAllFriends(AppPreferences.user.id);
                //Parse JSON
                try {
                    JSONArray jsonDataArray = new JSONArray(jsonResult);
                    if (BuildConfig.DEBUG) Log.d("ISS", "friendslist json length" + jsonDataArray.length());
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
                    linearLayoutFriends = (LinearLayout) viewFragFriendList.findViewById(R.id.linearLayoutFriends);
                    if (linearLayoutFriends != null) {
                        linearLayoutFriends.removeAllViews();
                        friendsList = buildFriendInfoList(linearLayoutFriends, (short) 0);
                    }
                } catch (Exception e) {
                    Toast.makeText(context, "Error:" + jsonResult, Toast.LENGTH_SHORT).show();
                    Log.d("ISS", "Exception: " + e.toString());
                    e.printStackTrace();
                }
            } else if (response.startsWith("SuccessFriendsSadhana:")) {
                Toast.makeText(context, "Download Friends Sadhana: Success", Toast.LENGTH_SHORT).show();
                String jsonResult = response.substring("SuccessFriendsSadhana:".length());
                if (jsonResult.isEmpty()) {
                    Toast.makeText(context, "No Sadhana Update Found", Toast.LENGTH_SHORT).show();
                    Log.d("ISS", "No Sadhana Update Found");
                    return;
                }
                //Parse JSON
                try {
                    JSONArray jsonDataArray = new JSONArray(jsonResult);
                    //Log.d("ISS", "friendsSadhana json length"+jsonDataArray.length());
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

                        //Now get friend list
                        String link = context.getString(R.string.WEB_ROOT) + "devotee.php?act=friends";
                        String data = "email=" + AppPreferences.user.email;
                        new ExecuteHttpPost(context, FragmentFriendsList.this, httpProgressBar).execute(link, data);

                    } else {
                        //Toast.makeText(context,"Error:"+jsonResult, Toast.LENGTH_SHORT).show();
                        Log.d("ISS", "no sadhana  object found");
                    }
                } catch (Exception e) {
                    Toast.makeText(context, "Error:" + jsonResult, Toast.LENGTH_SHORT).show();
                    Log.d("ISS", "Exception: " + e.toString());
                    e.printStackTrace();
                }
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
            Log.d("ISS", "FriendListHttpResponse: " + e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void onTimePickSelection(View view, int combinedTime, boolean delayIgnoreDialog) {
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
    public interface OnFragmentInteractionListener {
        // you can Update argument type and name
        public void onFragmentInteraction(String response);
    }
}
