package com.aic.beacondetails;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class GPSInfoActivity extends SerialActivity {
    protected ListView msgList;
    protected List<String> prevMsg = new ArrayList<String>();
    protected ArrayAdapter<String> msgAdapter;
    protected BeaconView beaconView;// = new BeaconView();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serial);

        beaconView = new BeaconView(this, R.id.textViewDateTime, R.id.textViewLat, R.id.textViewLon,
                R.id.textViewAlt,R.id.textViewHdop,R.id.textViewFixq,R.id.textViewFix,
                R.id.textViewRecentMsg,R.id.textViewLock,R.id.editTextMsg);
        msgList = (ListView) findViewById(R.id.previousMessage);
        msgAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, prevMsg);

        msgList.setAdapter(msgAdapter);
        msgAdapter.notifyDataSetChanged();
        final SharedPreferences sharedpref = PreferenceManager.getDefaultSharedPreferences(this);
        Button sendButton = (Button) findViewById(R.id.buttonSend);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!beaconView.editTextMSG.getText().toString().equals("")) {
                    state.SetBeaconID(sharedpref,getApplicationContext());
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

    @Override
    public void onResume() {
        super.onResume();
        msgAdapter.notifyDataSetChanged();
    }
    @Override
    public void processMessage(String data) {
        String BeaconData[] = data.split(";",-1);
        state.setBeaconAttributes(BeaconData);
        showBeaconData(beaconView, state);
    }
    private void showBeaconData(BeaconView view, BeaconState data){

        view.SetGPSInfo(data.getM_message(), setCorrectTimezone(data.getM_gpsdatetime()),
                RawToDegMin(data.getM_latitude()),RawToDegMin(data.getM_longitude()),
                String.valueOf(data.getM_altitude()),String.valueOf(data.getM_hdop()),
                String.valueOf(data.getM_quality()),String.valueOf(data.getM_fix()));

        view.SetGPSFix(data.getM_fix() > 0);
    }

    private String RawToDegMin(float degrees) {
        int deg = (int) degrees/100;
        float min = degrees - deg*100;
        return String.valueOf(deg!=0?deg:"")+String.valueOf(min);
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
