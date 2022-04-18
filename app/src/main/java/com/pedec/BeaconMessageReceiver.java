//package com.pedec;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.util.Log;
//
//import com.google.android.gms.nearby.messages.Message;
//import com.google.android.gms.nearby.messages.MessageListener;
//
//import com.google.android.gms.nearby.Nearby;
//
//public class BeaconMessageReceiver extends BroadcastReceiver {
//    private static final String TAG = "PADEC";
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        Nearby.Messages.handleIntent(intent, new MessageListener() {
//            @Override
//            public void onFound(Message message) {
//                Log.i(TAG, "Found message via PendingIntent: " + message);
//            }
//
//            @Override
//            public void onLost(Message message) {
//                Log.i(TAG, "Lost message via PendingIntent: " + message);
//            }
//        });
//    }
//}
