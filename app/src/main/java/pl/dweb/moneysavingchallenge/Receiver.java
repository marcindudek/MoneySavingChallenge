package pl.dweb.moneysavingchallenge;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v7.app.NotificationCompat;

/**
 * Created by md on 4/1/17.
 */
public class Receiver extends BroadcastReceiver {

    private Context context;
    private Intent intent;

    @Override
    public void onReceive(Context context, Intent intent) {
        showNotification(context);
    }

    public void showNotification(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent =PendingIntent.getActivity(context, 0, intent, 0);
        Notification notification = new Notification.Builder(context).setTicker("TickerTitle")
                .setContentTitle("Content title")
                .setContentText("Content Text")
                .setSmallIcon(R.drawable.ic_help_black_24dp)
                .setVibrate(new long[] { 1000, 1000})
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentIntent(pendingIntent).getNotification();

        notification.flags = Notification.FLAG_AUTO_CANCEL;
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, notification);
    }
}