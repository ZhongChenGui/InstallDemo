package com.learn.installdemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.learn.installdemo.receiver.PackageReceiver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 202;
    private String TAG = "MainActivity";

    private String[] permissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
//            "android.permission.REQUEST_INSTALL_PACKAGES"
    };
    private PackageReceiver packageReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        savePath = getSystemPath(this);
//        requestInstallPermission();
        requestPermission();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addDataScheme("package");
        packageReceiver = new PackageReceiver();
        registerReceiver(packageReceiver, filter);
        this.startService(new Intent(this, InstallService.class));
        finish();
//        installApk("/sdcard/app-release.apk");
//        installApk("/storage/emulated/0/Android/data/com.linkbroad.bvtv.main/files/launcher_tcl_cloud_g62e_v1.11_23091418.apk");

    }

    private void requestPermission() {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "requestPermission: 请求权限..............");
                ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE);
                return;
            }
        }
//        getDownApp();
//        installApk("/storage/emulated/0/Android/data/com.learn.installdemo/files/com.linkbroad.bvtv.ui.apk");
//        installApk("/sdcard/lbv2022_player_sony_x85k_cloud_v1.0.1_23082418.apk");
//        installApk("/sdcard/launcher_cloud_tcl_g65e_sign_v1.8_23050915.apk");

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startInstallPermissionSettingActivity(Context context) {
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + context.getPackageName()));
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(intent);
        this.startActivityForResult(intent, 101);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult: ............." + requestCode);
        int size = 0;
        if (REQUEST_CODE == requestCode && permissions.length > 0) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == -1) {
                    requestPermission();
                    Log.d(TAG, "onRequestPermissionsResult: " + permissions[i] + "  权限拒绝");
                    return;
                } else {
                    size++;
                    Log.d(TAG, "onRequestPermissionsResult: " + permissions[i] + "权限以获取");
                }
            }
        }
        if (size == permissions.length) {
//            getDownApp();
//            installApk("/sdcard/lbv2022_player_sony_x85k_cloud_v1.0.1_23082418.apk");
//            installApk("/sdcard/launcher_cloud_tcl_g65e_sign_v1.8_23050915.apk");
//            installApk("/storage/emulated/0/Android/data/com.linkbroad.bvtv.main/files/launcher_tcl_cloud_g62e_v1.11_23091418.apk");
//            installApk("/storage/emulated/0/Android/data/com.learn.installdemo/files/com.linkbroad.bvtv.ui.apk");

        }
    }

    private void requestInstallPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            boolean hasInstallPermission = getPackageManager().canRequestPackageInstalls();
            if (!hasInstallPermission) {
                Log.e(TAG, "onCreate: 没有获取到安装未知应用权限");
                Toast.makeText(this, "Enable the Install unknown apps permission for the Launcher", Toast.LENGTH_SHORT).show();
                startInstallPermissionSettingActivity(this);
            } else {
                Log.d(TAG, "requestInstallPermission: 以获取安装未知应用权限");
                requestPermission();
            }
        } else {
            requestPermission();
        }
    }

    private void getDownApp() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = "https://bv.linkbroad.com/api/box/upgrade";
                HttpUtil http = new HttpUtil(url);
                String rs = http.getString("f87344fd-b169-386c-8367-ad3ab78fc74b");
                JSONArray datas = null;
                if (rs == null) {
                    return;
                }

                try {
                    Log.d(TAG, "run: rs:" + rs);
                    JSONObject json = new JSONObject(rs);
                    datas = json.optJSONArray("data");

                    if (!json.optString("ret").equals("0")) {
                        return;
                    }
                    AppVO vo = new AppVO();
                    for (int i = 0; i < datas.length(); i++) {
                        // vo.device =  datas.optJSONObject(i).optString("device");
                        vo.name = datas.optJSONObject(i).optString("pkname");
                        vo.apkPath = datas.optJSONObject(i).optString("url");
                        vo.version = datas.optJSONObject(i).optString("version");
                        vo.apptype = datas.optJSONObject(i).optString("apptype");

                    }
                    Log.d(TAG, "run: vo = " + vo);
                    startDownload(vo);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static String savePath;

    public String getSystemPath(Context context) {
        String filePath = "";
        if (isUseDefinedPath)
            return definedPath;
        if (checkSDCard()) {
            filePath = context.getExternalFilesDir(null) + File.separator;
        } else {
            filePath = context.getCacheDir().getAbsolutePath() + File.separator;
        }
        return filePath;
    }

    public static String definedPath = "/data/data/com.linkbroad.main/";
    public static boolean isUseDefinedPath = false;//使用自定义路径


    public static boolean checkSDCard() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }


    private void startDownload(AppVO app) {
        String appName = savePath + app.name + ".apk";
        Log.d(TAG, "onProgress: appName = " + appName);
        FileDownManager.addDownThread(app.apkPath, appName, 0, new FileDownManager.ProgressCall() {
            @Override
            public void onProgress(int state, long totalSzie, long downloadCount) {
                if (state == FileDownManager.STATUS_SUCCESSFUL) {
                    Log.d(TAG, "下载完成" + app.apkPath);
                    try {
                        Runtime.getRuntime().exec("chmod 777 " + appName);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    installApk(appName);
                } else if (state == FileDownManager.STATUS_RUNNING) {

                    Log.d("updata", "正在下载" + app.apkPath + " total size" + totalSzie + " download size" + downloadCount);
                    //  dispatchMsg(MsgCmd.LogMsg,"正在下载"+app.name+" total size"+totalSzie+" download size"+downloadCount);
                } else {
                    Log.d(TAG, "下载" + app.apkPath + "出错");


                }
            }
        });
    }

    private void installApk(String apkFilePath) {
        Log.d(TAG, "installApk: ...................");
        try {
            Intent installIntent = new Intent();
            installIntent.setAction(Intent.ACTION_VIEW);
            installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
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
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri uri = FileProvider.getUriForFile(this,
                    "com.learn.installdemo.fileprovider", f);//这一部分要与前面对应
            this.grantUriPermission(this.getPackageName(), uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            installIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            installIntent.setDataAndType(uri, type);

//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    boolean hasInstallPermission = this.getPackageManager().canRequestPackageInstalls();
//                    if (!hasInstallPermission) {
//                        Toast.makeText(this, "请开启安装未知应用权限", Toast.LENGTH_SHORT).show();
//                        startInstallPermissionSettingActivity(this);
//                    }
//                }
//            } else {
//                installIntent.setDataAndType(Uri.fromFile(f), type);
//            }
            this.startActivityForResult(installIntent, 202);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode = " + requestCode);
    }
}