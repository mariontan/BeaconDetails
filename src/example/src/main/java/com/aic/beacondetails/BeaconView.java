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

    public void SetGPSInfo(String message, String gpsDatetime, String lattd, String longh) {

            msg.setText(message);
            dateTime.setText(gpsDatetime);
            lat.setText(lattd);
            lon.setText(longh);
    }

    public void SetGPSFix(boolean hasFix) {
        gpsLock.setText(hasFix ? "good gps lock" : "no GPS Lock, Go out!");
        gpsLock.setTextColor(hasFix ? Color.parseColor("#77b800"): Color.parseColor("#c90000"));
    }

}
