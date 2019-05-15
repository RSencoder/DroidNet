package com.mingxiangChen.droidnet.gateway;

import com.github.megatronking.netbare.gateway.Interceptor;
import com.github.megatronking.netbare.gateway.InterceptorFactory;
import com.github.megatronking.netbare.gateway.Request;
import com.github.megatronking.netbare.gateway.RequestChain;
import com.github.megatronking.netbare.gateway.Response;
import com.github.megatronking.netbare.gateway.ResponseChain;
import com.github.megatronking.netbare.gateway.VirtualGateway;
import com.github.megatronking.netbare.net.Session;
import com.mingxiangChen.droidnet.App;
import com.mingxiangChen.droidnet.SwitcherListener;
import com.mingxiangChen.droidnet.interceptor.CaptureRawInterceptor;
import com.mingxiangChen.droidnet.interceptor.FirewallInterceptor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * A {@link VirtualGateway} designed for packet capture and firewall usage. It contains two 
 * {@link com.github.megatronking.netbare.gateway.Interceptor}, the first one for packet capture, the
 * second one for firewall.
 * 
 * @author RSmxchen
 * @since 2019-05-01 16:28
 */
public class DroidNetVirtualGateway extends VirtualGateway implements SwitcherListener {

    private final List<Interceptor<Request, RequestChain, Response, ResponseChain>> mInterceptors;
    private App app = App.getInstance();
    
    protected DroidNetVirtualGateway(Session session, Request request, Response response, 
                                     List<InterceptorFactory<Request, RequestChain, Response, ResponseChain>> factories) {
        super(session, request, response);
        this.mInterceptors = new ArrayList<>(factories.size());
        for (InterceptorFactory<Request, RequestChain, Response, ResponseChain> factory : factories) {
            mInterceptors.add(factory.create());
        }
        
        app.registerSwitcherListener(this);
    }

    @Override
    public void onRequest(ByteBuffer buffer) throws IOException {
        new RequestChain(mRequest, mInterceptors).process(buffer);
    }

    @Override
    public void onResponse(ByteBuffer buffer) throws IOException {
        new ResponseChain(mResponse, mInterceptors).process(buffer);
    }

    @Override
    public void onRequestFinished() {
        for (Interceptor<Request, RequestChain, Response, ResponseChain> interceptor: mInterceptors) {
            interceptor.onRequestFinished(mRequest);
        }
        
        app.removeSwitcherListener(this);
    }

    @Override
    public void onResponseFinished() {
        for (Interceptor<Request, RequestChain, Response, ResponseChain> interceptor: mInterceptors) {
            interceptor.onResponseFinished(mResponse);
        }
        
        app.removeSwitcherListener(this);
    }
    
    @Override
    public void captureOn() {
        for (Interceptor interceptor: mInterceptors) {
            if (interceptor instanceof CaptureRawInterceptor) {
                ((CaptureRawInterceptor) interceptor).setCapture(true);
            }
        }
    }

    @Override
    public void captureOff() {
        for (Interceptor interceptor: mInterceptors) {
            if (interceptor instanceof CaptureRawInterceptor) {
                ((CaptureRawInterceptor) interceptor).setCapture(false);
            }
        }
    }

    @Override
    public void firewallOn() {
        for (Interceptor interceptor: mInterceptors) {
            if (interceptor instanceof FirewallInterceptor) {
                ((FirewallInterceptor) interceptor).setFirewallOn(true);
            }
        }
    }

    @Override
    public void firewallOff() {
        for (Interceptor interceptor: mInterceptors) {
            if (interceptor instanceof FirewallInterceptor) {
                ((FirewallInterceptor) interceptor).setFirewallOn(false);
            }
        }
    }
}
