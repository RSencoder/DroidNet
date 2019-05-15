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
 * A {@link VirtualGatewayFactory} that produces the {@link FirewallVirtualGateway}.
 * 
 * @author RSmxchen
 * @since 2019-04-28 21:50
 */
public class FirewallVirtualGatewayFactory implements VirtualGatewayFactory {

    private List<InterceptorFactory<Request, RequestChain, Response, ResponseChain>> mFactories;
    
    public FirewallVirtualGatewayFactory(@NonNull List<InterceptorFactory<Request, RequestChain,
            Response, ResponseChain>> factories) {
        this.mFactories = factories;
    }

    @Override
    public VirtualGateway create(Session session, Request request, Response response) {
        return new FirewallVirtualGateway(session, request, response, new ArrayList<>(mFactories));
    }
}
