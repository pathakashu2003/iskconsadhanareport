/**
 * Created by Ashu Pathak on 3/6/2016.
 */
package com.technicalround.iskconsadhanareport;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DatabaseActions {

    // Database fields
    private static SQLiteDatabase database;
    private static DatabaseSadhana dbHelper;

    //private String[] ALL_COLUMNS = { DatabaseSadhana.COLUMN_ID, DatabaseSadhana.COLUMN_sadhanaEntry };

    public DatabaseActions(Context context) {
        dbHelper = new DatabaseSadhana(context);
    }

    public static void open() throws SQLException {
        try {
            database = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void close() {
        dbHelper.close();
        //Log.d("ISS", "dbHelper: close");
    }

    public static int insertSadhanaEntry(SadhanaEntry entry) {
        //Log.d("ISS", "" + database.isOpen());
        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseSadhana.COLUMN_USERID, entry.usId);
            values.put(DatabaseSadhana.COLUMN_DATE, entry.date);
            values.put(DatabaseSadhana.COLUMN_COLORBITMAP, entry.colorBitmap);
            values.put(DatabaseSadhana.COLUMN_SCORE, entry.score);
            values.put(DatabaseSadhana.COLUMN_TSLEEP, entry.tSleep);
            values.put(DatabaseSadhana.COLUMN_TWAKEUP, entry.tWakeup);
            values.put(DatabaseSadhana.COLUMN_TCHANT, entry.tChant);
            values.put(DatabaseSadhana.COLUMN_CHANTRND, entry.chantRnd);
            values.put(DatabaseSadhana.COLUMN_CHANTMORN, entry.chantMorn);
            values.put(DatabaseSadhana.COLUMN_CHANTQ, entry.chantQ);
            values.put(DatabaseSadhana.COLUMN_MANGAL, entry.mangal);
            values.put(DatabaseSadhana.COLUMN_THEAR, entry.tHear);
            values.put(DatabaseSadhana.COLUMN_TREAD, entry.tRead);
            values.put(DatabaseSadhana.COLUMN_TSERVICE, entry.tService);
            values.put(DatabaseSadhana.COLUMN_TDAYREST, entry.tDayRest);
            values.put(DatabaseSadhana.COLUMN_THEAR2, entry.tHear2);
            values.put(DatabaseSadhana.COLUMN_TSHLOKA, entry.tShloka);
            values.put(DatabaseSadhana.COLUMN_TOFFICE, entry.tOffice);
            values.put(DatabaseSadhana.COLUMN_ISSYNCED, entry.bIsSynced);
            long insertId = database.insert(DatabaseSadhana.TABLE_SADHANA, null,
                    values);
            //Log.d("ISS", "insertId:" + insertId);
            //Check whether query successfull
            return (int) insertId;
        } catch (Exception e) {
            Log.d("ISS", "insertSadhanaEntry: " + e.toString());
            e.printStackTrace();
            return 0;
        }
    }

    public static boolean querySadhanaEntryForDate(int userId, int dateIndexSince2016, SadhanaEntry outSadhanaEntry) {
        try {
            boolean isExistingEntry = false;
            Cursor cursor = database.query(DatabaseSadhana.TABLE_SADHANA,
                    null, /*all columns*/
                    DatabaseSadhana.COLUMN_USERID + "=" + userId + " AND " + DatabaseSadhana.COLUMN_DATE + "=" + dateIndexSince2016,
                    null, null, null, null);
            //Log.d("ISS", "search entry for userId:"+userId+"dateindex"+dateIndexSince2016+"getcount:"+cursor.getCount());
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                isExistingEntry = SadhanaEntry.cursorToSadhanaEntry(cursor, outSadhanaEntry);
                cursor.close();
                //Log.d("ISS", "existingSadhanaEntry colorBitmap:" + outSadhanaEntry.colorBitmap);
                return isExistingEntry;
            } else
                return isExistingEntry;
        } catch (Exception e) {
            Log.d("ISS", "querySadhanaEntryForDate: " + e.toString());
            e.printStackTrace();
            return false;
        }
    }

    /*Query last n sadhana entries for a user*/
    public static int queryLastNSadhanaEntry(int userId, int numOfPrevEntries, SadhanaEntry[] outSadhanaEntry) {
        try {
            int entriesRead = 0;
            /*query (String table, String[] columns, String selection, String[] selectionArgs, String groupBy,
                    String having, String orderBy, String limit)*/
            Cursor cursor = database.query(DatabaseSadhana.TABLE_SADHANA,
                    null, /*all columns*/
                    DatabaseSadhana.COLUMN_USERID + "=" + userId,
                    null, null, null, DatabaseSadhana.COLUMN_DATE + " DESC ", "" + numOfPrevEntries);

            //Log.d("ISS", "search entry for userId:" + userId + "dateindex" + numOfPrevEntries + "getcount:" + cursor.getCount());
            //SadhanaEntry singleEntry = new SadhanaEntry();
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {

                    SadhanaEntry.cursorToSadhanaEntry(cursor, outSadhanaEntry[entriesRead]);
                    //outSadhanaEntry[i]=(singleEntry);
                    //Log.d("ISS", "found Entry date:" + outSadhanaEntry[entriesRead].date);
                    cursor.moveToNext();
                    entriesRead++;
                }
                cursor.close();
                //Log.d("ISS", "found sadhanaEntries count:" + cursor.getCount());
            }

            return cursor.getCount();
        } catch (Exception e) {
            Log.d("ISS", "queryLastNSadhanaEntry: " + e.toString());
            e.printStackTrace();
            return 0;
        }
    }

    public static int queryCountUnSyncedSadhanaEntries(int userId) {
        try {
            Cursor cursor = database.query(DatabaseSadhana.TABLE_SADHANA,
                    null, /*all columns*/
                    DatabaseSadhana.COLUMN_USERID + "=" + userId + " AND " + DatabaseSadhana.COLUMN_ISSYNCED + "=" + 0,
                    null, null, null, null);
            //Log.d("ISS", "search entry for userId:"+userId+"dateindex"+dateIndexSince2016+"getcount:"+cursor.getCount());
            return cursor.getCount();
        } catch (Exception e) {
            Log.d("ISS", "querySadhanaEntryForDate: " + e.toString());
            e.printStackTrace();
            return 0;
        }
    }

    public static int queryRecentUnSyncedSadhanaEntries(int userId, SadhanaEntry[] outSadhanaEntry) {
        try {
            int entriesRead = 0;
            Cursor cursor = database.query(DatabaseSadhana.TABLE_SADHANA,
                    null, /*all columns*/
                    DatabaseSadhana.COLUMN_USERID + "=" + userId + " AND " + DatabaseSadhana.COLUMN_ISSYNCED + "=" + 0,
                    null, null, null, DatabaseSadhana.COLUMN_DATE + " DESC ", "" + 3);
            //Log.d("ISS", "search entry for userId:"+userId+"dateindex"+dateIndexSince2016+"getcount:"+cursor.getCount());
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    SadhanaEntry.cursorToSadhanaEntry(cursor, outSadhanaEntry[entriesRead]);
                    cursor.moveToNext();
                    entriesRead++;
                }
                cursor.close();
                return entriesRead;
            } else
                return 0;
        } catch (Exception e) {
            Log.d("ISS", "querySadhanaEntryForDate: " + e.toString());
            e.printStackTrace();
            return 0;
        }
    }

    /*Update existing shadhana entry*/
    public static int updateSadhanaEntry(SadhanaEntry entry) {
        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseSadhana.COLUMN_USERID, entry.usId);
            values.put(DatabaseSadhana.COLUMN_DATE, entry.date);
            values.put(DatabaseSadhana.COLUMN_COLORBITMAP, entry.colorBitmap);
            values.put(DatabaseSadhana.COLUMN_SCORE, entry.score);
            values.put(DatabaseSadhana.COLUMN_TSLEEP, entry.tSleep);
            values.put(DatabaseSadhana.COLUMN_TWAKEUP, entry.tWakeup);
            values.put(DatabaseSadhana.COLUMN_TCHANT, entry.tChant);
            values.put(DatabaseSadhana.COLUMN_CHANTRND, entry.chantRnd);
            values.put(DatabaseSadhana.COLUMN_CHANTMORN, entry.chantMorn);
            values.put(DatabaseSadhana.COLUMN_CHANTQ, entry.chantQ);
            values.put(DatabaseSadhana.COLUMN_MANGAL, entry.mangal);
            values.put(DatabaseSadhana.COLUMN_THEAR, entry.tHear);
            values.put(DatabaseSadhana.COLUMN_TREAD, entry.tRead);
            values.put(DatabaseSadhana.COLUMN_TSERVICE, entry.tService);
            values.put(DatabaseSadhana.COLUMN_TDAYREST, entry.tDayRest);
            values.put(DatabaseSadhana.COLUMN_THEAR2, entry.tHear2);
            values.put(DatabaseSadhana.COLUMN_TSHLOKA, entry.tShloka);
            values.put(DatabaseSadhana.COLUMN_TOFFICE, entry.tOffice);
            values.put(DatabaseSadhana.COLUMN_ISSYNCED, entry.bIsSynced);

            int numRowsAffected = database.update(DatabaseSadhana.TABLE_SADHANA,
                    values, DatabaseSadhana.COLUMN_AUTOID + "=" + entry.autoId, null);
            //Log.d("ISS","updateId:"+entry.autoId+"Success="+numRowsAffected);
            //Check whether query successfull
            return numRowsAffected;
        } catch (Exception e) {
            Log.d("ISS", "updateSadhanaEntry: " + e.toString());
            e.printStackTrace();
            return 0;
        }

    }

    /*Update existing shadhana entry*/
    public static int markSadhanaEntrySynced(int usId, int date) {
        try {
            ContentValues values = new ContentValues();
            //values.put(DatabaseSadhana.COLUMN_USERID, usId);
            //values.put(DatabaseSadhana.COLUMN_DATE, date);
            values.put(DatabaseSadhana.COLUMN_ISSYNCED, 1);

            int numRowsAffected = database.update(DatabaseSadhana.TABLE_SADHANA,
                    values, DatabaseSadhana.COLUMN_USERID + "=" + usId + " AND " + DatabaseSadhana.COLUMN_DATE + "=" + date, null);
            if (BuildConfig.DEBUG) Log.d("ISS", "Entry Synced:" + date + "Success=" + numRowsAffected);
            //Check whether query successful
            return numRowsAffected;
        } catch (Exception e) {
            Log.d("ISS", "SadhanaEntrySynced: " + e.toString());
            e.printStackTrace();
            return 0;
        }

    }

    /*
    public void deletesadhanaEntry(SadhanaEntry SadhanaEntry) {
        long id = SadhanaEntry.getId();
        System.out.println("SadhanaEntry deleted with id: " + id);
        database.delete(DatabaseSadhana.TABLE_SADHANA, DatabaseSadhana.COLUMN_ID
                + " = " + id, null);
    }

    public List<SadhanaEntry> getAllsadhanaEntrys() {
        List<SadhanaEntry> sadhanaEntrys = new ArrayList<SadhanaEntry>();

        Cursor cursor = database.query(DatabaseSadhana.TABLE_SADHANA,
                DatabaseSadhana.ALL_COLUMNS, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            SadhanaEntry SadhanaEntry = SadhanaEntry.cursorTosadhanaEntry(cursor);
            sadhanaEntrys.add(SadhanaEntry);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return sadhanaEntrys;
    }
*/

    public static int insertFriendEntry(int userId, DevoteeEntry devotee) {
        //Log.d("ISS", "" + database.isOpen());
        //Log.d("ISS", "DATABASE_CREATE1: sqlQuery1:" + DatabaseSadhana.DATABASE_CREATE);
        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseSadhana.COLUMN_USERID, userId);
            values.put(DatabaseSadhana.COLUMN_FRIENDID, devotee.id);
            values.put(DatabaseSadhana.COLUMN_NAME, devotee.name);
            values.put(DatabaseSadhana.COLUMN_COUNTRY, devotee.country);
            values.put(DatabaseSadhana.COLUMN_CITY, devotee.city);
            values.put(DatabaseSadhana.COLUMN_REPORTSTATUS, devotee.reportStatus);
            values.put(DatabaseSadhana.COLUMN_LASTONLINE, devotee.lastOnline);
            values.put(DatabaseSadhana.COLUMN_SCORE, devotee.score);
            values.put(DatabaseSadhana.COLUMN_TSLEEP, devotee.defaultTSleep);
            values.put(DatabaseSadhana.COLUMN_TWAKEUP, devotee.defaultTWakeup);
            values.put(DatabaseSadhana.COLUMN_CHANTRND, devotee.defaultChantRnd);
            values.put(DatabaseSadhana.COLUMN_THEAR, devotee.defaultTHear);
            values.put(DatabaseSadhana.COLUMN_TREAD, devotee.defaultTRead);
            values.put(DatabaseSadhana.COLUMN_TSERVICE, devotee.defaultTService);
            values.put(DatabaseSadhana.COLUMN_TDAYREST, devotee.defaultTDayRest);
            values.put(DatabaseSadhana.COLUMN_PICVER, devotee.picVer);

            long insertId = database.insert(DatabaseSadhana.TABLE_FRIENDS, null,
                    values);
            //Log.d("ISS", "insertId:" + insertId);
            //Check whether query successfull
            return (int) insertId;
        } catch (Exception e) {
            Log.d("ISS", "insertSadhanaEntry: " + e.toString());
            e.printStackTrace();
            return 0;
        }
    }

    /*friends for a user*/
    public static DevoteeEntry getFriendInfo(int friendId) {
        try {
            int entriesRead = 0;
            /*query (String table, String[] columns, String selection, String[] selectionArgs, String groupBy,
                    String having, String orderBy, String limit)*/
            Cursor cursor = database.query(DatabaseSadhana.TABLE_FRIENDS,
                    null, /*all columns*/
                    DatabaseSadhana.COLUMN_FRIENDID + "=" + friendId,
                    null, null, null, null, null);

            //Log.d("ISS", " friend info for userId:" + friendId + "getcount:" + cursor.getCount());
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                DevoteeEntry friend = new DevoteeEntry();
                DevoteeEntry.cursorToFriendEntry(cursor, friend);
                return friend;
            }
            cursor.close();
            //Log.d("ISS", "found sadhanaEntries count:" + cursor.getCount());
            return null;
        } catch (Exception e) {
            Log.d("ISS", "getDevoteeInfo: " + e.toString());
            e.printStackTrace();
            return null;
        }
    }

    /*friends for a user*/
    public static DevoteeEntry[] fetchFriends(int userId, short isJunior) {
        try {
            int entriesRead = 0;
            /*query (String table, String[] columns, String selection, String[] selectionArgs, String groupBy,
                    String having, String orderBy, String limit)*/
            //Get Juniors list
            String selection = DatabaseSadhana.COLUMN_USERID + "=" + userId /*+ "' AND "+
                    DatabaseSadhana.COLUMN_ISJUNIOR + "='" + isJunior + "'"*/;
            Cursor cursor = database.query(DatabaseSadhana.TABLE_FRIENDS,
                    null, /*all columns*/
                    selection,
                    null, null, null, DatabaseSadhana.COLUMN_LASTONLINE + " DESC", null);

            DevoteeEntry[] friends = new DevoteeEntry[cursor.getCount()];
            for (int i = 0; i < cursor.getCount(); i++) {
                friends[i] = new DevoteeEntry();
            }

            //Log.d("ISS", "search friends for userId:" + userId + "getcount:" + cursor.getCount());
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    DevoteeEntry.cursorToFriendEntry(cursor, friends[entriesRead]);
                    //Log.d("ISS", "found Entry date:" + outSadhanaEntry[entriesRead].date);
                    cursor.moveToNext();
                    entriesRead++;
                }
                cursor.close();
                //Log.d("ISS", "found sadhanaEntries count:" + cursor.getCount());
            }
            return friends;
        } catch (Exception e) {
            Log.d("ISS", "fetchFriends: " + e.toString());
            e.printStackTrace();
            return null;
        }
    }

    public static boolean removeAllFriends(int userId) {
        try {
            /*delete(String table, String whereClause, String[] whereArgs)*/
            database.delete(DatabaseSadhana.TABLE_FRIENDS,
                    DatabaseSadhana.COLUMN_USERID + "=" + userId, null);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int insertMessageEntry(MessageEntry entry) {
        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseSadhana.COLUMN_SNDRNAME, entry.sndrName);
            values.put(DatabaseSadhana.COLUMN_RECEIVERID, entry.rcvrId);
            values.put(DatabaseSadhana.COLUMN_DATE, entry.date);
            values.put(DatabaseSadhana.COLUMN_MSG, entry.msg);
            values.put(DatabaseSadhana.COLUMN_ISSYNCED, entry.bIsSynced);
            long insertId = database.insert(DatabaseSadhana.TABLE_MESSAGE, null,
                    values);
            if (BuildConfig.DEBUG) Log.d("ISS", "MsgInsertId:" + insertId);
            //Check whether query successfull
            return (int) insertId;
        } catch (Exception e) {
            Log.d("ISS", "insertMessageEntry: " + e.toString());
            e.printStackTrace();
            return 0;
        }
    }

    /*Update existing Message entry*/
    public static int markMessageEntrySynced(int rcvrId) {
        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseSadhana.COLUMN_ISSYNCED, 1);

            int numRowsAffected = database.update(DatabaseSadhana.TABLE_MESSAGE,
                    values, DatabaseSadhana.COLUMN_RECEIVERID + "=" + rcvrId, null);
            if (BuildConfig.DEBUG) Log.d("ISS", "Msg Entry Synced:" + rcvrId + "Success=" + numRowsAffected);
            //Check whether query successful
            return numRowsAffected;
        } catch (Exception e) {
            Log.d("ISS", "MessageEntrySynced: " + e.toString());
            e.printStackTrace();
            return 0;
        }

    }

    public static int queryCountUnSyncedMessageEntries(int userId) {
        try {
            Cursor cursor = database.query(DatabaseSadhana.TABLE_MESSAGE,
                    null, /*all columns*/
                    DatabaseSadhana.COLUMN_SNDRNAME + "=" + userId + " AND " + DatabaseSadhana.COLUMN_ISSYNCED + "=" + 0,
                    null, null, null, null);
            return cursor.getCount();
        } catch (Exception e) {
            Log.d("ISS", "queryMessageEntryForDate: " + e.toString());
            e.printStackTrace();
            return 0;
        }
    }


    public static int queryRecentUnSyncedMessageEntries(int userId, MessageEntry[] outMessageEntry) {
        try {
            int entriesRead = 0;
            Cursor cursor = database.query(DatabaseSadhana.TABLE_MESSAGE,
                    null, /*all columns*/
                    DatabaseSadhana.COLUMN_SNDRNAME + "=" + userId + " AND " + DatabaseSadhana.COLUMN_ISSYNCED + "=" + 0,
                    null, null, null, DatabaseSadhana.COLUMN_DATE + " DESC ", "" + 3);
            //Log.d("ISS", "search entry for userId:"+userId+"dateindex"+dateIndexSince2016+"getcount:"+cursor.getCount());
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    MessageEntry.cursorToMessageEntry(cursor, outMessageEntry[entriesRead]);
                    cursor.moveToNext();
                    entriesRead++;
                }
                cursor.close();
                return entriesRead;
            } else
                return 0;
        } catch (Exception e) {
            Log.d("ISS", "queryMessageEntryForDate: " + e.toString());
            e.printStackTrace();
            return 0;
        }
    }

    /*Query last n sadhana entries for a user*/
    public static MessageEntry[] queryInboxMessageEntries(int userId, MessageEntry[] outMessageEntry) {
        try {
            int entriesRead = 0;
            /*query (String table, String[] columns, String selection, String[] selectionArgs, String groupBy,
                    String having, String orderBy, String limit)*/
            Cursor cursor = database.query(DatabaseSadhana.TABLE_MESSAGE,
                    null, /*all columns*/
                    DatabaseSadhana.COLUMN_RECEIVERID + "=" + userId,
                    null, null, null, DatabaseSadhana.COLUMN_DATE + " DESC ", null);

            //Log.d("ISS", "search entry for userId:" + userId + "dateindex" + numOfPrevEntries + "getcount:" + cursor.getCount());
            //MessageEntry singleEntry = new MessageEntry();
            if (cursor.getCount() > 0) {
                int count = cursor.getCount();
                outMessageEntry = new MessageEntry[count];
                for (int i = 0; i < count; i++) {
                    outMessageEntry[i] = new MessageEntry();
                }
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {

                    MessageEntry.cursorToMessageEntry(cursor, outMessageEntry[entriesRead]);
                    //outMessageEntry[i]=(singleEntry);
                    //Log.d("ISS", "found Entry date:" + outMessageEntry[entriesRead].date);
                    cursor.moveToNext();
                    entriesRead++;
                }
                cursor.close();
                Log.d("ISS", "found MessageEntries count:" + cursor.getCount());
                return outMessageEntry;
            }
            return null;
        } catch (Exception e) {
            Log.d("ISS", "queryInboxMessageEntries: " + e.toString());
            e.printStackTrace();
            return null;
        }
    }


    public static int exportSadhanaEntriesToCsv(Context context, int userId, String name) {
        try {
            int entriesRead = 0;
            SadhanaEntry entry = new SadhanaEntry();
            if (BuildConfig.DEBUG) Log.d("ISS", "exportSadhanaEntriesToCsv Start:" + userId + name);

            //CSV Write Example using CSVPrinter
            CSVFormat format = CSVFormat.EXCEL.withDelimiter(',');
            //initialize FileWriter object
            String dirName = Environment.getExternalStorageDirectory() + "/iskconSadhanaSharing";
            File backupDir = new File(Environment.getExternalStorageDirectory(), "iskconSadhanaSharing");
            if (!backupDir.exists()) {
                if (!backupDir.mkdir()) {
                    Log.d("ISS", "Unable to create backup directory:" + dirName);
                    return 0;
                }
            }

            Calendar calendar1 = Calendar.getInstance();
            int year = calendar1.get(Calendar.YEAR);
            int month = calendar1.get(Calendar.MONTH) + 1;
            int day = calendar1.get(Calendar.DAY_OF_MONTH);
            String fileName = year + "_" + month + "_" + day + "_" + name + ".csv";
            File backupFile = new File(dirName, fileName);
            backupFile.createNewFile();

            FileWriter fileWriter = new FileWriter(dirName + "/" + fileName);

            CSVPrinter printer = new CSVPrinter(fileWriter, format);
            printer.printRecord("Date", "Name", "Score", "Sleep", "Wakeup", "Morn Chant", "Total Chant",
                    "Chant Quality", "Arti", "Hear", "Read", "Serve", "Waste");

            Cursor cursor = database.query(DatabaseSadhana.TABLE_SADHANA,
                    null, /*all columns*/
                    DatabaseSadhana.COLUMN_USERID + "=" + userId,
                    null, null, null, DatabaseSadhana.COLUMN_DATE + " DESC ", null);

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    SadhanaEntry.cursorToSadhanaEntry(cursor, entry);
                    List<String> line = new ArrayList<String>();

                    line.add(FragmentFillSadhana.printDateForIndex(entry.date));
                    line.add(name);
                    line.add(entry.score + "%");
                    if (entry.tSleep == 255) line.add("-");
                    else {
                        int data1 = entry.tSleep / 10;
                        int data2 = (entry.tSleep % 10) * 6;
                        line.add(String.format("%d:%02d", data1, data2));
                    }

                    if (entry.tWakeup == 255) line.add("-");
                    else {
                        int data1 = entry.tWakeup / 10;
                        int data2 = (entry.tWakeup % 10) * 6;
                        line.add(String.format("%d:%02d", data1, data2));
                    }
                    line.add(entry.chantMorn + "");
                    line.add(entry.chantRnd + "");
                    line.add(entry.chantQ + "");
                    line.add(entry.mangal + "");
                    line.add(entry.tHear + "");
                    line.add(entry.tRead + "");
                    line.add(entry.tService + "");
                    if (entry.tDayRest == 255) line.add("-");
                    else line.add(entry.tDayRest + "");

                    printer.printRecord(line);
                    cursor.moveToNext();
                    entriesRead++;
                }
            }
            cursor.close();
            fileWriter.flush();
            fileWriter.close();
            Toast.makeText(context, entriesRead + " entries saved in /iskconSadhanaSharing /" + fileName,
                    Toast.LENGTH_LONG).show();
            //close the printer
            printer.close();
            return entriesRead;
        } catch (Exception e) {
            Log.d("ISS", "exportSadhanaEntriesToCsv: " + e.toString());
            e.printStackTrace();
            return 0;
        }
    }

}
