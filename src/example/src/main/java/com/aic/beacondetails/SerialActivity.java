package com.aic.beacondetails;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

public abstract class SerialActivity extends AppCompatActivity {

    protected UsbService usbService;
    protected final MyHandler mHandler = new MyHandler(this);
    protected BeaconState state = new BeaconState();

    /*
     * Notifications from UsbService will be received here.
     */
    protected final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String msg = SerialConnector.GetToastMessage(intent.getAction());
            if(msg == null) return;
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }
    };

    protected final ServiceConnection usbConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            usbService = ((UsbService.UsbBinder) arg1).getService();
            usbService.setHandler(mHandler);
            Toast.makeText(getApplicationContext(), "USB Service connected", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            usbService = null;
            Toast.makeText(getApplicationContext(), "USB Service disconnected", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        SerialConnector.setFilters(this,mUsbReceiver);  // Start listening notifications from UsbService
        SerialConnector.startService(this,UsbService.class, usbConnection, null); // Start UsbService(if it was not started before) and Bind it
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mUsbReceiver);
        unbindService(usbConnection);
    }

    public static String getDefaults(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }

    public void processMessage(String data) {}

    protected void CloseKeyboard(){
        View view = this.getCurrentFocus();
        if(view !=null){
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }
    /*
     * This handler will be passed to UsbService. Data received from serial port is displayed through this handler
     */
    protected class MyHandler<MySerialActivity extends SerialActivity> extends Handler {
        private final WeakReference<MySerialActivity> mActivity;

        public MyHandler(MySerialActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {

            String toastMsg = "No message";

            switch (msg.what) {
                case UsbService.MESSAGE_FROM_SERIAL_PORT:
                    String data = msg.obj.toString();

                    //toastMsg = data;
                    try{
                        processMessage(data);
                        toastMsg = data + " is the message. Parsing succeded";
                    }catch (Exception e){
                        toastMsg = data + " is the message. " + e.toString() + "is the error.";
                    }
                    break;
            }

            MySerialActivity main = mActivity.get();
            Toast toast = Toast.makeText(main.getApplicationContext(),
                    msg.what + ":" + toastMsg,
                    Toast.LENGTH_SHORT);

            //toast.show();
        }
    }
}