package com.felhr.serialportexample;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

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
    private TextView gpsLock,msg,display;
    private EditText editText;
    private ListView msgList;
    private MyHandler mHandler;
    private List<String> prevMsg = new ArrayList<String>();
    private ArrayAdapter<String> msgAdapter;
    private BeaconState state = new BeaconState();


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
        setContentView(R.layout.activity_main);

        mHandler = new MyHandler(this);

        display = (TextView) findViewById(R.id.textView1);
        editText = (EditText) findViewById(R.id.editText1);
        msgList = (ListView) findViewById(R.id.previousMessage);
        msgAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, prevMsg);

        display.setMovementMethod(new ScrollingMovementMethod());
        msgList.setAdapter(msgAdapter);
        msgAdapter.notifyDataSetChanged();
        final SharedPreferences sharedpref = PreferenceManager.getDefaultSharedPreferences(this);
        Button sendButton = (Button) findViewById(R.id.buttonSend);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editText.getText().toString().equals("")) {
                    state.setM_id(sharedpref.getString(getString(R.string.BD),""));
                    state.setM_message(sharedpref.getString(getString(R.string.name),""));
                    state.setM_age(sharedpref.getInt(getString(R.string.age),0));
                    state.setM_gender(sharedpref.getString(getString(R.string.gender),""));
                    String data =state.getM_id()+":"+ state.getM_message()+":"+String.valueOf(state.getM_age())+":"+state.getM_gender()+":"+editText.getText().toString();
                    if (usbService != null) { // if UsbService was correctly binded, Send data
                        prevMsg.add(data);
                        usbService.write(data.getBytes());
                        msgAdapter.notifyDataSetChanged();
                        editText.setText("");
                        closeKeyboard();
                    }
                }
            }
        });

    }
    private void closeKeyboard(){
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
    private static class MyHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;

        public MyHandler(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UsbService.MESSAGE_FROM_SERIAL_PORT:
                    String data = (String) msg.obj;
                    String toastMsg = "No message";
                    MainActivity main = mActivity.get();
                    //updating message
                    main.msg = (TextView) main.findViewById(R.id.textViewRecentMsg);
                    main.gpsLock = (TextView) main.findViewById(R.id.textViewLock);
                    main.display.append(data);//updates the text box with latest serial data

                    try{
                        String BeaconData[] = data.split(",",-1);
                        main.state.setM_message(BeaconData[BeaconData.length-1]);
                        main.state.setM_fix(Integer.parseInt(BeaconData[BeaconData.length-2]));
                        if(main.state.getM_fix()>0){
                            main.msg.setText("Recent: "+main.state.getM_message());
                            main.gpsLock.setText("good GPS Lock");
                            main.gpsLock.setTextColor(Color.parseColor("#77b800"));
                        }
                        else{
                            main.msg.setText("no message");
                            main.gpsLock.setText("no GPS Lock, Go out!");
                            main.gpsLock.setTextColor(Color.parseColor("#c90000"));
                        }
                        toastMsg = BeaconData[BeaconData.length-1];
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
    }
}