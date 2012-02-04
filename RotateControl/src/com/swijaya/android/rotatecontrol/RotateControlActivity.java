package com.swijaya.android.rotatecontrol;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class RotateControlActivity extends Activity {

    private static final String TAG = RotateControlActivity.class.getSimpleName();

    private RotateControl mRotateControl;
    private boolean mIsAutoRotateEnabled;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mRotateControl = new RotateControl(this);
        mIsAutoRotateEnabled = mRotateControl.isAutoRotateEnabled();

        Button button = (Button) findViewById(R.id.toggle_button);
        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                mRotateControl.setAutoRotateEnabled(!mIsAutoRotateEnabled);
                boolean success = mRotateControl.isAutoRotateEnabled() != mIsAutoRotateEnabled;
                if (!success) {
                    Log.e(TAG, "Cannot set auto rotate!");
                } else {
                    mIsAutoRotateEnabled = !mIsAutoRotateEnabled;
                }

                Toast.makeText(RotateControlActivity.this,
                        (mIsAutoRotateEnabled ? R.string.autorotate_on : R.string.autorotate_off),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}