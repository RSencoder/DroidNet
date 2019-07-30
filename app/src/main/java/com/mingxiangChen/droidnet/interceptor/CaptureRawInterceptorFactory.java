package com.mingxiangChen.droidnet.interceptor;

import android.support.annotation.NonNull;

import com.github.megatronking.netbare.gateway.Interceptor;
import com.github.megatronking.netbare.gateway.InterceptorFactory;
import com.mingxiangChen.droidnet.fragment.PacketCapturedListener;

/**
 * A {@link InterceptorFactory} that creates {@link CaptureRawInterceptor}.
 * 
 * @author RSmxchen
 */
public class CaptureRawInterceptorFactory implements InterceptorFactory {

    private PacketCapturedListener mPacketCapturedListener;

//    public PacketCapturedListener getPacketCapturedListener() {
//        return mPacketCapturedListener;
//    }

    public void setPacketCapturedListener(PacketCapturedListener mPacketCapturedListener) {
        this.mPacketCapturedListener = mPacketCapturedListener;
    }

    @NonNull
    @Override
    public Interceptor create() {
        CaptureRawInterceptor captureRawInterceptor = new CaptureRawInterceptor();
        captureRawInterceptor.setPacketCapturedListener(mPacketCapturedListener);
        return captureRawInterceptor;
    }
}
