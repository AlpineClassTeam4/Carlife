package com.alphine.team4.carlife.ui.home;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
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
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(getActivity()).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        root.findViewById(R.id.button_GPS).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getActivity().getPackageManager().getLaunchIntentForPackage("com.qinwang.locationactivity");
                if(intent != null){
                    intent.putExtra("points",receiveText);
                    startActivity(intent);
                }else {
                    Toast.makeText(getActivity(),"导航程序未安装!",Toast.LENGTH_SHORT).show();
                }
            }
        });
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        i = new Intent(getActivity(), SocketService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getActivity().startForegroundService(i);
        }else {
            getActivity().startService(i);
        }
        getActivity().bindService(i, connection, BIND_AUTO_CREATE);

    }

    IOnSocketReceivedListener receivedListener = new IOnSocketReceivedListener.Stub() {
        @Override
        public void onReceived(String data) throws RemoteException {
            receiveText = data;
            Log.e("TAG", "onReceived: "+data);
        }

        @Override
        public void onEvent(int e) throws RemoteException {
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