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
            Log.e(TAG, e.getLocalizedMessage());
            assert (false);
        }
        return true;
    }

}
