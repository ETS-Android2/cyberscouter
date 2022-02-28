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
    private int[] redPositionButtons;
    private int[] bluePositionButtons;
    //private final int[] redPositionButtons = {R.id.Ball1, R.id.Ball3, R.id.Ball6};
 //   private final int[] bluePositionButtons = {R.id.Ball2, R.id.Ball4, R.id.Ball5,R.id.button7,R.id.Ball7};
    private int upperGoalCount = 0;
    private int lowerGoalCount = 0;
    private int missedGoalCount = 0;
    private int PickedUpCount = 0;
    private int moveBonus = -1;
    private int blueField = R.drawable.betterbluefield2022;
    private int redField = R.drawable.betterredfield2022;
    private int[] BallsPickedUp= {0,0,0,0,0,0,0};

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
//        button = findViewById(R.id.FlipFieldButton);
//        button.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                FlipField();
//            }
//        });

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
        button = findViewById(R.id.button_BallsMovedMinus);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                BallsMovedMinus();
            }
        });
        button = findViewById(R.id.button_BallsPickedUpPlus);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
             BallsMovedPlus();
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

        button = findViewById(R.id.Ball1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BallPickedUp(0, R.id.Ball1);
            }
        });
        button = findViewById(R.id.Ball2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BallPickedUp(1, R.id.Ball2);
            }
        });
        button = findViewById(R.id.Ball3);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BallPickedUp(2, R.id.Ball3);
            }
        });
        button = findViewById(R.id.Ball4);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BallPickedUp(3, R.id.Ball4);
            }
        });
        button = findViewById(R.id.Ball5);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BallPickedUp(4, R.id.Ball5);
            }
        });
        button = findViewById(R.id.Ball6);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BallPickedUp(5, R.id.Ball6);
            }
        });
        button = findViewById(R.id.Ball7);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BallPickedUp(6, R.id.Ball7);
            }
        });



        iv = findViewById(R.id.imageView_btAutoIndicator);
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

            if(moveBonus != -1) {
                FakeRadioGroup.buttonDisplay(this, moveBonus, moveBonusButtons, SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
                button = findViewById(R.id.button_startMatch);
                button.setEnabled(true);
            } else {
                button = findViewById(R.id.button_startMatch);
                button.setEnabled(false);
            }
            button = findViewById(R.id.upperCounter);
            button.setText(String.valueOf(upperGoalCount));
            button = findViewById(R.id.lowerCounter);
            button.setText(String.valueOf(lowerGoalCount));
            button = findViewById(R.id.missedCounter);
            button.setText(String.valueOf(missedGoalCount));
        }
    }

    public void StartMatch() {
        if(-1 == moveBonus) {
            return;
        }
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
        button = findViewById(R.id.button_startMatch);
        button.setEnabled(true);
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
        ImageView iv = findViewById(R.id.imageView_btAutoIndicator);
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
    } public void BallsMovedMinus() {
        button = findViewById(R.id.PickedUpCounter);
        if (PickedUpCount > 0)
            PickedUpCount--;
        button.setText(String.valueOf(PickedUpCount));
    }
    public void BallsMovedPlus() {
        button = findViewById(R.id.PickedUpCounter);
        PickedUpCount++;
        button.setText(String.valueOf(PickedUpCount));
    }
    public void BallPickedUp(int BallPickedUp, int BTN){
        button = findViewById(BTN);
        if (BallsPickedUp[BallPickedUp]== 0) {
            button.setBackgroundColor(SELECTED_BUTTON_TEXT_COLOR);
            BallsPickedUp[BallPickedUp]=1;
        }
        else {
 //           if ()
           button.setBackgroundColor(defaultButtonTextColor);
           BallsPickedUp[BallPickedUp]=0;

        }

    }

}

