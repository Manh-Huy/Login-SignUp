package com.example.authenticationuseraccount.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.media.browse.MediaBrowser;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.session.MediaController;
import androidx.media3.session.MediaSession;
import androidx.media3.session.SessionToken;

import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.common.LogUtils;
import com.example.authenticationuseraccount.common.PermissionManager;
import com.example.authenticationuseraccount.service.BackEventHandler;
import com.example.authenticationuseraccount.service.MediaItemHolder;
import com.example.authenticationuseraccount.service.MusicService;
import com.example.authenticationuseraccount.service.UIThread;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    private UIThread m_vThread;

    public static interface OnMediaControllerConnect {
        void onMediaControllerConnect(MediaController controller);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        BackEventHandler.getInstance();

        PermissionManager.requestPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE, 100);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            PermissionManager.requestPermission(this, Manifest.permission.FOREGROUND_SERVICE, 100);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            PermissionManager.requestPermission(this, Manifest.permission.MANAGE_EXTERNAL_STORAGE, 100);
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        }


        this.m_vThread = new UIThread(this);

    }

    @UnstableApi
    @Override
    protected void onStart() {
        super.onStart();

        SessionToken sessionToken = new SessionToken(MainActivity.this, new ComponentName(MainActivity.this, MusicService.class));
        MediaController.Builder builder = new MediaController.Builder(MainActivity.this, sessionToken);
        ListenableFuture<MediaController> controllerFuture = builder.buildAsync();
        controllerFuture.addListener(() -> {
            try {
                if (MediaItemHolder.getInstance().getMediaController() == null) {
                    MediaController mediaController = controllerFuture.get();
                    MediaItemHolder.getInstance().setMediaController(mediaController);
                    m_vThread.onMediaControllerConnect(mediaController);
                }
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, MoreExecutors.directExecutor());
    }
}