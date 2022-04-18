package com.pedec;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;

public class systemInfoService extends Service {
    private static ActivityManager actManager;
    private static Intent batteryStatus;
    public String TAG="PADEC";

//    /**
//     * Class for clients to access.  Because we know this service always
//     * runs in the same process as its clients, we don't need to deal with
//     * IPC. This binds the service so that it can be used in the background
//     */
//    public class systemInfoServiceBinder extends Binder {
//        systemInfoService getService() {
//            // Return this instance of LocalService so clients can call public methods
//            return systemInfoService.this;
//        }
//    }
//
//    // this is the service binder used to bind this service to the application
//    private final IBinder binder = new systemInfoServiceBinder();

    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private final ISystemInfoService.Stub binder = new ISystemInfoService.Stub() {
        @Override
        public String getSystemInfo(){
            try{
                getAllSystemInfo();
                return getAllSystemInfo();
            } catch (Exception e){
                return null;
            }
        }
    };

//    @Override
//    public void onCreate() {
//        super.onCreate();
//
////        mMessageListener = new MessageListener() {
////            @Override
////            public void onFound(Message message) {
////                Log.d(TAG, "Found message: " + new String(message.getContent()));
////                if (new String(message.getContent()).equals("Request for system Info")){
////                    try {
////                        publish(getSystemInfo());
////                    } catch (Exception e) {
////                        e.printStackTrace();
////                    }
////                }
////            }
////
////            @Override
////            public void onLost(Message message) {
////                Log.d(TAG, "Lost sight of message: " + new String(message.getContent()));
////            }
////        };
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        Log.i(TAG, "Received start id " + startId + ": " + intent);
//
////        try {
////            subscribe();
////            backgroundSubscribe();
////            Log.d(TAG, "Subscribe Successfully");
////        } catch (Exception e) {
////            Log.e(TAG, "Failed to Subscribe " + e);
////        }
//
//        return START_NOT_STICKY;
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
////        unsubscribe();
////        unpublish();
//        // // Tell the user we stopped.
//        //Toast.makeText(this, "Destroy System Info Service", Toast.LENGTH_SHORT).show();
//    }
//
////    void publish(String message) {
////        Log.i(TAG, "Publishing message: " + message);
////        mActiveMessage = new Message(message.getBytes());
////        Nearby.getMessagesClient(this).publish(mActiveMessage);
////    }
////
////    void unpublish() {
////        Log.i(TAG, "Unpublishing.");
////        if (mActiveMessage != null) {
////            Nearby.getMessagesClient(this).unpublish(mActiveMessage);
////            mActiveMessage = null;
////        }
////    }
////
////    // Subscribe to receive messages.
////    void subscribe() {
////        try {
////            SubscribeOptions options = new SubscribeOptions.Builder()
////                    .setStrategy(Strategy.DEFAULT)
////                    .build();
////            Nearby.getMessagesClient(this).subscribe(mMessageListener, options);
////            Log.i(TAG, "Subscribing.");
////        } catch (Exception e){
////            Log.e(TAG, "Failed to do Subscribe");
////        }
////    }
////
////    // Subscribe to messages in the background.
////    private void backgroundSubscribe() {
////        SubscribeOptions options = new SubscribeOptions.Builder()
////                .setStrategy(Strategy.DEFAULT)
////                .build();
////        Nearby.getMessagesClient(this).subscribe(getPendingIntent(), options);
////        Log.i(TAG, "Subscribing for background updates.");
////    }
////
////    private void unsubscribe() {
////        Log.i(TAG, "Unsubscribing.");
////        Nearby.getMessagesClient(this).unsubscribe(mMessageListener);
////    }
////
////    private PendingIntent getPendingIntent() {
////        return PendingIntent.getBroadcast(this, 0, new Intent(this, BeaconMessageReceiver.class),
////                PendingIntent.FLAG_UPDATE_CURRENT);
////    }

    // Systen Info
    public String getAllSystemInfo() throws Exception {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        batteryStatus = this.registerReceiver(null, ifilter);
        //Declaring and Initializing the ActivityManager
        actManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);

        String CPUInfo = systemInfo.getCPUInfo() + "\n";
//        String GPUInfo = systemInfo.getGPUInfo(actManager);
        String RAMInfo = systemInfo.getRAMInfo(actManager);
        String PowerInfo = systemInfo.getPowerInfo(batteryStatus);
        String StoragInfo = systemInfo.getStorageInfo();

//        return CPUInfo+GPUInfo+RAMInfo+PowerInfo+StoragInfo;
        return CPUInfo+RAMInfo+PowerInfo+StoragInfo;
    }
}