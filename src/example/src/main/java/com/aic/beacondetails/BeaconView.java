package com.aic.beacondetails;

import android.app.Activity;
import android.graphics.Color;
import android.widget.EditText;
import android.widget.TextView;

public class BeaconView {

    private TextView gpsLock,msg,dateTime,lat,lon,alt,hdop,fixq,fix;
    //protected TextView display;
    protected EditText editTextMSG;

    public BeaconView() {

    }

    public BeaconView(Activity activity, int dateTimeID, int latID, int longID,
                      int altID,int hdopID, int fixqID, int fixID,
                      int msgID, int gpsLockID, int editTxtMsgID) {
        InitializeView(activity, dateTimeID, latID, longID, altID,
                        hdopID, fixqID, fixID, msgID, gpsLockID, editTxtMsgID);
    }

    public void InitializeView(Activity activity, int dateTimeID, int latID, int longID,
                               int altID,int hdopID, int fixqID, int fixID,
                               int msgID, int gpsLockID, int editTxtMsgID) {
        dateTime = (TextView) activity.findViewById(dateTimeID);
        lat = (TextView) activity.findViewById(latID);
        lon = (TextView) activity.findViewById(longID);
        alt = (TextView) activity.findViewById(altID);
        hdop = (TextView) activity.findViewById(hdopID);
        fixq = (TextView) activity.findViewById(fixqID);
        fix = (TextView) activity.findViewById(fixID);
        msg = (TextView) activity.findViewById(msgID);
        gpsLock = (TextView) activity.findViewById(gpsLockID);
        editTextMSG = (EditText) activity.findViewById(editTxtMsgID);
    }

    public void SetGPSInfo(String message, String gpsDatetime, String lattd, String longh, String alT, String hdoP, String fixQ, String fiX) {

            msg.setText(message);
            dateTime.setText(gpsDatetime);
            lat.setText(lattd);
            lon.setText(longh);
            alt.setText(alT);
            hdop.setText(hdoP);
            fixq.setText(fixQ);
            fix.setText(fiX);
    }

    public void SetGPSFix(boolean hasFix) {
        gpsLock.setText(hasFix ? "good gps lock" : "no GPS Lock, Go out!");
        gpsLock.setTextColor(hasFix ? Color.parseColor("#77b800"): Color.parseColor("#c90000"));
    }

}
