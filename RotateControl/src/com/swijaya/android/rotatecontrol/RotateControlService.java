package com.swijaya.android.rotatecontrol;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

public class RotateControlService extends Service implements SettingsContentObserver.Delegate {

    private static final String TAG = RotateControlService.class.getSimpleName();

    private RotateControl mRotateControl;
    private ContentObserver mSettingsContentObserver;
    private boolean mIsAutoRotateEnabled;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.v(TAG, "onCreate");
        mRotateControl = new RotateControl(getApplicationContext());
        mIsAutoRotateEnabled = mRotateControl.isAutoRotateEnabled();
        observeSystemSettings();
    }

    @Override
    public void onDestroy() {
        Log.v(TAG, "onDestroy");
        stopObserveSystemSettings();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(TAG, "onStartCommand:" + intent.getAction());

        // if we get killed, after returning from here, restart
        return START_STICKY;
    }

    /**
     * This will be called by the content observer when it--in turn--was
     * notified by the system that a settings change had taken place.
     */
    public void onChange(boolean selfChange) {
        Log.v(TAG, "Registered settings change");
        boolean isAutoRotateEnabled = mRotateControl.isAutoRotateEnabled();
        if (isAutoRotateEnabled != mIsAutoRotateEnabled) {
            Log.v(TAG, "Auto rotate is now " + (isAutoRotateEnabled ? "enabled" : "disabled"));
            mIsAutoRotateEnabled = isAutoRotateEnabled;
            // send a broadcast intent to tell widgets to redraw
            // TODO
        }
    }

    /**
     * Register the context supplied to this object as the system settings'
     * content observer. This will automatically--and more effectively--update
     * RotateControl's knowledge of the current state of the setting(s) it
     * is interested in.
     */
    private void observeSystemSettings() {
        // set up the content observer
        mSettingsContentObserver = new SettingsContentObserver(new Handler(), this);

        // set it as Settings.System's content observer
        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        contentResolver.registerContentObserver(Settings.System.CONTENT_URI,
                true, mSettingsContentObserver);
    }

    /**
     * Unregister a content subscriber for the system settings that is
     * associated with the context supplied to this object.
     */
    private void stopObserveSystemSettings() {
        if (mSettingsContentObserver != null) {
            ContentResolver contentResolver = getApplicationContext().getContentResolver();
            contentResolver.unregisterContentObserver(mSettingsContentObserver);
            mSettingsContentObserver = null;
        }
    }

}
