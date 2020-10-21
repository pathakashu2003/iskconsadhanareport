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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

//import android.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link FragmentFillSadhana#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentFillSadhana extends Fragment
        implements InterfaceTaskCompleted {
    private static final int TEXT_VIEW_ID_SLEEP = 0;
    private static final int TEXT_VIEW_ID_WAKEUP = 1;
    private static final int TEXT_VIEW_ID_CHANTING = 2;
    private static String[] string_LIST_HOURS;
    private static String[] string_LIST_MINUTES;
    private static String[] string_LIST_TIME_TYPES;
    private static String[] string_MONTH_NAMES;
    private static String[] string_MONTH_DAYS;

    private int selectedElementId;
    private int timeIdxSleep = 255, timeIdxWakeup = 255, timeIdxChant = 255;
    private int defaultUserId, defaultTimeIdxSleep, defaultTimeIdxWakeup, defaultChantRnd;
    private int defaultTimeRead, defaultTimeHear, defaultTimeDayRest, defaultTimeService;
    private int marksSleep = -100, marksWakeup = -100, marksRead = 0, marksHear = 0;
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

    public FragmentFillSadhana() {
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
    public static FragmentFillSadhana newInstance(String param1, String param2) {
        FragmentFillSadhana fragment = new FragmentFillSadhana();
        return fragment;
    }

    public static String printDateForIndex(int dateIndex) {
        int yearIndex = dateIndex / (31 * 12);
        dateIndex = dateIndex - yearIndex * 31 * 12;
        int month = (dateIndex / 31) + 1;
        int date = (dateIndex % 31) + 1;
        int year = (yearIndex + 2016);
        return String.format("%d/%02d/%02d", year, month, date);
    }

    public static short dateIndexForToday() {
        Calendar calendar1 = Calendar.getInstance();
        int year = calendar1.get(Calendar.YEAR);
        int month = calendar1.get(Calendar.MONTH) + 1;
        int day = calendar1.get(Calendar.DAY_OF_MONTH);
        //Calculate date INdex from 01/Jan/2016 considering 31 Days in each month.
        return ((short) (((year - 2016) * 31 * 12) + ((month - 1) * 31) + (day - 1)));

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
        fragView = inflater.inflate(R.layout.fragment_fill_sadhana, container, false);
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
            defaultTimeService = AppPreferences.user.defaultTService;
            defaultTimeDayRest = AppPreferences.user.defaultTDayRest;

            //Submit set time text view listener
            TextView textViewTimeSleep = (TextView) fragView.findViewById(R.id.textViewTimeSleep);
            textViewTimeSleep.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Default time is 9:30pm so idx = 215, let's Sleep Text view id = 0
                    showDialogTimePicker(view, defaultTimeIdxSleep, TEXT_VIEW_ID_SLEEP);
                }
            });
            //Submit set time text view listener
            TextView textViewTimeWakeup = (TextView) fragView.findViewById(R.id.textViewTimeWakeup);
            textViewTimeWakeup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Default time is 3:30am so idx = 35
                    showDialogTimePicker(view, defaultTimeIdxWakeup, TEXT_VIEW_ID_WAKEUP);
                }
            });
            //Submit set time text view listener
            TextView textViewTimeChanting = (TextView) fragView.findViewById(R.id.textViewTimeChanting);
            textViewTimeChanting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Default time is 7:12am so idx = 72
                    showDialogTimePicker(view, 72, TEXT_VIEW_ID_CHANTING);
                }
            });

            //TODO:date click listener to select date directly

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

    @Override
    public void onResume() {
        super.onResume();
    }

    private void showDialogTimePicker(View view, int defaultCombinedTimeIdx, int elementId) {
        try {
            //Log.d("ISS", "going for TimePicker");
            selectedElementId = elementId;
            DialogTimePicker dialogTimePicker = new DialogTimePicker(getContext(), elementId, this,
                    view, defaultCombinedTimeIdx);
            dialogTimePicker.setTitle("Select Approx Time");
            //WindowManager.LayoutParams wmlp = dialogTimePicker.getWindow().getAttributes();
            //wmlp.gravity = Gravity.TOP | Gravity.CENTER;
            //wmlp.x = 0;   //x position
            //wmlp.y = 0;   //y position
            dialogTimePicker.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void validateAndSubmitSadhanaFields() throws Exception {
        try {
            int maxMarks = 0, marksChantQ = 0, marksMangal = 0, marksDayRest = 0, total = 0;

            link = getContext().getString(R.string.WEB_ROOT) + "sadhana.php?act=add";
            data = "usId=" + defaultUserId + "&date=" + dateIndexSince2016;
            sadhanaEntry.usId = defaultUserId;
            sadhanaEntry.date = dateIndexSince2016;
            sadhanaEntry.colorBitmap = 0;
            /**
             * Bitmap (16 bits): |Score(2)|DayRest(2)|Service(2)|Read(2)|Hear(2)|Chant(2)|Wake(2)|Sleep(2)|   LSB side
             */
            //Sleep
            data += "&tSleep=" + timeIdxSleep;
            if (marksSleep != -100) {
                maxMarks += 15;
                total += marksSleep;
                sadhanaEntry.colorBitmap |= ((marksSleep / 6) + 1) & 0x03;//2Bits
            } else
                ((TextView) getActivity().findViewById(R.id.textViewMarksSleep)).setText("IGNORED");
            sadhanaEntry.tSleep = (short) timeIdxSleep;

            //Wake up
            data += "&tWakeup=" + timeIdxWakeup;
            if (marksWakeup != -100) {
                maxMarks += 15;
                total += marksWakeup;
                sadhanaEntry.colorBitmap |= (((marksWakeup / 6) + 1) & 0x03) << 2;//2Bits
            } else
                ((TextView) getActivity().findViewById(R.id.textViewMarksWakeup)).setText("IGNORED");
            sadhanaEntry.tWakeup = (short) timeIdxWakeup;
            //Chant time
            data += "&tChant=" + timeIdxChant;
            sadhanaEntry.tChant = (short) timeIdxChant;
        /* //Marking to be done with Rounds in Morning
        if(marksChant != -100) {
            maxMarks += 15;
            total += marksChant;
        }*/

            //Total  chanting
            String field = ((EditText) getActivity().findViewById(R.id.editTextChantRounds)).getText().toString();
            int count = 0;
            if (!field.isEmpty()) count = Integer.valueOf(field);
            sadhanaEntry.chantRnd = (short) count;
            data += "&chantRnd=" + count;
            //Morning chanting
            field = ((EditText) getActivity().findViewById(R.id.editTextChantMorning)).getText().toString();
            count = 0;
            if (!field.isEmpty()) count = Integer.valueOf(field);
            sadhanaEntry.chantMorn = (short) count;
            if (count > 16) count = 16;
            if (defaultChantRnd > 0) {//Chanting Expected
                marksChant = count * 16 / defaultChantRnd;
                maxMarks += 16;
            }
            data += "&chantMorn=" + count;
            //Chant Morn should be less than chant total
            if (sadhanaEntry.chantMorn > sadhanaEntry.chantRnd) {
                Toast.makeText(getContext(), "Total chanting can't be less than chanting before 10AM.", Toast.LENGTH_SHORT).show();
                return;
            }

            //Chant quality
            int chantQ = ((Spinner) getActivity().findViewById(R.id.spinnerChantQuality)).getSelectedItemPosition();
            data += "&chantQ=" + chantQ;
            sadhanaEntry.chantQ = (short) chantQ;
            if (defaultChantRnd > 0) {//Chanting Expected
                marksChant += (chantQ - 1) * 3;
                maxMarks += 9;
                total += marksChant;
                sadhanaEntry.colorBitmap |= (((marksChant / 9) + 1) & 0x03) << 4;//2Bits
                ((TextView) getActivity().findViewById(R.id.textViewMarksChantQ)).setText(marksChant + "/25");
            } else
                ((TextView) getActivity().findViewById(R.id.textViewMarksChantQ)).setText("NO TARGET");

            //Mangal Arti
            if (((CheckBox) getActivity().findViewById(R.id.checkBoxMangal)).isChecked())
                marksMangal = 1;
            else marksMangal = 0;
            maxMarks += 10;
            total += marksMangal * 5;
            data += "&mangal=" + marksMangal;
            sadhanaEntry.mangal = (short) marksMangal;
            ((TextView) getActivity().findViewById(R.id.textViewMarksMangal)).setText((marksMangal * 5) + "/5");

            //Hearing
            field = ((EditText) getActivity().findViewById(R.id.editTextTimeHearing)).getText().toString();
            int time = 0;
            if (!field.isEmpty()) time = Integer.valueOf(field);
            if (time > 240) time = 240;
            sadhanaEntry.tHear = (short) time;
            data += "&tHear=" + time;
            if (defaultTimeHear > 0) {//Hearing Expected
                if (time > defaultTimeHear) marksHear = 15;
                else marksHear = time * 15 / defaultTimeHear;

                maxMarks += 15;
                total += marksHear;
                sadhanaEntry.colorBitmap |= (((marksHear / 6) + 1) & 0x03) << 6;//2Bits
                ((TextView) getActivity().findViewById(R.id.textViewMarksHear)).setText(marksHear + "/15");
            } else
                ((TextView) getActivity().findViewById(R.id.textViewMarksHear)).setText("NO TARGET");


            //Reading
            field = ((EditText) getActivity().findViewById(R.id.editTextTimeReading)).getText().toString();
            time = 0;
            if (!field.isEmpty()) time = Integer.valueOf(field);
            if (time > 240) time = 240;
            sadhanaEntry.tRead = (short) time;
            data += "&tRead=" + time;
            if (defaultTimeRead > 0) {//Reading Expected
                if (time > defaultTimeRead) marksRead = 15;
                else marksRead = time * 15 / defaultTimeRead;

                maxMarks += 15;
                total += marksRead;
                sadhanaEntry.colorBitmap |= (((marksRead / 6) + 1) & 0x03) << 8;//2Bits
                ((TextView) getActivity().findViewById(R.id.textViewMarksRead)).setText(marksRead + "/15");
            } else
                ((TextView) getActivity().findViewById(R.id.textViewMarksRead)).setText("NO TARGET");

            //Service
            field = ((EditText) getActivity().findViewById(R.id.editTextTimeService)).getText().toString();
            time = 0;
            if (!field.isEmpty()) time = Integer.valueOf(field);
            if (time > 240) time = 240;
            sadhanaEntry.tService = (short) time;
            data += "&tService=" + time;
            if (defaultTimeService > 0) {//Service Expected
                if (time > defaultTimeService) marksService = 10;
                else marksService = time * 10 / defaultTimeService;

                maxMarks += 10;
                total += marksService;
                sadhanaEntry.colorBitmap |= (((marksService / 4) + 1) & 0x03) << 10;//2Bits
                ((TextView) getActivity().findViewById(R.id.textViewMarksService)).setText(marksService + "/10");
            } else
                ((TextView) getActivity().findViewById(R.id.textViewMarksService)).setText("NO TARGET");


            //Time Waste
            field = ((EditText) getActivity().findViewById(R.id.editTextTimeDayRest)).getText().toString();
            time = 255;
            if (!field.isEmpty()) {
                time = Integer.valueOf(field);
                if (time > 240) time = 240;
                int extra = time - defaultTimeDayRest;
                if (extra <= 30) marksDayRest = 10;
                else if (extra <= 60) marksDayRest = 7;
                else if (extra <= 90) marksDayRest = 5;
                else if (extra <= 120) marksDayRest = 3;
                else if (extra <= 180) marksDayRest = 1;
                else marksDayRest = 0;
                maxMarks += 10;
                total += marksDayRest;
                sadhanaEntry.colorBitmap |= (((marksDayRest / 4) + 1) & 0x03) << 12;//2Bits
                ((TextView) getActivity().findViewById(R.id.textViewMarksDayRest)).setText(marksDayRest + "/10");
            } else
                ((TextView) getActivity().findViewById(R.id.textViewMarksDayRest)).setText("IGNORED");

            sadhanaEntry.tDayRest = (short) time;
            data += "&tDayRest=" + time;

            //Calculate score
            score = total * 100 / maxMarks;
            sadhanaEntry.colorBitmap |= (score / 26) << 14;//2Bits//Divide by 26 to handle 100
            int dateToday = sadhanaEntry.date - dateOffset;
            data += "&score=" + score + "&colorBitmap=" + sadhanaEntry.colorBitmap
                    + "&dateToday=" + dateToday;
            sadhanaEntry.score = (byte) score;
            sadhanaEntry.bIsSynced = 0;//Not synced till now
            ((TextView) getActivity().findViewById(R.id.textViewScore)).setText("Score: " + score + "%");

            //Phone only fields
            //Hearing-2
            field = ((EditText) getActivity().findViewById(R.id.editTextTimeHearing2)).getText().toString();
            time = 0;
            if (!field.isEmpty()) time = Integer.valueOf(field);
            if (time > 255) time = 255;
            sadhanaEntry.tHear2 = (short) time;

            //Shloka
            field = ((EditText) getActivity().findViewById(R.id.editTextTimeShloka)).getText().toString();
            time = 0;
            if (!field.isEmpty()) time = Integer.valueOf(field);
            if (time > 255) time = 255;
            sadhanaEntry.tShloka = (short) time;

            //Office
            field = ((EditText) getActivity().findViewById(R.id.editTextTimeOffice)).getText().toString();
            time = 0;
            if (!field.isEmpty()) time = Integer.valueOf(field);
            if (time > 255) time = 255;
            sadhanaEntry.tOffice = (short) time;

            //Execute HTTP Link, confirm if score
            // make a text input dialog and show it
            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
            alert.setTitle("Ignore Marks?");
            alert.setMessage("Score " + score + "%. Are you sure to SUBMIT REPORT?");
            alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //Log.d("ISS", data);

                    //Save Data to local database
                    if (isExistingEntry == false) {
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
                        new ExecuteHttpPost(getContext(), com.technicalround.iskconsadhanareport.FragmentFillSadhana.this,
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

            ((TextView) fragmentView.findViewById(R.id.textViewTimeSleep)).setText("Enter Time");
            ((TextView) fragmentView.findViewById(R.id.textViewTimeWakeup)).setText("Enter Time");
            ((TextView) fragmentView.findViewById(R.id.textViewTimeChanting)).setText("Enter Time");
            ((EditText) fragmentView.findViewById(R.id.editTextChantMorning)).setText("");
            ((EditText) fragmentView.findViewById(R.id.editTextChantRounds)).setText("");
            ((Spinner) fragmentView.findViewById(R.id.spinnerChantQuality)).setSelection(0);
            ((CheckBox) fragmentView.findViewById(R.id.checkBoxMangal)).setChecked(false);
            ((EditText) fragmentView.findViewById(R.id.editTextTimeHearing)).setText("");
            ((EditText) fragmentView.findViewById(R.id.editTextTimeReading)).setText("");
            ((EditText) fragmentView.findViewById(R.id.editTextTimeService)).setText("");
            ((EditText) fragmentView.findViewById(R.id.editTextTimeDayRest)).setText("");
            ((EditText) fragmentView.findViewById(R.id.editTextTimeHearing2)).setText("");
            ((EditText) fragmentView.findViewById(R.id.editTextTimeShloka)).setText("");
            ((EditText) fragmentView.findViewById(R.id.editTextTimeOffice)).setText("");
            //Clear various marks fields
            ((TextView) fragmentView.findViewById(R.id.textViewMarksChantQ)).setText("-");
            ((TextView) fragmentView.findViewById(R.id.textViewMarksHear)).setText("-");
            ((TextView) fragmentView.findViewById(R.id.textViewMarksRead)).setText("-");
            ((TextView) fragmentView.findViewById(R.id.textViewScore)).setText("Score: 0%");
            ((TextView) fragmentView.findViewById(R.id.textViewMarksService)).setText("-");
            ((TextView) fragmentView.findViewById(R.id.textViewMarksSleep)).setText("-");
            ((TextView) fragmentView.findViewById(R.id.textViewMarksWakeup)).setText("-");
            //((TextView) fragmentView.findViewById(R.id.textViewMarksChant)).setText("0");
            ((TextView) getActivity().findViewById(R.id.textViewMarksDayRest)).setText("-");
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
            //Log.d("ISS", "existing entry:" + sadhanaEntry.tSleep + sadhanaEntry.tWakeup);

            //Fill Sleep, wakeup and chanting time text boxes which are generally filled by click
            if (sadhanaEntry.tSleep != 255) {
                selectedElementId = TEXT_VIEW_ID_SLEEP;
                onTimePickSelection(fragmentView.findViewById(R.id.textViewTimeSleep), sadhanaEntry.tSleep, false);
            }

            if (sadhanaEntry.tWakeup != 255) {
                selectedElementId = TEXT_VIEW_ID_WAKEUP;
                onTimePickSelection(fragmentView.findViewById(R.id.textViewTimeWakeup), sadhanaEntry.tWakeup, false);
            }

            if (sadhanaEntry.tChant != 255) {
                selectedElementId = TEXT_VIEW_ID_CHANTING;
                onTimePickSelection(fragmentView.findViewById(R.id.textViewTimeChanting), sadhanaEntry.tChant, false);
            }
            if (sadhanaEntry.chantMorn > 0)
                ((EditText) fragmentView.findViewById(R.id.editTextChantMorning)).setText("" + sadhanaEntry.chantMorn);
            if (sadhanaEntry.chantRnd > 0)
                ((EditText) fragmentView.findViewById(R.id.editTextChantRounds)).setText("" + sadhanaEntry.chantRnd);
            if (sadhanaEntry.chantQ > 0)
                ((Spinner) fragmentView.findViewById(R.id.spinnerChantQuality)).setSelection(sadhanaEntry.chantQ);
            if (sadhanaEntry.mangal > 0)
                ((CheckBox) fragmentView.findViewById(R.id.checkBoxMangal)).setChecked(true);
            if (sadhanaEntry.tHear > 0)
                ((EditText) fragmentView.findViewById(R.id.editTextTimeHearing)).setText("" + sadhanaEntry.tHear);
            if (sadhanaEntry.tRead > 0)
                ((EditText) fragmentView.findViewById(R.id.editTextTimeReading)).setText("" + sadhanaEntry.tRead);
            if (sadhanaEntry.tService > 0)
                ((EditText) fragmentView.findViewById(R.id.editTextTimeService)).setText("" + sadhanaEntry.tService);
            if (sadhanaEntry.tDayRest != 255)
                ((EditText) fragmentView.findViewById(R.id.editTextTimeDayRest)).setText("" + sadhanaEntry.tDayRest);

            if (sadhanaEntry.tHear2 > 0)
                ((EditText) fragmentView.findViewById(R.id.editTextTimeHearing2)).setText("" + sadhanaEntry.tHear2);
            if (sadhanaEntry.tShloka > 0)
                ((EditText) fragmentView.findViewById(R.id.editTextTimeShloka)).setText("" + sadhanaEntry.tShloka);
            if (sadhanaEntry.tOffice > 0)
                ((EditText) fragmentView.findViewById(R.id.editTextTimeOffice)).setText("" + sadhanaEntry.tOffice);

            ((TextView) fragmentView.findViewById(R.id.textViewScore)).setText("Score: " + sadhanaEntry.score + "%");
        } catch (Exception e) {
            Log.d("ISS", "LoadExistingEntry: " + e.toString());
            e.printStackTrace();
        }

    }

    public String printDate(int day, int month, int year) {
        String[] string_MONTH_NAMES = getResources().getStringArray(R.array.string_MONTH_NAMES);
        return string_MONTH_NAMES[month - 1] + "" + day + ", " + year;
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
        //Log.d("ISS", "Return from TimePicker" + combinedTimeIdx);
        try {
            convertTimePickToString(callerView, combinedTimeIdx);

            //Store this timeIdx based on elementId
            if (selectedElementId == TEXT_VIEW_ID_SLEEP) {
                timeIdxSleep = combinedTimeIdx;
                //First handle if slept after mid-night
                if (timeIdxSleep < 120) timeIdxSleep += 240;//Reached in next date

                int delay = (timeIdxSleep - defaultTimeIdxSleep) * 6;
                if (delay < 15) marksSleep = 15;//Slept before 15min late
                else if (delay <= 30) marksSleep = 12;
                else if (delay <= 60) marksSleep = 9;
                else if (delay <= 90) marksSleep = 6;
                else if (delay <= 120) marksSleep = 3;
                else marksSleep = 0;
                ((TextView) fragView.findViewById(R.id.textViewMarksSleep)).setText(marksSleep + "/15");
                if ((delay > 89) && delayIgnoreDialog) {
                    //confirm whether to ignore these marks
                    dialogIgnoreMarks(delay);
                }

                //Bring back timeIndexSleep to below 240
                if (timeIdxSleep >= 240) timeIdxSleep -= 240;//Reached in next date

            } else if (selectedElementId == TEXT_VIEW_ID_WAKEUP) {
                timeIdxWakeup = combinedTimeIdx;
                int delay = (timeIdxWakeup - defaultTimeIdxWakeup) * 6;
                if (delay < 15) marksWakeup = 15;//Slept before 15min late
                else if (delay <= 30) marksWakeup = 12;
                else if (delay <= 60) marksWakeup = 9;
                else if (delay <= 90) marksWakeup = 6;
                else if (delay <= 120) marksWakeup = 3;
                else marksWakeup = 0;
                ((TextView) fragView.findViewById(R.id.textViewMarksWakeup)).setText(marksWakeup + "/15");
                if ((delay > 89) && delayIgnoreDialog) {
                    //confirm whether to ignore these marks
                    dialogIgnoreMarks(delay);
                }
            } else if (selectedElementId == TEXT_VIEW_ID_CHANTING) {
                timeIdxChant = combinedTimeIdx;
                /*
                if (timeIdxChant < 100) marksChant = 15;//Before 10AM
                else if (timeIdxChant <= 140) marksChant = 10;
                else if (timeIdxChant <= 180) marksChant = 5;
                else marksChant = 0;
                ((TextView) fragView.findViewById(R.id.textViewMarksChant)).setText("NOT USED");
                */
            }
        } catch (Exception e) {
            Log.d("ISS", "onTimepickSelection: " + e.toString());
            e.printStackTrace();
        }
    }

    public void convertTimePickToString(View callerView, int combinedTimeIdx) throws Exception {
        //Log.d("ISS","Return from TimePicker"+combinedTimeIdx);
        int amPmIdx = combinedTimeIdx / 120;
        int hourIdx = (combinedTimeIdx - amPmIdx * 120) / 10;
        int minuteIdx = (combinedTimeIdx % 10);
        ((TextView) callerView).setText(string_LIST_HOURS[hourIdx] + " : " + string_LIST_MINUTES[minuteIdx] + " " + string_LIST_TIME_TYPES[amPmIdx]);
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

    private void dialogIgnoreMarks(int delay) {
        try {
            // make a text input dialog and show it
            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
            alert.setTitle("Ignore Marks?");
            alert.setMessage("Dear Devotee, you are " + delay + " min late than Ideal time. Any URGENT/CRITICAL reason?");
            alert.setPositiveButton("Yes, Ignore marks", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //Log.d("ISS", "yes ignore marks");
                    if (selectedElementId == TEXT_VIEW_ID_SLEEP) {
                        marksSleep = -100;
                        ((TextView) fragView.findViewById(R.id.textViewMarksSleep)).setText("IGNORED");
                    } else if (selectedElementId == TEXT_VIEW_ID_WAKEUP) {
                        marksWakeup = -100;
                        ((TextView) fragView.findViewById(R.id.textViewMarksWakeup)).setText("IGNORED");
                    }
                    //else if (selectedElementId == TEXT_VIEW_ID_CHANTING) marksChant = -100;
                }
            });
            alert.setNegativeButton("No, My mistake", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //Log.d("ISS", "no my mistake");
                }
            });
            alert.show();
        } catch (Exception e) {
            Log.d("ISS", "dialogIgnoreMarks: " + e.toString());
            e.printStackTrace();
        }
    }
}
