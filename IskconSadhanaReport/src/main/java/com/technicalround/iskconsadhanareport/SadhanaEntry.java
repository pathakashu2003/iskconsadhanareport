package com.technicalround.iskconsadhanareport;

import android.database.Cursor;

import org.json.JSONObject;

/**
 * Created by Ashu Pathak on 3/6/2016.
 */
public class SadhanaEntry {
    public int autoId;
    public int usId;
    public int date;
    public int colorBitmap;
    public int score;
    public short tSleep;
    public short tWakeup;
    public short tChant;
    public short chantRnd;
    public short chantMorn;
    public short chantQ;
    public short mangal;
    public short tHear;
    public short tRead;
    public short tService;
    public short tDayRest;
    public short tHear2;
    public short tShloka;
    public short tOffice;
    public short bIsSynced;

    public SadhanaEntry() {

    }

    //This function returns the jSonAutoId to help track maximum index tracked on server
    public static int saveJsonToSadhanaEntry(JSONObject jsonDataSingle) throws Exception {
        SadhanaEntry entry = new SadhanaEntry();
        entry.autoId = Integer.valueOf(jsonDataSingle.getString("_id"));
        entry.usId = Integer.valueOf(jsonDataSingle.getString("usId"));
        entry.date = Integer.valueOf(jsonDataSingle.getString("date"));
        entry.colorBitmap = Integer.valueOf(jsonDataSingle.getString("colorBitmap"));
        entry.score = Integer.valueOf(jsonDataSingle.getString("score"));
        entry.tSleep = Integer.valueOf(jsonDataSingle.getString("tSleep")).shortValue();
        entry.tWakeup = Integer.valueOf(jsonDataSingle.getString("tWakeup")).shortValue();
        entry.tChant = Integer.valueOf(jsonDataSingle.getString("tChant")).shortValue();
        entry.chantMorn = Integer.valueOf(jsonDataSingle.getString("chantMorn")).shortValue();
        entry.chantQ = Integer.valueOf(jsonDataSingle.getString("chantQ")).shortValue();
        entry.chantRnd = Integer.valueOf(jsonDataSingle.getString("chantRnd")).shortValue();
        entry.mangal = Integer.valueOf(jsonDataSingle.getString("mangal")).shortValue();
        entry.tHear = Integer.valueOf(jsonDataSingle.getString("tHear")).shortValue();
        entry.tRead = Integer.valueOf(jsonDataSingle.getString("tRead")).shortValue();
        entry.tService = Integer.valueOf(jsonDataSingle.getString("tService")).shortValue();
        entry.tDayRest = Integer.valueOf(jsonDataSingle.getString("tDayRest")).shortValue();
        entry.bIsSynced = 1;

        //Save this entry to Local DB
        DatabaseActions.insertSadhanaEntry(entry);

        return entry.autoId;
    }

    //Function to convert local database cursor to sadhana entry
    public static boolean cursorToSadhanaEntry(Cursor cursor, SadhanaEntry entry) {
        try {
            entry.autoId = cursor.getInt(0);
            entry.usId = cursor.getInt(1);
            entry.date = cursor.getInt(2);
            entry.colorBitmap = cursor.getInt(3);
            entry.score = cursor.getInt(4);
            entry.tSleep = (short) cursor.getShort(5);
            entry.tWakeup = (short) cursor.getShort(6);
            entry.tChant = (short) cursor.getShort(7);
            entry.chantRnd = (short) cursor.getShort(8);
            entry.chantMorn = (short) cursor.getShort(9);
            entry.chantQ = (short) cursor.getShort(10);
            entry.mangal = (short) cursor.getShort(11);
            entry.tHear = (short) cursor.getShort(12);
            entry.tRead = (short) cursor.getShort(13);
            entry.tService = (short) cursor.getShort(14);
            entry.tDayRest = (short) cursor.getShort(15);
            entry.tHear2 = (short) cursor.getShort(16);
            entry.tShloka = (short) cursor.getShort(17);
            entry.tOffice = (short) cursor.getShort(18);
            entry.bIsSynced = (short) cursor.getShort(19);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
