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
        List<BeaconState> beaconMessages = db.allEntries();
        if(beaconMessages != null){
            for(int i = 0; i<beaconMessages.size();i++){
                chatlst.add(beaconMessages.get(i).toString());
            }
        }
        // create some players
//        Player player1 = new Player(1, "Lebron James", "F", 203);
//        Player player2 = new Player(2, "Kevin Durant", "F", 208);
//        Player player3 = new Player(3, "Rudy Gobert", "C", 214);
        // add them
//        db.addPlayer(player1);
//        db.addPlayer(player2);
//        db.addPlayer(player3);
//        // list all players
//        List<Player> players = db.allPlayers();
//
//        if (players != null) {
//            String[] itemsNames = new String[players.size()];
//
//            for (int i = 0; i < players.size(); i++) {
//                itemsNames[i] = players.get(i).toString();
//            }
//
//            // display like string instances
////            ListView list = (ListView) findViewById(R.id.lvChatView);
////            list.setAdapter(new ArrayAdapter<String>(this,
////            android.R.layout.simple_list_item_1, android.R.id.text1, itemsNames));
////            chatAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,itemsNames);
////            chatView.setAdapter(chatAdapter);
////            chatAdapter.notifyDataSetChanged();
//
//        }
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
            CloseKeyboard();
        }
    }

    public void processMessage(String data) {
        String BeaconData[] = data.split(";", -1);
        state.setBeaconAttributes(BeaconData);
        chatlst.add(state.toString());
        ContentValues entry = db.convertBeaconStateToEntry(state);
        Toast.makeText(getApplicationContext(), "State message: ["+entry.toString()+"]", Toast.LENGTH_SHORT).show();

        db.addEntry(entry);
        chatAdapter.notifyDataSetChanged();
    }

}
