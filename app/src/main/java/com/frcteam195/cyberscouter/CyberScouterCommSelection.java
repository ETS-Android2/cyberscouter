package com.frcteam195.cyberscouter;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CyberScouterCommSelection {
    static final public String DEFAULT_IP = "10.0.20.195";
    public int choice;
    public String serverIp;

    public static CyberScouterCommSelection get(SQLiteDatabase db) {
        Cursor cursor = null;
        CyberScouterCommSelection ret = new CyberScouterCommSelection();


        String[] projection = {
                CyberScouterContract.CommSelection.COLUMN_NAME_COMM_CHOICE,
                CyberScouterContract.CommSelection.COLUMN_NAME_SERVER_IP
        };

        cursor = db.query(
                CyberScouterContract.CommSelection.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );

        if (0 < cursor.getCount()) {
            cursor.moveToNext();
            ret.choice = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.CommSelection.COLUMN_NAME_COMM_CHOICE));
            ret.serverIp = cursor.getString(cursor.getColumnIndex(CyberScouterContract.CommSelection.COLUMN_NAME_SERVER_IP));
        }

        return ret;
    }

    public static void set(SQLiteDatabase db, int commSelection, String serverIp) {
        System.out.println(String.format(">>>>>>>>>>>>>>>>>>>>>>>Setting comm selection to %d, %s", commSelection, serverIp));
        try {
            ContentValues values = new ContentValues();

            values.put(CyberScouterContract.CommSelection.COLUMN_NAME_COMM_CHOICE, commSelection);
            values.put(CyberScouterContract.CommSelection.COLUMN_NAME_SERVER_IP, serverIp);

            long newRowId = db.update(CyberScouterContract.CommSelection.TABLE_NAME,
                    values,null,null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
