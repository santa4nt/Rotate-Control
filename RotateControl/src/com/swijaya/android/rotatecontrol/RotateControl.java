package com.swijaya.android.rotatecontrol;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;


public class RotateControl implements SettingsContentObserver.Delegate {

    private static final String TAG = RotateControl.class.getSimpleName();

    private final Context mContext;

    private ContentObserver mSettingsContentObserver;
    private boolean mIsAutoRotateEnabled;
    private boolean mIsDirty;

    public RotateControl(Context context) {
        mContext = context;
        mIsAutoRotateEnabled = queryAutoRotateEnabled();
        mIsDirty = false;
    }

    /**
     * Register the context supplied to this object as the system settings'
     * content observer. This will automatically--and more effectively--update
     * RotateControl's knowledge of the current state of the setting(s) it
     * is interested in.
     */
    public synchronized void observeSystemSettings() {
        // set up the content observer
        mIsAutoRotateEnabled = queryAutoRotateEnabled();
        mSettingsContentObserver = new SettingsContentObserver(new Handler(), this);

        // set it as Settings.System's content observer
        ContentResolver contentResolver = mContext.getContentResolver();
        contentResolver.registerContentObserver(Settings.System.CONTENT_URI,
                true, mSettingsContentObserver);
    }

    /**
     * Unregister a content subscriber for the system settings that is
     * associated with the context supplied to this object.
     */
    public synchronized void stopObserveSystemSettings() {
        ContentResolver contentResolver = mContext.getContentResolver();
        contentResolver.unregisterContentObserver(mSettingsContentObserver);
        mSettingsContentObserver = null;
    }

    /**
     * Set whether auto-rotation is enabled or not.
     * 
     * @param enable whether to set auto-rotation

     */
    public synchronized void setAutoRotateEnabled(boolean enable) {
        ContentResolver contentResolver = mContext.getContentResolver();
        String name = Settings.System.ACCELEROMETER_ROTATION;
        int value = enable ? 1 : 0;
        Settings.System.putInt(contentResolver, name, value);
        mIsDirty = true;
    }

    public synchronized boolean isAutoRotateEnabled() {
        if (mIsDirty) {
            mIsAutoRotateEnabled = queryAutoRotateEnabled();
        }
        return mIsAutoRotateEnabled;
    }

    /**
     * Query the system's auto-rotation setting.
     * 
     * @return whether auto-rotation is set
     */
    private boolean queryAutoRotateEnabled() {
        ContentResolver contentResolver = mContext.getContentResolver();
        String name = Settings.System.ACCELEROMETER_ROTATION;
        try {
            return Settings.System.getInt(contentResolver, name) == 1;
        }
        catch (SettingNotFoundException e) {
            Log.wtf(TAG, e.getLocalizedMessage());
            assert (false);
        }
        return true;
    }

    public synchronized void onChange(boolean selfChange) {
        mIsDirty = true;
    }

}
