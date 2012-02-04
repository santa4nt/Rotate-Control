package com.swijaya.android.rotatecontrol;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;


public class RotateControl {

    private static final String TAG = RotateControl.class.getSimpleName();

    private final Context mContext;

    public RotateControl(Context context) {
        mContext = context;
    }

    /**
     * Set whether auto-rotation is enabled or not.
     * 
     * @param enable whether to set auto-rotation

     */
    public void setAutoRotateEnabled(boolean enable) {
        ContentResolver contentResolver = mContext.getContentResolver();
        String name = Settings.System.ACCELEROMETER_ROTATION;
        int value = enable ? 1 : 0;
        Settings.System.putInt(contentResolver, name, value);
    }

    public boolean isAutoRotateEnabled() {
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

}
