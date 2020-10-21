package com.technicalround.iskconsadhanareport;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class ActivityAddDevotee extends AppCompatActivity implements InterfaceTaskCompleted {

    public static AppPreferences userPreferences;
    ArrayAdapter<String> dataAdapterCenter;
    ArrayList<String> centerList;
    boolean isProfilePage;
    private Spinner spinnerCountryList, spinnerCenterList, spinnerUserTypeList, spinnerGenderList;
    private ProgressBar progressBarNetwork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            isProfilePage = getIntent().getBooleanExtra("isProfilePage", false);
            setContentView(R.layout.activity_add_devotee);
            spinnerCountryList = (Spinner) findViewById(R.id.spinnerCountry);
            spinnerCenterList = (Spinner) findViewById(R.id.spinnerCentre);
            //spinnerCenterList.setVisibility(View.INVISIBLE);
            spinnerUserTypeList = (Spinner) findViewById(R.id.spinnerUserType);
            spinnerGenderList = (Spinner) findViewById(R.id.spinnerGender);
            progressBarNetwork = (ProgressBar) findViewById(R.id.progressBarNetwork);
            progressBarNetwork.setVisibility(View.INVISIBLE);

            centerList = new ArrayList<String>();
            dataAdapterCenter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_dropdown_item, centerList);
            spinnerCenterList.setAdapter(dataAdapterCenter);

            //Make fields uneditable if Profile Page
            if (isProfilePage) {
                findViewById(R.id.editTextEmail).setEnabled(false);
                findViewById(R.id.editTextEmail).clearFocus();
                // Check if no view has focus:
                View view = this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                //findViewById(R.id.textViewEmailHeading).requestFocus();
                LinearLayout linearLayoutRegFields = (LinearLayout) findViewById(R.id.linearLayoutRegFields);
                linearLayoutRegFields.removeView(findViewById(R.id.textViewHelp));
                ((Button) findViewById(R.id.button2)).setText("UPDATE");
                //Set default value
                ((EditText) findViewById(R.id.editTextEmail)).setText(userPreferences.user.email);
                ((EditText) findViewById(R.id.editTextName)).setText(userPreferences.user.name);
                ((EditText) findViewById(R.id.editTextPassword)).setText(userPreferences.user.pass);
                ((EditText) findViewById(R.id.editTextEmailCounselor)).setText(userPreferences.user.counselor);
                ((EditText) findViewById(R.id.editTextEmailFriend)).setText(userPreferences.user.friend);
                ((Spinner) findViewById(R.id.spinnerGender)).setSelection(userPreferences.user.gender);
                ((Spinner) findViewById(R.id.spinnerUserType)).setSelection(userPreferences.user.type);

                //Search for index of already filled country
                try {
                    for (int i = 0; i < (getResources().getStringArray(R.array.LIST_COUNTRIES)).length; i++) {
                        if ((userPreferences.user.country).equals((getResources().getStringArray(R.array.LIST_COUNTRIES))[i])) {
                            spinnerCountryList.setSelection(i);
                            break;
                        }
                    }
                } catch (Exception e) {
                }

                int value = userPreferences.user.defaultTWakeup / 10;
                ((EditText) findViewById(R.id.editTextTimeWakeupHr)).setText("" + value);
                value = (userPreferences.user.defaultTWakeup % 10) * 6;
                ((EditText) findViewById(R.id.editTextTimeWakeupMin)).setText("" + value);
                value = (userPreferences.user.defaultTSleep - 120) / 10;
                ((EditText) findViewById(R.id.editTextTimeSleepHr)).setText("" + value);
                value = ((userPreferences.user.defaultTSleep - 120) % 10) * 6;
                ((EditText) findViewById(R.id.editTextTimeSleepMin)).setText("" + value);
                ((EditText) findViewById(R.id.editTextTimeHearing)).setText("" + userPreferences.user.defaultTHear);
                ((EditText) findViewById(R.id.editTextTimeReading)).setText("" + userPreferences.user.defaultTRead);
                ((EditText) findViewById(R.id.editTextChantRounds)).setText("" + userPreferences.user.defaultChantRnd);
                ((EditText) findViewById(R.id.editTextTimeService)).setText("" + userPreferences.user.defaultTService);
                if (userPreferences.user.defaultTDayRest != 255)
                    ((EditText) findViewById(R.id.editTextTimeDayRest)).setText("" + userPreferences.user.defaultTDayRest);

            }

            //Now Call event listener for district select
            spinnerCountryList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    //usCountry = (String) spinnerDistrictList.getSelectedItem();
                    int usCountry = spinnerCountryList.getSelectedItemPosition();
                    centerList.clear();
                    centerList.add("All Centers");
                    if ((usCountry > 0) && (usCountry < 4)) { //India, UK, US
                        //Update CENTER list assigned to spinner adapter
                        if (usCountry == 1) {//India
                            for (int i = 0; i < (getResources().getStringArray(R.array.LIST_INDIA_CENTERS)).length; i++)
                                centerList.add(getResources().getStringArray(R.array.LIST_INDIA_CENTERS)[i]);
                        } else if (usCountry == 2) {//UK
                            for (int i = 0; i < (getResources().getStringArray(R.array.LIST_UK_CENTERS)).length; i++)
                                centerList.add(getResources().getStringArray(R.array.LIST_UK_CENTERS)[i]);
                        } else if (usCountry == 3) {//US
                            for (int i = 0; i < (getResources().getStringArray(R.array.LIST_USA_CENTERS)).length; i++)
                                centerList.add(getResources().getStringArray(R.array.LIST_USA_CENTERS)[i]);
                        }
                        //Toast.makeText(getApplicationContext(), "Select Center"+usCountry, Toast.LENGTH_SHORT).show();
                    }
                    dataAdapterCenter.notifyDataSetChanged();
                    if (isProfilePage)
                        spinnerCenterList.setSelection(dataAdapterCenter.getPosition(userPreferences.user.city));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
    }

    public void addNewDevotee(View view) {
        try {
            String link;
            if (!isProfilePage) {
                link = this.getString(R.string.WEB_ROOT) + "devotee.php?act=register";
            } else {
                link = this.getString(R.string.WEB_ROOT) + "devotee.php?act=update";
            }
            String data = "";

            String sEmail = ((EditText) findViewById(R.id.editTextEmail)).getText().toString();
            if (sEmail.isEmpty()) {
                Toast.makeText(ActivityAddDevotee.this, "Fill Your E-mail address", Toast.LENGTH_SHORT).show();
                return;
            } else sEmail = sEmail.replace(" ", "");//Remove Space

            data += "email=" + sEmail;
            String sName = ((EditText) findViewById(R.id.editTextName)).getText().toString();
            if (sName.isEmpty()) {
                Toast.makeText(ActivityAddDevotee.this, "Fill Your Name", Toast.LENGTH_SHORT).show();
                return;
            } else sName = sName.replace(" ", "");//Remove Space
            data += "&name=" + sName;
            String sPassword = ((EditText) findViewById(R.id.editTextPassword)).getText().toString();
            if (sPassword.isEmpty()) {
                Toast.makeText(ActivityAddDevotee.this, "Fill Your Password", Toast.LENGTH_SHORT).show();
                return;
            }
            data += "&pass=" + sPassword;
            String sEmailFriend = ((EditText) findViewById(R.id.editTextEmailFriend)).getText().toString();
            String sEmailCounselor = ((EditText) findViewById(R.id.editTextEmailCounselor)).getText().toString();
            sEmailCounselor = sEmailCounselor.replace(" ", "");//Remove Space
            sEmailFriend = sEmailFriend.replace(" ", "");//Remove Space
            //Remove self as counselor or friend
            if (sEmailCounselor.equalsIgnoreCase(sEmail)) sEmailCounselor = "";
            if (sEmailFriend.equalsIgnoreCase(sEmail)) sEmailFriend = "";
            data += "&frnd=" + sEmailFriend;
            data += "&cnslr=" + sEmailCounselor;
            if (sEmailCounselor.isEmpty() && sEmailFriend.isEmpty()) {
                Toast.makeText(ActivityAddDevotee.this, "Adding without Spiritual Guide or Friend !", Toast.LENGTH_SHORT).show();
            }

            int usType = spinnerUserTypeList.getSelectedItemPosition();
            if (usType < 1) {
                Toast.makeText(ActivityAddDevotee.this, "Fill User Type", Toast.LENGTH_SHORT).show();
                return;
            }
            data += "&type=" + usType;
            int value = spinnerGenderList.getSelectedItemPosition();
            data += "&gender=" + value;

            String text = (String) (spinnerCountryList.getSelectedItem());
            data += "&cntry=" + text;            //Country
            text = (String) (spinnerCenterList.getSelectedItem());
            data += "&city=" + text;            //City

            //WAKE-UP
            int timeHr = 3;
            String field = ((EditText) findViewById(R.id.editTextTimeWakeupHr)).getText().toString();
            if (!field.isEmpty()) {
                timeHr = Integer.parseInt(field);
            }
            if (timeHr > 12) {
                Toast.makeText(ActivityAddDevotee.this, "Invalid Wakeup Time:Hr", Toast.LENGTH_SHORT).show();
                return;
            }
            int timeMin = 30;
            field = ((EditText) findViewById(R.id.editTextTimeWakeupMin)).getText().toString();
            if (!field.isEmpty()) {
                timeMin = Integer.parseInt(field);
            }
            if (timeMin > 59) {
                Toast.makeText(ActivityAddDevotee.this, "Invalid Wakeup Time:Min", Toast.LENGTH_SHORT).show();
                return;
            }
            int timeCombinedIdx = timeHr * 10 + ((timeMin + 3) / 6);//Nearest minute based Time index
            data += "&wake=" + timeCombinedIdx;

            //SLEEP
            timeHr = 21;//9:30PM
            field = ((EditText) findViewById(R.id.editTextTimeSleepHr)).getText().toString();
            if (!field.isEmpty()) {
                timeHr = Integer.parseInt(field) % 12;
            }
            if (timeHr > 12) {
                Toast.makeText(ActivityAddDevotee.this, "Invalid Sleep Time:Hr", Toast.LENGTH_SHORT).show();
                return;
            }
            timeMin = 30;
            field = ((EditText) findViewById(R.id.editTextTimeSleepMin)).getText().toString();
            if (!field.isEmpty()) {
                timeMin = Integer.parseInt(field) % 60;
            }
            if (timeMin > 59) {
                Toast.makeText(ActivityAddDevotee.this, "Invalid Sleep Time:Min", Toast.LENGTH_SHORT).show();
                return;
            }
            timeCombinedIdx = timeHr * 10 + ((timeMin + 3) / 6) + 120;//Nearest minute based Time index. PM conversion
            data += "&sleep=" + timeCombinedIdx;

            int rounds = 0;
            field = ((EditText) findViewById(R.id.editTextChantRounds)).getText().toString();
            if (!field.isEmpty()) {
                rounds = Integer.parseInt(field);
            }
            rounds = (rounds > 16) ? 16 : rounds;
            data += "&rounds=" + rounds;

            int timeHear = 0;
            field = ((EditText) findViewById(R.id.editTextTimeHearing)).getText().toString();
            if (!field.isEmpty()) {
                timeHear = Integer.parseInt(field);
            }
            timeHear = (timeHear > 240) ? 240 : timeHear;
            data += "&hear=" + timeHear;

            int timeRead = 0;
            field = ((EditText) findViewById(R.id.editTextTimeReading)).getText().toString();
            if (!field.isEmpty()) {
                timeRead = Integer.parseInt(field);
            }
            timeRead = (timeRead > 240) ? 240 : timeRead;
            data += "&read=" + timeRead;

            int timeService = 0;
            field = ((EditText) findViewById(R.id.editTextTimeService)).getText().toString();
            if (!field.isEmpty()) {
                timeService = Integer.parseInt(field);
            }
            timeService = (timeService > 240) ? 240 : timeService;
            data += "&serv=" + timeService;

            int timeDayRest = 255;
            field = ((EditText) findViewById(R.id.editTextTimeDayRest)).getText().toString();
            if (!field.isEmpty()) {
                timeDayRest = Integer.parseInt(field);
                timeDayRest = (timeDayRest > 240) ? 240 : timeDayRest;
            }
            data += "&rest=" + timeDayRest;

            //Background task for Network activity
            /*
            new com.technicalround.iskconsadhanareport.ExecuteHttpGet(this, this, progressBarNetwork).execute(link);
            */
            new ExecuteHttpPost(this, this, progressBarNetwork).execute(link, data);

        } catch (Exception e) {
            Toast toast = Toast.makeText(this, e.toString(), Toast.LENGTH_LONG);
            toast.show();
            Log.d("ISS", e.toString());
            e.printStackTrace();
        }
    }

    public void startDownloadSchoolList(View view) {
        //toast.show();
    }

    //Callback function handler
    @Override
    public void onHttpResponse(String response) {
        try {

            //Log.d("Ashu:", "In callback listener in Add Devotee" + response);
            if (ActivityLogin.userLoginOnResponse(this, response)) {
                //Start main activity
                Intent intent = new Intent(this, ActivityMainDevotee.class);
                startActivity(intent);
                finish();
            }
        } catch (Exception e) {
            Log.d("ISS", "AddDevoteeHttpResponse: " + e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void onFragmentInteraction(String response) {
    }

    @Override
    public void onTimePickSelection(View view, int combinedTim, boolean delayIgnoreDialoge) {
    }

}
