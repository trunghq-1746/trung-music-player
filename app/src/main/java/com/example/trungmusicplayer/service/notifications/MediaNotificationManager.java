package com.example.trungmusicplayer.service.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import com.example.trungmusicplayer.R;
import com.example.trungmusicplayer.service.MediaPlayerService;
import com.example.trungmusicplayer.ui.MainActivity;

public class MediaNotificationManager {
    public static final int NOTIFICATION_ID = 322;
    public static final String CHANNEL_ID = "com.example.trungmusicplayer.channel";
    private Context mContext;
    private MediaPlayerService mMediaPlayerService;
    private NotificationManager mNotificationManager;

    public MediaNotificationManager(Context context, MediaPlayerService service) {
        mContext = context;
        mMediaPlayerService = service;
        mNotificationManager =
            (NotificationManager) mMediaPlayerService.getSystemService(
                Context.NOTIFICATION_SERVICE);
    }

    public NotificationManager getNotificationManager() {
        return mNotificationManager;
    }

    public Notification buildNotification(boolean isPlaying) {
        if (isAndroidOOrHigher()) createChannel();
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext,
            0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
            mContext, CHANNEL_ID)
            .setContentTitle("Media Player")
            .setContentText("Playing...")
            .setSmallIcon(R.drawable.ic_album_black_24dp)
            .setContentIntent(pendingIntent);
        if (!isPlaying) builder.setContentText("Pause");
        return builder.build();
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void createChannel() {
        if (mNotificationManager.getNotificationChannel(CHANNEL_ID) == null) {
            CharSequence name = mContext.getString(R.string.notification_channel_name);
            String des = mContext.getString(R.string.notification_channel_des);
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                name, importance);
            channel.setDescription(des);
            mNotificationManager.createNotificationChannel(channel);
        }
    }

    private boolean isAndroidOOrHigher() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }
}
