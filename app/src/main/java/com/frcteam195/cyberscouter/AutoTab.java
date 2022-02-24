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
    private final int[] workingAutoButtons = {R.id.button_WorkingAutoNo, R.id.button_WorkingAutoYes};
    private int typicalLowCargo = 0;
    private int typicalHighCargo = 0;
    private View _view;
    private int defaultButtonBackgroundColor = Color.RED;
    private final int SELECTED_BUTTON_TEXT_COLOR = Color.GREEN;
    private String[] StartPosSpinner = {"1", "2", "3", "4", "5", "6"};
    private String[] HumanSpinner = {"Accurate", "Partially Accurate", "Not Accurate", "Does Not Use"};
    private int currentTeam;
    private CyberScouterDbHelper mDbHelper;
    SQLiteDatabase _db;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_auto, container, false);
        _view = view;

        Spinner startPos = view.findViewById(R.id.spinner_StartPosition);
        Activity startPosSpinner = getActivity();
        if(null != startPosSpinner) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(startPosSpinner, android.R.layout.simple_spinner_dropdown_item, StartPosSpinner);
            startPos.setAdapter(adapter);
            startPos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    updateSpinnerValue(CyberScouterContract.Teams.COLUMN_NAME_AUTO_START_POS_ID, i);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }

        Spinner humanPlayer = view.findViewById(R.id.spinner_HumanPlayer);
        Activity humanPlayerSpinner = getActivity();
        if(null != humanPlayerSpinner) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(humanPlayerSpinner, android.R.layout.simple_spinner_dropdown_item, HumanSpinner);
            humanPlayer.setAdapter(adapter);
            humanPlayer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

        button = view.findViewById(R.id.button_WorkingAutoYes);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                workingAutoYes();
            }
        });

        button = view.findViewById(R.id.button_WorkingAutoNo);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                workingAutoNo();
            }
        });

        button = view.findViewById(R.id.button_TypicalCellsStoredHighMinus);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                typicalHighMinus();
            }
        });

        button = view.findViewById(R.id.button_TypicalCellsStoredHighPlus);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                typicalHighPlus();
            }
        });

        button = view.findViewById(R.id.button_TypicalCellsStoredLowMinus);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                typicalLowMinus();
            }
        });

        button = view.findViewById(R.id.button_TypicalCellsStoredLowPlus);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                typicalLowPlus();
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
            FakeRadioGroup.buttonDisplay(getActivity(), _view, cst.getPreload(), preloadButtons, SELECTED_BUTTON_TEXT_COLOR, defaultButtonBackgroundColor);
            FakeRadioGroup.buttonDisplay(getActivity(), _view, cst.getAutoPickUp(), workingAutoButtons, SELECTED_BUTTON_TEXT_COLOR, defaultButtonBackgroundColor);

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
        FakeRadioGroup.buttonPressed(getActivity(), _view, 0, preloadButtons, CyberScouterContract.Teams.COLUMN_NAME_PRE_LOAD, SELECTED_BUTTON_TEXT_COLOR, defaultButtonBackgroundColor);
        try {
            CyberScouterTeams.updateTeamMetric(_db, CyberScouterContract.Teams.COLUMN_NAME_PRE_LOAD, 0, currentTeam);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void preloadYes() {
        FakeRadioGroup.buttonPressed(getActivity(), _view, 1, preloadButtons, CyberScouterContract.Teams.COLUMN_NAME_PRE_LOAD, SELECTED_BUTTON_TEXT_COLOR, defaultButtonBackgroundColor);
        try {
            CyberScouterTeams.updateTeamMetric(_db, CyberScouterContract.Teams.COLUMN_NAME_PRE_LOAD, 1, currentTeam);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void workingAutoYes() {
        FakeRadioGroup.buttonPressed(getActivity(), _view, 1, workingAutoButtons, CyberScouterContract.Teams.COLUMN_NAME_HAS_AUTO, SELECTED_BUTTON_TEXT_COLOR, defaultButtonBackgroundColor);
        try {
            CyberScouterTeams.updateTeamMetric(_db, CyberScouterContract.Teams.COLUMN_NAME_HAS_AUTO, 1, currentTeam);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void workingAutoNo() {
        FakeRadioGroup.buttonPressed(getActivity(), _view, 0, workingAutoButtons, CyberScouterContract.Teams.COLUMN_NAME_HAS_AUTO, SELECTED_BUTTON_TEXT_COLOR, defaultButtonBackgroundColor);
        try {
            CyberScouterTeams.updateTeamMetric(_db, CyberScouterContract.Teams.COLUMN_NAME_HAS_AUTO, 1, currentTeam);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void typicalHighPlus() {
        button = _view.findViewById(R.id.button_TypicalCellsStoredHighCounter);
        typicalHighCargo++;
        button.setText(String.valueOf(typicalHighCargo));
        try {
            CyberScouterTeams.updateTeamMetric(_db, CyberScouterContract.Teams.COLUMN_NAME_DRIVE_MOTORS, typicalHighCargo, currentTeam);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void typicalHighMinus() {
        button = _view.findViewById(R.id.button_TypicalCellsStoredHighCounter);
        if (typicalHighCargo > 0)
            typicalHighCargo--;
        button.setText(String.valueOf(typicalHighCargo));
        try {
            CyberScouterTeams.updateTeamMetric(_db, CyberScouterContract.Teams.COLUMN_NAME_DRIVE_MOTORS, typicalHighCargo, currentTeam);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void typicalLowPlus() {
        button = _view.findViewById(R.id.button_TypicalCellsStoredLowCounter);
        typicalLowCargo++;
        button.setText(String.valueOf(typicalLowCargo));
        try {
            CyberScouterTeams.updateTeamMetric(_db, CyberScouterContract.Teams.COLUMN_NAME_DRIVE_MOTORS, typicalLowCargo, currentTeam);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void typicalLowMinus() {
        button = _view.findViewById(R.id.button_TypicalCellsStoredLowCounter);
        if (typicalLowCargo > 0)
            typicalLowCargo--;
        button.setText(String.valueOf(typicalLowCargo));
        try {
            CyberScouterTeams.updateTeamMetric(_db, CyberScouterContract.Teams.COLUMN_NAME_DRIVE_MOTORS, typicalLowCargo, currentTeam);
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
