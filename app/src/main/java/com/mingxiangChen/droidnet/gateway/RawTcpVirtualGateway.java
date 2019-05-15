package com.mingxiangChen.droidnet.gateway;

import com.github.megatronking.netbare.gateway.Interceptor;
import com.github.megatronking.netbare.gateway.InterceptorFactory;
import com.github.megatronking.netbare.gateway.Request;
import com.github.megatronking.netbare.gateway.RequestChain;
import com.github.megatronking.netbare.gateway.Response;
import com.github.megatronking.netbare.gateway.ResponseChain;
import com.github.megatronking.netbare.net.Session;
import com.github.megatronking.netbare.tcp.TcpVirtualGateway;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * A {@link TcpVirtualGateway} designed for showing Tcp data in ASCII.
 *
 * @author RSmxchen
 * @since 2019-04-27 13:50
 */
public class RawTcpVirtualGateway extends TcpVirtualGateway {

    private final List<Interceptor<Request, RequestChain, Response, ResponseChain>> mInterceptors;

    RawTcpVirtualGateway(Session session, Request request, Response response,
                         List<InterceptorFactory<Request, RequestChain, Response, ResponseChain>> factories) {
        super(session, request, response);
        this.mInterceptors = new ArrayList<>(factories.size());
        for (InterceptorFactory<Request, RequestChain, Response, ResponseChain> factory : factories) {
            mInterceptors.add(factory.create());
        }
    }

    @Override
    protected void onSpecRequest(ByteBuffer buffer) throws IOException {
        new RequestChain(mRequest, mInterceptors).process(buffer);
    }

    @Override
    protected void onSpecResponse(ByteBuffer buffer) throws IOException {
        new ResponseChain(mResponse, mInterceptors).process(buffer);
    }

    @Override
    protected void onSpecRequestFinished() {
        for (Interceptor<Request, RequestChain, Response, ResponseChain> interceptor: mInterceptors) {
            interceptor.onRequestFinished(mRequest);
        }
    }

    @Override
    protected void onSpecResponseFinished() {
        for (Interceptor<Request, RequestChain, Response, ResponseChain> interceptor: mInterceptors) {
            interceptor.onResponseFinished(mResponse);
        }
    }
}
