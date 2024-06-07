package com.example.authenticationuseraccount.fcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.authenticationuseraccount.MyApplication;
import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.activity.MainActivity;
import com.example.authenticationuseraccount.common.Constants;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMessagingSer";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        //Get Notification
        RemoteMessage.Notification notification = message.getNotification();
        if (notification == null) {
            return;
        }
        String strTitle = notification.getTitle();
        String strMessage = notification.getBody();

        //Get Data messages
        Map<String, String> stringMap = message.getData();
        String songURl = stringMap.get("songURL");
        Log.e(TAG, "onMessageReceived: " + songURl);
        sendNotification(strTitle, strMessage, songURl);
    }

    private void sendNotification(String strTitle, String strMessage, String songURL) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction(Constants.NOTIFICATION_ACTION_CLICK);
        if (songURL != null) {
            intent.putExtra(Constants.NOTIFICATION_SONG_URL, songURL);
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, MyApplication.CHANNEL_ID_2)
                .setContentTitle(strTitle)
                .setContentText(strMessage)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(strMessage))
                .setSmallIcon(R.drawable.logo)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        Notification notification = notificationBuilder.build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(565, notification);
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        //This token should be sent to Server everytime it's refresh

        Log.e(TAG, "onNewToken: " + token);
    }
}
