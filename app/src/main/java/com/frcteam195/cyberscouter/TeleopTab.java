package com.frcteam195.cyberscouter;


import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


public class TeleopTab extends Fragment implements IOnEditTextSaveListener {
    private Button button;
    private View _view;
    int scoredHighTele = 0;
    int scoredLowTele = 0;
    private final int[] sortCargoYN = {R.id.button_sortCargoNo, R.id.button_sortCargoYes};
    private final int[] defenseYN = {R.id.button_playDefenseNo, R.id.button_playDefenseYes};
    private final int[] evadeYN = {R.id.button_evadeDefenseNo, R.id.button_evadeDefenseYes};
    private final int[] shootWhileDriveYN = {R.id.button_shootWhileDrivingNo, R.id.button_shootWhileDrivingYes};
    private final int[] carryCapacityButtons = {R.id.button_carryingCapacityZero, R.id.button_carryingCapacityOne, R.id.button_carryingCapacityTwo};
    private int defaultButtonBackgroundColor = Color.LTGRAY;
    private final int SELECTED_BUTTON_TEXT_COLOR = Color.GREEN;

    private int currentTeam;
    private CyberScouterDbHelper mDbHelper;
    SQLiteDatabase _db;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teleop, container, false);
        _view = view;

        Button button = view.findViewById(R.id.button_cargoScoredHighTelePlus);
        button.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                scoredHighTelePlus();
            }
        });

        button = view.findViewById(R.id.button_cargoScoredHighTeleMinus);
        button.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                scoredHighTeleMinus();
            }
        });

        button = view.findViewById(R.id.button_cargoScoredLowTelePlus);
        button.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                scoredLowTelePlus();
            }
        });

        button = view.findViewById(R.id.button_cargoScoredLowTeleMinus);
        button.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                scoredLowTeleMinus();
            }
        });

        button = view.findViewById(R.id.button_sortCargoNo);
        button.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                sortCargoNo();
            }
        });

        button = view.findViewById(R.id.button_sortCargoYes);
        button.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                sortCargoYes();
            }
        });

        button = view.findViewById(R.id.button_playDefenseNo);
        button.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                playDefenseNo();
            }
        });

        button = view.findViewById(R.id.button_playDefenseYes);
        button.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                playDefenseYes();
            }
        });

        button = view.findViewById(R.id.button_evadeDefenseNo);
        button.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                evadeDefenseNo();
            }
        });

        button = view.findViewById(R.id.button_evadeDefenseYes);
        button.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                evadeDefenseYes();
            }
        });

        button = view.findViewById(R.id.button_shootWhileDrivingNo);
        button.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                shootWhileDrivingNo();
            }
        });

        button = view.findViewById(R.id.button_shootWhileDrivingYes);
        button.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                shootWhileDrivingYes();
            }
        });

        button = view.findViewById(R.id.button_carryingCapacityZero);
        button.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                carryingCapacityZero();
            }
        });

        button = view.findViewById(R.id.button_carryingCapacityOne);
        button.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                carryingCapacityOne();
            }
        });

        button = view.findViewById(R.id.button_carryingCapacityTwo);
        button.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                carryingCapacityTwo();
            }
        });

        return (view);
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
            EditText et = _view.findViewById(R.id.editText_teleopStrat);
            et.setText(String.valueOf(cst.getTeleStrategy()));
            et.setSelectAllOnFocus(true);

            button = _view.findViewById(R.id.textButton_cargoScoredHighTeleCounter);
            button.setText(String.valueOf(cst.getTeleBallsScoredHigh()));
            scoredHighTele = cst.getTeleBallsScoredHigh();
            button = _view.findViewById(R.id.textButton_scoredLowTeleCounter);
            button.setText(String.valueOf(cst.getTeleBallsScoredLow()));
            scoredLowTele = cst.getTeleBallsScoredLow();

            FakeRadioGroup.buttonDisplay(getActivity(), _view, cst.getTeleSortCargo(), sortCargoYN, SELECTED_BUTTON_TEXT_COLOR, defaultButtonBackgroundColor);
            FakeRadioGroup.buttonDisplay(getActivity(), _view, cst.getTeleDefense(), defenseYN, SELECTED_BUTTON_TEXT_COLOR, defaultButtonBackgroundColor);
            FakeRadioGroup.buttonDisplay(getActivity(), _view, cst.getTeleDefenseEvade(), evadeYN, SELECTED_BUTTON_TEXT_COLOR, defaultButtonBackgroundColor);
            FakeRadioGroup.buttonDisplay(getActivity(), _view, cst.getTeleShootWhileDrive(), shootWhileDriveYN, SELECTED_BUTTON_TEXT_COLOR, defaultButtonBackgroundColor);
            FakeRadioGroup.buttonDisplay(getActivity(), _view, cst.getMaxBallCapacity(), carryCapacityButtons, SELECTED_BUTTON_TEXT_COLOR, defaultButtonBackgroundColor);
        }
    }

    private void scoredHighTelePlus() {
        button = _view.findViewById(R.id.textButton_cargoScoredHighTeleCounter);
        scoredHighTele++;
        button.setText(String.valueOf(scoredHighTele));
        try {
            CyberScouterTeams.updateTeamMetric(_db, CyberScouterContract.Teams.COLUMN_NAME_TELE_BALLS_SCORED_HIGH, scoredHighTele, currentTeam);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void scoredHighTeleMinus() {
        button = _view.findViewById(R.id.textButton_cargoScoredHighTeleCounter);
        if (scoredHighTele > 0)
            scoredHighTele--;
        button.setText(String.valueOf(scoredHighTele));
        try {
            CyberScouterTeams.updateTeamMetric(_db, CyberScouterContract.Teams.COLUMN_NAME_TELE_BALLS_SCORED_HIGH, scoredHighTele, currentTeam);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void scoredLowTelePlus() {
        button = _view.findViewById(R.id.textButton_scoredLowTeleCounter);
        scoredLowTele++;
        button.setText(String.valueOf(scoredLowTele));
        try {
            CyberScouterTeams.updateTeamMetric(_db, CyberScouterContract.Teams.COLUMN_NAME_TELE_BALLS_SCORED_LOW, scoredLowTele, currentTeam);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void scoredLowTeleMinus() {
        button = _view.findViewById(R.id.textButton_scoredLowTeleCounter);
        if (scoredLowTele > 0)
            scoredLowTele--;
        button.setText(String.valueOf(scoredLowTele));
        try {
            CyberScouterTeams.updateTeamMetric(_db, CyberScouterContract.Teams.COLUMN_NAME_TELE_BALLS_SCORED_LOW, scoredLowTele, currentTeam);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sortCargoNo() {
        FakeRadioGroup.buttonPressed(getActivity(), _view, 0, sortCargoYN,
                CyberScouterContract.Teams.COLUMN_NAME_TELE_SORT_CARGO, SELECTED_BUTTON_TEXT_COLOR,
                defaultButtonBackgroundColor);
        try {
            CyberScouterTeams.updateTeamMetric(_db, CyberScouterContract.Teams.COLUMN_NAME_TELE_SORT_CARGO, 0, currentTeam);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sortCargoYes() {
        FakeRadioGroup.buttonPressed(getActivity(), _view, 1, sortCargoYN,
                CyberScouterContract.Teams.COLUMN_NAME_TELE_SORT_CARGO, SELECTED_BUTTON_TEXT_COLOR,
                defaultButtonBackgroundColor);
        try {
            CyberScouterTeams.updateTeamMetric(_db, CyberScouterContract.Teams.COLUMN_NAME_TELE_SORT_CARGO, 1, currentTeam);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void playDefenseNo() {
        FakeRadioGroup.buttonPressed(getActivity(), _view, 0, defenseYN,
                CyberScouterContract.Teams.COLUMN_NAME_TELE_DEFENSE, SELECTED_BUTTON_TEXT_COLOR,
                defaultButtonBackgroundColor);
        try {
            CyberScouterTeams.updateTeamMetric(_db, CyberScouterContract.Teams.COLUMN_NAME_TELE_DEFENSE, 0, currentTeam);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void playDefenseYes() {
        FakeRadioGroup.buttonPressed(getActivity(), _view, 1, defenseYN,
                CyberScouterContract.Teams.COLUMN_NAME_TELE_DEFENSE, SELECTED_BUTTON_TEXT_COLOR,
                defaultButtonBackgroundColor);
        try {
            CyberScouterTeams.updateTeamMetric(_db, CyberScouterContract.Teams.COLUMN_NAME_TELE_DEFENSE, 1, currentTeam);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void evadeDefenseNo() {
        FakeRadioGroup.buttonPressed(getActivity(), _view, 0, evadeYN,
                CyberScouterContract.Teams.COLUMN_NAME_TELE_DEFENSE_EVADE, SELECTED_BUTTON_TEXT_COLOR,
                defaultButtonBackgroundColor);
        try {
            CyberScouterTeams.updateTeamMetric(_db, CyberScouterContract.Teams.COLUMN_NAME_TELE_DEFENSE_EVADE, 0, currentTeam);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void evadeDefenseYes() {
        FakeRadioGroup.buttonPressed(getActivity(), _view, 1, evadeYN,
                CyberScouterContract.Teams.COLUMN_NAME_TELE_DEFENSE_EVADE, SELECTED_BUTTON_TEXT_COLOR,
                defaultButtonBackgroundColor);
        try {
            CyberScouterTeams.updateTeamMetric(_db, CyberScouterContract.Teams.COLUMN_NAME_TELE_DEFENSE_EVADE, 1, currentTeam);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void shootWhileDrivingNo() {
        FakeRadioGroup.buttonPressed(getActivity(), _view, 0, shootWhileDriveYN,
                CyberScouterContract.Teams.COLUMN_NAME_TELE_DEFENSE_EVADE, SELECTED_BUTTON_TEXT_COLOR,
                defaultButtonBackgroundColor);
        try {
            CyberScouterTeams.updateTeamMetric(_db, CyberScouterContract.Teams.COLUMN_NAME_TELE_SHOOT_WHILE_DRIVE, 0, currentTeam);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void shootWhileDrivingYes() {
        FakeRadioGroup.buttonPressed(getActivity(), _view, 1, shootWhileDriveYN,
                CyberScouterContract.Teams.COLUMN_NAME_TELE_DEFENSE_EVADE, SELECTED_BUTTON_TEXT_COLOR,
                defaultButtonBackgroundColor);
        try {
            CyberScouterTeams.updateTeamMetric(_db, CyberScouterContract.Teams.COLUMN_NAME_TELE_SHOOT_WHILE_DRIVE, 1, currentTeam);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void carryingCapacityZero() {
        FakeRadioGroup.buttonPressed(getActivity(), _view, 0, carryCapacityButtons,
                CyberScouterContract.Teams.COLUMN_NAME_MAX_BALL_CAPACITY, SELECTED_BUTTON_TEXT_COLOR,
                defaultButtonBackgroundColor);
        try {
            CyberScouterTeams.updateTeamMetric(_db, CyberScouterContract.Teams.COLUMN_NAME_MAX_BALL_CAPACITY, 0, currentTeam);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void carryingCapacityOne() {
        FakeRadioGroup.buttonPressed(getActivity(), _view, 1, carryCapacityButtons,
                CyberScouterContract.Teams.COLUMN_NAME_MAX_BALL_CAPACITY, SELECTED_BUTTON_TEXT_COLOR,
                defaultButtonBackgroundColor);
        try {
            CyberScouterTeams.updateTeamMetric(_db, CyberScouterContract.Teams.COLUMN_NAME_MAX_BALL_CAPACITY, 1, currentTeam);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void carryingCapacityTwo() {
        FakeRadioGroup.buttonPressed(getActivity(), _view, 2, carryCapacityButtons,
                CyberScouterContract.Teams.COLUMN_NAME_MAX_BALL_CAPACITY, SELECTED_BUTTON_TEXT_COLOR,
                defaultButtonBackgroundColor);
        try {
            CyberScouterTeams.updateTeamMetric(_db, CyberScouterContract.Teams.COLUMN_NAME_MAX_BALL_CAPACITY, 2, currentTeam);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveTextValues() {
        try {
            EditText et = _view.findViewById(R.id.editText_teleopStrat);
            CyberScouterTeams.updateTeamMetric(_db, CyberScouterContract.Teams.COLUMN_NAME_TELE_STRATEGY,
                    et.getText().toString(), currentTeam);
            et = _view.findViewById(R.id.editText_defenseStrat);
            CyberScouterTeams.updateTeamMetric(_db, CyberScouterContract.Teams.COLUMN_NAME_TELE_DEFENSE_STRAT,
                    et.getText().toString(), currentTeam);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}