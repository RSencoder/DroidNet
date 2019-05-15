package com.mingxiangChen.droidnet.gateway;

import android.support.annotation.NonNull;

import com.github.megatronking.netbare.gateway.InterceptorFactory;
import com.github.megatronking.netbare.gateway.Request;
import com.github.megatronking.netbare.gateway.RequestChain;
import com.github.megatronking.netbare.gateway.Response;
import com.github.megatronking.netbare.gateway.ResponseChain;
import com.github.megatronking.netbare.gateway.VirtualGateway;
import com.github.megatronking.netbare.gateway.VirtualGatewayFactory;
import com.github.megatronking.netbare.net.Session;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link VirtualGatewayFactory} that produces the {@link RawTcpVirtualGateway}.
 *
 * @author RSmxchen
 * @since 2019-04-27 14:08
 */
public class RawTcpVirtualGatewayFactory implements VirtualGatewayFactory {

    private List<InterceptorFactory<Request, RequestChain, Response, ResponseChain>> mFactories;

    public RawTcpVirtualGatewayFactory(@NonNull List<InterceptorFactory<Request, RequestChain,
            Response, ResponseChain>> factories) {
        this.mFactories = factories;
    }

    @Override
    public VirtualGateway create(Session session, Request request, Response response) {
        return new RawTcpVirtualGateway(session, request, response, new ArrayList<>(mFactories));
    }
}
