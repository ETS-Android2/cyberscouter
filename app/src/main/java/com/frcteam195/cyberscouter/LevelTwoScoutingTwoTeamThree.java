package com.frcteam195.cyberscouter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LevelTwoScoutingTwoTeamThree extends AppCompatActivity {
    private Button button;
    private int defaultButtonTextColor = Color.LTGRAY;
    private final int SELECTED_BUTTON_TEXT_COLOR = Color.GREEN;
    private Spinner primaryDefenseButtons = (Spinner) findViewById(R.id.spinner_PrimaryDefense);
    private Spinner secondaryDefenseButtons = (Spinner) findViewById(R.id.spinner_SecondayDefense);
    private Spinner howLongButtons = (Spinner) findViewById(R.id.spinner_HowLong);
    private final int[] MultiRobotDefenseButtons = {R.id.button_MRDYes, R.id.button_MRDNo};
    private final String[] DefenseOptions = {"Pinning", "Shoving", "Steal Cargo", "Blocking", "Counter-Defense", "None"};
    private final String[] HowLongOptions = {"One Quarter of the Match", "Half of the Match", "Three Quarters of the Match", "The Entire Match"};
    private ArrayAdapter<String> pd = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, DefenseOptions);
    private ArrayAdapter<String> sd = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, DefenseOptions);
    private ArrayAdapter<String> hl = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, HowLongOptions);

    String[] _lColumns = {CyberScouterContract.MatchScoutingL2.COLUMN_NAME_T3MULTIROBOTDEFENSE,
            CyberScouterContract.MatchScoutingL2.COLUMN_NAME_T3PRIMARYDEFENSE,
            CyberScouterContract.MatchScoutingL2.COLUMN_NAME_T3SECONDARYDEFENSE,
            CyberScouterContract.MatchScoutingL2.COLUMN_NAME_T3HOWLONG
    };

    private int multiRobotDefense, primaryDefense, secondaryDefense, howLong;

    private int currentCommStatusColor;

    private CyberScouterDbHelper mDbHelper = new CyberScouterDbHelper(this);
    private SQLiteDatabase _db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_two_scouting_two_team_three);

        Intent intent = getIntent();
        currentCommStatusColor = intent.getIntExtra("commstatuscolor", Color.LTGRAY);
        updateStatusIndicator(currentCommStatusColor);

        button = findViewById(R.id.button_MRDNo);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRDNo();
            }
        });

        button = findViewById(R.id.button_MRDYes);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRDYes();
            }
        });

        primaryDefenseButtons.setAdapter(pd);
        pd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        primaryDefenseButtons.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                primaryDefense = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        secondaryDefenseButtons.setAdapter(sd);
        sd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        secondaryDefenseButtons.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                secondaryDefense = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        howLongButtons.setAdapter(hl);
        hl.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        howLongButtons.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                howLong = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        _db = mDbHelper.getWritableDatabase();
        CyberScouterConfig cfg = CyberScouterConfig.getConfig(_db);
        CyberScouterMatchScoutingL2 csm = CyberScouterMatchScoutingL2.getCurrentMatch(_db, TeamMap.getNumberForTeam(cfg.getAlliance_station()));

        if (null != csm) {
            int tval = csm.getT3MultiRobotDefense() == 0 ? 0 : csm.getT3MultiRobotDefense() - 1;
            FakeRadioGroup.buttonDisplay(this, tval, MultiRobotDefenseButtons, SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
            multiRobotDefense = csm.getT3MultiRobotDefense();
        }
    }

    public void mRDNo() {
        FakeRadioGroup.buttonPressed(this, 0, MultiRobotDefenseButtons, CyberScouterContract.MatchScoutingL2.COLUMN_NAME_T3MULTIROBOTDEFENSE, SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
        multiRobotDefense = 0;
    }

    public void mRDYes() {
        FakeRadioGroup.buttonPressed(this, 1, MultiRobotDefenseButtons, CyberScouterContract.MatchScoutingL2.COLUMN_NAME_T3MULTIROBOTDEFENSE, SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
        multiRobotDefense = 1;
    }

    private void updateStatusIndicator(int color) {
        ImageView iv = findViewById(R.id.imageView_btIndicator);
        BluetoothComm.updateStatusIndicator(iv, color);
    }
    private void updateLevelTwoScoutingThree() {
        CyberScouterConfig cfg = CyberScouterConfig.getConfig(_db);
        try {
            Integer[] _lValues = {multiRobotDefense, primaryDefense, secondaryDefense, howLong};
            CyberScouterMatchScouting.updateMatchMetric(_db, _lColumns, _lValues, cfg);
        } catch(Exception e) {
            e.printStackTrace();
            MessageBox.showMessageBox(this, "Update Error",
                    "LevelTwoScoutingTwoTeamThree.updateLevelTwoScoutingTwoTeamThree", "SQLite update failed!\n "+e.getMessage());
        }
    }
}