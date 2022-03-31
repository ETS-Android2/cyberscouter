package com.frcteam195.cyberscouter;

import android.app.Activity;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;


public class EthernetComm {
    private final static String _serverAddress = FakeBluetoothServer.serverIp;
    private final static Integer _serverPort = 60195;
    private final static String _errorJson = "{'result': 'failure', 'msg': 'network command failed!'}";
    private final static String _successJson = "{'result': 'success', 'msg': 'ping succeeded'}";
    private static boolean bLastBTCommFailed = true;
    private final static Integer OneNineFive = 195;

    public String response;
    public boolean pingSucceeded;

    public boolean send_ping(Activity activity) {
        try {
            Thread t = new Thread(new EthernetCommThread(activity, null, this, true));
            t.start();
            Thread.yield();
            t.join();
            Thread.yield();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return(pingSucceeded);
    }

    public String send_cmd(Activity activity, String json) {
        try {
            Thread t = new Thread(new EthernetCommThread(activity, json, this, false));
            t.start();
            t.join();
            System.out.println(String.format("Length of return response is %d", response.length()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return (response);
    }

    private static class EthernetCommThread implements Runnable {
        private final Activity activity;
        private final String json;
        private final EthernetComm pobj;
        private final boolean isPing;

        public EthernetCommThread(Activity _activity, String _json, EthernetComm _pobj, boolean _ping) {
            activity = _activity;
            json = _json;
            pobj = _pobj;
            isPing = _ping;
        }

        public void run() {
            if(isPing) {
                pingServer(pobj);
                return;
            }

            String resp = _errorJson;
            Socket mmSocket = null;

            synchronized (OneNineFive) {

                try {
                    mmSocket = new Socket(_serverAddress, _serverPort);
                    mmSocket.setSoLinger(false, 1);
                    mmSocket.setSoTimeout(30000);
                    OutputStream mmOutputStream = mmSocket.getOutputStream();
                    InputStream mmInputStream = mmSocket.getInputStream();
                    mmOutputStream.write(json.getBytes());
                    Thread.sleep(10);
                    System.out.println("Network bytes written...");
                    byte[] ibytes = new byte[20480];

                    while(mmInputStream.available() == 0) {
                        Thread.sleep(10);
                    }

                    int bytes_read = mmInputStream.read(ibytes);
                    System.out.println(String.format("%d network bytes read...", bytes_read));
                    resp = new String(Arrays.copyOfRange(ibytes, 0, bytes_read));
                    System.out.println(String.format("Response is %s", resp));
                    System.out.println(String.valueOf(ibytes[ibytes.length - 1]));
                    if (3 != ibytes[bytes_read - 1]) {
                        for (int i = 0; i < 50; ++i) {
                            if (0 < ibytes.length) {
                                bytes_read = mmInputStream.read(ibytes);
                                if(0 < bytes_read) {
                                    resp = resp.concat(new String(Arrays.copyOfRange(ibytes, 0, bytes_read)));
                                    System.out.println(String.format("%da. Return string length = %d", i, resp.length()));
                                    if (3 == ibytes[bytes_read - 1]) {
                                        System.out.println("EOD character received!");
                                        break;
                                    }
                                } else {
//                                    break;
                                }
                            }
                        }
                    } else {
                        System.out.println("EOD character received!");
                    }
                    JSONObject jo = new JSONObject();
                    jo.put("cmd", "blurg");
                    String sss = jo.toString();
                    mmOutputStream.write(sss.getBytes());
                    mmOutputStream.flush();
                    Thread.sleep(2000);
                    mmOutputStream.close();
                    mmInputStream.close();
                    mmSocket.close();
                    mmSocket = null;
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (mmSocket != null) {
                        try {
                            mmSocket.close();
                        } catch (Exception deadexp) {
                            deadexp.printStackTrace();
                        }
                    }
                }
            }

            pobj.response = resp;
        }


/*
        public static void updateStatusIndicator(ImageView iv, int color) {
            Bitmap bitmap = Bitmap.createBitmap(32, 32, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(color);
            canvas.drawCircle(16, 16, 12, paint);
            iv.setImageBitmap(bitmap);
        }

*/
        private void pingServer(EthernetComm pobj) {
            boolean bCommGood = false;

            synchronized (OneNineFive) {

                try {
                    Socket mmSocket = new Socket(_serverAddress, _serverPort);
                    mmSocket.setSoLinger(false, 1);
                    mmSocket.setSoTimeout(2000);
                    bCommGood = true;
                    OutputStream mmOutputStream = mmSocket.getOutputStream();
                    mmOutputStream.write(0x03);
                    mmOutputStream.close();
                    mmSocket.close();
                } catch (Exception e) {
//                e.printStackTrace();
                }
            }

            pobj.pingSucceeded = bCommGood;
        }
    }
}
