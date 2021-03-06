/**
 * Copyright (c) 2012 Santoso Wijaya
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.swijaya.android.rotatecontrol;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

public class RotateControlService extends Service implements SettingsContentObserver.Delegate {

    private static final String TAG = RotateControlService.class.getSimpleName();

    public static final String ACTION_ENABLE = TAG + ".ACTION_ENABLE";
    public static final String ACTION_DISABLE = TAG + ".ACTION_DISABLE";
    public static final String ACTION_TOGGLE = TAG + ".ACTION_TOGGLE";

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
        Log.i(TAG, "Starting service");
        mRotateControl = new RotateControl(getApplicationContext());
        mIsAutoRotateEnabled = mRotateControl.isAutoRotateEnabled();
        // just in case we started out-of-sync with the widget
        notifyWidget(mIsAutoRotateEnabled);
        observeSystemSettings();
    }

    @Override
    public void onDestroy() {
        Log.v(TAG, "onDestroy");
        stopObserveSystemSettings();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            // if our service gets killed for some reason, and the system restarts it,
            // this callback will be provided a null intent
            // see: http://developer.android.com/reference/android/app/Service.html#START_STICKY
            Log.i(TAG, "onStartCommand: null intent");
            // do nothing; the necessary setup has been done in onCreate()
            return START_STICKY;
        }

        String action = intent.getAction();
        Log.v(TAG, "onStartCommand:" + action);

        if (action.equals(ACTION_ENABLE)) {
            // do nothing; we've set things up in onCreate()
        } else if (action.equals(ACTION_DISABLE)) {
            Log.i(TAG, "Shutting down service");
            stopSelf();
        } else if (action.equals(ACTION_TOGGLE)) {
            Log.v(TAG, "Auto rotate setting is currently " + (mIsAutoRotateEnabled ? "enabled" : "disabled"));
            Log.i(TAG, "Toggling auto rotate");
            mRotateControl.setAutoRotateEnabled(!mIsAutoRotateEnabled);
        } else {
            Log.e(TAG, "Unrecognized action: " + action);
            assert (false);
        }

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
            notifyWidget(mIsAutoRotateEnabled);
            // tell the user with a toast
            Toast.makeText(getApplicationContext(),
                    (mIsAutoRotateEnabled ? R.string.autorotate_on : R.string.autorotate_off),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void notifyWidget(boolean isAutoRotateEnabled) {
        Intent intent = new Intent(getApplicationContext(), RotateControlWidgetProvider.class);
        intent.setAction(RotateControlWidgetProvider.ACTION_UPDATE_WITH_STATE);
        intent.putExtra(RotateControlWidgetProvider.EXTRA_AUTOROTATE_ENABLED, isAutoRotateEnabled);
        sendBroadcast(intent);
    }

    /**
     * Register the context supplied to this object as the system settings'
     * content observer. This will automatically--and more effectively--update
     * RotateControl's knowledge of the current state of the setting(s) it
     * is interested in.
     */
    private void observeSystemSettings() {
        Log.v(TAG, "observeSystemSettings");
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
            Log.v(TAG, "stopObserveSystemSettings");
            ContentResolver contentResolver = getApplicationContext().getContentResolver();
            contentResolver.unregisterContentObserver(mSettingsContentObserver);
            mSettingsContentObserver = null;
        }
    }

}
