package com.mingxiangChen.droidnet.interceptor;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.github.megatronking.netbare.gateway.AbstractRequestChain;
import com.github.megatronking.netbare.gateway.AbstractResponseChain;
import com.github.megatronking.netbare.gateway.Interceptor;
import com.github.megatronking.netbare.gateway.InterceptorFactory;
import com.github.megatronking.netbare.gateway.Request;
import com.github.megatronking.netbare.gateway.Response;
import com.mingxiangChen.droidnet.App;
import com.mingxiangChen.droidnet.table.InterceptRules;

import org.litepal.LitePal;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A {@link Interceptor} decides whether the firewall logic intercepts the packet or not, according
 * to the rules in {@link com.mingxiangChen.droidnet.table.InterceptRules}.
 * 
 * @author RSmxchen
 * @since 2018-05-01 20:23
 */
public class FirewallInterceptor implements Interceptor {
    
    private String TAG = "Firewall";
    private boolean isFirewallOn;
    private Charset mAscii;
    
    private FirewallInterceptor() {
        this.isFirewallOn = App.getInstance().mFirewallSwitcher;
        mAscii = Charset.forName("ascii");
    }
    
    public static InterceptorFactory createFactory() {
        return new InterceptorFactory() {
            @NonNull
            @Override
            public Interceptor create() {
                return new FirewallInterceptor();
            }
        };
    }

    @Override
    public void intercept(@NonNull AbstractRequestChain chain, @NonNull ByteBuffer buffer) throws IOException {
        if (isFirewallOn) {
            // 读取数据库获得拦截规则
            List<InterceptRules> rules = LitePal.findAll(InterceptRules.class);
            // 针对三个检索项的拦截标志
            boolean sessionHit = false, pkgNameHit = false, patternHit = false;
            // 将buffer拿到循环前面解码并filp()，避免每次都跑一遍，优化性能
            String decodeResult = mAscii.decode(buffer).toString();
            buffer.flip();
            // 遍历规则表
            for (InterceptRules rule: rules) {
                // 检测Session
                if (rule.getSession().equals("N/A")) {
                    sessionHit = true;
                } else {
                    String[] ruleIPandPort = rule.getSession().split(":");
                    String realSessionIP = chain.request().ip();
                    int realSessionPort = chain.request().port();
                    if (ruleIPandPort[0].equals(realSessionIP) 
                            && ruleIPandPort[1].equals(Integer.toString(realSessionPort))) {
                        sessionHit = true;
                    } else {
                        sessionHit = false;
                    }
                }
                // 检测应用包名
                if (rule.getPackageName().equals("N/A")) {
                    pkgNameHit = true;
                }  else {
                    PackageManager packageManager = App.getInstance().getPackageManager();
                    String pkgname = packageManager.getNameForUid(chain.request().uid());
                    if (rule.getPackageName().equals(pkgname)) {
                        pkgNameHit = true;
                    } else {
                        pkgNameHit = false;
                    }
                }
                // 检测正则表达式
                if (rule.getPattern().equals("N/A")) {
                    patternHit = true;
                } else {
                    Pattern pattern = Pattern.compile(rule.getPattern());
                    Matcher matcher = pattern.matcher(decodeResult);
                    if (matcher.find()) {
                        patternHit = true;
                    } else {
                        patternHit = false;
                    }
                }
                // 拦截动作判断
                if (sessionHit & pkgNameHit & patternHit) {
                    // 不调用chain.process(buffer)就表示阻止此包的传送，并调用return直接返回
                    Log.w(TAG, "被防火墙拦截，命中规则：\nSession:" + rule.getSession() 
                            + " PackageName:" + rule.getPackageName() + " Pattern:" + rule.getPattern());
                    return;
                }
            }
            // 如果循环跑完了，意味着没有命中任何一个规则，放行此包
            chain.process(buffer);
        } else {
            chain.process(buffer);
        }
    }

    @Override
    public void intercept(@NonNull AbstractResponseChain chain, @NonNull ByteBuffer buffer) throws IOException {
        chain.process(buffer);
    }

    @Override
    public void onRequestFinished(@NonNull Request request) {
        
    }

    @Override
    public void onResponseFinished(@NonNull Response response) {
        
    }

    public void setFirewallOn(boolean firewallOn) {
        isFirewallOn = firewallOn;
    }
}
