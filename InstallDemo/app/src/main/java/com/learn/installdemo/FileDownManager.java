package com.learn.installdemo;

import android.util.Log;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by ken.chen on 2018/6/22.
 */

public class FileDownManager {
    private static final String TAG = "FileDownManager";
    public static int STATUS_RUNNING = 1;
    public static int STATUS_SUCCESSFUL = 2;
    public static int STATUS_FAILED = 3;
    private static HashMap<String, Thread> downThreads = new HashMap<String, Thread>();
    private static HashMap<String, Boolean> downStreams = new HashMap<String, Boolean>();
    public static long addDownThread(final String downloadUrl, final String saveUrl, final long currentSize, final ProgressCall call){
        Thread downThread = new Thread(new Runnable() {
            @Override
            public void run() {
                File f = new File(saveUrl);
                if(f.exists())
                    f.delete();
                try {
                    downloadFile(downloadUrl,saveUrl,currentSize,call);
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
            }
        });
        downThread.start();
        return downThread.getId();
    }
    public static void cancelDown(long id){
        downStreams.put(id+"", false);
    }
    public static long downloadFile(String downloadUrl, String saveUrl, final long currentSize, ProgressCall call) throws Exception {
        ProgressCall callBack = call;
        int updateTotalSize = 0;
        Log.d(TAG, "downloadFile: saveUrl = " + saveUrl);
        File saveFile = new File(saveUrl+".temp");
        HttpURLConnection httpConnection = null;
        InputStream is = null;
        RandomAccessFile fos = null;
        long totalSize = currentSize;
        long curPosition =0;
        try {
            URL url = new URL(downloadUrl);
            httpConnection = (HttpURLConnection)url.openConnection();
            httpConnection.setAllowUserInteraction(true);
          // HttpURLConnection temp =  (HttpURLConnection)url.openConnection();
        //   updateTotalSize = temp.getContentLength();
         //  temp.disconnect();
           httpConnection.setRequestProperty("User-Agent", "PacificHttpClient");
//
//            if(currentSize > 0) {
//                curPosition = currentSize;
//                httpConnection.setRequestProperty("Range", "bytes=" + curPosition+ "-"+updateTotalSize);
//                if(saveFile.exists()){
//                    TVLog.log("DownloadFile", "downloadFile savefile "+" "+saveFile.getName()+" "+saveFile.length());
//                }
//            }
            Log.d("DownloadFile", "downloadFile start "+curPosition+" saveFile "+saveFile.exists());
            Log.d("DownloadFile", "downloadFile: saveFile = " + saveFile);

            httpConnection.setConnectTimeout(60*1000);
            httpConnection.setReadTimeout(60*1000);

            if (httpConnection.getResponseCode() == 404) {
                callBack.onProgress(STATUS_FAILED,404, 0);
                return 0;
            }
            is = httpConnection.getInputStream();
            updateTotalSize = httpConnection.getContentLength();
            long id = Thread.currentThread().getId();
            downStreams.put(id+"", true);
            fos = new RandomAccessFile(saveFile, "rw");

            byte buffer[] = new byte[300*1024];
            int readsize = 0;
            fos.seek(curPosition);
            while( downStreams.get(id+"")&&(curPosition < updateTotalSize)){
                readsize = is.read(buffer);
                fos.write(buffer, 0, readsize);
                curPosition = curPosition + readsize;
                //为了防止频繁的通知导致应用吃紧，百分比增加5才通知一次
                callBack.onProgress(STATUS_RUNNING,updateTotalSize, curPosition);
            }
            if(downStreams.get(id+"")&&is!=null && callBack!=null){
                File newFile = new File(saveUrl);
                if(!saveFile.exists()){
                    callBack.onProgress(STATUS_FAILED,updateTotalSize, 0);
                }else{
                    saveFile.renameTo(newFile);
                    callBack.onProgress(STATUS_SUCCESSFUL,updateTotalSize, curPosition);
                }
            }else if(!downStreams.get(id+"")){
                Log.d("DownloadFile", "........downloadFile cancel.......");
            }
        } catch(Exception e){
            e.printStackTrace();
            if(saveFile.exists()){
                saveFile.delete();
            }
            if(callBack!=null)
                callBack.onProgress(STATUS_FAILED,0, 0);
        } finally {
            if(httpConnection != null) {
                httpConnection.disconnect();
            }
            if(is != null) {
                is.close();
            }
            if(fos != null) {
                fos.close();
            }
            Log.d("DownloadFile", "downloadFile close");
        }
        return totalSize;
    }
    public static interface ProgressCall{
        public void onProgress(int state, long totalSzie, long downloadCount);
    }

}
