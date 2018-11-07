package app.task2.com;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

/**
 * Created by ARK on 11/7/2018.
 */

public class Restarter extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        //This Broadcast Restarted the service
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            context.startForegroundService(new Intent(context, BackgroundService.class).putExtra(Constansts.TIMESTAMP, intent.getLongExtra(Constansts.TIMESTAMP, 0))
                    .putExtra(Constansts.MINUTES, intent.getIntExtra(Constansts.MINUTES, 0)).putExtra(Constansts.SECONDS, intent.getIntExtra(Constansts.SECONDS, 0)));
        } else
        {
            context.startService(new Intent(context, BackgroundService.class).putExtra(Constansts.TIMESTAMP, intent.getLongExtra(Constansts.TIMESTAMP, 0))
                    .putExtra(Constansts.MINUTES, intent.getIntExtra(Constansts.MINUTES, 0)).putExtra(Constansts.SECONDS, intent.getIntExtra(Constansts.SECONDS, 0)));
        }
    }
}
