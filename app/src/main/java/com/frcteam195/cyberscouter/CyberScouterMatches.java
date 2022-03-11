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

import java.util.Locale;
import java.util.Vector;

public class CyberScouterMatches {
    final static String MATCHES_UPDATED_FILTER = "frcteam195_cyberscoutermatches_matches_updated_intent_filter";

    private static boolean webQueryInProgress = false;

    private static String webResponse;

    static String getWebResponse() {
        return (webResponse);
    }

    private int eventID;
    private int matchID;
    private int matchNo;
    private String redTeam1;
    private String redTeam2;
    private String redTeam3;
    private String blueTeam1;
    private String blueTeam2;
    private String blueTeam3;

    public String toJSON() {
        String json = "";

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("eventID", getEventID());
            jsonObject.put("matchNo", getMatchID());
            jsonObject.put("redTeam1", getRedTeam1());
            jsonObject.put("redTeam1", getRedTeam2());
            jsonObject.put("redTeam1", getRedTeam3());
            jsonObject.put("redTeam1", getBlueTeam1());
            jsonObject.put("redTeam1", getBlueTeam2());
            jsonObject.put("redTeam1", getBlueTeam3());

            json = jsonObject.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return json;
    }

    static String getMatchesRemote(AppCompatActivity activity, SQLiteDatabase db) {
        int last_hash = CyberScouterTimeCode.getLast_update(db);
        String ret = null;
        try {
            BluetoothComm btcomm = new BluetoothComm();
            String response = btcomm.getMatches(activity, last_hash);
            if (null != response) {
                JSONObject jo = new JSONObject(response);
                String result = jo.getString("result");
                if (!result.equalsIgnoreCase("failure")) {
                    if (result.equalsIgnoreCase("skip")) {
                        ret = "skip";
                    } else {
                        JSONArray payload = jo.getJSONArray("payload");
                        ret = payload.toString();
                        last_hash = jo.getInt("hash");
                        CyberScouterTimeCode.setLast_update(db, last_hash);
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

    public static CyberScouterMatches getLocalMatch(SQLiteDatabase db, int l_eventID, int l_matchID) throws Exception {
        String selection =
                CyberScouterContract.Matches.COLUMN_NAME_EVENTID + " = ? AND "
                        + CyberScouterContract.Matches.COLUMN_NAME_MATCH_NUMBER + " = ?";
        String[] selectionArgs = {
                String.format(Locale.getDefault(), "%d", l_eventID),
                String.format(Locale.getDefault(), "%d", l_matchID),
        };

        CyberScouterMatches[] csmv = getLocalMatches(db, selection, selectionArgs, null);

        if (null != csmv) {
            if (1 < csmv.length) {
                throw new Exception(String.format(Locale.getDefault(), "Too many match scouting rows found.  Wanted %d, found %d!\n\nEventID=%d, MatchID=%d",
                        1, csmv.length, l_eventID, l_matchID));
            } else
                return (csmv[0]);
        } else
            return null;
    }

    private static CyberScouterMatches[] getLocalMatches(SQLiteDatabase db, String selection, String[] selectionArgs, String sortOrder) {
        CyberScouterMatches csm;
        Vector<CyberScouterMatches> csmv = new Vector<>();

        Cursor cursor = null;
        try {
            String[] projection = {
                    CyberScouterContract.Matches.COLUMN_NAME_EVENTID,
                    CyberScouterContract.Matches.COLUMN_NAME_MATCHID,
                    CyberScouterContract.Matches.COLUMN_NAME_MATCH_NUMBER,
                    CyberScouterContract.Matches.COLUMN_NAME_RED_TEAM_1,
                    CyberScouterContract.Matches.COLUMN_NAME_RED_TEAM_2,
                    CyberScouterContract.Matches.COLUMN_NAME_RED_TEAM_3,
                    CyberScouterContract.Matches.COLUMN_NAME_BLUE_TEAM_1,
                    CyberScouterContract.Matches.COLUMN_NAME_BLUE_TEAM_2,
                    CyberScouterContract.Matches.COLUMN_NAME_BLUE_TEAM_3
            };


            cursor = db.query(
                    CyberScouterContract.Matches.TABLE_NAME,   // The table to query
                    projection,             // The array of columns to return (pass null to get all)
                    selection,              // The columns for the WHERE clause
                    selectionArgs,          // The values for the WHERE clause
                    null,                   // don't group the rows
                    null,                   // don't filter by row groups
                    sortOrder               // The sort order
            );

            if (0 < cursor.getCount()) {
                while (cursor.moveToNext()) {
                    csm = new CyberScouterMatches();
                    /* Read the match information from SQLite */
                    csm.eventID = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.Matches.COLUMN_NAME_EVENTID));
                    csm.matchID = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.Matches.COLUMN_NAME_MATCHID));
                    csm.matchNo = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.Matches.COLUMN_NAME_MATCH_NUMBER));
                    csm.redTeam1 = cursor.getString(cursor.getColumnIndex(CyberScouterContract.Matches.COLUMN_NAME_RED_TEAM_1));
                    csm.redTeam2 = cursor.getString(cursor.getColumnIndex(CyberScouterContract.Matches.COLUMN_NAME_RED_TEAM_2));
                    csm.redTeam3 = cursor.getString(cursor.getColumnIndex(CyberScouterContract.Matches.COLUMN_NAME_RED_TEAM_3));
                    csm.blueTeam1 = cursor.getString(cursor.getColumnIndex(CyberScouterContract.Matches.COLUMN_NAME_BLUE_TEAM_1));
                    csm.blueTeam2 = cursor.getString(cursor.getColumnIndex(CyberScouterContract.Matches.COLUMN_NAME_BLUE_TEAM_2));
                    csm.blueTeam3 = cursor.getString(cursor.getColumnIndex(CyberScouterContract.Matches.COLUMN_NAME_BLUE_TEAM_3));

                    csmv.add(csm);
                }
            }

            if (0 < csmv.size()) {
                CyberScouterMatches[] csma = new CyberScouterMatches[csmv.size()];
                return csmv.toArray(csma);
            } else
                return null;

        } catch (Exception e) {
            e.printStackTrace();
            throw (e);
        } finally {
            if (null != cursor && !cursor.isClosed())
                cursor.close();
        }
    }

    public static void setMatches(SQLiteDatabase db, String joString) throws Exception {

        JSONArray ja = new JSONArray(joString);

        for (int i = 0; i < ja.length(); ++i) {

            JSONObject jo = ja.getJSONObject(i);

            ContentValues values = new ContentValues();
            values.put(CyberScouterContract.Matches.COLUMN_NAME_EVENTID, jo.getInt(CyberScouterContract.Matches.COLUMN_NAME_EVENTID));
            values.put(CyberScouterContract.Matches.COLUMN_NAME_MATCHID, jo.getInt(CyberScouterContract.Matches.COLUMN_NAME_MATCHID));
            values.put(CyberScouterContract.Matches.COLUMN_NAME_MATCH_NUMBER, jo.getInt(CyberScouterContract.Matches.COLUMN_NAME_MATCH_NUMBER));
            values.put(CyberScouterContract.Matches.COLUMN_NAME_RED_TEAM_1, jo.getString(CyberScouterContract.Matches.COLUMN_NAME_RED_TEAM_1));
            values.put(CyberScouterContract.Matches.COLUMN_NAME_RED_TEAM_2, jo.getString(CyberScouterContract.Matches.COLUMN_NAME_RED_TEAM_2));
            values.put(CyberScouterContract.Matches.COLUMN_NAME_RED_TEAM_3, jo.getString(CyberScouterContract.Matches.COLUMN_NAME_RED_TEAM_3));
            values.put(CyberScouterContract.Matches.COLUMN_NAME_BLUE_TEAM_1, jo.getString(CyberScouterContract.Matches.COLUMN_NAME_BLUE_TEAM_1));
            values.put(CyberScouterContract.Matches.COLUMN_NAME_BLUE_TEAM_2, jo.getString(CyberScouterContract.Matches.COLUMN_NAME_BLUE_TEAM_2));
            values.put(CyberScouterContract.Matches.COLUMN_NAME_BLUE_TEAM_3, jo.getString(CyberScouterContract.Matches.COLUMN_NAME_BLUE_TEAM_3));

            long newRowId = db.insert(CyberScouterContract.Matches.TABLE_NAME, null, values);
            if (-1 == newRowId) {
                throw (new Exception("An invalid row id was generated by the SQLite database insert command"));
            }
        }
    }

    static void deleteOldMatches(SQLiteDatabase db, int l_eventID) {
        String[] nada = null;
        db.delete(CyberScouterContract.Matches.TABLE_NAME,
                "", nada);
    }

    private static int updateMatch(SQLiteDatabase db, ContentValues values, String selection, String[] selectionArgs) {
        return db.update(
                CyberScouterContract.Matches.TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }

    static void getMatchesWebService(final Activity activity) {

        if (webQueryInProgress)
            return;

        webQueryInProgress = true;
        RequestQueue rq = Volley.newRequestQueue(activity);
        String url = String.format("%s/matches", FakeBluetoothServer.webServiceBaseUrl);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        webQueryInProgress = false;
                        try {
                            Intent i = new Intent(MATCHES_UPDATED_FILTER);
                            webResponse = response;
                            i.putExtra("cyberscoutermatches", "fetch");
                            activity.sendBroadcast(i);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                webQueryInProgress = false;
                String msg;
                if (null == error.networkResponse) {
                    msg = error.getMessage();
                } else {
                    msg = String.format("Status Code: %d\nMessage: %s", error.networkResponse.statusCode, new String(error.networkResponse.data));
                }

                MessageBox.showMessageBox(activity, "Fetch of Match Records Failed", "CyberScouterMatches.getMatchesWebService",
                        String.format("Can't get list of matches to scout.\nContact a scouting mentor right away\n\n%s\n", msg));
            }
        });

        rq.add(stringRequest);
    }


    int getEventID() {
        return eventID;
    }
    int getMatchID() {
        return matchID;
    }
    int getMatchNo() {
        return matchNo;
    }
    String getRedTeam1() { return redTeam1;}
    String getRedTeam2() { return redTeam2;}
    String getRedTeam3() { return redTeam3;}
    String getBlueTeam1() { return blueTeam1;}
    String getBlueTeam2() { return blueTeam2;}
    String getBlueTeam3() { return blueTeam3;}

}
