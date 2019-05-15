package com.mingxiangChen.droidnet.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.mingxiangChen.droidnet.App;
import com.mingxiangChen.droidnet.R;
import com.mingxiangChen.droidnet.util.ThreadProxy;

import java.util.List;

/**
 * A {@link Activity} shows all the packages in device and let the user choose one. Reference the
 * NetworkPacketCapture project.
 * 
 * @author RSmxchen
 * @since 2019-05-05 22:09
 */
public class PackageListActivity extends Activity {

    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private List<PackageShowInfo> mPackageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_list);
        
        // 将耗时的getPackageShowInfo()方法扔进线程池里，别阻塞UI线程
        mRecyclerView = findViewById(R.id.recyclerView_package_list);
        mProgressBar = findViewById(R.id.progressBar_package_list);
        ThreadProxy.getInstance().execute(new Runnable() {
            
            private PackageShowInfoAdapter adapter;
            
            @Override
            public void run() {
                mPackageList = PackageShowInfo.getPackageShowInfo(App.getInstance());
                adapter = new PackageShowInfoAdapter(mPackageList, PackageListActivity.this);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LinearLayoutManager layoutManager = new LinearLayoutManager(PackageListActivity.this);
                        mRecyclerView.setLayoutManager(layoutManager);
                        mRecyclerView.setAdapter(adapter);
                        mProgressBar.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }
}
