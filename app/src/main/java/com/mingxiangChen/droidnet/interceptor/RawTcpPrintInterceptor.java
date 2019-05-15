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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

/**
 * A {@link Interceptor} converts tcp data to ASCII-encoding String.
 * 
 * @author RSmxchen
 * @since 2019-04-27
 */
public class RawTcpPrintInterceptor implements Interceptor {

    private String TAG = "Raw Tcp data\n";
    private Charset mAscii;
    
    private RawTcpPrintInterceptor() {
        this.mAscii = Charset.forName("ascii");
    }
    
    public static InterceptorFactory createFactory() {
        return new InterceptorFactory() {
            @NonNull
            @Override
            public Interceptor create() {
                return new RawTcpPrintInterceptor();
            }
        };
    }
    
    @Override
    public void intercept(@NonNull AbstractRequestChain chain, @NonNull ByteBuffer buffer) throws IOException {
        PackageManager packageManager = App.getInstance().getPackageManager();
        Log.d("PackageName:", packageManager.getNameForUid(chain.request().uid()));
        
        CharBuffer result = mAscii.decode(buffer);
        Log.d(TAG, result.toString());
        
        buffer.flip();  // decode过程操作了buffer，需要把极限和位置还原，否则remote tunnel发送大小为0
        chain.process(buffer);
    }

    @Override
    public void intercept(@NonNull AbstractResponseChain chain, @NonNull ByteBuffer buffer) throws IOException {
        CharBuffer result = mAscii.decode(buffer);
        Log.d(TAG, result.toString());
        
        buffer.flip();
        chain.process(buffer);
    }

    @Override
    public void onRequestFinished(@NonNull Request request) {
        Log.i(TAG, "onRequestFinished!");
    }

    @Override
    public void onResponseFinished(@NonNull Response response) {
        Log.i(TAG, "onResponseFinished!");
    }
}
