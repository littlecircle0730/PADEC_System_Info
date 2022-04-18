package com.pedec;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.StatFs;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    public String TAG="PADEC";
    private systemInfoService mSystemInfoService;
    private GLSurfaceView glSurfaceView;
    private StringBuilder sb;
    ActivityManager actManager;
    Intent batteryStatus;

    protected ISystemInfoService iSystemInfoService = null;

    // boolean indicating whether the Blend service is bound
    private boolean mBound = false;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        batteryStatus = this.registerReceiver(null, ifilter);
        //Declaring and Initializing the ActivityManager
        actManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);

//        final Button requestButton = findViewById(R.id.request);
        final Button startServiceButton = findViewById(R.id.startService);

//        requestButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//            }
//        });

        startServiceButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TextView systemInfo = findViewById(R.id.system_Info);
                try {
                    systemInfo.setText(iSystemInfoService.getSystemInfo());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        if (!mBound) {
            // Bind to the service
            bindService(new Intent(this, systemInfoService.class), mConnection,
                    Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
        super.onDestroy();
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
//            systemInfoService.systemInfoServiceBinder binder = (systemInfoService.systemInfoServiceBinder) service;
//            mSystemInfoService = binder.getService();
//            mBound = true;
            iSystemInfoService = ISystemInfoService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
//            mBound = false;
            Log.e(TAG, "Service has unexpectedly disconnected");
            iSystemInfoService = null;
        }
    };
}