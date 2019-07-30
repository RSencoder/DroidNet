package com.mingxiangChen.droidnet.interceptor;

import android.support.annotation.NonNull;

import com.github.megatronking.netbare.gateway.Interceptor;
import com.github.megatronking.netbare.gateway.InterceptorFactory;
import com.mingxiangChen.droidnet.fragment.ToastFirewallHandler;

/**
 * A {@link InterceptorFactory} that creates {@link FirewallInterceptor}.
 * 
 * @author RSmxchen
 * @since 2019-06-03 17:48
 */
public class FirewallInterceptorFactory implements InterceptorFactory {
    
    private ToastFirewallHandler mHandler;
    
    public void setHandler(ToastFirewallHandler handler) {
        this.mHandler = handler;
    }
    
    @NonNull
    @Override
    public Interceptor create() {
        FirewallInterceptor firewallInterceptor = new FirewallInterceptor();
        firewallInterceptor.setHandler(mHandler);
        return firewallInterceptor;
    }
}
