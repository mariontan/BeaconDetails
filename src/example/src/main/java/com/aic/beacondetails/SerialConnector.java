package com.aic.beacondetails;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import android.os.Handler;

import java.util.Set;

public class SerialConnector {



    public static String GetToastMessage(String action) {
        switch (action) {
            case UsbService.ACTION_USB_PERMISSION_GRANTED: // USB PERMISSION GRANTED
                return "USB Ready";
            case UsbService.ACTION_USB_PERMISSION_NOT_GRANTED: // USB PERMISSION NOT GRANTED
                return "USB Permission not granted";
            case UsbService.ACTION_NO_USB: // NO USB CONNECTED
                return "No USB connected";
            case UsbService.ACTION_USB_DISCONNECTED: // USB DISCONNECTED
                return "USB disconnected";
            case UsbService.ACTION_USB_NOT_SUPPORTED: // USB NOT SUPPORTED
                return "USB device not supported";
        }
        return null;
    }

    private static UsbService m_usbService;
    private static Handler m_handler;

    public static ServiceConnection getUsbConnection(UsbService p_usbService, Handler p_handler){
        m_usbService = p_usbService;
        m_handler = p_handler;
         return new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName arg0, IBinder arg1) {
                m_usbService = ((UsbService.UsbBinder) arg1).getService();
                m_usbService.setHandler(m_handler);
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
                m_usbService = null;
            }
        };
    }

    public static void startService(Activity activity, Class<?> service, ServiceConnection serviceConnection, Bundle extras) {
        if (!UsbService.SERVICE_CONNECTED) {
            Intent startService = new Intent(activity, service);
            if (extras != null && !extras.isEmpty()) {
                Set<String> keys = extras.keySet();
                for (String key : keys) {
                    String extra = extras.getString(key);
                    startService.putExtra(key, extra);
                }
            }
            activity.startService(startService);
        }
        Intent bindingIntent = new Intent(activity, service);
        activity.bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }
    public static void setFilters(Activity activity,BroadcastReceiver p_usbReceiver) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbService.ACTION_USB_PERMISSION_GRANTED);
        filter.addAction(UsbService.ACTION_NO_USB);
        filter.addAction(UsbService.ACTION_USB_DISCONNECTED);
        filter.addAction(UsbService.ACTION_USB_NOT_SUPPORTED);
        filter.addAction(UsbService.ACTION_USB_PERMISSION_NOT_GRANTED);
        activity.registerReceiver(p_usbReceiver, filter);
    }
}
