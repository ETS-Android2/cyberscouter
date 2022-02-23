package com.frcteam195.cyberscouter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/* + or - block briefly turns alliance color when tapped
Word background turns light gray, red, yellow, green, blue, or purple depending on how many times the + or - button has been tapped

Black=-3
Red=-2
Yellow=-1
White=0
Green=+1
Blue=+2
Purple=+3
*/

public class WordCloudActivity extends AppCompatActivity {
    static private AppCompatActivity _activity;

    static protected AppCompatActivity getActivity() {
        return (_activity);
    }

    private CyberScouterDbHelper mDbHelper = new CyberScouterDbHelper(this);
    private SQLiteDatabase _db = null;

    private ViewPager mPager;

    private Integer fastCountT1, driverCountT1, aggressiveCountT1, cargoCountT1, evasiveCountT1, sturdyCountT1, powerfulCountT1;
   /* String[] _lColumns = {CyberScouterContract.MatchScouting.COLUMN_NAME_TELEBALLHIGH,
            CyberScouterContract.MatchScouting.COLUMN_NAME_TELEBALLLOW,
            CyberScouterContract.MatchScouting.COLUMN_NAME_TELEBALLMISS}; */

    private Integer fastCountT2, driverCountT2, aggressiveCountT2, cargoCountT2, evasiveCountT2, sturdyCountT2, powerfulCountT2;
   /* String[] _lColumns = {CyberScouterContract.MatchScouting.COLUMN_NAME_TELEBALLHIGH,
            CyberScouterContract.MatchScouting.COLUMN_NAME_TELEBALLLOW,
            CyberScouterContract.MatchScouting.COLUMN_NAME_TELEBALLMISS}; */

    private Integer fastCountT3, driverCountT3, aggressiveCountT3, cargoCountT3, evasiveCountT3, sturdyCountT3, powerfulCountT3;
   /* String[] _lColumns = {CyberScouterContract.MatchScouting.COLUMN_NAME_TELEBALLHIGH,
            CyberScouterContract.MatchScouting.COLUMN_NAME_TELEBALLLOW,
            CyberScouterContract.MatchScouting.COLUMN_NAME_TELEBALLMISS}; */

    private Handler mFetchHandler;
    private Thread fetcherThread;
    private final int START_PROGRESS = 0;
    private final int FETCH_WORDS = 1;
    private final int FETCH_MATCHES = 2;

    private int _currentPos;

    BroadcastReceiver mWordsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String ret = intent.getStringExtra("cyberscouterwords");
            updateWords(ret);
        }
    };

    BroadcastReceiver mMatchesL2Receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String ret = intent.getStringExtra("cyberscoutermatchesl2");
            updateMatchesL2Local(ret);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_cloud);

        _activity = this;

        registerReceiver(mWordsReceiver, new IntentFilter(CyberScouterWords.WORDS_UPDATED_FILTER));
        registerReceiver(mMatchesL2Receiver, new IntentFilter(CyberScouterMatchScoutingL2.MATCH_SCOUTING_L2_UPDATED_FILTER));

        //mPager = findViewById(R.id.viewPager_wcPager);

        _db = mDbHelper.getWritableDatabase();

        fetcherThread = new Thread(new RemoteFetcher());


        mFetchHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case START_PROGRESS:
                        showProgress();
                        break;
                    case FETCH_WORDS:
                        fetchWords();
                        break;
                    case FETCH_MATCHES:
                        fetchMatches();
                        break;
                }
            }
        };

        if (null == fetcherThread) {
            fetcherThread = new Thread(new RemoteFetcher());
        }
        if (!fetcherThread.isAlive()) {
            fetcherThread.start();
        }

       Button button = findViewById(R.id.FastMinusTeam1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastMinusTeam1Clicked();
            }
        });
        button = findViewById(R.id.fastMinusTeam2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastMinusTeam2Clicked();
            }
        });
        button = findViewById(R.id.fastMinusTeam3);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastMinusTeam3Clicked();
            }
        });
        button = findViewById(R.id.fastPlusTeam);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fastPlusTeamClicked();
            }
        });
        button = findViewById(R.id.fastPlusTeam2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fastPlusTeam2Clicked();
            }
        });
        button = findViewById(R.id.FastPlusTeam3);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastPlusTeam3Clicked();
            }
        });

        button = findViewById(R.id.GoodDriverMinusTeam1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoodDriverMinusTeam1Clicked();
            }
        });
        button = findViewById(R.id.goodDriverMinusTeam2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goodDriverMinusTeam2Clicked();
            }
        });
        button = findViewById(R.id.goodDriverMinusTeam3);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goodDriverMinusTeam3Clicked();
            }
        });
        button = findViewById(R.id.goodDriverTeam);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goodDriverPlusTeamClicked();
            }
        });
        button = findViewById(R.id.goodDriverTeam2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goodDriverPlusTeam2Clicked();
            }
        });
        button = findViewById(R.id.GoodDriverTeam3);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoodDriverPlusTeam3Clicked();
            }
        });

        button = findViewById(R.id.AggessiveMinusTeam1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AggressiveMinusTeam1Clicked();
            }
        });
        button = findViewById(R.id.aggessiveMinusTeam2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aggressiveMinusTeam2Clicked();
            }
        });
        button = findViewById(R.id.aggessiveMinusTeam3);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aggressiveMinusTeam3Clicked();
            }
        });
        button = findViewById(R.id.aggressivePlusTeam);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aggressivePlusTeamClicked();
            }
        });
        button = findViewById(R.id.aggressivePlusTeam2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aggressivePlusTeam2Clicked();
            }
        });
        button = findViewById(R.id.AggressivePlusTeam3);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AggressivePlusTeam3Clicked();
            }
        });

        button = findViewById(R.id.BlockMinusTeam1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BlockMinusTeam1Clicked();
            }
        });
        button = findViewById(R.id.blockMinusTeam2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blockMinusTeam2Clicked();
            }
        });
        button = findViewById(R.id.blockMinusTeam3);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blockMinusTeam3Clicked();
            }
        });
        button = findViewById(R.id.blockPlusTeam);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blockPlusTeamClicked();
            }
        });
        button = findViewById(R.id.blockPlusTeam2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blockPlusTeam2Clicked();
            }
        });
        button = findViewById(R.id.BlockPlusTeam3);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BlockPlusTeam3Clicked();
            }
        });

        button = findViewById(R.id.EvasiveMinusTeam1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EvasiveMinusTeam1Clicked();
            }
        });
        button = findViewById(R.id.evasiveMinusTeam2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                evasiveMinusTeam2Clicked();
            }
        });
        button = findViewById(R.id.evasiveMinusTeam3);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                evasiveMinusTeam3Clicked();
            }
        });
        button = findViewById(R.id.evasivePlusTeam);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                evasivePlusTeamClicked();
            }
        });
        button = findViewById(R.id.evasivePlusTeam2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                evasivePlusTeam2Clicked();
            }
        });
        button = findViewById(R.id.EvasivePlusTeam3);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EvasivePlusTeam3Clicked();
            }
        });

        button = findViewById(R.id.SturdyMinusTeam1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SturdyMinusTeam1Clicked();
            }
        });
        button = findViewById(R.id.sturdyMinusTeam2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sturdyMinusTeam2Clicked();
            }
        });
        button = findViewById(R.id.sturdyMinusTeam3);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sturdyMinusTeam3Clicked();
            }
        });
        button = findViewById(R.id.sturdyPlusTeam);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sturdyPlusTeamClicked();
            }
        });
        button = findViewById(R.id.sturdyPlusTeam2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sturdyPlusTeam2Clicked();
            }
        });
        button = findViewById(R.id.SturdyPlusTeam3);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SturdyPlusTeam3Clicked();
            }
        });

        button = findViewById(R.id.powerfulPlusTeam);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PowerfulMinusTeam1Clicked();
            }
        });
        button = findViewById(R.id.powerfulPlusTeam2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                powerfulMinusTeam2Clicked();
            }
        });
        button = findViewById(R.id.PowerfulPlusTeam3);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                powerfulMinusTeam3Clicked();
            }
        });
        button = findViewById(R.id.powerfulPlusTeam);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                powerfulPlusTeamClicked();
            }
        });
        button = findViewById(R.id.powerfulPlusTeam2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                powerfulPlusTeam2Clicked();
            }
        });
        button = findViewById(R.id.PowerfulPlusTeam3);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PowerfulPlusTeam3Clicked();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private class RemoteFetcher implements Runnable {

        @Override
        public void run() {
            Message msg = new Message();
            msg.what = START_PROGRESS;
            mFetchHandler.sendMessage(msg);
            try {
                Thread.sleep(500);
            } catch (Exception e) {
            }
            Message msg2 = new Message();
            msg2.what = FETCH_WORDS;
            mFetchHandler.sendMessage(msg2);
            try {
                Thread.sleep(500);
            } catch (Exception e) {
            }
            Message msg3 = new Message();
            msg3.what = FETCH_MATCHES;
            mFetchHandler.sendMessage(msg3);
        }
    }

    private void showProgress() {
//        ProgressBar pb = findViewById(R.id.progressBar_scoutingDataAccess);
//        pb.setVisibility(View.VISIBLE);
    }

    private void fetchWords() {
        String csw_str = CyberScouterWords.getWordsRemote(this);
        if (null != csw_str) {
            updateWords(csw_str);
        }
    }

    private void fetchMatches() {
        CyberScouterConfig cfg = CyberScouterConfig.getConfig(_db);
        if (null != cfg) {
            String csms_str = CyberScouterMatchScoutingL2.getMatchesL2Remote(this, _db, cfg.getEvent_id());
            if (null != csms_str) {
                updateMatchesL2Local(csms_str);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        fetcherThread = null;
    }

    @Override
    protected void onDestroy() {
        if(null != mDbHelper) {
            mDbHelper.close();
        }
        unregisterReceiver(mWordsReceiver);
        unregisterReceiver(mMatchesL2Receiver);
        super.onDestroy();
    }


    protected void updateMatchesL2Local(String json) {
        try {
            CyberScouterConfig cfg = CyberScouterConfig.getConfig(_db);
            if (!json.equalsIgnoreCase("skip")) {
                if (json.equalsIgnoreCase("fetch")) {
                    json = CyberScouterMatchScoutingL2.getWebResponse();
                }
                CyberScouterMatchScoutingL2.deleteOldMatches(_db, cfg.getEvent_id());
                CyberScouterMatchScoutingL2.mergeMatches(_db, json);
                WordCloudFragmentAdapter _wcfAdapter = new WordCloudFragmentAdapter(getSupportFragmentManager());
                PagerAdapter _pagerAdapter = _wcfAdapter;
                mPager.setAdapter(_pagerAdapter);
                mPager.setCurrentItem(_currentPos);
            }

        } catch (Exception e) {
            MessageBox.showMessageBox(WordCloudActivity.getActivity(), "Fetch Match Information Failed", "updateMatchesLocal",
                    String.format("Attempt to fetch match info and merge locally failed!\n%s", e.getMessage()));
            e.printStackTrace();
        }
    }

    private void updateWords(String json) {
        try {
            if (2 < json.length()) {
                CyberScouterWords.setWordsLocal(_db, json);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void FastMinusTeam1Clicked() {
        Button button = findViewById(R.id.FastMinusTeam1);
        if (fastCountT1 > -3) {
            fastCountT1--;
             if (fastCountT1 == -1) {
                 TextView et = findViewById(R.id.fastTextTeam1);
                 et.setTextColor(getColor(R.color.yellow));
            }
             else if (fastCountT1 == -2) {
                 TextView et = findViewById(R.id.fastTextTeam1);
                 et.setTextColor(getColor(R.color.red));
            }
             else if (fastCountT1 == -3) {
                 TextView et = findViewById(R.id.fastTextTeam1);
                 et.setTextColor(getColor(R.color.black));
            }
        }
    }

    private void FastMinusTeam2Clicked() {
        Button button = findViewById(R.id.fastMinusTeam2);
        if (fastCountT2 > -3) {
            fastCountT2--;
            if (fastCountT2 == -1) {
                TextView et = findViewById(R.id.fastText2);
                et.setTextColor(getColor(R.color.yellow));
            }
            else if (fastCountT2 == -2) {
                TextView et = findViewById(R.id.fastText2);
                et.setTextColor(getColor(R.color.red));
            }
            else if (fastCountT2 == -3) {
                TextView et = findViewById(R.id.fastText2);
                et.setTextColor(getColor(R.color.black));
            }
        }
    }

    private void FastMinusTeam3Clicked() {
        Button button = findViewById(R.id.fastMinusTeam3);
        if (fastCountT3 > -3) {
            fastCountT3--;
            if (fastCountT3 == -1) {
                TextView et = findViewById(R.id.fastText3);
                et.setTextColor(getColor(R.color.yellow));
            }
            else if (fastCountT3 == -2) {
                TextView et = findViewById(R.id.fastText3);
                et.setTextColor(getColor(R.color.red));
            }
            else if (fastCountT3 == -3) {
                TextView et = findViewById(R.id.fastText3);
                et.setTextColor(getColor(R.color.black));
            }
        }
    }

    private void fastPlusTeamClicked() {
        Button button = findViewById(R.id.fastPlusTeam);
        if (fastCountT1 < 3) {
            fastCountT1++;
            if (fastCountT1 == 1) {
                TextView et = findViewById(R.id.fastTextTeam1);
                et.setTextColor(getColor(R.color.green));
            }
            else if (fastCountT1 == 2) {
                TextView et = findViewById(R.id.fastTextTeam1);
                et.setTextColor(getColor(R.color.blue));
            }
            else if (fastCountT1 == 3) {
                TextView et = findViewById(R.id.fastTextTeam1);
                et.setTextColor(getColor(R.color.purple));
            }
        }
    }

    private void fastPlusTeam2Clicked() {
        Button button = findViewById(R.id.fastPlusTeam2);
        if (fastCountT2 < 3) {
            fastCountT2++;
            if (fastCountT2 == 1) {
                TextView et = findViewById(R.id.fastText2);
                et.setTextColor(getColor(R.color.green));
            }
            else if (fastCountT2 == 2) {
                TextView et = findViewById(R.id.fastText2);
                et.setTextColor(getColor(R.color.blue));
            }
            else if (fastCountT2 == 3) {
                TextView et = findViewById(R.id.fastText2);
                et.setTextColor(getColor(R.color.purple));
            }
        }
    }

    private void FastPlusTeam3Clicked() {
        Button button = findViewById(R.id.FastPlusTeam3);
        if (fastCountT3 < 3) {
            fastCountT3++;
            if (fastCountT3 == 1) {
                TextView et = findViewById(R.id.fastText3);
                et.setTextColor(getColor(R.color.green));
            }
            else if (fastCountT3 == 2) {
                TextView et = findViewById(R.id.fastText3);
                et.setTextColor(getColor(R.color.blue));
            }
            else if (fastCountT3 == 3) {
                TextView et = findViewById(R.id.fastText3);
                et.setTextColor(getColor(R.color.purple));
            }
        }
    }

    private void GoodDriverMinusTeam1Clicked() {
        Button button = findViewById(R.id.GoodDriverMinusTeam1);
        if (driverCountT1 > -3) {
            driverCountT1--;
            if (driverCountT1 == -1) {
                TextView et = findViewById(R.id.goodDriverTextTeam1);
                et.setTextColor(getColor(R.color.yellow));
            }
            else if (driverCountT1 == -2) {
                TextView et = findViewById(R.id.goodDriverTextTeam1);
                et.setTextColor(getColor(R.color.red));
            }
            else if (driverCountT1 == -3) {
                TextView et = findViewById(R.id.goodDriverTextTeam1);
                et.setTextColor(getColor(R.color.black));
            }
        }
    }

    private void goodDriverMinusTeam2Clicked() {
        Button button = findViewById(R.id.goodDriverMinusTeam2);
        if (driverCountT2 > -3) {
            driverCountT2--;
            if (driverCountT2 == -1) {
                TextView et = findViewById(R.id.goodDriverText2);
                et.setTextColor(getColor(R.color.yellow));
            }
            else if (driverCountT2 == -2) {
                TextView et = findViewById(R.id.goodDriverText2);
                et.setTextColor(getColor(R.color.red));
            }
            else if (driverCountT2 == -3) {
                TextView et = findViewById(R.id.goodDriverText2);
                et.setTextColor(getColor(R.color.black));
            }
        }
    }

    private void goodDriverMinusTeam3Clicked() {
        Button button = findViewById(R.id.goodDriverMinusTeam3);
        if (driverCountT3 > -3) {
            driverCountT3--;
            if (driverCountT3 == -1) {
                TextView et = findViewById(R.id.goodDriverText3);
                et.setTextColor(getColor(R.color.yellow));
            }
            else if (driverCountT3 == -2) {
                TextView et = findViewById(R.id.goodDriverText3);
                et.setTextColor(getColor(R.color.red));
            }
            else if (driverCountT3 == -3) {
                TextView et = findViewById(R.id.goodDriverText3);
                et.setTextColor(getColor(R.color.black));
            }
        }
    }

    private void goodDriverPlusTeamClicked() {
        Button button = findViewById(R.id.goodDriverTeam);
        if (driverCountT1 < 3) {
            driverCountT1++;
            if (driverCountT1 == 1) {
                TextView et = findViewById(R.id.goodDriverTextTeam1);
                et.setTextColor(getColor(R.color.green));
            }
            else if (driverCountT1 == 2) {
                TextView et = findViewById(R.id.goodDriverTextTeam1);
                et.setTextColor(getColor(R.color.blue));
            }
            else if (driverCountT1 == 3) {
                TextView et = findViewById(R.id.goodDriverTextTeam1);
                et.setTextColor(getColor(R.color.purple));
            }
        }
    }

    private void goodDriverPlusTeam2Clicked() {
        Button button = findViewById(R.id.goodDriverTeam2);
        if (driverCountT2 < 3) {
            driverCountT2++;
            if (driverCountT2 == 1) {
                TextView et = findViewById(R.id.goodDriverText2);
                et.setTextColor(getColor(R.color.green));
            }
            else if (driverCountT2 == 2) {
                TextView et = findViewById(R.id.goodDriverText2);
                et.setTextColor(getColor(R.color.blue));
            }
            else if (driverCountT2 == 3) {
                TextView et = findViewById(R.id.goodDriverText2);
                et.setTextColor(getColor(R.color.purple));
            }
        }
    }

    private void GoodDriverPlusTeam3Clicked() {
        Button button = findViewById(R.id.GoodDriverTeam3);
        if (driverCountT3 < 3) {
            driverCountT3++;
            if (driverCountT3 == 1) {
                TextView et = findViewById(R.id.goodDriverText3);
                et.setTextColor(getColor(R.color.green));
            }
            else if (driverCountT3 == 2) {
                TextView et = findViewById(R.id.goodDriverText3);
                et.setTextColor(getColor(R.color.blue));
            }
            else if (driverCountT3 == 3) {
                TextView et = findViewById(R.id.goodDriverText3);
                et.setTextColor(getColor(R.color.purple));
            }
        }
    }

    private void AggressiveMinusTeam1Clicked() {
        Button button = findViewById(R.id.AggessiveMinusTeam1);
        if (aggressiveCountT1 > -3) {
            aggressiveCountT1--;
            if (aggressiveCountT1 == -1) {
                TextView et = findViewById(R.id.aggressiveText);
                et.setTextColor(getColor(R.color.yellow));
            }
            else if (aggressiveCountT1 == -2) {
                TextView et = findViewById(R.id.aggressiveText);
                et.setTextColor(getColor(R.color.red));
            }
            else if (aggressiveCountT1 == -3) {
                TextView et = findViewById(R.id.aggressiveText);
                et.setTextColor(getColor(R.color.black));
            }
        }
    }

    private void aggressiveMinusTeam2Clicked() {
        Button button = findViewById(R.id.aggessiveMinusTeam2);
        if (aggressiveCountT2 > -3) {
            aggressiveCountT2--;
            if (aggressiveCountT2 == -1) {
                TextView et = findViewById(R.id.aggressiveText2);
                et.setTextColor(getColor(R.color.yellow));
            }
            else if (aggressiveCountT2 == -2) {
                TextView et = findViewById(R.id.aggressiveText2);
                et.setTextColor(getColor(R.color.red));
            }
            else if (aggressiveCountT2 == -3) {
                TextView et = findViewById(R.id.aggressiveText2);
                et.setTextColor(getColor(R.color.black));
            }
        }
    }

    private void aggressiveMinusTeam3Clicked() {
        Button button = findViewById(R.id.aggessiveMinusTeam3);
        if (aggressiveCountT3 > -3) {
            aggressiveCountT3--;
            if (aggressiveCountT3 == -1) {
                TextView et = findViewById(R.id.aggressiveText3);
                et.setTextColor(getColor(R.color.yellow));
            }
            else if (aggressiveCountT3 == -2) {
                TextView et = findViewById(R.id.aggressiveText3);
                et.setTextColor(getColor(R.color.red));
            }
            else if (aggressiveCountT3 == -3) {
                TextView et = findViewById(R.id.aggressiveText3);
                et.setTextColor(getColor(R.color.black));
            }
        }
    }

    private void aggressivePlusTeamClicked() {
        Button button = findViewById(R.id.aggressivePlusTeam);
        if (aggressiveCountT1 < 3) {
            aggressiveCountT1++;
            if (aggressiveCountT1 == 1) {
                TextView et = findViewById(R.id.aggressiveText);
                et.setTextColor(getColor(R.color.green));
            }
            else if (aggressiveCountT1 == 2) {
                TextView et = findViewById(R.id.aggressiveText);
                et.setTextColor(getColor(R.color.blue));
            }
            else if (aggressiveCountT1 == 3) {
                TextView et = findViewById(R.id.aggressiveText);
                et.setTextColor(getColor(R.color.purple));
            }
        }
    }

    private void aggressivePlusTeam2Clicked() {
        Button button = findViewById(R.id.aggressivePlusTeam2);
        if (aggressiveCountT2 < 3) {
            aggressiveCountT2++;
            if (aggressiveCountT2 == 1) {
                TextView et = findViewById(R.id.aggressiveText2);
                et.setTextColor(getColor(R.color.green));
            }
            else if (aggressiveCountT2 == 2) {
                TextView et = findViewById(R.id.aggressiveText2);
                et.setTextColor(getColor(R.color.blue));
            }
            else if (aggressiveCountT2 == 3) {
                TextView et = findViewById(R.id.aggressiveText2);
                et.setTextColor(getColor(R.color.purple));
            }
        }
    }

    private void AggressivePlusTeam3Clicked() {
        Button button = findViewById(R.id.AggressivePlusTeam3);
        if (aggressiveCountT3 < 3) {
            aggressiveCountT3++;
            if (aggressiveCountT3 == 1) {
                TextView et = findViewById(R.id.aggressiveText3);
                et.setTextColor(getColor(R.color.green));
            }
            else if (aggressiveCountT3 == 2) {
                TextView et = findViewById(R.id.aggressiveText3);
                et.setTextColor(getColor(R.color.blue));
            }
            else if (aggressiveCountT3 == 3) {
                TextView et = findViewById(R.id.aggressiveText3);
                et.setTextColor(getColor(R.color.purple));
            }
        }
    }

    private void BlockMinusTeam1Clicked() {
        Button button = findViewById(R.id.BlockMinusTeam1);
        if (cargoCountT1 > -3) {
            cargoCountT1--;
            if (cargoCountT1 == -1) {
                TextView et = findViewById(R.id.blockTextTeam1);
                et.setTextColor(getColor(R.color.yellow));
            }
            else if (cargoCountT1 == -2) {
                TextView et = findViewById(R.id.blockTextTeam1);
                et.setTextColor(getColor(R.color.red));
            }
            else if (cargoCountT1 == -3) {
                TextView et = findViewById(R.id.blockTextTeam1);
                et.setTextColor(getColor(R.color.black));
            }
        }
    }

    private void blockMinusTeam2Clicked() {
        Button button = findViewById(R.id.blockMinusTeam2);
        if (cargoCountT2 > -3) {
            cargoCountT2--;
            if (cargoCountT2 == -1) {
                TextView et = findViewById(R.id.blockText2);
                et.setTextColor(getColor(R.color.yellow));
            }
            else if (cargoCountT2 == -2) {
                TextView et = findViewById(R.id.blockTextTeam1);
                et.setTextColor(getColor(R.color.red));
            }
            else if (cargoCountT2 == -3) {
                TextView et = findViewById(R.id.blockTextTeam1);
                et.setTextColor(getColor(R.color.black));
            }
        }
    }

    private void blockMinusTeam3Clicked() {
        Button button = findViewById(R.id.blockMinusTeam3);
        if (cargoCountT3 > -3) {
            cargoCountT3--;
            if (cargoCountT3 == -1) {
                TextView et = findViewById(R.id.blockText3);
                et.setTextColor(getColor(R.color.yellow));
            }
            else if (cargoCountT3 == -2) {
                TextView et = findViewById(R.id.blockText3);
                et.setTextColor(getColor(R.color.red));
            }
            else if (cargoCountT3 == -3) {
                TextView et = findViewById(R.id.blockText3);
                et.setTextColor(getColor(R.color.black));
            }
        }
    }

    private void blockPlusTeamClicked() {
        Button button = findViewById(R.id.blockPlusTeam);
        if (cargoCountT1 < 3) {
            cargoCountT1++;
            if (cargoCountT1 == 1) {
                TextView et = findViewById(R.id.blockTextTeam1);
                et.setTextColor(getColor(R.color.green));
            }
            else if (cargoCountT1 == 2) {
                TextView et = findViewById(R.id.blockTextTeam1);
                et.setTextColor(getColor(R.color.blue));
            }
            else if (cargoCountT1 == 3) {
                TextView et = findViewById(R.id.blockTextTeam1);
                et.setTextColor(getColor(R.color.purple));
            }
        }
    }

    private void blockPlusTeam2Clicked() {
        Button button = findViewById(R.id.blockPlusTeam2);
        if (cargoCountT2 < 3) {
            cargoCountT2++;
            if (cargoCountT2 == 1) {
                TextView et = findViewById(R.id.blockText2);
                et.setTextColor(getColor(R.color.green));
            }
            else if (cargoCountT2 == 2) {
                TextView et = findViewById(R.id.blockText2);
                et.setTextColor(getColor(R.color.blue));
            }
            else if (cargoCountT2 == 3) {
                TextView et = findViewById(R.id.blockText2);
                et.setTextColor(getColor(R.color.purple));
            }
        }
    }

    private void BlockPlusTeam3Clicked() {
        Button button = findViewById(R.id.BlockPlusTeam3);
        if (cargoCountT3 < 3) {
            cargoCountT3++;
            if (cargoCountT3 == 1) {
                TextView et = findViewById(R.id.blockText3);
                et.setTextColor(getColor(R.color.green));
            }
            else if (cargoCountT3 == 2) {
                TextView et = findViewById(R.id.blockText3);
                et.setTextColor(getColor(R.color.blue));
            }
            else if (cargoCountT3 == 3) {
                TextView et = findViewById(R.id.blockText3);
                et.setTextColor(getColor(R.color.purple));
            }
        }
    }

    private void EvasiveMinusTeam1Clicked() {
        Button button = findViewById(R.id.EvasiveMinusTeam1);
        if (evasiveCountT1 > -3) {
            evasiveCountT1--;
            if (evasiveCountT1 == -1) {
                TextView et = findViewById(R.id.evasiveTextTeam1);
                et.setTextColor(getColor(R.color.yellow));
            }
            else if (evasiveCountT1 == -2) {
                TextView et = findViewById(R.id.evasiveTextTeam1);
                et.setTextColor(getColor(R.color.red));
            }
            else if (evasiveCountT1 == -3) {
                TextView et = findViewById(R.id.evasiveTextTeam1);
                et.setTextColor(getColor(R.color.black));
            }
        }
    }

    private void evasiveMinusTeam2Clicked() {
        Button button = findViewById(R.id.evasiveMinusTeam2);
        if (evasiveCountT2 > -3) {
            evasiveCountT2--;
            if (evasiveCountT2 == -1) {
                TextView et = findViewById(R.id.evasiveText2);
                et.setTextColor(getColor(R.color.yellow));
            }
            else if (evasiveCountT2 == -2) {
                TextView et = findViewById(R.id.evasiveText2);
                et.setTextColor(getColor(R.color.red));
            }
            else if (evasiveCountT2 == -3) {
                TextView et = findViewById(R.id.evasiveText2);
                et.setTextColor(getColor(R.color.black));
            }
        }
    }

    private void evasiveMinusTeam3Clicked() {
        Button button = findViewById(R.id.evasiveMinusTeam3);
        if (evasiveCountT3 > -3) {
            evasiveCountT3--;
            if (evasiveCountT3 == -1) {
                TextView et = findViewById(R.id.evasiveText3);
                et.setTextColor(getColor(R.color.yellow));
            }
            else if (evasiveCountT3 == -2) {
                TextView et = findViewById(R.id.evasiveText3);
                et.setTextColor(getColor(R.color.red));
            }
            else if (evasiveCountT3 == -3) {
                TextView et = findViewById(R.id.evasiveText3);
                et.setTextColor(getColor(R.color.black));
            }
        }
    }

    private void evasivePlusTeamClicked() {
        Button button = findViewById(R.id.evasivePlusTeam);
        if (evasiveCountT1 < 3) {
            evasiveCountT1++;
            if (evasiveCountT1 == 1) {
                TextView et = findViewById(R.id.evasiveTextTeam1);
                et.setTextColor(getColor(R.color.green));
            }
            else if (evasiveCountT1 == 2) {
                TextView et = findViewById(R.id.evasiveTextTeam1);
                et.setTextColor(getColor(R.color.blue));
            }
            else if (evasiveCountT1 == 3) {
                TextView et = findViewById(R.id.evasiveTextTeam1);
                et.setTextColor(getColor(R.color.purple));
            }
        }
    }

    private void evasivePlusTeam2Clicked() {
        Button button = findViewById(R.id.evasivePlusTeam2);
        if (evasiveCountT2 < 3) {
            evasiveCountT2++;
            if (evasiveCountT2 == 1) {
                TextView et = findViewById(R.id.evasiveText2);
                et.setTextColor(getColor(R.color.green));
            }
            else if (evasiveCountT2 == 2) {
                TextView et = findViewById(R.id.evasiveText2);
                et.setTextColor(getColor(R.color.blue));
            }
            else if (evasiveCountT2 == 3) {
                TextView et = findViewById(R.id.evasiveText2);
                et.setTextColor(getColor(R.color.purple));
            }
        }
    }

    private void EvasivePlusTeam3Clicked() {
        Button button = findViewById(R.id.EvasivePlusTeam3);
        if (evasiveCountT3 < 3) {
            evasiveCountT3++;
            if (evasiveCountT3 == 1) {
                TextView et = findViewById(R.id.evasiveText3);
                et.setTextColor(getColor(R.color.green));
            }
            else if (evasiveCountT3 == 2) {
                TextView et = findViewById(R.id.evasiveText3);
                et.setTextColor(getColor(R.color.blue));
            }
            else if (evasiveCountT3 == 3) {
                TextView et = findViewById(R.id.evasiveText3);
                et.setTextColor(getColor(R.color.purple));
            }
        }
    }

    private void SturdyMinusTeam1Clicked() {
        Button button = findViewById(R.id.SturdyMinusTeam1);
        if (sturdyCountT1 > -3) {
            sturdyCountT1--;
            if (sturdyCountT1 == -1) {
                TextView et = findViewById(R.id.sturdyTextTeam1);
                et.setTextColor(getColor(R.color.yellow));
            }
            else if (sturdyCountT1 == -2) {
                TextView et = findViewById(R.id.sturdyTextTeam1);
                et.setTextColor(getColor(R.color.red));
            }
            else if (sturdyCountT1 == -3) {
                TextView et = findViewById(R.id.sturdyTextTeam1);
                et.setTextColor(getColor(R.color.black));
            }
        }
    }

    private void sturdyMinusTeam2Clicked() {
        Button button = findViewById(R.id.sturdyMinusTeam2);
        if (sturdyCountT2 > -3) {
            sturdyCountT2--;
            if (sturdyCountT2 == -1) {
                TextView et = findViewById(R.id.sturdyText2);
                et.setTextColor(getColor(R.color.yellow));
            }
            else if (sturdyCountT2 == -2) {
                TextView et = findViewById(R.id.sturdyText2);
                et.setTextColor(getColor(R.color.red));
            }
            else if (sturdyCountT2 == -3) {
                TextView et = findViewById(R.id.sturdyText2);
                et.setTextColor(getColor(R.color.black));
            }
        }
    }

    private void sturdyMinusTeam3Clicked() {
        Button button = findViewById(R.id.sturdyMinusTeam3);
        if (sturdyCountT3 > -3) {
            sturdyCountT3--;
            if (sturdyCountT3 == -1) {
                TextView et = findViewById(R.id.sturdyText3);
                et.setTextColor(getColor(R.color.yellow));
            }
            else if (sturdyCountT3 == -2) {
                TextView et = findViewById(R.id.sturdyText3);
                et.setTextColor(getColor(R.color.red));
            }
            else if (sturdyCountT3 == -3) {
                TextView et = findViewById(R.id.sturdyText3);
                et.setTextColor(getColor(R.color.black));
            }
        }
    }

    private void sturdyPlusTeamClicked() {
        Button button = findViewById(R.id.sturdyPlusTeam);
        if (sturdyCountT1 < 3) {
            sturdyCountT1++;
            if (sturdyCountT1 == 1) {
                TextView et = findViewById(R.id.sturdyTextTeam1);
                et.setTextColor(getColor(R.color.green));
            }
            else if (sturdyCountT1 == 2) {
                TextView et = findViewById(R.id.sturdyTextTeam1);
                et.setTextColor(getColor(R.color.blue));
            }
            else if (sturdyCountT1 == 3) {
                TextView et = findViewById(R.id.sturdyTextTeam1);
                et.setTextColor(getColor(R.color.purple));
            }
        }
    }

    private void sturdyPlusTeam2Clicked() {
        Button button = findViewById(R.id.sturdyPlusTeam2);
        if (sturdyCountT2 < 3) {
            sturdyCountT2++;
            if (sturdyCountT2 == 1) {
                TextView et = findViewById(R.id.sturdyText2);
                et.setTextColor(getColor(R.color.green));
            }
            else if (sturdyCountT2 == 2) {
                TextView et = findViewById(R.id.sturdyText2);
                et.setTextColor(getColor(R.color.blue));
            }
            else if (sturdyCountT2 == 3) {
                TextView et = findViewById(R.id.sturdyText2);
                et.setTextColor(getColor(R.color.purple));
            }
        }
    }

    private void SturdyPlusTeam3Clicked() {
        Button button = findViewById(R.id.SturdyPlusTeam3);
        if (sturdyCountT3 < 3) {
            sturdyCountT3++;
            if (sturdyCountT3 == 1) {
                TextView et = findViewById(R.id.sturdyText3);
                et.setTextColor(getColor(R.color.green));
            }
            else if (sturdyCountT3 == 2) {
                TextView et = findViewById(R.id.sturdyText3);
                et.setTextColor(getColor(R.color.blue));
            }
            else if (sturdyCountT3 == 3) {
                TextView et = findViewById(R.id.sturdyText3);
                et.setTextColor(getColor(R.color.purple));
            }
        }
    }

    private void PowerfulMinusTeam1Clicked() {
        //Button button = findViewById(R.id.PowerulMinusTeam1);
        if (powerfulCountT1 > -3) {
            powerfulCountT1--;
            if (powerfulCountT1 == -1) {
                TextView et = findViewById(R.id.powerfulTextTeam1);
                et.setTextColor(getColor(R.color.yellow));
            }
            else if (powerfulCountT1 == -2) {
                TextView et = findViewById(R.id.powerfulTextTeam1);
                et.setTextColor(getColor(R.color.red));
            }
            else if (powerfulCountT1 == -3) {
                TextView et = findViewById(R.id.powerfulTextTeam1);
                et.setTextColor(getColor(R.color.black));
            }
        }
    }

    private void powerfulMinusTeam2Clicked() {
        //Button button = findViewById(R.id.powerulMinusTeam2);
        if (powerfulCountT2 > -3) {
            powerfulCountT2--;
            if (powerfulCountT2 == -1) {
                TextView et = findViewById(R.id.powerfulText2);
                et.setTextColor(getColor(R.color.yellow));
            }
            else if (powerfulCountT2 == -2) {
                TextView et = findViewById(R.id.powerfulText2);
                et.setTextColor(getColor(R.color.red));
            }
            else if (powerfulCountT2 == -3) {
                TextView et = findViewById(R.id.powerfulText2);
                et.setTextColor(getColor(R.color.black));
            }
        }
    }

    private void powerfulMinusTeam3Clicked() {
        //Button button = findViewById(R.id.PowerulMinusTeam3);
        if (powerfulCountT3 > -3) {
            powerfulCountT3--;
            if (powerfulCountT3 == -1) {
                TextView et = findViewById(R.id.powerfulText3);
                et.setTextColor(getColor(R.color.yellow));
            }
            else if (powerfulCountT3 == -2) {
                TextView et = findViewById(R.id.powerfulText3);
                et.setTextColor(getColor(R.color.red));
            }
            else if (powerfulCountT3 == -3) {
                TextView et = findViewById(R.id.powerfulText3);
                et.setTextColor(getColor(R.color.black));
            }
        }
    }

    private void powerfulPlusTeamClicked() {
        Button button = findViewById(R.id.powerfulPlusTeam);
        if (powerfulCountT1 < 3) {
            powerfulCountT1++;
            if (powerfulCountT1 == 1) {
                TextView et = findViewById(R.id.powerfulTextTeam1);
                et.setTextColor(getColor(R.color.green));
            }
            else if (powerfulCountT1 == 2) {
                TextView et = findViewById(R.id.powerfulTextTeam1);
                et.setTextColor(getColor(R.color.blue));
            }
            else if (powerfulCountT1 == 3) {
                TextView et = findViewById(R.id.powerfulTextTeam1);
                et.setTextColor(getColor(R.color.purple));
            }
        }
    }

    private void powerfulPlusTeam2Clicked() {
        Button button = findViewById(R.id.powerfulPlusTeam2);
        if (powerfulCountT2 < 3) {
            powerfulCountT2++;
            if (powerfulCountT2 == 1) {
                TextView et = findViewById(R.id.powerfulText2);
                et.setTextColor(getColor(R.color.green));
            }
            else if (powerfulCountT2 == 2) {
                TextView et = findViewById(R.id.powerfulText2);
                et.setTextColor(getColor(R.color.blue));
            }
            else if (powerfulCountT2 == 3) {
                TextView et = findViewById(R.id.powerfulText2);
                et.setTextColor(getColor(R.color.purple));
            }
        }
    }

    private void PowerfulPlusTeam3Clicked() {
        Button button = findViewById(R.id.PowerfulPlusTeam3);
        if (powerfulCountT3 < 3) {
            powerfulCountT3++;
            if (powerfulCountT3 == 1) {
                TextView et = findViewById(R.id.powerfulText3);
                et.setTextColor(getColor(R.color.green));
            }
            else if (powerfulCountT3 == 2) {
                TextView et = findViewById(R.id.powerfulText3);
                et.setTextColor(getColor(R.color.blue));
            }
            else if (powerfulCountT3 == 3) {
                TextView et = findViewById(R.id.powerfulText3);
                et.setTextColor(getColor(R.color.purple));
            }
        }
    }

    /* private void updateWordCloudData() {
        CyberScouterConfig cfg = CyberScouterConfig.getConfig(_db);
        try {
            Integer[] _lValues = {};
            CyberScouterMatchScouting.updateMatchMetric(_db, _lColumns, _lValues, cfg);
        } catch(Exception e) {
            e.printStackTrace();
            MessageBox.showMessageBox(this, "Update Error",
                    "WordCloudPage.updateWordCloudData", "SQLite update failed!\n "+e.getMessage());
        }
    } */

}