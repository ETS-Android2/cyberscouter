package com.frcteam195.cyberscouter;

import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import org.json.JSONObject;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Locale;

public class FakeBluetoothServer {

    public static boolean bUseFakeBluetoothServer = false;
    final private static String _webHost = "8zaof0vuah.execute-api.us-east-1.amazonaws.com";
    final public static String webServiceBaseUrl = String.format("https://%s", _webHost);

    final public static String default_fakeBluetoothComputerName = "Team 195 Scout 1A";

    public static String fakeBluetoothComputerName = null;
    //Change the scout number to change which tablet you are emulating, the numbers correspond as follows
    //Scout 1-3 Red, 4-6 Blue, 7-9 Level 2 Scouting, 10 Pit

    private FakeBluetoothServer() {}

    public FakeBluetoothServer(String btname) {
        if(btname == null || btname.toLowerCase(Locale.ROOT).trim().endsWith("a")) {
            bUseFakeBluetoothServer = true;
            if(btname != null) {
                fakeBluetoothComputerName = btname;
            } else {
                fakeBluetoothComputerName = default_fakeBluetoothComputerName;
            }
        } else {
            bUseFakeBluetoothServer = false;
            fakeBluetoothComputerName = btname;
        }
    }

    public void getResponse(AppCompatActivity activity, JSONObject obj) {

        try {
            String cmd = obj.getString("cmd");
            switch (cmd) {
                case "get-config":
                    CyberScouterConfig.getConfigWebService(activity, fakeBluetoothComputerName);
                    break;
                case "get-users":
                    CyberScouterUsers.getUsersWebService(activity);
                    break;
                case "get-matches":
                    JSONObject payload = obj.getJSONObject("payload");
                    int eventId = payload.getInt("eventId");
                    CyberScouterMatchScouting.getMatchesWebService(activity, eventId);
                    break;
                case "get-matches-l2":
                    payload = obj.getJSONObject("payload");
                    eventId = payload.getInt("eventId");
                    CyberScouterMatchScoutingL2.getMatchesL2WebService(activity, eventId);
                    break;
                case "get-matches-all":
                    CyberScouterMatches.getMatchesWebService(activity);
                    break;
                case "get-teams":
                    CyberScouterTeams.getTeamsWebService(activity);
                    break;
                case "get-words":
                    CyberScouterWords.getWordsWebService(activity);
                    break;
                case "get-word-cloud":
                    CyberScouterWordCloud.getWordCloudWebService(activity);
                    break;
                case "put-match-scouting":
                    CyberScouterMatchScouting.setMatchesWebService(activity, obj);
                case "put-teams":
                    CyberScouterTeams.setTeamsWebService(activity, obj);
                    break;
                case "put-word-cloud":
                    CyberScouterWordCloud.setWordCloudWebService(activity, obj);
                    break;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean pingWebHost() {
        try {
            Socket sock = new Socket();
            sock.connect(new InetSocketAddress(_webHost, 443), 900);
            if(sock.isConnected()) {
                sock.close();
                return (true);
            } else {
//                return(false);
                return(true);
            }
        } catch(Exception e) {
//            return(false);
            return(true);
        }
    }

}

