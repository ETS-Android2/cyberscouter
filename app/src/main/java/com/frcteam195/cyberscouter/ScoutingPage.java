package com.frcteam195.cyberscouter;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class ScoutingPage extends AppCompatActivity implements NamePickerDialog.NamePickerDialogListener {
    final static private int FIELD_ORIENTATION_RIGHT = 0;
    final static private int FIELD_ORIENTATION_LEFT = 1;
    private static int field_orientation = FIELD_ORIENTATION_LEFT;

    private CyberScouterDbHelper mDbHelper = new CyberScouterDbHelper(this);
    private SQLiteDatabase _db = null;

    static protected Handler mFetchHandler;
    private Thread fetcherThread;
    private final static int START_PROGRESS = 0;
    private final static int FETCH_USERS = 1;
    private final static int FETCH_TEAMS = 2;
    private final static int FETCH_MATCHES = 3;
    private final static int FETCH_MATCH_TEAMS = 4;
    private static boolean isRed = true;

    private int mCurrentMatch;
    private String mCurrentMatchTeam;

    public static int getFieldOrientation() {
        return field_orientation;
    }

    BroadcastReceiver mOnlineStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int color = intent.getIntExtra("onlinestatus", Color.RED);
            updateStatusIndicator(color);
        }
    };

    BroadcastReceiver mUsersReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String ret = intent.getStringExtra("cyberscouterusers");
            updateUsers(ret);
        }
    };

    BroadcastReceiver mTeamsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String ret = intent.getStringExtra("cyberscouterteams");
            updateTeams(ret);
        }
    };

    BroadcastReceiver mMatchesReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("cyberscoutermatches")) {
                String ret = intent.getStringExtra("cyberscoutermatches");
                updateMatchesLocal(ret);
            } else if (intent.hasExtra("cyberscoutermatch")) {
            } else {
                System.out.println("mMatchesReceiver got unrecognized broadcast message!");
            }
        }
    };

    BroadcastReceiver mMatchTeamsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("cyberscoutermatchteams")) {
                String ret = intent.getStringExtra("cyberscoutermatchteams");
                updateMatchTeamsLocal(ret);
            } else {
                System.out.println("mMatchTeamsReceiver got unrecognized broadcast message!");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Button button;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scouting_page);

        registerReceiver(mOnlineStatusReceiver, new IntentFilter(BluetoothComm.ONLINE_STATUS_UPDATED_FILTER));
        registerReceiver(mUsersReceiver, new IntentFilter(CyberScouterUsers.USERS_FETCHED_FILTER));
        registerReceiver(mTeamsReceiver, new IntentFilter(CyberScouterTeams.TEAMS_UPDATED_FILTER));
        registerReceiver(mMatchesReceiver, new IntentFilter(CyberScouterMatchScouting.MATCH_SCOUTING_FETCHED_FILTER));
        registerReceiver(mMatchTeamsReceiver, new IntentFilter(CyberScouterMatches.MATCH_TEAMS_FETCHED_FILTER));

        button = findViewById(R.id.Button_Start);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPreAuto();
            }
        });

        Button npbutton = findViewById(R.id.Button_NamePicker);
        npbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNamePickerPage();

            }
        });

        button = findViewById(R.id.Button_Return);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToMainMenu();
            }
        });

        _db = mDbHelper.getWritableDatabase();

        fetcherThread = new Thread(new RemoteFetcher());

        ImageView iv = findViewById(R.id.imageView2);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flipFieldOrientation();
            }
        });
        iv.setEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkFieldOrientation();

        mFetchHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case START_PROGRESS:
                        showProgress();
                        break;
                    case FETCH_USERS:
                        fetchUsers();
                        break;
                    case FETCH_TEAMS:
                        fetchTeams();
                        break;
                    case FETCH_MATCHES:
                        fetchMatches();
                        break;
/*
                    case FETCH_MATCH_TEAMS:
                        fetchMatchTeams();
                        break;
*/
                    default:
                        throw new IllegalStateException("Unexpected value: " + msg.what);
                }
            }
        };

        if (null == fetcherThread) {
            fetcherThread = new Thread(new RemoteFetcher());
        }
        if (!fetcherThread.isAlive()) {
            fetcherThread.start();
        }
    }

    private static final class RemoteFetcher implements Runnable {

        @Override
        public void run() {
            Message msg = new Message();
            msg.what = START_PROGRESS;
            mFetchHandler.sendMessage(msg);
            try {
                Thread.sleep(500);
            } catch (Exception e) {
            }
            Message msg2 = new Message();
            msg2.what = FETCH_USERS;
            mFetchHandler.sendMessage(msg2);
            try {
                Thread.sleep(500);
            } catch (Exception e) {
            }
            Message msg3 = new Message();
            msg3.what = FETCH_TEAMS;
            mFetchHandler.sendMessage(msg3);
            Message msg4 = new Message();
            msg4.what = FETCH_MATCHES;
            mFetchHandler.sendMessage(msg4);
/*
            try {
                Thread.sleep(500);
            } catch (Exception e) {
            }
            Message msg5 = new Message();
            msg5.what = FETCH_MATCH_TEAMS;
            mFetchHandler.sendMessage(msg5);
*/
        }
    }

    private void showProgress() {
        ProgressBar pb = findViewById(R.id.progressBar_scoutingDataAccess);
        pb.setVisibility(View.VISIBLE);
    }

    private void fetchUsers() {
        Button npbutton = findViewById(R.id.Button_NamePicker);
        CyberScouterConfig cfg = CyberScouterConfig.getConfig(_db);
        if (CyberScouterConfig.UNKNOWN_USER_IDX != cfg.getUser_id()) {
            npbutton.setText(cfg.getUsername());
        } else {
            npbutton.setText("Select your name");
        }
        String csu_str = CyberScouterUsers.getUsersRemote(this, _db);
        if (null != csu_str) {
            updateUsers(csu_str);
        }
    }

    private void fetchTeams() {
        String cst_str = CyberScouterTeams.getTeamsRemote(this, _db);
        if (null != cst_str) {
            updateTeams(cst_str);
        }
    }

    private void fetchMatches() {
        CyberScouterConfig cfg = CyberScouterConfig.getConfig(_db);
        if (null != cfg) {
            String csms_str = CyberScouterMatchScouting.getMatchesRemote(this, _db,
                    cfg.getEvent_id(), cfg.getAlliance_station_id());
            if (null != csms_str) {
                updateMatchesLocal(csms_str);
            }
        }
    }

    private void fetchMatchTeams(int currentMatch) {
        String csmt_str = CyberScouterMatches.getCurrentMatchTeamsRemote(this, _db,
                currentMatch);
        if (null != csmt_str) {
            updateMatchTeamsLocal(csmt_str);
        }
    }

    @Override
    public void onBackPressed() {
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        fetcherThread = null;
    }

    @Override
    protected void onDestroy() {
        mFetchHandler.removeCallbacksAndMessages(null);
        _db.close();
        mDbHelper.close();
        unregisterReceiver(mOnlineStatusReceiver);
        unregisterReceiver(mUsersReceiver);
        unregisterReceiver(mTeamsReceiver);
        unregisterReceiver(mMatchesReceiver);
        unregisterReceiver(mMatchTeamsReceiver);
        super.onDestroy();
    }


    public void openPreAuto() {
        CyberScouterConfig cfg = CyberScouterConfig.getConfig(_db);

        if (null == cfg || (CyberScouterConfig.UNKNOWN_USER_IDX == cfg.getUser_id())) {
            FragmentManager fm = getSupportFragmentManager();
            NamePickerDialog npd = new NamePickerDialog();
            npd.show(fm, "namepicker");
        } else {
            Intent intent = new Intent(this, PreAutoPage.class);
            startActivity(intent);

        }
    }

    public void openNamePickerPage() {
        FragmentManager fm = getSupportFragmentManager();
        NamePickerDialog npd = new NamePickerDialog();
        npd.show(fm, "namepicker");
    }

    public void returnToMainMenu() {
        Intent intent = new Intent(this, MainActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        finish();
    }

    public void setUsername(String val, int idx) {
        try {
            int userId = 0;
            CyberScouterUsers csu = CyberScouterUsers.getLocalUser(_db, val);
            if (csu != null) {
                userId = csu.getUserID();
            }
            ContentValues values = new ContentValues();
            values.put(CyberScouterContract.ConfigEntry.COLUMN_NAME_USERNAME, val);
            values.put(CyberScouterContract.ConfigEntry.COLUMN_NAME_USERID, userId);
            int count = _db.update(
                    CyberScouterContract.ConfigEntry.TABLE_NAME,
                    values,
                    null,
                    null);
        } catch (Exception e) {
            e.printStackTrace();
            throw (e);
        }
    }

    private void flipFieldOrientation() {
        ImageView iv = findViewById(R.id.imageView2);

        field_orientation = field_orientation == FIELD_ORIENTATION_LEFT ? FIELD_ORIENTATION_RIGHT : FIELD_ORIENTATION_LEFT;
        CyberScouterConfig cfg = CyberScouterConfig.getConfig(_db);
        cfg.setFieldOrientation(_db, field_orientation);

        if (FIELD_ORIENTATION_LEFT == field_orientation) {
            iv.setRotation(0);
        } else {
            iv.setRotation(180);
        }
    }

    private void checkFieldOrientation() {
        ImageView iv = findViewById(R.id.imageView2);

        if (FIELD_ORIENTATION_LEFT == field_orientation) {
            iv.setRotation(0);
        } else {
            iv.setRotation(180);
        }
    }

    @Override
    public void onNameSelected(String name, int idx) {
        Button button = findViewById(R.id.Button_NamePicker);
        button.setText(name);
        setUsername(name, idx);
    }

    private void updateStatusIndicator(int color) {
        ImageView iv = findViewById(R.id.imageView_btIndicator);
        BluetoothComm.updateStatusIndicator(iv, color);
    }

    private void updateUsers(String json) {
        if (json.equalsIgnoreCase("fetched")) {
            json = CyberScouterUsers.getWebResponse();
        }
        if (!json.equalsIgnoreCase("skip")) {
            CyberScouterUsers.setUsers(_db, json);
        }
    }

    private void updateTeams(String teams) {
        if (teams.equalsIgnoreCase("fetch")) {
            teams = CyberScouterTeams.getWebResponse();
        }
        if (!teams.equalsIgnoreCase("update")) {
            CyberScouterTeams.setTeams(_db, teams);
        }
    }

    private void updateMatchesLocal(String json) {
        try {
            CyberScouterConfig cfg = CyberScouterConfig.getConfig(_db);
            if (json != null) {
                if (!json.equalsIgnoreCase("skip")) {
                    if (json.equalsIgnoreCase("fetch")) {
                        json = CyberScouterMatchScouting.getWebResponse();
                    }
                    CyberScouterMatchScouting.deleteOldMatches(_db, cfg.getEvent_id());
                    CyberScouterMatchScouting.mergeMatches(_db, json);
                }
            }
            CyberScouterMatchScouting csm = CyberScouterMatchScouting.getCurrentMatch(_db,
                    TeamMap.getNumberForTeam(cfg.getAlliance_station()));
            if (null != csm) {
                mCurrentMatch = csm.getMatchID();
                mCurrentMatchTeam = csm.getTeam();
                TextView tv = findViewById(R.id.textView7);
                tv.setText(getString(R.string.tagMatch, csm.getMatchNo()));
                TextView tvtn = findViewById(R.id.textView_teamNumber);
                tvtn.setText(getString(R.string.tagTeam, mCurrentMatchTeam));
                fetchMatchTeams(csm.getMatchID());
            } else {
                TextView tv = findViewById(R.id.textView7);
                tv.setText(getString(R.string.NoUnscoutedMatches));
                TextView tvtn = findViewById(R.id.textView_teamNumber);
                tvtn.setText(getString(R.string.tagTeam, "n/a"));
            }
        } catch (Exception e) {
            MessageBox.showMessageBox(this, "Fetch Match Information Failed",
                    "updateMatchesLocal",
                    String.format("Attempt to fetch match info and merge locally failed!\n%s",
                            e.getMessage()));
            e.printStackTrace();
        }
    }

    private void updateMatchTeamsLocal(String csmt_str) {
        try {
            if (csmt_str.equalsIgnoreCase("fetch")) {
                csmt_str = CyberScouterMatches.getWebResponse();
            }
            JSONArray ja = new JSONArray(csmt_str);
            if (null != ja && ja.length() > 0) {
                TextView tvtn = findViewById(R.id.textView_teamNumber);
                JSONObject jo = (JSONObject) ja.get(0);
                TextView tv = findViewById(R.id.textView20);
                String tname = jo.getString("RedTeam1");
                tv.setText(tname);
                if (tname.equals(mCurrentMatchTeam)) {
                    tvtn.setTextColor(Color.RED);
                    isRed = true;
                }
                tv = findViewById(R.id.textView21);
                tname = jo.getString("RedTeam2");
                tv.setText(tname);
                if (tname.equals(mCurrentMatchTeam)) {
                    tvtn.setTextColor(Color.RED);
                    isRed = true;
                }
                tv = findViewById(R.id.textView22);
                tname = jo.getString("RedTeam3");
                tv.setText(tname);
                if (tname.equals(mCurrentMatchTeam)) {
                    tvtn.setTextColor(Color.RED);
                    isRed = true;
                }
                tv = findViewById(R.id.textView35);
                tname = jo.getString("BlueTeam1");
                tv.setText(tname);
                if (tname.equals(mCurrentMatchTeam)) {
                    tvtn.setTextColor(Color.BLUE);
                    isRed = false;
                }
                tv = findViewById(R.id.textView27);
                tname = jo.getString("BlueTeam2");
                tv.setText(tname);
                if (tname.equals(mCurrentMatchTeam)) {
                    tvtn.setTextColor(Color.BLUE);
                    isRed = false;
                }
                tv = findViewById(R.id.textView26);
                tname = jo.getString("BlueTeam3");
                tv.setText(tname);
                if (tname.equals(mCurrentMatchTeam)) {
                    tvtn.setTextColor(Color.BLUE);
                    isRed = false;
                }
                Button button = findViewById(R.id.Button_Start);
                button.setEnabled(true);
            }
        } catch (Exception e) {
            MessageBox.showMessageBox(this, "Fetch Match Teams Information Failed",
                    "updateMatchTeamsLocal",
                    String.format("Attempt to fetch match team info failed!\n%s", e.getMessage()));
            e.printStackTrace();
        } finally {
            ProgressBar pb = findViewById(R.id.progressBar_scoutingDataAccess);
            pb.setVisibility(View.INVISIBLE);
        }
    }

    public static boolean getIsRed() {
        return isRed;
    }
}
