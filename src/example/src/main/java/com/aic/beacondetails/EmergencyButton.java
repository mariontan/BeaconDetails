package com.aic.beacondetails;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.Set;

public class EmergencyButton extends SerialActivity {

    public void eButton(int ID, final int strID, final SharedPreferences sharedpref){
        Button button = (Button) findViewById(ID);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                state.SetBeaconID(sharedpref,getApplicationContext());
                String data = state.getM_id()+":"+ state.getM_message()+":"+String.valueOf(state.getM_age())+":"+
                        state.getM_gender()+":"+getString(strID);
                if(usbService != null){
                    usbService.write(data.getBytes());
                }
            }
        });
    }
    public void read(View V){
        Intent intent = new Intent(EmergencyButton.this, GPSInfoActivity.class);
        startActivity(intent);
        finish();
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_button);
        final SharedPreferences sharedpref = PreferenceManager.getDefaultSharedPreferences(this);
        eButton(R.id.btn1,R.string.s_btn1,sharedpref);
        eButton(R.id.btn2,R.string.s_btn2,sharedpref);
        eButton(R.id.btn3,R.string.s_btn3,sharedpref);
        eButton(R.id.btn4,R.string.s_btn4,sharedpref);
        eButton(R.id.btn5,R.string.s_btn5,sharedpref);
        eButton(R.id.btn6,R.string.s_btn6,sharedpref);
    }
    @Override
    public void onBackPressed(){
        Intent intent = new Intent(EmergencyButton.this, USER_info.class);
        startActivity(intent);
        finish();
    }

}
