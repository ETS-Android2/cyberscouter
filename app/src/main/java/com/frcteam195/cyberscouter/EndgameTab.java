package com.frcteam195.cyberscouter;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class EndgameTab extends Fragment implements IOnEditTextSaveListener {
    private final int[] canTheyClimb = {R.id.noCanTheyClimb, R.id.yesCanTheyClimb};
    private int defaultButtonBackgroundColor = Color.LTGRAY;
    private final int SELECTED_BUTTON_TEXT_COLOR = Color.GREEN;
    private View _view;
    private String[] positionOnBar = {"None", "Left", "Center", "Right", "Any"};
    private String[] typicalHighestRung = {"None", "Low", "Medium", "High", "Traversal"};

    private int currentTeam;
    private CyberScouterDbHelper mDbHelper;
    SQLiteDatabase _db;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_endgame,container,false);
        _view = view;

        Button button = view.findViewById(R.id.yesCanTheyClimb);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                yesCanTheyClimb();
            }
        });

        button = view.findViewById(R.id.noCanTheyClimb);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noCanTheyClimb();
            }
        });

        Spinner spinnerPositionOnBar = view.findViewById(R.id.spinnerPositionOnBar);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, positionOnBar);
        spinnerPositionOnBar.setAdapter(adapter);
        spinnerPositionOnBar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                updateSpinnerValue(CyberScouterContract.Teams.COLUMN_NAME_CLIMB_POSITION, i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Spinner spinnerTypicalHighestRung = view.findViewById(R.id.spinnerTypicalHighestRung);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, typicalHighestRung);
        spinnerTypicalHighestRung.setAdapter(adapter1);
        spinnerTypicalHighestRung.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                updateSpinnerValue(CyberScouterContract.Teams.COLUMN_NAME_CLIMB_HEIGHT_ID, i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        populateScreen();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            populateScreen();
        }
    }

    @Override
    public void onDestroy() {
        if (mDbHelper != null) {
            mDbHelper.close();
        }
        super.onDestroy();
    }

    private void populateScreen() {
        if (null == getActivity()) {
            return;
        }
        mDbHelper = new CyberScouterDbHelper(getActivity());

        _db = mDbHelper.getWritableDatabase();
        currentTeam = PitScoutingActivity.getCurrentTeam();

        CyberScouterTeams cst = CyberScouterTeams.getCurrentTeam(_db, currentTeam);

        if (null != cst) {
            EditText et = _view.findViewById(R.id.editText_endgameStrat);
            et.setText(String.valueOf(cst.getClimbHeightID()));
            et.setSelectAllOnFocus(true);

            FakeRadioGroup.buttonDisplay(getActivity(), _view, cst.getCanClimb(), canTheyClimb, SELECTED_BUTTON_TEXT_COLOR, defaultButtonBackgroundColor);
        }
    }

    private void yesCanTheyClimb() {
        FakeRadioGroup.buttonPressed(getActivity(),_view,1,canTheyClimb,
                CyberScouterContract.Teams.COLUMN_NAME_CAN_CLIMB,
                SELECTED_BUTTON_TEXT_COLOR, defaultButtonBackgroundColor);
        updateFakeRadioButton(CyberScouterContract.Teams.COLUMN_NAME_CAN_CLIMB, 1);
    }

    private void noCanTheyClimb() {
        FakeRadioGroup.buttonPressed(getActivity(),_view,0,canTheyClimb,
                CyberScouterContract.Teams.COLUMN_NAME_CAN_CLIMB,
                SELECTED_BUTTON_TEXT_COLOR, defaultButtonBackgroundColor);
        updateFakeRadioButton(CyberScouterContract.Teams.COLUMN_NAME_CAN_CLIMB, 0);
    }

    public void saveTextValues() {
        try {
            EditText et = _view.findViewById(R.id.editText_endgameStrat);
            CyberScouterTeams.updateTeamMetric(_db, CyberScouterContract.Teams.COLUMN_NAME_CLIMB_TIME,
                    Integer.parseInt(et.getText().toString()), currentTeam);
            et = _view.findViewById(R.id.editText_endgameStrat);
            CyberScouterTeams.updateTeamMetric(_db, CyberScouterContract.Teams.COLUMN_NAME_CLIMB_STRATEGY,
                    et.getText().toString(), currentTeam);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateFakeRadioButton(String col, int val) {
        try {
            CyberScouterTeams.updateTeamMetric(_db, col, val, currentTeam);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateSpinnerValue(String col, int val) {
        try {
            CyberScouterTeams.updateTeamMetric(_db, col, val, currentTeam);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
