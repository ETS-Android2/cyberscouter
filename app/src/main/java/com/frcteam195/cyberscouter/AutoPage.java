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

public class AutoPage extends AppCompatActivity {
    private Button button;
    private final int defaultButtonTextColor = Color.LTGRAY;
    private final int SELECTED_BUTTON_TEXT_COLOR = Color.GREEN;
    private final int[] moveBonusButtons = {R.id.button_didNotMove, R.id.button_attempted, R.id.button_moveBonusYes};
    private final int[] redPositionButtons = {R.id.Button1, R.id.button3, R.id.button6};
    private final int[] bluePositionButtons = {R.id.Button2, R.id.button4, R.id.button5,R.id.button7,R.id.button8, R.id.button9,R.id.button10};
    private int upperGoalCount = 0;
    private int lowerGoalCount = 0;
    private int missedGoalCount = 0;
    private int moveBonus = 9;
    private int blueField = R.drawable.betterbluefield2022;
    private int redField = R.drawable.betterredfield2022;

    private int field_orientation;
    private int currentCommStatusColor;
    private final CyberScouterDbHelper mDbHelper = new CyberScouterDbHelper(this);
    private SQLiteDatabase _db;

    String[] _lColumns = {CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOMOVEBONUS,
        CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLMISS,
        CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLHIGH,
        CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLLOW};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_page);

        button = findViewById(R.id.button_startMatch);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                StartMatch();
            }
        });

        ImageView iv = findViewById(R.id.imageView5);
        if (!(ScoutingPage.getIsRed()) && ScoutingPage.getFieldOrientation() == 0 || (ScoutingPage.getIsRed() && ScoutingPage.getFieldOrientation() == 1)) {
            iv.setRotation(iv.getRotation() + 180);
        }
        button = findViewById(R.id.FlipFieldButton);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FlipField();
            }
        });

        button = findViewById(R.id.buttonPrevious);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ReturnToScoutingPage();
            }
        });

        button = findViewById(R.id.button_moveBonusYes);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                moveBonus(2);
            }
        });

        button = findViewById(R.id.button_didNotMove);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                moveBonus(0);
            }
        });

        button = findViewById(R.id.button_attempted);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                moveBonus(1);
            }
        });

        button = findViewById(R.id.button_upperGoalMinus);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                upperGoalMinus();
            }
        });

        button = findViewById(R.id.button_upperGoalPlus);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                upperGoalPlus();
            }
        });

        button = findViewById(R.id.button_LowerGoalMinus);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                lowerGoalMinus();
            }
        });

        button = findViewById(R.id.button_LowerGoalPlus);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                lowerGoalPlus();
            }
        });

        button = findViewById(R.id.button_missedGoalMinus);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                missedGoalMinus();
            }
        });

        button = findViewById(R.id.button_missedGoalPlus);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                missedGoalPlus();
            }
        });

         iv = findViewById(R.id.imageView_teleBtIndicator);
        Intent intent = getIntent();
        currentCommStatusColor = intent.getIntExtra("commstatuscolor", Color.LTGRAY);
        updateStatusIndicator(currentCommStatusColor);
    }

    @Override
    protected void onResume() {
        super.onResume();

        _db = mDbHelper.getWritableDatabase();
        CyberScouterConfig cfg = CyberScouterConfig.getConfig(_db);

        CyberScouterMatchScouting csm = CyberScouterMatchScouting.getCurrentMatch(_db, TeamMap.getNumberForTeam(cfg.getAlliance_station()));

        if (null != csm) {
            TextView tv = findViewById(R.id.textView_autoMatch);
            tv.setText(getString(R.string.tagMatch, csm.getMatchNo()));
            tv = findViewById(R.id.textView_autoTeam);
            tv.setText(getString(R.string.tagTeam, csm.getTeam()));

            moveBonus = csm.getAutoMoveBonus();
            upperGoalCount = csm.getAutoBallHigh();
            lowerGoalCount = csm.getAutoBallLow();
            missedGoalCount = csm.getAutoBallMiss();

            FakeRadioGroup.buttonDisplay(this, moveBonus, moveBonusButtons, SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
            button = findViewById(R.id.upperCounter);
            button.setText(String.valueOf(upperGoalCount));
            button = findViewById(R.id.lowerCounter);
            button.setText(String.valueOf(lowerGoalCount));
            button = findViewById(R.id.missedCounter);
            button.setText(String.valueOf(missedGoalCount));
        }
    }

    public void StartMatch() {
//        if(9 == moveBonus) {
//            return;
//        }
        updateAutoData();
        Intent intent = new Intent(this, TelePage.class);
        intent.putExtra("commstatuscolor", currentCommStatusColor);
        startActivity(intent);
    }


    public void ReturnToScoutingPage() {
        updateAutoData();
        this.finish();
    }



    public void skipMatch() {
        CyberScouterDbHelper mDbHelper = new CyberScouterDbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        CyberScouterConfig cfg = CyberScouterConfig.getConfig(db);

        CyberScouterMatchScouting csm = CyberScouterMatchScouting.getCurrentMatch(db, TeamMap.getNumberForTeam(cfg.getAlliance_station()));

        if (null != csm) {
            try {
                CyberScouterMatchScouting.skipMatch(db, csm.getMatchScoutingID());
                this.onResume();
            } catch (Exception e) {
                MessageBox.showMessageBox(this, "Skip Match Failed Alert", "skipMatch",
                        "Update of UploadStatus failed!\n\n" +
                                "The error is:\n" + e.getMessage());
            }
        }
    }


    public void moveBonus(Integer val) {
        FakeRadioGroup.buttonPressed(this, val, moveBonusButtons,
                CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOMOVEBONUS,
                SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
        moveBonus =  val;
    }

    public void upperGoalMinus() {
        button = findViewById(R.id.upperCounter);
        if (upperGoalCount > 0)
            upperGoalCount--;
        button.setText(String.valueOf(upperGoalCount));
    }
    public void FlipField() {
       ImageView iv = findViewById(R.id.imageView5);
     //   if(iv.image)
       iv.setImageResource(R.drawable.betterredfield2022);
    }
    public void upperGoalPlus() {
        button = findViewById(R.id.upperCounter);
        upperGoalCount++;
        button.setText(String.valueOf(upperGoalCount));

    }
    public void lowerGoalMinus() {
        button = findViewById(R.id.lowerCounter);
        if (lowerGoalCount > 0)
            lowerGoalCount--;
        button.setText(String.valueOf(lowerGoalCount));
    }
    public void lowerGoalPlus() {
        button = findViewById(R.id.lowerCounter);
        lowerGoalCount++;
        button.setText(String.valueOf(lowerGoalCount));
    }
    public void missedGoalMinus() {
        button = findViewById(R.id.missedCounter);
        if (missedGoalCount > 0)
            missedGoalCount--;
        button.setText(String.valueOf(missedGoalCount));
    }
    public void missedGoalPlus() {
        button = findViewById(R.id.missedCounter);
        missedGoalCount++;
        button.setText(String.valueOf(missedGoalCount));
    }

    private void updateStatusIndicator(int color) {
        ImageView iv = findViewById(R.id.imageView_btIndicator);
        BluetoothComm.updateStatusIndicator(iv, color);
    }
    public void parkedClimb() {
 //       FakeRadioGroup.buttonPressed(this, 4, climbStatusButtons, CyberScouterContract.MatchScouting.COLUMN_NAME_CLIMBSTATUS, SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
 //       climbStatus = 5;
    }

    private void updateAutoData() {
        CyberScouterConfig cfg = CyberScouterConfig.getConfig(_db);
        try {
            Integer[] _lValues = {moveBonus, missedGoalCount, upperGoalCount, lowerGoalCount};
            CyberScouterMatchScouting.updateMatchMetric(_db, _lColumns, _lValues, cfg);
        } catch(Exception e) {
            e.printStackTrace();
            MessageBox.showMessageBox(this, "Update Error",
                    "AutoPage.updateAutoData", "SQLite update failed!\n "+e.getMessage());
        }
    }
}

