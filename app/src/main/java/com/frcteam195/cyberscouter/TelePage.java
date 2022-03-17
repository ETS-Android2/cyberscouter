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

public class TelePage extends AppCompatActivity {
    private static final int SELECTED_BUTTON_TEXT_COLOR = Color.GREEN;
    private final int defaultButtonTextColor = Color.LTGRAY;

    private ImageView imageView8;
    private ImageView imageView9;
    private int currentCommStatusColor;

    private Integer highCount, lowCount, missCount;
    String[] _lColumns = {CyberScouterContract.MatchScouting.COLUMN_NAME_TELEBALLHIGH,
            CyberScouterContract.MatchScouting.COLUMN_NAME_TELEBALLLOW,
            CyberScouterContract.MatchScouting.COLUMN_NAME_TELEBALLMISS};

    private CyberScouterDbHelper mDbHelper = new CyberScouterDbHelper(this);
    private SQLiteDatabase _db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tele_page);

        mDbHelper = new CyberScouterDbHelper(this);
        _db = mDbHelper.getWritableDatabase();

        Button button;

        CyberScouterConfig cfg = CyberScouterConfig.getConfig(_db);

        ImageView iv = findViewById(R.id.imageView_teleBtIndicator);
        Intent intent = getIntent();
        currentCommStatusColor = intent.getIntExtra("commstatuscolor", Color.LTGRAY);
        updateStatusIndicator(currentCommStatusColor);

        TextView tv = findViewById(R.id.textView_roleTag);
        tv.setText(R.string.teleopTitle);

        button = findViewById(R.id.btnTelePrevious);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToAutoPage();
            }
        });

        button = findViewById(R.id.btnTeleNext);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EndGamePage();
            }
        });

        CyberScouterMatchScouting csm = CyberScouterMatchScouting.getCurrentMatch(_db, TeamMap.getNumberForTeam(cfg.getAlliance_station()));

        button = findViewById(R.id.btnTeleUpperAdd);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upperAddClicked();
            }
        });

        button = findViewById(R.id.btnTeleUpperSubtract);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upperSubtractClicked();
            }
        });

        button = findViewById(R.id.btnTeleLowerAdd);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lowerAddClicked();
            }
        });

        button = findViewById(R.id.btnTeleLowerSubtract);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lowerSubtractClicked();
            }
        });

        button = findViewById(R.id.MissedPlusButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MissedPlusClicked();
            }
        });

        button = findViewById(R.id.MissedMinusButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MissedMinusClicked();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        Button button;

        CyberScouterConfig cfg = CyberScouterConfig.getConfig(_db);
        if (null != cfg) {
            CyberScouterMatchScouting csm = CyberScouterMatchScouting.getCurrentMatch(_db, TeamMap.getNumberForTeam(cfg.getAlliance_station()));

            if (null != csm) {
                TextView tv = findViewById(R.id.textView_teleMatch);
                tv.setText(getString(R.string.tagMatch, csm.getMatchNo()));
                tv = findViewById(R.id.textView_teamNumber);
                tv.setText(getString(R.string.tagTeam, csm.getTeam()));

                button = findViewById(R.id.btnTeleUpperCounter);
                highCount = csm.getTeleBallHigh();
                button.setText(String.valueOf(highCount));

                button = findViewById(R.id.btnTeleLowerCounter);
                lowCount = csm.getTeleBallLow();
                button.setText(String.valueOf(lowCount));

                button = findViewById(R.id.MissedCounter);
                missCount = csm.getTeleBallMiss();
                button.setText(String.valueOf(missCount));
            }
        }
    }

    @Override
    protected void onDestroy(){
        _db.close();
        mDbHelper.close();
        super.onDestroy();
    }

    public void returnToAutoPage() {
        setMetricValues();
        this.finish();
    }

    public void EndGamePage() {
        setMetricValues();
        Intent intent = new Intent(this, EndPage.class);
        intent.putExtra("commstatuscolor", currentCommStatusColor);
        startActivity(intent);
    }

    void setMetricValues() {
        CyberScouterConfig cfg = CyberScouterConfig.getConfig(_db);

        if (null != cfg) {
            try {
                Integer[] _values = {highCount, lowCount, missCount};
                CyberScouterMatchScouting.updateMatchMetric(_db, _lColumns, _values, cfg);
            }
            catch (Exception e) {
                MessageBox.showMessageBox(this, "Metric Update Failed Alert", "setMetricValue", "An error occurred trying to update metric.\n\nError is:\n" + e.getMessage());
            }
        } else {
            MessageBox.showMessageBox(this, "Configuration Not Found Alert", "setMetricValue", "An error occurred trying to acquire current configuration.  Cannot continue.");
        }
    }

    private void updateStatusIndicator(int color) {
        ImageView iv = findViewById(R.id.imageView_teleBtIndicator);
        BluetoothComm.updateStatusIndicator(iv, color);
    }

    private void upperAddClicked() {
        highCount++;
        Button button = findViewById(R.id.btnTeleUpperCounter);
        button.setText(highCount.toString());
        /*try {
            CyberScouterTeams.updateTeamMetric(_db, CyberScouterContract.MatchScouting.COLUMN_NAME_TELEBALLHIGH, highCount, currentTeam);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    private void upperSubtractClicked() {
        if(0 < highCount) {
            highCount--;
            Button button = findViewById(R.id.btnTeleUpperCounter);
            button.setText(highCount.toString());
        }
    }

    private void lowerAddClicked() {
        lowCount++;
        Button button = findViewById(R.id.btnTeleLowerCounter);
        button.setText(lowCount.toString());
    }

    private void lowerSubtractClicked() {
        if(0 < lowCount) {
            lowCount--;
            Button button = findViewById(R.id.btnTeleLowerCounter);
            button.setText(lowCount.toString());
        }
    }

    private void MissedPlusClicked() {
        missCount++;
        Button button = findViewById(R.id.MissedCounter);
        button.setText(missCount.toString());
    }

    private void MissedMinusClicked() {
        if(0 < missCount) {
            missCount--;
            Button button = findViewById(R.id.MissedCounter);
            button.setText(missCount.toString());
        }
    }
}
