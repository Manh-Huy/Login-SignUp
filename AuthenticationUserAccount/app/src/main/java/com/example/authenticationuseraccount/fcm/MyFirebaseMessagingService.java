package com.example.authenticationuseraccount.fcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.authenticationuseraccount.MyApplication;
import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.activity.LoginSignUpActivity;
import com.example.authenticationuseraccount.activity.MainActivity;
import com.example.authenticationuseraccount.common.Constants;
import com.example.authenticationuseraccount.model.business.Song;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Date;
import java.util.Map;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMessagingSer";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        //Get Notification
        /*RemoteMessage.Notification notification = message.getNotification();
        if (notification != null) {
            strTitle = notification.getTitle();
            strMessage = notification.getBody();
            return;
        }*/


        //Get Data messages
        Map<String, String> stringMap = message.getData();
        String strTitle = stringMap.get("title");
        String strMessage = stringMap.get("body");


        String songID = stringMap.get("songID");
        if (songID != null) {

            String name = stringMap.get("nameSong");
            String artist = stringMap.get("artistSong");
            String genre = stringMap.get("genreSong");
            String album = stringMap.get("albumSong");
            String view = stringMap.get("viewsSong");
            String songUrl = stringMap.get("songURL");
            String imgUrl = stringMap.get("imageURL");
            Song song = new Song(artist, album, imgUrl, name, genre, songID, view, songUrl);

            Log.e(TAG, "onMessageReceived name: " + name);
            Log.e(TAG, "onMessageReceived artist: " + artist);
            Log.e(TAG, "onMessageReceived genre: " + genre);
            Log.e(TAG, "onMessageReceived album: " + album);
            Log.e(TAG, "onMessageReceived imgUrl: " + imgUrl);
            Log.e(TAG, "onMessageReceived songUrl: " + songUrl);

            Glide.with(this)
                    .asBitmap()
                    .load(imgUrl).into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            sendMediaNotification(song, strTitle, strMessage, resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });

        } else {
            String bannerId = stringMap.get("bannerID");
            String link = stringMap.get("link");
            String imgUrl = stringMap.get("imageURL");

            Glide.with(this)
                    .asBitmap()
                    .load(imgUrl).into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            sendNotification(strTitle, strMessage, link, resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });
        }


    }

    private void sendMediaNotification(Song song, String strTitle, String strMsg, Bitmap bitmap) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction(Constants.NOTIFICATION_ACTION_CLICK);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.NOTIFICATION_SONG_OBJECT, song);
        intent.putExtras(bundle);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, MyApplication.CHANNEL_ID_2)
                .setContentTitle(strTitle)
                .setContentText(strMsg)
                .setSmallIcon(R.drawable.logo)
                .setLargeIcon(bitmap)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap).bigLargeIcon((Bitmap) null))
                .setColor(getResources().getColor(R.color.colorAccent))
                .setAutoCancel(false)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(getNotificationId(), notification);
    }

    private void sendNotification(String strTitle, String strMessage, String linkPost, Bitmap bitmap) {

        Intent intent = new Intent(this, LoginSignUpActivity.class);
        /*intent.setAction(Constants.NOTIFICATION_ACTION_CLICK);
        intent.putExtra(Constants.NOTIFICATION_SONG_URL, songURL);*/
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, MyApplication.CHANNEL_ID_2)
                .setContentTitle(strTitle)
                .setContentText(strMessage)
                .setSmallIcon(R.drawable.logo)
                .setLargeIcon(bitmap)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap).bigLargeIcon((Bitmap) null))
                .setColor(getResources().getColor(R.color.colorAccent))
                .setAutoCancel(true)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(getNotificationId(), notification);
    }

    private int getNotificationId() {
        return (int) new Date().getTime();
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        //This token should be sent to Server everytime it's refresh

        Log.e(TAG, "onNewToken: " + token);
    }
}
