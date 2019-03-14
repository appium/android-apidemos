/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.appium.android.apis.app;

import java.util.Random;

import androidx.annotation.RequiresApi;
import io.appium.android.apis.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * UI for posting an example notification.
 */
public class IncomingMessage extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.incoming_message);

        Button button = (Button) findViewById(R.id.notify_app);
        button.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    showAppNotification();
                }
            });

        button = (Button) findViewById(R.id.notify_interstitial);
        button.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    showInterstitialNotification();
                }
            });

        createChannel();
    }



    /**
     * This method creates an array of Intent objects representing the
     * activity stack for the incoming message details state that the
     * application should be in when launching it from a notification.
     */
    static Intent[] makeMessageIntentStack(Context context, CharSequence from,
            CharSequence msg) {
        // A typical convention for notifications is to launch the user deeply
        // into an application representing the data in the notification; to
        // accomplish this, we can build an array of intents to insert the back
        // stack stack history above the item being displayed.
        Intent[] intents = new Intent[4];

        // First: root activity of ApiDemos.
        // This is a convenient way to make the proper Intent to launch and
        // reset an application's task.
        intents[0] = Intent.makeRestartActivityTask(new ComponentName(context,
                io.appium.android.apis.ApiDemos.class));

        // "App"
        intents[1] = new Intent(context, io.appium.android.apis.ApiDemos.class);
        intents[1].putExtra("io.appium.android.apis.Path", "App");
        // "App/Notification"
        intents[2] = new Intent(context, io.appium.android.apis.ApiDemos.class);
        intents[2].putExtra("io.appium.android.apis.Path", "App/Notification");

        // Now the activity to display to the user.  Also fill in the data it
        // should display.
        intents[3] = new Intent(context, IncomingMessageView.class);
        intents[3].putExtra(IncomingMessageView.KEY_FROM, from);
        intents[3].putExtra(IncomingMessageView.KEY_MESSAGE, msg);

        return intents;
    }


    private static final String CHANNEL_ID = "main_channel";
    private static final String CHANNEL_NAME = "Sample App";
    private static final String CHANNEL_DESCRIPTION = "Incoming Message";

    @RequiresApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        if (mNotificationManager == null) {
            return;
        }
        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        mChannel.setDescription(CHANNEL_DESCRIPTION);
        mChannel.setShowBadge(true);
        mChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        mNotificationManager.createNotificationChannel(mChannel);
    }

    /**
     * The notification is the icon and associated expanded entry in the
     * status bar.
     */
    void showAppNotification() {
        // look up the notification manager service
        NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        // The details of our fake message
        CharSequence from = "Joe";
        CharSequence message;
        switch ((new Random().nextInt()) % 3) {
            case 0: message = "r u hungry?  i am starved"; break;
            case 1: message = "im nearby u"; break;
            default: message = "kthx. meet u for dinner. cul8r"; break;
        }
        // The PendingIntent to launch our activity if the user selects this
        // notification.  Note the use of FLAG_CANCEL_CURRENT so that, if there
        // is already an active matching pending intent, cancel it and replace
        // it with the new array of Intents.
        PendingIntent contentIntent = PendingIntent.getActivities(this, 0,
                makeMessageIntentStack(this, from, message), PendingIntent.FLAG_CANCEL_CURRENT);
        // The ticker text, this uses a formatted string so our message could be localized
        String tickerText = getString(R.string.imcoming_message_ticker_text, message);
        // Set the info for the views that show in the notification panel.

        Notification.Builder notifBuilder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notifBuilder = new Notification.Builder(this, CHANNEL_ID);
        } else {
            notifBuilder = new Notification.Builder(this);
        }

        notifBuilder.setSmallIcon(R.drawable.stat_sample)  // the status icon
                .setTicker(tickerText)  // the status text
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentTitle(from)  // the label of the entry
                .setContentText(message)  // the contents of the entry
                .setContentIntent(contentIntent);  // The intent to send when the entry is clicked
        // We'll have this notification do the default sound, vibration, and led.
        // Note that if you want any of these behaviors, you should always have
        // a preference for the user to turn them off.
        notifBuilder.setDefaults(Notification.DEFAULT_ALL);
        // Note that we use R.layout.incoming_message_panel as the ID for
        // the notification.  It could be any integer you want, but we use
        // the convention of using a resource id for a string related to
        // the notification.  It will always be a unique number within your
        // application.
        nm.notify(R.string.imcoming_message_ticker_text, notifBuilder.build());
    }


    /**
     * The notification is the icon and associated expanded entry in the
     * status bar.
     */
    void showInterstitialNotification() {
        // look up the notification manager service
        NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        // The details of our fake message
        CharSequence from = "Dianne";
        CharSequence message;
        switch ((new Random().nextInt()) % 3) {
            case 0: message = "i am ready for some dinner"; break;
            case 1: message = "how about thai down the block?"; break;
            default: message = "meet u soon. dont b late!"; break;
        }
        // The PendingIntent to launch our activity if the user selects this
        // notification.  Note the use of FLAG_CANCEL_CURRENT so that, if there
        // is already an active matching pending intent, cancel it and replace
        // it with the new Intent.
        Intent intent = new Intent(this, IncomingMessageInterstitial.class);
        intent.putExtra(IncomingMessageView.KEY_FROM, from);
        intent.putExtra(IncomingMessageView.KEY_MESSAGE, message);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);

        // The ticker text, this uses a formatted string so our message could be localized
        String tickerText = getString(R.string.imcoming_message_ticker_text, message);

        Notification.Builder notifBuilder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notifBuilder = new Notification.Builder(this, CHANNEL_ID);
        } else {
            notifBuilder = new Notification.Builder(this);
        }

        // Set the info for the views that show in the notification panel.
        notifBuilder
                .setSmallIcon(R.drawable.stat_sample)  // the status icon
                .setTicker(tickerText)  // the status text
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentTitle(from)  // the label of the entry
                .setContentText(message)  // the contents of the entry
                .setContentIntent(contentIntent);  // The intent to send when the entry is clicked
        // We'll have this notification do the default sound, vibration, and led.
        // Note that if you want any of these behaviors, you should always have
        // a preference for the user to turn them off.
        notifBuilder.setDefaults(Notification.DEFAULT_ALL);
        // Note that we use R.layout.incoming_message_panel as the ID for
        // the notification.  It could be any integer you want, but we use
        // the convention of using a resource id for a string related to
        // the notification.  It will always be a unique number within your
        // application.
        nm.notify(R.string.imcoming_message_ticker_text, notifBuilder.build());
    }
}
