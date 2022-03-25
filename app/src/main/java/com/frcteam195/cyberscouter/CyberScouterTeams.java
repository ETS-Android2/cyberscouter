package com.frcteam195.cyberscouter;

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

import java.io.UnsupportedEncodingException;
import java.security.cert.PolicyNode;
import java.util.Locale;
import java.util.Vector;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

class CyberScouterTeams {
    public final static String TEAMS_UPDATED_FILTER = "frcteam195_cyberscouterteams_teams_updated_intent_filter";

    private static String webResponse;

    static String getWebResponse() {
        return (webResponse);
    }

    private static boolean webQueryInProgress = false;

    private int teamID;
    private int team;
    private String TeamName;
    private String TeamLocation;
    private String TeamCity;
    private String TeamStateProv;
    private String TeamCountry;
    private int NumWheels;
    private int NumDriveMotors;
    private int WheelTypeID;
    private int DriveTypeID;
    private int MotorTypeID;
    private int LanguageID;
    private int Speed;
    private String GearRatio;
    private int NumGearSpeed;
    private int RobotLength;
    private int RobotWidth;
    private int RobotHeight;
    private int RobotWeight;
    private int Pneumatics;
    private int IntakeType;
    private int Preload;
    private int HasAuto;
    private int AutoScoredHigh;
    private int AutoScoredLow;
    private int MoveBonus;
    private int AutoPickUp;
    private int AutoStartPosID;
    private String AutoSummary;
    private int AutoHuman;
    private int TeleBallsScoredHigh;
    private int TeleBallsScoredLow;
    private int MaxBallCapacity;
    private int TeleDefense;
    private int TeleDefenseEvade;
    private String TeleStrategy;
    private String TeleDefenseStrat;
    private int TeleSortCargo;
    private int TeleShootWhileDrive;
    private int CanClimb;
    private int ClimbPosition;
    private String ClimbStrategy;
    private int ClimbTime;
    private int ClimbHeightID;
    private int DoneScouting;
    private int UploadStatus;

    static String getTeamsRemote(AppCompatActivity activity, SQLiteDatabase db) {
        String ret = null;
        int last_hash = 0;

        try {
            last_hash = CyberScouterTimeCode.getLast_update(db);
            BluetoothComm btcomm = new BluetoothComm();
            String response = btcomm.getTeams(activity, last_hash);
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

        return (ret);
    }

    public String setTeamsRemote(AppCompatActivity activity) {
        String ret = "failed";
        try {
            JSONObject jo = new JSONObject();
            jo.put("cmd", "put-teams");
            jo.put("key_column", CyberScouterContract.Teams.COLUMN_NAME_TEAM);
            jo.put("key", team);
            jo.put("table_name", CyberScouterContract.Teams.TABLE_NAME);
            JSONObject payload = new JSONObject();
            payload.put(CyberScouterContract.Teams.COLUMN_NAME_NUM_WHEELS, NumWheels);
            payload.put(CyberScouterContract.Teams.COLUMN_NAME_DRIVE_MOTORS, NumDriveMotors);
            payload.put(CyberScouterContract.Teams.COLUMN_NAME_WHEEL_TYPE_ID, WheelTypeID);
            payload.put(CyberScouterContract.Teams.COLUMN_NAME_DRIVE_TYPE_ID, DriveTypeID);
            payload.put(CyberScouterContract.Teams.COLUMN_NAME_MOTOR_TYPE_ID, MotorTypeID);
            payload.put(CyberScouterContract.Teams.COLUMN_NAME_LANGUAGE_ID, LanguageID);
            payload.put(CyberScouterContract.Teams.COLUMN_NAME_SPEED, Speed);
            payload.put(CyberScouterContract.Teams.COLUMN_NAME_GEAR_RATIO, GearRatio);
            payload.put(CyberScouterContract.Teams.COLUMN_NAME_NUM_GEAR_SPEED, NumGearSpeed);
            payload.put(CyberScouterContract.Teams.COLUMN_NAME_ROBOT_LENGTH, RobotLength);
            payload.put(CyberScouterContract.Teams.COLUMN_NAME_ROBOT_WIDTH, RobotWidth);
            payload.put(CyberScouterContract.Teams.COLUMN_NAME_ROBOT_HEIGHT, RobotHeight);
            payload.put(CyberScouterContract.Teams.COLUMN_NAME_ROBOT_WEIGHT, RobotWeight);
            payload.put(CyberScouterContract.Teams.COLUMN_NAME_PNEUMATICS, Pneumatics);
            payload.put(CyberScouterContract.Teams.COLUMN_NAME_INTAKE_TYPE, IntakeType);
            payload.put(CyberScouterContract.Teams.COLUMN_NAME_PRE_LOAD, Preload);
            payload.put(CyberScouterContract.Teams.COLUMN_NAME_HAS_AUTO, HasAuto);
            payload.put(CyberScouterContract.Teams.COLUMN_NAME_AUTO_SCORED_HIGH, AutoScoredHigh);
            payload.put(CyberScouterContract.Teams.COLUMN_NAME_AUTO_SCORED_LOW, AutoScoredLow);
            payload.put(CyberScouterContract.Teams.COLUMN_NAME_MOVE_BONUS, MoveBonus);
            payload.put(CyberScouterContract.Teams.COLUMN_NAME_AUTO_PICKUP, AutoPickUp);
            payload.put(CyberScouterContract.Teams.COLUMN_NAME_AUTO_START_POS_ID, AutoStartPosID);
            payload.put(CyberScouterContract.Teams.COLUMN_NAME_AUTO_SUMMARY, AutoSummary);
            payload.put(CyberScouterContract.Teams.COLUMN_NAME_AUTO_HUMAN, AutoHuman);
            payload.put(CyberScouterContract.Teams.COLUMN_NAME_TELE_BALLS_SCORED_HIGH, TeleBallsScoredHigh);
            payload.put(CyberScouterContract.Teams.COLUMN_NAME_TELE_BALLS_SCORED_LOW, TeleBallsScoredLow);
            payload.put(CyberScouterContract.Teams.COLUMN_NAME_MAX_BALL_CAPACITY, MaxBallCapacity);
            payload.put(CyberScouterContract.Teams.COLUMN_NAME_TELE_DEFENSE, TeleDefense);
            payload.put(CyberScouterContract.Teams.COLUMN_NAME_TELE_DEFENSE_EVADE, TeleDefenseEvade);
            payload.put(CyberScouterContract.Teams.COLUMN_NAME_TELE_STRATEGY, TeleStrategy);
            payload.put(CyberScouterContract.Teams.COLUMN_NAME_TELE_DEFENSE_STRAT, TeleDefenseStrat);
            payload.put(CyberScouterContract.Teams.COLUMN_NAME_TELE_SORT_CARGO, TeleSortCargo);
            payload.put(CyberScouterContract.Teams.COLUMN_NAME_TELE_SHOOT_WHILE_DRIVE, TeleShootWhileDrive);
            payload.put(CyberScouterContract.Teams.COLUMN_NAME_CAN_CLIMB, CanClimb);
            payload.put(CyberScouterContract.Teams.COLUMN_NAME_CLIMB_POSITION, ClimbPosition);
            payload.put(CyberScouterContract.Teams.COLUMN_NAME_CLIMB_STRATEGY, ClimbStrategy);
            payload.put(CyberScouterContract.Teams.COLUMN_NAME_CLIMB_TIME, ClimbTime);
            payload.put(CyberScouterContract.Teams.COLUMN_NAME_CLIMB_HEIGHT_ID, ClimbHeightID);
//            payload.put(CyberScouterContract.Teams.COLUMN_NAME_COMPUTERID, computerID);
//            payload.put(CyberScouterContract.Teams.COLUMN_NAME_SCOUTERID, scouterID);

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

        return (ret);
    }


    static public void getTeamsWebService(final AppCompatActivity activity) {
        if (webQueryInProgress)
            return;

        webQueryInProgress = true;

        String url = String.format("%s/teams", FakeBluetoothServer.webServiceBaseUrl);

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(url, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                System.out.println("Starting get call...");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                webQueryInProgress = false;
                Intent i = new Intent(TEAMS_UPDATED_FILTER);
                webResponse = new String(response);
                i.putExtra("cyberscouterteams", "fetch");
                activity.sendBroadcast(i);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                webQueryInProgress = false;
                MessageBox.showMessageBox(activity,
                        "Fetch of Teams Record Failed",
                        "CyberScouterTeams.getTeamsWebService",
                        String.format(
                                "Failed to fetch remote Teams records!\nContact a scouting mentor right away\n\n%s\n",
                                e.getMessage()));
            }

            @Override
            public void onRetry(int retryNo) {
                System.out.println(String.format("Retry number %d", retryNo));
            }
        });
    }

    static public void setTeamsWebService(final AppCompatActivity activity, JSONObject jo) {
        if (webQueryInProgress)
            return;

        webQueryInProgress = true;

        Integer team = -99;
        try {
            team = jo.getInt("key");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Integer finalTeam = team;
        String url = String.format("%s/update", FakeBluetoothServer.webServiceBaseUrl);
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
                        Intent i = new Intent(TEAMS_UPDATED_FILTER);
                        webResponse = new String(response);
                        i.putExtra("cyberscouterteams", "update");
                        i.putExtra("team", finalTeam);
                        activity.sendBroadcast(i);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                        webQueryInProgress = false;
                        MessageBox.showMessageBox(activity,
                                "Update of Teams Records Failed",
                                "CyberScouterTeams.setTeamsWebService",
                                String.format(
                                        "Can't update team information.\nContact a scouting mentor right away\n\n%s\n",
                                        e.getMessage()));
                    }

                    @Override
                    public void onRetry(int retryNo) {
                        System.out.println(String.format("Retry number %d", retryNo));
                    }
                });
    }

    static String[] getTeamNumbers(SQLiteDatabase db, int wasScouted) {
        Vector<String> teamVector = new Vector<>();
        Cursor cursor;
        try {
            String[] projection = {
                    CyberScouterContract.Teams.COLUMN_NAME_TEAM,
            };

            String selection = CyberScouterContract.Teams.COLUMN_NAME_DONE_SCOUTING + " = ?";
            String[] selectionArgs = {
                    String.format(Locale.getDefault(), "%d", wasScouted)
            };


            cursor = db.query(
                    CyberScouterContract.Teams.TABLE_NAME,   // The table to query
                    projection,             // The array of columns to return (pass null to get all)
                    selection,              // The columns for the WHERE clause
                    selectionArgs,          // The values for the WHERE clause
                    null,                   // don't group the rows
                    null,                   // don't filter by row groups
                    CyberScouterContract.Teams.COLUMN_NAME_TEAM + " ASC"               // The sort order
            );

            if (0 < cursor.getCount()) {
                while (cursor.moveToNext()) {
                    String team_no = String.valueOf(cursor.getInt(cursor.getColumnIndex(CyberScouterContract.Teams.COLUMN_NAME_TEAM)));
                    teamVector.add(team_no);
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        if (0 < teamVector.size()) {
            String[] nv2 = new String[teamVector.size()];
            return teamVector.toArray(nv2);
        } else {
            return (null);
        }
    }

    static CyberScouterTeams getCurrentTeam(SQLiteDatabase db, int team) {
        String selection = CyberScouterContract.Teams.COLUMN_NAME_TEAM + "=?";
        String[] selectionArgs = {
                String.format(Locale.getDefault(), "%d", team)
        };

        CyberScouterTeams[] csta = getLocalTeams(db, selection, selectionArgs, null);
        if (null != csta && 0 < csta.length) {
            return (csta[0]);
        } else {
            return (null);
        }
    }

    static void setTeams(SQLiteDatabase db, String json) {

        try {

            JSONArray ja = new JSONArray(json);
            for (int i = 0; i < ja.length(); ++i) {
                JSONObject jo = ja.getJSONObject(i);
                ContentValues values = new ContentValues();

                values.put(CyberScouterContract.Teams.COLUMN_NAME_TEAM, jo.getString(CyberScouterContract.Teams.COLUMN_NAME_TEAM));
                values.put(CyberScouterContract.Teams.COLUMN_NAME_TEAM_NAME, jo.getString(CyberScouterContract.Teams.COLUMN_NAME_TEAM_NAME));
                values.put(CyberScouterContract.Teams.COLUMN_NAME_TEAM_LOCATION, jo.getString(CyberScouterContract.Teams.COLUMN_NAME_TEAM_LOCATION));
                values.put(CyberScouterContract.Teams.COLUMN_NAME_TEAM_CITY, jo.getString(CyberScouterContract.Teams.COLUMN_NAME_TEAM_CITY));
                values.put(CyberScouterContract.Teams.COLUMN_NAME_TEAM_STATE_PROV, jo.getString(CyberScouterContract.Teams.COLUMN_NAME_TEAM_STATE_PROV));
                values.put(CyberScouterContract.Teams.COLUMN_NAME_TEAM_COUNTRY, jo.getString(CyberScouterContract.Teams.COLUMN_NAME_TEAM_COUNTRY));
                values.put(CyberScouterContract.Teams.COLUMN_NAME_NUM_WHEELS, jo.getString(CyberScouterContract.Teams.COLUMN_NAME_NUM_WHEELS));
                values.put(CyberScouterContract.Teams.COLUMN_NAME_DRIVE_MOTORS, jo.getString(CyberScouterContract.Teams.COLUMN_NAME_DRIVE_MOTORS));
                values.put(CyberScouterContract.Teams.COLUMN_NAME_WHEEL_TYPE_ID, jo.getString(CyberScouterContract.Teams.COLUMN_NAME_WHEEL_TYPE_ID));
                values.put(CyberScouterContract.Teams.COLUMN_NAME_DRIVE_TYPE_ID, jo.getString(CyberScouterContract.Teams.COLUMN_NAME_DRIVE_TYPE_ID));
                values.put(CyberScouterContract.Teams.COLUMN_NAME_MOTOR_TYPE_ID, jo.getString(CyberScouterContract.Teams.COLUMN_NAME_MOTOR_TYPE_ID));
                values.put(CyberScouterContract.Teams.COLUMN_NAME_LANGUAGE_ID, jo.getString(CyberScouterContract.Teams.COLUMN_NAME_LANGUAGE_ID));
                values.put(CyberScouterContract.Teams.COLUMN_NAME_SPEED, jo.getString(CyberScouterContract.Teams.COLUMN_NAME_SPEED));
                values.put(CyberScouterContract.Teams.COLUMN_NAME_GEAR_RATIO, jo.getString(CyberScouterContract.Teams.COLUMN_NAME_GEAR_RATIO));
                values.put(CyberScouterContract.Teams.COLUMN_NAME_NUM_GEAR_SPEED, jo.getString(CyberScouterContract.Teams.COLUMN_NAME_NUM_GEAR_SPEED));
                values.put(CyberScouterContract.Teams.COLUMN_NAME_ROBOT_LENGTH, jo.getString(CyberScouterContract.Teams.COLUMN_NAME_ROBOT_LENGTH));
                values.put(CyberScouterContract.Teams.COLUMN_NAME_ROBOT_WIDTH, jo.getString(CyberScouterContract.Teams.COLUMN_NAME_ROBOT_WIDTH));
                values.put(CyberScouterContract.Teams.COLUMN_NAME_ROBOT_HEIGHT, jo.getString(CyberScouterContract.Teams.COLUMN_NAME_ROBOT_HEIGHT));
                values.put(CyberScouterContract.Teams.COLUMN_NAME_ROBOT_WEIGHT, jo.getString(CyberScouterContract.Teams.COLUMN_NAME_ROBOT_WEIGHT));
                values.put(CyberScouterContract.Teams.COLUMN_NAME_PNEUMATICS, jo.getString(CyberScouterContract.Teams.COLUMN_NAME_PNEUMATICS));
                values.put(CyberScouterContract.Teams.COLUMN_NAME_INTAKE_TYPE, jo.getString(CyberScouterContract.Teams.COLUMN_NAME_INTAKE_TYPE));
                values.put(CyberScouterContract.Teams.COLUMN_NAME_PRE_LOAD, jo.getString(CyberScouterContract.Teams.COLUMN_NAME_PRE_LOAD));
                values.put(CyberScouterContract.Teams.COLUMN_NAME_HAS_AUTO, jo.getString(CyberScouterContract.Teams.COLUMN_NAME_HAS_AUTO));
                values.put(CyberScouterContract.Teams.COLUMN_NAME_AUTO_SCORED_HIGH, jo.getString(CyberScouterContract.Teams.COLUMN_NAME_AUTO_SCORED_HIGH));
                values.put(CyberScouterContract.Teams.COLUMN_NAME_AUTO_SCORED_LOW, jo.getString(CyberScouterContract.Teams.COLUMN_NAME_AUTO_SCORED_LOW));
                values.put(CyberScouterContract.Teams.COLUMN_NAME_MOVE_BONUS, jo.getString(CyberScouterContract.Teams.COLUMN_NAME_MOVE_BONUS));
                values.put(CyberScouterContract.Teams.COLUMN_NAME_AUTO_PICKUP, jo.getString(CyberScouterContract.Teams.COLUMN_NAME_AUTO_PICKUP));
                values.put(CyberScouterContract.Teams.COLUMN_NAME_AUTO_START_POS_ID, jo.getString(CyberScouterContract.Teams.COLUMN_NAME_AUTO_START_POS_ID));
                values.put(CyberScouterContract.Teams.COLUMN_NAME_AUTO_SUMMARY, jo.getString(CyberScouterContract.Teams.COLUMN_NAME_AUTO_SUMMARY));
                values.put(CyberScouterContract.Teams.COLUMN_NAME_AUTO_HUMAN, jo.getString(CyberScouterContract.Teams.COLUMN_NAME_AUTO_HUMAN));
                values.put(CyberScouterContract.Teams.COLUMN_NAME_TELE_BALLS_SCORED_HIGH, jo.getString(CyberScouterContract.Teams.COLUMN_NAME_TELE_BALLS_SCORED_HIGH));
                values.put(CyberScouterContract.Teams.COLUMN_NAME_TELE_BALLS_SCORED_LOW, jo.getString(CyberScouterContract.Teams.COLUMN_NAME_TELE_BALLS_SCORED_LOW));
                values.put(CyberScouterContract.Teams.COLUMN_NAME_MAX_BALL_CAPACITY, jo.getString(CyberScouterContract.Teams.COLUMN_NAME_MAX_BALL_CAPACITY));
                values.put(CyberScouterContract.Teams.COLUMN_NAME_TELE_DEFENSE, jo.getString(CyberScouterContract.Teams.COLUMN_NAME_TELE_DEFENSE));
                values.put(CyberScouterContract.Teams.COLUMN_NAME_TELE_DEFENSE_EVADE, jo.getString(CyberScouterContract.Teams.COLUMN_NAME_TELE_DEFENSE_EVADE));
                values.put(CyberScouterContract.Teams.COLUMN_NAME_TELE_STRATEGY, jo.getString(CyberScouterContract.Teams.COLUMN_NAME_TELE_STRATEGY));
                values.put(CyberScouterContract.Teams.COLUMN_NAME_TELE_DEFENSE_STRAT, jo.getString(CyberScouterContract.Teams.COLUMN_NAME_TELE_DEFENSE_STRAT));
                values.put(CyberScouterContract.Teams.COLUMN_NAME_TELE_SORT_CARGO, jo.getString(CyberScouterContract.Teams.COLUMN_NAME_TELE_SORT_CARGO));
                values.put(CyberScouterContract.Teams.COLUMN_NAME_TELE_SHOOT_WHILE_DRIVE, jo.getString(CyberScouterContract.Teams.COLUMN_NAME_TELE_SHOOT_WHILE_DRIVE));
                values.put(CyberScouterContract.Teams.COLUMN_NAME_CAN_CLIMB, jo.getString(CyberScouterContract.Teams.COLUMN_NAME_CAN_CLIMB));
                values.put(CyberScouterContract.Teams.COLUMN_NAME_CLIMB_POSITION, jo.getString(CyberScouterContract.Teams.COLUMN_NAME_CLIMB_POSITION));
                values.put(CyberScouterContract.Teams.COLUMN_NAME_CLIMB_STRATEGY, jo.getString(CyberScouterContract.Teams.COLUMN_NAME_CLIMB_STRATEGY));
                values.put(CyberScouterContract.Teams.COLUMN_NAME_CLIMB_TIME, jo.getString(CyberScouterContract.Teams.COLUMN_NAME_CLIMB_TIME));
                values.put(CyberScouterContract.Teams.COLUMN_NAME_CLIMB_HEIGHT_ID, jo.getString(CyberScouterContract.Teams.COLUMN_NAME_CLIMB_HEIGHT_ID));
                values.put(CyberScouterContract.Teams.COLUMN_NAME_DONE_SCOUTING, 0);
                values.put(CyberScouterContract.Teams.COLUMN_NAME_UPLOAD_STATUS, com.frcteam195.cyberscouter.UploadStatus.NOT_UPLOADED);

                long newRowId = db.insertWithOnConflict(CyberScouterContract.Teams.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void deleteTeams(SQLiteDatabase db) {
        db.execSQL("DELETE from " + CyberScouterContract.Teams.TABLE_NAME);
    }

    static void updateTeamMetric(SQLiteDatabase db, String lcolumn, Integer lvalue, Integer currentTeam) throws Exception {
        ContentValues values = new ContentValues();
        values.put(lcolumn, lvalue);
        updateTeamMetric(db, values, currentTeam);
    }

    static void updateTeamMetric(SQLiteDatabase db, String lcolumn, String lvalue, Integer currentTeam) throws Exception {
        ContentValues values = new ContentValues();
        values.put(lcolumn, lvalue);
        updateTeamMetric(db, values, currentTeam);
    }

    static private void updateTeamMetric(SQLiteDatabase db, ContentValues cv, Integer currentTeam) throws Exception {
        String selection = CyberScouterContract.Teams.COLUMN_NAME_TEAM + " = ?";
        String[] selectionArgs = {
                String.format(Locale.getDefault(), "%d", currentTeam)
        };

        if (1 > updateTeam(db, cv, selection, selectionArgs))
            throw new Exception(String.format("An error occurred while updating the local teams table.\n\nNo rows were updated for Team=%d", currentTeam));
    }

    private static int updateTeam(SQLiteDatabase db, ContentValues values, String selection, String[] selectionArgs) {
        return db.update(
                CyberScouterContract.Teams.TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }

    static CyberScouterTeams[] getTeamsReadyToUpload(SQLiteDatabase db) {

        String selection =
                CyberScouterContract.Teams.COLUMN_NAME_DONE_SCOUTING + " = ? AND " +
                        CyberScouterContract.Teams.COLUMN_NAME_UPLOAD_STATUS + " = ?";

        String[] selectionArgs = {
                String.format(Locale.getDefault(), "%d", 1),
                String.format(Locale.getDefault(), "%d",
                        com.frcteam195.cyberscouter.UploadStatus.NOT_UPLOADED)
        };

        String sortOrder =
                CyberScouterContract.Teams.COLUMN_NAME_TEAM + " ASC";

        return (getLocalTeams(db, selection, selectionArgs, sortOrder));
    }

    static CyberScouterTeams[] getLocalTeams(SQLiteDatabase db, String selection,
                                             String[] selectionArgs, String sortOrder) {
        Vector<CyberScouterTeams> cstv = new Vector<>();
        CyberScouterTeams cst = null;

        Cursor cursor = null;
        try {
            String[] projection = {
                    CyberScouterContract.Teams.COLUMN_NAME_TEAM,
                    CyberScouterContract.Teams.COLUMN_NAME_TEAM_NAME,
                    CyberScouterContract.Teams.COLUMN_NAME_NUM_WHEELS,
                    CyberScouterContract.Teams.COLUMN_NAME_DRIVE_MOTORS,
                    CyberScouterContract.Teams.COLUMN_NAME_WHEEL_TYPE_ID,
                    CyberScouterContract.Teams.COLUMN_NAME_DRIVE_TYPE_ID,
                    CyberScouterContract.Teams.COLUMN_NAME_MOTOR_TYPE_ID,
                    CyberScouterContract.Teams.COLUMN_NAME_LANGUAGE_ID,
                    CyberScouterContract.Teams.COLUMN_NAME_SPEED,
                    CyberScouterContract.Teams.COLUMN_NAME_GEAR_RATIO,
                    CyberScouterContract.Teams.COLUMN_NAME_NUM_GEAR_SPEED,
                    CyberScouterContract.Teams.COLUMN_NAME_ROBOT_LENGTH,
                    CyberScouterContract.Teams.COLUMN_NAME_ROBOT_WIDTH,
                    CyberScouterContract.Teams.COLUMN_NAME_ROBOT_HEIGHT,
                    CyberScouterContract.Teams.COLUMN_NAME_ROBOT_WEIGHT,
                    CyberScouterContract.Teams.COLUMN_NAME_PNEUMATICS,
                    CyberScouterContract.Teams.COLUMN_NAME_INTAKE_TYPE,
                    CyberScouterContract.Teams.COLUMN_NAME_PRE_LOAD,
                    CyberScouterContract.Teams.COLUMN_NAME_HAS_AUTO,
                    CyberScouterContract.Teams.COLUMN_NAME_AUTO_SCORED_HIGH,
                    CyberScouterContract.Teams.COLUMN_NAME_AUTO_SCORED_LOW,
                    CyberScouterContract.Teams.COLUMN_NAME_MOVE_BONUS,
                    CyberScouterContract.Teams.COLUMN_NAME_AUTO_PICKUP,
                    CyberScouterContract.Teams.COLUMN_NAME_AUTO_START_POS_ID,
                    CyberScouterContract.Teams.COLUMN_NAME_AUTO_SUMMARY,
                    CyberScouterContract.Teams.COLUMN_NAME_AUTO_HUMAN,
                    CyberScouterContract.Teams.COLUMN_NAME_TELE_BALLS_SCORED_HIGH,
                    CyberScouterContract.Teams.COLUMN_NAME_TELE_BALLS_SCORED_LOW,
                    CyberScouterContract.Teams.COLUMN_NAME_MAX_BALL_CAPACITY,
                    CyberScouterContract.Teams.COLUMN_NAME_TELE_DEFENSE,
                    CyberScouterContract.Teams.COLUMN_NAME_TELE_DEFENSE_EVADE,
                    CyberScouterContract.Teams.COLUMN_NAME_TELE_STRATEGY,
                    CyberScouterContract.Teams.COLUMN_NAME_TELE_DEFENSE_STRAT,
                    CyberScouterContract.Teams.COLUMN_NAME_TELE_SORT_CARGO,
                    CyberScouterContract.Teams.COLUMN_NAME_TELE_SHOOT_WHILE_DRIVE,
                    CyberScouterContract.Teams.COLUMN_NAME_CAN_CLIMB,
                    CyberScouterContract.Teams.COLUMN_NAME_CLIMB_POSITION,
                    CyberScouterContract.Teams.COLUMN_NAME_CLIMB_STRATEGY,
                    CyberScouterContract.Teams.COLUMN_NAME_CLIMB_TIME,
                    CyberScouterContract.Teams.COLUMN_NAME_CLIMB_HEIGHT_ID,
                    CyberScouterContract.Teams.COLUMN_NAME_DONE_SCOUTING,
                    CyberScouterContract.Teams.COLUMN_NAME_UPLOAD_STATUS
            };

            cursor = db.query(
                    CyberScouterContract.Teams.TABLE_NAME,   // The table to query
                    projection,             // The array of columns to return (pass null to get all)
                    selection,              // The columns for the WHERE clause
                    selectionArgs,          // The values for the WHERE clause
                    null,                   // don't group the rows
                    null,                   // don't filter by row groups
                    sortOrder               // The sort order
            );

            if (0 < cursor.getCount()) {
                while (cursor.moveToNext()) {
                    cst = new CyberScouterTeams();
                    cst.team = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.Teams.COLUMN_NAME_TEAM));
                    cst.TeamName = cursor.getString(cursor.getColumnIndex(CyberScouterContract.Teams.COLUMN_NAME_TEAM_NAME));
                    cst.NumWheels = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.Teams.COLUMN_NAME_NUM_WHEELS));
                    cst.NumDriveMotors = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.Teams.COLUMN_NAME_DRIVE_MOTORS));
                    cst.WheelTypeID = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.Teams.COLUMN_NAME_WHEEL_TYPE_ID));
                    cst.DriveTypeID = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.Teams.COLUMN_NAME_DRIVE_TYPE_ID));
                    cst.MotorTypeID = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.Teams.COLUMN_NAME_MOTOR_TYPE_ID));
                    cst.LanguageID = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.Teams.COLUMN_NAME_LANGUAGE_ID));
                    cst.Speed = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.Teams.COLUMN_NAME_SPEED));
                    cst.GearRatio = cursor.getString(cursor.getColumnIndex(CyberScouterContract.Teams.COLUMN_NAME_GEAR_RATIO));
                    cst.NumGearSpeed = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.Teams.COLUMN_NAME_NUM_GEAR_SPEED));
                    cst.RobotLength = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.Teams.COLUMN_NAME_ROBOT_LENGTH));
                    cst.RobotWidth = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.Teams.COLUMN_NAME_ROBOT_WIDTH));
                    cst.RobotHeight = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.Teams.COLUMN_NAME_ROBOT_HEIGHT));
                    cst.RobotWeight = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.Teams.COLUMN_NAME_ROBOT_WEIGHT));
                    cst.Pneumatics = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.Teams.COLUMN_NAME_PNEUMATICS));
                    cst.IntakeType = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.Teams.COLUMN_NAME_INTAKE_TYPE));
                    cst.Preload = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.Teams.COLUMN_NAME_PRE_LOAD));
                    cst.HasAuto = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.Teams.COLUMN_NAME_HAS_AUTO));
                    cst.AutoScoredHigh = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.Teams.COLUMN_NAME_AUTO_SCORED_HIGH));
                    cst.AutoScoredLow = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.Teams.COLUMN_NAME_AUTO_SCORED_LOW));
                    cst.MoveBonus = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.Teams.COLUMN_NAME_MOVE_BONUS));
                    cst.AutoPickUp = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.Teams.COLUMN_NAME_AUTO_PICKUP));
                    cst.AutoStartPosID = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.Teams.COLUMN_NAME_AUTO_START_POS_ID));
                    cst.AutoSummary = cursor.getString(cursor.getColumnIndex(CyberScouterContract.Teams.COLUMN_NAME_AUTO_SUMMARY));
                    cst.AutoHuman = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.Teams.COLUMN_NAME_AUTO_HUMAN));
                    cst.TeleBallsScoredHigh = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.Teams.COLUMN_NAME_TELE_BALLS_SCORED_HIGH));
                    cst.TeleBallsScoredLow = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.Teams.COLUMN_NAME_TELE_BALLS_SCORED_LOW));
                    cst.MaxBallCapacity = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.Teams.COLUMN_NAME_MAX_BALL_CAPACITY));
                    cst.TeleDefense = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.Teams.COLUMN_NAME_TELE_DEFENSE));
                    cst.TeleDefenseEvade = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.Teams.COLUMN_NAME_TELE_DEFENSE_EVADE));
                    cst.TeleStrategy = cursor.getString(cursor.getColumnIndex(CyberScouterContract.Teams.COLUMN_NAME_TELE_STRATEGY));
                    cst.TeleDefenseStrat = cursor.getString(cursor.getColumnIndex(CyberScouterContract.Teams.COLUMN_NAME_TELE_DEFENSE_STRAT));
                    cst.TeleSortCargo = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.Teams.COLUMN_NAME_TELE_SORT_CARGO));
                    cst.TeleShootWhileDrive = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.Teams.COLUMN_NAME_TELE_SHOOT_WHILE_DRIVE));
                    cst.CanClimb = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.Teams.COLUMN_NAME_CAN_CLIMB));
                    cst.ClimbPosition = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.Teams.COLUMN_NAME_CLIMB_POSITION));
                    cst.ClimbStrategy = cursor.getString(cursor.getColumnIndex(CyberScouterContract.Teams.COLUMN_NAME_CLIMB_STRATEGY));
                    cst.ClimbTime = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.Teams.COLUMN_NAME_CLIMB_TIME));
                    cst.ClimbHeightID = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.Teams.COLUMN_NAME_CLIMB_HEIGHT_ID));
                    cst.DoneScouting = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.Teams.COLUMN_NAME_DONE_SCOUTING));
                    cst.UploadStatus = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.Teams.COLUMN_NAME_UPLOAD_STATUS));

                    cstv.add(cst);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        CyberScouterTeams[] csta = new CyberScouterTeams[cstv.size()];
        return cstv.toArray(csta);
    }

    int getTeam() {
        return team;
    }

    String getTeamName() {
        return TeamName;
    }

    String getTeamLocation() {
        return TeamLocation;
    }

    String getTeamCity() {
        return TeamCity;
    }

    String getTeamStateProv() {
        return TeamStateProv;
    }

    String getTeamCountry() {
        return TeamCountry;
    }

    int getNumWheels() {
        return NumWheels;
    }

    int getNumDriveMotors() {
        return NumDriveMotors;
    }

    int getWheelTypeID() {
        return WheelTypeID;
    }

    int getDriveTypeID() {
        return DriveTypeID;
    }

    int getMotorTypeID() {
        return MotorTypeID;
    }

    int getLanguageID() {
        return LanguageID;
    }

    int getSpeed() {
        return Speed;
    }

    String getGearRatio() {
        return GearRatio;
    }

    int getNumGearSpeed() {
        return NumGearSpeed;
    }

    int getRobotLength() {
        return RobotLength;
    }

    int getRobotWidth() {
        return RobotWidth;
    }

    int getRobotHeight() {
        return RobotHeight;
    }

    int getRobotWeight() {
        return RobotWeight;
    }

    int getPneumatics() {
        return Pneumatics;
    }

    int getIntakeType() {
        return IntakeType;
    }

    int getPreload() {
        return Preload;
    }

    int getHasAuto() {
        return HasAuto;
    }

    int getAutoScoredHigh() {
        return AutoScoredHigh;
    }

    int getAutoScoredLow() {
        return AutoScoredLow;
    }

    int getMoveBonus() {
        return MoveBonus;
    }

    int getAutoPickUp() {
        return AutoPickUp;
    }

    int getAutoStartPosID() {
        return AutoStartPosID;
    }

    String getAutoSummary() {
        return AutoSummary;
    }

    int getAutoHuman() {
        return AutoHuman;
    }

    int getTeleBallsScoredHigh() {
        return TeleBallsScoredHigh;
    }

    int getTeleBallsScoredLow() {
        return TeleBallsScoredLow;
    }

    int getMaxBallCapacity() {
        return MaxBallCapacity;
    }

    int getTeleDefense() {
        return TeleDefense;
    }

    int getTeleDefenseEvade() {
        return TeleDefenseEvade;
    }

    String getTeleStrategy() {
        return TeleStrategy;
    }

    String getTeleDefenseStrat() {
        return TeleDefenseStrat;
    }

    int getTeleSortCargo() {
        return TeleSortCargo;
    }

    int getTeleShootWhileDrive() {
        return TeleShootWhileDrive;
    }

    int getCanClimb() {
        return CanClimb;
    }

    int getClimbPosition() {
        return ClimbPosition;
    }

    String getClimbStrategy() {
        return ClimbStrategy;
    }

    int getClimbTime() {
        return ClimbTime;
    }

    int getClimbHeightID() {
        return ClimbHeightID;
    }

    int getDoneScouting() {
        return DoneScouting;
    }
}
