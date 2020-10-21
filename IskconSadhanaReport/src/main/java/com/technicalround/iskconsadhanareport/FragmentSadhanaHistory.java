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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

//import android.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InterfaceTaskCompleted} interface
 * to handle interaction events.
 * Use the {@link FragmentSadhanaHistory#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentSadhanaHistory extends Fragment implements InterfaceTaskCompleted {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final int NUM_OF_COLUMNS = 10;
    private static final int ENTRY_COLUMN_USERID = 1;
    private static final int ENTRY_COLUMN_DATE = 2;
    private static final int ENTRY_COLUMN_TSLEEP = 3;
    private static final int ENTRY_COLUMN_TWAKEUP = 4;
    private static final int ENTRY_COLUMN_TCHANT = 5;
    private static final int ENTRY_COLUMN_CHANTRND = 6;
    private static final int ENTRY_COLUMN_CHANTMORN = 7;
    private static final int ENTRY_COLUMN_CHANTQ = 8;
    private static final int ENTRY_COLUMN_MANGAL = 9;
    private static final int ENTRY_COLUMN_THEAR = 10;
    private static final int ENTRY_COLUMN_TREAD = 11;
    private static final int ENTRY_COLUMN_TSERVICE = 12;
    private static final int ENTRY_COLUMN_TDAYREST = 13;
    private static final int ENTRY_COLUMN_SCORE = 14;
    private static int userId, screenWidthInPixels, defaultCellWidth = 20;
    private static TableRow.LayoutParams columnLayoutParams;
    DevoteeEntry friendInfo;
    boolean mDone = false;
    TableLayout tableHistory, tableHistoryHeader;
    LinearLayout linearLayoutHistorySadhana;
    private int NUM_OF_ROWS = 15;//For friends
    private int friendId;
    private InterfaceTaskCompleted mListener;
    private Context context;
    private View fragView;
    private Button buttonDataSync;
    private int sId = 0;

    public FragmentSadhanaHistory() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param friendId_1 Parameter 1.
     * @param userId_1   Parameter 2.
     * @return A new instance of fragment FragmentSadhanaHistory.
     */
    public static FragmentSadhanaHistory newInstance(int friendId_1, int userId_1) {
        FragmentSadhanaHistory fragment = new FragmentSadhanaHistory();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, friendId_1);
        args.putInt(ARG_PARAM2, userId_1);
        fragment.setArguments(args);
        //Log.d("ISS", "FragmentSadhanaHistory: newInstance:"+param1);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            if (getArguments() != null) {
                friendId = getArguments().getInt(ARG_PARAM1);
                userId = getArguments().getInt(ARG_PARAM2);
                friendInfo = DatabaseActions.getFriendInfo(friendId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragView = inflater.inflate(R.layout.fragment_sadhana_history, container, false);
        context = getContext();
        try {
            tableHistory = (TableLayout) fragView.findViewById(R.id.tableHistory);
            tableHistoryHeader = (TableLayout) fragView.findViewById(R.id.tableHistoryHeader);
            linearLayoutHistorySadhana = (LinearLayout) fragView.findViewById(R.id.linearLayoutHistorySadhana);

            //If seeing own sadhana history then show one month data
            if (friendId == userId) {
                //Show history for 30 days instead of 21 Days
                NUM_OF_ROWS = 30;
                //Hide Message button
                RelativeLayout relativeLayoutComments = (RelativeLayout) fragView.findViewById(R.id.relativeLayoutComments);
                relativeLayoutComments.removeAllViews();
            } else {
                if (friendInfo == null) return null;
                buttonDataSync = (Button) fragView.findViewById(R.id.buttonGiveComments);
                buttonDataSync.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Toast.makeText(context,"Feature Coming Soon", Toast.LENGTH_SHORT).show();
                        //Download Inbox messages
                        if (BuildConfig.DEBUG) Log.d("ISS", "Add Message for user:" + friendInfo.name);

                        //Disable this button until response comes
                        buttonDataSync.setEnabled(false);
                        buttonDataSync.setText("WAIT");

                        String msg = ((EditText) fragView.findViewById(R.id.editTextComments)).getText().toString();
                        msg = msg.replace("&", ",");
                        String link = context.getString(R.string.WEB_ROOT) + "message.php?act=add";
                        String data = "rcvrId=" + friendInfo.id +
                                "&sndrName=" + AppPreferences.user.name +
                                "&date=" + FragmentFillSadhana.dateIndexForToday() +
                                "&msg=" + msg;
                        new ExecuteHttpPost(context, FragmentSadhanaHistory.this, null).execute(link, data);

                    }
                });
            }
            columnLayoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT);
            screenWidthInPixels = getResources().getDisplayMetrics().widthPixels;
            defaultCellWidth = screenWidthInPixels / NUM_OF_COLUMNS;
            //columnLayoutParams.width = (screenWidth / NUM_OF_COLUMNS)-3;//3dp margin
            columnLayoutParams.setMargins(0, 2, 6, 0);//(0, 2, 15, 0);

            buildForm();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return fragView;
    }

    @Override
    public void onResume() {
        super.onResume();
        //Log.d("ISS", "FragmentSadhanaHistory: onResume:" + mParam1 + " and " + mParam2 + " Sadhana History");
    }

    // You can Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction("");
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (InterfaceTaskCompleted) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement InterfaceTaskCompleted");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private int id() {
        return sId++;
    }

    private void buildForm() throws Exception {
        SadhanaEntry[] outSadhanaEntry = new SadhanaEntry[NUM_OF_ROWS];
        for (int i = 0; i < NUM_OF_ROWS; i++) {
            outSadhanaEntry[i] = new SadhanaEntry();
        }
        int entriesRead = DatabaseActions.queryLastNSadhanaEntry(friendId, NUM_OF_ROWS, outSadhanaEntry);

        addTableHistoryHeader();
        for (int i = 0; i < entriesRead; i++) {
            addTableHistoryRow(outSadhanaEntry[i]);
        }
    }

    private void addTableHistoryHeader() throws Exception {

        TableRow tableRowSadhanaHeader = new TableRow(context);
        int cellWidth = defaultCellWidth;
        //Log.d("ISS", "TableRow Entry header:");
        TextView tvDate = new TextView(context);
        tvDate.setText("DATE");
        tvDate.setTextSize(10);
        tvDate.setWidth(cellWidth + 6);//4margin

        TextView tvScore = new TextView(context);
        tvScore.setText("Score");
        tvScore.setTextSize(10);
        tvScore.setWidth(cellWidth - 10);//4margin

        TextView tvSleep = new TextView(context);
        tvSleep.setText("Sleep");
        tvSleep.setTextSize(10);
        tvSleep.setWidth(cellWidth + 10);//4margin

        TextView tvWakeup = new TextView(context);
        tvWakeup.setText("Wake");
        tvWakeup.setTextSize(10);
        tvWakeup.setWidth(cellWidth + 4);//4margin

        TextView tvLabel3 = new TextView(context);
        tvLabel3.setText("Arti");
        tvLabel3.setTextSize(10);
        tvLabel3.setWidth(cellWidth - 20);//4margin

        TextView tvLabel4 = new TextView(context);
        tvLabel4.setText("Chant");
        tvLabel4.setTextSize(10);
        tvLabel4.setWidth(cellWidth + 10);//4margin

        TextView tvLabel5 = new TextView(context);
        tvLabel5.setText("Hear");
        tvLabel5.setTextSize(10);
        tvLabel5.setWidth(cellWidth - 0);//4margin

        TextView tvLabel6 = new TextView(context);
        tvLabel6.setText("Read");
        tvLabel6.setTextSize(10);
        tvLabel6.setWidth(cellWidth - 0);//4margin

        TextView tvLabel7 = new TextView(context);
        tvLabel7.setText("Serv");
        tvLabel7.setTextSize(10);
        tvLabel7.setWidth(cellWidth - 0);//4margin

        TextView tvLabel8 = new TextView(context);
        tvLabel8.setText("Waste");
        tvLabel8.setTextSize(10);
        tvLabel8.setWidth(cellWidth - 0);//4margin

        tableRowSadhanaHeader.addView(tvDate);
        tableRowSadhanaHeader.addView(tvScore);
        tableRowSadhanaHeader.addView(tvSleep);
        tableRowSadhanaHeader.addView(tvWakeup);
        tableRowSadhanaHeader.addView(tvLabel3);
        tableRowSadhanaHeader.addView(tvLabel4);
        tableRowSadhanaHeader.addView(tvLabel5);
        tableRowSadhanaHeader.addView(tvLabel6);
        tableRowSadhanaHeader.addView(tvLabel7);
        tableRowSadhanaHeader.addView(tvLabel8);
        //linearLayoutHistorySadhana.addView(tableRowSadhana);
        tableHistoryHeader.addView(tableRowSadhanaHeader);
    }

    private void addTableHistoryRow(SadhanaEntry entry) throws Exception {

        TableRow tableRowSadhana = new TableRow(context);
        int colorBitmap = entry.colorBitmap;
        //Log.d("ISS", "TableRow Entry date:" + entry.date);
        TextView tvDate = new TextView(context);
        setSadhanaCellProperties(tvDate, ENTRY_COLUMN_DATE, 0);
        int date = entry.date % (31 * 12);
        int data1 = (date % 31) + 1;//DayOfMonth
        int data2 = (date / 31) + 1;//Month
        tvDate.setText(data1 + "/" + data2);

        TextView tvScore = new TextView(context);
        setSadhanaCellProperties(tvScore, ENTRY_COLUMN_SCORE, (colorBitmap >> 14) & 0x03);
        tvScore.setText(entry.score + "");

        TextView tvSleep = new TextView(context);
        setSadhanaCellProperties(tvSleep, ENTRY_COLUMN_TSLEEP, colorBitmap & 0x03);
        data1 = entry.tSleep / 10;
        data2 = (entry.tSleep % 10) * 6;
        if (entry.tSleep == 255) tvSleep.setText("-");
        else {
            tvSleep.setText(String.format("%d:%02d", data1, data2));
            //tvSleep.setText(data1 + ":" + data2);
        }
        TextView tvWakeup = new TextView(context);
        setSadhanaCellProperties(tvWakeup, ENTRY_COLUMN_TWAKEUP, (colorBitmap >> 2) & 0x03);
        data1 = entry.tWakeup / 10;
        data2 = (entry.tWakeup % 10) * 6;
        if (entry.tWakeup == 255) tvWakeup.setText("-");
        else {
            tvWakeup.setText(String.format("%d:%02d", data1, data2));
            //tvWakeup.setText(data1 + ":" + data2);
        }

        TextView tvLabel3 = new TextView(context);
        setSadhanaCellProperties(tvLabel3, ENTRY_COLUMN_MANGAL, entry.mangal);
        tvLabel3.setText("" + entry.mangal);

        TextView tvLabel4 = new TextView(context);
        setSadhanaCellProperties(tvLabel4, ENTRY_COLUMN_CHANTRND, (colorBitmap >> 4) & 0x03);
        tvLabel4.setText(String.format("%2d,%2d", entry.chantMorn, (entry.chantRnd - entry.chantMorn)));

        TextView tvLabel5 = new TextView(context);
        setSadhanaCellProperties(tvLabel5, ENTRY_COLUMN_THEAR, (colorBitmap >> 6) & 0x03);
        tvLabel5.setText("" + entry.tHear);

        TextView tvLabel6 = new TextView(context);
        setSadhanaCellProperties(tvLabel6, ENTRY_COLUMN_TREAD, (colorBitmap >> 8) & 0x03);
        tvLabel6.setText("" + entry.tRead);

        TextView tvLabel7 = new TextView(context);
        setSadhanaCellProperties(tvLabel7, ENTRY_COLUMN_TSERVICE, (colorBitmap >> 10) & 0x03);
        tvLabel7.setText("" + entry.tService);

        TextView tvLabel8 = new TextView(context);
        setSadhanaCellProperties(tvLabel8, ENTRY_COLUMN_TDAYREST, (colorBitmap >> 12) & 0x03);
        if (entry.tDayRest == 255) tvLabel8.setText("-");
        else tvLabel8.setText("" + entry.tDayRest);

        tableRowSadhana.addView(tvDate);
        tableRowSadhana.addView(tvScore);
        tableRowSadhana.addView(tvSleep);
        tableRowSadhana.addView(tvWakeup);
        tableRowSadhana.addView(tvLabel3);
        tableRowSadhana.addView(tvLabel4);
        tableRowSadhana.addView(tvLabel5);
        tableRowSadhana.addView(tvLabel6);
        tableRowSadhana.addView(tvLabel7);
        tableRowSadhana.addView(tvLabel8);
        //linearLayoutHistorySadhana.addView(tableRowSadhana);
        tableHistory.addView(tableRowSadhana);
    }

    private void setSadhanaCellProperties(TextView cell, int fieldType, int colorIndex)
            throws Exception {
        /*
        TableRow.LayoutParams columnLayoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT);
        columnLayoutParams.setMargins(6, 5, 0, 5);
        cell.setLayoutParams(columnLayoutParams);
        cell.setPadding(1, 0, 0, 0);
        params.bottomMargin = 5;
        params.topMargin = 10;
        params.weight = (float)0.5;
        */
        int cellWidth = defaultCellWidth;
        cell.setPadding(3, 0, 3, 0);
        cell.setLayoutParams(columnLayoutParams);
        cell.setTextSize(12);

        //Set color
        switch (fieldType) {
            case ENTRY_COLUMN_DATE:
                //NO color
                break;
            case ENTRY_COLUMN_TSLEEP:
            case ENTRY_COLUMN_TWAKEUP:
            case ENTRY_COLUMN_TCHANT:
                switch (colorIndex) {
                    //0:No color: Ignored
                    //1:Red
                    //2:yellow
                    //3:Green
                    case 0:
                        break;
                    case 1:
                        cell.setBackgroundColor(0x50FF0000);
                        break;
                    case 2:
                        cell.setBackgroundColor(0x70ffff00);
                        break;
                    case 3:
                        cell.setBackgroundColor(0x3000FF00);
                        break;

                }
                break;
            case ENTRY_COLUMN_CHANTRND:
                switch (colorIndex) {
                    /*
                    //0:No color: Ignored
                    //1:Red
                    //2:yellow
                    //3:Green
                    */
                    case 0:
                        break;
                    case 1:
                        cell.setBackgroundColor(0x50FF0000);
                        break;
                    case 2:
                        cell.setBackgroundColor(0x70ffff00);
                        break;
                    case 3:
                        cell.setBackgroundColor(0x3000FF00);
                        break;
                }
                break;

            case ENTRY_COLUMN_THEAR:
            case ENTRY_COLUMN_TREAD:
            case ENTRY_COLUMN_TSERVICE:
            case ENTRY_COLUMN_TDAYREST:
                switch (colorIndex) {
                    //0:No color: Ignored
                    //1:Red
                    //2:yellow
                    //3:Green
                    case 0:
                        break;
                    case 1:
                        cell.setBackgroundColor(0x50FF0000);
                        break;
                    case 2:
                        cell.setBackgroundColor(0x70ffff00);
                        break;
                    case 3:
                        cell.setBackgroundColor(0x3000FF00);
                        break;
                }
                break;

            case ENTRY_COLUMN_SCORE:
                switch (colorIndex) {

                    //0:Red
                    //1:Pink
                    //2:Yellow
                    //3:Green
                    case 0:
                        cell.setBackgroundColor(0x70FF0000);
                        break;
                    case 1:
                        cell.setBackgroundColor(0x30FF0000);
                        break;
                    case 2:
                        cell.setBackgroundColor(0x70ffff00);
                        break;
                    case 3:
                        cell.setBackgroundColor(0x3000FF00);
                        break;

                }
                break;

            case ENTRY_COLUMN_MANGAL:
                switch (colorIndex) {
                    //0:None
                    //1:Green
                    case 0:
                        break;
                    default:
                        cell.setBackgroundColor(0x3000FF00);
                        break;
                }
                break;
        }

        //Set Cell Width
        switch (fieldType) {
            case ENTRY_COLUMN_DATE:
                //NO color
                cellWidth += 6;
                break;
            case ENTRY_COLUMN_TSLEEP:
                //case ENTRY_COLUMN_TCHANT:
                cellWidth += 10;
                break;
            case ENTRY_COLUMN_TWAKEUP:
                cellWidth += 4;
                break;
            case ENTRY_COLUMN_CHANTRND:
                cellWidth += 10;
                break;

            case ENTRY_COLUMN_THEAR:
            case ENTRY_COLUMN_TREAD:
            case ENTRY_COLUMN_TSERVICE:
            case ENTRY_COLUMN_TDAYREST:
                cellWidth -= 0;
                break;

            case ENTRY_COLUMN_SCORE:
                cellWidth -= 10;
                break;

            case ENTRY_COLUMN_MANGAL:
                cellWidth -= 20;
                break;
        }
        cell.setWidth(cellWidth - 6);//6margin
    }

    //Callback function handler
    @Override
    public void onFragmentInteraction(String response) {

    }

    //Callback function handler
    @Override
    public void onHttpResponse(String response) {
        try {
            buttonDataSync.setEnabled(true);
            buttonDataSync.setText("SEND");
            if (response.startsWith("SuccessAddMessage:")) {
                int rcvrId = Integer.valueOf(response.substring("SuccessAddMessage:".length()));
                //Mark entry synced
                //DatabaseActions.markMessageEntrySynced(AppPreferences.user.id, dateIndex);
                Toast.makeText(getContext(), " Message Sent to " + friendInfo.name, Toast.LENGTH_LONG).show();
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

}
