package com.technicalround.iskconsadhanareport;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Ashu Pathak on 2/15/2016.
 */
public class DatabaseSadhana extends SQLiteOpenHelper {
    public static final String TABLE_SADHANA = "tbl_sadhana";
    public static final String TABLE_FRIENDS = "tbl_devotee";
    public static final String TABLE_MESSAGE = "tbl_inbox";
    public static final String COLUMN_AUTOID = "_id";
    public static final String COLUMN_USERID = "usId";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_COLORBITMAP = "colorBitmap";
    public static final String COLUMN_SCORE = "score";
    public static final String COLUMN_TSLEEP = "tSleep";
    public static final String COLUMN_TWAKEUP = "tWakeup";
    public static final String COLUMN_TCHANT = "tChant";
    public static final String COLUMN_CHANTRND = "chantRnd";
    public static final String COLUMN_CHANTMORN = "chantMorn";
    public static final String COLUMN_CHANTQ = "chantQ";
    public static final String COLUMN_MANGAL = "mangal";
    public static final String COLUMN_THEAR = "tHear";
    public static final String COLUMN_TREAD = "tRead";
    public static final String COLUMN_TSERVICE = "tService";
    public static final String COLUMN_TDAYREST = "tDayRest";
    public static final String COLUMN_PICVER = "picVer";
    public static final String COLUMN_THEAR2 = "tHear2";
    public static final String COLUMN_TSHLOKA = "tShloka";
    public static final String COLUMN_TOFFICE = "tOffice";
    public static final String COLUMN_COMMENTS = "comments";
    public static final String COLUMN_ISSYNCED = "bIsSynced";
    // Database creation sql statement
    public static final String DATABASE_CREATE_SADHANA = "CREATE TABLE IF NOT EXISTS " + TABLE_SADHANA + " ( " +
            COLUMN_AUTOID + " integer primary key autoincrement, " +
            COLUMN_USERID + " int, " +
            COLUMN_DATE + " smallint, " +
            COLUMN_COLORBITMAP + " smallint, " +
            COLUMN_SCORE + " tinyint, " +
            COLUMN_TSLEEP + " tinyint, " +
            COLUMN_TWAKEUP + " tinyint, " +
            COLUMN_TCHANT + " tinyint, " +
            COLUMN_CHANTRND + " tinyint, " +
            COLUMN_CHANTMORN + " tinyint, " +
            COLUMN_CHANTQ + " tinyint, " +
            COLUMN_MANGAL + " tinyint, " +
            COLUMN_THEAR + " tinyint, " +
            COLUMN_TREAD + " tinyint, " +
            COLUMN_TSERVICE + " tinyint, " +
            COLUMN_TDAYREST + " tinyint, " +
            COLUMN_THEAR2 + " tinyint, " +
            COLUMN_TSHLOKA + " tinyint, " +
            COLUMN_TOFFICE + " tinyint, " +
            COLUMN_ISSYNCED + " tinyint);";
    public static final String COLUMN_FRIENDID = "frId";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_COUNTRY = "country";
    public static final String COLUMN_CITY = "city";
    public static final String COLUMN_LASTONLINE = "lastOnline";
    public static final String COLUMN_REPORTSTATUS = "reportStatus";
    public static final String COLUMN_ISJUNIOR = "isJunior";
    // Database creation sql statement
    public static final String DATABASE_CREATE_FRIENDS = "CREATE TABLE IF NOT EXISTS " + TABLE_FRIENDS + " ( " +
            COLUMN_AUTOID + " integer primary key autoincrement, " +
            COLUMN_USERID + " int, " +
            COLUMN_FRIENDID + " int, " +
            COLUMN_NAME + " varchar(16), " +
            COLUMN_COUNTRY + " varchar(16), " +
            COLUMN_CITY + " varchar(16), " +
            COLUMN_ISJUNIOR + " tinyint, " +
            COLUMN_REPORTSTATUS + " smallint, " +
            COLUMN_LASTONLINE + " smallint, " +
            COLUMN_SCORE + " tinyint, " +
            COLUMN_TSLEEP + " tinyint, " +
            COLUMN_TWAKEUP + " tinyint, " +
            COLUMN_CHANTRND + " tinyint, " +
            COLUMN_THEAR + " tinyint, " +
            COLUMN_TREAD + " tinyint, " +
            COLUMN_TSERVICE + " tinyint, " +
            COLUMN_TDAYREST + " tinyint, " +
            COLUMN_PICVER + " tinyint);";
    public static final String COLUMN_SNDRNAME = "sndrName";
    public static final String COLUMN_RECEIVERID = "rcvrId";
    public static final String COLUMN_MSG = "msg";
    // Database creation sql statement
    public static final String DATABASE_CREATE_INBOX = "CREATE TABLE IF NOT EXISTS " + TABLE_MESSAGE + " ( " +
            COLUMN_AUTOID + " integer primary key autoincrement, " +
            COLUMN_SNDRNAME + " varchar(8), " +
            COLUMN_RECEIVERID + " int, " +
            COLUMN_DATE + " short, " +
            COLUMN_MSG + " varchar(128), " +
            COLUMN_ISSYNCED + " tinyint)";
    /*
    public static final String ALL_COLUMNS = "(" + COLUMN_AUTOID +","+ COLUMN_USERID+","+ COLUMN_DATE+
            "," + COLUMN_COLORBITMAP+"," + COLUMN_SCORE+","+ COLUMN_TSLEEP +","+COLUMN_TWAKEUP+"," +COLUMN_TCHANT+
            "," + COLUMN_CHANTRND+"," +COLUMN_CHANTMORN+"," +COLUMN_CHANTQ+","  +COLUMN_MANGAL+
            "," + COLUMN_THEAR+"," +COLUMN_TREAD+"," +COLUMN_TSERVICE+","+ COLUMN_TDAYREST+")";
    */
    private static final String DATABASE_NAME = "dtbs_iskconsadhanareport.db";
    private static final int DATABASE_VERSION = 5;


    public DatabaseSadhana(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        try {
            Log.d("ISS", "DATABASE_CREATE: onCreate:");
            database.execSQL(DATABASE_CREATE_SADHANA);
            database.execSQL(DATABASE_CREATE_FRIENDS);
            database.execSQL(DATABASE_CREATE_INBOX);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onOpen(SQLiteDatabase database) {
        try {
            Log.d("ISS", "DATABASE_CREATE: onOpen:");
            database.execSQL(DATABASE_CREATE_SADHANA);
            database.execSQL(DATABASE_CREATE_FRIENDS);
            database.execSQL(DATABASE_CREATE_INBOX);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            Log.w(DatabaseSadhana.class.getName(),
                    "Upgrading database from version " + oldVersion + " to "
                            + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SADHANA);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_FRIENDS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGE);
            onCreate(db);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
