package com.alphine.team4.carlife.ui.setting;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.os.IBinder;
import android.os.RemoteException;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.alphine.team4.carlife.IOnSocketReceivedListener;
import com.alphine.team4.carlife.ISocketBinder;
import com.alphine.team4.carlife.MainActivity;
import com.alphine.team4.carlife.R;
import com.alphine.team4.carlife.ui.login.toolsBeans.DBHelper;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.Context.BIND_AUTO_CREATE;
import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private static final String TAG = "HomeDate";
    TextView textView;

    DBHelper dbHelper = new DBHelper(getActivity());

    String nickName;

    EditText etRemoteIP;
    EditText etRemotePort;
    EditText etLocalPort;

    String receiveText;//存储接收数据
    Switch swUdp;
    ToggleButton btStartService;

    ISocketBinder socketBinder;

    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            Log.d(TAG, "onServiceConnected: is called.");
            socketBinder = (ISocketBinder) iBinder;
            if(socketBinder==null){
                Toast.makeText(getActivity(),"服务连接失败！",Toast.LENGTH_SHORT).show();
                return;
            }
            //初始化设置参数
            try {
                socketBinder.registerListener(receivedListener);
                //恢复状态
                swUdp.setChecked(socketBinder.isUDPEnabled());
                etLocalPort.setText(String.valueOf(socketBinder.getLocalPortPort()));
                etRemoteIP.setText(socketBinder.getRemoteIP());
                etRemotePort.setText(String.valueOf(socketBinder.getRemotePort()));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    IOnSocketReceivedListener receivedListener = new IOnSocketReceivedListener.Stub() {
        @Override
        public void onReceived(String data) throws RemoteException {
            receiveText = data;
            Log.d(TAG, "ReceivedDate: "+receiveText);
            Toast.makeText(getActivity(),"已收到数据："+receiveText,Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onEvent(int e) throws RemoteException {
            switch (e){
                case IOnSocketReceivedListener.OPEN_SUCCESS:
                    Toast.makeText(getActivity(), "打开端口成功",Toast.LENGTH_SHORT).show();
                    break;
                case IOnSocketReceivedListener.CLOSE_SUCCESS:
                    Toast.makeText(getActivity(),"关闭端口成功",Toast.LENGTH_SHORT).show();
                    break;
                case IOnSocketReceivedListener.ACCEPT_SUCCESS:
                    Toast.makeText(getActivity(), "客户端接入成功",Toast.LENGTH_SHORT).show();
                    break;
                case IOnSocketReceivedListener.OPEN_FAILED:
                    Toast.makeText(getActivity(), "打开端口失败",Toast.LENGTH_SHORT).show();
                    break;
                case IOnSocketReceivedListener.OPEN_TIMEOUT:
                    Toast.makeText(getActivity(), "连接超时",Toast.LENGTH_SHORT).show();
                    break;
                case IOnSocketReceivedListener.SEND_ERROR:
                    Toast.makeText(getActivity(), "发送失败",Toast.LENGTH_SHORT).show();
                    break;
                case IOnSocketReceivedListener.RECV_ERROR:
                    Toast.makeText(getActivity(), "接收失败",Toast.LENGTH_SHORT).show();
                    break;
                case IOnSocketReceivedListener.ACCEPT_ERROR:
                    Toast.makeText(getActivity(), "客户端接入失败",Toast.LENGTH_SHORT).show();
                    break;
                case IOnSocketReceivedListener.UNKNOWN_ERROR:
                    Toast.makeText(getActivity(), "未知网络错误",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public UserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserFragment newInstance(String param1, String param2) {
        UserFragment fragment = new UserFragment();
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
                .setTitle("User");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_date",MODE_PRIVATE);
        String input_email = sharedPreferences.getString("user_email","");

        SQLiteDatabase db;

        textView = getActivity().findViewById(R.id.textView4);

        etRemoteIP = getActivity().findViewById(R.id.etRemoteIP);
        etRemotePort = getActivity().findViewById(R.id.etRemotePort);
        etLocalPort = getActivity().findViewById(R.id.etLocalPort);
        swUdp = getActivity().findViewById(R.id.switchUDP);
        btStartService = getActivity().findViewById(R.id.toggleButtonStartService);

        etRemoteIP.setInputType(InputType.TYPE_NULL);
        etRemotePort.setInputType(InputType.TYPE_NULL);
        etLocalPort.setInputType(InputType.TYPE_NULL);

        etLocalPort.setOnClickListener(this);
        etRemotePort.setOnClickListener(this);
        etRemoteIP.setOnClickListener(this);

        swUdp.setOnCheckedChangeListener(this);
        btStartService.setOnCheckedChangeListener(this);

        String path = getActivity().getFilesDir() + "/"+"user_info.db";
        db = SQLiteDatabase.openOrCreateDatabase(path,null);
        Cursor cursor = db.query("user_info",null,"user_email=?",
                new String[]{input_email},
                null,null,null,null);
        while (cursor.moveToNext()){
            nickName = cursor.getString(cursor.getColumnIndex("user_nickname"));
        }
        if(input_email.equals("")){
            textView.setText("未登录");
        }else {
            textView.setText(nickName+" 已登录");
        }

        getActivity().findViewById(R.id.button_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_date",MODE_PRIVATE);

                String logout_email;
                String logout_password;

                logout_email = sharedPreferences.getString("user_email",null);
                logout_password = sharedPreferences.getString("user_password",null);

                Intent intent = new Intent(getActivity(),com.alphine.team4.carlife.ui.login.LoginSystemActivity.class);
                intent.putExtra("logout_email",logout_email);
                intent.putExtra("logout_password",logout_password);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("user_email");
                editor.remove("user_password");
                editor.commit();

                Intent i = new Intent(getActivity(), SocketService.class);
                getActivity().stopService(i);

                getActivity().finish();

                startActivity(intent);
                Toast.makeText(getActivity(),nickName+" 已登出",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if(isServiceRunning(ISocketBinder.SERVICE_NAME)) {
            btStartService.setChecked(true);
            Intent i = new Intent(getActivity(), SocketService.class);
            getActivity().bindService(i, connection, BIND_AUTO_CREATE);
        }
    }

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
            socketBinder = null;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.etRemoteIP:
                inputTitleDialog(view.getId(),etRemoteIP.getText().toString());
                break;
            case R.id.etRemotePort:
                inputTitleDialog(view.getId(),etRemotePort.getText().toString());
                break;
            case R.id.etLocalPort:
                inputTitleDialog(view.getId(),etLocalPort.getText().toString());
                break;
        }
    }

    private void inputTitleDialog(final int viewId, String currentText) {

        final EditText etInput = new EditText(getActivity());
        etInput.setFocusable(true);
        etInput.setText(currentText);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("请输入")
                .setView(etInput)
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(socketBinder == null){
                            Toast.makeText(getActivity(),"没有链接到服务！",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String input = etInput.getText().toString();
                        try {
                            switch (viewId){
                                case R.id.etRemoteIP:
                                    socketBinder.setRemoteIP(input);
                                    etRemoteIP.setText(input);
                                    break;
                                case R.id.etRemotePort:
                                    socketBinder.setRemotePort(Integer.parseInt(input));
                                    etRemotePort.setText(input);
                                    break;
                                case R.id.etLocalPort:
                                    socketBinder.setLocalPort(Integer.parseInt(input));
                                    etLocalPort.setText(input);
                                    break;
                            }
                            Toast.makeText(getActivity(),"设置成功！",Toast.LENGTH_SHORT).show();
                        }catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(getActivity(),"设置失败！",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
        builder.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if(!compoundButton.isPressed())return;
        if(compoundButton.getId()!=R.id.toggleButtonStartService && socketBinder==null){
            Toast.makeText(getActivity(),"请开启服务！",Toast.LENGTH_SHORT).show();
            compoundButton.setChecked(false);
            return;
        }
        switch (compoundButton.getId()){
            case R.id.switchUDP:
                try {
                    socketBinder.setUDPEnabled(b);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.toggleButtonStartService:
                Intent i = new Intent(getActivity(), SocketService.class);
                if(b){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        getActivity().startForegroundService(i);
                    }else {
                        getActivity().startService(i);
                    }
                    getActivity().bindService(i, connection, BIND_AUTO_CREATE);

                }else {
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
                    swUdp.setChecked(false);
                }
                break;
        }
    }

    boolean isServiceRunning(String serviceName){
        // 校验服务是否还存在
        ActivityManager am = (ActivityManager) getActivity().getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> services = am.getRunningServices(100);
        for (ActivityManager.RunningServiceInfo info : services) {
            // 得到所有正在运行的服务的名称
            String name = info.service.getClassName();
            if (serviceName.equals(name)) {
                return true;
            }
        }
        return false;
    }
}
