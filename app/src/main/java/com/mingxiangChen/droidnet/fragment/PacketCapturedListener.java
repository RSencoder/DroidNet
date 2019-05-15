package com.mingxiangChen.droidnet.fragment;

/**
 * A callback which invoked when {@link com.mingxiangChen.droidnet.interceptor.CaptureRawInterceptor} 
 * intercept() method captures a packet. Let the {@link CaptureFragment} know to refresh the recyclerView.
 * So the adapter of the recyclerView should implement this interface.
 * 
 * @author RSmxchen
 * @since 2019-05-13 00:35
 */
public interface PacketCapturedListener {
    void onPacketCaptured(PacketShowInfo info);
}
