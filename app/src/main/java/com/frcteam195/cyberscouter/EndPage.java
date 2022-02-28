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
    private final int[] rungClimbedButtons = {R.id.button_Low, R.id.button_Middle, R.id.button_High, R.id.button_Traversal};
    private final int[] climbStatusButtons = {R.id.button_NABrokeDown, R.id.button_NAPlayedDefense, R.id.button_NAScoredCargo, R.id.button_CAFailed, R.id.button_CASuccess};
    private final int[] insteadOfClimbButtons = {R.id.button_PlayedDefense, R.id.button_ScoredCargo, R.id.button_BrokeDown, R.id.button_ClimbFailed};

    String[] _lColumns = {CyberScouterContract.MatchScouting.COLUMN_NAME_CLIMBSTATUS,
            CyberScouterContract.MatchScouting.COLUMN_NAME_CLIMBHEIGHT,
//            CyberScouterContract.MatchScouting.COLUMN_NAME_INSTEADOFCLIMB,
            CyberScouterContract.MatchScouting.COLUMN_NAME_CLIMBPOSITION
    };

    private int climbPosition, rungClimbed, climbStatus, insteadOfClimb;

    private int currentCommStatusColor;

    private CyberScouterDbHelper mDbHelper = new CyberScouterDbHelper(this);
    private SQLiteDatabase _db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_page);

        Intent intent = getIntent();
        currentCommStatusColor = intent.getIntExtra("commstatuscolor", Color.LTGRAY);
        updateStatusIndicator(currentCommStatusColor);

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
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                positionLeft();
            }
        });
        button = findViewById(R.id.button_positionCenter);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                positionCenter();
            }
        });
        button = findViewById(R.id.button_positionRight);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                positionRight();
            }
        });
        button = findViewById(R.id.button_Low);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lowClimb();
            }
        });
        button = findViewById(R.id.button_Middle);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                middleClimb();
            }
        });
        button = findViewById(R.id.button_High);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                highClimb();
            }
        });
        button = findViewById(R.id.button_Traversal);
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
        button = findViewById(R.id.button_PlayedDefense);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playedDefense();
            }
        });
        button = findViewById(R.id.button_ScoredCargo);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scoredCargo();
            }
        });
        button = findViewById(R.id.button_BrokeDown);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                brokeDown();
            }
        });
        button = findViewById(R.id.button_ClimbFailed);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                climbFailed();
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
            rungClimbed = csm.getClimbHeight();
            climbStatus = csm.getClimbStatus();
            insteadOfClimb = csm.getInsteadOfClimb();

            int tval = csm.getClimbStatus() == 0 ? 0 : csm.getClimbStatus() - 1;
            FakeRadioGroup.buttonDisplay(this, tval, climbStatusButtons, SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
            climbPosition = csm.getClimbPosition();
            FakeRadioGroup.buttonDisplay(this, tval, rungClimbedButtons, SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
            rungClimbed = csm.getClimbHeight();
            FakeRadioGroup.buttonDisplay(this, tval, climbStatusButtons, SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
            climbStatus = csm.getClimbStatus();
            FakeRadioGroup.buttonDisplay(this, tval, insteadOfClimbButtons, SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
            insteadOfClimb = csm.getInsteadOfClimb();
        }
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
    }
    public void positionCenter() {
        FakeRadioGroup.buttonPressed(this, 1, climbPositionButtons, CyberScouterContract.MatchScouting.COLUMN_NAME_CLIMBPOSITION, SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
        climbPosition = 1;
    }
    public void positionRight() {
        FakeRadioGroup.buttonPressed(this, 2, climbPositionButtons, CyberScouterContract.MatchScouting.COLUMN_NAME_CLIMBPOSITION, SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
        climbPosition = 2;
    }
    public void naBrokeDown() {
        FakeRadioGroup.buttonPressed(this, 0, climbStatusButtons, CyberScouterContract.MatchScouting.COLUMN_NAME_CLIMBSTATUS, SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
        climbStatus = 0;
    }
    public void naPlayedDefense() {
        FakeRadioGroup.buttonPressed(this, 1, climbStatusButtons, CyberScouterContract.MatchScouting.COLUMN_NAME_CLIMBSTATUS, SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
        climbStatus = 1;
    }
    public void naScoredCargo() {
        FakeRadioGroup.buttonPressed(this, 2, climbStatusButtons, CyberScouterContract.MatchScouting.COLUMN_NAME_CLIMBSTATUS, SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
        climbStatus = 2;
    }
    public void caFailed() {
        FakeRadioGroup.buttonPressed(this, 3, climbStatusButtons, CyberScouterContract.MatchScouting.COLUMN_NAME_CLIMBSTATUS, SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
        climbStatus = 3;
    }
    public void caSuccess() {
        FakeRadioGroup.buttonPressed(this, 4, climbStatusButtons, CyberScouterContract.MatchScouting.COLUMN_NAME_CLIMBSTATUS, SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
        climbStatus = 4;
    }
    public void lowClimb() {
        FakeRadioGroup.buttonPressed(this, 0,rungClimbedButtons, CyberScouterContract.MatchScouting.COLUMN_NAME_CLIMBHEIGHT, SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
        rungClimbed = 0;
    }
    public void middleClimb() {
        FakeRadioGroup.buttonPressed(this, 1,rungClimbedButtons, CyberScouterContract.MatchScouting.COLUMN_NAME_CLIMBHEIGHT, SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
        rungClimbed = 1;
    }
    public void highClimb() {
        FakeRadioGroup.buttonPressed(this, 2,rungClimbedButtons, CyberScouterContract.MatchScouting.COLUMN_NAME_CLIMBHEIGHT, SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
        rungClimbed = 2;
    }
    public void traversalClimb() {
        FakeRadioGroup.buttonPressed(this, 3, rungClimbedButtons, CyberScouterContract.MatchScouting.COLUMN_NAME_CLIMBHEIGHT, SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
        rungClimbed = 3;
    }
    public void playedDefense() {
//        FakeRadioGroup.buttonPressed(this, 0, insteadOfClimbButtons, CyberScouterContract.MatchScouting.COLUMN_NAME_INSTEADOFCLIMB, SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
        insteadOfClimb = 0;
    }
    public void scoredCargo() {
//        FakeRadioGroup.buttonPressed(this, 1, insteadOfClimbButtons, CyberScouterContract.MatchScouting.COLUMN_NAME_INSTEADOFCLIMB, SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
        insteadOfClimb = 1;
    }
    public void brokeDown() {
//        FakeRadioGroup.buttonPressed(this, 2, insteadOfClimbButtons, CyberScouterContract.MatchScouting.COLUMN_NAME_INSTEADOFCLIMB, SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
        insteadOfClimb = 2;
    }
    public void climbFailed() {
//        FakeRadioGroup.buttonPressed(this, 3, insteadOfClimbButtons, CyberScouterContract.MatchScouting.COLUMN_NAME_INSTEADOFCLIMB, SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
        insteadOfClimb = 3;
    }

    private void updateStatusIndicator(int color) {
        ImageView iv = findViewById(R.id.imageView_btEndIndicator);
        BluetoothComm.updateStatusIndicator(iv, color);
    }

    private void updateEndPageData() {
        CyberScouterConfig cfg = CyberScouterConfig.getConfig(_db);
        try {
//            Integer[] _lValues = {climbPosition, rungClimbed, climbStatus, insteadOfClimb};
            Integer[] _lValues = {climbStatus, rungClimbed, climbPosition};
            CyberScouterMatchScouting.updateMatchMetric(_db, _lColumns, _lValues, cfg);
        } catch(Exception e) {
            e.printStackTrace();
            MessageBox.showMessageBox(this, "Update Error",
                    "EndPage.updateEndPageData", "SQLite update failed!\n "+e.getMessage());
        }
    }
}