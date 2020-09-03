package com.alphine.team4.carlife.ui.setting;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.alphine.team4.carlife.R;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.alphine.team4.carlife.ui.setting.Adpater.WifiRecycleViewAdpater;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WifiFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WifiFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Button button;

    WifiManager wifiManager;
    WifiRecycleViewAdpater wifiadpater;
    RecyclerView recyclerView;
    List<ScanResult> scanResults;
    Switch mst_wifi,mst_wifihot;
    TextView tv_status;
    WifiConfiguration tempConfig;

    public WifiFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WifiFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WifiFragment newInstance(String param1, String param2) {
        WifiFragment fragment = new WifiFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((CollapsingToolbarLayout) requireActivity().findViewById(R.id.CollapsingToolbarLayout))
                .setTitle("WIFI");
        ((ImageView) requireActivity().findViewById(R.id.setting_imageView))
                .setImageResource(R.drawable.ic_wifi);
        ((TextView) requireActivity().findViewById(R.id.tv_explain))
                .setText("实现wifi的打开"+"\n"+
                        "可用wifi的扫描"+"\n"+
                        "wifi的选择连接");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wifi, container, false);
    }

    @Override
    public void onStart() {
        requireActivity().registerReceiver(Wifistarreceiver,new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));//wifi开关状态
        requireActivity().registerReceiver(Wifistarreceiver,new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));//wifi扫描结果
        requireActivity().registerReceiver(Wifiintoreceiver,new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));//wifi连接状态
        requireActivity().registerReceiver(wifiApBroadcast,new IntentFilter("android.net.wifi.WIFI_AP_STATE_CHANGED"));//wifi热点状态
        super.onStart();
    }

    @Override
    public void onStop() {
        requireActivity().unregisterReceiver(Wifistarreceiver);
        requireActivity().unregisterReceiver(wifiApBroadcast);
        requireActivity().unregisterReceiver(Wifiintoreceiver);
        super.onStop();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tv_status = requireActivity().findViewById(R.id.tv_status);
        mst_wifi = requireActivity().findViewById(R.id.st_wifi);
        mst_wifihot = requireActivity().findViewById(R.id.st_wifihot);
        recyclerView = requireActivity().findViewById(R.id.recyclerView_wifi);
        scanResults = new ArrayList<>();
        wifiManager = (WifiManager)requireActivity().getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        showList();
        mst_wifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    wifiManager.setWifiEnabled(true);

                }else{
                    wifiManager.setWifiEnabled(false);
                }
            }
        });

        mst_wifihot.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                openAP();
            }
        });

    }

    private void doWifistartScan(final boolean star) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(star){
                    wifiManager.startScan();
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void showList() {
        wifiadpater = new WifiRecycleViewAdpater(requireActivity(),scanResults);
        recyclerView.setLayoutManager(new GridLayoutManager(requireActivity(),2));
        //添加Android自带的分割线
        recyclerView.addItemDecoration(new DividerItemDecoration(requireActivity(),DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(wifiadpater);
        initListener();
    }

    /**
     * wifi扫描列表显示更新
     * @param data
     */
    public void upData(List<ScanResult> data)
    {
        wifiadpater.setData(data);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireActivity());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(wifiadpater);
    }

    private void initListener() {
        wifiadpater.setOnItemClickListener(new WifiRecycleViewAdpater.OnItemClickListener() {
            @Override
            public void onItemClick(String wifiSSID, String wifitype, int position) {
                doWifistartScan(false);
                tempConfig = isExsitsInRecord(wifiSSID);
                Log.e("TAG", "onItemClick: "+tempConfig);
                if (tempConfig != null) {
                    wifiManager.disconnect();
                    saveconnect(tempConfig.networkId,tempConfig);
                }else {
                    final String ssid = wifiSSID;
                    final String type = wifitype;
                    final View view = View.inflate(requireActivity(), R.layout.intopassword, null);
                    final EditText etpossward = view.findViewById(R.id.et_password);
                    AlertDialog dialog = new AlertDialog.Builder(requireActivity())
                            .setTitle("请输入WIFI密码")
                            .setView(view)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String possward = etpossward.getText().toString();
                                    connect(creatWifiConfiguration(ssid,possward,type));
                                }
                            })
                            .setNegativeButton("取消", null)
                            .create();
                    dialog.show();
                }
            }
        });
    }

    public WifiConfiguration creatWifiConfiguration(String ssid, String password,String type) {
        int typenumber;
        if (type.contains("WPA") || type.contains("wpa")) {
            typenumber = 0;
        } else if (type.contains("WEP") || type.contains("wep")) {
            typenumber = 1;
        } else {
            typenumber = 2;
        }
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();//允许的Auth算法
        config.allowedGroupCiphers.clear();//允许的群组密码
        config.allowedKeyManagement.clear();//允许的密钥管理
        config.allowedPairwiseCiphers.clear();//允许的协议
        config.allowedProtocols.clear();
        config.SSID = "\"" + ssid + "\"";
        switch (typenumber) {
            case 2:
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                break;
            case 1:
                config.hiddenSSID = true;
                config.wepKeys[0] = "\"" + password + "\"";
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED/*共享*/);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);//CCMP （计数器模式密码块链消息完整码协议）
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);//TKIP的密码类型
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                config.wepTxKeyIndex = 0;
                break;
            case 0:
                config.preSharedKey = "\"" + password + "\"";
                config.hiddenSSID = true;
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                // WPA-PSK（预共享密钥）
                //“临时密钥完整性协议”（Temporal Key Integrity Protocol，TKIP）密钥长度为128位
                // AES是高级加密标准（Advanced Encryption Standard，AES）区块长度固定为128位，密钥长度则可以是128，192或256位。
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);//TKIP的密码类型
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);//运行wpa_psk的密钥管理
                config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                config.status = WifiConfiguration.Status.ENABLED;
                break;
        }
        return config;
    }

    /**
     * 实现wifi连接函数
     * @param config
     */
    public void connect(WifiConfiguration config) {
        int wcgID = wifiManager.addNetwork(config);
        wifiManager.enableNetwork(wcgID, true);//enable使能
        Toast.makeText(requireActivity(),"连接了:"+config.SSID,Toast.LENGTH_SHORT).show();
    }

    /**
     * 连接保存的wifi
     * @param wcgID
     * @param config
     */
    public void saveconnect(int wcgID,WifiConfiguration config) {
        wifiManager.enableNetwork(wifiManager.addNetwork(config), true);//enable使能
        Toast.makeText(requireActivity(),"连接了:"+config.SSID,Toast.LENGTH_SHORT).show();
    }
    /**
     * 判断是否连接过指定的SSID wifi信号
     */
    public WifiConfiguration isExsitsInRecord(String SSID) {
        if (wifiManager == null){
            Log.e("TAG", "isExsitsInRecord: null");
            return null;
        }
        List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                Log.e("TAG", "isExsitsInRecord: 2+"+SSID);
                return existingConfig;
            }
        }
        Log.e("TAG", "isExsitsInRecord: null2");
        return null;
    }

    /**
     * 打开系统的便携式热点界面  针对7.1版本
     */
    private void openAP() {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //打开网络共享与热点设置页面
        ComponentName comp = new ComponentName("com.android.settings",
                "com.android.settings.Settings$TetherSettingsActivity");
        intent.setComponent(comp);
        startActivity(intent);
    }

    /**
     * wifi开关状态广播
     */
    private BroadcastReceiver Wifistarreceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case WifiManager.WIFI_STATE_CHANGED_ACTION:
                    int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
                            WifiManager.WIFI_STATE_DISABLED);
                    switch (wifiState) {
                        case WifiManager.WIFI_STATE_DISABLING:
                            Toast.makeText(requireActivity(),"WIFI正在关闭",Toast.LENGTH_SHORT).show();
                            break;
                        case WifiManager.WIFI_STATE_DISABLED:
                            Toast.makeText(requireActivity(),"WIFI已经关闭",Toast.LENGTH_SHORT).show();
                            mst_wifi.setChecked(false);
                            doWifistartScan(false);
                            break;
                        case WifiManager.WIFI_STATE_ENABLING:
                            Toast.makeText(requireActivity(),"WIFI正在开启",Toast.LENGTH_SHORT).show();
                            break;
                        case WifiManager.WIFI_STATE_ENABLED:
                            Toast.makeText(requireActivity(),"WIFI已经开启",Toast.LENGTH_SHORT).show();
                            mst_wifi.setChecked(true);
                            doWifistartScan(true);
                            break;
                    }
                    break;
                case WifiManager.SCAN_RESULTS_AVAILABLE_ACTION:
                    final String action = intent.getAction();
                    if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                        // wifi已成功扫描到可用wifi。
                        //scanResults = null;
                        scanResults = wifiManager.getScanResults();
                        upData(scanResults);
                        Toast.makeText(requireActivity(),"扫描结束",Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
            }
        }
    };

    /**
     * wifi热点状态广播
     */
    private BroadcastReceiver wifiApBroadcast = new  BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if("android.net.wifi.WIFI_AP_STATE_CHANGED".equals(action)){
                //便携式热点的状态为：10---正在关闭；11---已关闭；12---正在开启；13---已开启
                int state = intent.getIntExtra("wifi_state",  0);
                if(state == 13){
                    mst_wifihot.setText("关闭WIFI热点");
                    mst_wifihot.setChecked(true);
                    Log.e("MainActivity","热点已开启");
                }else if(state == 11){
                    mst_wifihot.setText("开启WIFI热点");
                    mst_wifihot.setChecked(false);
                    Log.e("MainActivity","热点已关闭");
                }else if(state == 10){
                    Log.e("MainActivity","热点正在关闭");
                }else if(state == 12){
                    Log.e("MainActivity","热点正在开启");
                }
            }
        }
    };

    /**
     * wifi连接状态广播
     */
    private BroadcastReceiver Wifiintoreceiver = new BroadcastReceiver() {
        private String TAG = "zx";
        @Override
        public void onReceive(Context context, Intent intent) {
            NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            NetworkInfo.DetailedState state = info.getDetailedState();
            if (state == NetworkInfo.DetailedState.SCANNING) {
                Log.i(TAG, "onReceive onNetWorkStateChanged: +正在扫描");
                tv_status.setText("正在扫描");
            } else if (state == NetworkInfo.DetailedState.CONNECTING) {
                Log.i(TAG, "onReceive onNetWorkStateChanged: +正在连接");
                tv_status.setText("正在连接");

            } else if (state == NetworkInfo.DetailedState.OBTAINING_IPADDR) {
                Log.i(TAG, "onReceive onNetWorkStateChanged: +获取IP地址");
                tv_status.setText("获取IP地址");

            } else if (state == NetworkInfo.DetailedState.CONNECTED) {
                Log.i(TAG, "onReceive onNetWorkStateChanged: +建立连接");
                tv_status.setText("建立连接");

            } else if (state == NetworkInfo.DetailedState.DISCONNECTING) {
                Log.i(TAG, "onReceive onNetWorkStateChanged: +正在断开连接");
                tv_status.setText("正在断开连接");

            } else if (state == NetworkInfo.DetailedState.DISCONNECTED) {
                Log.i(TAG, "onReceive onNetWorkStateChanged: +已断开连接");
                tv_status.setText("已断开连接");

            } else if (state == NetworkInfo.DetailedState.FAILED) {
                Log.i(TAG, "onReceive onNetWorkStateChanged: +连接失败");
                tv_status.setText("连接失败");

            }
        }
    };


}
