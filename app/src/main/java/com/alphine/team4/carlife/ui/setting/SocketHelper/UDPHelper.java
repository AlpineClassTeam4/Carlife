package com.alphine.team4.carlife.ui.setting.SocketHelper;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.alphine.team4.carlife.ui.setting.utils.SocketUtils;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

public class UDPHelper {
    private final String TAG = "UDPHelper";
    private int LocalPort = 7001;
    private int RemotePort = 7001;
    private String RemoteIP = "127.0.0.1";
    private DatagramSocket mSocket;
    private OnUDPReceiveListener receiveListener;
    private Thread receiveThread;
    private InetAddress iaRemoteIP = null;
    private boolean isOpened = false;
    private boolean isStartRecv = false;
    private String ReceivedMsg;
    private final Object apiThreadLock = new Object();
    private OnUDPEventListener eventListener = null;

    private final int HANDLE_RECV_MSG = 100;
    private final int HANDLE_OPEN_SUCCESS= 101;
    private final int HANDLE_CLOSE_SUCCESS= 111;
    private final int HANDLE_OPEN_FAILED= 102;
    private final int HANDLE_SEND_ERROR= 104;
    private final int HANDLE_RECV_ERROR= 105;
    private final int HANDLE_UNKNOWN_ERROR= 106;

    public UDPHelper() {
    }

    public UDPHelper(int localPort) {
        this.LocalPort = localPort;
    }

    public void setLocalPort(int Port) {
        this.LocalPort = Port;
        Log.d(TAG, "setLocalPort: port=" + Port);
    }

    public int getLocalPortPort() {
        return LocalPort;
    }

    public boolean setRemoteIP(String ip) {
        if (SocketUtils.isIP(ip)) {
            RemoteIP = ip;
            Log.d(TAG, "setRemoteIP: ip=" + ip);
            return true;
        } else return false;
    }

    public void setRemotePort(int port) {
        RemotePort = port;
        Log.d(TAG, "setRemotePort: port=" + port);
    }

    public int getRemotePort() {
        return RemotePort;
    }

    public boolean isOpen() {
        return isOpened;
    }

    public synchronized void openSocket() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (apiThreadLock) {
                    try {
                        if (isOpened) {
                            return;
                        }
                        mSocket = new DatagramSocket(LocalPort);
                        mSocket.setSoTimeout(1000);//timeout for read
                        isOpened = true;
                        mHandler.sendEmptyMessage(HANDLE_OPEN_SUCCESS);
                        Log.d(TAG, "openSocket: OK.");
                    } catch (Exception e) {
                        mSocket = null;
                        isOpened = false;
                        mHandler.sendEmptyMessage(HANDLE_OPEN_FAILED);
                        Log.e(TAG, "openSocket error.e=" + e.toString());
                    }
                }
            }
        }).start();

    }

    public void send(final String data) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
//                    mSocket = new DatagramSocket();
//                    mSocket.connect(InetAddress.getByName(RemoteIP), RemotePort);
                    if (mSocket == null || mSocket.isClosed()) {
                        Log.e(TAG, "send: socket is inavilable");
                        return;
                    }

                    Log.d(TAG, "send:" + data);
                    byte[] datas = data.getBytes();
                    final DatagramPacket packet = new DatagramPacket(datas, datas.length, InetAddress.getByName(RemoteIP), RemotePort);
                    mSocket.send(packet);
                } catch (Exception e) {
                    mHandler.sendEmptyMessage(HANDLE_SEND_ERROR);
                    Log.e(TAG, "send error.e=" + e.toString());
                }
            }
        });
        t.start();
    }

    public void setOnUDPEventListener(OnUDPEventListener listener) {
        eventListener = listener;
    }
    public void setOnReceiveListener(OnUDPReceiveListener listener) {
        receiveListener = listener;
    }

    public String getRemoteIP() {
        if (iaRemoteIP == null) return "";
        return iaRemoteIP.getHostAddress();
    }

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLE_RECV_MSG:
                    if (receiveListener != null) receiveListener.onReceived(ReceivedMsg);
                    break;
                case HANDLE_OPEN_SUCCESS:
                    if (eventListener != null) eventListener.onUdpEvent(UDP_EVENT.UDP_OPEN_SUCCESS);
                    break;
                case HANDLE_CLOSE_SUCCESS:
                    if (eventListener != null) eventListener.onUdpEvent(UDP_EVENT.UDP_CLOSE_SUCCESS);
                    break;
                case HANDLE_OPEN_FAILED:
                    if (eventListener != null) eventListener.onUdpEvent(UDP_EVENT.UDP_OPEN_FAILED);
                    break;
                case HANDLE_SEND_ERROR:
                    if (eventListener != null) eventListener.onUdpEvent(UDP_EVENT.UDP_SEND_ERROR);
                    break;
                case HANDLE_RECV_ERROR:
                    if (eventListener != null) eventListener.onUdpEvent(UDP_EVENT.UDP_RECV_ERROR);
                    break;
                case HANDLE_UNKNOWN_ERROR:
                    if (eventListener != null) eventListener.onUdpEvent(UDP_EVENT.UDP_UNKNOWN_ERROR);
                    break;
            }
        }
    };

    public synchronized void startReceiveData() {
        if (isStartRecv) return;
        receiveThread = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (apiThreadLock) {
                    if (mSocket == null || mSocket.isClosed()) {
                        Log.e(TAG, "receiveThread: socket is inavilable");
                        return;
                    }
                    Log.d(TAG, "receiveThread: start ok.");

                    isStartRecv = true;
                    byte[] datas = new byte[512];
                    //DatagramPacket packet = new DatagramPacket(datas, datas.length, null, LocalPort);
                    DatagramPacket packet = new DatagramPacket(datas, datas.length);

                    while (isStartRecv) {
                        try {
                            mSocket.receive(packet);
                            iaRemoteIP = packet.getAddress();
                            ReceivedMsg = new String(packet.getData()).trim();
                            mHandler.sendEmptyMessage(HANDLE_RECV_MSG);
                            java.util.Arrays.fill(datas, (byte) 0);
                            Log.d(TAG, "recv:(" + iaRemoteIP.getHostAddress() + ")" + ReceivedMsg);
                        } catch (SocketTimeoutException e) {
                            //超时，继续接受
                            Log.d(TAG, "receiveThread: is waiting...");
                        } catch (Exception e) {
                            mHandler.sendEmptyMessage(HANDLE_RECV_ERROR);
                            Log.e(TAG, "startReceiveData error.e=" + e.toString());
                        }
                    }
                    isStartRecv = false;
                    Log.d(TAG, "receiveThread: end!");
                }
            }
        });
        receiveThread.start();
    }

    public synchronized void stopReceiveData() {
        isStartRecv = false;
    }
    public synchronized void stopReceiveDataSync() {
        if (!isStartRecv) return;
        isStartRecv = false;
        try {
            if (receiveThread != null && receiveThread.isAlive()) {
                receiveThread.join();
                receiveThread = null;
            }
        } catch (Exception e) {
            mHandler.sendEmptyMessage(HANDLE_UNKNOWN_ERROR);
            Log.e(TAG, "stopReceiveData error.e=" + e.toString());
        }
    }
    public void closeSocket() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (apiThreadLock) {
                    try {
                        if (isStartRecv) {
                            stopReceiveDataSync();
                        }
                        if (mSocket != null && !mSocket.isClosed()) {
                            mSocket.close();
                            mSocket = null;
                        }
                        isOpened = false;
                        mHandler.sendEmptyMessage(HANDLE_CLOSE_SUCCESS);
                        Log.d(TAG, "closeSocket: ok");
                    } catch (Exception e) {
                        mHandler.sendEmptyMessage(HANDLE_UNKNOWN_ERROR);
                        Log.e(TAG, "closeSocket error.e=" + e.toString());
                    }
                }
            }
        }).start();
    }

    public void restartReceiveData() {
        new Thread() {
            @Override
            public void run() {
                synchronized (apiThreadLock) {
                    //停止接收线程
                    if (isStartRecv) {
                        isStartRecv = false;
                        try {
                            if (receiveThread != null && receiveThread.isAlive()) {
                                receiveThread.join();
                                receiveThread = null;
                            }
                        } catch (Exception e) {
                            mHandler.sendEmptyMessage(HANDLE_UNKNOWN_ERROR);
                            Log.e(TAG, "stopReceiveData error.e=" + e.toString());
                        }
                    }
                }
                closeSocket();
                openSocket();
                startReceiveData();
            }
        }.start();
    }

    public interface OnUDPReceiveListener {
        void onReceived(String data);
    }
    public enum UDP_EVENT{
        UDP_OPEN_SUCCESS,
        UDP_CLOSE_SUCCESS,
        UDP_OPEN_FAILED,
        UDP_SEND_ERROR,
        UDP_RECV_ERROR,
        UDP_UNKNOWN_ERROR
    }
    public interface OnUDPEventListener {
        void onUdpEvent(UDPHelper.UDP_EVENT e);
    }
}
