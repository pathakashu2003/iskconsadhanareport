package com.technicalround.iskconsadhanareport;

import android.database.Cursor;

import org.json.JSONObject;

public class DevoteeEntry {
    public static final short DEV_NO_REPORTING = 0;
    public static final short DEV_FRIEND_GETTING = 1;
    public static final short DEV_COUNSELOR_GETTING = 2;
    public static final short DEV_BOTH_GETTING = 3;

    public int id;
    public String email;
    public String pass;
    public String name;
    public String counselor;
    public String friend;
    public String country;
    public String city;
    public short type;
    public short gender;
    public short isJunior;
    public short reportStatus;
    public short lastOnline;
    public short score;
    public short defaultTSleep;
    public short defaultTWakeup;
    public short defaultChantRnd;
    public short defaultTHear;
    public short defaultTRead;
    public short defaultTService;
    public short defaultTDayRest;
    public int picVer;

    public DevoteeEntry() {

    }

    public static void saveJsonToDevoteeEntry(JSONObject jsonDataSingle, DevoteeEntry devotee, short isJunior) throws Exception {
        devotee.id = Integer.valueOf(jsonDataSingle.getString("dvAutoId"));
        devotee.email = jsonDataSingle.getString("dvEmail");
        //Log.d("ISS", "saveJsonToDevoteeEntry:" + devotee.email);
        devotee.pass = jsonDataSingle.getString("dvPassword");
        devotee.name = jsonDataSingle.getString("dvFName");
        devotee.counselor = jsonDataSingle.getString("dvCounselor");
        devotee.friend = jsonDataSingle.getString("dvFriend");
        devotee.country = jsonDataSingle.getString("dvCountry");
        devotee.city = jsonDataSingle.getString("dvCity");
        devotee.type = Integer.valueOf(jsonDataSingle.getString("dvType")).shortValue();
        devotee.gender = Integer.valueOf(jsonDataSingle.getString("dvGender")).shortValue();
        devotee.isJunior = isJunior;
        devotee.reportStatus = Integer.valueOf(jsonDataSingle.getString("dvReportStatus")).shortValue();
        devotee.lastOnline = Integer.valueOf(jsonDataSingle.getString("dvLastEntryDate")).shortValue();
        devotee.score = Integer.valueOf(jsonDataSingle.getString("dvScoreAvg")).shortValue();
        devotee.defaultTSleep = Integer.valueOf(jsonDataSingle.getString("dvTSleep")).shortValue();
        devotee.defaultTWakeup = Integer.valueOf(jsonDataSingle.getString("dvTWakeup")).shortValue();
        devotee.defaultChantRnd = Integer.valueOf(jsonDataSingle.getString("dvChant")).shortValue();
        devotee.defaultTHear = Integer.valueOf(jsonDataSingle.getString("dvTHear")).shortValue();
        devotee.defaultTRead = Integer.valueOf(jsonDataSingle.getString("dvTRead")).shortValue();
        devotee.defaultTService = Integer.valueOf(jsonDataSingle.getString("dvTServ")).shortValue();
        devotee.defaultTDayRest = Integer.valueOf(jsonDataSingle.getString("dvTDRest")).shortValue();
        devotee.picVer = Integer.valueOf(jsonDataSingle.getString("dvPicVer"));
    }

    public static void saveJsonToFriendEntry(int userId, JSONObject jsonDataSingle, short isJunior) throws Exception {
        DevoteeEntry devotee = new DevoteeEntry();
        devotee.id = Integer.valueOf(jsonDataSingle.getString("dvAutoId"));
        //devotee.email = jsonDataSingle.getString("dvEmail");
        devotee.name = jsonDataSingle.getString("dvFName");
        //devotee.counselor = jsonDataSingle.getString("dvCounselor");
        //devotee.friend = jsonDataSingle.getString("dvFriend");
        devotee.country = jsonDataSingle.getString("dvCountry");
        devotee.city = jsonDataSingle.getString("dvCity");
        //devotee.type = Integer.valueOf(jsonDataSingle.getString("dvType")).shortValue();
        devotee.isJunior = isJunior;
        try {devotee.reportStatus = Integer.valueOf(jsonDataSingle.getString("dvReportStatus")).shortValue();}catch(Exception e){devotee.reportStatus=0;}
        try {devotee.lastOnline = Integer.valueOf(jsonDataSingle.getString("dvLastEntryDate")).shortValue();}catch(Exception e){devotee.lastOnline=0;}
        try {devotee.score = Integer.valueOf(jsonDataSingle.getString("dvScoreAvg")).shortValue();}catch(Exception e){devotee.score=0;}
        try {devotee.defaultTSleep = Integer.valueOf(jsonDataSingle.getString("dvTSleep")).shortValue();}catch(Exception e){devotee.defaultTSleep=255;}
        try {devotee.defaultTWakeup = Integer.valueOf(jsonDataSingle.getString("dvTWakeup")).shortValue();}catch(Exception e){devotee.defaultTWakeup=255;}
        try {devotee.defaultChantRnd = Integer.valueOf(jsonDataSingle.getString("dvChant")).shortValue();}catch(Exception e){devotee.defaultChantRnd=0;}
        try {devotee.defaultTHear = Integer.valueOf(jsonDataSingle.getString("dvTHear")).shortValue();}catch(Exception e){devotee.defaultTHear=255;}
        try {devotee.defaultTRead = Integer.valueOf(jsonDataSingle.getString("dvTRead")).shortValue();}catch(Exception e){devotee.defaultTRead=255;}
        try {devotee.defaultTService = Integer.valueOf(jsonDataSingle.getString("dvTServ")).shortValue();}catch(Exception e){devotee.defaultTService=255;}
        try {devotee.defaultTDayRest = Integer.valueOf(jsonDataSingle.getString("dvTDRest")).shortValue();}catch(Exception e){devotee.defaultTDayRest=255;}
        try {devotee.picVer = Integer.valueOf(jsonDataSingle.getString("dvPicVer")).shortValue();}catch(Exception e){devotee.picVer=0;}

        //Save this entry to Local DB
        DatabaseActions.insertFriendEntry(userId, devotee);
    }

    //Function to convert local database cursor to devotee entry
    public static boolean cursorToFriendEntry(Cursor cursor, DevoteeEntry devotee) {
        try {
            devotee.id = cursor.getInt(2);
            devotee.name = cursor.getString(3);
            devotee.country = cursor.getString(4);
            devotee.city = cursor.getString(5);
            devotee.isJunior = cursor.getShort(6);
            devotee.reportStatus = (short) cursor.getShort(7);
            devotee.lastOnline = (short) cursor.getShort(8);
            devotee.score = (short) cursor.getShort(9);
            devotee.defaultTSleep = (short) cursor.getShort(10);
            devotee.defaultTWakeup = (short) cursor.getShort(11);
            devotee.defaultChantRnd = (short) cursor.getShort(12);
            devotee.defaultTHear = (short) cursor.getShort(13);
            devotee.defaultTRead = (short) cursor.getShort(14);
            devotee.defaultTService = (short) cursor.getShort(15);
            devotee.defaultTDayRest = (short) cursor.getShort(16);
            devotee.picVer = (short) cursor.getShort(17);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
