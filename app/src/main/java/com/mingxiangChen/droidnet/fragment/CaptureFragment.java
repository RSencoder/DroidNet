package com.mingxiangChen.droidnet.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mingxiangChen.droidnet.App;
import com.mingxiangChen.droidnet.R;

import java.util.ArrayList;

public class CaptureFragment extends BaseFragment {
    
    private RecyclerView mPacketRecyclerView;
    private PacketShowInfoAdapter mPacketShowInfoAdapter;
    
    private AddPacketHandler mHandler;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        mPacketRecyclerView = view.findViewById(R.id.recyclerView_packet_list);
        mPacketShowInfoAdapter = new PacketShowInfoAdapter(new ArrayList<PacketShowInfo>(), getActivity());
        mHandler = new AddPacketHandler(mPacketShowInfoAdapter);
        App.getInstance().mDroidNetVirtualGatewayFactory.setPacketCaptureListener(mHandler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mPacketRecyclerView.setLayoutManager(layoutManager);
        mPacketRecyclerView.setAdapter(mPacketShowInfoAdapter);
    }

    @Override
    int getLayout() {
        return R.layout.fragment_capture;
    }

    public PacketShowInfoAdapter getPacketShowInfoAdapter() {
        return mPacketShowInfoAdapter;
    }
}
