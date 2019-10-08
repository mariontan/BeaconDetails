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
        Toast.makeText(MsgDetailsActivity.this,beaconState.getM_message(),Toast.LENGTH_SHORT).show();

    }
    @Override
    public void onBackPressed(){
        Intent intent = new Intent(MsgDetailsActivity.this,ChatActivity.class);
        startActivity(intent);
        //finish();
    }

}
