package com.swijaya.android.rotatecontrol;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.util.Log;

public class RotateControlAppWidgetProvider extends AppWidgetProvider {

    private static final String TAG = RotateControlAppWidgetProvider.class.getSimpleName();

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Log.v(TAG, "onDisabled");
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Log.v(TAG, "onEnabled");
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
            int[] appWidgetIds) {
        // TODO Auto-generated method stub
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

}
