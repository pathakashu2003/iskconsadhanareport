package com.technicalround.iskconsadhanareport;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

//import android.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link FragmentFillSadhanaQuick#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentFillSadhanaQuick extends Fragment
        implements InterfaceTaskCompleted {
    private static String[] string_LIST_HOURS;
    private static String[] string_LIST_MINUTES;
    private static String[] string_LIST_TIME_TYPES;
    private static String[] string_MONTH_NAMES;
    private static String[] string_MONTH_DAYS;
    private int timeIdxSleep = 255, timeIdxWakeup = 255, timeIdxChant = 255;
    private int defaultUserId, defaultTimeIdxSleep, defaultTimeIdxWakeup, defaultChantRnd, defaultChantMorn;
    private int defaultTimeRead, defaultTimeHear;
    private int marksSleep = -100, marksWakeup = -100;
    private int marksChant = 0, marksService = 0, score = 0;
    private String link, data;
    private TextView fillDate;
    private int dateOffset = 0;
    private short dateIndexSince2016 = 0;
    private ProgressBar httpProgressBar;

    private SadhanaEntry sadhanaEntry;
    private boolean isExistingEntry = false;

    private View fragView;
    private InterfaceTaskCompleted mListener;

    public FragmentFillSadhanaQuick() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentFillSadhana.
     */
    public static FragmentFillSadhanaQuick newInstance(String param1, String param2) {
        FragmentFillSadhanaQuick fragment = new FragmentFillSadhanaQuick();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            string_LIST_HOURS = getResources().getStringArray(R.array.string_LIST_HOURS);
            string_LIST_MINUTES = getResources().getStringArray(R.array.string_LIST_MINUTES);
            string_LIST_TIME_TYPES = getResources().getStringArray(R.array.string_LIST_TIME_TYPES);
            string_MONTH_NAMES = getResources().getStringArray(R.array.string_MONTH_NAMES);
            string_MONTH_DAYS = getResources().getStringArray(R.array.string_MONTH_DAYS);
            dateOffset = 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragView = inflater.inflate(R.layout.fragment_fill_sadhana_quick, container, false);
        try {
            //Bring today's date
            isExistingEntry = false;
            fillDate = (TextView) fragView.findViewById(R.id.textViewFillDate);
            fillDate.setText(printDateFromToday(dateOffset));
            httpProgressBar = (ProgressBar) fragView.findViewById(R.id.httpProgressBar);
            httpProgressBar.setVisibility(View.INVISIBLE);

            defaultUserId = AppPreferences.user.id;
            defaultTimeIdxSleep = AppPreferences.user.defaultTSleep;
            defaultTimeIdxWakeup = AppPreferences.user.defaultTWakeup;
            defaultChantRnd = AppPreferences.user.defaultChantRnd;
            defaultTimeHear = AppPreferences.user.defaultTHear;
            defaultTimeRead = AppPreferences.user.defaultTRead;

            defaultChantMorn = defaultChantRnd * 3 / 4;//75% chanting

            //Update Labels
            ((TextView) fragView.findViewById(R.id.textViewSleep)).setText(
                    "Last Night Slept before " + timeIndexToString(defaultTimeIdxSleep)
            );
            ((TextView) fragView.findViewById(R.id.textViewWakeup)).setText(
                    "Wake Up before " + timeIndexToString(defaultTimeIdxWakeup)
            );
            ((TextView) fragView.findViewById(R.id.textViewChantMorn)).setText(
                    "Chanted " + defaultChantMorn + " rounds before 9 AM"
            );
            ((TextView) fragView.findViewById(R.id.textViewHear)).setText(
                    "Heard Gurudev " + defaultTimeHear + " minutes"
            );
            ((TextView) fragView.findViewById(R.id.textViewRead)).setText(
                    "Read SP Book " + defaultTimeRead + " minutes"
            );

            //Previous and next date button listeners
            Button buttonPrevDate = (Button) fragView.findViewById(R.id.buttonPrevDate);
            buttonPrevDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dateOffset--;
                    fillDate.setText(printDateFromToday(dateOffset));
                    //Clear all fields
                    clearAllFillSadhanaFields(fragView);
                    //Update all fields with if any saved data
                    //Query if any existing entry for this date
                    loadExistingSadhanaFields(fragView);
                }
            });

            //Previous and next date button listeners
            Button buttonNextDate = (Button) fragView.findViewById(R.id.buttonNextDate);
            buttonNextDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dateOffset++;
                    fillDate.setText(printDateFromToday(dateOffset));
                    //Clear all fields
                    clearAllFillSadhanaFields(fragView);
                    //Update all fields with if any saved data
                    //Query if any existing entry for this date
                    loadExistingSadhanaFields(fragView);
                }
            });

            //Submit sadhana details listener
            Button buttonSubmit = (Button) fragView.findViewById(R.id.buttonSubmit);
            buttonSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        validateAndSubmitSadhanaFields();
                    } catch (Exception e) {
                        Log.d("ISS", "Submit Sadhana Exception: " + e.toString());
                        e.printStackTrace();
                    }
                }
            });

            sadhanaEntry = new SadhanaEntry();
            //Query if any existing entry for this date
            loadExistingSadhanaFields(fragView);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fragView;

    }

    private void validateAndSubmitSadhanaFields() throws Exception {
        try {
            int maxMarks = 0, marksChantQ = 0, marksMangal = 0, marksChant = 0,
                    marksDayRest = 0, total = 0,
                    marksRead = 0, marksHear = 0;

            link = getContext().getString(R.string.WEB_ROOT) + "sadhana.php?act=add";
            data = "usId=" + defaultUserId + "&date=" + dateIndexSince2016;
            sadhanaEntry.usId = defaultUserId;
            sadhanaEntry.date = dateIndexSince2016;
            sadhanaEntry.colorBitmap = 0;
            /**
             * Bitmap (16 bits): |Score(2)|DayRest(2)|Service(2)|Read(2)|Hear(2)|Chant(2)|Wake(2)|Sleep(2)|   LSB side
             */
            //Sleep
            if (((CheckBox) fragView.findViewById(R.id.checkBoxTimeSleep)).isChecked()) {
                marksSleep = 1;
                timeIdxSleep = defaultTimeIdxSleep;
            } else {
                marksSleep = 0;
                timeIdxSleep = 0;
            }
            data += "&tSleep=" + timeIdxSleep;
            maxMarks += 1;
            total += marksSleep;
            sadhanaEntry.colorBitmap |= marksSleep * 3;//2Bits
            sadhanaEntry.tSleep = (short) timeIdxSleep;

            //Wake up
            if (((CheckBox) fragView.findViewById(R.id.checkBoxTimeWakeup)).isChecked()) {
                marksWakeup = 1;
                timeIdxWakeup = defaultTimeIdxWakeup;
            } else {
                marksWakeup = 0;
                timeIdxWakeup = 0;
            }
            data += "&tWakeup=" + timeIdxWakeup;
            maxMarks += 1;
            total += marksWakeup;
            sadhanaEntry.colorBitmap |= (marksWakeup * 3) << 2;//2Bits
            sadhanaEntry.tWakeup = (short) timeIdxWakeup;

            //Chant time
            data += "&tChant=" + timeIdxChant;
            sadhanaEntry.tChant = (short) timeIdxChant;

            int count = 0;
            //Morning chanting
            if (((CheckBox) fragView.findViewById(R.id.checkBoxChantMorning)).isChecked()) {
                count = defaultChantMorn;//75% of total chanting
                marksChant++;
            }
            sadhanaEntry.chantMorn = (short) count;
            data += "&chantMorn=" + count;
            //Total  chanting
            if (((CheckBox) fragView.findViewById(R.id.checkBoxChantRounds)).isChecked()) {
                count = defaultChantRnd;
                marksChant++;
            }
            total += marksChant;
            maxMarks += 2;
            sadhanaEntry.chantRnd = (short) count;
            data += "&chantRnd=" + count;
            sadhanaEntry.colorBitmap |= ((marksChant + 1) & 0x03) << 4;//2Bits
            data += "&chantQ=" + 0;

            //Mangal Arti
            if (((CheckBox) fragView.findViewById(R.id.checkBoxMangal)).isChecked())
                marksMangal = 1;
            maxMarks += 2;
            total += marksMangal * 2;
            data += "&mangal=" + marksMangal;
            sadhanaEntry.mangal = (short) marksMangal;

            //Hearing
            int time = 0;
            if (((CheckBox) fragView.findViewById(R.id.checkBoxTimeHearing)).isChecked()) {
                time = defaultTimeHear;
                marksHear = 1;
            }
            sadhanaEntry.tHear = (short) time;
            data += "&tHear=" + time;
            maxMarks += 1;
            total += marksHear;
            sadhanaEntry.colorBitmap |= (marksHear * 3) << 6;//2Bits

            //Reading
            if (((CheckBox) fragView.findViewById(R.id.checkBoxTimeReading)).isChecked()) {
                time = defaultTimeRead;
                marksRead = 1;
            } else time = 0;
            sadhanaEntry.tRead = (short) time;
            data += "&tRead=" + time;
            maxMarks += 1;
            total += marksRead;
            sadhanaEntry.colorBitmap |= (marksRead * 3) << 8;//2Bits

            //Service
            String field = ((EditText) fragView.findViewById(R.id.editTextTimeService)).getText().toString();
            time = 0;
            if (!field.isEmpty()) time = Integer.valueOf(field);
            if (time > 24) {
                Toast.makeText(getContext(), "Service Time Incorrect. Put it in Hours.", Toast.LENGTH_SHORT).show();
                return;
            }
            sadhanaEntry.tService = (short) time;
            data += "&tService=" + time;

            //Time Waste
            sadhanaEntry.tDayRest = 255;
            data += "&tDayRest=" + 255;

            //Calculate score
            score = total * 100 / maxMarks;
            sadhanaEntry.colorBitmap |= (score / 26) << 14;//2Bits//Divide by 26 to handle 100
            int dateToday = sadhanaEntry.date - dateOffset;
            data += "&score=" + score + "&colorBitmap=" + sadhanaEntry.colorBitmap
                    + "&dateToday=" + dateToday;
            sadhanaEntry.score = (byte) score;
            sadhanaEntry.bIsSynced = 0;//Not synced till now
            ((TextView) fragView.findViewById(R.id.textViewScore)).setText("Score: " + score + "%");

            //Phone only fields
            //Gayatri
            if (((CheckBox) fragView.findViewById(R.id.checkBoxGayatri)).isChecked()) {
                time = 1;
            } else time = 0;
            sadhanaEntry.tHear2 = (short) time;

            //Shloka
            if (((CheckBox) fragView.findViewById(R.id.checkBoxTimeShloka)).isChecked()) {
                time = 1;
            } else time = 0;
            sadhanaEntry.tShloka = (short) time;

            sadhanaEntry.tOffice = (short) 255;

            //Execute HTTP Link, confirm if score
            // make a text input dialog and show it
            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
            alert.setTitle("SUBMIT REPORT");
            alert.setMessage("Score " + score + "%. Are you sure to SUBMIT REPORT?");
            alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //Save Data to local database
                    if (!isExistingEntry) {
                        if (DatabaseActions.insertSadhanaEntry(sadhanaEntry) > 0) {
                            Toast.makeText(getContext(), "Local Database: Add Entry Saved.", Toast.LENGTH_SHORT).show();
                            //Log.d("ISS", "Local Database: sadhanaEntry Saved.");
                            isExistingEntry = true;
                        } else {
                            Toast.makeText(getContext(), "Local Database: Add Entry Failed.", Toast.LENGTH_SHORT).show();
                            Log.d("ISS", "Local Database: sadhanaEntry Failed.");
                        }
                    } else {
                        if (DatabaseActions.updateSadhanaEntry(sadhanaEntry) > 0) {
                            Toast.makeText(getContext(), "Local Database: Entry Updated.", Toast.LENGTH_SHORT).show();
                            //Log.d("ISS", "Local Database: Entry Updated");
                        } else {
                            Toast.makeText(getContext(), "Local Database: Entry Update Failed.", Toast.LENGTH_SHORT).show();
                            Log.d("ISS", "Local Database: Entry Update Failed.");
                        }
                    }

                    //Send Data to Server
                    if (defaultUserId > 0) {
                        new ExecuteHttpPost(getContext(), FragmentFillSadhanaQuick.this,
                                httpProgressBar).execute(link, data);
                    } else {
                        Toast toast = Toast.makeText(getContext(), "GUEST user can't save data on INTERNET SERVER", Toast.LENGTH_SHORT);
                        toast.show();
                        //Log.d("ISS", "Guest not allowed to save data on INTERNET SERVER.");
                    }
                }
            });
            alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //Log.d("ISS", "no, don't submit");
                }
            });
            alert.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void clearAllFillSadhanaFields(View fragmentView) {
        try {
            ((EditText) fragmentView.findViewById(R.id.editTextTimeService)).setText("");

            ((CheckBox) fragmentView.findViewById(R.id.checkBoxTimeSleep)).setChecked(false);
            ((CheckBox) fragmentView.findViewById(R.id.checkBoxTimeWakeup)).setChecked(false);
            ((CheckBox) fragmentView.findViewById(R.id.checkBoxMangal)).setChecked(false);
            ((CheckBox) fragmentView.findViewById(R.id.checkBoxTimeHearing)).setChecked(false);
            ((CheckBox) fragmentView.findViewById(R.id.checkBoxTimeReading)).setChecked(false);
            ((CheckBox) fragmentView.findViewById(R.id.checkBoxChantMorning)).setChecked(false);
            ((CheckBox) fragmentView.findViewById(R.id.checkBoxChantRounds)).setChecked(false);
            ((CheckBox) fragmentView.findViewById(R.id.checkBoxGayatri)).setChecked(false);
            ((CheckBox) fragmentView.findViewById(R.id.checkBoxTimeShloka)).setChecked(false);

            //Clear various marks fields
            ((TextView) fragmentView.findViewById(R.id.textViewScore)).setText("Score: 0%");
        } catch (Exception e) {
            e.printStackTrace();
        }
        score = 0;
        marksSleep = -100;
        marksWakeup = -100;
        marksChant = -100;
        timeIdxSleep = 255;
        timeIdxWakeup = 255;
        timeIdxChant = 255;
    }

    /*Load Existing sadhana entry for this date*/
    public void loadExistingSadhanaFields(View fragmentView) {
        try {
            //Query if any existing entry for this date
            isExistingEntry = DatabaseActions.querySadhanaEntryForDate(defaultUserId, dateIndexSince2016, sadhanaEntry);
            if (isExistingEntry == false)
                return;

            //If entry exists then fill data in fields
            if (sadhanaEntry.tSleep > 0)
                ((CheckBox) fragmentView.findViewById(R.id.checkBoxTimeSleep)).setChecked(true);
            if (sadhanaEntry.tWakeup > 0)
                ((CheckBox) fragmentView.findViewById(R.id.checkBoxTimeWakeup)).setChecked(true);
            if (sadhanaEntry.mangal > 0)
                ((CheckBox) fragmentView.findViewById(R.id.checkBoxMangal)).setChecked(true);
            if (sadhanaEntry.tHear > 0)
                ((CheckBox) fragmentView.findViewById(R.id.checkBoxTimeHearing)).setChecked(true);
            if (sadhanaEntry.tRead > 0)
                ((CheckBox) fragmentView.findViewById(R.id.checkBoxTimeReading)).setChecked(true);
            if (sadhanaEntry.chantMorn > 0)
                ((CheckBox) fragmentView.findViewById(R.id.checkBoxChantMorning)).setChecked(true);
            if (sadhanaEntry.chantRnd > 0)
                ((CheckBox) fragmentView.findViewById(R.id.checkBoxChantRounds)).setChecked(true);
            if (sadhanaEntry.tHear2 > 0)
                ((CheckBox) fragmentView.findViewById(R.id.checkBoxGayatri)).setChecked(true);
            if (sadhanaEntry.tShloka > 0)
                ((CheckBox) fragmentView.findViewById(R.id.checkBoxTimeShloka)).setChecked(true);

            if (sadhanaEntry.tService > 0)
                ((EditText) fragmentView.findViewById(R.id.editTextTimeService)).setText("" + sadhanaEntry.tService);

            ((TextView) fragmentView.findViewById(R.id.textViewScore)).setText("Score: " + sadhanaEntry.score + "%");
        } catch (Exception e) {
            Log.d("ISS", "LoadExistingEntry: " + e.toString());
            e.printStackTrace();
        }

    }

    public String printDateFromToday(int offset) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.add(Calendar.DAY_OF_MONTH, offset);
        int year = calendar1.get(Calendar.YEAR);
        int month = calendar1.get(Calendar.MONTH) + 1;
        int date = calendar1.get(Calendar.DAY_OF_MONTH);
        int day = calendar1.get(Calendar.DAY_OF_WEEK);
        //Calculate date Index from 01/Jan/2016 considering 31 Days in each month.
        dateIndexSince2016 = (short) (((year - 2016) * 31 * 12) + ((month - 1) * 31) + (date - 1));
        //Log.d("ISS","printDateFromToday:"+year+month+day+dateIndexSince2016);
        return string_MONTH_DAYS[day - 1] + ", " + string_MONTH_NAMES[month - 1] + " " + date + ", " + year;
    }

    public String timeIndexToString(int combinedTimeIdx) throws Exception {
        int amPmIdx = combinedTimeIdx / 120;
        int hourIdx = (combinedTimeIdx - amPmIdx * 120) / 10;
        int minuteIdx = (combinedTimeIdx % 10);
        return (string_LIST_HOURS[hourIdx] + " : " + string_LIST_MINUTES[minuteIdx] + " " + string_LIST_TIME_TYPES[amPmIdx]);
    }

    // you can Rename method, update argument and hook method into UI event
    public void onButtonPressed(String response) {
        if (mListener != null) {
            mListener.onFragmentInteraction(response);
        }
    }

    //Callback function handler
    @Override
    public void onFragmentInteraction(String response) {

    }

    //Callback function handler
    @Override
    public void onHttpResponse(String response) {
        try {
            //Log.d("ISS", "submit:" + response);
            if (response.startsWith("SuccessAddSadhana:")) {
                //Toast.makeText(getContext(), response, Toast.LENGTH_SHORT).show();
                int dateIndex = Integer.valueOf(response.substring("SuccessAddSadhana:".length()));
                Toast.makeText(getContext(), ActivityMainDevotee.printDateFromDateIndex(dateIndex)
                        + " Entry Saved on Server", Toast.LENGTH_SHORT).show();
                //Mark entry synced
                DatabaseActions.markSadhanaEntrySynced(defaultUserId, dateIndex);
            } else if (response.startsWith("Error0:"))
                Toast.makeText(getContext(), "No Internet Connection !",
                        Toast.LENGTH_SHORT).show();
            else if (response.startsWith("Exception:"))
                Toast.makeText(getContext(), "Could not connect to Server, Try after some time",
                        Toast.LENGTH_SHORT).show();
            else if (response.startsWith("Error:"))
                Toast.makeText(getContext(), "Could not connect to Server, Try after some time",
                        Toast.LENGTH_SHORT).show();
            else Toast.makeText(getContext(), response, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.d("ISS", "FragmentFillSadhanaHttpResponse: " + e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void onTimePickSelection(View callerView, int combinedTimeIdx, boolean delayIgnoreDialog) {
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

}
