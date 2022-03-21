package com.frcteam195.cyberscouter;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Locale;
import java.util.Vector;
import java.util.stream.Stream;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class CyberScouterWordCloud {
    final static String WORD_CLOUD_UPDATED_FILTER = "frcteam195_cyberscouterwordcloud_word_cloud_updated_intent_filter";
    final static String WORD_CLOUD_FETCHED_FILTER = "frcteam195_cyberscouterwordcloud_word_cloud_fetched_intent_filter";

    private static String webResponse;

    static String getWebResponse() {
        return (webResponse);
    }

    private static boolean webQueryInProgress = false;

    private int EventID;
    private int MatchID;
    private int WordCloudID;
    private int WordCount;
    private String Team;
    private int WordID;

    void putWordCloud(SQLiteDatabase db, int conflict_action) {
        try {
            ContentValues values = new ContentValues();

            values.put(CyberScouterContract.WordCloud.COLUMN_NAME_EVENT_ID, EventID);
            values.put(CyberScouterContract.WordCloud.COLUMN_NAME_MATCH_ID, MatchID);
            values.put(CyberScouterContract.WordCloud.COLUMN_NAME_MATCH_SCOUTING_ID, WordCloudID);
            values.put(CyberScouterContract.WordCloud.COLUMN_NAME_TEAM, Team);
            values.put(CyberScouterContract.WordCloud.COLUMN_NAME_WORD_ID, WordID);
            values.put(CyberScouterContract.WordCloud.COLUMN_NAME_WORD_COUNT, WordCount);
            values.put(CyberScouterContract.WordCloud.COLUMN_NAME_DONE_SCOUTING, 0);
            values.put(CyberScouterContract.WordCloud.COLUMN_NAME_UPLOAD_STATUS, UploadStatus.NOT_UPLOADED);

            long newRowId = db.insertWithOnConflict(CyberScouterContract.WordCloud.TABLE_NAME,
                    null, values, conflict_action);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static public void setWordCloudMetric(SQLiteDatabase db, int _eventID, String _team, int _matchID, int _wordID, int _wordVal) {
        CyberScouterWordCloud cswc = new CyberScouterWordCloud();
        cswc.setEventID(_eventID);
        cswc.setMatchID(_matchID);
        cswc.setTeam(_team);
        cswc.setWordCloudID(0);
        cswc.setWordID(_wordID);
        cswc.setWordCount(_wordVal);

        cswc.putWordCloud(db, SQLiteDatabase.CONFLICT_REPLACE);
        }

    static public void deleteOldMatches(SQLiteDatabase db, int _matchID) {
        String[] wherevals = {String.valueOf(_matchID)};
        db.delete(CyberScouterContract.WordCloud.TABLE_NAME,
                CyberScouterContract.WordCloud.COLUMN_NAME_MATCH_ID + " <> ?", wherevals);
    }

    static public void mergeWordCloudMatchInfo(SQLiteDatabase db, CyberScouterMatches csm, String jstr, int numberOfWords, boolean redSelected) {
        CyberScouterWordCloud cswc = new CyberScouterWordCloud();
        cswc.setEventID(csm.getEventID());
        cswc.setMatchID(csm.getMatchID());
        cswc.setWordCloudID(0);
        cswc.setWordCount(0);

        if(redSelected) {
            cswc.setTeam(csm.getRedTeam1());
            insertZeroedTeamRecords(db, cswc, numberOfWords);
            cswc.setTeam(csm.getRedTeam2());
            insertZeroedTeamRecords(db, cswc, numberOfWords);
            cswc.setTeam(csm.getRedTeam3());
            insertZeroedTeamRecords(db, cswc, numberOfWords);
        } else {
            cswc.setTeam(csm.getBlueTeam1());
            insertZeroedTeamRecords(db, cswc, numberOfWords);
            cswc.setTeam(csm.getBlueTeam2());
            insertZeroedTeamRecords(db, cswc, numberOfWords);
            cswc.setTeam(csm.getBlueTeam3());
            insertZeroedTeamRecords(db, cswc, numberOfWords);
        }

        try {
            if(!jstr.equalsIgnoreCase("null")) {
                JSONArray jar = new JSONArray(jstr);
                for(int i=0; i<jar.length(); ++i) {
                    JSONObject jo = jar.getJSONObject(i);
                    cswc = new CyberScouterWordCloud();
                    cswc.setEventID(jo.getInt(CyberScouterContract.WordCloud.COLUMN_NAME_EVENT_ID));
                    cswc.setMatchID(jo.getInt(CyberScouterContract.WordCloud.COLUMN_NAME_MATCH_ID));
                    cswc.setTeam(jo.getString(CyberScouterContract.WordCloud.COLUMN_NAME_TEAM));
                    cswc.setWordID(jo.getInt(CyberScouterContract.WordCloud.COLUMN_NAME_WORD_ID));
                    cswc.setWordCount(jo.getInt(CyberScouterContract.WordCloud.COLUMN_NAME_WORD_COUNT));
                    cswc.putWordCloud(db, SQLiteDatabase.CONFLICT_REPLACE);
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    static private void insertZeroedTeamRecords(SQLiteDatabase db, CyberScouterWordCloud cswc, int numberOfWords) {
        for(int i=0; i<numberOfWords; ++i){
            cswc.setWordID(i+1);
            cswc.setWordCount(0);
            cswc.putWordCloud(db, SQLiteDatabase.CONFLICT_IGNORE);
        }
    }

    static CyberScouterWordCloud[] getLocalWordCloud(SQLiteDatabase db, String[] _teams, int _matchID) {
        CyberScouterWordCloud[] ret = {};

        for(int i=0; i<_teams.length; ++i) {
            CyberScouterWordCloud[] ta = getLocalWordCloud(db, _teams[i], _matchID);
            CyberScouterWordCloud[] finalRet = ret;
            CyberScouterWordCloud[] tmp = Stream.concat(Arrays.stream(ret), Arrays.stream(ta)).toArray(size -> (CyberScouterWordCloud[]) Array.newInstance(finalRet.getClass().getComponentType(), size));
            ret = tmp;
        }

        return(ret);
    }


    static CyberScouterWordCloud[] getLocalWordCloud(SQLiteDatabase db, String _team, int _matchID) {
        Vector<CyberScouterWordCloud> cswcv = new Vector<>();
        CyberScouterWordCloud cswc = null;
        Cursor cursor = null;


        String[] projection = {
                CyberScouterContract.WordCloud.COLUMN_NAME_EVENT_ID,
                CyberScouterContract.WordCloud.COLUMN_NAME_MATCH_ID,
                CyberScouterContract.WordCloud.COLUMN_NAME_MATCH_SCOUTING_ID,
                CyberScouterContract.WordCloud.COLUMN_NAME_TEAM,
                CyberScouterContract.WordCloud.COLUMN_NAME_WORD_ID,
                CyberScouterContract.WordCloud.COLUMN_NAME_WORD_COUNT
        };

        String selection = CyberScouterContract.WordCloud.COLUMN_NAME_TEAM + " = ? AND " +
                CyberScouterContract.WordCloud.COLUMN_NAME_MATCH_ID + " = ?";
        String[] selectionArgs = {
                String.format(Locale.getDefault(), "%s", _team),
                String.format(Locale.getDefault(), "%d", _matchID)
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
            while(cursor.moveToNext()) {
                cswc = new CyberScouterWordCloud();

                cswc.EventID = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.WordCloud.COLUMN_NAME_EVENT_ID));
                cswc.MatchID = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.WordCloud.COLUMN_NAME_MATCH_ID));
                cswc.WordCloudID = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.WordCloud.COLUMN_NAME_MATCH_SCOUTING_ID));
                cswc.Team = cursor.getString(cursor.getColumnIndex(CyberScouterContract.WordCloud.COLUMN_NAME_TEAM));
                cswc.WordID = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.WordCloud.COLUMN_NAME_WORD_ID));
                cswc.WordCount = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.WordCloud.COLUMN_NAME_WORD_COUNT));

                cswcv.add(cswc);
            }
            cursor.close();
        }

        if (0 < cswcv.size()) {
            CyberScouterWordCloud[] cswca = new CyberScouterWordCloud[cswcv.size()];
            return cswcv.toArray(cswca);
        } else
            return null;
    }

    static int deleteLocalWordCloud(SQLiteDatabase db, int _matchID, String _team, int _wordID) {

        String selection = CyberScouterContract.WordCloud.COLUMN_NAME_MATCH_SCOUTING_ID + " = ? AND " +
                CyberScouterContract.WordCloud.COLUMN_NAME_TEAM + " = ? AND " +
                CyberScouterContract.WordCloud.COLUMN_NAME_WORD_ID + " = ?";
        String[] selectionArgs = {
                String.format(Locale.getDefault(), "%d", _matchID),
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

    public static String setWordCloudRemote(AppCompatActivity activity, CyberScouterConfig cfg, CyberScouterWordCloud[] cswca) {
        String ret = "failed";
        try {
            JSONObject jo = new JSONObject();
            jo.put("cmd", "put-word-cloud");
            jo.put("table_name", CyberScouterContract.WordCloud.TABLE_NAME);
            JSONArray payload = new JSONArray();
            for(int i=0; i<cswca.length; ++i) {
                JSONObject jrec = new JSONObject();
                jrec.put(CyberScouterContract.WordCloud.COLUMN_NAME_EVENT_ID, cswca[i].getEventID());
                jrec.put(CyberScouterContract.WordCloud.COLUMN_NAME_MATCH_ID, cswca[i].getMatchID());
                jrec.put(CyberScouterContract.WordCloud.COLUMN_NAME_TEAM, cswca[i].getTeam());
                jrec.put(CyberScouterContract.WordCloud.COLUMN_NAME_MATCH_SCOUTING_ID, 0);
                jrec.put(CyberScouterContract.WordCloud.COLUMN_NAME_WORD_ID, cswca[i].getWordID());
                jrec.put(CyberScouterContract.WordCloud.COLUMN_NAME_WORD_COUNT, cswca[i].getWordCount());
                payload.put(jrec);
            }
            jo.put("payload", payload);

            BluetoothComm btcomm = new BluetoothComm();
            String response = btcomm.sendSetCommand(activity, jo);
            if (null != response) {
                response = response.replace("x03", "");
                JSONObject jresp = new JSONObject(response);
                ret = jresp.getString("result");
            } else {
                ret = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    static public void getWordCloudWebService(final AppCompatActivity activity) {
        if(webQueryInProgress) {
            return;
        }

        webQueryInProgress = true;

        String url = String.format("%s/word-cloud", FakeBluetoothServer.webServiceBaseUrl);
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(url, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                System.out.println("Starting get call...");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                webQueryInProgress = false;
                webResponse = new String(response);
                Intent i = new Intent(WORD_CLOUD_FETCHED_FILTER);
                i.putExtra("cyberscouterwordcloud", "fetched");
                activity.sendBroadcast(i);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                webQueryInProgress = false;
                e.printStackTrace();
            }

            @Override
            public void onRetry(int retryNo) {
                System.out.println(String.format("Retry number %d", retryNo));
            }
        });
    }

    static void setWordCloudWebService(final Activity activity, JSONObject jo) {

        if (webQueryInProgress)
            return;

        webQueryInProgress = true;
        String url = String.format("%s/insert-table", FakeBluetoothServer.webServiceBaseUrl);
        StringEntity requestBody = null;
        try {
            requestBody = new StringEntity(jo.toString());
        } catch (UnsupportedEncodingException uee) {
            uee.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        AsyncHttpClient client = new AsyncHttpClient();

        client.post(activity, url, requestBody, "application/json",
                new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                System.out.println("Starting get call...");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                webQueryInProgress = false;
                Intent i = new Intent(WORD_CLOUD_UPDATED_FILTER);
                webResponse = new String(response);
                i.putExtra("cyberscouterwordcloud", "updated");
                activity.sendBroadcast(i);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                webQueryInProgress = false;
                MessageBox.showMessageBox(activity,
                        "Update of Match Scouting Records Failed", "CyberScouterWordCloud.setWordCloudWebService",
                        String.format("Can't update scouted match.\nContact a scouting mentor right away\n\n%s\n",
                                e.getMessage()));
            }

            @Override
            public void onRetry(int retryNo) {
                System.out.println(String.format("Retry number %d", retryNo));
            }
        });
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

    public int getEventID() {
        return EventID;
    }

    public int getMatchID() {
        return MatchID;
    }

    public int getWordCloudID() {
        return WordCloudID;
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

    public void setWordCloudID(int WordCloudID) {
        WordCloudID = WordCloudID;
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
