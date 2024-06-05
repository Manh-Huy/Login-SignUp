package com.example.authenticationuseraccount;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.example.authenticationuseraccount.utils.DataLocalManager;
import com.stripe.android.PaymentConfiguration;

public class MyApplication extends Application {

    public static final String CHANNEL_ID = "CHANNEL_1";
    @Override
    public void onCreate() {
        super.onCreate();
        DataLocalManager.init(getApplicationContext());
        createNotificationChannel();

        PaymentConfiguration.init(
                getApplicationContext(),
                "pk_test_51POBbu1FjeTWqyK8DTko7WHq8pIlvPMVbvF7Aa43CUEMrNmaHWHQYGBCiLd3gIVQB4DdF6gv0SIom4mCFDkr8oiT00NAbc4OAd"
        );
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Music Control Channel", importance);
            channel.setSound(null,null);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
