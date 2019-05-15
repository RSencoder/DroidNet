package com.mingxiangChen.droidnet.fragment;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.mingxiangChen.droidnet.R;

/**
 * It contains the information of a packet captured by 
 * {@link com.mingxiangChen.droidnet.interceptor.CaptureRawInterceptor}.
 * 
 * @author RSmxchen
 * @since 2019-05-12 14:20
 */
public class PacketShowInfo {
    
    private Drawable appIcon;
    private String pkgName;
    private String protocol;
    private String session;
    private String packetSize;
    private String bufferInAscii;
    private ApplicationInfo applicationInfo;
    
    public PacketShowInfo(Context context, int uid) {
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(packageManager.getNameForUid(uid), 
                    PackageManager.MATCH_UNINSTALLED_PACKAGES);
            this.applicationInfo = packageInfo.applicationInfo;
            this.appIcon = applicationInfo.loadIcon(packageManager);
            this.pkgName = (String) applicationInfo.loadLabel(packageManager);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public String getPkgName() {
        return pkgName;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getPacketSize() {
        return packetSize;
    }

    public void setPacketSize(String pakcetSize) {
        this.packetSize = pakcetSize;
    }

    public String getBufferInAscii() {
        return bufferInAscii;
    }

    public void setBufferInAscii(String bufferInAscii) {
        this.bufferInAscii = bufferInAscii;
    }

    public ApplicationInfo getApplicationInfo() {
        return applicationInfo;
    }
}
