package pl.dweb.moneysavingchallenge;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

/**
 * Created by Admin on 01.04.2017.
 */

public class NotificationService extends JobService {

    @Override
    public boolean onStartJob(JobParameters job) {
        Intent intent = new Intent();
        PendingIntent pendingIntent =PendingIntent.getActivity(this, 0, intent, 0);
        Notification notification = new Notification.Builder(this).setTicker("TickerTitle")
                .setContentTitle("Content title")
                .setContentText("Content Text")
                .setSmallIcon(R.drawable.ic_cup_with_star_icon)
                .setContentIntent(pendingIntent).getNotification();

        notification.flags = Notification.FLAG_AUTO_CANCEL;
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, notification);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }
}
