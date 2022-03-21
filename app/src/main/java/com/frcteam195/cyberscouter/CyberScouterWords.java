package com.frcteam195.cyberscouter;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Vector;

import cz.msebera.android.httpclient.Header;

public class CyberScouterWords {
    final static String WORDS_UPDATED_FILTER = "frcteam195_cyberscouterwords_words_updated_intent_filter";

    private int WordID;
    private String Word;
    private int DisplayWordOrder;

    void putWord(SQLiteDatabase db) {
        try {
            ContentValues values = new ContentValues();

            values.put(CyberScouterContract.Words.COLUMN_NAME_WORD_ID, WordID);
            values.put(CyberScouterContract.Words.COLUMN_NAME_WORD, Word);
            values.put(CyberScouterContract.Words.COLUMN_NAME_DISPLAY_WORD_ORDER, DisplayWordOrder);

            long newRowId = db.insert(CyberScouterContract.Words.TABLE_NAME,
                    null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void setWordsLocal(SQLiteDatabase db, String words_json) {
        try {
            JSONArray ja = new JSONArray(words_json);
            for(int i=0 ; i<ja.length(); ++i) {
                JSONObject jo = ja.getJSONObject(i);
                ContentValues values = new ContentValues();

                values.put(CyberScouterContract.Words.COLUMN_NAME_WORD_ID, jo.getInt(CyberScouterContract.Words.COLUMN_NAME_WORD_ID));
                values.put(CyberScouterContract.Words.COLUMN_NAME_WORD, jo.getString(CyberScouterContract.Words.COLUMN_NAME_WORD));
                values.put(CyberScouterContract.Words.COLUMN_NAME_DISPLAY_WORD_ORDER, jo.getInt(CyberScouterContract.Words.COLUMN_NAME_DISPLAY_WORD_ORDER));

                long newRowId = db.insertWithOnConflict(CyberScouterContract.Words.TABLE_NAME,
                        null, values, SQLiteDatabase.CONFLICT_REPLACE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static String getWordsRemote(AppCompatActivity activity) {
        String ret = null;

        try {
            BluetoothComm btcomm = new BluetoothComm();
            String response = btcomm.getWords(activity, 0);
            if (null != response) {
                JSONObject jo = new JSONObject(response);
                String result = jo.getString("result");
                if ("failure" != result) {
                    if(result.equalsIgnoreCase("skip")) {
                        ret = "skip";
                    } else {
                        JSONArray payload = jo.getJSONArray("payload");
                        ret = payload.toString();
                    }
                } else {
                    ret = "skip";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return (ret);
    }

    static public void getWordsWebService(final AppCompatActivity activity) {
        String url = String.format("%s/words", FakeBluetoothServer.webServiceBaseUrl);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                System.out.println("Starting get call...");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                Intent i = new Intent(WORDS_UPDATED_FILTER);
                i.putExtra("cyberscouterwords", new String(response));
                activity.sendBroadcast(i);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onRetry(int retryNo) {
                System.out.println(String.format("Retry number %d", retryNo));
            }
        });
    }

    public static void setDoneScouting(SQLiteDatabase db) {
        try {
            ContentValues values = new ContentValues();

            values.put(CyberScouterContract.WordCloud.COLUMN_NAME_DONE_SCOUTING, 1);
            values.put(CyberScouterContract.WordCloud.COLUMN_NAME_UPLOAD_STATUS, UploadStatus.READY_TO_UPLOAD);

            long newRowId = db.update(CyberScouterContract.WordCloud.TABLE_NAME, values, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    static CyberScouterWords[] getLocalWords(SQLiteDatabase db) {
        Cursor cursor = null;

        Vector<CyberScouterWords> cswv = new Vector<CyberScouterWords>();
        CyberScouterWords[] cswa = null;
        CyberScouterWords csw = null;

        String[] projection = {
                CyberScouterContract.Words.COLUMN_NAME_WORD_ID,
                CyberScouterContract.Words.COLUMN_NAME_WORD,
                CyberScouterContract.Words.COLUMN_NAME_DISPLAY_WORD_ORDER
        };

        String sortOrder =
                CyberScouterContract.Words.COLUMN_NAME_DISPLAY_WORD_ORDER + " ASC";

        cursor = db.query(
                CyberScouterContract.Words.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );

        if (0 < cursor.getCount()) {
            while (cursor.moveToNext()) {
                csw = new CyberScouterWords();

                csw.WordID = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.Words.COLUMN_NAME_WORD_ID));
                csw.Word = cursor.getString(cursor.getColumnIndex(CyberScouterContract.Words.COLUMN_NAME_WORD));
                csw.DisplayWordOrder = cursor.getInt(cursor.getColumnIndex(CyberScouterContract.Words.COLUMN_NAME_DISPLAY_WORD_ORDER));

                cswv.add(csw);
            }

            cswa = new CyberScouterWords[cswv.size()];
            return (cswv.toArray(cswa));
        }

        return cswa;
    }

    public int getWordID() {
        return WordID;
    }

    public String getWord() {
        return Word;
    }

    public int getDisplayWordOrder() {
        return DisplayWordOrder;
    }
}
