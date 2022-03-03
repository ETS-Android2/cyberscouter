package com.frcteam195.cyberscouter;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import org.json.JSONObject;

public class L2ScoutingPage extends AppCompatActivity implements NamePickerDialog.NamePickerDialogListener {
    private CyberScouterDbHelper mDbHelper = new CyberScouterDbHelper(this);
    private SQLiteDatabase _db = null;
    private boolean redSelected = true;
    private int currentMatch = 1;
    CyberScouterMatches current_csm;

    private Handler mFetchHandler;
    private Thread fetcherThread;
    private final int START_PROGRESS = 0;
    private final int FETCH_USERS = 1;
    private final int FETCH_MATCHES = 2;

    private int[] teamButtons = {R.id.radioButton_l2Red, R.id.radioButton_l2Blue};

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

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Button button;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_l2_scouting_page);

        registerReceiver(mOnlineStatusReceiver, new IntentFilter(BluetoothComm.ONLINE_STATUS_UPDATED_FILTER));
        registerReceiver(mUsersReceiver, new IntentFilter(CyberScouterUsers.USERS_UPDATED_FILTER));
        registerReceiver(mMatchesReceiver, new IntentFilter(CyberScouterMatches.MATCHES_UPDATED_FILTER));

        button = findViewById(R.id.Button_Start);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWordCloud();
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

        RadioButton rb = findViewById(R.id.radioButton_l2Red);
        rb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redSelected = true;
                allianceSelected(true);
            }
        });

        rb = findViewById(R.id.radioButton_l2Blue);
        rb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redSelected = false;
                allianceSelected(false);
            }
        });

        EditText et = findViewById(R.id.editText_l2MatchNumber);
        et.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                matchNumberChanged(v, actionId, event);
                return(true);
            }
        });

        _db = mDbHelper.getWritableDatabase();
        fetcherThread = new Thread(new RemoteFetcher());
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
            String csms_str = CyberScouterMatches.getMatchesRemote(this, _db);
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


    public void openWordCloud() {
        CyberScouterConfig cfg = CyberScouterConfig.getConfig(_db);

        if (null == cfg || (CyberScouterConfig.UNKNOWN_USER_IDX == cfg.getUser_id())) {
            FragmentManager fm = getSupportFragmentManager();
            NamePickerDialog npd = new NamePickerDialog();
            npd.show(fm, "namepicker");
        } else {
            Intent intent = new Intent(this, WordCloudActivity.class);
            try {
                JSONObject jo = new JSONObject();
                jo.put("match_number", currentMatch);
                jo.put("is_red", redSelected);
                intent.putExtra("params", jo.toString());
            } catch(Exception e) {
                MessageBox.showMessageBox(this, "Launch Word Cloud Failed", "openWordCloud",
                        String.format("Attempt to Word Cloud activity failed!\n%s", e.getMessage()));
                e.printStackTrace();
            }
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
                        json = CyberScouterMatches.getWebResponse();
                    }
                    CyberScouterMatches.deleteOldMatches(_db, cfg.getEvent_id());
                    CyberScouterMatches.setMatches(_db, json);
                }
            }
            CyberScouterMatches csm = CyberScouterMatches.getLocalMatch(_db, cfg.getEvent_id(), currentMatch);
            if (null != csm) {
                updateMatchViews(csm);
                current_csm = csm;
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

    private void updateMatchViews(CyberScouterMatches csm) {
        EditText et = findViewById(R.id.editText_l2MatchNumber);
        et.setText(String.valueOf(currentMatch));

        TextView tv = findViewById(R.id.textView_l2RedAlliance);
        setColor(tv, Color.RED, Color.LTGRAY);
        tv = findViewById(R.id.textView_l2Red1);
        tv.setText(csm.getRedTeam1());
        setColor(tv, Color.RED, Color.LTGRAY);
        tv = findViewById(R.id.textView_l2Red2);
        tv.setText(csm.getRedTeam2());
        setColor(tv, Color.RED, Color.LTGRAY);
        tv = findViewById(R.id.textView_l2Red3);
        tv.setText(csm.getRedTeam3());
        setColor(tv, Color.RED, Color.LTGRAY);

        tv = findViewById(R.id.textView_l2BlueAlliance);
        setColor(tv, Color.LTGRAY, Color.BLUE);
        tv = findViewById(R.id.textView_l2Blue1);
        tv.setText(csm.getBlueTeam1());
        setColor(tv, Color.LTGRAY, Color.BLUE);
        tv = findViewById(R.id.textView_l2Blue2);
        tv.setText(csm.getBlueTeam2());
        setColor(tv, Color.LTGRAY, Color.BLUE);
        tv = findViewById(R.id.textView_l2Blue3);
        tv.setText(csm.getBlueTeam3());
        setColor(tv, Color.LTGRAY, Color.BLUE);
    }

    private void setColor(TextView tv, int isRedColor, int isNotRedColor) {
        if(redSelected) {tv.setTextColor(isRedColor);}
        else {tv.setTextColor(isNotRedColor);}
    }

    public void allianceSelected(boolean redClicked) {
        RadioButton rbRed = findViewById(R.id.radioButton_l2Red);
        RadioButton rbBlue = findViewById(R.id.radioButton_l2Blue);
        if(redClicked) {
            redSelected = true;
            rbRed.setChecked(true);
            rbBlue.setChecked(false);
        } else {
            redSelected = false;
            rbRed.setChecked(false);
            rbBlue.setChecked(true);
        }
        updateMatchViews(current_csm);
    }

    private void matchNumberChanged(TextView tv, int actionId, KeyEvent event) {
        if(actionId > 0 || (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            currentMatch = Integer.valueOf(String.valueOf(tv.getText())).intValue();
            try {
                CyberScouterConfig cfg = CyberScouterConfig.getConfig(_db);
                CyberScouterMatches csm = CyberScouterMatches.getLocalMatch(_db, cfg.getEvent_id(), currentMatch);
                if (null != csm) {
                    updateMatchViews(csm);
                    current_csm = csm;
                }
            } catch(Exception e) {
                MessageBox.showMessageBox(this, "Set Match Information Failed", "matchNumberChanged",
                        String.format("Attempt to update match info on the screen failed!\n%s", e.getMessage()));
                e.printStackTrace();
            }
        }
    }

}
