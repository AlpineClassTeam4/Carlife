package com.alphine.team4.carlife.ui.setting;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import com.alphine.team4.carlife.IOnSocketReceivedListener;
import com.alphine.team4.carlife.ISocketBinder;
import com.alphine.team4.carlife.R;
import com.alphine.team4.carlife.ui.setting.SocketHelper.TCPHelper;
import com.alphine.team4.carlife.ui.setting.SocketHelper.TCPServerHelper;
import com.alphine.team4.carlife.ui.setting.SocketHelper.UDPHelper;

import androidx.core.app.NotificationCompat;

public class SocketService extends Service {
    public static final String SERVICE_NAME = "com.neusoft.qiangzi.socketservicedemo.SocketService";
    private static final String TAG = "SocketService";
    private static final String SHP_NAME = "socket_config";
    private int localPort = 6000;
    private int remotePort = 6000;
    private String remoteIP = "10.0.2.2";
    private UDPHelper udpHelper = null;
    private TCPHelper tcpHelper = null;
    private TCPServerHelper tcpServerHelper = null;
    private RemoteCallbackList<IOnSocketReceivedListener> mListenerList = new RemoteCallbackList<>();

    public SocketService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: is called.");

        loadSocketParameter();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: is called");
        NotificationChannel notificationChannel = null;
        String CHANNEL_ID = getClass().getPackage().toString();
        String CHANNEL_NAME = "Socket Servier";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        Notification notification = new NotificationCompat.Builder(getApplicationContext(),CHANNEL_ID)
                .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, SettingActivity.class), 0))
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("网络服务")
                .setContentText("网络服务运行中。。。")
                .setWhen(System.currentTimeMillis())
                .build();
        startForeground(1,notification);

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: is called.");
        return new ISocketBinder.Stub() {
            @Override
            public void setUDPEnabled(boolean enabled) throws RemoteException {
                if (enabled) {
                    if(udpHelper==null) {
                        udpHelper = new UDPHelper(localPort);
                        udpHelper.setRemoteIP(remoteIP);
                        udpHelper.setRemotePort(remotePort);
                        udpHelper.setOnReceiveListener(new UDPHelper.OnUDPReceiveListener() {
                            @Override
                            public void onReceived(String data) {
                                Log.d(TAG, "onReceived: data=" + data);
                                broadcastReceivedData(data);
                            }
                        });
                        udpHelper.setOnUDPEventListener(new UDPHelper.OnUDPEventListener() {
                            @Override
                            public void onUdpEvent(UDPHelper.UDP_EVENT e) {
                                switch (e){
                                    case UDP_OPEN_SUCCESS:
                                        if(udpHelper!=null)udpHelper.startReceiveData();
                                        broadcastEvent(IOnSocketReceivedListener.OPEN_SUCCESS);
                                        break;
                                    case UDP_CLOSE_SUCCESS:
                                        broadcastEvent(IOnSocketReceivedListener.CLOSE_SUCCESS);
                                        break;
                                    case UDP_OPEN_FAILED:
                                        broadcastEvent(IOnSocketReceivedListener.OPEN_FAILED);
                                        break;
                                    case UDP_SEND_ERROR:
                                        broadcastEvent(IOnSocketReceivedListener.SEND_ERROR);
                                        break;
                                    case UDP_RECV_ERROR:
                                        broadcastEvent(IOnSocketReceivedListener.RECV_ERROR);
                                        break;
                                    case UDP_UNKNOWN_ERROR:
                                        broadcastEvent(IOnSocketReceivedListener.UNKNOWN_ERROR);
                                        break;
                                }
                            }
                        });
                        udpHelper.openSocket();
//                        udpHelper.startReceiveData();
                        Log.d(TAG, "setSocketType: UDP init...");
                    }else {
                        Log.d(TAG, "setSocketType: UDP is already running...");
                    }
                }else {
                    if(udpHelper!=null){
                        udpHelper.stopReceiveData();
                        udpHelper.closeSocket();
                        udpHelper = null;
                    }
                }
            }

            @Override
            public void setTCPEnabled(boolean enabled) throws RemoteException {
                if (enabled) {
                    if(tcpHelper==null) {
                        tcpHelper = new TCPHelper(remoteIP, remotePort);
                        tcpHelper.setOnReceiveListener(new TCPHelper.OnReceivedListener() {
                            @Override
                            public void onReceived(TCPHelper tcpHelper, String data) {
                                Log.d(TAG, "onReceived: data=" + data);
                                broadcastReceivedData(data);
                            }
                        });
                        tcpHelper.setOnTCPEventListener(new TCPHelper.OnTCPEventListener() {
                            @Override
                            public void onTcpEvent(TCPHelper helper, TCPHelper.TCP_EVENT e) {
                                switch (e){
                                    case TCP_OPEN_SUCCESS:
                                        if(tcpHelper!=null)tcpHelper.startReceiveData();
                                        broadcastEvent(IOnSocketReceivedListener.OPEN_SUCCESS);
                                        break;
                                    case TCP_CLOSE_SUCCESS:
                                        broadcastEvent(IOnSocketReceivedListener.CLOSE_SUCCESS);
                                        break;
                                    case TCP_OPEN_FAILED:
                                        broadcastEvent(IOnSocketReceivedListener.OPEN_FAILED);
                                        break;
                                    case TCP_OPEN_TIMEOUT:
                                        broadcastEvent(IOnSocketReceivedListener.OPEN_TIMEOUT);
                                        break;
                                    case TCP_SEND_ERROR:
                                        broadcastEvent(IOnSocketReceivedListener.SEND_ERROR);
                                        break;
                                    case TCP_RECV_ERROR:
                                        broadcastEvent(IOnSocketReceivedListener.RECV_ERROR);
                                        break;
                                    case TCP_BREAK_OFF:
                                        Log.d(TAG, "onTcpEvent: detect disconnect.");
                                        broadcastEvent(IOnSocketReceivedListener.BREAK_OFF);
                                        if(tcpHelper!=null){
                                            tcpHelper.stopReceiveData();
                                            tcpHelper.closeSocket();
                                            tcpHelper = null;
                                        }
                                        break;
                                    case TCP_UNKNOWN_ERROR:
                                        broadcastEvent(IOnSocketReceivedListener.UNKNOWN_ERROR);
                                        break;
                                }
                            }
                        });
                        tcpHelper.openSocket();

                        Log.d(TAG, "setSocketType: TCP init OK.");
                    }else {
                        Log.d(TAG, "setSocketType: UDP is already running...");
                    }
                }else {
                    if(tcpHelper!=null) {
                        tcpHelper.stopReceiveData();
                        tcpHelper.closeSocket();
                        tcpHelper = null;
                    }
                }
            }

            @Override
            public void setTCPServerEnabled(boolean enabled) throws RemoteException {
                if(enabled){
                    if(tcpServerHelper==null){
                        tcpServerHelper = new TCPServerHelper(localPort);
                        tcpServerHelper.setOnAcceptListener(new TCPServerHelper.OnAcceptListener() {
                            @Override
                            public void onAccepted(TCPHelper tcpClient) {
                                tcpClient.setOnReceiveListener(new TCPHelper.OnReceivedListener() {
                                    @Override
                                    public void onReceived(TCPHelper tcpHelper, String data) {
                                        Log.d(TAG, "onReceived: data=" + data);
                                        broadcastReceivedData(data);
                                    }
                                });
                                tcpClient.setOnTCPEventListener(new TCPHelper.OnTCPEventListener() {
                                    @Override
                                    public void onTcpEvent(TCPHelper tcpHelper, TCPHelper.TCP_EVENT e) {
                                        switch (e){
                                            case TCP_OPEN_SUCCESS:
                                                broadcastEvent(IOnSocketReceivedListener.ACCEPT_SUCCESS);
                                                break;
                                            case TCP_CLOSE_SUCCESS:
                                                broadcastEvent(IOnSocketReceivedListener.CLOSE_SUCCESS);
                                                break;
                                            case TCP_OPEN_FAILED:
                                                broadcastEvent(IOnSocketReceivedListener.ACCEPT_ERROR);
                                                break;
                                            case TCP_OPEN_TIMEOUT:
                                                broadcastEvent(IOnSocketReceivedListener.OPEN_TIMEOUT);
                                                break;
                                            case TCP_SEND_ERROR:
                                                broadcastEvent(IOnSocketReceivedListener.SEND_ERROR);
                                                break;
                                            case TCP_RECV_ERROR:
                                                broadcastEvent(IOnSocketReceivedListener.RECV_ERROR);
                                                break;
                                            case TCP_BREAK_OFF:
                                                Log.d(TAG, "onTcpEvent: detect disconnect.");
                                                broadcastEvent(IOnSocketReceivedListener.BREAK_OFF);
                                                tcpHelper.stopReceiveData();
                                                tcpHelper.closeSocket();
                                                if(tcpServerHelper!=null)tcpServerHelper.dropClient(tcpHelper);
                                                break;
                                            case TCP_UNKNOWN_ERROR:
                                                broadcastEvent(IOnSocketReceivedListener.UNKNOWN_ERROR);
                                                break;
                                        }
                                    }
                                });
                                tcpClient.startReceiveData();
                            }
                        });
                        tcpServerHelper.setOnEventListener(new TCPServerHelper.OnEventListener() {
                            @Override
                            public void onEvent(TCPServerHelper.EVENT e) {
                                switch (e){
                                    case OPEN_SUCCESS:
                                        broadcastEvent(IOnSocketReceivedListener.OPEN_SUCCESS);
                                        break;
                                    case CLOSE_SUCCESS:
                                        broadcastEvent(IOnSocketReceivedListener.CLOSE_SUCCESS);
                                        break;
                                    case OPEN_FAILED:
                                        broadcastEvent(IOnSocketReceivedListener.OPEN_FAILED);
                                        break;
                                    case ACCEPT_ERROR:
                                        broadcastEvent(IOnSocketReceivedListener.ACCEPT_ERROR);
                                        break;
                                    case UNKNOWN_ERROR:
                                        broadcastEvent(IOnSocketReceivedListener.UNKNOWN_ERROR);
                                        break;
                                }
                            }
                        });
                        tcpServerHelper.listenStart();
                        Log.d(TAG, "setTCPServerEnabled: TCP Server is init OK.");
                    }else {
                        Log.d(TAG, "setTCPServerEnabled: TCP Server is already running...");
                    }
                }else {
                    if(tcpServerHelper!=null){
                        tcpServerHelper.dropAllClient();
                        tcpServerHelper.listenStop();
                        tcpServerHelper = null;
                    }
                }
            }

            @Override
            public boolean isUDPEnabled() throws RemoteException {
                return udpHelper!=null;
            }

            @Override
            public boolean isTCPEnabled() throws RemoteException {
                return tcpHelper!=null;
            }

            @Override
            public boolean isTCPServerEnabled() throws RemoteException {
                return tcpServerHelper!=null;
            }

            @Override
            public void setLocalPort(int Port) throws RemoteException {
                SocketService.this.localPort = Port;
                saveSocketParameter("localPort");
                if(udpHelper!=null) {
                    udpHelper.setLocalPort(Port);
                    udpHelper.restartReceiveData();
                }
                if(tcpServerHelper!=null){
                    tcpServerHelper.setLocalPort(Port);
                    tcpServerHelper.listenRestart();
                }
            }

            @Override
            public int getLocalPortPort() throws RemoteException {
                return localPort;
            }

            @Override
            public void setRemoteIP(String ip) throws RemoteException {
                SocketService.this.remoteIP = ip;
                saveSocketParameter("remoteIP");
                if(udpHelper!=null){
                    udpHelper.setRemoteIP(ip);
                }
                if(tcpHelper!=null){
                    tcpHelper.setRemoteIP(ip);
                }
            }

            @Override
            public String getRemoteIP() throws RemoteException {
                return remoteIP;
            }

            @Override
            public void setRemotePort(int port) throws RemoteException {
                SocketService.this.remotePort = port;
                saveSocketParameter("remotePort");
                if(udpHelper!=null){
                    udpHelper.setRemotePort(port);
                }
                if(tcpHelper!=null){
                    tcpHelper.setRemotePort(port);
                }
            }

            @Override
            public int getRemotePort() throws RemoteException {
                return remotePort;
            }

            @Override
            public void sendText(String text) throws RemoteException {
//                Log.d(TAG, "sendText: text="+text);
                if(udpHelper!=null){
                    udpHelper.send(text);
                }
                if(tcpHelper!=null){
                    tcpHelper.send(text);
                }
                if(tcpServerHelper!=null){
                    tcpServerHelper.sendToAll(text);
                }
            }

            @Override
            public void connect(String remoteIp, int remotePort) throws RemoteException {
                if(tcpHelper!=null) {
                    tcpHelper.openSocket();
                    tcpHelper.startReceiveData();
                }
            }

            @Override
            public void disconnect(String remoteIp, int remotePort) throws RemoteException {
                if(tcpHelper!=null && tcpHelper.getRemoteIP().equals(remoteIp)
                        &&tcpHelper.getRemotePort()==remotePort)
                {
                    tcpHelper.stopReceiveData();
                    tcpHelper.closeSocket();
                }
                else if(tcpServerHelper!=null){
                    tcpServerHelper.dropClient(remoteIp,remotePort);
                }
            }

            @Override
            public void registerListener(IOnSocketReceivedListener listener) throws RemoteException {
                mListenerList.register(listener);
                Log.d(TAG, "registerListener: current size:" + mListenerList.getRegisteredCallbackCount());
            }

            @Override
            public void unregisterListener(IOnSocketReceivedListener listener) throws RemoteException {
                mListenerList.unregister(listener);
                Log.d(TAG, "unregisterListener: current size:" + mListenerList.getRegisteredCallbackCount());
            }
        };
    }

    private void loadSocketParameter() {
        SharedPreferences shp = getSharedPreferences(SHP_NAME, MODE_PRIVATE);
        localPort = shp.getInt("localPort", 6000);
        remoteIP = shp.getString("remoteIP", "127.0.0.1");
        remotePort = shp.getInt("remotePort", 6000);
    }

    private void saveSocketParameter(String param) {
        SharedPreferences shp = getSharedPreferences(SHP_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = shp.edit();
        if(param.equals("localPort"))
            editor.putInt("localPort", localPort);
        else if(param.equals("remoteIP"))
            editor.putString("remoteIP", remoteIP);
        else if(param.equals("remotePort"))
            editor.putInt("remotePort", remotePort);
        editor.apply();
    }

    private void broadcastReceivedData(String data) {
        synchronized (mListenerList) {
            int n = mListenerList.beginBroadcast();
//            Log.d(TAG, "broadcastReceivedData: begin n="+n);
            try {
                for (int i = 0; i < n; i++) {
                    IOnSocketReceivedListener listener = mListenerList.getBroadcastItem(i);
                    if (listener != null) {
                        listener.onReceived(data);
                    }
                }
            } catch (RemoteException e) {
                Log.e(TAG, "broadcastReceivedData: error!");
                e.printStackTrace();
            }
            mListenerList.finishBroadcast();
//            Log.d(TAG, "broadcastReceivedData: end");
        }
    }
    private void broadcastEvent(int event) {
        synchronized (mListenerList) {
            int n = mListenerList.beginBroadcast();
//            Log.d(TAG, "broadcastEvent: begin n="+n);
            try {
                for (int i = 0; i < n; i++) {
                    IOnSocketReceivedListener listener = mListenerList.getBroadcastItem(i);
                    if (listener != null) {
                        listener.onEvent(event);
                    }
                }
            } catch (RemoteException e) {
                Log.e(TAG, "broadcastEvent: error!");
                e.printStackTrace();
            }
            mListenerList.finishBroadcast();
//            Log.d(TAG, "broadcastEvent: end");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: is called.");
        if(udpHelper != null && udpHelper.isOpen()){
            udpHelper.stopReceiveData();
            udpHelper.closeSocket();
            udpHelper = null;
        }
        if(tcpHelper!=null && tcpHelper.isOpen()){
            tcpHelper.stopReceiveData();
            tcpHelper.closeSocket();
            tcpHelper = null;
        }
        if(tcpServerHelper!=null && tcpServerHelper.isListenStart()){
            tcpServerHelper.dropAllClient();
            tcpServerHelper.listenStop();
            tcpServerHelper = null;
        }
    }
}
