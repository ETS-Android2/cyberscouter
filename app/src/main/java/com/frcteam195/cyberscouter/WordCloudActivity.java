package com.frcteam195.cyberscouter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

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

    final private CyberScouterDbHelper mDbHelper = new CyberScouterDbHelper(this);
    private SQLiteDatabase _db = null;
    private int currentMatch = 1;
    private boolean redSelected = true;
    private String team1;
    private String team2;
    private String team3;

    private ViewPager mPager;


    private Integer[] Team1Cntrs = {0, 0, 0, 0, 0, 0, 0};
    final private Integer[] Team1CntrViews = {R.id.fastTextTeam1, R.id.goodDriverTextTeam1, R.id.aggressiveText, R.id.blockTextTeam1, R.id.evasiveTextTeam1, R.id.sturdyTextTeam1, R.id.powerfulTextTeam1};

    private Integer[] Team2Cntrs = {0, 0, 0, 0, 0, 0, 0};
    final private Integer[] Team2CntrViews = {R.id.fastText2, R.id.goodDriverText2, R.id.aggressiveText2, R.id.blockText2, R.id.evasiveText2, R.id.sturdyText2, R.id.powerfulText2};

    private Integer[] Team3Cntrs = {0, 0, 0, 0, 0, 0, 0};
    final private Integer[] Team3CntrViews = {R.id.fastText3, R.id.goodDriverText3, R.id.aggressiveText3, R.id.blockText3, R.id.evasiveText3, R.id.sturdyText3, R.id.powerfulText3};

    static private Handler mFetchHandler;
    private Thread fetcherThread;
    private final int START_PROGRESS = 0;
    private final int FETCH_WORD_CLOUD = 1;

    private int _currentPos;

    BroadcastReceiver mWordCloudReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String ret = intent.getStringExtra("cyberscouterwordcloud");
            if(ret.equalsIgnoreCase("updated")) {
                updateComplete();
            } else {
                updateWordCloudLocal(ret);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_cloud);

        _activity = this;

        registerReceiver(mWordCloudReceiver, new IntentFilter(CyberScouterWordCloud.WORD_CLOUD_UPDATED_FILTER));

        _db = mDbHelper.getWritableDatabase();

        Intent intent = getIntent();
        String sparams = intent.getStringExtra("params");
        try {
            JSONObject jo = new JSONObject(sparams);
            currentMatch = jo.getInt("match_number");
            redSelected = jo.getBoolean("is_red");
        } catch (Exception e) {
            e.printStackTrace();
        }

        fetcherThread = new Thread(new RemoteFetcher());

        mFetchHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case START_PROGRESS:
                        showProgress();
                        break;
                    case FETCH_WORD_CLOUD:
                        fetchWordCloud();
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

        button = findViewById(R.id.powerfulMinusTeam1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PowerfulMinusTeam1Clicked();
            }
        });
        button = findViewById(R.id.powerfulMinusTeam2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                powerfulMinusTeam2Clicked();
            }
        });
        button = findViewById(R.id.powerfulMinusTeam3);
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

        button = findViewById(R.id.btnWcPrevious);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReturnToPreviousPage();
            }
        });

        button = findViewById(R.id.btnWcFinish);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            CyberScouterConfig cfg = CyberScouterConfig.getConfig(_db);
            if (cfg != null) {
                CyberScouterMatches csm = CyberScouterMatches.getLocalMatch(_db, cfg.getEvent_id(), currentMatch);
                if (csm != null) {
                    if (redSelected) {
                        team1 = csm.getRedTeam1();
                        team2 = csm.getRedTeam2();
                        team3 = csm.getRedTeam3();
                    } else {
                        team1 = csm.getBlueTeam1();
                        team2 = csm.getBlueTeam2();
                        team3 = csm.getBlueTeam3();
                    }
                    TextView tv = findViewById(R.id.team1Text);
                    tv.setText(String.format(getString(R.string.tagTeam), team1));
                    tv = findViewById(R.id.team2Text);
                    tv.setText(String.format(getString(R.string.tagTeam), team2));
                    tv = findViewById(R.id.team3Text);
                    tv.setText(String.format(getString(R.string.tagTeam), team3));

                }
            }
        } catch (Exception e) {
            MessageBox.showMessageBox(this, "Fetch Match Information Failed", "onResume",
                    String.format("Attempt to fetch local match info failed!\n%s", e.getMessage()));
            e.printStackTrace();
        }
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
                e.printStackTrace();
            }
            Message msg2 = new Message();
            msg2.what = FETCH_WORD_CLOUD;
            mFetchHandler.sendMessage(msg2);
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showProgress() {
        ProgressBar pb = findViewById(R.id.progressBar_wordCloud);
        pb.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        ProgressBar pb = findViewById(R.id.progressBar_wordCloud);
        pb.setVisibility(View.INVISIBLE);
    }

    private void fetchWordCloud() {
        String csw_str = CyberScouterWordCloud.getWordCloudRemote(this);
        if(null != csw_str) {
            updateWordCloudLocal(csw_str);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        fetcherThread = null;
    }

    @Override
    protected void onDestroy() {
        mDbHelper.close();
        unregisterReceiver(mWordCloudReceiver);
        super.onDestroy();
    }


    protected void updateWordCloudLocal(String json) {
        CyberScouterConfig cfg = CyberScouterConfig.getConfig(_db);
        if (cfg != null) {
            try {
                if (json != null) {
                    if (!json.equalsIgnoreCase("skip")) {
                        if (json.equalsIgnoreCase("fetch")) {
                            json = CyberScouterWordCloud.getWebResponse();
                        }
                    }
                }
                CyberScouterMatches csm = CyberScouterMatches.getLocalMatch(_db, cfg.getEvent_id(), currentMatch);
                CyberScouterWordCloud.deleteOldMatches(_db, csm.getMatchID());
                CyberScouterWordCloud.mergeWordCloudMatchInfo(_db, csm, json, Team1Cntrs.length, redSelected);

                TextView tv;
                CyberScouterWordCloud[] cswcs = CyberScouterWordCloud.getLocalWordCloud(_db, team1, csm.getMatchID());
                for(int i=0; i<cswcs.length; ++i) {
                    Team1Cntrs[cswcs[i].getWordID()-1] = cswcs[i].getWordCount();
                    tv = findViewById(Team1CntrViews[cswcs[i].getWordID()-1]);
                    setTextColor(tv, cswcs[i].getWordCount());
                }

                cswcs = CyberScouterWordCloud.getLocalWordCloud(_db, team2, csm.getMatchID());
                for(int i=0; i<cswcs.length; ++i) {
                    Team2Cntrs[cswcs[i].getWordID()-1] = cswcs[i].getWordCount();
                    tv = findViewById(Team2CntrViews[cswcs[i].getWordID()-1]);
                    setTextColor(tv, cswcs[i].getWordCount());
                }

                cswcs = CyberScouterWordCloud.getLocalWordCloud(_db, team3, csm.getMatchID());
                for(int i=0; i<cswcs.length; ++i) {
                    Team3Cntrs[cswcs[i].getWordID()-1] = cswcs[i].getWordCount();
                    tv = findViewById(Team3CntrViews[cswcs[i].getWordID()-1]);
                    setTextColor(tv, cswcs[i].getWordCount());
                }

                hideProgress();

            } catch (Exception e) {
                MessageBox.showMessageBox(WordCloudActivity.getActivity(), "Fetch Match Information Failed", "updateMatchesLocal",
                        String.format("Attempt to fetch match info and merge locally failed!\n%s", e.getMessage()));
                e.printStackTrace();
            }
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

    private void setTextColor(TextView et, int val) {
        if (val == -1) {
            et.setTextColor(getColor(R.color.yellow));
        } else if (val == -2) {
            et.setTextColor(getColor(R.color.red));
        } else if (val == -3) {
            et.setTextColor(getColor(R.color.black));
        }
        if (val == 0) {
            et.setTextColor(getColor(android.R.color.darker_gray));
        }
        if (val == 1) {
            et.setTextColor(getColor(R.color.green));
        } else if (val == 2) {
            et.setTextColor(getColor(R.color.blue));
        } else if (val == 3) {
            et.setTextColor(getColor(R.color.purple));
        }
    }

    private void FastMinusTeam1Clicked() {
        if (Team1Cntrs[0] > -3) {
            Team1Cntrs[0]--;
            TextView et = findViewById(R.id.fastTextTeam1);
            setTextColor(et, Team1Cntrs[0]);
        }
    }

    private void FastMinusTeam2Clicked() {
        if (Team2Cntrs[0] > -3) {
            Team2Cntrs[0]--;
            TextView et = findViewById(R.id.fastText2);
            setTextColor(et, Team2Cntrs[0]);
        }
    }

    private void FastMinusTeam3Clicked() {
        if (Team3Cntrs[0] > -3) {
            Team3Cntrs[0]--;
            TextView et = findViewById(R.id.fastText3);
            setTextColor(et, Team3Cntrs[0]);
        }
    }

    private void fastPlusTeamClicked() {
        if (Team1Cntrs[0] < 3) {
            Team1Cntrs[0]++;
            TextView et = findViewById(R.id.fastTextTeam1);
            setTextColor(et, Team1Cntrs[0]);
        }
    }

    private void fastPlusTeam2Clicked() {
        if (Team2Cntrs[0] < 3) {
            Team2Cntrs[0]++;
            TextView et = findViewById(R.id.fastText2);
            setTextColor(et, Team2Cntrs[0]);
        }
    }

    private void FastPlusTeam3Clicked() {
        if (Team3Cntrs[0] < 3) {
            Team3Cntrs[0]++;
            TextView et = findViewById(R.id.fastText3);
            setTextColor(et, Team3Cntrs[0]);
        }
    }

    private void GoodDriverMinusTeam1Clicked() {
        if (Team1Cntrs[1] > -3) {
            Team1Cntrs[1]--;
            TextView et = findViewById(R.id.goodDriverTextTeam1);
            setTextColor(et, Team1Cntrs[1]);
        }
    }

    private void goodDriverMinusTeam2Clicked() {
        if (Team2Cntrs[1] > -3) {
            Team2Cntrs[1]--;
            TextView et = findViewById(R.id.goodDriverText2);
            setTextColor(et, Team2Cntrs[1]);
        }
    }

    private void goodDriverMinusTeam3Clicked() {
        if (Team3Cntrs[1] > -3) {
            Team3Cntrs[1]--;
            TextView et = findViewById(R.id.goodDriverText3);
            setTextColor(et, Team3Cntrs[1]);
        }
    }

    private void goodDriverPlusTeamClicked() {
        if (Team1Cntrs[1] < 3) {
            Team1Cntrs[1]++;
            TextView et = findViewById(R.id.goodDriverTextTeam1);
            setTextColor(et, Team1Cntrs[1]);
        }
    }

    private void goodDriverPlusTeam2Clicked() {
        if (Team2Cntrs[1] < 3) {
            Team2Cntrs[1]++;
            TextView et = findViewById(R.id.goodDriverText2);
            setTextColor(et, Team2Cntrs[1]);
        }
    }

    private void GoodDriverPlusTeam3Clicked() {
        if (Team3Cntrs[1] < 3) {
            Team3Cntrs[1]++;
            TextView et = findViewById(R.id.goodDriverText3);
            setTextColor(et, Team3Cntrs[1]);
        }
    }

    private void AggressiveMinusTeam1Clicked() {
        if (Team1Cntrs[2] > -3) {
            Team1Cntrs[2]--;
            TextView et = findViewById(R.id.aggressiveText);
            setTextColor(et, Team1Cntrs[2]);
        }
    }

    private void aggressiveMinusTeam2Clicked() {
        if (Team2Cntrs[2] > -3) {
            Team2Cntrs[2]--;
            TextView et = findViewById(R.id.aggressiveText2);
            setTextColor(et, Team2Cntrs[2]);
        }
    }

    private void aggressiveMinusTeam3Clicked() {
        if (Team3Cntrs[2] > -3) {
            Team3Cntrs[2]--;
            TextView et = findViewById(R.id.aggressiveText3);
            setTextColor(et, Team3Cntrs[2]);
        }
    }

    private void aggressivePlusTeamClicked() {
        if (Team1Cntrs[2] < 3) {
            Team1Cntrs[2]++;
            TextView et = findViewById(R.id.aggressiveText);
            setTextColor(et, Team1Cntrs[2]);
        }
    }

    private void aggressivePlusTeam2Clicked() {
        if (Team2Cntrs[2] < 3) {
            Team2Cntrs[2]++;
            TextView et = findViewById(R.id.aggressiveText2);
            setTextColor(et, Team2Cntrs[2]);
        }
    }

    private void AggressivePlusTeam3Clicked() {
        if (Team3Cntrs[2] < 3) {
            Team3Cntrs[2]++;
            TextView et = findViewById(R.id.aggressiveText3);
            setTextColor(et, Team3Cntrs[2]);
        }
    }

    private void BlockMinusTeam1Clicked() {
        if (Team1Cntrs[3] > -3) {
            Team1Cntrs[3]--;
            TextView et = findViewById(R.id.blockTextTeam1);
            setTextColor(et, Team1Cntrs[3]);
        }
    }

    private void blockMinusTeam2Clicked() {
        if (Team2Cntrs[3] > -3) {
            Team2Cntrs[3]--;
            TextView et = findViewById(R.id.blockText2);
            setTextColor(et, Team2Cntrs[3]);
        }
    }

    private void blockMinusTeam3Clicked() {
        if (Team3Cntrs[3] > -3) {
            Team3Cntrs[3]--;
            TextView et = findViewById(R.id.blockText3);
            setTextColor(et, Team3Cntrs[3]);
        }
    }

    private void blockPlusTeamClicked() {
        if (Team1Cntrs[3] < 3) {
            Team1Cntrs[3]++;
            TextView et = findViewById(R.id.blockTextTeam1);
            setTextColor(et, Team1Cntrs[3]);
        }
    }

    private void blockPlusTeam2Clicked() {
        if (Team2Cntrs[3] < 3) {
            Team2Cntrs[3]++;
            TextView et = findViewById(R.id.blockText2);
            setTextColor(et, Team2Cntrs[3]);
        }
    }

    private void BlockPlusTeam3Clicked() {
        if (Team3Cntrs[3] < 3) {
            Team3Cntrs[3]++;
            TextView et = findViewById(R.id.blockText3);
            setTextColor(et, Team3Cntrs[3]);
        }
    }

    private void EvasiveMinusTeam1Clicked() {
        if (Team1Cntrs[4] > -3) {
            Team1Cntrs[4]--;
            TextView et = findViewById(R.id.evasiveTextTeam1);
            setTextColor(et, Team1Cntrs[4]);
        }
    }

    private void evasiveMinusTeam2Clicked() {
        if (Team2Cntrs[4] > -3) {
            Team2Cntrs[4]--;
            TextView et = findViewById(R.id.evasiveText2);
            setTextColor(et, Team2Cntrs[4]);
        }
    }

    private void evasiveMinusTeam3Clicked() {
        if (Team3Cntrs[4] > -3) {
            Team3Cntrs[4]--;
            TextView et = findViewById(R.id.evasiveText3);
            setTextColor(et, Team3Cntrs[4]);
        }
    }

    private void evasivePlusTeamClicked() {
        if (Team1Cntrs[4] < 3) {
            Team1Cntrs[4]++;
            TextView et = findViewById(R.id.evasiveTextTeam1);
            setTextColor(et, Team1Cntrs[4]);
        }
    }

    private void evasivePlusTeam2Clicked() {
        if (Team2Cntrs[4] < 3) {
            Team2Cntrs[4]++;
            TextView et = findViewById(R.id.evasiveText2);
            setTextColor(et, Team2Cntrs[4]);
        }
    }

    private void EvasivePlusTeam3Clicked() {
        if (Team3Cntrs[4] < 3) {
            Team3Cntrs[4]++;
            TextView et = findViewById(R.id.evasiveText3);
            setTextColor(et, Team3Cntrs[4]);
        }
    }

    private void SturdyMinusTeam1Clicked() {
        if (Team1Cntrs[5] > -3) {
            Team1Cntrs[5]--;
            TextView et = findViewById(R.id.sturdyTextTeam1);
            setTextColor(et, Team1Cntrs[5]);
        }
    }

    private void sturdyMinusTeam2Clicked() {
        if (Team2Cntrs[5] > -3) {
            Team2Cntrs[5]--;
            TextView et = findViewById(R.id.sturdyText2);
            setTextColor(et, Team2Cntrs[5]);
        }
    }

    private void sturdyMinusTeam3Clicked() {
        if (Team3Cntrs[5] > -3) {
            Team3Cntrs[5]--;
            TextView et = findViewById(R.id.sturdyText3);
            setTextColor(et, Team3Cntrs[5]);
        }
    }

    private void sturdyPlusTeamClicked() {
        if (Team1Cntrs[5] < 3) {
            Team1Cntrs[5]++;
            TextView et = findViewById(R.id.sturdyTextTeam1);
            setTextColor(et, Team1Cntrs[5]);
        }
    }

    private void sturdyPlusTeam2Clicked() {
        if (Team2Cntrs[5] < 3) {
            Team2Cntrs[5]++;
            TextView et = findViewById(R.id.sturdyText2);
            setTextColor(et, Team2Cntrs[5]);
        }
    }

    private void SturdyPlusTeam3Clicked() {
        if (Team3Cntrs[5] < 3) {
            Team3Cntrs[5]++;
            TextView et = findViewById(R.id.sturdyText3);
            setTextColor(et, Team3Cntrs[5]);
        }
    }

    private void PowerfulMinusTeam1Clicked() {
        if (Team1Cntrs[6] > -3) {
            Team1Cntrs[6]--;
            TextView et = findViewById(R.id.powerfulTextTeam1);
            setTextColor(et, Team1Cntrs[6]);
        }
    }

    private void powerfulMinusTeam2Clicked() {
        if (Team2Cntrs[6] > -3) {
            Team2Cntrs[6]--;
            TextView et = findViewById(R.id.powerfulText2);
            setTextColor(et, Team2Cntrs[6]);
        }
    }

    private void powerfulMinusTeam3Clicked() {
        if (Team3Cntrs[6] > -3) {
            Team3Cntrs[6]--;
            TextView et = findViewById(R.id.powerfulText3);
            setTextColor(et, Team3Cntrs[6]);
        }
    }

    private void powerfulPlusTeamClicked() {
        if (Team1Cntrs[6] < 3) {
            Team1Cntrs[6]++;
            TextView et = findViewById(R.id.powerfulTextTeam1);
            setTextColor(et, Team1Cntrs[6]);
        }
    }

    private void powerfulPlusTeam2Clicked() {
        if (Team2Cntrs[6] < 3) {
            Team2Cntrs[6]++;
            TextView et = findViewById(R.id.powerfulText2);
            setTextColor(et, Team2Cntrs[6]);
        }
    }

    private void PowerfulPlusTeam3Clicked() {
        if (Team3Cntrs[6] < 3) {
            Team3Cntrs[6]++;
            TextView et = findViewById(R.id.powerfulText3);
            setTextColor(et, Team3Cntrs[6]);
        }
    }

    private void updateWordCloudData() {
        CyberScouterConfig cfg = CyberScouterConfig.getConfig(_db);

        try {
            CyberScouterMatches csm = CyberScouterMatches.getLocalMatch(_db, cfg.getEvent_id(), currentMatch);
            for(int i=0; i<Team1Cntrs.length; ++i) {
                CyberScouterWordCloud.setWordCloudMetric(_db, cfg.getEvent_id(), team1, csm.getMatchID(), i+1, Team1Cntrs[i]);
            }
            for(int i=0; i<Team2Cntrs.length; ++i) {
                CyberScouterWordCloud.setWordCloudMetric(_db, cfg.getEvent_id(), team2, csm.getMatchID(), i+1, Team2Cntrs[i]);
            }
            for(int i=0; i<Team3Cntrs.length; ++i) {
                CyberScouterWordCloud.setWordCloudMetric(_db, cfg.getEvent_id(), team3, csm.getMatchID(), i+1, Team3Cntrs[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
            MessageBox.showMessageBox(this, "Update Error",
                    "WordCloudPage.updateWordCloudData", "SQLite update failed!\n " + e.getMessage());
        }
    }

    private void ReturnToPreviousPage() {
        updateWordCloudData();
        this.finish();
    }


    private void submit() {
        try {
            updateWordCloudData();
            CyberScouterConfig cfg = CyberScouterConfig.getConfig(_db);
            String[] teams = {team1, team2, team3};
            CyberScouterMatches csm = CyberScouterMatches.getLocalMatch(_db, cfg.getEvent_id(), currentMatch);
            CyberScouterWordCloud[] cswca = CyberScouterWordCloud.getLocalWordCloud(_db, teams, csm.getMatchID());
            CyberScouterWordCloud.setWordCloudRemote(this, cfg, cswca);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateComplete(){
        hideProgress();
        this.finish();
    }
}