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
import com.github.megatronking.netbare.ip.Protocol;
import com.mingxiangChen.droidnet.App;
import com.mingxiangChen.droidnet.fragment.PacketCapturedListener;
import com.mingxiangChen.droidnet.fragment.PacketShowInfo;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

/**
 * A {@link Interceptor} decodes the bytes in TCP or UDP streams to ASCII chars and transfers it to UI.
 * 
 * @author RSmxchen
 * @since 2019-05-01 20:31
 */
public class CaptureRawInterceptor implements Interceptor {
    
    private String TAG = "Raw";
    private Charset mAscii;
    private boolean isCapture;
    private PacketCapturedListener mPacketCapturedListener;
    
    public CaptureRawInterceptor() {
        this.mAscii = Charset.forName("ascii");
        isCapture = App.getInstance().mCaptureSwitcher;
    }
    
    @Override
    public void intercept(@NonNull AbstractRequestChain chain, @NonNull ByteBuffer buffer) throws IOException {
        if (isCapture) {
            PacketShowInfo info = new PacketShowInfo(App.getInstance(), chain.request().uid());
            info.setSession(chain.request().ip() + ":" + chain.request().port());
            Protocol protocol = chain.request().protocol();
            info.setProtocol(protocol.toString());
            int bufferSize = buffer.remaining();
            info.setPacketSize(bufferSize + "bytes out");
            CharBuffer result = mAscii.decode(buffer);
            info.setBufferInAscii(result.toString());
            mPacketCapturedListener.onPacketCaptured(info);

            buffer.flip();  // decode过程操作了buffer，需要把极限和位置还原，否则remote tunnel发送大小为0
            chain.process(buffer);
        } else {
            chain.process(buffer);
        }
    }

    @Override
    public void intercept(@NonNull AbstractResponseChain chain, @NonNull ByteBuffer buffer) throws IOException {
        if (isCapture) {
            PacketShowInfo info = new PacketShowInfo(App.getInstance(), chain.response().uid());
            info.setSession(chain.response().ip() + ":" + chain.response().port());
            Protocol protocol = chain.response().protocol();
            info.setProtocol(protocol.toString());
            int bufferSize = buffer.remaining();
            info.setPacketSize(bufferSize + "bytes in");
            CharBuffer result = mAscii.decode(buffer);
            info.setBufferInAscii(result.toString());
            mPacketCapturedListener.onPacketCaptured(info);

            buffer.flip();
            chain.process(buffer);
        } else {
            chain.process(buffer);
        }
    }

    @Override
    public void onRequestFinished(@NonNull Request request) {

    }

    @Override
    public void onResponseFinished(@NonNull Response response) {

    }

    public void setCapture(boolean capture) {
        isCapture = capture;
    }

    protected void setPacketCapturedListener(PacketCapturedListener mPacketCapturedListener) {
        this.mPacketCapturedListener = mPacketCapturedListener;
    }
}
