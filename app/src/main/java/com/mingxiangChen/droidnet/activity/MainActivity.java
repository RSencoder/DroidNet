package com.mingxiangChen.droidnet.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.github.megatronking.netbare.NetBare;
import com.github.megatronking.netbare.NetBareConfig;
import com.github.megatronking.netbare.NetBareListener;
import com.github.megatronking.netbare.gateway.InterceptorFactory;
import com.github.megatronking.netbare.gateway.Request;
import com.github.megatronking.netbare.gateway.RequestChain;
import com.github.megatronking.netbare.gateway.Response;
import com.github.megatronking.netbare.gateway.ResponseChain;
import com.github.megatronking.netbare.http.HttpInterceptorFactory;
import com.github.megatronking.netbare.http.HttpVirtualGatewayFactory;
import com.github.megatronking.netbare.ssl.JKS;
import com.github.megatronking.netbare.ip.IpAddress;
import com.mingxiangChen.droidnet.App;
import com.mingxiangChen.droidnet.R;
import com.mingxiangChen.droidnet.fragment.BaseFragment;
import com.mingxiangChen.droidnet.fragment.CaptureFragment;
import com.mingxiangChen.droidnet.fragment.FirewallFragment;
import com.mingxiangChen.droidnet.fragment.PacketShowInfoAdapter;
import com.mingxiangChen.droidnet.gateway.DroidNetVirtualGatewayFactory;
import com.mingxiangChen.droidnet.gateway.RawTcpVirtualGatewayFactory;
import com.mingxiangChen.droidnet.interceptor.HttpUrlPrintInterceptor;
import com.mingxiangChen.droidnet.interceptor.RawTcpPrintInterceptor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NetBareListener {

    private String TAG = "DroidNet";
    private Toolbar mToolbar;
    private MenuItem mVPNSwitcher;
    private FloatingActionButton mCaptureSwitcher;
    
    private ArrayList<BaseFragment> mBaseFragments;
    private TabLayout mTabLayout;
    private FragmentPagerAdapter mSimpleFragmentPagerAdapter;
    private ViewPager mViewPager;
    
    private static final int REQUEST_CODE_PREPARE = 1;
    private NetBare mNetBare;
    
    private static final int MAINACTIVITY_REQUEST_CODE = 7777;
    private boolean isPkgSelected;
    private String mPkgSelected;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        
        initChildFragments();
        initViewPager();
        initTab();
        
        mCaptureSwitcher = findViewById(R.id.btn_captureAction);
        mCaptureSwitcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (App.getInstance().mCaptureSwitcher) {
                    App.getInstance().notifyCaptureOff();
                    mCaptureSwitcher.setImageResource(R.drawable.start);
                    App.getInstance().mCaptureSwitcher = false;
                } else {
                    App.getInstance().notifyCaptureOn();
                    mCaptureSwitcher.setImageResource(R.drawable.stop);
                    App.getInstance().mCaptureSwitcher = true;
                }
            }
        });
        
        mNetBare = NetBare.get();
        // 监听NetBare服务的启动和停止
        mNetBare.registerNetBareListener(this);
    }

    private void prepareNetBare() {
        // 安装自签证书
        if (!JKS.isInstalled(this, App.getJKS_ALIAS())) {
            try {
                JKS.install(this, App.getJKS_ALIAS(), App.getJKS_ALIAS());
            } catch (Exception e) {
                // 安装失败
            }
            return;
        }
        // 配置VPN
        Intent intent = NetBare.get().prepare();
        if (intent != null) {
            startActivityForResult(intent, REQUEST_CODE_PREPARE );
            return;
        }
//        //只抓DroidNetTestClient的包
//        NetBareConfig config = new NetBareConfig.Builder()
//                .dumpUid(false)
//                .setMtu(4096)
//                .setAddress(new IpAddress("10.1.10.1", 32))
//                .setSession("NetBare")
//                .addRoute(new IpAddress("0.0.0.0", 0))
//                .dumpUid(true)
////                .addAllowedApplication("com.mingxiangchen.droidnettestclient")
////                .setVirtualGatewayFactory(new HttpVirtualGatewayFactory(App.getInstance().getJKS(),
////                        interceptorFactories()))
////                .setVirtualGatewayFactory(new RawTcpVirtualGatewayFactory(interceptorFactoriesForRawTcp()))
//                .setVirtualGatewayFactory(new DroidNetVirtualGatewayFactory())
//                .build();
//        mNetBare.start(config);
        // 根据用户选择抓包应用情况配置NetBareConfig
        NetBareConfig.Builder builder = new NetBareConfig.Builder();
        builder.dumpUid(true)
                .setMtu(4096)
                .setAddress(new IpAddress("10.1.10.1", 32))
                .setSession("NetBare")
                .addRoute(new IpAddress("0.0.0.0", 0))
                .setVirtualGatewayFactory(App.getInstance().mDroidNetVirtualGatewayFactory);
        if (isPkgSelected) {
            builder.addAllowedApplication(mPkgSelected);
        }
        mNetBare.start(builder.build());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case MAINACTIVITY_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    isPkgSelected = true;
                    mPkgSelected = data.getStringExtra("selected_package_name");
                } else {
                    isPkgSelected = false;
                }
                break;
            case REQUEST_CODE_PREPARE:
                if (resultCode == Activity.RESULT_OK) {
                    prepareNetBare();
                }
                break;
        }
    }

    private List<HttpInterceptorFactory> interceptorFactories() {
        List<HttpInterceptorFactory> list = new LinkedList<>();
        HttpInterceptorFactory interceptor1 = HttpUrlPrintInterceptor.createFactory();
        ((LinkedList<HttpInterceptorFactory>) list).addLast(interceptor1);
        return list;
    }
    
    private List<InterceptorFactory<Request, RequestChain, Response, ResponseChain>> interceptorFactoriesForRawTcp() {
        List<InterceptorFactory<Request, RequestChain, Response, ResponseChain>> list = new ArrayList<>();
        InterceptorFactory rawTcpPrint = RawTcpPrintInterceptor.createFactory();
        list.add(rawTcpPrint);
        return list;
    }

    @Override
    public void onServiceStarted() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mVPNSwitcher.setTitle(R.string.vpn_stop);
            }
        });
    }

    @Override
    public void onServiceStopped() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mVPNSwitcher.setTitle(R.string.vpn_start);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mNetBare.unregisterNetBareListener(this);
        mNetBare.stop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        mVPNSwitcher = menu.findItem(R.id.menu_vpnSwitcher);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_selectPkg:
                Intent intent = new Intent(MainActivity.this, PackageListActivity.class);
                startActivityForResult(intent, MAINACTIVITY_REQUEST_CODE);
                break;
            case R.id.menu_vpnSwitcher:
                if (mNetBare.isActive()) {
                    mNetBare.stop();
                    App.getInstance().mVPNSwitcher = false;
                } else {
                    prepareNetBare();
                    App.getInstance().mVPNSwitcher = true;
                }
                break;
            case R.id.menu_firewallSwitcher:
                if (App.getInstance().mFirewallSwitcher) {
                    App.getInstance().notifyFirewallOff();
                    item.setTitle(R.string.firewall_on);
                    App.getInstance().mFirewallSwitcher = false;
                } else {
                    App.getInstance().notifyFirewallOn();
                    item.setTitle(R.string.firewall_off);
                    App.getInstance().mFirewallSwitcher = true;
                }
                break;
            case R.id.menu_clearCaptured:
                CaptureFragment captureFragment = (CaptureFragment) mBaseFragments.get(0);
                PacketShowInfoAdapter adapter = captureFragment.getPacketShowInfoAdapter();
                adapter.clearAll();
                break;
            default:
        }
        return true;
    }

    private void initChildFragments() {
        mBaseFragments = new ArrayList<>();
        mBaseFragments.add(new CaptureFragment());
        mBaseFragments.add(new FirewallFragment());
    }
    
    private void initViewPager() {
        mSimpleFragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return mBaseFragments.get(i);
            }

            @Override
            public int getCount() {
                return mBaseFragments.size();
            }
        };
        mViewPager = findViewById(R.id.viewPager);
        mViewPager.setAdapter(mSimpleFragmentPagerAdapter);
    }
    
    private void initTab() {
        mTabLayout = findViewById(R.id.tabLayout);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        String[] tabTitles = getResources().getStringArray(R.array.tabs);
        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            mTabLayout.getTabAt(i).setText(tabTitles[i]);
        }
    }
}
