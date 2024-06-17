package com.example.authenticationuseraccount.utils;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import androidx.core.app.NotificationCompat;

import com.example.authenticationuseraccount.MyApplication;
import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.activity.MainActivity;
import com.example.authenticationuseraccount.common.Constants;
import com.example.authenticationuseraccount.common.ErrorUtils;
import com.example.authenticationuseraccount.common.LogUtils;
import com.example.authenticationuseraccount.model.business.Song;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;

public class CustomDownloadManager {
    private static CustomDownloadManager instance;
    private Context context;
    private long downloadID;

    private CustomDownloadManager(Context context) {
        this.context = context.getApplicationContext();

        // Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.registerReceiver(onDownloadComplete, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            context.registerReceiver(onDownloadComplete, filter);
        }
    }

    public void unregisterReceiver() {
        // Unregister the BroadcastReceiver
        context.unregisterReceiver(onDownloadComplete);
    }

    public static synchronized CustomDownloadManager getInstance(Context context) {
        if (instance == null) {
            instance = new CustomDownloadManager(context);
        }
        return instance;
    }

    public void downloadFile(String fileUrl, String fileName) {

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileUrl));
        request.setTitle("Downloading " + fileName);
        //request.setDescription("Downloading " + fileName);
        //request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_MUSIC, fileName + ".mp3");
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        if (downloadManager != null) {
            downloadID = downloadManager.enqueue(request);
            ErrorUtils.showError(context, "Download started");
        } else {
            ErrorUtils.showError(context, "Download Manager is not available");
        }
    }

    private BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtils.ApplicationLogI("Receive Message Downloaded");
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

            if (downloadID == id) {
                // Download completed
                LogUtils.ApplicationLogI("onReceive BroadCast complete download");


                DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                if (downloadManager != null) {
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(downloadID);
                    Cursor cursor = downloadManager.query(query);
                    if (cursor.moveToFirst()) {
                        int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
                        String downloadedFileUriString = cursor.getString(columnIndex);
                        Uri downloadedFileUri = Uri.parse(downloadedFileUriString);

                        // Convert file to MyClass object
                        //Song song = convertFileToMyClass(new File(downloadedFileUri.getPath()));
                        ErrorUtils.showError(context, "onReceive BroadCast complete download: " + downloadedFileUri);
                        LogUtils.ApplicationLogI("download Uri: " + downloadedFileUri);
                    }
                    cursor.close();
                }

            }
        }

    };

    private Song convertFileToMyClass(File file) {
        Gson gson = new Gson();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            return gson.fromJson(br, Song.class);
        } catch (IOException e) {
            ErrorUtils.showError(context, "Error converting file to MyClass");
            return null;
        }
    }

    private void sendMediaNotification(Song song, String strTitle, String strMsg, Bitmap bitmap) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(Constants.NOTIFICATION_ACTION_CLICK);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.NOTIFICATION_SONG_OBJECT, song);
        intent.putExtras(bundle);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(context, MyApplication.CHANNEL_ID_2)
                .setContentTitle(strTitle)
                .setContentText(strMsg)
                .setSmallIcon(R.drawable.logo)
                .setLargeIcon(bitmap)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap).bigLargeIcon((Bitmap) null))
                .setAutoCancel(false)
                .build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(getNotificationId(), notification);
    }

    private int getNotificationId() {
        return (int) new Date().getTime();
    }

}
