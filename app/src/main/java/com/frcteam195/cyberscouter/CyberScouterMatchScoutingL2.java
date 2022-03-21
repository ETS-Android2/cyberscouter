package com.frcteam195.cyberscouter;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Locale;
import java.util.Vector;

import cz.msebera.android.httpclient.Header;

public class CyberScouterMatchScoutingL2 {
    final static String MATCH_SCOUTING_L2_UPDATED_FILTER = "frcteam195_cyberscoutermatchscoutingl2_match_scouting_l2_updated_intent_filter";

    private static boolean webQueryInProgress = false;

    private static String webResponse;

    static String getWebResponse() {
        return (webResponse);
    }

    private int matchScoutingL2ID;
    private int eventID;
    private int matchID;
    private int matchNo;
    private int computerID;
    private int scouterID;
    private int reviewerID;
    private int scoutingStatus;
    private String teamRed;
    private String teamBlue;
    private int matchScoutingIDRed;
    private int matchScoutingIDBlue;
    private String commentRed;
    private String commentBlue;
    private int allianceStationID;
    private boolean matchEnded;
    private boolean complete;
    private int uploadStatus;
    private int t1MultiRobotDefense;
    private int t2MultiRobotDefense;
    private int t3MultiRobotDefense;
    private int t1PrimaryDefense;
    private int t2PrimaryDefense;
    private int t3PrimaryDefense;
    private int t1SecondaryDefense;
    private int t2SecondaryDefense;
    private int t3SecondaryDefense;
    private int t1HowLong;
    private int t2HowLong;
    private int t3HowLong;

    public String toJSON() {
        String json = "";

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("matchScoutingL2Id", getMatchScoutingL2ID());
            jsonObject.put("eventID", getEventID());
            jsonObject.put("matchID", getMatchID());
            jsonObject.put("computerID", getComplete());
            jsonObject.put("scouterID", getScouterID());

            json = jsonObject.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return json;
    }

    static String getMatchesL2Remote(AppCompatActivity activity, SQLiteDatabase db, int eventId) {
        int last_hash = CyberScouterTimeCode.getLast_update(db);
        String ret = null;
        try {
            BluetoothComm btcomm = new BluetoothComm();
            String response = btcomm.getMatchesL2(activity, eventId, last_hash);
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

    public String setMatchesRemote(AppCompatActivity activity) {
        String ret = "failed";
        try {
            JSONObject jo = new JSONObject();
            jo.put("cmd", "put-match-scouting-l2");
            jo.put("key", matchScoutingL2ID);
            JSONObject payload = new JSONObject();
            payload.put(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_SCOUTINGSTATUS, ScoutingStatus.FINISHED_SUCCESSFULLY);
            payload.put(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_COMMENT_RED, commentRed);
            payload.put(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_COMMENT_BLUE, commentBlue);
            payload.put(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_COMPUTERID, computerID);
            payload.put(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_SCOUTERID, scouterID);

            jo.put("payload", payload);

            BluetoothComm btcomm = new BluetoothComm();
            String response = btcomm.sendSetCommand(activity, jo);
            if (null != response) {
                response = response.replace("x03", "");
                JSONObject jresp = new JSONObject(response);
                ret = jresp.getString("result");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    // Gets the next sequential un-scouted match for the current scouter station
    static CyberScouterMatchScoutingL2 getCurrentMatch(SQLiteDatabase db, int l_allianceStationID) {
        String selection = CyberScouterContract.MatchScoutingL2.COLUMN_NAME_SCOUTINGSTATUS + " = ? AND " +
                CyberScouterContract.MatchScoutingL2.COLUMN_NAME_ALLIANCESTATIONID + " = ? AND " +
                CyberScouterContract.MatchScoutingL2.COLUMN_NAME_UPLOADSTATUS + " = ?";
        String[] selectionArgs = {
                String.format(Locale.getDefault(), "%d", ScoutingStatus.UNSCOUTED),
                String.format(Locale.getDefault(), "%d", l_allianceStationID),
                String.format(Locale.getDefault(), "%d", UploadStatus.NOT_UPLOADED)
        };
        String sortOrder =
                CyberScouterContract.MatchScoutingL2.COLUMN_NAME_MATCH_NUMBER + " ASC";

        CyberScouterMatchScoutingL2[] csmv = getLocalMatchesL2(db, selection, selectionArgs, sortOrder);
        if (null != csmv && 0 < csmv.length) {
            return (csmv[0]);
        } else
            return null;
    }

    // Gets the next sequential unscouted match for all scouter stations
    static CyberScouterMatchScoutingL2[] getCurrentMatchAllTeams(SQLiteDatabase db, int l_matchNo, int l_matchID) {
        String selection = CyberScouterContract.MatchScoutingL2.COLUMN_NAME_MATCH_NUMBER + " = ? AND " + CyberScouterContract.MatchScoutingL2.COLUMN_NAME_MATCHID + " = ?";
        String[] selectionArgs = {
                String.format(Locale.getDefault(), "%d", l_matchNo),
                String.format(Locale.getDefault(), "%d", l_matchID)
        };
        String sortOrder =
                CyberScouterContract.MatchScoutingL2.COLUMN_NAME_ALLIANCESTATIONID + " ASC";

        return (getLocalMatchesL2(db, selection, selectionArgs, sortOrder));
    }

    // Returns only the matches that are unscouted
    private static CyberScouterMatchScoutingL2 getLocalMatch(SQLiteDatabase db, int l_eventID, int l_matchID, int l_allianceStationID) throws Exception {
        String selection =
                CyberScouterContract.MatchScoutingL2.COLUMN_NAME_EVENTID + " = ? AND "
                        + CyberScouterContract.MatchScoutingL2.COLUMN_NAME_MATCHID + " = ? AND "
                        + CyberScouterContract.MatchScoutingL2.COLUMN_NAME_ALLIANCESTATIONID + " = ?";
        String[] selectionArgs = {
                String.format(Locale.getDefault(), "%d", l_eventID),
                String.format(Locale.getDefault(), "%d", l_matchID),
                String.format(Locale.getDefault(), "%d", l_allianceStationID)
        };

        CyberScouterMatchScoutingL2[] csmv = getLocalMatchesL2(db, selection, selectionArgs, null);

        if (null != csmv) {
            if (1 < csmv.length) {
                throw new Exception(String.format(Locale.getDefault(), "Too many match scouting rows found.  Wanted %d, found %d!\n\nEventID=%d, MatchID=%d, AllianceStationID=%d",
                        1, csmv.length, l_eventID, l_matchID, l_allianceStationID));
            } else
                return (csmv[0]);
        } else
            return null;
    }

    static CyberScouterMatchScoutingL2[] getMatchesReadyToUpload(SQLiteDatabase db, int l_eventID, int l_allianceStationID) {

        String selection =
                CyberScouterContract.MatchScoutingL2.COLUMN_NAME_EVENTID + " = ? AND "
                        + CyberScouterContract.MatchScoutingL2.COLUMN_NAME_ALLIANCESTATIONID + " = ? AND "
                        + CyberScouterContract.MatchScoutingL2.COLUMN_NAME_UPLOADSTATUS + " = ?";

        String[] selectionArgs = {
                String.format(Locale.getDefault(), "%d", l_eventID),
                String.format(Locale.getDefault(), "%d", l_allianceStationID),
                String.format(Locale.getDefault(), "%d", UploadStatus.READY_TO_UPLOAD)
        };

        String sortOrder =
                CyberScouterContract.MatchScoutingL2.COLUMN_NAME_MATCH_NUMBER + " ASC";

        return (getLocalMatchesL2(db, selection, selectionArgs, sortOrder));
    }

    private static CyberScouterMatchScoutingL2[] getLocalMatchesL2(SQLiteDatabase db, String selection, String[] selectionArgs, String sortOrder) {
        CyberScouterMatchScoutingL2 csm;
        Vector<CyberScouterMatchScoutingL2> csmv = new Vector<>();

        Cursor cursor = null;
        try {
            String[] projection = {
                    CyberScouterContract.MatchScoutingL2.COLUMN_NAME_MATCHSCOUTINGL2ID,
                    CyberScouterContract.MatchScoutingL2.COLUMN_NAME_EVENTID,
                    CyberScouterContract.MatchScoutingL2.COLUMN_NAME_MATCHID,
                    CyberScouterContract.MatchScoutingL2.COLUMN_NAME_MATCH_NUMBER,
                    CyberScouterContract.MatchScoutingL2.COLUMN_NAME_COMPUTERID,
                    CyberScouterContract.MatchScoutingL2.COLUMN_NAME_SCOUTERID,
                    CyberScouterContract.MatchScoutingL2.COLUMN_NAME_REVIEWERID,
                    CyberScouterContract.MatchScoutingL2.COLUMN_NAME_TEAM_RED,
                    CyberScouterContract.MatchScoutingL2.COLUMN_NAME_TEAM_BLUE,
                    CyberScouterContract.MatchScoutingL2.COLUMN_NAME_MATCHSCOUTINGIDRED,
                    CyberScouterContract.MatchScoutingL2.COLUMN_NAME_MATCHSCOUTINGIDBLUE,
                    CyberScouterContract.MatchScoutingL2.COLUMN_NAME_COMMENT_RED,
                    CyberScouterContract.MatchScoutingL2.COLUMN_NAME_COMMENT_BLUE,
                    CyberScouterContract.MatchScoutingL2.COLUMN_NAME_ALLIANCESTATIONID,
                    CyberScouterContract.MatchScoutingL2.COLUMN_NAME_MATCHENDED,
                    CyberScouterContract.MatchScoutingL2.COLUMN_NAME_SCOUTINGSTATUS,
                    CyberScouterContract.MatchScoutingL2.COLUMN_NAME_COMPLETE,
                    CyberScouterContract.MatchScoutingL2.COLUMN_NAME_UPLOADSTATUS,
                    CyberScouterContract.MatchScoutingL2.COLUMN_NAME_T1MULTIROBOTDEFENSE,
                    CyberScouterContract.MatchScoutingL2.COLUMN_NAME_T2MULTIROBOTDEFENSE,
                    CyberScouterContract.MatchScoutingL2.COLUMN_NAME_T3MULTIROBOTDEFENSE,
                    CyberScouterContract.MatchScoutingL2.COLUMN_NAME_T1PRIMARYDEFENSE,
                    CyberScouterContract.MatchScoutingL2.COLUMN_NAME_T2PRIMARYDEFENSE,
                    CyberScouterContract.MatchScoutingL2.COLUMN_NAME_T3PRIMARYDEFENSE,
                    CyberScouterContract.MatchScoutingL2.COLUMN_NAME_T1SECONDARYDEFENSE,
                    CyberScouterContract.MatchScoutingL2.COLUMN_NAME_T2SECONDARYDEFENSE,
                    CyberScouterContract.MatchScoutingL2.COLUMN_NAME_T3SECONDARYDEFENSE,
                    CyberScouterContract.MatchScoutingL2.COLUMN_NAME_T1HOWLONG,
                    CyberScouterContract.MatchScoutingL2.COLUMN_NAME_T2HOWLONG,
                    CyberScouterContract.MatchScoutingL2.COLUMN_NAME_T3HOWLONG,
            };


            cursor = db.query(
                    CyberScouterContract.MatchScoutingL2.TABLE_NAME,   // The table to query
                    projection,             // The array of columns to return (pass null to get all)
                    selection,              // The columns for the WHERE clause
                    selectionArgs,          // The values for the WHERE clause
                    null,                   // don't group the rows
                    null,                   // don't filter by row groups
                    sortOrder               // The sort order
            );

            if (0 < cursor.getCount()) {
                while (cursor.moveToNext()) {
                    csm = new CyberScouterMatchScoutingL2();
                    /* Read the match information from SQLite */
                    csm.matchScoutingL2ID = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_MATCHSCOUTINGL2ID));
                    csm.eventID = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_EVENTID));
                    csm.matchID = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_MATCHID));
                    csm.matchNo = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_MATCH_NUMBER));
                    csm.computerID = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_COMPUTERID));
                    csm.scouterID = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_SCOUTERID));
                    csm.reviewerID = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_REVIEWERID));
                    csm.teamRed = cursor.getString(cursor.getColumnIndex(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_TEAM_RED));
                    csm.teamBlue = cursor.getString(cursor.getColumnIndex(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_TEAM_BLUE));
                    csm.matchScoutingIDRed = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_MATCHSCOUTINGIDRED));
                    csm.matchScoutingIDBlue = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_MATCHSCOUTINGIDBLUE));
                    csm.commentRed = cursor.getString(cursor.getColumnIndex(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_COMMENT_RED));
                    csm.commentBlue = cursor.getString(cursor.getColumnIndex(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_COMMENT_BLUE));
                    csm.allianceStationID = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_ALLIANCESTATIONID));
                    csm.matchEnded = (cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_MATCHENDED)) == 1);
                    csm.scoutingStatus = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_SCOUTINGSTATUS));
                    csm.complete = (cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_COMPLETE)) == 1);
                    csm.uploadStatus = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_UPLOADSTATUS));
                    csm.t1MultiRobotDefense = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_T1MULTIROBOTDEFENSE));
                    csm.t2MultiRobotDefense = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_T2MULTIROBOTDEFENSE));
                    csm.t3MultiRobotDefense = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_T3MULTIROBOTDEFENSE));
                    csm.t1PrimaryDefense = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_T1PRIMARYDEFENSE));
                    csm.t2PrimaryDefense = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_T2PRIMARYDEFENSE));
                    csm.t3PrimaryDefense = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_T3PRIMARYDEFENSE));
                    csm.t1SecondaryDefense = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_T1SECONDARYDEFENSE));
                    csm.t2SecondaryDefense = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_T2SECONDARYDEFENSE));
                    csm.t3SecondaryDefense = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_T3SECONDARYDEFENSE));
                    csm.t1HowLong = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_T1HOWLONG));
                    csm.t2HowLong = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_T2HOWLONG));
                    csm.t3HowLong = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_T3HOWLONG));

                    csmv.add(csm);
                }
            }

            if (0 < csmv.size()) {
                CyberScouterMatchScoutingL2[] csma = new CyberScouterMatchScoutingL2[csmv.size()];
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

    private static void setMatch(SQLiteDatabase db, JSONObject jo) throws Exception {

        ContentValues values = new ContentValues();
        values.put(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_MATCHSCOUTINGL2ID, jo.getInt(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_MATCHSCOUTINGL2ID));
        values.put(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_EVENTID, jo.getInt(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_EVENTID));
        values.put(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_MATCHID, jo.getInt(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_MATCHID));
        values.put(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_MATCH_NUMBER, jo.getInt(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_MATCH_NUMBER));
        values.put(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_COMPUTERID, jo.optInt(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_COMPUTERID));
        values.put(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_SCOUTERID, jo.optInt(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_SCOUTERID));
        values.put(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_REVIEWERID, jo.optInt(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_REVIEWERID));
        values.put(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_SCOUTINGSTATUS, jo.optInt(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_SCOUTINGSTATUS));
        values.put(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_TEAM_RED, jo.getString(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_TEAM_RED));
        values.put(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_TEAM_BLUE, jo.getString(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_TEAM_BLUE));
        values.put(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_MATCHSCOUTINGIDRED, jo.getString(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_MATCHSCOUTINGIDRED));
        values.put(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_MATCHSCOUTINGIDBLUE, jo.getString(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_MATCHSCOUTINGIDBLUE));
        values.put(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_COMMENT_RED, jo.getString(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_COMMENT_RED));
        values.put(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_COMMENT_BLUE, jo.getString(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_COMMENT_BLUE));
        values.put(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_ALLIANCESTATIONID, jo.getInt(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_ALLIANCESTATIONID));
        values.put(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_UPLOADSTATUS, UploadStatus.NOT_UPLOADED);
        values.put(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_T1MULTIROBOTDEFENSE, jo.getInt(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_T1MULTIROBOTDEFENSE));
        values.put(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_T2MULTIROBOTDEFENSE, jo.getInt(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_T2MULTIROBOTDEFENSE));
        values.put(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_T3MULTIROBOTDEFENSE, jo.getInt(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_T3MULTIROBOTDEFENSE));
        values.put(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_T1PRIMARYDEFENSE, jo.getInt(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_T1PRIMARYDEFENSE));
        values.put(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_T2PRIMARYDEFENSE, jo.getInt(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_T2PRIMARYDEFENSE));
        values.put(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_T3PRIMARYDEFENSE, jo.getInt(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_T3PRIMARYDEFENSE));
        values.put(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_T1SECONDARYDEFENSE, jo.getInt(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_T1SECONDARYDEFENSE));
        values.put(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_T2SECONDARYDEFENSE, jo.getInt(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_T2SECONDARYDEFENSE));
        values.put(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_T3SECONDARYDEFENSE, jo.getInt(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_T3SECONDARYDEFENSE));
        values.put(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_T1HOWLONG, jo.getInt(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_T1HOWLONG));
        values.put(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_T2HOWLONG, jo.getInt(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_T2HOWLONG));
        values.put(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_T3HOWLONG, jo.getInt(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_T3HOWLONG));

        long newRowId = db.insert(CyberScouterContract.MatchScoutingL2.TABLE_NAME, null, values);
        if (-1 == newRowId) {
            throw (new Exception("An invalid row id was generated by the SQLite database insert command"));
        }
    }

    static void deleteOldMatches(SQLiteDatabase db, int l_eventID) {

        String[] whereArgs = {String.format(Locale.getDefault(), "%d", l_eventID)};

        db.delete(CyberScouterContract.MatchScoutingL2.TABLE_NAME,
                CyberScouterContract.MatchScoutingL2.COLUMN_NAME_EVENTID + " <> ?", whereArgs);
    }

    private static void updateMatchTeamAndScoutingStatus(SQLiteDatabase db, JSONObject rmatch, CyberScouterMatchScoutingL2 lmatch) throws Exception {

        // if this is a match that we've finished scouting but hasn't been uploaded yet...
        if (UploadStatus.READY_TO_UPLOAD == lmatch.getUploadStatus())
            return;

        ContentValues values = new ContentValues();
        values.put(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_TEAM_RED, rmatch.getString("TeamRed"));
        values.put(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_TEAM_BLUE, rmatch.getString("TeamBlue"));
        values.put(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_SCOUTINGSTATUS, rmatch.optInt("ScoutingStatus"));

        String selection = CyberScouterContract.MatchScoutingL2.COLUMN_NAME_MATCHSCOUTINGL2ID + " = ?";
        String[] selectionArgs = {String.format(Locale.getDefault(), "%d", lmatch.matchScoutingL2ID)};

        if (1 > updateMatch(db, values, selection, selectionArgs))
            throw new Exception(String.format("An error occurred while updating the local match scouting table.\n\nNo rows were updated for MatchScoutingID=%d", lmatch.matchScoutingL2ID));
    }

    static void updateMatchMetric(SQLiteDatabase db, String[] lColumns, Integer[] lValues, CyberScouterConfig cfg) throws Exception {
        CyberScouterMatchScoutingL2 csms = getCurrentMatch(db, TeamMap.getNumberForTeam(cfg.getAlliance_station()));
        if (null == csms)
            throw new Exception(String.format("No current unscouted match was found!  Attempt to update a match statistic failed!\n\nRole=%s", cfg.getAlliance_station()));
        if (null == lColumns || null == lValues || lColumns.length != lValues.length)
            throw new Exception(String.format("Bad request! Attempt to update a match statistic failed!\n\nRole=%s", cfg.getAlliance_station()));

        ContentValues values = new ContentValues();
        for (int i = 0; i < lColumns.length; ++i) {
            values.put(lColumns[i], lValues[i]);
        }

        String selection = CyberScouterContract.MatchScoutingL2.COLUMN_NAME_MATCHSCOUTINGL2ID + " = ?";
        String[] selectionArgs = {
                String.format(Locale.getDefault(), "%d", csms.matchScoutingL2ID)
        };

        if (1 > updateMatch(db, values, selection, selectionArgs))
            throw new Exception(String.format("An error occurred while updating the local match scouting table.\n\nNo rows were updated for MatchScoutingID=%d", csms.matchScoutingL2ID));
    }

    static void updateMatchUploadStatus(SQLiteDatabase db, int l_matchScoutingL2ID, int newStatus) throws Exception {
        ContentValues values = new ContentValues();
        values.put(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_UPLOADSTATUS, newStatus);

        String selection = CyberScouterContract.MatchScoutingL2.COLUMN_NAME_MATCHSCOUTINGL2ID + " = ?";
        String[] selectionArgs = {
                String.format(Locale.getDefault(), "%d", l_matchScoutingL2ID)
        };

        if (1 > updateMatch(db, values, selection, selectionArgs))
            throw new Exception(String.format("An error occurred while updating the local match scouting table.\n\nNo rows were updated for MatchScoutingID=%d", l_matchScoutingL2ID));

    }

    static void skipMatch(SQLiteDatabase db, int l_matchScoutingL2ID) throws Exception {
        ContentValues values = new ContentValues();
        values.put(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_UPLOADSTATUS, UploadStatus.SKIPPED);
        String selection = CyberScouterContract.MatchScoutingL2.COLUMN_NAME_MATCHSCOUTINGL2ID + " = ?";
        String[] selectionArgs = {
                String.format(Locale.getDefault(), "%d", l_matchScoutingL2ID)
        };

        if (1 > updateMatch(db, values, selection, selectionArgs))
            throw new Exception(String.format("An error occurred while updating the local match scouting table.\n\nNo rows were updated for MatchScoutingID=%d", l_matchScoutingL2ID));
    }

    static void submitMatch(SQLiteDatabase db, int l_matchScoutingL2ID) throws Exception {
        ContentValues values = new ContentValues();
        values.put(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_UPLOADSTATUS, UploadStatus.READY_TO_UPLOAD);
        values.put(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_SCOUTINGSTATUS, ScoutingStatus.FINISHED_SUCCESSFULLY);
        String selection = CyberScouterContract.MatchScoutingL2.COLUMN_NAME_MATCHSCOUTINGL2ID + " = ?";
        String[] selectionArgs = {
                String.format(Locale.getDefault(), "%d", l_matchScoutingL2ID)
        };

        if (1 > updateMatch(db, values, selection, selectionArgs))
            throw new Exception(String.format("An error occurred while updating the local match scouting table.\n\nNo rows were updated for MatchScoutingID=%d", l_matchScoutingL2ID));
    }

    static void submitMatchForReview(SQLiteDatabase db, int l_matchScoutingL2ID) throws Exception {
        ContentValues values = new ContentValues();
        values.put(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_UPLOADSTATUS, UploadStatus.READY_TO_UPLOAD);
        values.put(CyberScouterContract.MatchScoutingL2.COLUMN_NAME_SCOUTINGSTATUS, ScoutingStatus.NEEDS_REVIEW);
        String selection = CyberScouterContract.MatchScoutingL2.COLUMN_NAME_MATCHSCOUTINGL2ID + " = ?";
        String[] selectionArgs = {
                String.format(Locale.getDefault(), "%d", l_matchScoutingL2ID)
        };

        if (1 > updateMatch(db, values, selection, selectionArgs))
            throw new Exception(String.format("An error occurred while updating the local match scouting table.\n\nNo rows were updated for MatchScoutingID=%d", l_matchScoutingL2ID));
    }

    private static int updateMatch(SQLiteDatabase db, ContentValues values, String selection, String[] selectionArgs) {
        return db.update(
                CyberScouterContract.MatchScoutingL2.TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }

    // If there are new match scouting records, insert them into the local database.
    // Otherwise, update the scouting status and the team for each match, to keep that info current
    static String mergeMatches(SQLiteDatabase db, String remoteMatchesJson) throws Exception {
        int updated = 0, inserted = 0;

        JSONArray ja = new JSONArray(remoteMatchesJson);

        for (int i = 0; i < ja.length(); ++i) {
            JSONObject jo = ja.getJSONObject(i);
            CyberScouterMatchScoutingL2 lmatch = getLocalMatch(db, jo.getInt("EventID"), jo.getInt("MatchID"), jo.getInt("AllianceStationID"));
            if (null != lmatch) {
                updateMatchTeamAndScoutingStatus(db, jo, lmatch);
                updated++;
            } else {
                setMatch(db, jo);
                inserted++;
            }
        }

        return (String.format(Locale.getDefault(), "%d matches inserted, %d matches updated", inserted, updated));
    }

    static void getMatchesL2WebService(final Activity activity, int eventId) {

        if (webQueryInProgress)
            return;

        webQueryInProgress = true;

        RequestParams params = new RequestParams();
        params.put("eventId", eventId);
        String url = String.format("%s/match-scouting-l2", FakeBluetoothServer.webServiceBaseUrl);

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                System.out.println("Starting get call...");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                webQueryInProgress = false;
                Intent i = new Intent(MATCH_SCOUTING_L2_UPDATED_FILTER);
                webResponse = new String(response);
                i.putExtra("cyberscoutermatchesl2", "fetch");
                activity.sendBroadcast(i);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                webQueryInProgress = false;
                MessageBox.showMessageBox(activity,
                        "Fetch of Match Scouting L2 Records Failed",
                        "CyberScouterMatchScoutingL2.getMatchScoutingL2WebService",
                        String.format(
                                "Can't get list of L2 matches to scout.\nContact a scouting mentor right away\n\n%s\n",
                                e.getMessage()));
            }

            @Override
            public void onRetry(int retryNo) {
                System.out.println(String.format("Retry number %d", retryNo));
            }
        });
    }


    int getMatchScoutingL2ID() {
        return matchScoutingL2ID;
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

    int getComputerID() {
        return computerID;
    }

    int getScouterID() {
        return scouterID;
    }

    int getReviewerID() {
        return reviewerID;
    }

    String getTeamRed() {
        return teamRed;
    }

    String getTeamBlue() {
        return teamBlue;
    }

    int getMatchScoutingIDRed() {
        return matchScoutingIDRed;
    }

    int getMatchScoutingIDBlue() {
        return matchScoutingIDBlue;
    }

    String getCommentRed() {
        return commentRed;
    }

    String getCommentBlue() {
        return commentBlue;
    }

    int getAllianceStationID() {
        return allianceStationID;
    }

    int getUploadStatus() {
        return uploadStatus;
    }

    boolean getMatchEnded() {
        return matchEnded;
    }

    int getScoutingStatus() {
        return scoutingStatus;
    }

    boolean getComplete() {
        return complete;
    }

    int getT1MultiRobotDefense() { return t1MultiRobotDefense; }

    int getT2MultiRobotDefense() { return t2MultiRobotDefense; }

    int getT3MultiRobotDefense() { return t3MultiRobotDefense; }

    int getT1PrimaryDefense() { return t1PrimaryDefense; }

    int getT2PrimaryDefense() { return t2PrimaryDefense; }

    int getT3PrimaryDefense() { return t3PrimaryDefense; }

    int getT1SecondaryDefense() { return t1SecondaryDefense; }

    int getT2SecondaryDefense() { return t2SecondaryDefense; }

    int getT3SecondaryDefense() { return t3SecondaryDefense; }

    int getT1HowLong() { return t1HowLong; }

    int getT2HowLong() { return t2HowLong; }

    int getT3HowLong() { return t3HowLong; }

    void setMatchScoutingL2ID(int matchScoutingL2ID) {
        this.matchScoutingL2ID = matchScoutingL2ID;
    }

    void setEventID(int eventID) {
        this.eventID = eventID;
    }

    void setMatchID(int matchID) {
        this.matchID = matchID;
    }

    void setComputerID(int computerID) {
        this.computerID = computerID;
    }

    void setScouterID(int scouterID) {
        this.scouterID = scouterID;
    }
}
