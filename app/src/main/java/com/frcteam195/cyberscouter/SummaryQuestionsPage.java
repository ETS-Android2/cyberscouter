package com.frcteam195.cyberscouter;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;


public class SummaryQuestionsPage extends AppCompatActivity {
    private Button button;
    private final int[] playedDefenseArray = {R.id.PlayedDefenseN, R.id.PlayedDefenseY};
    private final int[] defenseAgainstArray = {R.id.DefenseAgainstThemN, R.id.DefenseAgainstThemY};
    private final int[] brokeDownArray = {R.id.BrokeDownN, R.id.BrokeDownY};
    private final int[] lostCommArray = {R.id.LostCommN, R.id.LostCommY};
    private final int[] subsystemBrokeArray = {R.id.SubsystemN, R.id.SubsystemY};
    private final int[] launchPadArray = {R.id.ShootFromN, R.id.ShootFromY};
    /*private final String[] RatingOptions = {""}
    private Spinner rating = (Spinner) findViewById(R.id.spinner_Rating);
    private Spinner shootFrom = (Spinner) findViewById(R.id.spinner_ShootFrom);*/
    private final String[] spinnerOptions = {"1", "2", "3", "4", "5"};
    private final String[] shootFromOptions = {"Did not shoot", "from hub", "from radius", "alligned", "any orientation"};
    private int defaultButtonBackgroundColor = Color.LTGRAY;
    private int defaultButtonTextColor = Color.LTGRAY;
    private final int SELECTED_BUTTON_TEXT_COLOR = Color.GREEN;

    /*private ArrayAdapter<String> pp = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, rating);
    private ArrayAdapter<String> pd = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, shootFrom);*/


    private int[] qAnswered = {-1, -1, -1, -1, -1, -1, -1, -1, -1};

    private SQLiteDatabase _db;


    private int playedDefenseVar = -1,
            defenseAgainstVar = -1, brokeDownVar = -1, lostCommVar = -1,
            subsystemBrokeVar = -1, launchPadVar = -1, ratingVar = -1,
            shootFromVar = -1, speedVar = -1, howGoodVar = -1;
    //private int lastCheckedButton;

    String[] _lColumns = {
            CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMPLAYEDDEFENSE,
            CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMDEFPLAYEDAGAINST,
            CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMBROKEDOWN,
            CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMLOSTCOMM,
            CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMSUBSYSTEMBROKE,
            CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMLAUNCHPAD,
            CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMRATING,
            CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMSHOOTFROM,
            CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMSPEED,
            CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMMANUVERABILITY
    };

    private int currentCommStatusColor;


    private CyberScouterDbHelper mDbHelper = new CyberScouterDbHelper(this);

    public SummaryQuestionsPage() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary_questions_page);

        _db = mDbHelper.getWritableDatabase();

        CyberScouterConfig cfg = CyberScouterConfig.getConfig(_db);

        Intent intent = getIntent();
        currentCommStatusColor = intent.getIntExtra("commstatuscolor", Color.LTGRAY);
        updateStatusIndicator(currentCommStatusColor);

        CyberScouterDbHelper mDbHelper = new CyberScouterDbHelper(this);
        _db = mDbHelper.getWritableDatabase();

        Spinner speed = findViewById(R.id.spinner_Speed);
        speed.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, spinnerOptions));
        speed.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                speedVar = i;
                qAnswered[6] = 0;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView){}
        });

        Spinner shoot = findViewById(R.id.spinner_ShootFrom);
        shoot.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, shootFromOptions));
        shoot.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                shootFromVar = i;
                qAnswered[7] = 0;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView){}
        });

        Spinner ability = findViewById(R.id.spinner_Ability);
        ability.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, spinnerOptions));
        ability.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                howGoodVar = i;
                qAnswered[8] = 0;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView){}
        });

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
        button.setEnabled(false);

        button = findViewById(R.id.PlayedDefenseY);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlayedDefense(1);
            }
        });
        button = findViewById(R.id.PlayedDefenseN);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlayedDefense(0);
            }
        });
        button = findViewById(R.id.DefenseAgainstThemY);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DefenseAgainst(1);
            }
        });
        button = findViewById(R.id.DefenseAgainstThemN);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DefenseAgainst(0);
            }
        });
        button = findViewById(R.id.BrokeDownY);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BrokeDown(1);
            }
        });
        button = findViewById(R.id.BrokeDownN);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BrokeDown(0);
            }
        });
        button = findViewById(R.id.LostCommY);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LostComm(1);
            }
        });
        button = findViewById(R.id.LostCommN);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LostComm(0);
            }
        });
        button = findViewById(R.id.SubsystemY);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SubsystemBroke(1);
            }
        });
        button = findViewById(R.id.SubsystemN);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SubsystemBroke(0);
            }
        });
        button = findViewById(R.id.ShootFromY);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShootFrom(1);
            }
        });
        button = findViewById(R.id.ShootFromN);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShootFrom(0);
            }
        });

        button = findViewById(R.id.button_sumqNext);
        button.setEnabled(false);
        //if (groundPickupVar == 0, 1 != 1 && terminalPickupVar, playedDefenseVar, defenseAgainstVar, shootWhileVar, brokeDownVar, lostCommVar, subsystemBrokeVar, scoreOppVar, shootFromVar)
    }

    @Override
    protected void onResume() {
        super.onResume();

        CyberScouterConfig cfg = CyberScouterConfig.getConfig(_db);

        if (null != cfg) {

            CyberScouterMatchScouting csm = CyberScouterMatchScouting.getCurrentMatch(_db,
                    TeamMap.getNumberForTeam(cfg.getAlliance_station()));
            CyberScouterTeams cst = CyberScouterTeams.getCurrentTeam(_db, Integer.valueOf(csm.getTeam()));

            if (null != csm) {
                TextView tv = findViewById(R.id.textView_endMatch);
                tv.setText(getString(R.string.tagMatch, csm.getMatchNo()));
                tv = findViewById(R.id.textView_endTeamNumber);
                String teamText = null;
                if(cst != null) {
                    teamText = csm.getTeam() + " - " + cst.getTeamName();
                } else {
                    teamText = csm.getTeam();
                }
                tv.setText(getString(R.string.tagTeam, teamText));


                Spinner sp = findViewById(R.id.spinner_Speed);
                sp.setSelection(csm.getSummSpeed());

                sp = findViewById(R.id.spinner_ShootFrom);
                sp.setSelection(csm.getSummShootFrom());

                sp = findViewById(R.id.spinner_Ability);
                sp.setSelection(csm.getSummManuverabitlity());

                int val = csm.getSummPlayedDefense();
                playedDefenseVar = val;
                if (val != -1) {
                    FakeRadioGroup.buttonPressed(this, val, playedDefenseArray,
                            CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMPLAYEDDEFENSE,
                            SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
                    qAnswered[0] = 1;
                    shouldEnableNext();
                }
                val = csm.getSummDefPlayedAgainst();
                defenseAgainstVar = val;
                if (val != -1) {
                    FakeRadioGroup.buttonPressed(this, val, defenseAgainstArray,
                            CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMDEFPLAYEDAGAINST,
                            SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
                    qAnswered[1] = 1;
                    shouldEnableNext();
                }
                val = csm.getSummBrokeDown();
                brokeDownVar = val;
                if (val != -1) {
                    FakeRadioGroup.buttonPressed(this, val, brokeDownArray,
                            CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMBROKEDOWN,
                            SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
                    qAnswered[2] = 1;
                    shouldEnableNext();
                }
                val = csm.getSummLostComm();
                lostCommVar = val;
                if (val != -1) {
                    FakeRadioGroup.buttonPressed(this, val, lostCommArray,
                            CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMLOSTCOMM,
                            SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
                    qAnswered[3] = 1;
                    shouldEnableNext();
                }
                val = csm.getSummSubsystemBroke();
                subsystemBrokeVar = val;
                if (val != -1) {
                    FakeRadioGroup.buttonPressed(this, val, subsystemBrokeArray,
                            CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMSUBSYSTEMBROKE,
                            SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
                    qAnswered[4] = 1;
                    shouldEnableNext();
                }
                val = csm.getSummLaunchPad();
                launchPadVar = val;
                if (val != -1) {
                    FakeRadioGroup.buttonPressed(this, val, launchPadArray,
                            CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMLAUNCHPAD,
                            SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
                    qAnswered[5] = 1;
                    shouldEnableNext();
                }

                // Temporary placeholders
                ratingVar = csm.getSummRating();
                shootFromVar = csm.getSummShootFrom();
            }
        }

    }

    @Override
    protected void onDestroy(){
        _db.close();
        mDbHelper.close();
        super.onDestroy();
    }


    public void returnToEndGamePage() {
        updateSummaryQuestionPageData();
        this.finish();
    }

    public void openSubmitPage() {
        updateSummaryQuestionPageData();
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

    private void PlayedDefense(int val) {
        FakeRadioGroup.buttonPressed(this, val, playedDefenseArray,
                CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMPLAYEDDEFENSE,
                SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
        playedDefenseVar = val;
        qAnswered[0] = 1;
        shouldEnableNext();
    }

    private void DefenseAgainst(int val) {
        FakeRadioGroup.buttonPressed(this, val, defenseAgainstArray,
                CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMDEFPLAYEDAGAINST,
                SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
        defenseAgainstVar = val;
        qAnswered[1] = 1;
        shouldEnableNext();
    }


    private void BrokeDown(int val) {
        FakeRadioGroup.buttonPressed(this, val, brokeDownArray,
                CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMBROKEDOWN,
                SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
        brokeDownVar = val;
        qAnswered[2] = 1;
        shouldEnableNext();
    }

    private void LostComm(int val) {
        FakeRadioGroup.buttonPressed(this, val, lostCommArray,
                CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMLOSTCOMM,
                SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
        lostCommVar = val;
        qAnswered[3] = 1;
        shouldEnableNext();
    }

    private void SubsystemBroke(int val) {
        FakeRadioGroup.buttonPressed(this, val, subsystemBrokeArray,
                CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMSUBSYSTEMBROKE,
                SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
        subsystemBrokeVar = val;
        qAnswered[4] = 1;
        shouldEnableNext();
    }


    private void ShootFrom(int val) {
        FakeRadioGroup.buttonPressed(this, val, launchPadArray,
                CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMLAUNCHPAD,
                SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
        launchPadVar = val;
        qAnswered[5] = 1;
        shouldEnableNext();
    }

    private void shouldEnableNext() {
        boolean bAll = true;
        for (int i : qAnswered) {
            if (i == -1) {
                bAll = false;
                break;
            }
        }
        if (bAll) {
            button = findViewById(R.id.button_sumqNext);
            button.setEnabled(bAll);
        }
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
            Integer[] _lValues = { playedDefenseVar,
                    defenseAgainstVar, brokeDownVar, lostCommVar, subsystemBrokeVar, launchPadVar, ratingVar, shootFromVar, speedVar, howGoodVar};
            CyberScouterMatchScouting.updateMatchMetric(_db, _lColumns, _lValues, cfg);
        } catch (Exception e) {
            e.printStackTrace();
            MessageBox.showMessageBox(this, "Update Error",
                    "SummaryQuestionsPage.updateSummaryQuestionsPageData", "SQLite update failed!\n " + e.getMessage());
        }
    }
}