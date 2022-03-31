package com.frcteam195.cyberscouter;

import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

/**
 * A simple {@link DialogFragment} subclass.
 * Use the {@link CommPickerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CommPickerFragment extends DialogFragment {

    public interface CommSelectionDialogListener {
        public void onCommSelected(int choice, String serverIp);
    }

    CommPickerFragment.CommSelectionDialogListener mListener;

    private View _view;
    private CyberScouterDbHelper mDbHelper;
    SQLiteDatabase _db;

    private int _choice;
    private String _serverIp;


    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "choice";
    private static final String ARG_PARAM2 = "serverip";

    public CommPickerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param ch  the communication choice made on the dialog.
     * @param sip the server IP address, if the choice is ethernet.
     * @return A new instance of fragment CommPickerFragment.
     */
    public static CommPickerFragment newInstance(int ch, String sip) {
        CommPickerFragment fragment = new CommPickerFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, ch);
        args.putString(ARG_PARAM2, sip);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_comm_picker, container, false);
        _view = view;

        RadioButton rb1 = _view.findViewById(R.id.radioButton_fcpAws);
        if (_choice == FakeBluetoothServer.COMM.AWS.ordinal()) {
            rb1.setChecked(true);
            setIpField(false);
        }
        rb1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View rview) {
                aws_selected(rview);
            }
        });

        RadioButton rb2 = _view.findViewById(R.id.radioButton_fcpEthernet);
        if (_choice == FakeBluetoothServer.COMM.ETHERNET.ordinal()) {
            rb2.setChecked(true);
            setIpField(true);
        }
        rb2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View rview) {
                ethernet_selected(rview);
            }
        });

        RadioButton rb3 = _view.findViewById(R.id.radioButton_fcpBluetooth);
        if(null == Settings.Secure.getString(getActivity().getContentResolver(), "bluetooth_name")) {
            rb3.setEnabled(false);
            rb3.setChecked(true);
            rb1.setChecked(true);
            setIpField(false);
        } else if (_choice == FakeBluetoothServer.COMM.BLUETOOTH.ordinal()) {
            rb3.setChecked(true);
            setIpField(false);
        }
        rb3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View rview) {
                bluetooth_selected(rview);
            }
        });


        Button btn = _view.findViewById(R.id.button_fcpOk);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss_dialog();
            }
        });

        return (view);
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        super.onDismiss(dialogInterface);
    }

    void dismiss_dialog() {
        EditText et = _view.findViewById(R.id.editText_fcpServerIp);
        _serverIp = et.getText().toString();
        mListener.onCommSelected(_choice, _serverIp);
        this.dismiss();
    }

    void aws_selected(View view) {
        RadioButton rbview = (RadioButton) view;
        boolean checked = rbview.isChecked();
        if (checked) {
            setIpField(false);
            _choice = FakeBluetoothServer.COMM.AWS.ordinal();
            _serverIp = CyberScouterCommSelection.DEFAULT_IP;
        }
    }

    void bluetooth_selected(View view) {
        RadioButton rbview = (RadioButton) view;
        boolean checked = rbview.isChecked();
        if (checked) {
            setIpField(false);
            _choice = FakeBluetoothServer.COMM.BLUETOOTH.ordinal();
            _serverIp = CyberScouterCommSelection.DEFAULT_IP;
        }
    }

    void ethernet_selected(View view) {
        RadioButton rbview = (RadioButton) view;
        boolean checked = rbview.isChecked();
        if (checked) {
            setIpField(true);
            _choice = FakeBluetoothServer.COMM.ETHERNET.ordinal();
        }
    }

    private void setIpField(boolean isEnabled) {
        EditText et = _view.findViewById(R.id.editText_fcpServerIp);
        et.setEnabled(isEnabled);
        if (isEnabled) {
            et.setText(_serverIp, TextView.BufferType.EDITABLE);
        } else {
            et.setText(_serverIp, TextView.BufferType.NORMAL);
        }
        TextView tv = _view.findViewById(R.id.textView_fpcServerAddr);
        tv.setEnabled(isEnabled);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (CommPickerFragment.CommSelectionDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(getActivity().toString()
                    + " must implement CommCelectionDialogListener");
        } catch (Exception ge) {
            ge.printStackTrace();
        }

        if (getArguments() != null) {
            _choice = getArguments().getInt(ARG_PARAM1);
            _serverIp = getArguments().getString(ARG_PARAM2);
        }

    }
}