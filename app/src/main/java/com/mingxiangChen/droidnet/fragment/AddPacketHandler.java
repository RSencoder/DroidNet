package com.mingxiangChen.droidnet.fragment;

import android.os.Handler;
import android.os.Message;

/**
 * A handler designed for {@link com.mingxiangChen.droidnet.interceptor.CaptureRawInterceptor} thread 
 * communicating with UI thread.
 * 
 * @author RSmxchen
 * @since 2019-05-13 22:46
 */
public class AddPacketHandler extends Handler implements PacketCapturedListener {

    private PacketShowInfoAdapter adapter;
    
    public AddPacketHandler(PacketShowInfoAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public void handleMessage(Message msg) {
        if (msg.obj instanceof PacketShowInfo) {
            adapter.addPacket((PacketShowInfo) msg.obj);
        }
    }

    @Override
    public void onPacketCaptured(PacketShowInfo info) {
        Message message = new Message();
        message.obj = info;
        this.sendMessage(message);
    }
}
