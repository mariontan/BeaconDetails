package com.aic.beacondetails;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private UsbService usbService;
    private MyHandler myHandler;
    private ListView chatView;
    private List<String> chatlst = new ArrayList<>();
    private ArrayAdapter<String> chatAdapter;
    private BeaconState state = new BeaconState();
    private EditText edtChat;

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String msg = SerialConnector.GetToastMessage(intent.getAction());
            if(msg == null) return;
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }
    };

    private final ServiceConnection usbConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            usbService = ((UsbService.UsbBinder) arg1).getService();
            usbService.setHandler(myHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            usbService = null;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        myHandler = new MyHandler(this);
        chatView = (ListView) findViewById(R.id.lvChatView);
        chatAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,chatlst);
        chatView.setAdapter(chatAdapter);
        chatAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        SerialConnector.setFilters(this,mUsbReceiver);  // Start listening notifications from UsbService
        SerialConnector.startService(this,UsbService.class, usbConnection, null); // Start UsbService(if it was not started before) and Bind it
        chatAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mUsbReceiver);
        unbindService(usbConnection);
    }
    public void send (View v){
        edtChat = (EditText) findViewById(R.id.edtChat);
        String msg = edtChat.getText().toString();
        if(!msg.equals("")){
            usbService.write(msg.getBytes());
            edtChat.setText("");
        }
    }
    private class MyHandler extends Handler {
        private final WeakReference<ChatActivity> mActivity;

        public MyHandler(ChatActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UsbService.MESSAGE_FROM_SERIAL_PORT:
                    String data = msg.obj.toString();
                    String toastMsg = "No message";
                    ChatActivity main = mActivity.get();
                    //main.beaconView.display.append(data);//updates the text box with latest serial data
                    //*
                    try {
                        String BeaconData[] = data.split(";", -1);
                        state.setBeaconAttributes(BeaconData);
                        chatlst.add(state.getM_message());
                        chatAdapter.notifyDataSetChanged();
                        toastMsg = "data length: " + BeaconData.length;
                    } catch (Exception e) {
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
