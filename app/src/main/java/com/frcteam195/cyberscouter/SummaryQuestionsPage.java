package com.frcteam195.cyberscouter;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class SummaryQuestionsPage extends AppCompatActivity {
    private Button button;
    /*private RadioButton rb;
    private RadioGroup rg;*/
    private final int[] groundPickupArray = {R.id.GroundPickupY, R.id.GroundPickupN};
    private final int[] terminalPickupArray = {R.id.TerminalPickupY, R.id.TerminalPickupN};
    private final int[] playedDefenseArray = {R.id.PlayedDefenseY, R.id.PlayedDefenseN};
    private final int[] defenseAgainstArray = {R.id.DefenseAgainstThemY, R.id.DefenseAgainstThemN};
    private final int[] shootWhileArray = {R.id.ShootWhileY, R.id.ShootWhileN};
    private final int[] brokeDownArray = {R.id.BrokeDownY, R.id.BrokeDownN};
    private final int[] lostCommArray = {R.id.LostCommY, R.id.LostCommN};
    private final int[] subsystemBrokeArray = {R.id.SubsystemY, R.id.SubsystemN};
    private final int[] scoreOppArray = {R.id.ScoreOppY, R.id.ScoreOppN};
    private final int[] shootFromArray = {R.id.ShootFromY, R.id.ShootFromN};
    private View _view;
    private int defaultButtonBackgroundColor = Color.LTGRAY;
    private int defaultButtonTextColor = Color.BLACK;
    private final int SELECTED_BUTTON_TEXT_COLOR = Color.GREEN;
    private int[] ButtonArray = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    private int groundPickupVar, terminalPickupVar, playedDefenseVar, defenseAgainstVar, shootWhileVar, brokeDownVar, lostCommVar, subsystemBrokeVar, scoreOppVar, shootFromVar;
    //private int lastCheckedButton;

        String[] _lColumns = {CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMLAUNCHPAD,
                CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMSORTCARGO,
                CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMSHOOTDRIVING,
                CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMBROKEDOWN,
                CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMLOSTCOMM,
                CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMSUBSYSTEMBROKE,
                CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMGROUNDPICKUP,
                CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMTERMINALPICKUP,
                CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMPLAYEDDEFENSE,
                CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMDEFPLAYEDAGAINST,

        };

    private int currentCommStatusColor;


    private CyberScouterDbHelper mDbHelper = new CyberScouterDbHelper(this);
    private SQLiteDatabase _db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary_questions_page);


        Intent intent = getIntent();
        currentCommStatusColor = intent.getIntExtra("commstatuscolor", Color.LTGRAY);
        updateStatusIndicator(currentCommStatusColor);

        button = findViewById(R.id.button_sumqPrevious);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToEndGamePage();
            }
        });

        button = findViewById(R.id.button_sumqNext);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSubmitPage();

            }
        });

        button = findViewById(R.id.GroundPickupY);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        button = findViewById(R.id.GroundPickupN);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        button = findViewById(R.id.TerminalPickupY);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //nextAnswer();

            }
        });

        button = findViewById(R.id.TerminalPickupN);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //previousAnswer();

            }
        });
        button = findViewById(R.id.PlayedDefenseY);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        button = findViewById(R.id.PlayedDefenseN);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        button = findViewById(R.id.ShootWhileY);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        button = findViewById(R.id.ShootWhileN);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        button = findViewById(R.id.BrokeDownY);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        button = findViewById(R.id.BrokeDownN);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        button = findViewById(R.id.LostCommY);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        button = findViewById(R.id.LostCommN);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        button = findViewById(R.id.SubsystemY);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        button = findViewById(R.id.SubsystemN);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        button = findViewById(R.id.ScoreOppY);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        button = findViewById(R.id.ScoreOppN);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        button = findViewById(R.id.ShootFromY);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        button = findViewById(R.id.BrokeDownN);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
/*
        rb = findViewById(R.id.radioButton1);
        rb.setVisibility(View.GONE);
        rb.setEnabled(false);
        rb = findViewById(R.id.radioButton2);
        rb.setVisibility(View.GONE);
        rb.setEnabled(false);
        rb = findViewById(R.id.radioButton3);
        rb.setVisibility(View.GONE);
        rb.setEnabled(false);
        rb = findViewById(R.id.radioButton4);
        rb.setVisibility(View.GONE);
        rb.setEnabled(false);
        rb = findViewById(R.id.radioButton5);
        rb.setVisibility(View.GONE);
        rb.setEnabled(false);
*/
        CyberScouterDbHelper mDbHelper = new CyberScouterDbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        CyberScouterConfig cfg = CyberScouterConfig.getConfig(db);

        if (null != cfg) {

            CyberScouterMatchScouting csm = CyberScouterMatchScouting.getCurrentMatch(db, TeamMap.getNumberForTeam(cfg.getAlliance_station()));

            if (null != csm) {
                TextView tv = findViewById(R.id.textView_endMatch);
                tv.setText(getString(R.string.tagMatch, csm.getTeamMatchNo()));
                tv = findViewById(R.id.textView_endTeamNumber);
                tv.setText(getString(R.string.tagTeam, csm.getTeam()));
            }
            ButtonArray[0] = csm.getAutoPreload();
            ButtonArray[1] = csm.getAutoPreload();
            ButtonArray[2] = csm.getAutoPreload();
            ButtonArray[3] = csm.getAutoPreload();
            ButtonArray[4] = csm.getAutoPreload();
            ButtonArray[5] = csm.getAutoPreload();
            ButtonArray[6] = csm.getAutoPreload();
            ButtonArray[7] = csm.getAutoPreload();
            ButtonArray[8] = csm.getAutoPreload();
            ButtonArray[9] = csm.getAutoPreload();

            FakeRadioGroup.buttonDisplay(this, ButtonArray[0], groundPickupArray, SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);

        }

    }
    /*private void GroundPickupNo()
    {
        FakeRadioGroup.buttonPressed(this, 1, preloadButtons, CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOSTARTPOS, SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
        preload = 0;
        compCheck[1] = true;
        if(compCheck[0])
        {
            button = findViewById(R.id.PreAutoContinueButton);
            button.setEnabled(true);
        }
    }*/

    public void returnToEndGamePage() {
        // updateAnswer();
        this.finish();
    }

    public void openSubmitPage() {
        //updateAnswer();
        Intent intent = new Intent(this, SubmitPage.class);
        intent.putExtra("commstatuscolor", currentCommStatusColor);
        startActivity(intent);
    }

    public void nextAnswer() {
        // Update the Match Scouting record
        //updateAnswer();

        // Get the next question, if any
        setNextQuestion(1);
        this.onResume();
    }

    public void previousAnswer() {
        //updateAnswer();

        setNextQuestion(-1);
        this.onResume();
    }

    private void setNextQuestion(int val) {
        CyberScouterDbHelper mDbHelper = new CyberScouterDbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

    }

    /*
        private void updateAnswer() {
            CyberScouterDbHelper mDbHelper = new CyberScouterDbHelper(this);
            SQLiteDatabase db = mDbHelper.getWritableDatabase();

            CyberScouterConfig cfg = CyberScouterConfig.getConfig(db);

            if (null != cfg) {

                rg = findViewById(R.id.radioGroup1);
                int rbid = rg.getCheckedRadioButtonId();

                int ans;
                switch (rbid) {
                    case (R.id.radioButton1):
                        ans = 0;
                        break;
                    case (R.id.radioButton2):
                        ans = 1;
                        break;
                    case (R.id.radioButton3):
                        ans = 2;
                        break;
                    case (R.id.radioButton4):
                        ans = 3;
                        break;
                    case (R.id.radioButton5):
                        ans = 4;
                        break;
                    default:
                        ans = -1;
                }

            }

        }

        private void rbClicked(View v) {
            rg = findViewById(R.id.radioGroup1);
            int currentCheckedButton = rg.getCheckedRadioButtonId();
            if(lastCheckedButton == currentCheckedButton) {
                rg.clearCheck();
                updateAnswer();
            } else {
                rg.check(v.getId());
                updateAnswer();
            }
            this.onResume();
        }

    }*/
    private void updateStatusIndicator(int color) {
        ImageView iv = findViewById(R.id.imageView_btEndIndicator);
        BluetoothComm.updateStatusIndicator(iv, color);
    }
    private void updateSummaryQuestionPageData() {
        CyberScouterConfig cfg = CyberScouterConfig.getConfig(_db);
        try {
            Integer[] _lValues = {groundPickupVar, terminalPickupVar, playedDefenseVar, defenseAgainstVar, shootWhileVar, brokeDownVar, lostCommVar, subsystemBrokeVar, scoreOppVar, shootFromVar};
            CyberScouterMatchScouting.updateMatchMetric(_db, _lColumns, _lValues, cfg);
        } catch(Exception e) {
            e.printStackTrace();
            MessageBox.showMessageBox(this, "Update Error",
                    "EndPage.updateEndPageData", "SQLite update failed!\n "+e.getMessage());
        }
    }
}
