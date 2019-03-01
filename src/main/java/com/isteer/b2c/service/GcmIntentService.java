package com.isteer.b2c.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.isteer.b2c.config.B2CAppConstant;
import com.isteer.b2c.R;
import com.isteer.b2c.receiver.GcmBroadcastReceiver;

public class GcmIntentService extends IntentService {
	
    public static int NOTIFICATION_ID = 1;
	private static final String TAG = GcmIntentService.class.getName();
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
    	
    	Log.e("GcmIntentService", "onHandleIntent");
    	
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {/* 

            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error: " + extras.toString());
            } 
            else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " + extras.toString());
            } 
            else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
            	
                Log.e(TAG, "Received: " + extras.toString());
                
                String message = extras.getString(SEMAppConstant.KEY_MSG);
                
                sendNotification(message);

            }
        */}

        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

/*    private void sendNotification(String msg) {
    	
    	Log.e("GcmIntentService", "sendNotification");
    	
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, SEMProductListScreen.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.notify_isteer_sw)
        .setTicker("iSteer Retail")
        .setDefaults(Notification.DEFAULT_SOUND)
        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.notify_isteer))
        .setContentTitle("iSteer Retail")
        .setStyle(new NotificationCompat.BigTextStyle()
        .bigText(msg))
        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID++, mBuilder.build());
        
    }*/
}