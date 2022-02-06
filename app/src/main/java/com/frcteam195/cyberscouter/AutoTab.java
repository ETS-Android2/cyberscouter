package com.frcteam195.cyberscouter;

import android.app.Activity;
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

public class AutoTab extends Fragment implements IOnEditTextSaveListener {
    private Button button;
    private final int[] moveBonusButtons = {R.id.button_didNotMove, R.id.button_moveBonusYes};
    private final int[] pickupButtons = {R.id.button_PickupNo, R.id.button_PickupYes};
    private final int[] preloadButtons = {R.id.button_PreloadNo, R.id.button_PreloadYes};
    private View _view;
    private int defaultButtonBackgroundColor = Color.LTGRAY;
    private final int SELECTED_BUTTON_TEXT_COLOR = Color.GREEN;
    private String[] SpinnerItems = {"Starting Position", "1", "2", "3", "4", "5", "6"};
    private String[] HumanSpinnerItems = {"Human Player", "Accurate", "Partially Accurate", "Not Accurate", "Does Not Use"};
    private int currentTeam;
    private CyberScouterDbHelper mDbHelper;
    SQLiteDatabase _db;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_auto, container, false);
        _view = view;

        Spinner dropdown = view.findViewById(R.id.spinner_StartPosition);
        Activity acty = getActivity();
        if(null != acty) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(acty, android.R.layout.simple_spinner_dropdown_item, SpinnerItems);
            dropdown.setAdapter(adapter);
            dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    updateSpinnerValue(CyberScouterContract.Teams.COLUMN_NAME_AUTO_START_POS_ID, i);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }

        button = view.findViewById(R.id.button_didNotMove);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                moveBonusNo();
            }
        });

        button = view.findViewById(R.id.button_moveBonusYes);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                moveBonusYes();
            }
        });

        button = view.findViewById(R.id.button_PickupNo);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                pickupNo();
            }
        });

        button = view.findViewById(R.id.button_PickupYes);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                pickupYes();
            }
        });

        button = view.findViewById(R.id.button_PreloadYes);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                preloadYes();
            }
        });

        button = view.findViewById(R.id.button_PreloadNo);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                preloadNo();
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
            EditText et = _view.findViewById(R.id.editText_autoSummary);
            et.setText(String.valueOf(cst.getAutoSummary()));
            et.setSelectAllOnFocus(true);



            FakeRadioGroup.buttonDisplay(getActivity(), _view, cst.getMoveBonus(), moveBonusButtons, SELECTED_BUTTON_TEXT_COLOR, defaultButtonBackgroundColor);
            FakeRadioGroup.buttonDisplay(getActivity(), _view, cst.getAutoPickUp(), pickupButtons, SELECTED_BUTTON_TEXT_COLOR, defaultButtonBackgroundColor);
            FakeRadioGroup.buttonDisplay(getActivity(), _view, cst.getAutoPickUp(), preloadButtons, SELECTED_BUTTON_TEXT_COLOR, defaultButtonBackgroundColor);

            Spinner sp = _view.findViewById(R.id.spinner_StartPosition);
            sp.setSelection(cst.getAutoStartPosID());

        }
    }


    public void moveBonusNo() {
        FakeRadioGroup.buttonPressed(getActivity(), _view, 0, moveBonusButtons, CyberScouterContract.Teams.COLUMN_NAME_MOVE_BONUS, SELECTED_BUTTON_TEXT_COLOR, defaultButtonBackgroundColor);
        try {
            CyberScouterTeams.updateTeamMetric(_db, CyberScouterContract.Teams.COLUMN_NAME_MOVE_BONUS, 0, currentTeam);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void moveBonusYes() {
        FakeRadioGroup.buttonPressed(getActivity(), _view, 1, moveBonusButtons, CyberScouterContract.Teams.COLUMN_NAME_MOVE_BONUS, SELECTED_BUTTON_TEXT_COLOR, defaultButtonBackgroundColor);
        try {
            CyberScouterTeams.updateTeamMetric(_db, CyberScouterContract.Teams.COLUMN_NAME_MOVE_BONUS, 1, currentTeam);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pickupNo() {
        FakeRadioGroup.buttonPressed(getActivity(), _view, 0, pickupButtons, CyberScouterContract.Teams.COLUMN_NAME_AUTO_PICKUP, SELECTED_BUTTON_TEXT_COLOR, defaultButtonBackgroundColor);
        try {
            CyberScouterTeams.updateTeamMetric(_db, CyberScouterContract.Teams.COLUMN_NAME_AUTO_PICKUP, 0, currentTeam);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pickupYes() {
        FakeRadioGroup.buttonPressed(getActivity(), _view, 1, pickupButtons, CyberScouterContract.Teams.COLUMN_NAME_AUTO_PICKUP, SELECTED_BUTTON_TEXT_COLOR, defaultButtonBackgroundColor);
        try {
            CyberScouterTeams.updateTeamMetric(_db, CyberScouterContract.Teams.COLUMN_NAME_AUTO_PICKUP, 1, currentTeam);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void preloadNo() {
        FakeRadioGroup.buttonPressed(getActivity(), _view, 0, preloadButtons, CyberScouterContract.Teams.COLUMN_NAME_NUM_PRE_LOAD, SELECTED_BUTTON_TEXT_COLOR, defaultButtonBackgroundColor);
        try {
            CyberScouterTeams.updateTeamMetric(_db, CyberScouterContract.Teams.COLUMN_NAME_NUM_PRE_LOAD, 0, currentTeam);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void preloadYes() {
        FakeRadioGroup.buttonPressed(getActivity(), _view, 1, preloadButtons, CyberScouterContract.Teams.COLUMN_NAME_NUM_PRE_LOAD, SELECTED_BUTTON_TEXT_COLOR, defaultButtonBackgroundColor);
        try {
            CyberScouterTeams.updateTeamMetric(_db, CyberScouterContract.Teams.COLUMN_NAME_NUM_PRE_LOAD, 1, currentTeam);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveTextValues() {
        try {
            EditText et = _view.findViewById(R.id.editText_autoSummary);
            CyberScouterTeams.updateTeamMetric(_db, CyberScouterContract.Teams.COLUMN_NAME_AUTO_SUMMARY,
                    et.getText().toString(), currentTeam);
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
