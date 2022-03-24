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
    private int currentCommStatusColor;
    private int preload = -1;

    //used to check if all data fields are completed
    private boolean[] compCheck = {false, false};

    private final CyberScouterDbHelper mDbHelper = new CyberScouterDbHelper(this);
    private SQLiteDatabase _db;

    private String[] _lColumns = {CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOSTARTPOS,
            CyberScouterContract.MatchScouting.COLUMN_NAME_AUTODIDNOTSHOW, CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOPRELOAD};
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

    //moves buttons to lign up with field if it is rotated
    public void moveStartButtons()
    {
        //move button 6
        button = findViewById(R.id.startbutton6);
        button.setX(-750); button.setY(-240);

        //move button 5
        button = findViewById(R.id.startbutton5);
        button.setX(-600); button.setY(-220); button.setRotation(-55);

        //move button 4
        button = findViewById(R.id.startbutton4);
        button.setX(-480);button.setY(-150); button.setRotation(-35);

        //move button 3
        button = findViewById(R.id.startbutton3);
        button.setX(-400); button.setY(28);

        //move button 2
        button = findViewById(R.id.startbutton2);
        button.setX(-400); button.setY(200);

        //move button 1
        button = findViewById(R.id.startbutton1);
        button.setX(-480); button.setY(350);
    }


    @Nullable
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_auto_page);

        _db = mDbHelper.getWritableDatabase();

        CyberScouterConfig cfg = CyberScouterConfig.getConfig(_db);

        ImageView iv = findViewById(R.id.imageView6);

        //if field is red, uses red field image, else will use blue field image
        if(ScoutingPage.getIsRed()){
            iv.setImageResource(R.drawable.betterredfield2022);
        }
        else {
            iv.setImageResource(R.drawable.betterbluefield2022);
        }

        //if the field should be flipped, rotation is set to 180 and buttons are moved
        if (!(ScoutingPage.getIsRed()) && ScoutingPage.getFieldOrientation() == 0 || (ScoutingPage.getIsRed() && ScoutingPage.getFieldOrientation() == 1)) {
            iv.setRotation(iv.getRotation() + 180);
            moveStartButtons();
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
        CyberScouterTeams cst = null;
        if (null != cfg) {
            csm = CyberScouterMatchScouting.getCurrentMatch(_db, TeamMap.getNumberForTeam(cfg.getAlliance_station()));
            cst = CyberScouterTeams.getCurrentTeam(_db, Integer.valueOf(csm.getTeam()));
        }

        if(cst == null) {
            System.out.println("CST IS NULL!!!");
        }

        if (null != csm) {
            TextView tv = findViewById(R.id.textView_preAutoMatch);
            tv.setText(getString(R.string.tagMatch, csm.getMatchNo()));
            tv = findViewById(R.id.textView_preAutoTeam);
            String teamText = null;
            if(cst != null) {
                teamText = csm.getTeam() + " - " + cst.getTeamName();
            } else {
                teamText = csm.getTeam();
            }
            tv.setText(getString(R.string.tagTeam, teamText));

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
            if(preload != -1) {
                FakeRadioGroup.buttonDisplay(this, preload, preloadButtons, SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
                button = findViewById(R.id.PreAutoContinueButton);
                button.setEnabled(true);
                compCheck[1] = true;
            } else {
                button = findViewById(R.id.PreAutoContinueButton);
                button.setEnabled(false);
                compCheck[1] = false;
            }
        }
    }

    @Override
    protected void onDestroy(){
        unregisterReceiver(mOnlineStatusReceiver);
        _db.close();
        mDbHelper.close();
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
            Integer[] _lValues = {_startPos, _didNotShow, preload};
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
        checkEnableContinue();
    }

    private void preloadYes()
    {
        FakeRadioGroup.buttonPressed(this, 0, preloadButtons, CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOSTARTPOS, SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
        compCheck[1] = true;
        preload = 0;
        checkEnableContinue();
    }

    private void preloadNo()
    {
        FakeRadioGroup.buttonPressed(this, 1, preloadButtons, CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOSTARTPOS, SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
        preload = 1;
        compCheck[1] = true;
        checkEnableContinue();
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

    private void checkEnableContinue(){
        boolean b = true;
        for(int i=0; i<compCheck.length; ++i) {
            b = b && compCheck[i];
        }
        button = findViewById(R.id.PreAutoContinueButton);
        if(b) {
            button.setEnabled(true);
        } else {
            button.setEnabled(false);
        }
    }

}
