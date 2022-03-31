package com.frcteam195.cyberscouter;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private ComponentName _serviceComponentName = null;
    private Button button;

    private Handler mConfigHandler;

    public static AppCompatActivity _activity;

    private Thread fetcherThread;
    final private static int START_PROGRESS = 0;
    final private static int UPDATE_CONFIG = 1;

    BroadcastReceiver mConfigReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String ret = intent.getStringExtra("cyberscouterconfig");
            processConfig(ret);
        }
    };

    BroadcastReceiver mOnlineStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int color = intent.getIntExtra("onlinestatus", Color.RED);
            updateStatusIndicator(color);
        }
    };

    BroadcastReceiver mMatchesUpdater = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Integer iret = intent.getIntExtra("cyberscoutermatch", -99);
            matchScoutingWebUpdated(iret);
        }
    };


    private CyberScouterDbHelper mDbHelper = new CyberScouterDbHelper(this);
    SQLiteDatabase _db = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // The following lines allow diagnosis of leaked connections and such
/*        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .build()); */
        setContentView(R.layout.activity_main);

        try {
            if (null == _serviceComponentName) {
                _activity = this;
                Intent backgroundIntent = new Intent(getApplicationContext(), BackgroundUpdater.class);
                _serviceComponentName = startService(backgroundIntent);
                if (null == _serviceComponentName) {
                    MessageBox.showMessageBox(MainActivity.this, "Start Service Failed Alert", "processConfig", "Attempt to start background update service failed!");
                }
            }
        } catch (Exception e) {
            MessageBox.showMessageBox(MainActivity.this, "Start Service Failed Alert", "processConfig", "Attempt to start background update service failed!\n\n" +
                    "The error is:\n" + e.getMessage());
        }

        _db = mDbHelper.getWritableDatabase();
        CyberScouterTimeCode.setLast_update(_db, 0);
        System.out.println(String.format(">>>>>>>>>>>>>>>>>>>>>>>Reseting LastUpdate to %d", 0));


        registerReceiver(mConfigReceiver, new IntentFilter(CyberScouterConfig.CONFIG_UPDATED_FILTER));
        registerReceiver(mOnlineStatusReceiver, new IntentFilter(BluetoothComm.ONLINE_STATUS_UPDATED_FILTER));
        registerReceiver(mMatchesUpdater, new IntentFilter(CyberScouterMatchScouting.MATCH_SCOUTING_UPDATED_FILTER));


        button = findViewById(R.id.button_scouting);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openScouting();

            }
        });

        button = findViewById(R.id.button_mainForceResync);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onResume();

            }
        });

        TextView tv = findViewById(R.id.textView_teamName);
        tv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return (resetDatabase());
            }
        });

        String btname = Settings.Secure.getString(this.getContentResolver(), "bluetooth_name");
        FakeBluetoothServer fbs = new FakeBluetoothServer(btname);

        if (FakeBluetoothServer.communicationMethod == FakeBluetoothServer.COMM.BLUETOOTH) {
            final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            BluetoothAdapter _bluetoothAdapter = bluetoothManager.getAdapter();

            // Ensures Bluetooth is available on the device and it is enabled. If not,
            // displays a dialog requesting user permission to enable Bluetooth.
            if (_bluetoothAdapter == null || !_bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                int REQUEST_ENABLE_BT = 1;
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
        fetcherThread = new Thread(new ConfigFetcher());
    }

    @Override
    protected void onResume() {
        super.onResume();

        mConfigHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case START_PROGRESS:
                        showProgress();
                        break;
                    case UPDATE_CONFIG:
                        fetchConfig();
                        break;
                }
            }
        };

        if (null == fetcherThread) {
            fetcherThread = new Thread(new ConfigFetcher());
        }
        if (!fetcherThread.isAlive()) {
            fetcherThread.start();
        }
    }

    private class ConfigFetcher implements Runnable {

        @Override
        public void run() {
            Message msg = new Message();
            msg.what = START_PROGRESS;
            mConfigHandler.sendMessage(msg);
            int loops = 0;
            while (BluetoothComm.bLastBTCommFailed() && loops < 5) {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                }
                loops++;
            }
            Message msg2 = new Message();
            msg2.what = UPDATE_CONFIG;
            mConfigHandler.sendMessage(msg2);
        }
    }

    private void showProgress() {
        ProgressBar pb = findViewById(R.id.progressBar_mainDataAccess);
        pb.setVisibility(View.VISIBLE);
    }

    private void fetchConfig() {
        String cfg_str = CyberScouterConfig.getConfigRemote(this);
        if (null != cfg_str) {
            processConfig(cfg_str);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        if (REQUEST_ENABLE_BT == requestCode) {
//            Toast.makeText(getApplicationContext(), String.format("Result: %d", resultCode), Toast.LENGTH_LONG).show();
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        fetcherThread = null;
    }

    @Override
    protected void onDestroy() {
        _db.close();
        mDbHelper.close();
        unregisterReceiver(mConfigReceiver);
        unregisterReceiver(mOnlineStatusReceiver);
        unregisterReceiver(mMatchesUpdater);
        super.onDestroy();
    }

    private void processConfig(String config_json) {
        try {
            if (null != config_json) {
                if (!config_json.equalsIgnoreCase("skip")) {
                    JSONObject jo = new JSONObject(config_json);
                    CyberScouterConfig.setConfigLocal(_db, jo);
                    CyberScouterConfig cfg = CyberScouterConfig.getConfig(_db);
                    CyberScouterMatchScouting.deleteOldMatches(_db, cfg.getEvent_id());
                }
            }
        } catch (Exception e) {
            MessageBox.showMessageBox(this, "Exception Caught", "processConfig", "An exception occurred: \n" + e.getMessage());
            e.printStackTrace();
        }
        populateView();
    }

    void populateView() {
        button = findViewById(R.id.button_scouting);
        CyberScouterConfig cfg = CyberScouterConfig.getConfig(_db);
        if (null != cfg) {
            TextView textView = findViewById(R.id.textView_eventString);
            textView.setText(cfg.getEvent() + "\n" + cfg.getEvent_location());
            textView = findViewById(R.id.textView_roleString);
            String allianceStation = cfg.getAlliance_station();
            if (allianceStation.startsWith("Blu"))
                textView.setTextColor(Color.BLUE);
            else if (allianceStation.startsWith("Red"))
                textView.setTextColor(Color.RED);
            else
                textView.setTextColor(Color.BLACK);
            textView.setText(allianceStation);

            button.setEnabled(true);

            button = findViewById(R.id.button_mainForceResync);
            button.setEnabled(false);
            button.setVisibility(View.INVISIBLE);

            ProgressBar pb = findViewById(R.id.progressBar_mainDataAccess);
            pb.setVisibility(View.INVISIBLE);
        } else {
            MessageBox.showMessageBox(this, "No Event Information", "MainActivity.populateView",
                    "No Event information is available.  You need to sync with the server, and you are either not close enough or the server is not running.  Ask a mentor for assistance.");
            button.setEnabled(false);

            button = findViewById(R.id.button_mainForceResync);
            button.setEnabled(true);
            button.setVisibility(View.VISIBLE);
        }
    }

    void openScouting() {
        CyberScouterDbHelper mDbHelper = new CyberScouterDbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        CyberScouterConfig cfg = CyberScouterConfig.getConfig(db);

        Class nextIntent;
        switch (cfg.getComputer_type_id()) {
            case (CyberScouterConfig.CONFIG_COMPUTER_TYPE_LEVEL_2_SCOUTER):
                nextIntent = L2ScoutingPage.class;
                break;
            case (CyberScouterConfig.CONFIG_COMPUTER_TYPE_LEVEL_PIT_SCOUTER):
                nextIntent = PitScoutingActivity.class;
                break;
            default:
                nextIntent = ScoutingPage.class;
        }

        Intent intent = new Intent(this, nextIntent);
        startActivity(intent);
        db.close();
    }

    void updateStatusIndicator(int color) {
        ImageView iv = findViewById(R.id.imageView_btIndicator);
        BluetoothComm.updateStatusIndicator(iv, color);
    }

    void matchScoutingWebUpdated(Integer iret) {
        try {
            if (iret != -99) {
                CyberScouterMatchScouting.updateMatchUploadStatus(_db, iret, UploadStatus.UPLOADED);
                System.out.println(String.format("CyberScouterMatchScouting id %d updated in AWS!!", iret));
                popToast(String.format(Locale.getDefault(), "Match Scouting id %d was uploaded successfully.", iret));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void popToast(final String msg) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean resetDatabase() {
        AlertDialog.Builder messageBox = new AlertDialog.Builder(MainActivity.this);
        messageBox.setTitle("Reset Local Database");
        messageBox.setMessage("Are you sure you want to delete the local database and reload it from the remote server?\n");
        messageBox.setCancelable(true);
        //Yes Button
        messageBox.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doDatabaseReset();
                dialog.dismiss();
            }
        });

        //No Button
        messageBox.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog msb = messageBox.create();
        msb.show();

        return (true);
    }

    public void doDatabaseReset() {
        CyberScouterDbHelper mDbHelper = new CyberScouterDbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        mDbHelper.onDowngrade(db, 0, 1);
        Intent intent = this.getIntent();
        finish();
        startActivity(intent);
        db.close();
    }
}
