package com.frcteam195.cyberscouter;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import java.util.Locale;

public class ScoutingTab extends Fragment {
    private View _view;
    //    private String[] user_array;
    private String[] team_array;
    private String[] team_scouted_array;

    private CyberScouterDbHelper mDbHelper;
    SQLiteDatabase _db;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scouting, container, false);
        _view = view;

        mDbHelper = new CyberScouterDbHelper(getActivity());
        _db = mDbHelper.getWritableDatabase();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        Button button = _view.findViewById(R.id.button_scoutingTabCommit);
        //        user_array = CyberScouterUsers.getUserNames(_db);

        team_array = CyberScouterTeams.getTeamNumbers(_db, 0);
        team_scouted_array = CyberScouterTeams.getTeamNumbers(_db, 1);
        if (null != team_array) {
            populateView();
        } else {
            button.setEnabled(false);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            if (_view == null)
                return;
            Button button = _view.findViewById(R.id.button_scoutingTabCommit);
            team_array = CyberScouterTeams.getTeamNumbers(_db, 0);
            team_scouted_array = CyberScouterTeams.getTeamNumbers(_db, 1);
            if (null != team_array) {
                populateView();
            } else {
                button.setEnabled(false);
            }
        }
    }

    @Override
    public void onDestroy() {
        if (mDbHelper != null) {
            mDbHelper.close();
        }
        super.onDestroy();
    }


    private void commitTeam(int _val, int team) {
        try {
            CyberScouterTeams.updateTeamMetric(_db, CyberScouterContract.Teams.COLUMN_NAME_DONE_SCOUTING,
                    _val, team);
            if (0 == _val) {
                CyberScouterTeams.updateTeamMetric(_db, CyberScouterContract.Teams.COLUMN_NAME_UPLOAD_STATUS,
                        UploadStatus.NOT_UPLOADED, team);
            }
        } catch (Exception e) {
            MessageBox.showMessageBox(getActivity(), "Pit Scouting Commit Failed", "ScoutingTab.commitTeam",
                    String.format(Locale.getDefault(), "Attempt to commit pit scouting statistics for team %d failed!", PitScoutingActivity.getCurrentTeam()));
            e.printStackTrace();
        }
        teamFetchCompleted();
    }

    public void teamFetchCompleted() {
        team_array = CyberScouterTeams.getTeamNumbers(_db, 0);
        team_scouted_array = CyberScouterTeams.getTeamNumbers(_db, 1);
        if (null != team_array) {
            populateView();
        }
    }

    private void populateView() {
        Button button = _view.findViewById(R.id.button_scoutingTabCommit);
        Spinner teams_spinner = _view.findViewById(R.id.spinner_teams);
        Activity acty = getActivity();
        if (null != acty) {
            ArrayAdapter<String> adapter1 = new ArrayAdapter<>(acty,
                    R.layout.spinner_text_items, team_array);
            teams_spinner.setAdapter(adapter1);
            teams_spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    PitScoutingActivity.setCurrentTeam(Integer.parseInt(team_array[position]));
                    CyberScouterTeams cst = CyberScouterTeams.getCurrentTeam(_db, PitScoutingActivity.getCurrentTeam());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }

            });

            for (int i = 0; i < team_array.length; i++) {
                if (Integer.parseInt(team_array[i]) == PitScoutingActivity.getCurrentTeam()) {
                    teams_spinner.setSelection(i);
                    break;
                }
            }

            PitScoutingActivity.setCurrentTeam(Integer.parseInt(team_array[teams_spinner.getSelectedItemPosition()]));
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    commitTeam(1, PitScoutingActivity.getCurrentTeam());
                }
            });
            button.setEnabled(true);

//        Spinner names = view.findViewById(R.id.spinner_fragmentScoutingNames);
//        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(getActivity(),
//                R.layout.spinner_text_items, user_array);
//        names.setAdapter(adapter2);

            GridView gv = _view.findViewById(R.id.gridView_scoutedTeams);
            gv.removeAllViewsInLayout();
            if (null == team_scouted_array) {
                gv.setAdapter(null);
            } else {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(acty, R.layout.spinner_text_items, team_scouted_array);
                gv.setNumColumns(1);

                gv.setAdapter(adapter);

                gv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                        if (null != team_scouted_array) {
                            int t = Integer.parseInt(team_scouted_array[i]);
                            commitTeam(0, t);
                        }
                        return (true);
                    }
                });
            }
        }
    }
}
