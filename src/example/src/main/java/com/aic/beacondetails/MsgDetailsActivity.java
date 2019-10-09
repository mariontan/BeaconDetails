package com.aic.beacondetails;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MsgDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg_details);
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        BeaconState beaconState = (BeaconState) bundle.getSerializable("MSG");
        //Toast.makeText(MsgDetailsActivity.this,beaconState.getM_message(),Toast.LENGTH_SHORT).show();
        BeaconView beaconView = new BeaconView(this, R.id.textViewDateTime, R.id.textViewLat, R.id.textViewLon,
                R.id.textViewAlt,R.id.textViewHdop,R.id.textViewFixq,R.id.textViewFix,
                R.id.textViewRecentMsg,R.id.textViewLock,R.id.editTextMsg);
        beaconView.SetGPSInfo(beaconState.getM_message(),beaconState.getM_gpsdatetime(),String.valueOf(beaconState.getM_latitude()),
              String.valueOf(beaconState.getM_longitude()),String.valueOf(beaconState.getM_altitude()),String.valueOf(beaconState.getM_hdop()),
              String.valueOf(beaconState.getM_fix()),String.valueOf(beaconState.getM_quality()));
    }
    @Override
    public void onBackPressed(){
        Intent intent = new Intent(MsgDetailsActivity.this,ChatActivity.class);
        startActivity(intent);
        finish();
    }

}
