package com.alphine.team4.carlife.ui.home;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.alphine.team4.carlife.Dataaidl;
import com.alphine.team4.carlife.IOnSocketReceivedListener;
import com.alphine.team4.carlife.ISocketBinder;
import com.alphine.team4.carlife.R;
import com.alphine.team4.carlife.ui.setting.SocketService;
import com.alphine.team4.carlife.ui.setting.UserFragment;

import static android.content.Context.BIND_AUTO_CREATE;

public class HomeFragment extends Fragment {


    private HomeViewModel homeViewModel;
    ISocketBinder socketBinder;
    String receiveText;
    Intent i;
    Switch swcarlock,swcarstart,swcardoor,swbackbox;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(getActivity()).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        root.findViewById(R.id.button_GPS).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                ComponentName componentName = new ComponentName("com.qinwang.locationactivity","com.qinwang.locationactivity.activity.MainActivity");
                intent.setComponent(componentName);
                Uri uri = Uri.parse("com.qinwang.locationactivity.activity.MainActivity");
                intent.setData(uri);
                intent.putExtra("points","38.894473,121.555926");
                Bundle bundle  =intent.getExtras();
                if (bundle == null){
                    Toast.makeText(getActivity(),
                            "请先获取汽车位置",
                            Toast.LENGTH_LONG).show();
                }else {
                    startActivity(intent);
                }
            }
        });

        swcarlock = root.findViewById(R.id.sw_carlock);
        swcarstart = root.findViewById(R.id.sw_carstart);
        swcardoor = root.findViewById(R.id.sw_cardoor);
        swbackbox = root.findViewById(R.id.sw_carbackbox);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        i = new Intent(getActivity(), SocketService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            getActivity().startForegroundService(i);
        }else{
            getActivity().startService(i);
        }
        getActivity().bindService(i, connection, BIND_AUTO_CREATE);

    }

    IOnSocketReceivedListener receivedListener = new IOnSocketReceivedListener.Stub() {
        @Override
        public void onReceived(String data) throws RemoteException{
            receiveText = data;
            Log.e("TAG", "onReceived: "+data);
            if (receiveText.equals("车门上锁")){
                swcarlock.setChecked(true);
                swcarlock.setText("车门解锁");
            }else if (receiveText.equals("车门解锁")){
                swcarlock.setChecked(false);
                swcarlock.setText("车门上锁");
            }else if (receiveText.equals("车辆启动")){
                swcarstart.setChecked(true);
                swcarstart.setText("车辆已启动");
            }else if (receiveText.equals("车辆关闭")){
                swcarstart.setChecked(false);
                swcarstart.setText("车辆未启动");
            }else if (receiveText.equals("打开后备箱")){
                swbackbox.setChecked(true);
                swbackbox.setText("关闭后备箱");
            }else if (receiveText.equals("关闭后备箱")){
                swbackbox.setChecked(false);
                swbackbox.setText("打开后备箱");
            }else if (receiveText.equals("车门未关闭")){
                swbackbox.setChecked(true);
                swbackbox.setText("车门未关闭");
            }else if (receiveText.equals("车门已关闭")){
                swbackbox.setChecked(false);
                swbackbox.setText("车门已关闭");
            }
        }

        @Override
        public void onEvent(int e) throws RemoteException{
        }
    };

    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            socketBinder = (ISocketBinder) iBinder;
            if(socketBinder==null){
                Toast.makeText(getActivity(),"服务连接失败！",Toast.LENGTH_SHORT).show();
                return;
            }
            //初始化设置参数
            try {
                socketBinder.registerListener(receivedListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            try {
                socketBinder.setUDPEnabled(true);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public void onStop() {
        super.onStop();
        if(socketBinder!=null) {
            try {
                socketBinder.unregisterListener(receivedListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            getActivity().unbindService(connection);
        }

        getActivity().stopService(i);
        socketBinder = null;
    }
}