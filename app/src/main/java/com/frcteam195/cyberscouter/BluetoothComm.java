package com.frcteam195.cyberscouter;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.Lock;


public class BluetoothComm {
    private final static String _serviceUuid = "c3252081-b20b-46df-a9f8-1c3722eadbef";
    private final static String _serviceName = "Team195Pi";
    private final static String _errorJson = "{'result': 'failure', 'msg': 'bluetooth command failed!'}";
    private final static String _successJson = "{'result': 'success', 'msg': 'ping succeeded'}";
    private static boolean bLastBTCommFailed = true;
    private final static Integer OneNineFive = new Integer(195);
    private static int fieldColor = Color.BLUE;

    public final static String ONLINE_STATUS_UPDATED_FILTER = "frcteam195_bluetoothcomm_online_status_updated_intent_filter";

    public static boolean bLastBTCommFailed() {
        return bLastBTCommFailed;
    }

    public static void setLastBTCommFailed() {
        bLastBTCommFailed = true;
    }

    public static void setLastBTCommSucceeded() {
        bLastBTCommFailed = false;
    }

    private String sendCommand(Activity activity, String json) {
        String resp = _errorJson;

        synchronized (OneNineFive) {

            try {
                final BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
                BluetoothAdapter _bluetoothAdapter = bluetoothManager.getAdapter();

                if (null != _bluetoothAdapter && !bLastBTCommFailed) {
                    Set<BluetoothDevice> pairedDevices = _bluetoothAdapter.getBondedDevices();
                    if (pairedDevices.size() > 0) {
                        for (BluetoothDevice device : pairedDevices) {
                            String deviceName = device.getName();
                            String deviceHardwareAddress = device.getAddress();
                            if (deviceName.equals(_serviceName)) {
                                BluetoothSocket mmSocket = device.createRfcommSocketToServiceRecord(UUID.fromString(_serviceUuid));
                                mmSocket.connect();
                                OutputStream mmOutputStream = mmSocket.getOutputStream();
                                InputStream mmInputStream = mmSocket.getInputStream();
                                mmOutputStream.write(json.getBytes());
                                Thread.sleep(1);
                                byte[] ibytes = new byte[mmInputStream.available()];
                                while (ibytes.length == 0) {
                                    Thread.sleep(10);
                                    ibytes = new byte[mmInputStream.available()];
                                }
                                System.out.println(String.format("1. Bytes available: %d", ibytes.length));
                                mmInputStream.read(ibytes);
                                resp = new String(ibytes);
                                if (0x03 != ibytes[ibytes.length - 1]) {
                                    for (int i = 0; i < 500; ++i) {
                                        Thread.sleep(500);
                                        ibytes = new byte[mmInputStream.available()];
                                        if (0 < ibytes.length) {
                                            System.out.println(String.format("%d. Bytes available: %d", i, ibytes.length));
                                            mmInputStream.read(ibytes);
                                            resp = resp.concat(new String(ibytes));
                                            System.out.println(String.format("%da. Return string length = %d", i, resp.length()));
                                            if (0x03 == ibytes[ibytes.length - 1]) {
                                                System.out.println("EOF character received!");
                                                break;
                                            }
                                        }
                                    }
                                }
                                mmOutputStream.close();
                                mmInputStream.close();
                                mmSocket.close();

                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            }
        }

        return resp;
    }

    public String getConfig(AppCompatActivity activity, int last_hash) {
        String returnJson = _errorJson;
        try {
            String btname = Settings.Secure.getString(activity.getContentResolver(), "bluetooth_name");
            if(btname == null) {
                btname = FakeBluetoothServer.default_fakeBluetoothComputerName;
            }
            System.out.println("tablet name is " + btname);
            JSONObject jr = new JSONObject();
            JSONObject j1 = new JSONObject();

            j1.put("computerName", btname);
            jr.put("cmd", "get-config");
            jr.put("payload", j1);
            jr.put("last_hash", last_hash);

            if (FakeBluetoothServer.bUseFakeBluetoothServer) {
                if (!bLastBTCommFailed) {
                    FakeBluetoothServer fbts = new FakeBluetoothServer(btname);
                    fbts.getResponse(activity, jr);
                    returnJson = null;
                }
            } else {
                returnJson = sendCommand(activity, jr.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (returnJson);
    }

    public String getUsers(AppCompatActivity activity, int last_hash) {
        String returnJson = _errorJson;
        try {
            JSONObject jr = new JSONObject();

            jr.put("cmd", "get-users");
            jr.put("last_hash", last_hash);

            if (FakeBluetoothServer.bUseFakeBluetoothServer) {
                if (!bLastBTCommFailed) {
                    String btname = Settings.Secure.getString(activity.getContentResolver(), "bluetooth_name");
                    FakeBluetoothServer fbts = new FakeBluetoothServer(btname);
                    fbts.getResponse(activity, jr);
                    returnJson = null;
                }
            } else {
                returnJson = sendCommand(activity, jr.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (returnJson);
    }

    public String getMatches(AppCompatActivity activity, int eventId, int last_hash) {
        String returnJson = _errorJson;
        try {
            JSONObject jr = new JSONObject();
            JSONObject j1 = new JSONObject();

            j1.put("eventId", eventId);
            jr.put("cmd", "get-matches");
            jr.put("payload", j1);
            jr.put("last_hash", last_hash);

            if (FakeBluetoothServer.bUseFakeBluetoothServer) {
                if (!bLastBTCommFailed) {
                    String btname = Settings.Secure.getString(activity.getContentResolver(), "bluetooth_name");
                    FakeBluetoothServer fbts = new FakeBluetoothServer(btname);
                    fbts.getResponse(activity, jr);
                    returnJson = null;
                }
            } else {
                returnJson = sendCommand(activity, jr.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (returnJson);
    }

    public String getMatchesL2(AppCompatActivity activity, int eventId, int last_hash) {
        String returnJson = _errorJson;
        try {
            JSONObject jr = new JSONObject();
            JSONObject j1 = new JSONObject();

            j1.put("eventId", eventId);
            jr.put("cmd", "get-matches-l2");
            jr.put("payload", j1);
            jr.put("last_hash", last_hash);

            if (FakeBluetoothServer.bUseFakeBluetoothServer) {
                if (!bLastBTCommFailed) {
                    String btname = Settings.Secure.getString(activity.getContentResolver(), "bluetooth_name");
                    FakeBluetoothServer fbts = new FakeBluetoothServer(btname);
                    fbts.getResponse(activity, jr);
                    returnJson = null;
                }
            } else {
                returnJson = sendCommand(activity, jr.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (returnJson);
    }

    public String sendSetCommand(AppCompatActivity activity, JSONObject jo) {
        String returnJson = _errorJson;
        try {
            if (FakeBluetoothServer.bUseFakeBluetoothServer) {
                if (!bLastBTCommFailed) {
                    String btname = Settings.Secure.getString(activity.getContentResolver(), "bluetooth_name");
                    FakeBluetoothServer fbts = new FakeBluetoothServer(btname);
                    fbts.getResponse(activity, jo);
                    returnJson = null;
                }
            } else {
                returnJson = sendCommand(activity, jo.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (returnJson);
    }

    public String getTeams(AppCompatActivity activity, int last_hash) {
        String returnJson = _errorJson;
        try {
            JSONObject jr = new JSONObject();

            jr.put("cmd", "get-teams");
            jr.put("last_hash", last_hash);

            if (FakeBluetoothServer.bUseFakeBluetoothServer) {
                if (!bLastBTCommFailed) {
                    String btname = Settings.Secure.getString(activity.getContentResolver(), "bluetooth_name");
                    FakeBluetoothServer fbts = new FakeBluetoothServer(btname);
                    fbts.getResponse(activity, jr);
                    returnJson = null;
                }
            } else {
                returnJson = sendCommand(activity, jr.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (returnJson);
    }

    public String getWordCloud(AppCompatActivity activity, int last_hash) {
        String returnJson = _errorJson;
        try {
            JSONObject jr = new JSONObject();

            jr.put("cmd", "get-word-cloud");
            jr.put("last_hash", last_hash);

            if (FakeBluetoothServer.bUseFakeBluetoothServer) {
                String btname = Settings.Secure.getString(activity.getContentResolver(), "bluetooth_name");
                FakeBluetoothServer fbts = new FakeBluetoothServer(btname);
                fbts.getResponse(activity, jr);
                returnJson = null;
            } else {
                returnJson = sendCommand(activity, jr.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (returnJson);
    }

    public String getWords(AppCompatActivity activity, int last_hash) {
        String returnJson = _errorJson;
        try {
            JSONObject jr = new JSONObject();

            jr.put("cmd", "get-words");
            jr.put("last_hash", last_hash);

            if (FakeBluetoothServer.bUseFakeBluetoothServer) {
                if (!bLastBTCommFailed) {
                    String btname = Settings.Secure.getString(activity.getContentResolver(), "bluetooth_name");
                    FakeBluetoothServer fbts = new FakeBluetoothServer(btname);
                    fbts.getResponse(activity, jr);
                    returnJson = null;
                }
            } else {
                returnJson = sendCommand(activity, jr.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (returnJson);
    }

    public static void updateStatusIndicator(ImageView iv, int color) {
        Bitmap bitmap = Bitmap.createBitmap(32, 32, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        canvas.drawCircle(16, 16, 12, paint);
        iv.setImageBitmap(bitmap);
    }

    public static int getColor()
    {
        return fieldColor;
    }

    public static boolean pingServer(AppCompatActivity activity) {
        boolean bCommGood = false;

        synchronized (OneNineFive) {

            try {
                final BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
                BluetoothAdapter _bluetoothAdapter = bluetoothManager.getAdapter();

                if (null != _bluetoothAdapter) {
                    Set<BluetoothDevice> pairedDevices = _bluetoothAdapter.getBondedDevices();
                    if (pairedDevices.size() > 0) {
                        for (BluetoothDevice device : pairedDevices) {
                            String deviceName = device.getName();
                            String deviceHardwareAddress = device.getAddress();
                            if (deviceName.equals(_serviceName)) {
                                BluetoothSocket mmSocket = device.createRfcommSocketToServiceRecord(UUID.fromString(_serviceUuid));
                                mmSocket.connect();
                                bCommGood = true;
                                OutputStream mmOutputStream = mmSocket.getOutputStream();
                                mmOutputStream.write(0x03);
                                mmOutputStream.close();
                                mmSocket.close();
                            }
                        }
                    }
                }
            } catch (Exception e) {
//                e.printStackTrace();
            }
        }

        return (bCommGood);
    }
}
