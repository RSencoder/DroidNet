package com.mingxiangChen.droidnet.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mingxiangChen.droidnet.App;
import com.mingxiangChen.droidnet.R;
import com.mingxiangChen.droidnet.activity.PacketDetailActivity;

import java.util.List;

/**
 * The adapter class for recyclerView in {@link CaptureFragment}.
 * 
 * @author RSmxchen
 * @since 2019-05-12 15:24
 */
public class PacketShowInfoAdapter extends RecyclerView.Adapter<PacketShowInfoAdapter.ViewHolder> {
    
    private List<PacketShowInfo> mPacketShowInfos;
    private static final String TAG = "PacketShowInfoAdapter";
    private Activity mActivity;

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView appIcon;
        TextView pkgName;
        TextView protocol;
        TextView session;
        TextView packetSize;
        View packetView;
        
        public ViewHolder(View view) {
            super(view);
            appIcon = view.findViewById(R.id.img_packetIcon);
            pkgName = view.findViewById(R.id.txt_packet_pkgName);
            protocol = view.findViewById(R.id.txt_packet_protocol);
            session = view.findViewById(R.id.txt_packet_session);
            packetSize = view.findViewById(R.id.txt_packet_size);
            packetView = view;
        }
    }
    
    public PacketShowInfoAdapter(List<PacketShowInfo> packetShowInfos, Activity activity) {
        this.mPacketShowInfos = packetShowInfos;
        this.mActivity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_packet, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.packetView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                PacketShowInfo info = mPacketShowInfos.get(position);
                Intent intent = new Intent(mActivity, PacketDetailActivity.class);
                intent.putExtra("packetDetail", info.getBufferInAscii());
                mActivity.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        PacketShowInfo packetShowInfo = mPacketShowInfos.get(position);
        viewHolder.appIcon.setImageDrawable(packetShowInfo.getAppIcon());
        viewHolder.pkgName.setText(packetShowInfo.getPkgName());
        viewHolder.protocol.setText(packetShowInfo.getProtocol());
        viewHolder.session.setText(packetShowInfo.getSession());
        viewHolder.packetSize.setText(packetShowInfo.getPacketSize());
    }
    
    @Override
    public int getItemCount() {
        return mPacketShowInfos.size();
    }
    
    public void addPacket(PacketShowInfo info) {
        mPacketShowInfos.add(info);
        notifyItemInserted(mPacketShowInfos.size() - 1);
    }
    
    public void clearAll() {
        int size = mPacketShowInfos.size();
        mPacketShowInfos.clear();
        notifyItemRangeRemoved(0, size);
    }
}
