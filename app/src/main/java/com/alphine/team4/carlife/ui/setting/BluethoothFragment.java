package com.alphine.team4.carlife.ui.setting;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.alphine.team4.carlife.R;
import com.alphine.team4.carlife.ui.setting.Adpater.BluetoothListAdpater;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BluethoothFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BluethoothFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    BluetoothAdapter mBluetoothAdapter;
    RecyclerView recycleListView;
    List<BluetoothDevice> mbluetoothDeviceList;
    BluetoothListAdpater mbluetoothListAdpater;
    Object mA2dp = null;
    Switch sw_bluethooth;
    ImageButton imbt_change;
    TextView tv_bluetooth_status;

    public BluethoothFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BluethoothFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BluethoothFragment newInstance(String param1, String param2) {
        BluethoothFragment fragment = new BluethoothFragment();
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
                .setTitle("蓝牙");
        ((ImageView) requireActivity().findViewById(R.id.setting_imageView))
                .setImageResource(R.drawable.ic_bluetooth);
        ((TextView) requireActivity().findViewById(R.id.tv_explain))
                .setText("实现蓝牙的打开"+"\n"+
                        "可用蓝牙的扫描"+"\n"+
                        "蓝牙的选择连接");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bluethooth, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        requireActivity().registerReceiver(mFindBlueToothReceiver,
       new IntentFilter(android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_STARTED));//搜索开始的过滤器
        requireActivity().registerReceiver(mFindBlueToothReceiver,
        new IntentFilter(android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_FINISHED));//搜索结束的过滤器
        requireActivity().registerReceiver(mFindBlueToothReceiver,
        new IntentFilter(BluetoothDevice.ACTION_FOUND));//寻找到设备的过滤器
        requireActivity().registerReceiver(mFindBlueToothReceiver,
        new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));//绑定状态改变
        requireActivity().registerReceiver(mFindBlueToothReceiver,
        new IntentFilter(BluetoothDevice.ACTION_PAIRING_REQUEST));//配对请求
        requireActivity().registerReceiver(mBlueToothReceiver,
                new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
        requireActivity().registerReceiver(mBlueToothReceiver,
                new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED));
        requireActivity().registerReceiver(mBlueToothReceiver,
                new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED));
    }

    @Override
    public void onStop() {
        super.onStop();
        requireActivity().unregisterReceiver(mFindBlueToothReceiver);
        requireActivity().unregisterReceiver(mBlueToothReceiver);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mbluetoothDeviceList = new ArrayList<>();
        recycleListView = requireActivity().findViewById(R.id.recyclerView_blue);
        sw_bluethooth = requireActivity().findViewById(R.id.sw_bluethooth);
        imbt_change = requireActivity().findViewById(R.id.imbt_change);
        tv_bluetooth_status = requireActivity().findViewById(R.id.tv_bluetooth_status);

        //初始化列表
        showList();

        //获取BluetoothAdapter对象
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.getProfileProxy(requireActivity(), mListener, BluetoothProfile.A2DP);

        sw_bluethooth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    //判断设备是否支持蓝牙，如果mBluetoothAdapter为空则不支持，否则支持
                    if (mBluetoothAdapter == null) {
                        Toast.makeText(requireActivity(), "这台设备不支持蓝牙", Toast.LENGTH_SHORT).show();
                    } else {
                        // If BT is not on, request that it be enabled.
                        // setupChat() will then be called during onActivityResult
                        //判断蓝牙是否开启，如果蓝牙没有打开则打开蓝牙
                        if (!mBluetoothAdapter.isEnabled()) {
                            mBluetoothAdapter.enable();
                        } else {
                            //getDeviceList();
                        }
                    }
                }else {
                    mBluetoothAdapter.disable();
                }
            }
        });

        imbt_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mbluetoothDeviceList.clear();
                doDiscovery();
            }
        });

    }

    private void showList() {
        mbluetoothListAdpater = new BluetoothListAdpater(requireActivity(),mbluetoothDeviceList);
        recycleListView.setLayoutManager(new GridLayoutManager(requireActivity(),2));
        //添加Android自带的分割线
        recycleListView.addItemDecoration(new DividerItemDecoration(requireActivity(),DividerItemDecoration.VERTICAL));
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireActivity());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recycleListView.setLayoutManager(layoutManager);
        recycleListView.setAdapter(mbluetoothListAdpater);
    }

    private void doDiscovery() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(mBluetoothAdapter.isDiscovering()){
                    mBluetoothAdapter.cancelDiscovery();
                }
                mBluetoothAdapter.startDiscovery();
            }
        }).start();
    }

    public void upData(List<BluetoothDevice> data)
    {
        mbluetoothListAdpater.setData(data);
        recycleListView.setAdapter(mbluetoothListAdpater);
        initListener();
    }

    private void initListener() {
        mbluetoothListAdpater.setOnItemClickListener(new BluetoothListAdpater.OnItemClickListener() {
            @Override
            public void onItemClick(BluetoothDevice bluetoothDevice) {
                //在配对之前，停止搜索
                mBluetoothAdapter.cancelDiscovery();
                //获取要匹配的BluetoothDevice对象，后边的deviceList是你本地存的所有对象
                BluetoothDevice device = bluetoothDevice;
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {//没配对才配对
                    try {
                        Log.d("TAG", "开始配对...");
                        Method createBondMethod = BluetoothDevice.class.getMethod("createBond");
                        Boolean returnValue = (Boolean) createBondMethod.invoke(device);
                        if (returnValue){
                            Log.d("TAG", "配对成功...");
                        }
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        mbluetoothListAdpater.setOnItemLongClickListener(new BluetoothListAdpater.OnItemLongClickListener() {
            @Override
            public void OnLongClick(BluetoothDevice bluetoothDevice) {
                Toast.makeText(requireActivity(), "长按了", Toast.LENGTH_SHORT).show();
                connectA2dp(bluetoothDevice);
            }
        });
    }

    private void connectA2dp(BluetoothDevice device){
        setPriority(device, 100); //设置priority
        try {
            //通过反射获取BluetoothA2dp中connect方法（hide的），进行连接。
            Method connectMethod = BluetoothA2dp.class.getMethod("connect",
                    BluetoothDevice.class);
            connectMethod.invoke(mA2dp, device);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BluetoothProfile.ServiceListener mListener = new BluetoothProfile.ServiceListener() {
        @Override
        public void onServiceDisconnected(int profile) {
            if(profile == BluetoothProfile.A2DP){
                mA2dp = null;
            }
        }
        @Override
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            if(profile == BluetoothProfile.A2DP){
                mA2dp = proxy; //转换
            }
        }
    };


    public void setPriority(BluetoothDevice device, int priority) {
        if (mA2dp == null) return;
        try {//通过反射获取BluetoothA2dp中setPriority方法（hide的），设置优先级
            Method connectMethod =BluetoothA2dp.class.getMethod("setPriority",
                    BluetoothDevice.class,int.class);
            connectMethod.invoke(mA2dp, device, priority);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getPriority(BluetoothDevice device) {
        int priority = 0;
        if (mA2dp == null) return priority;
        try {//通过反射获取BluetoothA2dp中getPriority方法（hide的），获取优先级
            Method connectMethod =BluetoothA2dp.class.getMethod("getPriority",
                    BluetoothDevice.class);
            priority = (Integer) connectMethod.invoke(mA2dp, device);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return priority;
    }

    /**
     * 蓝牙开关连接状态广播
     */
    private BroadcastReceiver mBlueToothReceiver = new BroadcastReceiver() {
        private String TAG = "zx";
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action != null){
                switch (action) {
                    case BluetoothAdapter.ACTION_STATE_CHANGED:
                        int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                        switch (blueState) {
                            case BluetoothAdapter.STATE_TURNING_ON:
                                Toast.makeText(context,"蓝牙正在打开",Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "onReceive: 1");
                                break;
                            case BluetoothAdapter.STATE_ON:
                                Log.e(TAG, "onReceive: 2");
                                sw_bluethooth.setChecked(true);
                                break;
                            case BluetoothAdapter.STATE_TURNING_OFF:
                                Toast.makeText(context,"蓝牙正在关闭",Toast.LENGTH_SHORT).show();
                                sw_bluethooth.setChecked(false);
                                break;
                            case BluetoothAdapter.STATE_OFF:
                                sw_bluethooth.setChecked(false);
                                break;
                        }
                        break;
                    case BluetoothDevice.ACTION_ACL_CONNECTED:
                        tv_bluetooth_status.setText("蓝牙设备已连接");
                        break;
                    case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                        tv_bluetooth_status.setText("蓝牙设备已断开");
                        break;
                }

            }
        }
    };

    //广播接收器，当远程蓝牙设备被发现时，回调函数onReceiver()会被执行
    private  BroadcastReceiver mFindBlueToothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            switch (action){
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    Log.d("TAG", "开始扫描...");
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    Log.d("TAG", "结束扫描...");
                    break;
                case BluetoothDevice.ACTION_FOUND:
                    if (device.getName()!=null){
                        mbluetoothDeviceList.add(device);
                        upData(mbluetoothDeviceList);
                        Log.d("TAG", "发现设备..."+device.getName()+"   "+device.getAddress());
                    }
                    break;
                case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                    Log.d("TAG", "设备绑定状态改变...");
                    switch (device.getBondState()) {
                        case BluetoothDevice.BOND_BONDING:
                            Log.w("TAG", "正在配对......");
                            break;
                        case BluetoothDevice.BOND_BONDED:
                            Log.w( "TAG", "配对完成");
                            break;
                        case BluetoothDevice.BOND_NONE:
                            Log.w("TAG", "取消配对");
                        default:
                            break;
                    }
                    break;
            }
        }
    };
}
