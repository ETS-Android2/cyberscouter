package com.frcteam195.cyberscouter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class PreAutoPage extends AppCompatActivity {
    private Button button;
    private final int[] startPositionButtons = {R.id.startbutton1, R.id.startbutton2, R.id.startbutton3, R.id.startbutton4, R.id.startbutton5, R.id.startbutton6};
    private final int[] preloadButtons = {R.id.preloadYesBtn, R.id.preloadNoBtn};
    private int defaultButtonTextColor = Color.LTGRAY;
    private final int SELECTED_BUTTON_TEXT_COLOR = Color.GREEN;
    private Button didnotshowyesbutton;
    private int currentCommStatusColor;
    private int preload = 0;
    private boolean[] compCheck = {false, false};

    private final CyberScouterDbHelper mDbHelper = new CyberScouterDbHelper(this);
    private SQLiteDatabase _db;

    private String[] _lColumns = {CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOSTARTPOS,
            CyberScouterContract.MatchScouting.COLUMN_NAME_AUTODIDNOTSHOW};
    private int[] _lValues;
    private int _didNotShow = 0;
    private int _startPos = 0;

    BroadcastReceiver mOnlineStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int color = intent.getIntExtra("onlinestatus", Color.RED);
            updateStatusIndicator(color);
        }
    };

    public void moveStartButtons()
    {
        //move button 6
        button = findViewById(R.id.startbutton6);
        button.setX(-960); button.setY(-320);

        //move button 5
        button = findViewById(R.id.startbutton5);
        button.setX(-780); button.setY(-280); button.setRotation(-55);

        //move button 4
        button = findViewById(R.id.startbutton4);
        button.setX(-620);button.setY(-200); button.setRotation(-35);

        //move button 3
        button = findViewById(R.id.startbutton3);
        button.setX(-510); button.setY(25);

        //move button 2
        button = findViewById(R.id.startbutton2);
        button.setX(-550); button.setY(220);

        //move button 1
        button = findViewById(R.id.startbutton1);
        button.setX(-670); button.setY(400);
    }


    @Nullable
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_auto_page);

        _db = mDbHelper.getWritableDatabase();

        CyberScouterConfig cfg = CyberScouterConfig.getConfig(_db);

        ImageView iv = findViewById(R.id.imageView6);
        if(ScoutingPage.getIsRed()){
            iv.setImageResource(R.drawable.betterredfield2022);
            iv.setRotation(iv.getRotation() + 180);
        }
        else {
            iv.setImageResource(R.drawable.betterbluefield2022);
        }
        if (null != cfg && !cfg.isFieldRedLeft()) {
            iv.setRotation(iv.getRotation() + 180);
            System.out.println(iv.getRotation());
            int rot = (int)(iv.getRotation() % 360);
            if(rot > 100) {
                moveStartButtons();
            }
        }

        button = findViewById(R.id.startbutton1);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startPosition(1);
            }
        });

        button = findViewById(R.id.startbutton2);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startPosition(2);
            }
        });

        button = findViewById(R.id.startbutton3);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startPosition(3);
            }
        });

        button = findViewById(R.id.startbutton4);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startPosition(4);
            }
        });

        button = findViewById(R.id.startbutton5);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startPosition(5);
            }
        });

        button = findViewById(R.id.startbutton6);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startPosition(6);
            }
        });

        button = findViewById(R.id.didntshowyesbutton);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                didNotShow();
            }
        });

        button = findViewById(R.id.PreAutoContinueButton);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                openAutoPage();
            }
        });

        button = findViewById(R.id.button_preAutoPrevious);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                previous();
            }
        });

        button = findViewById(R.id.preloadYesBtn);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                preloadYes();

            }
        });

        button = findViewById(R.id.preloadNoBtn);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                preloadNo();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(mOnlineStatusReceiver, new IntentFilter(BluetoothComm.ONLINE_STATUS_UPDATED_FILTER));

        CyberScouterConfig cfg = CyberScouterConfig.getConfig(_db);
        CyberScouterMatchScouting csm = null;
        if (null != cfg) {
            csm = CyberScouterMatchScouting.getCurrentMatch(_db, TeamMap.getNumberForTeam(cfg.getAlliance_station()));
        }

        if (null != csm) {
            TextView tv = findViewById(R.id.textView_preAutoMatch);
            tv.setText(getString(R.string.tagMatch, csm.getMatchNo()));
            tv = findViewById(R.id.textView_preAutoTeam);
            tv.setText(getString(R.string.tagTeam, csm.getTeam()));

            String[] lColumns = {CyberScouterContract.MatchScouting.COLUMN_NAME_AUTODIDNOTSHOW};
            Integer[] lval = {0};
            try {
                CyberScouterMatchScouting.updateMatchMetric(_db, lColumns, lval, cfg);
                csm = CyberScouterMatchScouting.getCurrentMatch(_db, TeamMap.getNumberForTeam(cfg.getAlliance_station()));
            } catch(Exception e) {
                e.printStackTrace();
            }
            _startPos = csm.getAutoStartPos();
            button = findViewById(R.id.PreAutoContinueButton);
            if(0 < _startPos) {
                FakeRadioGroup.buttonDisplay(this, _startPos - 1, startPositionButtons, SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
                compCheck[0] = true;
            } else {
                compCheck[0] = false;
            }
            preload = csm.getAutoPreload();
            FakeRadioGroup.buttonDisplay(this, preload, preloadButtons, SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
        }
    }

    @Override
    protected void onDestroy(){
        unregisterReceiver(mOnlineStatusReceiver);
        super.onDestroy();
    }

    private void didNotShow() {
        _didNotShow = 1;
        updatePreAutoData();
        Intent intent = new Intent(this, SubmitPage.class);
        startActivity(intent);
    }

    private void updatePreAutoData() {
        CyberScouterConfig cfg = CyberScouterConfig.getConfig(_db);
        try {
            Integer[] _lValues = {_startPos, _didNotShow};
            CyberScouterMatchScouting.updateMatchMetric(_db, _lColumns, _lValues, cfg);
        } catch(Exception e) {
            e.printStackTrace();
            MessageBox.showMessageBox(this, "Update Error",
                    "PreAutoPage.updatePreAutoData", "SQLite update failed!\n "+e.getMessage());
        }
    }

    public void startPosition(int val) {
        FakeRadioGroup.buttonPressed(this, val - 1, startPositionButtons, CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOSTARTPOS, SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
        _startPos = val;
        compCheck[0] = true;
        if(compCheck[0] && compCheck[1]) {
            button = findViewById(R.id.PreAutoContinueButton);
            button.setEnabled(true);
        }
    }

    private void preloadYes()
    {
        FakeRadioGroup.buttonPressed(this, 0, preloadButtons, CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOSTARTPOS, SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
        compCheck[1] = true;
        preload = 1;
        if(compCheck[0])
        {
            button = findViewById(R.id.PreAutoContinueButton);
            button.setEnabled(true);
        }
    }

    private void preloadNo()
    {
        FakeRadioGroup.buttonPressed(this, 1, preloadButtons, CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOSTARTPOS, SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
        preload = 0;
        compCheck[1] = true;
        if(compCheck[0])
        {
            button = findViewById(R.id.PreAutoContinueButton);
            button.setEnabled(true);
        }
    }

    private void updateStatusIndicator(int color) {
        ImageView iv = findViewById(R.id.imageView_btIndicator);
        BluetoothComm.updateStatusIndicator(iv, color);
        currentCommStatusColor = color;
    }

    private void openAutoPage() {
        updatePreAutoData();

        Intent intent = new Intent(this, AutoPage.class);
        intent.putExtra("commstatuscolor", currentCommStatusColor);
        startActivity(intent);
    }

    private void previous(){
        updatePreAutoData();
        this.finish();
    }

}
