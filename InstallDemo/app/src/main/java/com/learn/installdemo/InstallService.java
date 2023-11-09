package com.learn.installdemo;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;

/**
 * Created by howe.zhong
 * on 2023/11/3  17:29
 */
public class InstallService extends Service {

    private static final String TAG = "InstallService";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: .........");
        installApk("/sdcard/launcher_lbv_tcl_cloud_G62E_v1.3_23110317.apk");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    private void installApk(String apkFilePath) {
        Log.d(TAG, "installApk: ...................");
        try {
            Intent installIntent = new Intent();
            installIntent.setAction(Intent.ACTION_VIEW);
//            installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            installIntent.addCategory(Intent.CATEGORY_DEFAULT);
            File f = new File(apkFilePath); //找到下载的文件路径

            String type = "application/vnd.android.package-archive";  // 固定格式
            try {
                String[] args2 = {"chmod", "777", apkFilePath};
                Runtime.getRuntime().exec(args2);
            } catch (IOException e) {
                e.printStackTrace();
            }
//        如果是android7之后
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri uri = FileProvider.getUriForFile(this,
                        "com.learn.installdemo.fileprovider", f);//这一部分要与前面对应
                this.grantUriPermission(this.getPackageName(), uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                installIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                installIntent.setDataAndType(uri, type);
            } else {
                installIntent.setDataAndType(Uri.fromFile(f), type);
            }
            startActivity(installIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
