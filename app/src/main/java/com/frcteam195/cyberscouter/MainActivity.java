package com.frcteam195.cyberscouter;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private ComponentName _serviceComponentName = null;
    private Button button;

    public static AppCompatActivity _activity;

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

    private CyberScouterDbHelper mDbHelper = new CyberScouterDbHelper(this);
    SQLiteDatabase _db = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        ProgressBar pb = findViewById(R.id.progressBar_mainDataAccess);
        pb.setVisibility(View.INVISIBLE);

        _db = mDbHelper.getWritableDatabase();

        registerReceiver(mConfigReceiver, new IntentFilter(CyberScouterConfig.CONFIG_UPDATED_FILTER));
        registerReceiver(mOnlineStatusReceiver, new IntentFilter(BluetoothComm.ONLINE_STATUS_UPDATED_FILTER));

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

    @Override
    protected void onResume() {
        super.onResume();
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
        ProgressBar pb = findViewById(R.id.progressBar_mainDataAccess);
        pb.setVisibility(View.INVISIBLE);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mDbHelper.close();
        unregisterReceiver(mConfigReceiver);
        unregisterReceiver(mOnlineStatusReceiver);
        super.onDestroy();
    }

    private void processConfig(String config_json) {
        try {
            if (null != config_json) {
                if(!config_json.equalsIgnoreCase("skip")) {
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

    void populateView(){
        button = findViewById(R.id.button_scouting);
        CyberScouterConfig cfg = CyberScouterConfig.getConfig(_db);
        if(null != cfg) {
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

        ProgressBar pb = findViewById(R.id.progressBar_mainDataAccess);
        pb.setVisibility(View.VISIBLE);

        Class nextIntent;
        switch (cfg.getComputer_type_id()) {
            case (CyberScouterConfig.CONFIG_COMPUTER_TYPE_LEVEL_2_SCOUTER):
                nextIntent = WordCloudActivity.class;
                break;
            case (CyberScouterConfig.CONFIG_COMPUTER_TYPE_LEVEL_PIT_SCOUTER):
                nextIntent = PitScoutingActivity.class;
                break;
            default:
                nextIntent = ScoutingPage.class;
        }

        Intent intent = new Intent(this, nextIntent);
        startActivity(intent);
    }

    void updateStatusIndicator(int color) {
        ImageView iv = findViewById(R.id.imageView_btIndicator);
        BluetoothComm.updateStatusIndicator(iv, color);
    }
}
