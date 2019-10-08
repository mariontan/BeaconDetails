package com.aic.beacondetails;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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
    private List<BeaconState> beaconMessages = new ArrayList<>();
    private ArrayAdapter<String> chatAdapter;
    private EditText edtChat;

    private SQLiteDatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        chatView = (ListView) findViewById(R.id.lvChatView);
        chatAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,chatlst);
        chatView.setAdapter(chatAdapter);
        chatAdapter.notifyDataSetChanged();

        db = new SQLiteDatabaseHandler(this);
        beaconMessages = db.allEntries();
        if(beaconMessages != null){
            for(int i = 0; i<beaconMessages.size();i++){
                chatlst.add(beaconMessages.get(i).toString());
            }
        }
        chatView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(ChatActivity.this, beaconMessages.get(position).getM_destid(), Toast.LENGTH_SHORT).show();
                //passing objects https://zocada.com/using-intents-extras-pass-data-activities-android-beginners-guide/
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("MSG",beaconMessages.get(position));
//
//                Intent intent = new Intent(ChatActivity.this,MsgDetailsActivity.class);
//                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        chatAdapter.notifyDataSetChanged();
    }

    public void send (View v){
        edtChat = (EditText) findViewById(R.id.edtChat);
        String msg = state.getM_id()+":"+edtChat.getText().toString();
        if(!msg.equals("")){
            usbService.write(msg.getBytes());
            edtChat.setText("");
            CloseKeyboard();
        }
    }

    public void processMessage(String data) {
        String BeaconData[] = data.split(";", -1);
        state.setBeaconAttributes(BeaconData);
        chatlst.add(state.toString());
        ContentValues entry = db.convertBeaconStateToEntry(state);
        //Toast.makeText(getApplicationContext(), "State message: ["+entry.toString()+"]", Toast.LENGTH_SHORT).show();
        db.addEntry(entry);
        beaconMessages.add(state);
        chatAdapter.notifyDataSetChanged();
    }

}
