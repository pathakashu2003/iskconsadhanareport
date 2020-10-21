package com.technicalround.iskconsadhanareport;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONObject;

/**
 * Created by Ashu Pathak on 3/27/2016.
 */

public class AppPreferences {
    public static final String TAG = "ISS";
    public static final String US_PREFS = "userPrefsFile";
    public static final String US_LOGGED_ON = "userLoggedOn";
    public static final String US_REMEMBER_PASS = "userAskPass";
    public static final String US_ID = "usId";
    public static final String US_EMAIL = "email";
    public static final String US_PASSWORD = "password";
    public static final String US_NAME = "name";
    public static final String US_COUNSELOREMAIL = "counselorEmail";
    public static final String US_FRIENDEMAIL = "friendEmail";
    public static final String US_COUNTRY = "country";
    public static final String US_CITY = "city";
    public static final String US_TYPE = "type";
    public static final String US_REPORTSTATUS = "reportStatus";
    public static final String US_LASTONLINE = "lastOnline";
    public static final String US_SCORE = "score";
    public static final String US_DEFAULT_TSLEEP = "defaultTSleep";
    public static final String US_DEFAULT_TWAKEUP = "defaultTWakeup";
    public static final String US_DEFAULT_CHANT = "defaultChantRnd";
    public static final String US_DEFAULT_THEAR = "defaultTHear";
    public static final String US_DEFAULT_TREAD = "defaultTRead";
    public static final String US_DEFAULT_TSERV = "defaultTServ";
    public static final String US_DEFAULT_TDREST = "defaultTDayRest";
    public static final String US_DEFAULT_PICVER = "picVer";
    public static final String US_SYNCED_SADHANA_INDEX = "syncedIndex";
    public static final String US_SAVED_NEWS = "savedNews";
    public static final String DEVICERANDOMID= "iDeviceRandomId";
    public static final String SESSIONCOUNT= "iSessionCount";
    //public static final String NUM_FRIENDS_ACCEPTED = "numOfFriendsAccepted";
    public static boolean isUserLoginHistory = false;
    public static boolean isRememberPass = true;
    public static DevoteeEntry user;
    public static int syncedSadhanaIndex = 0;
    public static int iSessionCount = 0;
    public static String tSyncTime;
    public static int iDeviceRandomId;
    public static int homePageCount = 0;
    public static String savedNews = "No News !";

    public static AppPreferences appPreferences = null;

    private static SharedPreferences sharedPreferences;

    public AppPreferences(Context context) {
        init(context);
    }

    //Start appPreferences such that only one settings over app
    public static void start(Context context) {
        //If appPreferences is already initialized then don't reinitialize
        if (appPreferences == null) appPreferences = new AppPreferences(context);
    }

    public static void clean(Context context) {
        sharedPreferences = context.getSharedPreferences(US_PREFS, Context.MODE_PRIVATE);
        int iDeviceRandomId_keep = sharedPreferences.getInt(DEVICERANDOMID, 0);
        int iSessionCount_keep = sharedPreferences.getInt(SESSIONCOUNT, 0);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        //Now reapply same random value
        iDeviceRandomId = iDeviceRandomId_keep;
        iSessionCount = iSessionCount_keep;
        editor.putInt(DEVICERANDOMID, iDeviceRandomId);
        editor.putInt(SESSIONCOUNT, iSessionCount);
        editor.apply();

        //Init the local variables
        init(context);
    }

    public static void init(Context context) {
        sharedPreferences = context.getSharedPreferences(US_PREFS, Context.MODE_PRIVATE);

        isUserLoginHistory = sharedPreferences.getBoolean(US_LOGGED_ON, false);
        isRememberPass = sharedPreferences.getBoolean(US_REMEMBER_PASS, false);

        user = new DevoteeEntry();
        user.id = sharedPreferences.getInt(US_ID, 0);
        user.name = sharedPreferences.getString(US_NAME, null);
        user.email = sharedPreferences.getString(US_EMAIL, null);
        user.pass = sharedPreferences.getString(US_PASSWORD, null);
        user.counselor = sharedPreferences.getString(US_COUNSELOREMAIL, null);
        user.friend = sharedPreferences.getString(US_FRIENDEMAIL, null);
        user.type = (short) sharedPreferences.getInt(US_TYPE, 0);
        user.country = sharedPreferences.getString(US_COUNTRY, "India");
        user.city = sharedPreferences.getString(US_CITY, "Vrindavan");
        user.defaultTSleep = (short) sharedPreferences.getInt(US_DEFAULT_TSLEEP, 215);
        user.defaultTWakeup = (short) sharedPreferences.getInt(US_DEFAULT_TWAKEUP, 35);
        user.defaultChantRnd = (short) sharedPreferences.getInt(US_DEFAULT_CHANT, 16);
        user.defaultTHear = (short) sharedPreferences.getInt(US_DEFAULT_THEAR, 0);
        user.defaultTRead = (short) sharedPreferences.getInt(US_DEFAULT_TREAD, 0);
        user.defaultTService = (short) sharedPreferences.getInt(US_DEFAULT_TSERV, 0);
        user.defaultTDayRest = (short) sharedPreferences.getInt(US_DEFAULT_TDREST, 255);
        user.picVer = (short) sharedPreferences.getInt(US_DEFAULT_PICVER, 0);

        syncedSadhanaIndex = sharedPreferences.getInt(US_SYNCED_SADHANA_INDEX + user.email, 0);
        savedNews = sharedPreferences.getString(US_SAVED_NEWS, "No News !");
    }

    /**
     * guestUserLogin
     */
    public static void guestUserLogin() {
        isUserLoginHistory = false;
        user.id = 0;
        user.email = "guest@gmail.com";
        user.name = "Guest User";
        user.counselor = "";
        user.friend = "";
        user.type = 0;
        user.country = "India";
        user.city = "";
        user.defaultTSleep = 215;
        user.defaultTWakeup = 35;
        user.defaultChantRnd = 16;
        user.defaultTHear = 0;
        user.defaultTRead = 0;
        user.defaultTService = 0;
        user.defaultTDayRest = 255;
        user.picVer = 0;

        //Save info to permanent sharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(US_LOGGED_ON, false);
        editor.apply();

        // Save Guest user details in app preferences
        saveUserEntryToUserPref();
    }

    /**
     * guestUserLogin
     */
    public static void saveJsonToUserPref(JSONObject jsonDataSingle, short isJunior) throws Exception {
        isUserLoginHistory = true;
        DevoteeEntry.saveJsonToDevoteeEntry(jsonDataSingle, user, isJunior);
        saveUserEntryToUserPref();
    }

    public static void updatePicVer() {
        user.picVer++;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(US_DEFAULT_PICVER, user.picVer);
        editor.apply();
    }

    private static void saveUserEntryToUserPref() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(US_ID, user.id);
        editor.putString(US_EMAIL, user.email);
        editor.putString(US_NAME, user.name);
        editor.putString(US_COUNSELOREMAIL, user.counselor);
        editor.putString(US_FRIENDEMAIL, user.friend);
        editor.putInt(US_TYPE, user.type);
        editor.putString(US_PASSWORD, user.pass);
        editor.putString(US_COUNTRY, user.country);
        editor.putString(US_CITY, user.city);
        editor.putInt(US_REPORTSTATUS, user.reportStatus);
        editor.putInt(US_LASTONLINE, user.lastOnline);
        editor.putInt(US_SCORE, user.score);
        editor.putInt(US_DEFAULT_TSLEEP, user.defaultTSleep);
        editor.putInt(US_DEFAULT_TWAKEUP, user.defaultTWakeup);
        editor.putInt(US_DEFAULT_CHANT, user.defaultChantRnd);
        editor.putInt(US_DEFAULT_THEAR, user.defaultTHear);
        editor.putInt(US_DEFAULT_TREAD, user.defaultTRead);
        editor.putInt(US_DEFAULT_TSERV, user.defaultTService);
        editor.putInt(US_DEFAULT_TDREST, user.defaultTDayRest);
        editor.putInt(US_DEFAULT_PICVER, user.picVer);
        editor.putBoolean(US_LOGGED_ON, true);
        editor.apply();
    }

    public static void saveMaxJsonIndex(int jsonAutoIdMax) throws Exception {
        Log.d("ISS", "Update syncedSadhanaIndex to:" + jsonAutoIdMax);
        syncedSadhanaIndex = jsonAutoIdMax;
        //Save this to permanent place
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //Save user Index for all users comoing on phone so need to save key value with email
        editor.putInt(US_SYNCED_SADHANA_INDEX + user.email, jsonAutoIdMax);
        editor.apply();
    }

    public static void saveNews(String news) throws Exception {
        if (BuildConfig.DEBUG) Log.d("ISS", "News Updated:" + news);
        savedNews = news;
        //Save this to permanent place
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //Save news
        editor.putString(US_SAVED_NEWS, news);
        editor.apply();
    }

    public boolean isUserloggedOn() {
        return isUserLoginHistory;
    }

    public boolean isRememberPassword() {
        return isRememberPass;
    }

    public void saveRememberPassword(boolean isRememberPass1) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(US_REMEMBER_PASS, isRememberPass1);
        editor.apply();
    }
}
