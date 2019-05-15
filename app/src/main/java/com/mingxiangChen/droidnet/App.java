package com.mingxiangChen.droidnet;

import android.app.Application;
import android.content.Context;

import com.github.megatronking.netbare.NetBare;
import com.github.megatronking.netbare.NetBareListener;
import com.github.megatronking.netbare.NetBareUtils;
import com.github.megatronking.netbare.ssl.JKS;
import com.mingxiangChen.droidnet.gateway.DroidNetVirtualGatewayFactory;

import org.litepal.LitePal;

import java.util.LinkedHashSet;
import java.util.Set;

import me.weishu.reflection.Reflection;

public class App extends Application {
    private static String JKS_ALIAS = "DroidNet";
    private static App sInstance;

    public static App getInstance() {
        return sInstance;
    }

    private static JKS mJKS;
    
    // 应用状态变量 默认为false
    public boolean mCaptureSwitcher;   // 是否正在抓包
    public boolean mFirewallSwitcher;  // 是否开启防火墙
    public boolean mVPNSwitcher;       // 是否开启VPN
    
    public Set<SwitcherListener> mSwitcherListeners;  // 观察者模式：所有VirtualGateway都关注抓包与防火墙状态
    
    public DroidNetVirtualGatewayFactory mDroidNetVirtualGatewayFactory;

    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化litepal
        LitePal.initialize(this);
        
        mSwitcherListeners = new LinkedHashSet<>();

        mDroidNetVirtualGatewayFactory = new DroidNetVirtualGatewayFactory();
        
        sInstance = this;
        // 创建自签证书
        mJKS = new JKS(this, JKS_ALIAS, JKS_ALIAS.toCharArray(), JKS_ALIAS,JKS_ALIAS,
                JKS_ALIAS, JKS_ALIAS, JKS_ALIAS);
        // 初始化NetBare
        NetBare.get().attachApplication(this, BuildConfig.DEBUG);
    }

    public static JKS getJKS() { return mJKS; }
    public static String getJKS_ALIAS() { return JKS_ALIAS; }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // On android Q, we can't access Java8EngineWrapper with reflect.
        if (NetBareUtils.isAndroidQ()) {
            Reflection.unseal(base);
        }
    }

    /**
     * Register a {@link com.github.megatronking.netbare.gateway.VirtualGateway} which monitors the
     * switcher state.
     * 
     * @param listener a {@link com.github.megatronking.netbare.gateway.VirtualGateway} 
     *                 which implements {@link SwitcherListener}.
     */
    public void registerSwitcherListener(SwitcherListener listener) {
        mSwitcherListeners.add(listener);
    }

    /**
     * Remove a {@link com.github.megatronking.netbare.gateway.VirtualGateway} which monitors the
     * switcher state.
     * 
     * @param listener a {@link com.github.megatronking.netbare.gateway.VirtualGateway} 
     *                 which implements {@link SwitcherListener}.
     */
    public void removeSwitcherListener(SwitcherListener listener) {
        mSwitcherListeners.remove(listener);
    }

    /**
     * notify registered listeners the user has clicked capture start button.
     */
    public void notifyCaptureOn() {
        for (SwitcherListener listener: new LinkedHashSet<>(mSwitcherListeners)) {
            listener.captureOn();
        }
    }

    /**
     * notify registered listeners the user has clicked capture stop button.
     */
    public void notifyCaptureOff() {
        for (SwitcherListener listener: new LinkedHashSet<>(mSwitcherListeners)) {
            listener.captureOff();
        }
    }

    /**
     * notify registered listeners the user has clicked firewall start button.
     */
    public void notifyFirewallOn() {
        for (SwitcherListener listener: new LinkedHashSet<>(mSwitcherListeners)) {
            listener.firewallOn();
        }
    }

    /**
     * notify registered listeners the user has clicked firewall stop button.
     */
    public void notifyFirewallOff() {
        for (SwitcherListener listener: new LinkedHashSet<>(mSwitcherListeners)) {
            listener.firewallOff();
        }
    }
}
