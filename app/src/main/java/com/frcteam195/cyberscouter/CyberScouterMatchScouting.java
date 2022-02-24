package com.frcteam195.cyberscouter;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.Vector;

class CyberScouterMatchScouting {
    final static String MATCH_SCOUTING_UPDATED_FILTER = "frcteam195_cyberscoutermatchscouting_match_scouting_updated_intent_filter";

    private static boolean webQueryInProgress = false;

    private static String webResponse;

    static String getWebResponse() {
        return (webResponse);
    }

    private int matchScoutingID;
    private int eventID;
    private int matchID;
    private int matchNo;
    private int computerID;
    private int scouterID;
    private int reviewerID;
    private String team;
    private int teamMatchNo;
    private int allianceStationID;

    private int autoStartPos;
    private int autoPreload;
    private int autoDidNotShow;
    private int autoMoveBonus;
    private int autoBallLow;
    private int autoBallHigh;
    private int autoBallMiss;
    private int autoBallPos1;
    private int autoBallPos2;
    private int autoBallPos3;
    private int autoBallPos4;
    private int autoBallPos5;
    private int autoBallPos6;
    private int autoBallPos7;
    private int autoBallPos8;
    private int autoBallPos9;
    private int autoBallPos10;

    private int teleBallLow;
    private int teleBallHigh;
    private int teleBallMiss;

    private int climbStatus;
    private int rungClimbed;
    private int climbPosition;
    private int insteadOfClimb;

    private int summLaunchPad;
    private int summSortCargo;
    private int summShootDriving;
    private int summBrokeDown;
    private int summLostComm;
    private int summSubsystemBroke;
    private int summGroundPickup;
    private int summTerminalPickup;
    private int summHopperLoad;
    private int summPlayedDefense;
    private int summDefPlayedAgainst;

    private boolean matchEnded;
    private int scoutingStatus;
    private boolean complete;
    private int uploadStatus;

    public String toJSON() {
        String json = "";

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("matchScoutingId", getMatchScoutingID());
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

    static String getMatchesRemote(AppCompatActivity activity, SQLiteDatabase db, int eventId) {
        String ret = null;
        try {
            int last_hash = CyberScouterTimeCode.getLast_update(db);
            System.out.println(String.format(">>>>>>>>>>>>>>>>>>>>>>>LastUpdate=%d", last_hash));
            BluetoothComm btcomm = new BluetoothComm();
            String response = btcomm.getMatches(activity, eventId, last_hash);
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
            jo.put("cmd", "put-match-scouting");
            jo.put("key", matchScoutingID);
            JSONObject payload = new JSONObject();
            payload.put(CyberScouterContract.MatchScouting.COLUMN_NAME_SCOUTINGSTATUS, ScoutingStatus.FINISHED_SUCCESSFULLY);
            payload.put(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOSTARTPOS, autoStartPos);
            payload.put(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOPRELOAD, autoPreload);
            payload.put(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTODIDNOTSHOW, autoDidNotShow);
            payload.put(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOMOVEBONUS, autoMoveBonus);
            payload.put(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLLOW, autoBallLow);
            payload.put(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLHIGH, autoBallHigh);
            payload.put(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLMISS, autoBallMiss);
            payload.put(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS1, autoBallPos1);
            payload.put(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS2, autoBallPos2);
            payload.put(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS3, autoBallPos3);
            payload.put(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS4, autoBallPos4);
            payload.put(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS5, autoBallPos5);
            payload.put(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS6, autoBallPos6);
            payload.put(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS7, autoBallPos7);
            payload.put(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS8, autoBallPos8);
            payload.put(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS9, autoBallPos9);
            payload.put(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS10, autoBallPos10);
            payload.put(CyberScouterContract.MatchScouting.COLUMN_NAME_TELEBALLLOW, teleBallLow);
            payload.put(CyberScouterContract.MatchScouting.COLUMN_NAME_TELEBALLHIGH, teleBallHigh);
            payload.put(CyberScouterContract.MatchScouting.COLUMN_NAME_TELEBALLMISS, teleBallMiss);
            payload.put(CyberScouterContract.MatchScouting.COLUMN_NAME_CLIMBSTATUS, climbStatus);
            payload.put(CyberScouterContract.MatchScouting.COLUMN_NAME_CLIMBHEIGHT, rungClimbed);
            payload.put(CyberScouterContract.MatchScouting.COLUMN_NAME_CLIMBPOSITION, climbPosition);
            payload.put(CyberScouterContract.MatchScouting.COLUMN_NAME_INSTEADOFCLIMB, insteadOfClimb);
            payload.put(CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMLAUNCHPAD, summLaunchPad);
            payload.put(CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMSORTCARGO, summSortCargo);
            payload.put(CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMSHOOTDRIVING, summShootDriving);
            payload.put(CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMBROKEDOWN, summBrokeDown);
            payload.put(CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMLOSTCOMM, summLostComm);
            payload.put(CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMSUBSYSTEMBROKE, summSubsystemBroke);
            payload.put(CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMGROUNDPICKUP, summGroundPickup);
            payload.put(CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMTERMINALPICKUP, summTerminalPickup);
            payload.put(CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMPLAYEDDEFENSE, summPlayedDefense);
            payload.put(CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMDEFPLAYEDAGAINST, summDefPlayedAgainst);
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
    static CyberScouterMatchScouting getCurrentMatch(SQLiteDatabase db, int l_allianceStationID) {
        String selection = CyberScouterContract.MatchScouting.COLUMN_NAME_SCOUTINGSTATUS + " = ? AND " +
                CyberScouterContract.MatchScouting.COLUMN_NAME_ALLIANCESTATIONID + " = ? AND " +
                CyberScouterContract.MatchScouting.COLUMN_NAME_UPLOADSTATUS + " = ?";
        String[] selectionArgs = {
                String.format(Locale.getDefault(), "%d", ScoutingStatus.UNSCOUTED),
                String.format(Locale.getDefault(), "%d", l_allianceStationID),
                String.format(Locale.getDefault(), "%d", UploadStatus.NOT_UPLOADED)
        };
        String sortOrder =
                CyberScouterContract.MatchScouting.COLUMN_NAME_TEAMMATCHNO + " ASC";

        CyberScouterMatchScouting[] csmv = getLocalMatches(db, selection, selectionArgs, sortOrder);
        if (null != csmv && 0 < csmv.length) {
            return (csmv[0]);
        } else
            return null;
    }

    // Gets the next sequential unscouted match for all scouter stations
    static CyberScouterMatchScouting[] getCurrentMatchAllTeams(SQLiteDatabase db, int l_matchNo, int l_matchID) {
        String selection = CyberScouterContract.MatchScouting.COLUMN_NAME_MATCH_NUMBER + " = ? AND " + CyberScouterContract.MatchScouting.COLUMN_NAME_MATCHID + " = ?";
        String[] selectionArgs = {
                String.format(Locale.getDefault(), "%d", l_matchNo),
                String.format(Locale.getDefault(), "%d", l_matchID)
        };
        String sortOrder =
                CyberScouterContract.MatchScouting.COLUMN_NAME_ALLIANCESTATIONID + " ASC";

        return (getLocalMatches(db, selection, selectionArgs, sortOrder));
    }

    // Returns only the matches that are unscouted
    private static CyberScouterMatchScouting getLocalMatch(SQLiteDatabase db, int l_eventID, int l_matchID, int l_allianceStationID) throws Exception {
        String selection =
                CyberScouterContract.MatchScouting.COLUMN_NAME_EVENTID + " = ? AND "
                        + CyberScouterContract.MatchScouting.COLUMN_NAME_MATCHID + " = ? AND "
                        + CyberScouterContract.MatchScouting.COLUMN_NAME_ALLIANCESTATIONID + " = ?";
        String[] selectionArgs = {
                String.format(Locale.getDefault(), "%d", l_eventID),
                String.format(Locale.getDefault(), "%d", l_matchID),
                String.format(Locale.getDefault(), "%d", l_allianceStationID)
        };

        CyberScouterMatchScouting[] csmv = getLocalMatches(db, selection, selectionArgs, null);

        if (null != csmv) {
            if (1 < csmv.length) {
                throw new Exception(String.format(Locale.getDefault(), "Too many match scouting rows found.  Wanted %d, found %d!\n\nEventID=%d, MatchID=%d, AllianceStationID=%d",
                        1, csmv.length, l_eventID, l_matchID, l_allianceStationID));
            } else
                return (csmv[0]);
        } else
            return null;
    }

    static CyberScouterMatchScouting[] getMatchesReadyToUpload(SQLiteDatabase db, int l_eventID, int l_allianceStationID) {

        String selection =
                CyberScouterContract.MatchScouting.COLUMN_NAME_EVENTID + " = ? AND "
                        + CyberScouterContract.MatchScouting.COLUMN_NAME_ALLIANCESTATIONID + " = ? AND "
                        + CyberScouterContract.MatchScouting.COLUMN_NAME_UPLOADSTATUS + " = ?";

        String[] selectionArgs = {
                String.format(Locale.getDefault(), "%d", l_eventID),
                String.format(Locale.getDefault(), "%d", l_allianceStationID),
                String.format(Locale.getDefault(), "%d", UploadStatus.READY_TO_UPLOAD)
        };

        String sortOrder =
                CyberScouterContract.MatchScouting.COLUMN_NAME_TEAMMATCHNO + " ASC";

        return (getLocalMatches(db, selection, selectionArgs, sortOrder));
    }

    private static CyberScouterMatchScouting[] getLocalMatches(SQLiteDatabase db, String selection, String[] selectionArgs, String sortOrder) {
        CyberScouterMatchScouting csm;
        Vector<CyberScouterMatchScouting> csmv = new Vector<>();

        Cursor cursor = null;
        try {
            String[] projection = {
                    CyberScouterContract.MatchScouting.COLUMN_NAME_MATCHSCOUTINGID,
                    CyberScouterContract.MatchScouting.COLUMN_NAME_EVENTID,
                    CyberScouterContract.MatchScouting.COLUMN_NAME_MATCHID,
                    CyberScouterContract.MatchScouting.COLUMN_NAME_MATCH_NUMBER,
                    CyberScouterContract.MatchScouting.COLUMN_NAME_COMPUTERID,
                    CyberScouterContract.MatchScouting.COLUMN_NAME_SCOUTERID,
                    CyberScouterContract.MatchScouting.COLUMN_NAME_REVIEWERID,
                    CyberScouterContract.MatchScouting.COLUMN_NAME_TEAM,
                    CyberScouterContract.MatchScouting.COLUMN_NAME_TEAMMATCHNO,
                    CyberScouterContract.MatchScouting.COLUMN_NAME_ALLIANCESTATIONID,
                    CyberScouterContract.MatchScouting.COLUMN_NAME_MATCHENDED,
                    CyberScouterContract.MatchScouting.COLUMN_NAME_SCOUTINGSTATUS,
                    CyberScouterContract.MatchScouting.COLUMN_NAME_AREASTOREVIEW,
                    CyberScouterContract.MatchScouting.COLUMN_NAME_COMPLETE,
                    CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOSTARTPOS,
                    CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOPRELOAD,
                    CyberScouterContract.MatchScouting.COLUMN_NAME_AUTODIDNOTSHOW,
                    CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOMOVEBONUS,
                    CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLLOW,
                    CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLHIGH,
                    CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLMISS,
                    CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS1,
                    CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS2,
                    CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS3,
                    CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS4,
                    CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS5,
                    CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS6,
                    CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS7,
                    CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS8,
                    CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS9,
                    CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS10,
                    CyberScouterContract.MatchScouting.COLUMN_NAME_TELEBALLLOW,
                    CyberScouterContract.MatchScouting.COLUMN_NAME_TELEBALLHIGH,
                    CyberScouterContract.MatchScouting.COLUMN_NAME_TELEBALLMISS,
                    CyberScouterContract.MatchScouting.COLUMN_NAME_CLIMBSTATUS,
                    CyberScouterContract.MatchScouting.COLUMN_NAME_CLIMBHEIGHT,
                    CyberScouterContract.MatchScouting.COLUMN_NAME_CLIMBPOSITION,
                    CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMLAUNCHPAD,
                    CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMSORTCARGO,
                    CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMSHOOTDRIVING,
                    CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMBROKEDOWN,
                    CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMLOSTCOMM,
                    CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMSUBSYSTEMBROKE,
                    CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMGROUNDPICKUP,
                    CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMTERMINALPICKUP,
                    CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMPLAYEDDEFENSE,
                    CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMDEFPLAYEDAGAINST,
                    CyberScouterContract.MatchScouting.COLUMN_NAME_UPLOADSTATUS};


            cursor = db.query(
                    CyberScouterContract.MatchScouting.TABLE_NAME,   // The table to query
                    projection,             // The array of columns to return (pass null to get all)
                    selection,              // The columns for the WHERE clause
                    selectionArgs,          // The values for the WHERE clause
                    null,                   // don't group the rows
                    null,                   // don't filter by row groups
                    sortOrder               // The sort order
            );

            if (0 < cursor.getCount()) {
                while (cursor.moveToNext()) {
                    csm = new CyberScouterMatchScouting();
                    /* Read the match information from SQLite */
                    csm.matchScoutingID = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScouting.COLUMN_NAME_MATCHSCOUTINGID));
                    csm.eventID = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScouting.COLUMN_NAME_EVENTID));
                    csm.matchID = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScouting.COLUMN_NAME_MATCHID));
                    csm.matchNo = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScouting.COLUMN_NAME_MATCH_NUMBER));
                    csm.computerID = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScouting.COLUMN_NAME_COMPUTERID));
                    csm.scouterID = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScouting.COLUMN_NAME_SCOUTERID));
                    csm.reviewerID = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScouting.COLUMN_NAME_REVIEWERID));
                    csm.team = cursor.getString(cursor.getColumnIndex(CyberScouterContract.MatchScouting.COLUMN_NAME_TEAM));
                    csm.teamMatchNo = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScouting.COLUMN_NAME_TEAMMATCHNO));
                    csm.allianceStationID = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScouting.COLUMN_NAME_ALLIANCESTATIONID));
                    csm.matchEnded = (cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScouting.COLUMN_NAME_MATCHENDED)) == 1);
                    csm.scoutingStatus = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScouting.COLUMN_NAME_SCOUTINGSTATUS));
                    csm.complete = (cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScouting.COLUMN_NAME_COMPLETE)) == 1);
                    if(cursor.isNull(cursor.getColumnIndex(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOPRELOAD))) {
                        csm.autoPreload = -1;
                    } else {
                        csm.autoStartPos = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOSTARTPOS));
                    }
                    csm.autoPreload = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOPRELOAD));
                    csm.autoDidNotShow = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTODIDNOTSHOW));
                    if(cursor.isNull(cursor.getColumnIndex(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOMOVEBONUS))){
                        csm.autoMoveBonus = -1;
                    } else {
                        csm.autoMoveBonus = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOMOVEBONUS));
                    }
                    csm.autoBallLow = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLLOW));
                    csm.autoBallHigh = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLHIGH));
                    csm.autoBallMiss = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLMISS));
                    csm.autoBallPos1 = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS1));
                    csm.autoBallPos2 = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS2));
                    csm.autoBallPos3 = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS3));
                    csm.autoBallPos4 = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS4));
                    csm.autoBallPos5 = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS5));
                    csm.autoBallPos6 = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS6));
                    csm.autoBallPos7 = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS7));
                    csm.autoBallPos8 = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS8));
                    csm.autoBallPos9 = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS9));
                    csm.autoBallPos10 = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS10));
                    csm.teleBallHigh = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScouting.COLUMN_NAME_TELEBALLHIGH));
                    csm.teleBallLow = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScouting.COLUMN_NAME_TELEBALLLOW));
                    csm.teleBallMiss = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScouting.COLUMN_NAME_TELEBALLMISS));
                    csm.climbStatus = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScouting.COLUMN_NAME_CLIMBSTATUS));
                    csm.rungClimbed = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScouting.COLUMN_NAME_CLIMBHEIGHT));
                    csm.climbPosition = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScouting.COLUMN_NAME_CLIMBPOSITION));
                    csm.summLaunchPad = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMLAUNCHPAD));
                    csm.summSortCargo = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMSORTCARGO));
                    csm.summShootDriving = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMSHOOTDRIVING));
                    csm.summBrokeDown = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMBROKEDOWN));
                    csm.summLostComm = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMLOSTCOMM));
                    csm.summSubsystemBroke = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMSUBSYSTEMBROKE));
                    csm.summGroundPickup = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMGROUNDPICKUP));
                    csm.summTerminalPickup = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMTERMINALPICKUP));
                    csm.summPlayedDefense = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMPLAYEDDEFENSE));
                    csm.summDefPlayedAgainst = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMDEFPLAYEDAGAINST));
                    csm.uploadStatus = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.MatchScouting.COLUMN_NAME_UPLOADSTATUS));

                    csmv.add(csm);
                }
            }

            if (0 < csmv.size()) {
                CyberScouterMatchScouting[] csma = new CyberScouterMatchScouting[csmv.size()];
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
        values.put(CyberScouterContract.MatchScouting.COLUMN_NAME_MATCHSCOUTINGID, jo.getInt(CyberScouterContract.MatchScouting.COLUMN_NAME_MATCHSCOUTINGID));
        values.put(CyberScouterContract.MatchScouting.COLUMN_NAME_EVENTID, jo.getInt(CyberScouterContract.MatchScouting.COLUMN_NAME_EVENTID));
        values.put(CyberScouterContract.MatchScouting.COLUMN_NAME_MATCHID, jo.getInt(CyberScouterContract.MatchScouting.COLUMN_NAME_MATCHID));
        values.put(CyberScouterContract.MatchScouting.COLUMN_NAME_MATCH_NUMBER, jo.getInt(CyberScouterContract.MatchScouting.COLUMN_NAME_MATCH_NUMBER));
        values.put(CyberScouterContract.MatchScouting.COLUMN_NAME_COMPUTERID, jo.optInt(CyberScouterContract.MatchScouting.COLUMN_NAME_COMPUTERID));
        values.put(CyberScouterContract.MatchScouting.COLUMN_NAME_SCOUTERID, jo.optInt(CyberScouterContract.MatchScouting.COLUMN_NAME_SCOUTERID));
        values.put(CyberScouterContract.MatchScouting.COLUMN_NAME_REVIEWERID, jo.optInt(CyberScouterContract.MatchScouting.COLUMN_NAME_REVIEWERID));
        values.put(CyberScouterContract.MatchScouting.COLUMN_NAME_SCOUTINGSTATUS, jo.optInt(CyberScouterContract.MatchScouting.COLUMN_NAME_SCOUTINGSTATUS));
        values.put(CyberScouterContract.MatchScouting.COLUMN_NAME_TEAM, jo.getString(CyberScouterContract.MatchScouting.COLUMN_NAME_TEAM));
        values.put(CyberScouterContract.MatchScouting.COLUMN_NAME_TEAMMATCHNO, jo.optInt(CyberScouterContract.MatchScouting.COLUMN_NAME_TEAMMATCHNO));
        values.put(CyberScouterContract.MatchScouting.COLUMN_NAME_ALLIANCESTATIONID, jo.getInt(CyberScouterContract.MatchScouting.COLUMN_NAME_ALLIANCESTATIONID));
        values.put(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOSTARTPOS, jo.optInt(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOSTARTPOS));
        values.put(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOPRELOAD, jo.optInt(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOPRELOAD));
        values.put(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTODIDNOTSHOW, jo.optInt(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTODIDNOTSHOW));
        values.put(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOMOVEBONUS, jo.optInt(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOMOVEBONUS));
        values.put(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLLOW, jo.optInt(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLLOW));
        values.put(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLHIGH, jo.optInt(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLHIGH));
        values.put(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLMISS, jo.optInt(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLMISS));
        values.put(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS1, jo.optInt(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS1));
        values.put(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS2, jo.optInt(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS2));
        values.put(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS3, jo.optInt(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS3));
        values.put(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS4, jo.optInt(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS4));
        values.put(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS5, jo.optInt(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS5));
        values.put(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS6, jo.optInt(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS6));
        values.put(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS7, jo.optInt(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS7));
        values.put(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS8, jo.optInt(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS8));
        values.put(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS9, jo.optInt(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS9));
        values.put(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS10, jo.optInt(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS10));
        values.put(CyberScouterContract.MatchScouting.COLUMN_NAME_TELEBALLHIGH, jo.optInt(CyberScouterContract.MatchScouting.COLUMN_NAME_TELEBALLHIGH));
        values.put(CyberScouterContract.MatchScouting.COLUMN_NAME_TELEBALLLOW, jo.optInt(CyberScouterContract.MatchScouting.COLUMN_NAME_TELEBALLLOW));
        values.put(CyberScouterContract.MatchScouting.COLUMN_NAME_TELEBALLMISS, jo.optInt(CyberScouterContract.MatchScouting.COLUMN_NAME_TELEBALLMISS));
        values.put(CyberScouterContract.MatchScouting.COLUMN_NAME_CLIMBSTATUS, jo.optInt(CyberScouterContract.MatchScouting.COLUMN_NAME_CLIMBSTATUS));
        values.put(CyberScouterContract.MatchScouting.COLUMN_NAME_CLIMBHEIGHT, jo.optInt(CyberScouterContract.MatchScouting.COLUMN_NAME_CLIMBHEIGHT));
        values.put(CyberScouterContract.MatchScouting.COLUMN_NAME_CLIMBPOSITION, jo.optInt(CyberScouterContract.MatchScouting.COLUMN_NAME_CLIMBPOSITION));
        values.put(CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMLAUNCHPAD, jo.optInt(CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMLAUNCHPAD));
        values.put(CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMSORTCARGO, jo.optInt(CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMSORTCARGO));
        values.put(CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMSHOOTDRIVING, jo.optInt(CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMSHOOTDRIVING));
        values.put(CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMBROKEDOWN, jo.optInt(CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMBROKEDOWN));
        values.put(CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMLOSTCOMM, jo.optInt(CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMLOSTCOMM));
        values.put(CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMSUBSYSTEMBROKE, jo.optInt(CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMSUBSYSTEMBROKE));
        values.put(CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMGROUNDPICKUP, jo.optInt(CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMGROUNDPICKUP));
        values.put(CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMTERMINALPICKUP, jo.optInt(CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMTERMINALPICKUP));
        values.put(CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMPLAYEDDEFENSE, jo.optInt(CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMPLAYEDDEFENSE));
        values.put(CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMDEFPLAYEDAGAINST, jo.optInt(CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMDEFPLAYEDAGAINST));
        values.put(CyberScouterContract.MatchScouting.COLUMN_NAME_UPLOADSTATUS, UploadStatus.NOT_UPLOADED);

        long newRowId = db.insert(CyberScouterContract.MatchScouting.TABLE_NAME, null, values);
        if (-1 == newRowId) {
            throw (new Exception("An invalid row id was generated by the SQLite database insert command"));
        }
    }

    static void deleteOldMatches(SQLiteDatabase db, int l_eventID) {

        String[] whereArgs = {String.format(Locale.getDefault(), "%d", l_eventID)};

        db.delete(CyberScouterContract.MatchScouting.TABLE_NAME,
                CyberScouterContract.MatchScouting.COLUMN_NAME_EVENTID + " <> ?", whereArgs);
    }

    private static void updateMatchTeamAndScoutingStatus(SQLiteDatabase db, JSONObject rmatch, CyberScouterMatchScouting lmatch) throws Exception {

        // if this is a match that we've finished scouting but hasn't been uploaded yet...
        if (UploadStatus.READY_TO_UPLOAD == lmatch.getUploadStatus())
            return;

        ContentValues values = new ContentValues();
        values.put(CyberScouterContract.MatchScouting.COLUMN_NAME_TEAM, rmatch.getString("Team"));
        values.put(CyberScouterContract.MatchScouting.COLUMN_NAME_SCOUTINGSTATUS, rmatch.optInt("ScoutingStatus"));

        String selection = CyberScouterContract.MatchScouting.COLUMN_NAME_MATCHSCOUTINGID + " = ?";
        String[] selectionArgs = {String.format(Locale.getDefault(), "%d", lmatch.matchScoutingID)};

        if (1 > updateMatch(db, values, selection, selectionArgs))
            throw new Exception(String.format("An error occurred while updating the local match scouting table.\n\nNo rows were updated for MatchScoutingID=%d", lmatch.matchScoutingID));
    }

    static void updateMatchMetric(SQLiteDatabase db, String[] lColumns, Integer[] lValues, CyberScouterConfig cfg) throws Exception {
        CyberScouterMatchScouting csms = getCurrentMatch(db, TeamMap.getNumberForTeam(cfg.getAlliance_station()));
        if (null == csms)
            throw new Exception(String.format("No current unscouted match was found!  Attempt to update a match statistic failed!\n\nRole=%s", cfg.getAlliance_station()));
        if (null == lColumns || null == lValues || lColumns.length != lValues.length)
            throw new Exception(String.format("Bad request! Attempt to update a match statistic failed!\n\nRole=%s", cfg.getAlliance_station()));

        ContentValues values = new ContentValues();
        for (int i = 0; i < lColumns.length; ++i) {
            values.put(lColumns[i], lValues[i]);
        }

        String selection = CyberScouterContract.MatchScouting.COLUMN_NAME_MATCHSCOUTINGID + " = ?";
        String[] selectionArgs = {
                String.format(Locale.getDefault(), "%d", csms.matchScoutingID)
        };

        if (1 > updateMatch(db, values, selection, selectionArgs))
            throw new Exception(String.format("An error occurred while updating the local match scouting table.\n\nNo rows were updated for MatchScoutingID=%d", csms.matchScoutingID));
    }

    static void updateMatchUploadStatus(SQLiteDatabase db, int l_matchScoutingID, int newStatus) throws Exception {
        ContentValues values = new ContentValues();
        values.put(CyberScouterContract.MatchScouting.COLUMN_NAME_UPLOADSTATUS, newStatus);

        String selection = CyberScouterContract.MatchScouting.COLUMN_NAME_MATCHSCOUTINGID + " = ?";
        String[] selectionArgs = {
                String.format(Locale.getDefault(), "%d", l_matchScoutingID)
        };

        if (1 > updateMatch(db, values, selection, selectionArgs))
            throw new Exception(String.format("An error occurred while updating the local match scouting table.\n\nNo rows were updated for MatchScoutingID=%d", l_matchScoutingID));

    }

    static void skipMatch(SQLiteDatabase db, int l_matchScoutingID) throws Exception {
        ContentValues values = new ContentValues();
        values.put(CyberScouterContract.MatchScouting.COLUMN_NAME_UPLOADSTATUS, UploadStatus.SKIPPED);
        String selection = CyberScouterContract.MatchScouting.COLUMN_NAME_MATCHSCOUTINGID + " = ?";
        String[] selectionArgs = {
                String.format(Locale.getDefault(), "%d", l_matchScoutingID)
        };

        if (1 > updateMatch(db, values, selection, selectionArgs))
            throw new Exception(String.format("An error occurred while updating the local match scouting table.\n\nNo rows were updated for MatchScoutingID=%d", l_matchScoutingID));
    }

    static void submitMatch(SQLiteDatabase db, int l_matchScoutingID) throws Exception {
        ContentValues values = new ContentValues();
        values.put(CyberScouterContract.MatchScouting.COLUMN_NAME_UPLOADSTATUS, UploadStatus.READY_TO_UPLOAD);
        values.put(CyberScouterContract.MatchScouting.COLUMN_NAME_SCOUTINGSTATUS, ScoutingStatus.FINISHED_SUCCESSFULLY);
        String selection = CyberScouterContract.MatchScouting.COLUMN_NAME_MATCHSCOUTINGID + " = ?";
        String[] selectionArgs = {
                String.format(Locale.getDefault(), "%d", l_matchScoutingID)
        };

        if (1 > updateMatch(db, values, selection, selectionArgs))
            throw new Exception(String.format("An error occurred while updating the local match scouting table.\n\nNo rows were updated for MatchScoutingID=%d", l_matchScoutingID));
    }

    static void submitMatchForReview(SQLiteDatabase db, int l_matchScoutingID) throws Exception {
        ContentValues values = new ContentValues();
        values.put(CyberScouterContract.MatchScouting.COLUMN_NAME_UPLOADSTATUS, UploadStatus.READY_TO_UPLOAD);
        values.put(CyberScouterContract.MatchScouting.COLUMN_NAME_SCOUTINGSTATUS, ScoutingStatus.NEEDS_REVIEW);
        String selection = CyberScouterContract.MatchScouting.COLUMN_NAME_MATCHSCOUTINGID + " = ?";
        String[] selectionArgs = {
                String.format(Locale.getDefault(), "%d", l_matchScoutingID)
        };

        if (1 > updateMatch(db, values, selection, selectionArgs))
            throw new Exception(String.format("An error occurred while updating the local match scouting table.\n\nNo rows were updated for MatchScoutingID=%d", l_matchScoutingID));
    }

    private static int updateMatch(SQLiteDatabase db, ContentValues values, String selection, String[] selectionArgs) {
        return db.update(
                CyberScouterContract.MatchScouting.TABLE_NAME,
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
            CyberScouterMatchScouting lmatch = getLocalMatch(db, jo.getInt("EventID"), jo.getInt("MatchID"), jo.getInt("AllianceStationID"));
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

    static void getMatchesWebService(final Activity activity, int eventId) {

        if (webQueryInProgress)
            return;

        webQueryInProgress = true;
        RequestQueue rq = Volley.newRequestQueue(activity);
        String url = String.format("%s/match-scouting?eventId=%s", FakeBluetoothServer.webServiceBaseUrl, eventId);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        webQueryInProgress = false;
                        try {
                            Intent i = new Intent(MATCH_SCOUTING_UPDATED_FILTER);
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

                MessageBox.showMessageBox(activity, "Fetch of Match Scouting Records Failed", "CyberScouterMatchScouting.getMatchScoutingWebService",
                        String.format("Can't get list of matches to scout.\nContact a scouting mentor right away\n\n%s\n", msg));
            }
        });

        rq.add(stringRequest);
    }


    static void setMatchesWebService(final Activity activity, JSONObject jo) {

        if (webQueryInProgress)
            return;

        webQueryInProgress = true;
        RequestQueue rq = Volley.newRequestQueue(activity);
        String url = String.format("%s/update", FakeBluetoothServer.webServiceBaseUrl);
        String requestBody = jo.toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        webQueryInProgress = false;
                        try {
                            Intent i = new Intent(MATCH_SCOUTING_UPDATED_FILTER);
                            webResponse = response;
                            i.putExtra("cyberscoutermatches", "update");
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

                MessageBox.showMessageBox(activity, "Update of Match Scouting Records Failed", "CyberScouterMatchScouting.setMatchScoutingWebService",
                        String.format("Can't update scouted match.\nContact a scouting mentor right away\n\n%s\n", msg));
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String responseString = "";
                if (response != null) {
                    responseString = String.valueOf(response.statusCode);
                    // can get more details such as response.headers
                }
                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
            }
        };

        rq.add(stringRequest);
    }

    int getMatchScoutingID() {
        return matchScoutingID;
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

    String getTeam() {
        return team;
    }

    int getTeamMatchNo() {
        return teamMatchNo;
    }

    int getAllianceStationID() {
        return allianceStationID;
    }

    int getAutoStartPos() {return autoStartPos; }

    int getAutoPreload() {return autoPreload; }

    int getAutoDidNotShow() {
        return autoDidNotShow;
    }

    int getAutoMoveBonus() {
        return autoMoveBonus;
    }

    int getAutoBallLow() {
        return autoBallLow;
    }

    int getAutoBallHigh() {
        return autoBallHigh;
    }

    int getAutoBallMiss() {
        return autoBallMiss;
    }

    int getAutoBallPos1() {
        return autoBallPos1;
    }

    int getAutoBallPos2() {
        return autoBallPos2;
    }

    int getAutoBallPos3() {
        return autoBallPos3;
    }

    int getAutoBallPos4() {
        return autoBallPos4;
    }

    int getAutoBallPos5() {
        return autoBallPos5;
    }

    int getAutoBallPos6() {
        return autoBallPos6;
    }

    int getAutoBallPos7() {
        return autoBallPos7;
    }

    int getAutoBallPos8() {
        return autoBallPos8;
    }

    int getAutoBallPos9() {
        return autoBallPos9;
    }

    int getAutoBallPos10() {
        return autoBallPos10;
    }

    int getTeleBallLow() {
        return teleBallLow;
    }

    int getTeleBallHigh() {
        return teleBallHigh;
    }

    int getTeleBallMiss() {
        return teleBallMiss;
    }

    int getClimbStatus() {
        return rungClimbed;
    }

    int getClimbHeight() {
        return rungClimbed;
    }

    int getInsteadOfClimb() {return insteadOfClimb;}

    int getClimbPosition() {return climbPosition;}

    int getSummLaunchPad() {
        return summLaunchPad;
    }

    int getSummSortCargo() {
        return summSortCargo;
    }

    int getSummShootDriving() { return summShootDriving; }

    int getSummBrokeDown() {
        return summBrokeDown;
    }

    int getSummLostComm() {
        return summLostComm;
    }

    int getSummSubsystemBroke() {
        return summSubsystemBroke;
    }

    int getSummGroundPickup() {
        return summGroundPickup;
    }

    int getSummTerminalPickup() {
        return summTerminalPickup;
    }

    int getSummPlayedDefense() {
        return summPlayedDefense;
    }

    int getSummDefPlayedAgainst() {
        return summDefPlayedAgainst;
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


    void setMatchScoutingID(int matchScoutingID) {
        this.matchScoutingID = matchScoutingID;
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
