package com.swijaya.android.rotatecontrol;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public class RotateControlWidgetProvider extends AppWidgetProvider {

    private static final String TAG = RotateControlWidgetProvider.class.getSimpleName();

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
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        Log.v(TAG, "onUpdate");
        final int n = appWidgetIds.length;
        // perform update for each widget belonging to this provider
        for (int i = 0; i < n; i++) {
            int appWidgetId = appWidgetIds[i];
            // create an intent to tell the service to toggle
            Intent intent = new Intent(context, RotateControlService.class);
            intent.setAction(RotateControlService.ACTION_TOGGLE);
            PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);
            // get the layout for the app widget, and attach the pending intent
            // to its onClick listener
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
            views.setOnClickPendingIntent(R.id.widget_button, pendingIntent);
            // finally, tell the manager to perform update
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

}
