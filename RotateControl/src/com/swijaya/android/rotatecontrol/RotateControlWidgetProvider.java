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

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public class RotateControlWidgetProvider extends AppWidgetProvider {

    private static final String TAG = RotateControlWidgetProvider.class.getSimpleName();

    public static final String ACTION_UPDATE_WITH_STATE = TAG + ".ACTION_UPDATE_WITH_STATE";
    public static final String EXTRA_AUTOROTATE_ENABLED = TAG + ".EXTRA_AUTOROTATE_ENABLED";

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Log.v(TAG, "onDisabled");

        Intent intent = new Intent(context, RotateControlService.class);
        intent.setAction(RotateControlService.ACTION_DISABLE);
        context.startService(intent);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Log.v(TAG, "onEnabled");

        Intent intent = new Intent(context, RotateControlService.class);
        intent.setAction(RotateControlService.ACTION_ENABLE);
        context.startService(intent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.v(TAG, "onReceive: " + action);
        if (action.equals(ACTION_UPDATE_WITH_STATE)) {
            // we were sent an explicit intent to update this widget with a given state
            boolean isAutoRotateEnabled = intent.getBooleanExtra(EXTRA_AUTOROTATE_ENABLED, true);
            Log.v(TAG, "Updating widget with auto rotate state " +
                    (isAutoRotateEnabled ? "enabled" : "disabled"));
            RemoteViews views = createWidgetWithState(context, isAutoRotateEnabled);
            // now, tell the widget manager to perform update
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName thisWidget = new ComponentName(context, RotateControlWidgetProvider.class);
            appWidgetManager.updateAppWidget(thisWidget, views);
        } else {
            super.onReceive(context, intent);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        Log.v(TAG, "onUpdate");
        final int n = appWidgetIds.length;

        RotateControl rotateControl = new RotateControl(context);
        final boolean isAutoRotateEnabled = rotateControl.isAutoRotateEnabled();

        // perform update for each widget belonging to this provider
        for (int i = 0; i < n; i++) {
            int appWidgetId = appWidgetIds[i];
            // get the layout for the app widget, and attach the pending intent
            // to its onClick listener
            RemoteViews views = createWidgetWithState(context, isAutoRotateEnabled);
            // finally, tell the manager to perform update
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    private RemoteViews createWidgetWithState(Context context, boolean enabled) {
        RemoteViews widgetViews =
                new RemoteViews(context.getPackageName(), R.layout.widget);
        widgetViews.setImageViewResource(R.id.widget_button,
                (enabled ? R.drawable.wg_autorotate_on : R.drawable.wg_autorotate_off));
        PendingIntent pendingIntent = createServiceToggleIntent(context);
        widgetViews.setOnClickPendingIntent(R.id.widget_button, pendingIntent);
        return widgetViews;
    }

    private PendingIntent createServiceToggleIntent(Context context) {
        Intent intent = new Intent(context, RotateControlService.class);
        intent.setAction(RotateControlService.ACTION_TOGGLE);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);
        return pendingIntent;
    }

}
