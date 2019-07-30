package com.mingxiangChen.droidnet.fragment;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

/**
 * A {@link Handler} designed for {@link com.mingxiangChen.droidnet.interceptor.FirewallInterceptor}, 
 * which lets FirewallInterceptor show a interception toast in proxy server thread.
 * 
 * @author RSmxchen
 * @since 2019-06-03 18:00
 */
public class ToastFirewallHandler extends Handler {
    
    private Context mContext;

    public void setContext(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void handleMessage(Message msg) {
        String toastString = (String) msg.obj;
        Toast.makeText(mContext, toastString, Toast.LENGTH_SHORT).show();
    }
}
