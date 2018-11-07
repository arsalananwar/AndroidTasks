package app.task2.com;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import java.util.concurrent.TimeUnit;

import static app.task2.com.Constansts.NOTIFICATION_CHANNEL_ID;
import static app.task2.com.Constansts.importance;

/**
 * Created by ARK on 11/7/2018.
 */

public class BackgroundService extends Service
{
    private boolean isTimeCompleted;
    private CountDownTimer countDownTimer;
    public String TIMESTAMP = "timestemp";
    public String MINUTES = "minutes";
    public String SECONDS = "seconds";
    private int sec = 0, min = 0, minutes = 4, seconds = 59;

    @Override
    public void onCreate()
    {
        super.onCreate();
    }

    private void createNotification()
    {
        final NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            manager.createNotificationChannel(notificationChannel);
        }

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle("Scheduled Notification")
                .setAutoCancel(true)
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setContentText("Time Left :" + minutes + ":" + seconds)
                .setAutoCancel(false)
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher_background).setChannelId(NOTIFICATION_CHANNEL_ID);


        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                1,
                new Intent(this, MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        manager.notify(1, builder.build());


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        super.onStartCommand(intent, flags, startId);
        //start countdcownTimer for background notification to alive
        startCountDownTimer(getextraValaue(intent));
        return START_STICKY;
    }

    private int getextraValaue(Intent intent)
    {
        // getting and setting countdowntimer previous value after service recreated by broadcast
        if (intent.getExtras() != null)
        {
            long timeStempDifference = System.currentTimeMillis() - intent.getLongExtra(TIMESTAMP, 0);
            int secondDifference = (int) TimeUnit.MILLISECONDS.toSeconds(timeStempDifference);
            min = intent.getIntExtra(MINUTES, 0);
            sec = intent.getIntExtra(SECONDS, 0);

            int totalSecnds = (min * 60) + sec + secondDifference;
            minutes = 0;
            seconds = 0;
            min = totalSecnds / 60;
            seconds = totalSecnds % 60;

            minutes = 4 - min;
            seconds = 59 - sec;

            if (((min * 60) * 1000) > 300000)
            {
                min = (((300000 / 1000) / 60));
                sec = 0;
                minutes = 0;
                seconds = 0;
            }
            totalSecnds = (min * 60) + sec;
            int totalRunningSeconds = (int) (totalSecnds * 1000);

            int remainingSeconds = (300000) - totalRunningSeconds;

            if (remainingSeconds < 0)
                remainingSeconds = 0;

            return remainingSeconds;// remaning seconds
        } else
            return 300000;
    }


    @Override
    public void onDestroy()
    {
        super.onDestroy();
        stopCountDowntimertask();
        if (!isTimeCompleted)
        {
            // start broadcast
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction("restartservice");
            long timeStemp = System.currentTimeMillis();
            broadcastIntent.putExtra(TIMESTAMP, timeStemp);
            broadcastIntent.putExtra(MINUTES, min);
            broadcastIntent.putExtra(SECONDS, sec);
            broadcastIntent.setClass(this, Restarter.class);
            this.sendBroadcast(broadcastIntent);
        }
    }

    public void startCountDownTimer(long miliseconds)
    {
        countDownTimer = new CountDownTimer(miliseconds, 1000)
        {

            public void onTick(long millisUntilFinished)
            {
                //here you can have your logic to set text to edittext
                sec++;
                seconds--;
                if (sec == 59)
                {
                    min++;
                    sec = 0;

                    minutes--;
                    seconds = 59;
                }
                if (minutes < 0)
                {
                    countDownTimer.onFinish();
                    countDownTimer.cancel();
                    finisServiceAndClearNotification();
                } else
                    createNotification();
            }

            public void onFinish()
            {
                // Time Finish service closed
                finisServiceAndClearNotification();
            }

        }.start();
    }

    private void finisServiceAndClearNotification()
    {
        isTimeCompleted = true;
        clearNotification();
        stopSelf();
    }

    private void clearNotification()
    {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(1);
    }

    public void stopCountDowntimertask()
    {
        if (countDownTimer != null)
        {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
}