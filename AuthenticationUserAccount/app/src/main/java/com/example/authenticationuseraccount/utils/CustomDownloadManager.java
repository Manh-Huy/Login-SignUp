package com.example.authenticationuseraccount.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import com.example.authenticationuseraccount.common.ErrorUtils;

public class CustomDownloadManager {
    private static CustomDownloadManager instance;
    private Context context;

    private CustomDownloadManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public static synchronized CustomDownloadManager getInstance(Context context) {
        if (instance == null) {
            instance = new CustomDownloadManager(context);
        }
        return instance;
    }

    public void downloadFile(String fileUrl,String fileName) {

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileUrl));
        request.setTitle("Downloading " + fileName);
        //request.setDescription("Downloading " + fileName);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_MUSIC, fileName + ".mp3");
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        if (downloadManager != null) {
            downloadManager.enqueue(request);
            ErrorUtils.showError(context, "Download started");
        } else {
            ErrorUtils.showError(context, "Download Manager is not available");
        }
    }
}
