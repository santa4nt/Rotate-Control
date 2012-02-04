package com.swijaya.android.rotatecontrol;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class RotateControlService extends Service implements SettingsContentObserver.Delegate {

    private static final String TAG = RotateControlService.class.getSimpleName();

    private RotateControl mRotateControl;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.v(TAG, "onCreate");
        mRotateControl = new RotateControl(getApplicationContext());
        mRotateControl.observeSystemSettings(this);
    }

    @Override
    public void onDestroy() {
        Log.v(TAG, "onDestroy");
        mRotateControl.stopObserveSystemSettings();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(TAG, "onStartCommand:" + intent.getAction());

        // if we get killed, after returning from here, restart
        return START_STICKY;
    }

    public void onChange(boolean selfChange) {
        Log.v(TAG, "Registered settings change");
        // send a broadcast intent to tell widgets to redraw
        // TODO
    }

}
