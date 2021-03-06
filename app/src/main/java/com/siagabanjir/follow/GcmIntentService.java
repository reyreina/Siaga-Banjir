package com.siagabanjir.follow;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.siagabanjir.DetailActivity;
import com.siagabanjir.R;

public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
	private static final String TAG = "SiagaBanjir";
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    public GcmIntentService() {
        super("GcmIntentService");
        //Toast.makeText(this.getApplicationContext(), "GcmIntentService", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                //sendNotification("Send error: " + extras.toString(), extras);
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                //sendNotification("Deleted messages on server: " +
                 //       extras.toString(), extras);
            // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // This loop represents the service doing some work.
                for (int i=0; i<5; i++) {
                    Log.i(TAG, "Working... " + (i+1)
                            + "/5 @ " + SystemClock.elapsedRealtime());
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                    }
                }
                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                // Post notification of received message.
                String changes = "";
                if (extras.getString("changes").equals("increased")) {
                	changes = "meningkat";
                } else if (extras.getString("changes").equals("decreased")) {
                	changes = "menurun";
                }
                
           
                //message = "Ketinggian air di Pintu Air Pasar Ikan meningkat pada pukul 19.00. Status: WASPADA.";
                String message = "Water level in "+ extras.getString("pintuair")+" sluice " + extras.getString("changes")+ " at "+extras.getString("waktu")+".00. Status: "+extras.getString("status")+".";
                if (extras.getString("changes").equals("increased")) {
                	message = "Watch out! " + message;
                }
                
                message = "Ketinggian air di Pintu Air "+ extras.getString("pintuair") + " "+ changes +" pada pukul " + extras.getString("waktu") + ".00. Status: " + extras.getString("status") + ".";
                if (extras.getString("changes").equals("increased")) {
                	message = "Hati-hati! " + message;
                }
                String affectedArea = extras.getString("affected_area");
                if (!affectedArea.isEmpty()) {
                    message += " Daerah yang terpengaruh: " + affectedArea + ".";
                }
                sendNotification(message, extras);
                Log.i(TAG, "Received: " + extras.toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg, Bundle extras) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, DetailActivity.class);

        //DataPintuAir pa = DataPintuAir.mapsPintuAir.get(extras.getString("pintuair"));
        
        intent.putExtra("namapintuair", extras.getString("pintuair"));
        
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                intent, 0);
        

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.ic_launcher)
        .setContentTitle("Notifikasi Pintu Air")
        .setStyle(new NotificationCompat.BigTextStyle()
        .bigText(msg))
        .setContentText(msg);

        //mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        AudioManager audio = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);

        if (audio.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE) {
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(250);
        }
    }
}
