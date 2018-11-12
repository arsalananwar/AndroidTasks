package app.task2.com;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;

public class MainActivity extends AppCompatActivity
{
    Intent mServiceIntent;
    private BackgroundService mYourService;
    @BindView(R.id.tvTimer)
    TextView tvTimer;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mYourService = new BackgroundService();
        mServiceIntent = new Intent(this, mYourService.getClass());
    }
    
    public void startService(View view)
    {
        if (!isMyServiceRunning(mYourService.getClass()))
        {
            //Starting Background Service
            startService(mServiceIntent);
        } else
        {
            Toast.makeText(this, "Background Notification is already running", Toast.LENGTH_SHORT).show();
        }
    }
    
    
    // Checking if service is running
    private boolean isMyServiceRunning(Class<?> serviceClass)
    {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
        {
            if (serviceClass.getName().equals(service.service.getClassName()))
            {
                return true;
            }
        }
        return false;
    }
    
    @Override
    protected void onDestroy()
    {
        //stop service and restarted by broadcast through service ondestroy
        if (mServiceIntent != null)
            stopService(mServiceIntent);
        super.onDestroy();
    }
}

