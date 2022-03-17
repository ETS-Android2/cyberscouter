package com.frcteam195.cyberscouter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.ListView;

public class NamePickerDialog extends DialogFragment {
    public interface NamePickerDialogListener {
        public void onNameSelected(String name, int idx);
    }

    NamePickerDialogListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        CyberScouterDbHelper mDbHelper = null;
        SQLiteDatabase db = null;
        try {
            mDbHelper = new CyberScouterDbHelper(getActivity());
            db = mDbHelper.getWritableDatabase();

            CyberScouterConfig cfg = CyberScouterConfig.getConfig(db);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            String[] names = CyberScouterUsers.getUserNames(db);
            builder.setTitle(R.string.NamePickerTitle)
                    .setItems(names, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            ListView lv = ((AlertDialog) dialog).getListView();
                            String tmp = (String) lv.getItemAtPosition(which);
                            mListener.onNameSelected(tmp, which);
                        }
                    });
            return builder.create();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(db != null) {
                db.close();
            }
            if(mDbHelper != null) {
                mDbHelper.close();
            }
        }
        return null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NamePickerDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(getActivity().toString()
                    + " must implement NamePickerDialogListener");
        }
    }

}