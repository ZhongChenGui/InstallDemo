package com.learn.installdemo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class PackageReceiver extends BroadcastReceiver {
    private String TAG = "PackageReceiver";
    static final String ACTION = "android.intent.action.PACKAGE_ADDED";
    static final String ACTION1 = "android.intent.action.PACKAGE_REPLACED";
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        Log.d(TAG,"onReceive "+intent.getAction());
        if (intent.getAction().equals(ACTION)) {
            final String packageName = intent.getData().getSchemeSpecificPart();
            Log.d(TAG,"package add "+packageName);
            if ((null != packageName) && (packageName.length() > 0)) {
                if (packageName.equals("com.linkbroad.lbv.main") || packageName.equals("com.linkbroad.bvtv.main")) {
                    Intent i = context.getPackageManager().getLaunchIntentForPackage(packageName);
                    if (null != i) {
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(i);
                    }
                    else {
                        Log.d(TAG, "Can not get package" + packageName);
                    }
                }
            }
        }
    }
}
