package com.learn.installdemo;

/**
 * Created by howe.zhong
 * on 2023/3/24  20:28
 */
public class AppVO {
    public String device = "";
    public String name = "";
    public String apkPath = "";
    public String version = "";
    public String apptype = "";

    @Override
    public String toString() {
        return "AppVO{" +
                "device='" + device + '\'' +
                ", name='" + name + '\'' +
                ", apkPath='" + apkPath + '\'' +
                ", version='" + version + '\'' +
                ", apptype='" + apptype + '\'' +
                '}';
    }
}
