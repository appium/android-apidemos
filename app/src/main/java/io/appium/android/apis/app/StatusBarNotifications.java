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

import androidx.annotation.RequiresApi;
import io.appium.android.apis.R;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;

/**
 * Demonstrates adding notifications to the status bar
 */
public class StatusBarNotifications extends Activity {

    private NotificationManager mNotificationManager;

    // Use our layout id for a unique identifier
    private static int MOOD_NOTIFICATIONS = R.layout.status_bar_notifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.status_bar_notifications);

        Button button;

        // Get the notification manager serivce.
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        button = (Button) findViewById(R.id.happy);
        button.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                setMood(R.drawable.stat_happy, R.string.status_bar_notifications_happy_message,
                        false);
            }
        });

        button = (Button) findViewById(R.id.neutral);
        button.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                setMood(R.drawable.stat_neutral, R.string.status_bar_notifications_ok_message,
                        false);
            }
        });

        button = (Button) findViewById(R.id.sad);
        button.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                setMood(R.drawable.stat_sad, R.string.status_bar_notifications_sad_message, false);
            }
        });

        button = (Button) findViewById(R.id.happyMarquee);
        button.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                setMood(R.drawable.stat_happy, R.string.status_bar_notifications_happy_message,
                        true);
            }
        });

        button = (Button) findViewById(R.id.neutralMarquee);
        button.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                setMood(R.drawable.stat_neutral, R.string.status_bar_notifications_ok_message, true);
            }
        });

        button = (Button) findViewById(R.id.sadMarquee);
        button.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                setMood(R.drawable.stat_sad, R.string.status_bar_notifications_sad_message, true);
            }
        });

        button = (Button) findViewById(R.id.happyViews);
        button.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                setMoodView(R.drawable.stat_happy, R.string.status_bar_notifications_happy_message);
            }
        });

        button = (Button) findViewById(R.id.neutralViews);
        button.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                setMoodView(R.drawable.stat_neutral, R.string.status_bar_notifications_ok_message);
            }
        });

        button = (Button) findViewById(R.id.sadViews);
        button.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                setMoodView(R.drawable.stat_sad, R.string.status_bar_notifications_sad_message);
            }
        });
        
        button = (Button) findViewById(R.id.defaultSound);
        button.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                setDefault(Notification.DEFAULT_SOUND);
            }
        });
        
        button = (Button) findViewById(R.id.defaultVibrate);
        button.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                setDefault(Notification.DEFAULT_VIBRATE);
            }
        });
        
        button = (Button) findViewById(R.id.defaultAll);
        button.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                setDefault(Notification.DEFAULT_ALL);
            }
        });
        
        button = (Button) findViewById(R.id.clear);
        button.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                mNotificationManager.cancel(R.layout.status_bar_notifications);
            }
        });

        createChannel();
    }

    private PendingIntent makeMoodIntent(int moodId) {
        // The PendingIntent to launch our activity if the user selects this
        // notification.  Note the use of FLAG_UPDATE_CURRENT so that if there
        // is already an active matching pending intent, we will update its
        // extras (and other Intents in the array) to be the ones passed in here.
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, NotificationDisplay.class).putExtra("moodimg", moodId),
                PendingIntent.FLAG_UPDATE_CURRENT);
        return contentIntent;
    }
    

    private PendingIntent makeDefaultIntent() {
        // A typical convention for notifications is to launch the user deeply
        // into an application representing the data in the notification; to
        // accomplish this, we can build an array of intents to insert the back
        // stack stack history above the item being displayed.
        Intent[] intents = new Intent[4];

        // First: root activity of ApiDemos.
        // This is a convenient way to make the proper Intent to launch and
        // reset an application's task.
        intents[0] = Intent.makeRestartActivityTask(new ComponentName(this,
                io.appium.android.apis.ApiDemos.class));

        // "App"
        intents[1] = new Intent(this, io.appium.android.apis.ApiDemos.class);
        intents[1].putExtra("io.appium.android.apis.Path", "App");
        // "App/Notification"
        intents[2] = new Intent(this, io.appium.android.apis.ApiDemos.class);
        intents[2].putExtra("io.appium.android.apis.Path", "App/Notification");

        // Now the activity to display to the user.
        intents[3] = new Intent(this, StatusBarNotifications.class);

        // The PendingIntent to launch our activity if the user selects this
        // notification.  Note the use of FLAG_UPDATE_CURRENT so that if there
        // is already an active matching pending intent, we will update its
        // extras (and other Intents in the array) to be the ones passed in here.
        PendingIntent contentIntent = PendingIntent.getActivities(this, 0,
                intents, PendingIntent.FLAG_UPDATE_CURRENT);
        return contentIntent;
    }

    private static final String CHANNEL_ID = "main_channel";
    private static final String CHANNEL_NAME = "Sample App";
    private static final String CHANNEL_DESCRIPTION = "Status Bar Notifications";


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

    private void setMood(int moodId, int textId, boolean showTicker) {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = getText(textId);
        // In this sample, we'll use this text for the title of the notification
        CharSequence title = getText(R.string.status_bar_notifications_mood_title);
        // Set the info for the views that show in the notification panel.

        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(this, CHANNEL_ID);
        } else {
            builder = new Notification.Builder(this);
        }

        Notification.Builder notifBuidler = builder // the context to use
                .setSmallIcon(moodId)  // the status icon
                .setWhen(System.currentTimeMillis())  // the timestamp for the notification
                .setContentTitle(title)  // the title for the notification
                .setContentText(text)  // the details to display in the notification
                .setContentIntent(makeMoodIntent(moodId));  // The intent to send clicked
        if (showTicker) {
            // include the ticker text
            notifBuidler.setTicker(getString(textId));
        }
        // Send the notification.
        // We use a layout id because it is a unique number.  We use it later to cancel.
        mNotificationManager.notify(MOOD_NOTIFICATIONS, notifBuidler.build());
    }

    private void setMoodView(int moodId, int textId) {
        // Instead of the normal constructor, we're going to use the one with no args and fill
        // in all of the data ourselves.  The normal one uses the default layout for notifications.
        // You probably want that in most cases, but if you want to do something custom, you
        // can set the contentView field to your own RemoteViews object.
        Notification notif = new Notification();

        // This is who should be launched if the user selects our notification.
        notif.contentIntent = makeMoodIntent(moodId);

        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = getText(textId);
        notif.tickerText = text;

        // the icon for the status bar
        notif.icon = moodId;

        // our custom view
        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.status_bar_balloon);
        contentView.setTextViewText(R.id.text, text);
        contentView.setImageViewResource(R.id.icon, moodId);
        notif.contentView = contentView;

        // we use a string id because is a unique number.  we use it later to cancel the
        // notification
        mNotificationManager.notify(MOOD_NOTIFICATIONS, notif);
    }

    private void setDefault(int defaults) {
        // This is who should be launched if the user selects our notification.
        PendingIntent contentIntent = makeDefaultIntent();
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = getText(R.string.status_bar_notifications_happy_message);
        // In this sample, we'll use this text for the title of the notification
        CharSequence title = getText(R.string.status_bar_notifications_mood_title);

        Notification.Builder notifBuilder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notifBuilder = new Notification.Builder(this, CHANNEL_ID);
        } else {
            notifBuilder = new Notification.Builder(this);
        }

        // Set the info for the views that show in the notification panel.
        Notification notification = notifBuilder // the context to use
                .setSmallIcon(R.drawable.stat_happy)  // the status icon
                .setTicker(text)  // the text to display in the ticker
                .setWhen(System.currentTimeMillis())  // the timestamp for the notification
                .setContentTitle(title)  // the title for the notification
                .setContentText(text)  // the details to display in the notification
                .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                .setDefaults(defaults)
                .build();
        mNotificationManager.notify(
                MOOD_NOTIFICATIONS, // we use a string id because it is a unique
                // number.  we use it later to cancel the notification
                notification);
    }
}
