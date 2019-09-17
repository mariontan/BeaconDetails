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

public class SerialActivity extends AppCompatActivity {

    /*
     * Notifications from UsbService will be received here.
     */
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case UsbService.ACTION_USB_PERMISSION_GRANTED: // USB PERMISSION GRANTED
                    Toast.makeText(context, "USB Ready", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_PERMISSION_NOT_GRANTED: // USB PERMISSION NOT GRANTED
                    Toast.makeText(context, "USB Permission not granted", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_NO_USB: // NO USB CONNECTED
                    Toast.makeText(context, "No USB connected", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_DISCONNECTED: // USB DISCONNECTED
                    Toast.makeText(context, "USB disconnected", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_NOT_SUPPORTED: // USB NOT SUPPORTED
                    Toast.makeText(context, "USB device not supported", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private UsbService usbService;
    private ListView msgList;
    private MyHandler mHandler;
    private List<String> prevMsg = new ArrayList<String>();
    private ArrayAdapter<String> msgAdapter;
    private BeaconState state = new BeaconState();
    private BeaconView beaconView;// = new BeaconView();


    private final ServiceConnection usbConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            usbService = ((UsbService.UsbBinder) arg1).getService();
            usbService.setHandler(mHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            usbService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serial);

        mHandler = new MyHandler(this);
        InitializeAllViews();

        msgList.setAdapter(msgAdapter);
        msgAdapter.notifyDataSetChanged();
        final SharedPreferences sharedpref = PreferenceManager.getDefaultSharedPreferences(this);
        Button sendButton = (Button) findViewById(R.id.buttonSend);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!beaconView.editTextMSG.getText().toString().equals("")) {
                    GetSharedPref(sharedpref);
                    String data =state.getM_id()+":"+ state.getM_message()+":"+String.valueOf(state.getM_age())+":"+state.getM_gender()+":"+beaconView.editTextMSG.getText().toString();
                    if (usbService != null) { // if UsbService was correctly binded, Send data
                        prevMsg.add(data);
                        usbService.write(data.getBytes());
                        msgAdapter.notifyDataSetChanged();
                        beaconView.editTextMSG.setText("");
                        CloseKeyboard();
                    }
                }
            }
        });

    }

    private void InitializeAllViews(){
        beaconView = new BeaconView(this, R.id.textViewDateTime, R.id.textViewLat, R.id.textViewLon,
                                    R.id.textViewAlt,R.id.textViewHdop,R.id.textViewFixq,R.id.textViewFix,
                                    R.id.textViewRecentMsg,R.id.textViewLock,R.id.editTextMsg);
        msgList = (ListView) findViewById(R.id.previousMessage);
        msgAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, prevMsg);
    }

    private void GetSharedPref(SharedPreferences sharedpref){
        state.setM_id(sharedpref.getString(getString(R.string.BD),""));
        state.setM_message(sharedpref.getString(getString(R.string.name),""));
        state.setM_age(sharedpref.getInt(getString(R.string.age),0));
        state.setM_gender(sharedpref.getString(getString(R.string.gender),""));
    }

    private void CloseKeyboard(){
        View view = this.getCurrentFocus();
        if(view !=null){
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        setFilters();  // Start listening notifications from UsbService
        startService(UsbService.class, usbConnection, null); // Start UsbService(if it was not started before) and Bind it
        msgAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mUsbReceiver);
        unbindService(usbConnection);
    }

    private void startService(Class<?> service, ServiceConnection serviceConnection, Bundle extras) {
        if (!UsbService.SERVICE_CONNECTED) {
            Intent startService = new Intent(this, service);
            if (extras != null && !extras.isEmpty()) {
                Set<String> keys = extras.keySet();
                for (String key : keys) {
                    String extra = extras.getString(key);
                    startService.putExtra(key, extra);
                }
            }
            startService(startService);
        }
        Intent bindingIntent = new Intent(this, service);
        bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void setFilters() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbService.ACTION_USB_PERMISSION_GRANTED);
        filter.addAction(UsbService.ACTION_NO_USB);
        filter.addAction(UsbService.ACTION_USB_DISCONNECTED);
        filter.addAction(UsbService.ACTION_USB_NOT_SUPPORTED);
        filter.addAction(UsbService.ACTION_USB_PERMISSION_NOT_GRANTED);
        registerReceiver(mUsbReceiver, filter);
    }

    public static String getDefaults(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }
    /*
     * This handler will be passed to UsbService. Data received from serial port is displayed through this handler
     */
    private class MyHandler extends Handler {
        private final WeakReference<SerialActivity> mActivity;

        public MyHandler(SerialActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UsbService.MESSAGE_FROM_SERIAL_PORT:
                    String data = msg.obj.toString();
                    String toastMsg = "No message";
                    SerialActivity main = mActivity.get();
                    //main.beaconView.display.append(data);//updates the text box with latest serial data
                    //*
                    try{
                        String BeaconData[] = data.split(";",-1);
                        state.setBeaconAttributes(BeaconData);
                        getBeaconData();
                        toastMsg = "data length: "+ BeaconData.length;
                    }catch (Exception e){
                        toastMsg = data + " is the message. " + e.toString() + "is the error.";
                    }

                    Toast toast = Toast.makeText(main.getApplicationContext(),
                            toastMsg,
                            Toast.LENGTH_SHORT);

                    //toast.show();
                    break;
            }
        }
        private void getBeaconData(){

            beaconView.SetGPSInfo(state.getM_message(), setCorrectTimezone(state.getM_gpsdatetime()),
                    RawToDegMin(state.getM_latitude()),RawToDegMin(state.getM_longitude()),
                    String.valueOf(state.getM_altitude()),String.valueOf(state.getM_hdop()),
                    String.valueOf(state.getM_quality()),String.valueOf(state.getM_fix()));

            beaconView.SetGPSFix(state.getM_fix() > 0);
        }

        private String RawToDegMin(float degrees) {
            int deg = (int) degrees/100;
            float min = degrees - deg*100;
            return String.valueOf(deg)+" "+String.valueOf(min);
        }

        private String  setCorrectTimezone(String dateTime){
            SimpleDateFormat sourceFormat = new SimpleDateFormat("ddMMyy,HHmmss") ;
            SimpleDateFormat destFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            TimeZone tz = TimeZone.getTimeZone("Asia/Manila");
            Date parsed = new Date();

            sourceFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            try {
                 parsed = sourceFormat.parse(dateTime);

            } catch (ParseException e) {
                e.printStackTrace();
            }
            destFormat.setTimeZone(tz);

            return destFormat.format(parsed);
        }
    }
}