package com.frcteam195.cyberscouter;

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

public class CyberScouterWordCloud {
    final static String WORD_CLOUD_UPDATED_FILTER = "frcteam195_cyberscouterwordcloud_word_cloud_updated_intent_filter";

    private int EventID;
    private int MatchID;
    private int MatchScoutingID;
    private int WordCount;
    private String Team;
    private int WordID;

    void putWordCloud(SQLiteDatabase db) {
        try {
            ContentValues values = new ContentValues();

            values.put(CyberScouterContract.WordCloud.COLUMN_NAME_EVENT_ID, EventID);
            values.put(CyberScouterContract.WordCloud.COLUMN_NAME_MATCH_ID, MatchID);
            values.put(CyberScouterContract.WordCloud.COLUMN_NAME_MATCH_SCOUTING_ID, MatchScoutingID);
            values.put(CyberScouterContract.WordCloud.COLUMN_NAME_TEAM, Team);
            values.put(CyberScouterContract.WordCloud.COLUMN_NAME_WORD_ID, WordID);
            values.put(CyberScouterContract.WordCloud.COLUMN_NAME_WORD_COUNT, WordCount);
            values.put(CyberScouterContract.WordCloud.COLUMN_NAME_DONE_SCOUTING, 0);
            values.put(CyberScouterContract.WordCloud.COLUMN_NAME_UPLOAD_STATUS, UploadStatus.NOT_UPLOADED);

            long newRowId = db.insertWithOnConflict(CyberScouterContract.WordCloud.TABLE_NAME,
                    null, values, SQLiteDatabase.CONFLICT_REPLACE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static CyberScouterWordCloud getLocalWordCloud(SQLiteDatabase db, int _matchScoutingID, String _team, int _wordID) {
        Cursor cursor = null;

        CyberScouterWordCloud cswc = null;

        String[] projection = {
                CyberScouterContract.WordCloud.COLUMN_NAME_EVENT_ID,
                CyberScouterContract.WordCloud.COLUMN_NAME_MATCH_ID,
                CyberScouterContract.WordCloud.COLUMN_NAME_MATCH_SCOUTING_ID,
                CyberScouterContract.WordCloud.COLUMN_NAME_TEAM,
                CyberScouterContract.WordCloud.COLUMN_NAME_WORD_ID,
                CyberScouterContract.WordCloud.COLUMN_NAME_WORD_COUNT
        };

        String selection = CyberScouterContract.WordCloud.COLUMN_NAME_MATCH_SCOUTING_ID + " = ? AND " +
                CyberScouterContract.WordCloud.COLUMN_NAME_TEAM + " = ? AND " +
                CyberScouterContract.WordCloud.COLUMN_NAME_WORD_ID + " = ?";
        String[] selectionArgs = {
                String.format(Locale.getDefault(), "%d", _matchScoutingID),
                String.format(Locale.getDefault(), "%s", _team),
                String.format(Locale.getDefault(), "%d", _wordID)
        };


        cursor = db.query(
                CyberScouterContract.WordCloud.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );

        if (0 < cursor.getCount()) {
            cursor.moveToNext();
            cswc = new CyberScouterWordCloud();

            cswc.EventID = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.WordCloud.COLUMN_NAME_EVENT_ID));
            cswc.MatchID = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.WordCloud.COLUMN_NAME_MATCH_ID));
            cswc.MatchScoutingID = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.WordCloud.COLUMN_NAME_MATCH_SCOUTING_ID));
            cswc.Team = cursor.getString(cursor.getColumnIndex(CyberScouterContract.WordCloud.COLUMN_NAME_TEAM));
            cswc.WordID = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.WordCloud.COLUMN_NAME_WORD_ID));
            cswc.WordCount = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.WordCloud.COLUMN_NAME_WORD_COUNT));
            cursor.close();
        }

        return cswc;
    }

    static int deleteLocalWordCloud(SQLiteDatabase db, int _matchScoutingID, String _team, int _wordID) {

        String selection = CyberScouterContract.WordCloud.COLUMN_NAME_MATCH_SCOUTING_ID + " = ? AND " +
                CyberScouterContract.WordCloud.COLUMN_NAME_TEAM + " = ? AND " +
                CyberScouterContract.WordCloud.COLUMN_NAME_WORD_ID + " = ?";
        String[] selectionArgs = {
                String.format(Locale.getDefault(), "%d", _matchScoutingID),
                String.format(Locale.getDefault(), "%s", _team),
                String.format(Locale.getDefault(), "%d", _wordID)
        };

        return(db.delete(CyberScouterContract.WordCloud.TABLE_NAME, selection, selectionArgs));

    }

    static String getWordCloudRemote(AppCompatActivity activity) {
        String ret = null;

        try {
            BluetoothComm btcomm = new BluetoothComm();
            String response = btcomm.getWordCloud(activity, 0);
            if (null != response) {
                JSONObject jo = new JSONObject(response);
                String result = jo.getString("result");
                if ("failure" != result) {
                    if (result.equalsIgnoreCase("skip")) {
                        ret = "skip";
                    } else {
                        JSONArray payload = jo.getJSONArray("payload");
                        ret = payload.toString();
                    }
                } else {
                    ret = "skip";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return (ret);
    }

    static public void getWordCloudWebService(final AppCompatActivity activity) {
        RequestQueue rq = Volley.newRequestQueue(activity);
        String url = String.format("%s/word-cloud", FakeBluetoothServer.webServiceBaseUrl);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Intent i = new Intent(WORD_CLOUD_UPDATED_FILTER);
                            i.putExtra("cyberscouterwords", response);
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

    public static void setDoneScouting(SQLiteDatabase db) {
        try {
            ContentValues values = new ContentValues();

            values.put(CyberScouterContract.WordCloud.COLUMN_NAME_DONE_SCOUTING, 1);
            values.put(CyberScouterContract.WordCloud.COLUMN_NAME_UPLOAD_STATUS, UploadStatus.READY_TO_UPLOAD);

            long newRowId = db.update(CyberScouterContract.WordCloud.TABLE_NAME, values, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void setWordMetric(SQLiteDatabase db, CyberScouterConfig cfg, int _wordId, int cntr) {
        return;
    }

    public int getEventID() {
        return EventID;
    }

    public int getMatchID() {
        return MatchID;
    }

    public int getMatchScoutingID() {
        return MatchScoutingID;
    }

    public int getWordCount() {
        return WordCount;
    }

    public String getTeam() {
        return Team;
    }

    public int getWordID() {
        return WordID;
    }

    public void setEventID(int eventID) {
        EventID = eventID;
    }

    public void setMatchID(int matchID) {
        MatchID = matchID;
    }

    public void setMatchScoutingID(int matchScoutingID) {
        MatchScoutingID = matchScoutingID;
    }

    public void setWordCount(int wordCount) {
        WordCount = wordCount;
    }

    public void setTeam(String team) {
        Team = team;
    }

    public void setWordID(int wordID) {
        WordID = wordID;
    }
}
