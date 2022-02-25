package com.frcteam195.cyberscouter;

import android.provider.BaseColumns;

public class CyberScouterContract {

    private CyberScouterContract(){}

    public static class ConfigEntry implements BaseColumns {
        public static final String TABLE_NAME = "config";
        public static final String COLUMN_NAME_ALLIANCE_STATIOM = "alliance_station";
        public static final String COLUMN_NAME_ALLIANCE_STATION_ID = "alliance_station_id";
        public static final String COLUMN_NAME_EVENT = "event";
        public static final String COLUMN_NAME_EVENT_LOCATION = "event_location";
        public static final String COLUMN_NAME_EVENT_ID = "event_id";
        public static final String COLUMN_NAME_TABLET_NUM = "tablet_num";
        public static final String COLUMN_NAME_OFFLINE = "offline";
        public static final String COLUMN_NAME_FIELD_REDLEFT = "field_red_left";
        public static final String COLUMN_NAME_USERNAME = "username";
        public static final String COLUMN_NAME_USERID = "user_id";
        public static final String COLUMN_NAME_COMPUTER_TYPE_ID = "computer_type_id";
    }

    public static class Events implements BaseColumns {
        public static final String TABLE_NAME = "Events";
        public static final String COLUMN_NAME_EVENTID = "EventId";
        public static final String COLUMN_NAME_EVENTNAME = "EventName";
        public static final String COLUMN_NAME_EVENTLOCATION = "EventLocation";
        public static final String COLUMN_NAME_STARTDATE = "StartDate";
        public static final String COLUMN_NAME_ENDDATE = "EndDate";
        public static final String COLUMN_NAME_CURRENTEVENT = "CurrentEvent";
    }

    public static class Users implements BaseColumns {
        public static final String TABLE_NAME = "Users";
        public static final String COLUMN_NAME_USERID = "UserId";
        public static final String COLUMN_NAME_FIRSTNAME = "FirstName";
        public static final String COLUMN_NAME_LASTNAME = "LastName";
        public static final String COLUMN_NAME_CELLPHONE = "CellPhone";
        public static final String COLUMN_NAME_EMAIL = "Email";
    }

    public static class MatchScouting implements BaseColumns {
        public static final String TABLE_NAME = "MatchScouting";
        public static final String COLUMN_NAME_MATCHSCOUTINGID = "MatchScoutingID";
        public static final String COLUMN_NAME_EVENTID = "EventID";
        public static final String COLUMN_NAME_MATCHID = "MatchID";
        public static final String COLUMN_NAME_MATCH_NUMBER = "MatchNo";
        public static final String COLUMN_NAME_COMPUTERID = "ComputerID";
        public static final String COLUMN_NAME_SCOUTERID = "ScouterID";
        public static final String COLUMN_NAME_REVIEWERID = "ReviewerID";
        public static final String COLUMN_NAME_TEAM = "Team";
        public static final String COLUMN_NAME_TEAMMATCHNO = "TeamMatchNo";
        public static final String COLUMN_NAME_ALLIANCESTATIONID = "AllianceStationID";
        public static final String COLUMN_NAME_MATCHENDED = "MatchEnded";
        public static final String COLUMN_NAME_SCOUTINGSTATUS = "ScoutingStatus";
        public static final String COLUMN_NAME_AREASTOREVIEW = "AreasToReview";
        public static final String COLUMN_NAME_COMPLETE = "Complete";
        public static final String COLUMN_NAME_AUTOSTARTPOS = "AutoStartPos";
        public static final String COLUMN_NAME_AUTOPRELOAD = "AutoPreload";
        public static final String COLUMN_NAME_AUTODIDNOTSHOW = "AutoDidNotShow";
        public static final String COLUMN_NAME_AUTOMOVEBONUS = "AutoMoveBonus";
        public static final String COLUMN_NAME_AUTOBALLLOW = "AutoBallLow";
        public static final String COLUMN_NAME_AUTOBALLHIGH = "AutoBallHigh";
        public static final String COLUMN_NAME_AUTOBALLMISS = "AutoBallMiss";
        public static final String COLUMN_NAME_AUTOBALLPOS1 = "AutoBallPos1";
        public static final String COLUMN_NAME_AUTOBALLPOS2 = "AutoBallPos2";
        public static final String COLUMN_NAME_AUTOBALLPOS3 = "AutoBallPos3";
        public static final String COLUMN_NAME_AUTOBALLPOS4 = "AutoBallPos4";
        public static final String COLUMN_NAME_AUTOBALLPOS5 = "AutoBallPos5";
        public static final String COLUMN_NAME_AUTOBALLPOS6 = "AutoBallPos6";
        public static final String COLUMN_NAME_AUTOBALLPOS7 = "AutoBallPos7";
        public static final String COLUMN_NAME_AUTOBALLPOS8 = "AutoBallPos8";
        public static final String COLUMN_NAME_AUTOBALLPOS9 = "AutoBallPos9";
        public static final String COLUMN_NAME_AUTOBALLPOS10 = "AutoBallPos10";
        public static final String COLUMN_NAME_TELEBALLLOW = "TeleBallLow";
        public static final String COLUMN_NAME_TELEBALLHIGH = "TeleBallHigh";
        public static final String COLUMN_NAME_TELEBALLMISS = "TeleBallMiss";
        public static final String COLUMN_NAME_CLIMBSTATUS = "ClimbStatus";
        public static final String COLUMN_NAME_CLIMBHEIGHT = "ClimbHeight";
        public static final String COLUMN_NAME_CLIMBPOSITION = "ClimbPosition";
        public static final String COLUMN_NAME_INSTEADOFCLIMB = "InsteadOfClimb";
        public static final String COLUMN_NAME_SUMMLAUNCHPAD = "SummLaunchPad";
        public static final String COLUMN_NAME_SUMMSORTCARGO = "SummSortCargo";
        public static final String COLUMN_NAME_SUMMSHOOTDRIVING = "SummShootDriving";
        public static final String COLUMN_NAME_SUMMBROKEDOWN = "SummBrokeDown";
        public static final String COLUMN_NAME_SUMMLOSTCOMM = "SummLostComm";
        public static final String COLUMN_NAME_SUMMSUBSYSTEMBROKE = "SummSubSystemBroke";
        public static final String COLUMN_NAME_SUMMGROUNDPICKUP = "SummGroundPickup";
        public static final String COLUMN_NAME_SUMMTERMINALPICKUP = "SummTerminalPickup";
        public static final String COLUMN_NAME_SUMMPLAYEDDEFENSE = "SummPlayedDefense";
        public static final String COLUMN_NAME_SUMMDEFPLAYEDAGAINST = "SummDefPlayedAgainst";
        public static final String COLUMN_NAME_UPLOADSTATUS = "UploadStatus";
    }

    public static class MatchScoutingL2 implements BaseColumns {
        public static final String TABLE_NAME = "MatchScoutingL2";
        public static final String COLUMN_NAME_MATCHSCOUTINGL2ID = "MatchScoutingL2ID";
        public static final String COLUMN_NAME_EVENTID = "EventID";
        public static final String COLUMN_NAME_MATCHID = "MatchID";
        public static final String COLUMN_NAME_MATCH_NUMBER = "MatchNo";
        public static final String COLUMN_NAME_COMPUTERID = "ComputerID";
        public static final String COLUMN_NAME_SCOUTERID = "ScouterID";
        public static final String COLUMN_NAME_REVIEWERID = "ReviewerID";
        public static final String COLUMN_NAME_SCOUTINGSTATUS = "ScoutingStatus";
        public static final String COLUMN_NAME_TEAM_RED = "TeamRed";
        public static final String COLUMN_NAME_TEAM_BLUE = "TeamBlue";
        public static final String COLUMN_NAME_MATCHSCOUTINGIDRED = "MatchScoutingIDRed";
        public static final String COLUMN_NAME_MATCHSCOUTINGIDBLUE = "MatchScoutingIDBlue";
        public static final String COLUMN_NAME_COMMENT_RED = "CommentRed";
        public static final String COLUMN_NAME_COMMENT_BLUE = "CommentBlue";
        public static final String COLUMN_NAME_ALLIANCESTATIONID = "AllianceStationID";
        public static final String COLUMN_NAME_MATCHENDED = "MatchEnded";
        public static final String COLUMN_NAME_COMPLETE = "Complete";
        public static final String COLUMN_NAME_UPLOADSTATUS = "UploadStatus";
        public static final String COLUMN_NAME_T1MULTIROBOTDEFENSE = "t1MultiRobotDefense";
        public static final String COLUMN_NAME_T2MULTIROBOTDEFENSE = "t2MultiRobotDefense";
        public static final String COLUMN_NAME_T3MULTIROBOTDEFENSE = "t3MultiRobotDefense";
        public static final String COLUMN_NAME_T1PRIMARYDEFENSE = "t1PrimaryDefense";
        public static final String COLUMN_NAME_T2PRIMARYDEFENSE = "t2PrimaryDefense";
        public static final String COLUMN_NAME_T3PRIMARYDEFENSE = "t3PrimaryDefense";
        public static final String COLUMN_NAME_T1SECONDARYDEFENSE = "t1SecondaryDefense";
        public static final String COLUMN_NAME_T2SECONDARYDEFENSE = "t2SecondaryDefense";
        public static final String COLUMN_NAME_T3SECONDARYDEFENSE = "t3SecondaryDefense";
        public static final String COLUMN_NAME_T1HOWLONG = "t1HowLong";
        public static final String COLUMN_NAME_T2HOWLONG = "t2HowLong";
        public static final String COLUMN_NAME_T3HOWLONG = "t3HowLong";
    }

    public static class Questions implements BaseColumns {
        public static final String TABLE_NAME = "Questions";
        public static final String COLUMN_NAME_QUESTIONID = "QuestionId";
        public static final String COLUMN_NAME_EVENTID = "EventID";
        public static final String COLUMN_NAME_QUESTIONNUMBER = "QuestionNumber";
        public static final String COLUMN_NAME_QUESTIONTEXT = "QuestionText";
        public static final String COLUMN_NAME_ANSWERS = "Answers";
    }

    public static class Teams implements BaseColumns {
        public static final String TABLE_NAME = "Teams";
        public static final String COLUMN_NAME_TEAM = "Team";
        public static final String COLUMN_NAME_TEAM_NAME = "TeamName";
        public static final String COLUMN_NAME_TEAM_LOCATION = "TeamLocation";
        public static final String COLUMN_NAME_TEAM_CITY = "TeamCity";
        public static final String COLUMN_NAME_TEAM_STATE_PROV = "TeamStateProv";
        public static final String COLUMN_NAME_TEAM_COUNTRY = "TeamCountry";
        public static final String COLUMN_NAME_NUM_WHEELS = "NumWheels";
        public static final String COLUMN_NAME_DRIVE_MOTORS = "NumDriveMotors";
        public static final String COLUMN_NAME_WHEEL_TYPE_ID = "WheelTypeID";
        public static final String COLUMN_NAME_DRIVE_TYPE_ID = "DriveTypeID";
        public static final String COLUMN_NAME_MOTOR_TYPE_ID = "MotorTypeID";
        public static final String COLUMN_NAME_LANGUAGE_ID = "LanguageID";
        public static final String COLUMN_NAME_SPEED = "Speed";
        public static final String COLUMN_NAME_GEAR_RATIO = "GearRatio";
        public static final String COLUMN_NAME_NUM_GEAR_SPEED = "NumGearSpeed";
        public static final String COLUMN_NAME_ROBOT_LENGTH = "RobotLength";
        public static final String COLUMN_NAME_ROBOT_WIDTH = "RobotWidth";
        public static final String COLUMN_NAME_ROBOT_HEIGHT = "RobotHeight";
        public static final String COLUMN_NAME_ROBOT_WEIGHT = "RobotWeight";
        public static final String COLUMN_NAME_PNEUMATICS = "Pneumatics";
        public static final String COLUMN_NAME_INTAKE_TYPE = "IntakeType";
        public static final String COLUMN_NAME_PRE_LOAD = "Preload";
        public static final String COLUMN_NAME_HAS_AUTO = "HasAuto";
        public static final String COLUMN_NAME_AUTO_SCORED_HIGH = "AutoScoredHigh";
        public static final String COLUMN_NAME_AUTO_SCORED_LOW = "AutoScoredLow";
        public static final String COLUMN_NAME_MOVE_BONUS = "MoveBonus";
        public static final String COLUMN_NAME_AUTO_PICKUP = "AutoPickUp";
        public static final String COLUMN_NAME_AUTO_START_POS_ID = "AutoStartPosID";
        public static final String COLUMN_NAME_AUTO_SUMMARY = "AutoSummary";
        public static final String COLUMN_NAME_AUTO_HUMAN = "AutoHuman";
        public static final String COLUMN_NAME_TELE_BALLS_SCORED_HIGH = "TeleBallsScoredHigh";
        public static final String COLUMN_NAME_TELE_BALLS_SCORED_LOW = "TeleBallsScoredLow";
        public static final String COLUMN_NAME_MAX_BALL_CAPACITY = "MaxBallCapacity";
        public static final String COLUMN_NAME_TELE_DEFENSE = "TeleDefense";
        public static final String COLUMN_NAME_TELE_DEFENSE_EVADE = "TeleDefenseEvade";
        public static final String COLUMN_NAME_TELE_STRATEGY = "TeleStrategy";
        public static final String COLUMN_NAME_TELE_DEFENSE_STRAT = "TeleDefenseStrat";
        public static final String COLUMN_NAME_TELE_SORT_CARGO = "TeleSortCargo";
        public static final String COLUMN_NAME_TELE_SHOOT_WHILE_DRIVE = "TeleShootWhileDrive";
        public static final String COLUMN_NAME_CAN_CLIMB = "CanClimb";
        public static final String COLUMN_NAME_CLIMB_POSITION = "ClimbPosition";
        public static final String COLUMN_NAME_CLIMB_STRATEGY = "ClimbStrategy";
        public static final String COLUMN_NAME_CLIMB_TIME = "ClimbTime";
        public static final String COLUMN_NAME_CLIMB_HEIGHT_ID = "ClimbHeightID";
        public static final String COLUMN_NAME_DONE_SCOUTING = "DoneScouting";
        public static final String COLUMN_NAME_UPLOAD_STATUS = "UploadStatus";
    }

    public static class WordCloud implements BaseColumns {
        public static final String TABLE_NAME = "WordCloud";
        public static final String COLUMN_NAME_EVENT_ID = "EventID";
        public static final String COLUMN_NAME_MATCH_ID = "MatchID";
        public static final String COLUMN_NAME_MATCH_SCOUTING_ID = "MatchScoutingID";
        public static final String COLUMN_NAME_TEAM = "Team";
        public static final String COLUMN_NAME_WORD_ID = "WordID";
        public static final String COLUMN_NAME_WORD_COUNT = "WordCount";
        public static final String COLUMN_NAME_DONE_SCOUTING = "DoneScouting";
        public static final String COLUMN_NAME_UPLOAD_STATUS = "UploadStatus";
    }

    public static class Words implements BaseColumns {
        public static final String TABLE_NAME = "Words";
        public static final String COLUMN_NAME_WORD_ID = "WordID";
        public static final String COLUMN_NAME_WORD = "Word";
        public static final String COLUMN_NAME_DISPLAY_WORD_ORDER = "DisplayWordOrder";
    }

    public static class TimeCode implements BaseColumns {
        public static final String TABLE_NAME = "TimeCode";
        public static final String COLUMN_NAME_LAST_UPDATE = "LastUpdate";
    }
}
