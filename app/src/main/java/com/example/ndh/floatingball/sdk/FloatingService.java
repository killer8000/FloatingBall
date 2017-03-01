package com.example.ndh.floatingball.sdk;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.ndh.floatingball.MainActivity;
import com.example.ndh.floatingball.R;

/**
 * Created by ndh on 16/12/14.
 */

public class FloatingService extends Service {

    private NotificationManager mManager;
    private Notification mNotification;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        createView();
        super.onCreate();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    private static final int ID = 0;

    private void createView() {
        FloatingWindowManager.create(this).init();
        createNotification();

    }

    private void createNotification() {
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);
        //START 让应用通过通知置于前台
        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_assist)
                .setContentText("快捷助手正在运行...")
                .setContentIntent(pendingIntent);
        mNotification = builder.build();
        mNotification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
        mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mManager.notify(ID, mNotification);
        startForeground(ID, mNotification);
        // END 让应用通过通知置于前台
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeAllView();
        stopForeground(true);
        mManager.cancel(ID);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        //横竖屏切换时，需要重置再重建
        removeAllView();
        createView();
        super.onConfigurationChanged(newConfig);
    }

    private void removeAllView() {
        FloatingWindowManager.create(this).removeAllView();
    }
}
