package com.frcteam195.cyberscouter;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class EndPage extends AppCompatActivity {
    private Button button;
    private int defaultButtonTextColor = Color.LTGRAY;
    private final int SELECTED_BUTTON_TEXT_COLOR = Color.GREEN;
    private final int[] climbPositionButtons = {R.id.button_positionLeft, R.id.button_positionCenter, R.id.button_positionRight};
    private final int[] rungClimbedButtons = {R.id.button_Bar1, R.id.button_Bar2, R.id.button_Bar3, R.id.button_Bar4};
    private final int[] climbStatusButtons = {R.id.button_NABrokeDown, R.id.button_NAPlayedDefense, R.id.button_NAScoredCargo, R.id.button_CAFailed, R.id.button_CASuccess};

    String[] _lColumns = {CyberScouterContract.MatchScouting.COLUMN_NAME_CLIMBSTATUS,
            CyberScouterContract.MatchScouting.COLUMN_NAME_CLIMBHEIGHT,
            CyberScouterContract.MatchScouting.COLUMN_NAME_CLIMBPOSITION
    };

    private int climbPosition = -1, rungClimbed = -1, climbStatus = -1;

    private int currentCommStatusColor;

    private CyberScouterDbHelper mDbHelper = new CyberScouterDbHelper(this);
    private SQLiteDatabase _db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_page);

        mDbHelper = new CyberScouterDbHelper(this);
        _db = mDbHelper.getWritableDatabase();

        Intent intent = getIntent();
        currentCommStatusColor = intent.getIntExtra("commstatuscolor", Color.LTGRAY);
        updateStatusIndicator(currentCommStatusColor);

        button = findViewById(R.id.button_Next);
        button.setEnabled(false);

        button = findViewById(R.id.button_Previous);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToTelePage();
            }
        });

        button = findViewById(R.id.button_Next);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { summaryQuestionsPage(); }
        });

        button = findViewById(R.id.button_positionLeft);
        button.setEnabled(false);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                positionLeft();
            }
        });
        button = findViewById(R.id.button_positionCenter);
        button.setEnabled(false);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                positionCenter();
            }
        });
        button = findViewById(R.id.button_positionRight);
        button.setEnabled(false);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                positionRight();
            }
        });
        button = findViewById(R.id.button_Bar1);
        button.setEnabled(false);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lowClimb();
            }
        });
        button = findViewById(R.id.button_Bar2);
        button.setEnabled(false);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                middleClimb();
            }
        });
        button = findViewById(R.id.button_Bar3);
        button.setEnabled(false);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                highClimb();
            }
        });
        button = findViewById(R.id.button_Bar4);
        button.setEnabled(false);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                traversalClimb();
            }
        });
        button = findViewById(R.id.button_NABrokeDown);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                naBrokeDown();
            }
        });
        button = findViewById(R.id.button_NAPlayedDefense);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                naPlayedDefense();
            }
        });
        button = findViewById(R.id.button_NAScoredCargo);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                naScoredCargo();
            }
        });
        button = findViewById(R.id.button_CAFailed);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                caFailed();
            }
        });
        button = findViewById(R.id.button_CASuccess);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                caSuccess();
            }
        });
        button = findViewById(R.id.button_NADidNothing);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                naDidNothing();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        _db = mDbHelper.getWritableDatabase();
        CyberScouterConfig cfg = CyberScouterConfig.getConfig(_db);
        CyberScouterMatchScouting csm = CyberScouterMatchScouting.getCurrentMatch(_db, TeamMap.getNumberForTeam(cfg.getAlliance_station()));

        if (null != csm) {
            TextView tv = findViewById(R.id.textView_endgameMatch);
            tv.setText(getString(R.string.tagMatch, csm.getMatchNo()));
            tv = findViewById(R.id.textView_endgameTeam);
            tv.setText(getString(R.string.tagTeam, csm.getTeam()));


            climbPosition = csm.getClimbPosition();
            if(climbPosition != -1) {
                FakeRadioGroup.buttonPressed(this, climbPosition, climbPositionButtons, CyberScouterContract.MatchScouting.COLUMN_NAME_CLIMBPOSITION, SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
            }
            rungClimbed = csm.getClimbHeight();
            if(rungClimbed != -1) {
                FakeRadioGroup.buttonPressed(this, 0,rungClimbedButtons, CyberScouterContract.MatchScouting.COLUMN_NAME_CLIMBHEIGHT, SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
            }
            climbStatus = csm.getClimbStatus();
            if(climbStatus != -1) {
                FakeRadioGroup.buttonPressed(this, climbStatus, climbStatusButtons, CyberScouterContract.MatchScouting.COLUMN_NAME_CLIMBSTATUS, SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
            }

            EnableNext();
            EnableClimb();
        }
    }

    @Override
    protected void onDestroy(){
        _db.close();
        mDbHelper.close();
        super.onDestroy();
    }


    public void returnToTelePage(){
        updateEndPageData();
        this.finish();
    }

    public void summaryQuestionsPage(){
        updateEndPageData();
        Intent intent = new Intent(this, SummaryQuestionsPage.class);
        intent.putExtra("commstatuscolor", currentCommStatusColor);
        startActivity(intent);

    }

    public void positionLeft() {
        FakeRadioGroup.buttonPressed(this, 0, climbPositionButtons, CyberScouterContract.MatchScouting.COLUMN_NAME_CLIMBPOSITION, SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
        climbPosition = 0;
        EnableNext();
    }
    public void positionCenter() {
        FakeRadioGroup.buttonPressed(this, 1, climbPositionButtons, CyberScouterContract.MatchScouting.COLUMN_NAME_CLIMBPOSITION, SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
        climbPosition = 1;
        EnableNext();
    }
    public void positionRight() {
        FakeRadioGroup.buttonPressed(this, 2, climbPositionButtons, CyberScouterContract.MatchScouting.COLUMN_NAME_CLIMBPOSITION, SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
        climbPosition = 2;
        EnableNext();
    }
    public void naBrokeDown() {
        FakeRadioGroup.buttonPressed(this, 0, climbStatusButtons, CyberScouterContract.MatchScouting.COLUMN_NAME_CLIMBSTATUS, SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
        climbStatus = 0;
        EnableClimb();
        EnableNext();
    }
    public void naPlayedDefense() {
        FakeRadioGroup.buttonPressed(this, 1, climbStatusButtons, CyberScouterContract.MatchScouting.COLUMN_NAME_CLIMBSTATUS, SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
        climbStatus = 1;
        EnableClimb();
        EnableNext();
    }
    public void naScoredCargo() {
        FakeRadioGroup.buttonPressed(this, 2, climbStatusButtons, CyberScouterContract.MatchScouting.COLUMN_NAME_CLIMBSTATUS, SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
        climbStatus = 2;
        EnableClimb();
        EnableNext();
    }
    public void caFailed() {
        FakeRadioGroup.buttonPressed(this, 3, climbStatusButtons, CyberScouterContract.MatchScouting.COLUMN_NAME_CLIMBSTATUS, SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
        climbStatus = 3;
        EnableClimb();
        EnableNext();
    }
    public void caSuccess() {
        FakeRadioGroup.buttonPressed(this, 4, climbStatusButtons, CyberScouterContract.MatchScouting.COLUMN_NAME_CLIMBSTATUS, SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
        climbStatus = 4;
        EnableClimb();
        EnableNext();
    }
    public void naDidNothing() {
        FakeRadioGroup.buttonPressed(this, 0, climbStatusButtons, CyberScouterContract.MatchScouting.COLUMN_NAME_CLIMBSTATUS, SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
        EnableClimb();
        EnableNext();
    }
    public void lowClimb() {
        FakeRadioGroup.buttonPressed(this, 0,rungClimbedButtons, CyberScouterContract.MatchScouting.COLUMN_NAME_CLIMBHEIGHT, SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
        rungClimbed = 0;
        EnableNext();
    }
    public void middleClimb() {
        FakeRadioGroup.buttonPressed(this, 1,rungClimbedButtons, CyberScouterContract.MatchScouting.COLUMN_NAME_CLIMBHEIGHT, SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
        rungClimbed = 1;
        EnableNext();
    }
    public void highClimb() {
        FakeRadioGroup.buttonPressed(this, 2,rungClimbedButtons, CyberScouterContract.MatchScouting.COLUMN_NAME_CLIMBHEIGHT, SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
        rungClimbed = 2;
        EnableNext();
    }
    public void traversalClimb() {
        FakeRadioGroup.buttonPressed(this, 3, rungClimbedButtons, CyberScouterContract.MatchScouting.COLUMN_NAME_CLIMBHEIGHT, SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
        rungClimbed = 3;
        EnableNext();
    }

    private void EnableNext() {
        if( climbStatus == -1) {
            button = findViewById(R.id.button_Next);
            button.setEnabled(false);
        } else if (climbStatus == 4) {
            button = findViewById(R.id.button_Next);
            button.setEnabled(false);
            if (climbPosition != -1 && rungClimbed != -1) {
                button = findViewById(R.id.button_Next);
                button.setEnabled(true);
            }
        }
        else {
            button = findViewById(R.id.button_Next);
            button.setEnabled(true);
        }
    }

    private void EnableClimb() {
        if (climbStatus == 4) {
            button = findViewById(R.id.button_Bar1);
            button.setEnabled(true);
            button = findViewById(R.id.button_Bar2);
            button.setEnabled(true);
            button = findViewById(R.id.button_Bar3);
            button.setEnabled(true);
            button = findViewById(R.id.button_Bar4);
            button.setEnabled(true);
            button = findViewById(R.id.button_positionLeft);
            button.setEnabled(true);
            button = findViewById(R.id.button_positionCenter);
            button.setEnabled(true);
            button = findViewById(R.id.button_positionRight);
            button.setEnabled(true);
        }
        if (climbStatus != 4) {
            button = findViewById(R.id.button_Bar1);
            button.setEnabled(false);
            button = findViewById(R.id.button_Bar2);
            button.setEnabled(false);
            button = findViewById(R.id.button_Bar3);
            button.setEnabled(false);
            button = findViewById(R.id.button_Bar4);
            button.setEnabled(false);
            button = findViewById(R.id.button_positionLeft);
            button.setEnabled(false);
            button = findViewById(R.id.button_positionCenter);
            button.setEnabled(false);
            button = findViewById(R.id.button_positionRight);
            button.setEnabled(false);
            rungClimbed = -1;
            climbPosition = -1;
        }
    }


    private void updateStatusIndicator(int color) {
        ImageView iv = findViewById(R.id.imageView_btEndIndicator);
        BluetoothComm.updateStatusIndicator(iv, color);
    }

    private void updateEndPageData() {
        CyberScouterConfig cfg = CyberScouterConfig.getConfig(_db);
        try {
            Integer[] _lValues = {climbStatus, rungClimbed, climbPosition};
            CyberScouterMatchScouting.updateMatchMetric(_db, _lColumns, _lValues, cfg);
        }
        catch(Exception e) {
            e.printStackTrace();
            MessageBox.showMessageBox(this, "Update Error",
                    "EndPage.updateEndPageData", "SQLite update failed!\n "+e.getMessage());
        }
    }
}