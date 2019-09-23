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

public class ChatActivity extends SerialActivity {

    private ListView chatView;
    private List<String> chatlst = new ArrayList<>();
    private ArrayAdapter<String> chatAdapter;
    private EditText edtChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        chatView = (ListView) findViewById(R.id.lvChatView);
        chatAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,chatlst);
        chatView.setAdapter(chatAdapter);
        chatAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        chatAdapter.notifyDataSetChanged();
    }

    public void send (View v){
        edtChat = (EditText) findViewById(R.id.edtChat);
        String msg = edtChat.getText().toString();
        if(!msg.equals("")){
            usbService.write(msg.getBytes());
            edtChat.setText("");
        }
    }

    public void processMessage(String data) {
        String BeaconData[] = data.split(";", -1);
        state.setBeaconAttributes(BeaconData);
        chatlst.add(state.getM_message());
        chatAdapter.notifyDataSetChanged();
    }
}
