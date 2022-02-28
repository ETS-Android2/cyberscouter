package com.frcteam195.cyberscouter;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONObject;

public class ScoutingPage extends AppCompatActivity implements NamePickerDialog.NamePickerDialogListener {
    final static private int FIELD_ORIENTATION_RIGHT = 0;
    final static private int FIELD_ORIENTATION_LEFT = 1;
    private static int field_orientation = FIELD_ORIENTATION_LEFT;

    private CyberScouterDbHelper mDbHelper = new CyberScouterDbHelper(this);
    private SQLiteDatabase _db = null;

    private Handler mFetchHandler;
    private Thread fetcherThread;
    private final int START_PROGRESS = 0;
    private final int FETCH_USERS = 1;
    private final int FETCH_MATCHES = 2;
    private static boolean isRed = true;

    public static int getFieldOrientation(){return field_orientation;}

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

    BroadcastReceiver mMatchesReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String ret = intent.getStringExtra("cyberscoutermatches");
            updateMatchesLocal(ret);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Button button;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scouting_page);

        registerReceiver(mOnlineStatusReceiver, new IntentFilter(BluetoothComm.ONLINE_STATUS_UPDATED_FILTER));
        registerReceiver(mUsersReceiver, new IntentFilter(CyberScouterUsers.USERS_UPDATED_FILTER));
        registerReceiver(mMatchesReceiver, new IntentFilter(CyberScouterMatchScouting.MATCH_SCOUTING_UPDATED_FILTER));

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
        flipFieldOrientation();

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

        mFetchHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch(msg.what) {
                    case START_PROGRESS:
                        showProgress();
                        break;
                    case FETCH_USERS:
                        fetchUsers();
                        break;
                    case FETCH_MATCHES:
                        fetchMatches();
                        break;
                }
            }
        };

        if(null == fetcherThread) {
            fetcherThread = new Thread(new RemoteFetcher());
        }
        if(!fetcherThread.isAlive()) {
            fetcherThread.start();
        }
    }

    private class RemoteFetcher implements Runnable {

        @Override
        public void run() {
            Message msg = new Message();
            msg.what = START_PROGRESS;
            mFetchHandler.sendMessage(msg);
            try{ Thread.sleep(500); } catch(Exception e) {}
            Message msg2 = new Message();
            msg2.what = FETCH_USERS;
            mFetchHandler.sendMessage(msg2);
            Message msg3 = new Message();
            msg3.what = FETCH_MATCHES;
            mFetchHandler.sendMessage(msg3);
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
        if(null != csu_str) {
            updateUsers(csu_str);
        }
    }

    private void fetchMatches() {
        CyberScouterConfig cfg = CyberScouterConfig.getConfig(_db);
        if (null != cfg) {
            String csms_str = CyberScouterMatchScouting.getMatchesRemote(this, _db, cfg.getEvent_id());
            if(null != csms_str) {
                updateMatchesLocal(csms_str);
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        fetcherThread = null;
    }

    @Override
    protected void onDestroy() {
        CyberScouterDbHelper mDbHelper = new CyberScouterDbHelper(this);
        mDbHelper.close();
        unregisterReceiver(mOnlineStatusReceiver);
        unregisterReceiver(mUsersReceiver);
        unregisterReceiver(mMatchesReceiver);
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
            ContentValues values = new ContentValues();
            values.put(CyberScouterContract.ConfigEntry.COLUMN_NAME_USERNAME, val);
            values.put(CyberScouterContract.ConfigEntry.COLUMN_NAME_USERID, idx);
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
        ImageButton im1, im2;
        im1 = findViewById(R.id.imageButton);
        im2 = findViewById(R.id.imageButton2);

        Drawable d1 = im1.getDrawable();
        Drawable d2 = im2.getDrawable();

        im1.setImageDrawable(d2);
        im2.setImageDrawable(d1);

        ImageView iv = findViewById(R.id.imageView2);

        field_orientation = field_orientation == FIELD_ORIENTATION_LEFT ? FIELD_ORIENTATION_RIGHT : FIELD_ORIENTATION_LEFT;
        CyberScouterConfig cfg = CyberScouterConfig.getConfig(_db);
        cfg.setFieldOrientation(_db, field_orientation);

        if(FIELD_ORIENTATION_LEFT == field_orientation) {
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
    private void updateUsers(String json){
        if(!json.equalsIgnoreCase("skip")) {
            CyberScouterUsers.setUsers(_db, json);
        }
    }

    private void updateMatchesLocal(String json){
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
            CyberScouterMatchScouting csm = CyberScouterMatchScouting.getCurrentMatch(_db, TeamMap.getNumberForTeam(cfg.getAlliance_station()));
            if (null != csm) {
                TextView tv = findViewById(R.id.textView7);
                tv.setText(getString(R.string.tagMatch, csm.getMatchNo()));
                TextView tvtn = findViewById(R.id.textView_teamNumber);
                String currentMatchTeam = csm.getTeam();
                tvtn.setText(getString(R.string.tagTeam, currentMatchTeam));
                CyberScouterMatchScouting[] csma = CyberScouterMatchScouting.getCurrentMatchAllTeams(_db, csm.getMatchNo(), csm.getMatchID());
                if (null != csma && 6 == csma.length) {
                    tv = findViewById(R.id.textView20);
                    tv.setText(csma[0].getTeam());
                    if (csma[0].getTeam().equals(currentMatchTeam)) {
                        tvtn.setTextColor(Color.RED);
                        isRed = true;
                    }
                    tv = findViewById(R.id.textView21);
                    tv.setText(csma[1].getTeam());
                    if (csma[1].getTeam().equals(currentMatchTeam)) {
                        tvtn.setTextColor(Color.RED);
                        isRed = true;
                    }
                    tv = findViewById(R.id.textView22);
                    tv.setText(csma[2].getTeam());
                    if (csma[2].getTeam().equals(currentMatchTeam)) {
                        tvtn.setTextColor(Color.RED);
                        isRed = true;
                    }
                    tv = findViewById(R.id.textView35);
                    tv.setText(csma[3].getTeam());
                    if (csma[3].getTeam().equals(currentMatchTeam)) {
                        tvtn.setTextColor(Color.BLUE);
                        isRed = false;
                    }
                    tv = findViewById(R.id.textView27);
                    tv.setText(csma[4].getTeam());
                    if (csma[4].getTeam().equals(currentMatchTeam)) {
                        tvtn.setTextColor(Color.BLUE);
                        isRed = false;
                    }
                    tv = findViewById(R.id.textView26);
                    tv.setText(csma[5].getTeam());
                    if (csma[5].getTeam().equals(currentMatchTeam)) {
                        tvtn.setTextColor(Color.BLUE);
                        isRed = false;
                    }
                }
                Button button = findViewById(R.id.Button_Start);
                button.setEnabled(true);
            }
        } catch(Exception e) {
            MessageBox.showMessageBox(this, "Fetch Match Information Failed", "updateMatchesLocal",
                    String.format("Attempt to fetch match info and merge locally failed!\n%s", e.getMessage()));
            e.printStackTrace();
        } finally {
            ProgressBar pb = findViewById(R.id.progressBar_scoutingDataAccess);
            pb.setVisibility(View.INVISIBLE);
        }
    }

    public static boolean getIsRed()
    {
        return isRed;
    }

}
