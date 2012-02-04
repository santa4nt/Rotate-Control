package com.swijaya.android.rotatecontrol;

import android.database.ContentObserver;
import android.os.Handler;
import android.util.Log;


public class SettingsContentObserver extends ContentObserver {

    public interface Delegate {
        public void onChange(boolean selfChange);
    }

    private static final String TAG = SettingsContentObserver.class.getSimpleName();

    private final Delegate mDelegate;

    public SettingsContentObserver(Handler handler, Delegate delegate) {
        super(handler);
        mDelegate = delegate;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        Log.v(TAG, "Settings change detected");
        mDelegate.onChange(selfChange);
    }

}
