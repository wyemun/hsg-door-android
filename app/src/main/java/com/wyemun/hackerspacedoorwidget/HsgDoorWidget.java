/*
 * Copyright 2015 Wyemun (wyemun@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wyemun.hackerspacedoorwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.wyemun.hackerspacedoorwidget.configs.AppConfig;

import java.io.IOException;

/**
 * Created by wyemun@gmail.com on 15/01/2015.
 */
public class HsgDoorWidget extends AppWidgetProvider {

    private static final String ACTION_OPEN_DOOR = "com.wyemun.hackerspacedoorwidget.opendoor";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        for (int appWidgetId : appWidgetIds) {
            drawWidget(context, appWidgetId);
        }
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        String action = intent.getAction();

        if (ACTION_OPEN_DOOR.equals(action)) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            String doorPin = sp.getString(AppConfig.PREF_PIN, null);

            if(doorPin == null) {
                Toast.makeText(context, R.string.toast_pin_missing, Toast.LENGTH_SHORT).show();
                Intent newActivity = new Intent(context, MainActivity.class);
                newActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(newActivity);
            } else {
                sendDoorRequest(doorPin);
            }

        }
    }

    /**
     * Create the widget view.
     * GUI creation and stuff
     */
    private void drawWidget(Context context, int appWidgetId) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget);

        rv.setOnClickPendingIntent(R.id.btn_open_door, PendingIntent.getBroadcast(
                context, 0,
                new Intent(context, HsgDoorWidget.class).setAction(ACTION_OPEN_DOOR),
                PendingIntent.FLAG_UPDATE_CURRENT
        ));

        appWidgetManager.updateAppWidget(appWidgetId, rv);
    }

    /**
     * Sending open door request via http
     */
    private void sendDoorRequest(String pin) {
        Log.d("DEBUG", "Create async task to open door");
        new OpenDoorTask().execute(pin);
    }


    private class OpenDoorTask extends AsyncTask<String, Void, Boolean> {
        protected Boolean doInBackground(String... pins) {
            Log.d("DEBUG", "Prepare to open door with pin : " + pins[0]);

            OkHttpClient client = new OkHttpClient();

            RequestBody formBody = new FormEncodingBuilder()
                    .add("pin", pins[0])
                    .add("type", "pin")
                    .build();

            Request request = new Request.Builder()
                    .url(AppConfig.URL_OPEN_DOOR)

                    .post(formBody)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                //Log.d("DEBUG", response.body().string());
                return response.isSuccessful();
            } catch (IOException e) {
                return false;
            }
        }

        protected void onPostExecute(Boolean result) {
            if(result) {
                Log.d("DEBUG", "Door opened");
            } else {
                Log.d("DEBUG", "Failed to open door");
            }
        }
    }
}