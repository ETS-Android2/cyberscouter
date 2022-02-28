package com.frcteam195.cyberscouter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
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
//import android.widget.ImageView;
//import android.hardware.camera2.CameraDevice;

import java.util.Locale;

public class PhysicalPropertiesTab extends Fragment implements IOnEditTextSaveListener {
    private Button button;
    private final int[] gearSpeedButtons = {R.id.gearSpeed1, R.id.gearSpeed2, R.id.gearSpeed3};
    private final int[] pneumaticsYNButtons = {R.id.pneumaticsNo, R.id.pneumaticsYes};
    private View _view;
    private int defaultButtonBackgroundColor = Color.LTGRAY;
    private final int SELECTED_BUTTON_TEXT_COLOR = Color.GREEN;
    private int numberOfMotors = 0;
    private int numberOfWheels = 0;
    private int pneumatics = 0;
    private int gearSpeed = 0;
    private String[] driveTypes = {"Swerve", "Mecanum", "Tank", "H-Drive", "Other"};
    private String[] motorTypes = {"CIM", "NEO", "Falcon", "Other"};
    private String[] wheelTypes = {"Colson", "Mecanum", "Tread", "Omni", "Pneumatic", "Traction", "Other"};
    private String[] progLangTypes = {"Java", "C++", "LabView", "Python", "Other"};
    private int currentTeam;

    private CyberScouterDbHelper mDbHelper;
    SQLiteDatabase _db;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_physical_properties, container, false);
        _view = view;

        currentTeam = PitScoutingActivity.getCurrentTeam();

        Spinner driveType = view.findViewById(R.id.driveTypePicker);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, driveTypes);
        driveType.setAdapter(adapter1);
        driveType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                updateSpinnerValue(CyberScouterContract.Teams.COLUMN_NAME_DRIVE_TYPE_ID, i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Spinner motorType = view.findViewById(R.id.motorTypePicker);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, motorTypes);
        motorType.setAdapter(adapter2);
        motorType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                updateSpinnerValue(CyberScouterContract.Teams.COLUMN_NAME_MOTOR_TYPE_ID, i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Spinner wheelType = view.findViewById(R.id.wheelTypePicker);
        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, wheelTypes);
        wheelType.setAdapter(adapter3);
        wheelType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                updateSpinnerValue(CyberScouterContract.Teams.COLUMN_NAME_WHEEL_TYPE_ID, i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        Spinner progLang = view.findViewById(R.id.progLangPicker);
        ArrayAdapter<String> adapter4 = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, progLangTypes);
        progLang.setAdapter(adapter4);
        progLang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                updateSpinnerValue(CyberScouterContract.Teams.COLUMN_NAME_LANGUAGE_ID, i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        Button button = view.findViewById(R.id.motorMinusButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                motorMinusButton();
            }
        });

        button = view.findViewById(R.id.motorPlusButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                motorPlusButton();
            }
        });

        button = view.findViewById(R.id.wheelsMinusButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wheelsMinusButton();
            }
        });

        button = view.findViewById(R.id.wheelsPlusButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wheelsPlusButton();
            }
        });

        button = view.findViewById(R.id.gearSpeed1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gearSpeed(0);
            }
        });

        button = view.findViewById(R.id.gearSpeed2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gearSpeed(1);
            }
        });

        button = view.findViewById(R.id.gearSpeed3);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gearSpeed(2);
            }
        });

        button = view.findViewById(R.id.pneumaticsYes);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pneumaticsYes();
            }
        });

//        button = view.findViewById(R.id.takePictureBtn);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ImageView iv = view.findViewById(R.id.robotPicture);
//                iv.setImageResource(MediaStore.ACTION_IMAGE_CAPTURE);
//            }
//        });

        button = view.findViewById(R.id.pneumaticsNo);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pneumaticsNo();
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
            currentTeam = PitScoutingActivity.getCurrentTeam();
            populateScreen();
        }
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
            EditText et = _view.findViewById(R.id.lengthInput);
            et.setText(String.valueOf(cst.getRobotLength()));
            et.setSelectAllOnFocus(true);
            et = _view.findViewById(R.id.widthInput);
            et.setText(String.valueOf(cst.getRobotWidth()));
            et.setSelectAllOnFocus(true);
            et = _view.findViewById(R.id.heightInput);
            et.setText(String.valueOf(cst.getRobotHeight()));
            et.setSelectAllOnFocus(true);
            et = _view.findViewById(R.id.weightInput);
            et.setText(String.valueOf(cst.getRobotWeight()));
            et.setSelectAllOnFocus(true);
            et = _view.findViewById(R.id.editText_topSpeed);
            et.setText(String.valueOf(cst.getSpeed()));
            et.setSelectAllOnFocus(true);
            String gearRatio = cst.getGearRatio();
            String[] aGearRatio;
            if (gearRatio.contains(":")) {
                aGearRatio = gearRatio.split(":");
                if (2 > aGearRatio.length) {
                    aGearRatio = new String[2];
                    aGearRatio[0] = "";
                    aGearRatio[1] = "";
                }
            } else {
                aGearRatio = new String[2];
                aGearRatio[0] = "";
                aGearRatio[1] = "";
            }
            et = _view.findViewById(R.id.gearRatio1);
            et.setText(aGearRatio[0]);
            et.setSelectAllOnFocus(true);
            et = _view.findViewById(R.id.gearRatio2);
            et.setText(aGearRatio[1]);
            et.setSelectAllOnFocus(true);


            button = _view.findViewById(R.id.numberOfMotorsButton);
            numberOfMotors = cst.getNumDriveMotors();
            button.setText(String.valueOf(numberOfMotors));
            button = _view.findViewById(R.id.numberOfWheelsButton);
            numberOfWheels = cst.getNumWheels();
            button.setText(String.valueOf(numberOfWheels));

            pneumatics = cst.getPneumatics();
            FakeRadioGroup.buttonDisplay(getActivity(), _view, pneumatics, pneumaticsYNButtons, SELECTED_BUTTON_TEXT_COLOR, defaultButtonBackgroundColor);
            gearSpeed = cst.getNumGearSpeed();
            FakeRadioGroup.buttonDisplay(getActivity(), _view, gearSpeed, gearSpeedButtons, SELECTED_BUTTON_TEXT_COLOR, defaultButtonBackgroundColor);

            Spinner sp = _view.findViewById(R.id.driveTypePicker);
            sp.setSelection(cst.getDriveTypeID());
            sp = _view.findViewById(R.id.motorTypePicker);
            sp.setSelection(cst.getMotorTypeID());
            sp = _view.findViewById(R.id.wheelTypePicker);
            sp.setSelection(cst.getWheelTypeID());
            sp = _view.findViewById(R.id.progLangPicker);
            sp.setSelection(cst.getLanguageID());
        }
    }

    private void motorMinusButton() {
        button = _view.findViewById(R.id.numberOfMotorsButton);
        if (numberOfMotors > 0)
            numberOfMotors--;
        button.setText(String.valueOf(numberOfMotors));
        try {
            CyberScouterTeams.updateTeamMetric(_db, CyberScouterContract.Teams.COLUMN_NAME_DRIVE_MOTORS, numberOfMotors, currentTeam);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void motorPlusButton() {
        button = _view.findViewById(R.id.numberOfMotorsButton);
        numberOfMotors++;
        button.setText(String.valueOf(numberOfMotors));
        try {
            CyberScouterTeams.updateTeamMetric(_db, CyberScouterContract.Teams.COLUMN_NAME_DRIVE_MOTORS, numberOfMotors, currentTeam);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void wheelsMinusButton() {
        button = _view.findViewById(R.id.numberOfWheelsButton);
        if (numberOfWheels > 0)
            numberOfWheels--;
        button.setText(String.valueOf(numberOfWheels));
        try {
            CyberScouterTeams.updateTeamMetric(_db, CyberScouterContract.Teams.COLUMN_NAME_NUM_WHEELS, numberOfWheels, currentTeam);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void wheelsPlusButton() {
        button = _view.findViewById(R.id.numberOfWheelsButton);
        numberOfWheels++;
        button.setText(String.valueOf(numberOfWheels));
        try {
            CyberScouterTeams.updateTeamMetric(_db, CyberScouterContract.Teams.COLUMN_NAME_NUM_WHEELS, numberOfWheels, currentTeam);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void gearSpeed(int val) {
        FakeRadioGroup.buttonPressed(getActivity(), _view, val, gearSpeedButtons,
                CyberScouterContract.Teams.COLUMN_NAME_NUM_GEAR_SPEED, SELECTED_BUTTON_TEXT_COLOR,
                defaultButtonBackgroundColor);
        gearSpeed = val;
        try {
            CyberScouterTeams.updateTeamMetric(_db, CyberScouterContract.Teams.COLUMN_NAME_NUM_GEAR_SPEED, val, currentTeam);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void pneumaticsYes() {
        FakeRadioGroup.buttonPressed(getActivity(), _view, 1, pneumaticsYNButtons,
                CyberScouterContract.Teams.COLUMN_NAME_PNEUMATICS, SELECTED_BUTTON_TEXT_COLOR,
                defaultButtonBackgroundColor);
        pneumatics = 1;
        try {
            CyberScouterTeams.updateTeamMetric(_db, CyberScouterContract.Teams.COLUMN_NAME_PNEUMATICS, 1, currentTeam);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void pneumaticsNo() {
        FakeRadioGroup.buttonPressed(getActivity(), _view, 0, pneumaticsYNButtons,
                CyberScouterContract.Teams.COLUMN_NAME_PNEUMATICS, SELECTED_BUTTON_TEXT_COLOR,
                defaultButtonBackgroundColor);
        pneumatics = 0;
        try {
            CyberScouterTeams.updateTeamMetric(_db, CyberScouterContract.Teams.COLUMN_NAME_PNEUMATICS, 0, currentTeam);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveTextValues() {
        try {
            EditText et = _view.findViewById(R.id.lengthInput);
            CyberScouterTeams.updateTeamMetric(_db, CyberScouterContract.Teams.COLUMN_NAME_ROBOT_LENGTH,
                    Integer.parseInt(et.getText().toString()), currentTeam);
            et = _view.findViewById(R.id.widthInput);
            CyberScouterTeams.updateTeamMetric(_db, CyberScouterContract.Teams.COLUMN_NAME_ROBOT_WIDTH,
                    Integer.parseInt(et.getText().toString()), currentTeam);
            et = _view.findViewById(R.id.heightInput);
            CyberScouterTeams.updateTeamMetric(_db, CyberScouterContract.Teams.COLUMN_NAME_ROBOT_HEIGHT,
                    Integer.parseInt(et.getText().toString()), currentTeam);
            et = _view.findViewById(R.id.weightInput);
            CyberScouterTeams.updateTeamMetric(_db, CyberScouterContract.Teams.COLUMN_NAME_ROBOT_WEIGHT,
                    Integer.parseInt(et.getText().toString()), currentTeam);
            et = _view.findViewById(R.id.editText_topSpeed);
            CyberScouterTeams.updateTeamMetric(_db, CyberScouterContract.Teams.COLUMN_NAME_SPEED,
                    Integer.parseInt(et.getText().toString()), currentTeam);
            et = _view.findViewById(R.id.gearRatio1);
            EditText et2 = _view.findViewById(R.id.gearRatio2);
            String gr = String.format(Locale.getDefault(), "%s:%s", et.getText().toString(), et2.getText().toString());
            CyberScouterTeams.updateTeamMetric(_db, CyberScouterContract.Teams.COLUMN_NAME_GEAR_RATIO,
                    gr, currentTeam);
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
