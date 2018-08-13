package com.out386.networkstats;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.Calendar;

import static com.out386.networkstats.Utils.formatBytes;

public class MyService extends Service {

    private static final long TOTAL_AVAILABLE = (long) (1.5 * 1024 * 1024 * 1024);
    private static final String CHANNEL_ID = "standard";


    private NetworkStatsManager networkStatsManager;
    private String subscriberID;
    private NotificationCompat.Builder notificationBuilder;
    private boolean first = true;
    private Handler handler;

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        generateAndSetMessage();
        return START_STICKY;
    }

    private void generateAndSetMessage() {
        NetworkStats.Bucket bucket = getBucket();
        String message;
        if (bucket == null)
            message = "No data available";
        else {
            long usage = bucket.getRxBytes() + bucket.getTxBytes();
            long remaining = TOTAL_AVAILABLE - usage;
            message = "Used: " + formatBytes(usage) + "\t\t\tAvailable: " + formatBytes(remaining);
        }

        Log.i("meh", "onStartCommand: " + message);
        /*if (handler == null)
            handler = new Handler();
        handler.postDelayed(this::generateAndSetMessage, 1000);*/
        forgroundify(message);

    }

    private NetworkStats.Bucket getBucket() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        if (subscriberID == null)
            subscriberID = getSubscriberId();

        if (networkStatsManager == null)
            networkStatsManager = (NetworkStatsManager) getApplicationContext()
                    .getSystemService(Context.NETWORK_STATS_SERVICE);

        NetworkStats.Bucket bucket;
        try {
            bucket = networkStatsManager.querySummaryForDevice(ConnectivityManager.TYPE_MOBILE,
                    subscriberID,
                    calendar.getTimeInMillis(),
                    System.currentTimeMillis());
        } catch (RemoteException e) {
            return null;
        }
        return bucket;
    }

    @SuppressLint({"MissingPermission", "HardwareIds"})
    private String getSubscriberId() {
        TelephonyManager tm = (TelephonyManager) getApplicationContext()
                .getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getSubscriberId();
    }

    private void forgroundify(String message) {
        if (notificationBuilder == null)
            notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);

        notificationBuilder
                .setSound(null)
                .setVibrate(null)
                .setAutoCancel(false)
                .setOnlyAlertOnce(true)
                //.setContentTitle(getString(R.string.notif_running))
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary))
        .setPriority(Notification.PRIORITY_MAX);

        if (first) {
            first = false;
            createNotifChannel();
        }

        Notification notif = notificationBuilder.build();
        notif.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;
        startForeground(1, notif);
    }

    private void createNotifChannel() {
        final String CHANNEL_NAME = "meh";
        final String CHANNEL_DESC = "meh";
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH);
        channel.setSound(null, null);
        channel.setDescription(CHANNEL_DESC);
        channel.enableLights(false);
        channel.enableVibration(false);
        NotificationManager notificationManager =
                ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE));
        if (notificationManager != null)
            notificationManager.createNotificationChannel(channel);
    }

}
