package com.mingxiangChen.droidnet.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.mingxiangChen.droidnet.R;

/**
 * An activity that displays the packet's byte-stream in ascii-encoding.
 * 
 * @author RSmxchen
 * @since 2019-05-14 16:53
 */
public class PacketDetailActivity extends Activity {
    
    private TextView mPacketDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_packet_detail);
        
        mPacketDetail = findViewById(R.id.txt_packet_detail);

        Intent intent = getIntent();
        mPacketDetail.setText(intent.getStringExtra("packetDetail"));
    }
}
