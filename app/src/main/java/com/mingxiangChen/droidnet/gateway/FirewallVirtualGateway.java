package com.mingxiangChen.droidnet.gateway;

import com.github.megatronking.netbare.gateway.Interceptor;
import com.github.megatronking.netbare.gateway.InterceptorFactory;
import com.github.megatronking.netbare.gateway.Request;
import com.github.megatronking.netbare.gateway.RequestChain;
import com.github.megatronking.netbare.gateway.Response;
import com.github.megatronking.netbare.gateway.ResponseChain;
import com.github.megatronking.netbare.gateway.VirtualGateway;
import com.github.megatronking.netbare.net.Session;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * A {@link VirtualGateway} provides firewall functions.
 * 
 * @author RSmxchen
 * @since 2019-04-28 21:29
 */
public class FirewallVirtualGateway extends VirtualGateway {
    
    private final List<Interceptor<Request, RequestChain, Response, ResponseChain>> mInterceptors;
    
    public FirewallVirtualGateway(Session session, Request request, Response response,
                                  List<InterceptorFactory<Request, RequestChain, Response, ResponseChain>> factories) {
        super(session, request, response);
        this.mInterceptors = new ArrayList<>(factories.size());
        for (InterceptorFactory<Request, RequestChain, Response, ResponseChain> factory : factories) {
            mInterceptors.add(factory.create());
        }
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
    }

    @Override
    public void onResponseFinished() {
        for (Interceptor<Request, RequestChain, Response, ResponseChain> interceptor: mInterceptors) {
            interceptor.onResponseFinished(mResponse);
        }
    }
}
