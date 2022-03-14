package com.frcteam195.cyberscouter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.frcteam195.cyberscouter.ui.main.SectionsPagerAdapter;

import java.util.List;

public class PitScoutingActivity extends AppCompatActivity {
    private CyberScouterDbHelper mDbHelper = new CyberScouterDbHelper(this);
    private static int _currentTeam;
    private static boolean _commitDisabled;

    public static int getCurrentTeam(){ return(_currentTeam);}
    public static void setCurrentTeam(int l_team) { _currentTeam = l_team; }

    public static boolean getCommitDisabled(){ return(_commitDisabled);}
    public static void setCommitDisabled(boolean l_val) { _commitDisabled = l_val; }

    private static ScoutingTab scoutingTabFragment;
    public void setScoutingTab(ScoutingTab st) { scoutingTabFragment = st;}

    private SQLiteDatabase _db;

    BroadcastReceiver mOnlineStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int color = intent.getIntExtra("onlinestatus", Color.RED);
            updateStatusIndicator(color);
        }
    };

    BroadcastReceiver mTeamsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String ret = intent.getStringExtra("cyberscouterteams");
            if(ret.equalsIgnoreCase("fetch")) {
                updateTeams(ret);
            } else if (ret.equalsIgnoreCase("update")) {
                handleTeamRemoteUpdateReturn();
            }
        }
    };

//    BroadcastReceiver mTeamsUpdater = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String ret = intent.getStringExtra("cyberscouterteams");
//            updateTeams(ret);
//        }
//    };

    //    BroadcastReceiver mUsersReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String ret = intent.getStringExtra("cyberscouterusers");
//            updateUsers(ret);
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pit_scouting);

        registerReceiver(mOnlineStatusReceiver, new IntentFilter(BluetoothComm.ONLINE_STATUS_UPDATED_FILTER));
        registerReceiver(mTeamsReceiver, new IntentFilter(CyberScouterTeams.TEAMS_UPDATED_FILTER));
//        registerReceiver(mTeamsUpdater, new IntentFilter(CyberScouterTeams.TEAMS_UPDATED_FILTER));
//        registerReceiver(mUsersReceiver, new IntentFilter(CyberScouterUsers.USERS_UPDATED_FILTER));

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //do stuff here
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                List<Fragment> fragList = getSupportFragmentManager().getFragments();
                for(Fragment f : fragList) {
                    if(f instanceof IOnEditTextSaveListener) {
                        IOnEditTextSaveListener ff = (IOnEditTextSaveListener) f;
                        ff.saveTextValues();
                    }
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        FloatingActionButton fab = findViewById(R.id.fab);
    }

    @Override
    protected void onResume() {
        super.onResume();

        _db = mDbHelper.getWritableDatabase();

//        String users_str = CyberScouterUsers.getUsersRemote(this);
//        if(null != users_str && !users_str.equalsIgnoreCase("skip")) {
//            updateUsers(users_str);
//        }

        String teams_str = CyberScouterTeams.getTeamsRemote(this, _db);
        if (null != teams_str && !teams_str.equalsIgnoreCase("skip")) {
            updateTeams(teams_str);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if(mDbHelper != null) {
            mDbHelper.close();
        }
        unregisterReceiver(mTeamsReceiver);
        unregisterReceiver(mOnlineStatusReceiver);
//        unregisterReceiver(mUsersReceiver);

        super.onDestroy();
    }

    private void updateStatusIndicator(int color) {
        ImageView iv = findViewById(R.id.imageView_btIndicator);
        BluetoothComm.updateStatusIndicator(iv, color);
    }

    private void updateTeams(String teams) {
        SQLiteDatabase _db =  mDbHelper.getWritableDatabase();
        if(teams.equalsIgnoreCase("fetch")) {
            teams = CyberScouterTeams.getWebResponse();
        }
        CyberScouterTeams.setTeams(_db, teams);

        FragmentManager supportFragmentManager = getSupportFragmentManager();
        List<Fragment> fragList = supportFragmentManager.getFragments();
        for(Fragment f : fragList) {
            if(f instanceof ScoutingTab) {
                ScoutingTab st = (ScoutingTab)f;
                st.teamFetchCompleted();
                break;
            }
        }

    }

    private void updateUsers(String json){
        SQLiteDatabase _db =  mDbHelper.getWritableDatabase();
        if(!json.equalsIgnoreCase("skip")) {
            CyberScouterUsers.setUsers(_db, json);
        }
    }

    private void handleTeamRemoteUpdateReturn() {
        popToast("Remote Teams record was updated!");
    }

    private void popToast(final String msg) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}