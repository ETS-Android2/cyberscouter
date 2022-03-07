package com.frcteam195.cyberscouter;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Vector;

public class CyberScouterUsers {
    public final static String USERS_UPDATED_FILTER = "frcteam195_cyberscouterusers_users_updated_intent_filter";

    public CyberScouterUsers() {
    }

    private int userID;
    private String firstName;
    private String lastName;
    private String cellPhone;
    private String email;

    static String getUsersRemote(AppCompatActivity activity, SQLiteDatabase db) {
        String ret = null;
        int last_hash = 0;
        try {
            last_hash = CyberScouterTimeCode.getLast_update(db);
            BluetoothComm btcomm = new BluetoothComm();
            String response = btcomm.getUsers(activity, last_hash);
            if(null != response) {
                JSONObject jo = new JSONObject(response);
                String result = jo.getString("result");
                if (!result.equalsIgnoreCase("failure")) {
                    if(result.equalsIgnoreCase("skip")) {
                        ret = "skip";
                    } else {
                        JSONArray payload = jo.getJSONArray("payload");
                        ret = payload.toString();
                        last_hash = jo.getInt("hash");
//                        CyberScouterTimeCode.setLast_update(db, last_hash);
                    }
                } else {
                    ret = "skip";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    static String[] getUserNames(SQLiteDatabase db) {
        CyberScouterUsers[] csu = getLocalUsers(db);
        Vector<String> nameVector = new Vector<String>();
        if (null != csu) {
            for (int i = 0; i < csu.length; ++i) {
                String name = csu[i].getFirstName() + " "
                        + csu[i].getLastName();
                nameVector.add(name);
            }

        }

        if (0 < nameVector.size()) {
            String[] nv2 = new String[nameVector.size()];
            return nameVector.toArray(nv2);
        } else {
            return null;
        }
    }

    static CyberScouterUsers[] getLocalUsers(SQLiteDatabase db) {
        String sortOrder =
                CyberScouterContract.Users.COLUMN_NAME_LASTNAME + " ASC, " + CyberScouterContract.Users.COLUMN_NAME_LASTNAME + " ASC";
        CyberScouterUsers[] csua = getLocalUsersWithSelect(db, null, null, sortOrder);
        return csua;
    }

    static CyberScouterUsers getLocalUser(SQLiteDatabase db, String userName) {
        CyberScouterUsers csu = null;
        String[] name_parts = userName.split(" ");
        if(name_parts.length == 2) {
            String selection = CyberScouterContract.Users.COLUMN_NAME_FIRSTNAME + " = ? AND " + CyberScouterContract.Users.COLUMN_NAME_LASTNAME + " = ?";
            String[] selectionArgs = {name_parts[0], name_parts[1]};
            CyberScouterUsers[] csua = getLocalUsersWithSelect(db, selection, selectionArgs, null);
            if(csua != null) {
                csu = csua[0];
            }
        }

        return(csu);
    }

    static CyberScouterUsers[] getLocalUsersWithSelect(SQLiteDatabase db, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;

        Vector<CyberScouterUsers> csuv = new Vector<CyberScouterUsers>();
        CyberScouterUsers[] csua = null;
        CyberScouterUsers csu = null;

        String[] projection = {
                CyberScouterContract.Users.COLUMN_NAME_USERID,
                CyberScouterContract.Users.COLUMN_NAME_FIRSTNAME,
                CyberScouterContract.Users.COLUMN_NAME_LASTNAME,
                CyberScouterContract.Users.COLUMN_NAME_CELLPHONE,
                CyberScouterContract.Users.COLUMN_NAME_EMAIL
        };

        if( sortOrder == null) {
            sortOrder =
                    CyberScouterContract.Users.COLUMN_NAME_LASTNAME + " ASC, " + CyberScouterContract.Users.COLUMN_NAME_LASTNAME + " ASC";
        }

        cursor = db.query(
                CyberScouterContract.Users.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );

        if (0 < cursor.getCount()) {
            while (cursor.moveToNext()) {
                csu = new CyberScouterUsers();

                csu.userID = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.Users.COLUMN_NAME_USERID));
                csu.firstName = cursor.getString(cursor.getColumnIndex(CyberScouterContract.Users.COLUMN_NAME_FIRSTNAME));
                csu.lastName = cursor.getString(cursor.getColumnIndex(CyberScouterContract.Users.COLUMN_NAME_LASTNAME));
                csu.cellPhone = cursor.getString(cursor.getColumnIndex(CyberScouterContract.Users.COLUMN_NAME_CELLPHONE));
                csu.email = cursor.getString(cursor.getColumnIndex(CyberScouterContract.Users.COLUMN_NAME_EMAIL));

                csuv.add(csu);
            }

            csua = new CyberScouterUsers[csuv.size()];
            return (csuv.toArray(csua));
        }

        return csua;
    }

    static void setUsers(SQLiteDatabase db, String json) {

        try {

            JSONArray ja = new JSONArray(json);
            for (int i = 0; i < ja.length(); ++i) {
                JSONObject jo = ja.getJSONObject(i);
                ContentValues values = new ContentValues();

                values.put(CyberScouterContract.Users.COLUMN_NAME_USERID, jo.getString("UserID"));
                values.put(CyberScouterContract.Users.COLUMN_NAME_FIRSTNAME, jo.getString("FirstName"));
                values.put(CyberScouterContract.Users.COLUMN_NAME_LASTNAME, jo.getString("LastName"));
                values.put(CyberScouterContract.Users.COLUMN_NAME_CELLPHONE, jo.getString("CellPhone"));
                values.put(CyberScouterContract.Users.COLUMN_NAME_EMAIL, jo.getString("Email"));

                long newRowId = db.insertWithOnConflict(CyberScouterContract.Users.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void deleteUsers(SQLiteDatabase db) {
        db.execSQL("DELETE from " + CyberScouterContract.Users.TABLE_NAME);
    }

    static public void getUsersWebService(final AppCompatActivity activity) {
        RequestQueue rq = Volley.newRequestQueue(activity);
        String url = String.format("%s/users", FakeBluetoothServer.webServiceBaseUrl);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Intent i = new Intent(USERS_UPDATED_FILTER);
                            i.putExtra("cyberscouterusers", response);
                            activity.sendBroadcast(i);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        rq.add(stringRequest);
        return;

    }


    public int getUserID() {
        return userID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getCellPhone() {
        return cellPhone;
    }

    public String getEmail() {
        return email;
    }
}
