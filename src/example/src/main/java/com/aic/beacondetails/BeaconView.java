package com.aic.beacondetails;

import android.app.Activity;
import android.graphics.Color;
import android.widget.EditText;
import android.widget.TextView;

public class BeaconView {

    private TextView gpsLock,msg,time,date,lat,lon;
    protected TextView display;
    protected EditText editTextMSG;

    public BeaconView(){

    }

    public void setView(Activity activity, int displayID, int timeID, int dateID, int latID, int longID, int msgID,int gpsLockID, int editTxtMsgID) {
        display = (TextView) activity.findViewById(displayID);
        time = (TextView) activity.findViewById(timeID);
        date = (TextView) activity.findViewById(dateID);
        lat = (TextView) activity.findViewById(latID);
        lon = (TextView) activity.findViewById(longID);
        msg = (TextView) activity.findViewById(msgID);
        gpsLock = (TextView) activity.findViewById(gpsLockID);
        editTextMSG = (EditText) activity.findViewById(editTxtMsgID);
    }

    public void SetGPSLock(int gpsLocked, String message, String gpsTime, String gpsDate, float lattd, float longh) {

        if(gpsLocked>0){
            msg.setText("Recent: " + message);
            gpsLock.setText("good GPS Lock");
            gpsLock.setTextColor(Color.parseColor("#77b800"));
            time.setText(gpsTime);
            date.setText(gpsDate);
            lat.setText(String.valueOf(lattd));
            lon.setText(String.valueOf(longh));
        }
        else{
            msg.setText("no message");
            gpsLock.setText("no GPS Lock, Go out!");
            gpsLock.setTextColor(Color.parseColor("#c90000"));
            time.setText("no gps Time");
            date.setText("no date");
            lat.setText("GPS");
            lon.setText("no GPS");
        }
    }

}
