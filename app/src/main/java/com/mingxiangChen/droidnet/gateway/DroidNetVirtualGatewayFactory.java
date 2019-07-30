package com.mingxiangChen.droidnet.gateway;

import com.github.megatronking.netbare.gateway.InterceptorFactory;
import com.github.megatronking.netbare.gateway.Request;
import com.github.megatronking.netbare.gateway.RequestChain;
import com.github.megatronking.netbare.gateway.Response;
import com.github.megatronking.netbare.gateway.ResponseChain;
import com.github.megatronking.netbare.gateway.VirtualGateway;
import com.github.megatronking.netbare.gateway.VirtualGatewayFactory;
import com.github.megatronking.netbare.net.Session;
import com.mingxiangChen.droidnet.fragment.PacketCapturedListener;
import com.mingxiangChen.droidnet.fragment.ToastFirewallHandler;
import com.mingxiangChen.droidnet.interceptor.CaptureRawInterceptor;
import com.mingxiangChen.droidnet.interceptor.CaptureRawInterceptorFactory;
import com.mingxiangChen.droidnet.interceptor.FirewallInterceptor;
import com.mingxiangChen.droidnet.interceptor.FirewallInterceptorFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link VirtualGatewayFactory} constructs the instance of {@link DroidNetVirtualGateway}.
 * 
 * @author RSmxchen
 * @since 2019-05-01 17:07
 */
public class DroidNetVirtualGatewayFactory implements VirtualGatewayFactory {

    private List<InterceptorFactory<Request, RequestChain, Response, ResponseChain>> mFactories;
    
    public DroidNetVirtualGatewayFactory() {
        mFactories = new ArrayList<>();
        CaptureRawInterceptorFactory captureRawInterceptorFactory = new CaptureRawInterceptorFactory();
        mFactories.add(captureRawInterceptorFactory);
        FirewallInterceptorFactory firewallInterceptorFactory = new FirewallInterceptorFactory();
        mFactories.add(firewallInterceptorFactory);
    }

    public void setPacketCaptureListener(PacketCapturedListener packetCaptureListener) {
        for (InterceptorFactory factory: mFactories) {
            if (factory instanceof CaptureRawInterceptorFactory) {
                ((CaptureRawInterceptorFactory) factory).setPacketCapturedListener(packetCaptureListener);
            }
        }
    }
    
    public void setToastFirewallHandler(ToastFirewallHandler handler) {
        for (InterceptorFactory factory: mFactories) {
            if (factory instanceof FirewallInterceptorFactory) {
                ((FirewallInterceptorFactory) factory).setHandler(handler);
            }
        }
    }

    @Override
    public VirtualGateway create(Session session, Request request, Response response) {
        return new DroidNetVirtualGateway(session, request, response, new ArrayList<>(mFactories));
    }
}
