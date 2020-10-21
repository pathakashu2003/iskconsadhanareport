package com.technicalround.iskconsadhanareport;

import android.database.Cursor;

import org.json.JSONObject;

/**
 * Created by Ashu Pathak on 3/6/2016.
 */
public class MessageEntry {
    public int autoId;
    public String sndrName;
    public int rcvrId;
    public short date;
    public short bIsSynced;
    public String msg;

    public MessageEntry() {

    }

    //This function returns the jSonAutoId to help server indicate delivery
    public static int saveJsonToMessageEntry(JSONObject jsonDataSingle) throws Exception {
        MessageEntry entry = new MessageEntry();
        entry.autoId = Integer.valueOf(jsonDataSingle.getString("_id"));
        entry.sndrName = jsonDataSingle.getString("sndrName");
        entry.rcvrId = Integer.valueOf(jsonDataSingle.getString("rcvrId"));
        entry.date = Integer.valueOf(jsonDataSingle.getString("date")).shortValue();
        entry.msg = jsonDataSingle.getString("msg");
        entry.bIsSynced = 1;

        //Save this entry to Local DB
        if (DatabaseActions.insertMessageEntry(entry) != -1)
            return entry.autoId;
        else
            return -1;
    }

    //Function to convert local database cursor to sadhana entry
    public static boolean cursorToMessageEntry(Cursor cursor, MessageEntry entry) {
        try {
            entry.autoId = cursor.getInt(0);
            entry.sndrName = cursor.getString(1);
            entry.rcvrId = cursor.getInt(2);
            entry.date = cursor.getShort(3);
            entry.msg = cursor.getString(4);
            entry.bIsSynced = (short) cursor.getShort(5);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
