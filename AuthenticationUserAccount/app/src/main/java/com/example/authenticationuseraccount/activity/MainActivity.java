package com.example.authenticationuseraccount.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;

import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.session.MediaController;
import androidx.media3.session.SessionToken;

import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.api.ApiService;
import com.example.authenticationuseraccount.common.Constants;
import com.example.authenticationuseraccount.common.LogUtils;
import com.example.authenticationuseraccount.common.PermissionManager;
import com.example.authenticationuseraccount.model.business.Song;
import com.example.authenticationuseraccount.model.business.User;
import com.example.authenticationuseraccount.service.MediaItemHolder;
import com.example.authenticationuseraccount.service.MusicService;
import com.example.authenticationuseraccount.service.UIThread;
import com.example.authenticationuseraccount.utils.BackEventHandler;
import com.example.authenticationuseraccount.utils.SocketIoManager;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.firebase.auth.FirebaseAuth;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private UIThread m_vThread;
    private boolean isReceiveNotification;
    private Song mSong;
    private FirebaseAuth firebaseAuth;
    private Disposable mDisposable;

    public static interface OnMediaControllerConnect {
        void onMediaControllerConnect(MediaController controller);

        void onUpdateUIOnRestar(MediaController mediaController);
    }

    @OptIn(markerClass = UnstableApi.class)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.ApplicationLogE("MainActivity onCreate");
        setContentView(R.layout.activity_main2);

        isReceiveNotification = false;
        mSong = null;

        if (this.m_vThread == null) {
            this.m_vThread = new UIThread(this);
        }

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
            LogUtils.ApplicationLogI("MainActivity: User Has Signed In!");
            SocketIoManager.getInstance();
            checkUserPremiumTime(User.getInstance());
            getUserLoveSong(User.getInstance().getUserID());
            getUserListenHistory(User.getInstance().getUserID());
        } else {
            LogUtils.ApplicationLogE("MainActivity: User Has Not Signed In!");
        }


        //Notification
        Intent intentFromFCM = getIntent();
        String actionFromNotification = intentFromFCM.getAction();
        HandleNotification(actionFromNotification, intentFromFCM);

        BackEventHandler.getInstance();
    }

    private void askingPermission() {

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
    }

    private void HandleNotification(String actionFromNotification, Intent intentFromFCM) {
        if (actionFromNotification != null && actionFromNotification.equals(Constants.NOTIFICATION_ACTION_CLICK)) {
            LogUtils.ApplicationLogI("Receive Action From Notfication");

            Bundle songBundle = intentFromFCM.getExtras();
            Song song = (Song) songBundle.getSerializable(Constants.NOTIFICATION_SONG_OBJECT);

            if (MediaItemHolder.getInstance().getMediaController() != null) {
                LogUtils.ApplicationLogI("Receive Action From Notfication And App Already Open");

                MediaItemHolder.getInstance().setMediaItem(song);
                m_vThread.onUpdateUIOnRestar(MediaItemHolder.getInstance().getMediaController());
                isReceiveNotification = false;
                mSong = null;

            } else {
                LogUtils.ApplicationLogI("Receive Action From Notfication mediaController null: " + song.getName());
                isReceiveNotification = true;
                mSong = song;
            }
        } else {
            LogUtils.ApplicationLogI("No Action From Any Notfication");
        }
    }


    @UnstableApi
    @Override
    protected void onStart() {
        super.onStart();

        LogUtils.ApplicationLogD("MainActivity onStart");
        if (MediaItemHolder.getInstance().getMediaController() != null) {
            HandleExoPlayerState();
            return;
        }

        getExoMediaController();
    }

    private void HandleExoPlayerState() {
        LogUtils.ApplicationLogI("MediaItemHolder Instance Not Null");

        if (isReceiveNotification && mSong != null) {
            LogUtils.ApplicationLogI("Receive noti and start playing");

            MediaItemHolder.getInstance().setMediaItem(mSong);
            isReceiveNotification = false;
            mSong = null;
        }
        if (MediaItemHolder.getInstance().getMediaController().getMediaMetadata().title != null) {
            LogUtils.ApplicationLogD("MediaMetadata Not Null => App is playing music (pausing / playing)");
            m_vThread.onUpdateUIOnRestar(MediaItemHolder.getInstance().getMediaController());
        } else {
            LogUtils.ApplicationLogD("App not playing music. just restart");
        }
    }

    private void getExoMediaController() {
        SessionToken sessionToken = new SessionToken(MainActivity.this, new ComponentName(MainActivity.this, MusicService.class));
        MediaController.Builder builder = new MediaController.Builder(MainActivity.this, sessionToken);
        ListenableFuture<MediaController> controllerFuture = builder.buildAsync();
        controllerFuture.addListener(() -> {
            try {
                if (MediaItemHolder.getInstance().getMediaController() == null) {
                    LogUtils.ApplicationLogE("OnStart Connect Media Controller");

                    MediaController mediaController = controllerFuture.get();
                    MediaItemHolder.getInstance().setMediaController(mediaController);
                    m_vThread.onMediaControllerConnect(mediaController);

                    if (isReceiveNotification && mSong != null) {
                        LogUtils.ApplicationLogI("Receive noti and start playing");

                        MediaItemHolder.getInstance().setMediaItem(mSong);
                        isReceiveNotification = false;
                        mSong = null;
                    }
                }
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, MoreExecutors.directExecutor());
    }

    private void getUserLoveSong(String userID) {
        ApiService.apiService.getUserLoveSong(userID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Song>>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull List<Song> songs) {
                        MediaItemHolder.getInstance().setListLoveSong(songs);
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        LogUtils.ApplicationLogE("Call api love song error: " + e.getMessage());
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        LogUtils.ApplicationLogI("Love Song Count: " + MediaItemHolder.getInstance().getListLoveSong().size());
                        LogUtils.ApplicationLogI("Call api love song Complete");
                    }
                });
    }

    private void getUserListenHistory(String userID) {
        ApiService.apiService.getUserListenHistory(userID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Song>>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull List<Song> songs) {
                        MediaItemHolder.getInstance().setListRecentSong(songs);
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        LogUtils.ApplicationLogE("Call api listen user history error");
                    }

                    @Override
                    public void onComplete() {
                        LogUtils.ApplicationLogE("Call api listen history complete");
                    }
                });
    }

    private void checkUserPremiumTime(User user) {

        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDateNow = sdf.format(now);
        LogUtils.ApplicationLogI("Date From Local: " + formattedDateNow);
        String expiredDatePremium = user.getExpiredDatePremium();
        LogUtils.ApplicationLogI("Date From Logged In User: " + user.getExpiredDatePremium());
        Date dateFromServer;

        try {
            if ("aN-aN-NaN".equals(expiredDatePremium) || "None".equals(expiredDatePremium) || "none".equals(expiredDatePremium)) {
                dateFromServer = null;
            } else {
                dateFromServer = sdf.parse(expiredDatePremium);
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        if (dateFromServer != null) {
            if (dateFromServer.before(now)) {
                LogUtils.ApplicationLogI("Premium expired");
                downgradePremium(user.getUserID());
            } else {
                LogUtils.ApplicationLogI("Premium Still Premium");
            }
        } else {
            LogUtils.ApplicationLogI("aN-aN-NaN => NormalUser");
        }

    }

    @SuppressLint("CheckResult")
    private void downgradePremium(String userID) {
        ApiService.apiService.downgradePremium(userID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    LogUtils.ApplicationLogD("DownGrade User Succefully");
                }, throwable -> {
                    LogUtils.ApplicationLogE("Failed To DownGrade User");
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtils.ApplicationLogD("MainActivity onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtils.ApplicationLogD("MainActivity onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtils.ApplicationLogE("MainActivity onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LogUtils.ApplicationLogE("MainActivity onRestart");
    }

    @Override
    protected void onDestroy() {
        LogUtils.ApplicationLogE("MainActivity onDestroy");
        if (m_vThread.getListener() != null) {
            MediaItemHolder.getInstance().getMediaController().removeListener(m_vThread.getListener());
        }
        if (MediaItemHolder.getInstance().getMediaController() != null) {
            MediaItemHolder.getInstance().getMediaController().release();
            MediaItemHolder.getInstance().destroy();
        }
        if (m_vThread != null) {
            m_vThread.release();
            m_vThread = null;
        }
        if (SocketIoManager.getInstance() != null) {
            SocketIoManager.getInstance().disconnect();
        }

        Intent intent = new Intent(MainActivity.this, MusicService.class);
        stopService(intent);

        super.onDestroy();
    }

}