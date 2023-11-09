package com.learn.installdemo.receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.widget.Toast;

public class InstallResultReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            final int status = intent.getIntExtra(PackageInstaller.EXTRA_STATUS,
                    PackageInstaller.STATUS_FAILURE);
            if (status == PackageInstaller.STATUS_SUCCESS) {
                // success
                Toast.makeText(context,"安装成功",Toast.LENGTH_LONG).show();
                //安装成功后再次重新打开页面
               // restartAPK(context);
            } else {
                //Log.e(TAG, intent.getStringExtra(PackageInstaller.EXTRA_STATUS_MESSAGE));
                Toast.makeText(context,"安装失败",Toast.LENGTH_LONG).show();

            }
        }
    }

    private void restartAPK(Context context) {
/*        String cmd= "sleep 15; am start -n com.tpv.xmic.help.ebony2k15/com.tpv.xmic.help.ebony2k15.HelpActivity";
        //Runtime对象
        Runtime runtime = Runtime.getRuntime();
        Log.d("CMSIntentService","restartAPK 1");
        try {
            Process localProcess = runtime.exec("su");
            OutputStream localOutputStream = localProcess.getOutputStream();
            DataOutputStream localDataOutputStream = new DataOutputStream(localOutputStream);
            localDataOutputStream.writeBytes(cmd);
            localDataOutputStream.flush();
            Log.d("CMSIntentService","restartAPK 2");
        } catch (IOException e) {
            Log.d("CMSIntentService","strLine:"+e.getMessage());
            e.printStackTrace();

        }*/
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ComponentName componentName = new ComponentName("com.tpv.xmic.cms.client", "com.tpv.xmic.cms.client.MainActivity");
        intent.setComponent(componentName);
        try {
            context.startActivity(intent);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
