package com.frcteam195.cyberscouter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

public class MessageBox {

    public static void showMessageBox(Context ctx, String title, String method, String message)
    {
        AlertDialog.Builder messageBox = new AlertDialog.Builder(ctx);
        messageBox.setTitle(title);
        messageBox.setMessage(message + "\n" + "(" + method + ")");
        messageBox.setCancelable(false);
        messageBox.setNeutralButton("OK", null);
        if(null != ctx)
            messageBox.show();
    }

}
