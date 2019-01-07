package com.aic.beacondetails;

import android.app.Activity;
import android.graphics.Color;
import android.widget.EditText;
import android.widget.TextView;

public class BeaconView {

    private TextView gpsLock,msg,dateTime,lat,lon;
    //protected TextView display;
    protected EditText editTextMSG;

    public BeaconView() {

    }

    public BeaconView(Activity activity, int dateTimeID, int latID, int longID, int msgID, int gpsLockID, int editTxtMsgID) {
        InitializeView(activity, dateTimeID, latID, longID, msgID, gpsLockID, editTxtMsgID);
    }

    public void InitializeView(Activity activity, int dateTimeID, int latID, int longID, int msgID, int gpsLockID, int editTxtMsgID) {
        dateTime = (TextView) activity.findViewById(dateTimeID);
        lat = (TextView) activity.findViewById(latID);
        lon = (TextView) activity.findViewById(longID);
        msg = (TextView) activity.findViewById(msgID);
        gpsLock = (TextView) activity.findViewById(gpsLockID);
        editTextMSG = (EditText) activity.findViewById(editTxtMsgID);
    }

    public void SetGPSLock(String message, String gpsDatetime, float lattd, float longh) {

            msg.setText("Recent: " + message);
            gpsLock.setText("good GPS Lock");
            gpsLock.setTextColor(Color.parseColor("#77b800"));
            dateTime.setText(gpsDatetime);
            lat.setText(String.valueOf(lattd));
            lon.setText(String.valueOf(longh));
    }

    public void SetNoGPSLock() {
        msg.setText("no message");
        gpsLock.setText("no GPS Lock, Go out!");
        gpsLock.setTextColor(Color.parseColor("#c90000"));
        dateTime.setText("no date");
        lat.setText("GPS");
        lon.setText("no GPS");
    }

}
