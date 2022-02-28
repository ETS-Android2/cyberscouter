package com.frcteam195.cyberscouter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CyberScouterDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 53;
    private static final String DATABASE_NAME = "CyberScouter.db";

    private static final String SQL_CREATE_CONFIG_ENTRIES =
            "CREATE TABLE " + CyberScouterContract.ConfigEntry.TABLE_NAME + " (" +
                    CyberScouterContract.ConfigEntry._ID + " INTEGER PRIMARY KEY," +
                    CyberScouterContract.ConfigEntry.COLUMN_NAME_ALLIANCE_STATIOM + " TEXT," +
                    CyberScouterContract.ConfigEntry.COLUMN_NAME_ALLIANCE_STATION_ID + " INTEGER," +
                    CyberScouterContract.ConfigEntry.COLUMN_NAME_EVENT + " TEXT," +
                    CyberScouterContract.ConfigEntry.COLUMN_NAME_EVENT_LOCATION + " TEXT," +
                    CyberScouterContract.ConfigEntry.COLUMN_NAME_EVENT_ID + " INTEGER," +
                    CyberScouterContract.ConfigEntry.COLUMN_NAME_TABLET_NUM + " INTEGER," +
                    CyberScouterContract.ConfigEntry.COLUMN_NAME_OFFLINE + " INTEGER," +
                    CyberScouterContract.ConfigEntry.COLUMN_NAME_FIELD_REDLEFT + " INTEGER," +
                    CyberScouterContract.ConfigEntry.COLUMN_NAME_USERNAME + " TEXT," +
                    CyberScouterContract.ConfigEntry.COLUMN_NAME_USERID + " INTEGER," +
                    CyberScouterContract.ConfigEntry.COLUMN_NAME_COMPUTER_TYPE_ID + " INTEGER, " +
                    "UNIQUE(" +
                    CyberScouterContract.ConfigEntry.COLUMN_NAME_EVENT_ID + "," +
                    CyberScouterContract.ConfigEntry.COLUMN_NAME_ALLIANCE_STATION_ID + "," +
                    CyberScouterContract.ConfigEntry.COLUMN_NAME_COMPUTER_TYPE_ID +
                    "))";

    private static final String SQL_DELETE_CONFIG_ENTRIES =
            "DROP TABLE IF EXISTS " + CyberScouterContract.ConfigEntry.TABLE_NAME;

    private static final String SQL_CREATE_MATCHES =
            "CREATE TABLE " + CyberScouterContract.MatchScouting.TABLE_NAME + " (" +
                    CyberScouterContract.MatchScouting.COLUMN_NAME_MATCHSCOUTINGID + " INTEGER PRIMARY KEY," +
                    CyberScouterContract.MatchScouting.COLUMN_NAME_EVENTID + " INTEGER," +
                    CyberScouterContract.MatchScouting.COLUMN_NAME_MATCHID + " INTEGER," +
                    CyberScouterContract.MatchScouting.COLUMN_NAME_MATCH_NUMBER + " INTEGER," +
                    CyberScouterContract.MatchScouting.COLUMN_NAME_COMPUTERID + " INTEGER," +
                    CyberScouterContract.MatchScouting.COLUMN_NAME_SCOUTERID + " INTEGER," +
                    CyberScouterContract.MatchScouting.COLUMN_NAME_REVIEWERID + " INTEGER," +
                    CyberScouterContract.MatchScouting.COLUMN_NAME_TEAM + " TEXT," +
                    CyberScouterContract.MatchScouting.COLUMN_NAME_TEAMMATCHNO + " INTEGER," +
                    CyberScouterContract.MatchScouting.COLUMN_NAME_ALLIANCESTATIONID + " INTEGER," +
                    CyberScouterContract.MatchScouting.COLUMN_NAME_MATCHENDED + " INTEGER," +
                    CyberScouterContract.MatchScouting.COLUMN_NAME_SCOUTINGSTATUS + " INTEGER," +
                    CyberScouterContract.MatchScouting.COLUMN_NAME_AREASTOREVIEW + " INTEGER," +
                    CyberScouterContract.MatchScouting.COLUMN_NAME_COMPLETE + " INTEGER," +
                    CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOSTARTPOS + " INTEGER," +
                    CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOPRELOAD + " INTEGER," +
                    CyberScouterContract.MatchScouting.COLUMN_NAME_AUTODIDNOTSHOW + " INTEGER," +
                    CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOMOVEBONUS + " INTEGER," +
                    CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLLOW + " INTEGER," +
                    CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLHIGH + " INTEGER," +
                    CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLMISS + " INTEGER," +
                    CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS1 + " INTEGER," +
                    CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS2 + " INTEGER," +
                    CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS3 + " INTEGER," +
                    CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS4 + " INTEGER," +
                    CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS5 + " INTEGER," +
                    CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS6 + " INTEGER," +
                    CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS7 + " INTEGER," +
                    CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS8 + " INTEGER," +
                    CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS9 + " INTEGER," +
                    CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS10 + " INTEGER," +
                    CyberScouterContract.MatchScouting.COLUMN_NAME_TELEBALLLOW + " INTEGER," +
                    CyberScouterContract.MatchScouting.COLUMN_NAME_TELEBALLHIGH + " INTEGER," +
                    CyberScouterContract.MatchScouting.COLUMN_NAME_TELEBALLMISS + " INTEGER," +
                    CyberScouterContract.MatchScouting.COLUMN_NAME_CLIMBSTATUS + " INTEGER," +
                    CyberScouterContract.MatchScouting.COLUMN_NAME_CLIMBHEIGHT + " INTEGER," +
//                    CyberScouterContract.MatchScouting.COLUMN_NAME_INSTEADOFCLIMB + " INTEGER," +
                    CyberScouterContract.MatchScouting.COLUMN_NAME_CLIMBPOSITION + " INTEGER," +
                    CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMLAUNCHPAD + " INTEGER," +
                    CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMSORTCARGO + " INTEGER," +
                    CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMSHOOTDRIVING + " INTEGER," +
                    CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMSUBSYSTEMBROKE + " INTEGER," +
                    CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMBROKEDOWN + " INTEGER," +
                    CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMLOSTCOMM + " INTEGER," +
                    CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMGROUNDPICKUP + " INTEGER," +
                    CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMTERMINALPICKUP + " INTEGER," +
                    CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMPLAYEDDEFENSE + " INTEGER," +
                    CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMDEFPLAYEDAGAINST + " INTEGER," +
                    CyberScouterContract.MatchScouting.COLUMN_NAME_UPLOADSTATUS + " INTEGER)"
            ;

    private static final String SQL_DELETE_MATCHES =
            "DROP TABLE IF EXISTS " + CyberScouterContract.MatchScouting.TABLE_NAME;

    private static final String SQL_CREATE_MATCHES_L2 =
            "CREATE TABLE " + CyberScouterContract.MatchScoutingL2.TABLE_NAME + " (" +
                    CyberScouterContract.MatchScoutingL2.COLUMN_NAME_MATCHSCOUTINGL2ID + " INTEGER PRIMARY KEY," +
                    CyberScouterContract.MatchScoutingL2.COLUMN_NAME_EVENTID + " INTEGER," +
                    CyberScouterContract.MatchScoutingL2.COLUMN_NAME_MATCHID + " INTEGER," +
                    CyberScouterContract.MatchScoutingL2.COLUMN_NAME_MATCH_NUMBER + " INTEGER," +
                    CyberScouterContract.MatchScoutingL2.COLUMN_NAME_COMPUTERID + " INTEGER," +
                    CyberScouterContract.MatchScoutingL2.COLUMN_NAME_SCOUTERID + " INTEGER," +
                    CyberScouterContract.MatchScoutingL2.COLUMN_NAME_REVIEWERID + " INTEGER," +
                    CyberScouterContract.MatchScoutingL2.COLUMN_NAME_TEAM_RED + " TEXT," +
                    CyberScouterContract.MatchScoutingL2.COLUMN_NAME_TEAM_BLUE + " TEXT," +
                    CyberScouterContract.MatchScoutingL2.COLUMN_NAME_MATCHSCOUTINGIDRED + " INTEGER," +
                    CyberScouterContract.MatchScoutingL2.COLUMN_NAME_MATCHSCOUTINGIDBLUE + " INTEGER," +
                    CyberScouterContract.MatchScoutingL2.COLUMN_NAME_COMMENT_RED + " TEXT," +
                    CyberScouterContract.MatchScoutingL2.COLUMN_NAME_COMMENT_BLUE + " TEXT," +
                    CyberScouterContract.MatchScoutingL2.COLUMN_NAME_ALLIANCESTATIONID + " INTEGER," +
                    CyberScouterContract.MatchScoutingL2.COLUMN_NAME_T1MULTIROBOTDEFENSE + " INTEGER," +
                    CyberScouterContract.MatchScoutingL2.COLUMN_NAME_T2MULTIROBOTDEFENSE + " INTEGER," +
                    CyberScouterContract.MatchScoutingL2.COLUMN_NAME_T3MULTIROBOTDEFENSE + " INTEGER," +
                    CyberScouterContract.MatchScoutingL2.COLUMN_NAME_T1PRIMARYDEFENSE + " INTEGER," +
                    CyberScouterContract.MatchScoutingL2.COLUMN_NAME_T2PRIMARYDEFENSE + " INTEGER," +
                    CyberScouterContract.MatchScoutingL2.COLUMN_NAME_T3PRIMARYDEFENSE + " INTEGER," +
                    CyberScouterContract.MatchScoutingL2.COLUMN_NAME_T1SECONDARYDEFENSE + " INTEGER," +
                    CyberScouterContract.MatchScoutingL2.COLUMN_NAME_T2SECONDARYDEFENSE + " INTEGER," +
                    CyberScouterContract.MatchScoutingL2.COLUMN_NAME_T3SECONDARYDEFENSE + " INTEGER," +
                    CyberScouterContract.MatchScoutingL2.COLUMN_NAME_T1HOWLONG + " INTEGER," +
                    CyberScouterContract.MatchScoutingL2.COLUMN_NAME_T2HOWLONG + " INTEGER," +
                    CyberScouterContract.MatchScoutingL2.COLUMN_NAME_T3HOWLONG + " INTEGER," +
                    CyberScouterContract.MatchScouting.COLUMN_NAME_MATCHENDED + " INTEGER," +
                    CyberScouterContract.MatchScouting.COLUMN_NAME_SCOUTINGSTATUS + " INTEGER," +
                    CyberScouterContract.MatchScouting.COLUMN_NAME_COMPLETE + " INTEGER," +
                    CyberScouterContract.MatchScouting.COLUMN_NAME_UPLOADSTATUS + " INTEGER)"
            ;

    private static final String SQL_DELETE_MATCHES_L2 =
            "DROP TABLE IF EXISTS " + CyberScouterContract.MatchScoutingL2.TABLE_NAME;

    private static final String SQL_CREATE_USER_ENTRIES =
            "CREATE TABLE " + CyberScouterContract.Users.TABLE_NAME + " (" +
                    CyberScouterContract.Users.COLUMN_NAME_USERID + " INTEGER PRIMARY KEY," +
                    CyberScouterContract.Users.COLUMN_NAME_FIRSTNAME + " TEXT," +
                    CyberScouterContract.Users.COLUMN_NAME_LASTNAME + " TEXT," +
                    CyberScouterContract.Users.COLUMN_NAME_CELLPHONE + " TEXT," +
                    CyberScouterContract.Users.COLUMN_NAME_EMAIL + " TEXT)"
            ;

    private static final String SQL_DELETE_USER_ENTRIES =
            "DROP TABLE IF EXISTS " + CyberScouterContract.Users.TABLE_NAME;

    private static final String SQL_CREATE_QUESTIONS =
            "CREATE TABLE " + CyberScouterContract.Questions.TABLE_NAME + " (" +
                CyberScouterContract.Questions.COLUMN_NAME_QUESTIONID + " INTEGER PRIMARY KEY," +
                    CyberScouterContract.Questions.COLUMN_NAME_EVENTID + " INTEGER," +
                    CyberScouterContract.Questions.COLUMN_NAME_QUESTIONNUMBER + " INTEGER," +
                    CyberScouterContract.Questions.COLUMN_NAME_QUESTIONTEXT + " TEXT," +
                    CyberScouterContract.Questions.COLUMN_NAME_ANSWERS + " TEXT)"
            ;

    private static final String SQL_DELETE_QUESTIONS =
            "DROP TABLE IF EXISTS " + CyberScouterContract.Questions.TABLE_NAME;

    private static final String SQL_CREATE_TEAMS =
            "CREATE TABLE " + CyberScouterContract.Teams.TABLE_NAME + " (" +
                    CyberScouterContract.Teams.COLUMN_NAME_TEAM + " INTEGER PRIMARY KEY," +
                    CyberScouterContract.Teams.COLUMN_NAME_TEAM_NAME + " TEXT," +
                    CyberScouterContract.Teams.COLUMN_NAME_TEAM_LOCATION + " TEXT," +
                    CyberScouterContract.Teams.COLUMN_NAME_TEAM_CITY + " TEXT," +
                    CyberScouterContract.Teams.COLUMN_NAME_TEAM_STATE_PROV + " TEXT," +
                    CyberScouterContract.Teams.COLUMN_NAME_TEAM_COUNTRY + " TEXT," +
                    CyberScouterContract.Teams.COLUMN_NAME_NUM_WHEELS + " INTEGER," +
                    CyberScouterContract.Teams.COLUMN_NAME_DRIVE_MOTORS + " INTEGER," +
                    CyberScouterContract.Teams.COLUMN_NAME_WHEEL_TYPE_ID + " INTEGER," +
                    CyberScouterContract.Teams.COLUMN_NAME_DRIVE_TYPE_ID + " INTEGER," +
                    CyberScouterContract.Teams.COLUMN_NAME_MOTOR_TYPE_ID + " INTEGER," +
                    CyberScouterContract.Teams.COLUMN_NAME_LANGUAGE_ID + " INTEGER," +
                    CyberScouterContract.Teams.COLUMN_NAME_SPEED + " INTEGER," +
                    CyberScouterContract.Teams.COLUMN_NAME_GEAR_RATIO + " TEXT," +
                    CyberScouterContract.Teams.COLUMN_NAME_NUM_GEAR_SPEED + " INTEGER," +
                    CyberScouterContract.Teams.COLUMN_NAME_ROBOT_LENGTH + " INTEGER," +
                    CyberScouterContract.Teams.COLUMN_NAME_ROBOT_WIDTH + " INTEGER," +
                    CyberScouterContract.Teams.COLUMN_NAME_ROBOT_HEIGHT + " INTEGER," +
                    CyberScouterContract.Teams.COLUMN_NAME_ROBOT_WEIGHT + " INTEGER," +
                    CyberScouterContract.Teams.COLUMN_NAME_PNEUMATICS + " INTEGER," +
                    CyberScouterContract.Teams.COLUMN_NAME_INTAKE_TYPE + " INTEGER," +
                    CyberScouterContract.Teams.COLUMN_NAME_PRE_LOAD + " INTEGER," +
                    CyberScouterContract.Teams.COLUMN_NAME_HAS_AUTO + " INTEGER," +
                    CyberScouterContract.Teams.COLUMN_NAME_AUTO_SCORED_HIGH + " INTEGER," +
                    CyberScouterContract.Teams.COLUMN_NAME_AUTO_SCORED_LOW + " INTEGER," +
                    CyberScouterContract.Teams.COLUMN_NAME_MOVE_BONUS + " INTEGER," +
                    CyberScouterContract.Teams.COLUMN_NAME_AUTO_PICKUP + " INTEGER," +
                    CyberScouterContract.Teams.COLUMN_NAME_AUTO_START_POS_ID + " INTEGER," +
                    CyberScouterContract.Teams.COLUMN_NAME_AUTO_SUMMARY + " TEXT," +
                    CyberScouterContract.Teams.COLUMN_NAME_AUTO_HUMAN + " INTEGER," +
                    CyberScouterContract.Teams.COLUMN_NAME_TELE_BALLS_SCORED_HIGH + " INTEGER," +
                    CyberScouterContract.Teams.COLUMN_NAME_TELE_BALLS_SCORED_LOW + " INTEGER," +
                    CyberScouterContract.Teams.COLUMN_NAME_MAX_BALL_CAPACITY + " INTEGER," +
                    CyberScouterContract.Teams.COLUMN_NAME_TELE_DEFENSE + " INTEGER," +
                    CyberScouterContract.Teams.COLUMN_NAME_TELE_DEFENSE_EVADE + " INTEGER," +
                    CyberScouterContract.Teams.COLUMN_NAME_TELE_STRATEGY + " TEXT," +
                    CyberScouterContract.Teams.COLUMN_NAME_TELE_DEFENSE_STRAT + " TEXT," +
                    CyberScouterContract.Teams.COLUMN_NAME_TELE_SORT_CARGO + " INTEGER," +
                    CyberScouterContract.Teams.COLUMN_NAME_TELE_SHOOT_WHILE_DRIVE + " INTEGER," +
                    CyberScouterContract.Teams.COLUMN_NAME_CAN_CLIMB + " INTEGER," +
                    CyberScouterContract.Teams.COLUMN_NAME_CLIMB_POSITION + " INTEGER," +
                    CyberScouterContract.Teams.COLUMN_NAME_CLIMB_STRATEGY + " TEXT," +
                    CyberScouterContract.Teams.COLUMN_NAME_CLIMB_TIME + " INTEGER," +
                    CyberScouterContract.Teams.COLUMN_NAME_CLIMB_HEIGHT_ID + " INTEGER," +
                    CyberScouterContract.Teams.COLUMN_NAME_DONE_SCOUTING + " INTEGER," +
                    CyberScouterContract.Teams.COLUMN_NAME_UPLOAD_STATUS + " INTEGER)"
            ;

    private static final String SQL_DELETE_TEAMS =
            "DROP TABLE IF EXISTS " + CyberScouterContract.Teams.TABLE_NAME;

    private static final String SQL_CREATE_WORD_CLOUD =
            "CREATE TABLE " + CyberScouterContract.WordCloud.TABLE_NAME + " (" +
                    CyberScouterContract.WordCloud.COLUMN_NAME_EVENT_ID + " INTEGER," +
                    CyberScouterContract.WordCloud.COLUMN_NAME_MATCH_ID + " INTEGER," +
                    CyberScouterContract.WordCloud.COLUMN_NAME_MATCH_SCOUTING_ID + " INTEGER," +
                    CyberScouterContract.WordCloud.COLUMN_NAME_TEAM + " TEXT," +
                    CyberScouterContract.WordCloud.COLUMN_NAME_WORD_ID + " INTEGER," +
                    CyberScouterContract.WordCloud.COLUMN_NAME_WORD_COUNT + " INTEGER," +
                    CyberScouterContract.WordCloud.COLUMN_NAME_DONE_SCOUTING + " INTEGER," +
                    CyberScouterContract.WordCloud.COLUMN_NAME_UPLOAD_STATUS + " INTEGER, " +
                    "UNIQUE(" +
                    CyberScouterContract.WordCloud.COLUMN_NAME_MATCH_SCOUTING_ID + "," +
                    CyberScouterContract.WordCloud.COLUMN_NAME_WORD_ID + "," +
                    CyberScouterContract.WordCloud.COLUMN_NAME_TEAM +
                    "))";

    private static final String SQL_DELETE_WORD_CLOUD =
            "DROP TABLE IF EXISTS " + CyberScouterContract.WordCloud.TABLE_NAME;

    private static final String SQL_CREATE_WORDS =
            "CREATE TABLE " + CyberScouterContract.Words.TABLE_NAME + " (" +
                    CyberScouterContract.Words.COLUMN_NAME_WORD_ID + " INTEGER PRIMARY KEY," +
                    CyberScouterContract.Words.COLUMN_NAME_WORD + " INTEGER," +
                    CyberScouterContract.Words.COLUMN_NAME_DISPLAY_WORD_ORDER + " INTEGER)"
            ;

    private static final String SQL_DELETE_WORDS =
            "DROP TABLE IF EXISTS " + CyberScouterContract.Words.TABLE_NAME;

    private static final String SQL_CREATE_TIME_CODE =
            "CREATE TABLE " + CyberScouterContract.TimeCode.TABLE_NAME + " (" +
                    CyberScouterContract.TimeCode.COLUMN_NAME_LAST_UPDATE + " TEXT)"
            ;

    private static final String SQL_DELETE_TIME_CODE =
            "DROP TABLE IF EXISTS " + CyberScouterContract.TimeCode.TABLE_NAME;

    public CyberScouterDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_CONFIG_ENTRIES);
        db.execSQL(SQL_CREATE_MATCHES);
        db.execSQL(SQL_CREATE_MATCHES_L2);
        db.execSQL(SQL_CREATE_USER_ENTRIES);
        db.execSQL(SQL_CREATE_QUESTIONS);
        db.execSQL(SQL_CREATE_TEAMS);
        db.execSQL(SQL_CREATE_WORD_CLOUD);
        db.execSQL(SQL_CREATE_WORDS);
        db.execSQL(SQL_CREATE_TIME_CODE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // simply to discard the data and start over
        db.execSQL(SQL_DELETE_CONFIG_ENTRIES);
        db.execSQL(SQL_DELETE_MATCHES);
        db.execSQL(SQL_DELETE_MATCHES_L2);
        db.execSQL(SQL_DELETE_USER_ENTRIES);
        db.execSQL(SQL_DELETE_QUESTIONS);
        db.execSQL(SQL_DELETE_TEAMS);
        db.execSQL(SQL_DELETE_WORD_CLOUD);
        db.execSQL(SQL_DELETE_WORDS);
        db.execSQL(SQL_DELETE_TIME_CODE);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
