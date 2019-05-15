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
import com.mingxiangChen.droidnet.interceptor.CaptureRawInterceptor;
import com.mingxiangChen.droidnet.interceptor.CaptureRawInterceptorFactory;
import com.mingxiangChen.droidnet.interceptor.FirewallInterceptor;

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
    
    private PacketCapturedListener mPacketCaptureListener;
    
    public DroidNetVirtualGatewayFactory() {
        mFactories = new ArrayList<>();
        CaptureRawInterceptorFactory captureRawInterceptorFactory = new CaptureRawInterceptorFactory();
        mFactories.add(captureRawInterceptorFactory);
        mFactories.add(FirewallInterceptor.createFactory());
    }

    public void setPacketCaptureListener(PacketCapturedListener mPacketCaptureListener) {
        this.mPacketCaptureListener = mPacketCaptureListener;
        for (InterceptorFactory factory: mFactories) {
            if (factory instanceof CaptureRawInterceptorFactory) {
                ((CaptureRawInterceptorFactory) factory).setPacketCapturedListener(mPacketCaptureListener);
            }
        }
    }

    @Override
    public VirtualGateway create(Session session, Request request, Response response) {
        return new DroidNetVirtualGateway(session, request, response, new ArrayList<>(mFactories));
    }
}
