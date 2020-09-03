package com.alphine.team4.carlife.ui.setting.SocketHelper;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.alphine.team4.carlife.ui.setting.utils.SocketUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

public class TCPHelper {
    private final String TAG = "TCPHelper";
    private int localPort = 7000;
    private int remotePort = 7000;
    private String remoteIP = "192.168.43.1";
    private Socket mSocket = null;
    private OutputStream outputStream = null;
    private InputStream inputStream = null;
    private OnReceivedListener mListener = null;
    private OnTCPEventListener eventListener = null;
    private Thread receiveThread = null;
    private boolean isOpened = false;
    private boolean isStartRecv = false;
    private String receivedMsg;

    private final int HANDLE_RECV_MSG = 100;
    private final int HANDLE_OPEN_SUCCESS= 101;
    private final int HANDLE_CLOSE_SUCCESS= 111;
    private final int HANDLE_OPEN_FAILED= 102;
    private final int HANDLE_OPEN_TIMEOUT= 103;
    private final int HANDLE_SEND_ERROR= 104;
    private final int HANDLE_RECV_ERROR= 105;
    private final int HANDLE_BREAK_OFF= 106;
    private final int HANDLE_UNKNOWN_ERROR = 107;


    public TCPHelper() {
    }

    public TCPHelper(Socket socket) {
        if(socket != null && socket.isConnected()){
            mSocket = socket;
            remoteIP = mSocket.getInetAddress().getHostAddress();
            Log.d(TAG, "TCPHelper: remoteIP="+remoteIP);
            remotePort = mSocket.getPort();
            Log.d(TAG, "TCPHelper: remotePort="+remotePort);
            localPort = mSocket.getLocalPort();
            Log.d(TAG, "TCPHelper: localPort="+localPort);
            try {
                mSocket.setSoTimeout(1000);//timeout for read
                mSocket.setKeepAlive(true);
                outputStream = mSocket.getOutputStream();
                inputStream = mSocket.getInputStream();
                isOpened = true;
                mHandler.sendEmptyMessage(HANDLE_OPEN_SUCCESS);
                Log.d(TAG, "TCPHelper: Create OK!");
            } catch (Exception e) {
                e.printStackTrace();
                isOpened = false;
                mSocket = null;
                mHandler.sendEmptyMessage(HANDLE_UNKNOWN_ERROR);
            }
        }
    }

    public TCPHelper(String remoteIP, int remotePort) {
        this.remoteIP = remoteIP;
        this.remotePort = remotePort;
    }

    public int getLocalPortPort() {
        return localPort;
    }

    public void setRemoteIP(String ip) {
        if (SocketUtils.isIP(ip)) {
            remoteIP = ip;
        }
    }
    public String getRemoteIP() {
        if(!remoteIP.isEmpty()){
            return remoteIP;
        }else if (mSocket != null && mSocket.isConnected()) {
            return mSocket.getRemoteSocketAddress().toString().split("/")[1];
        }else {
            return "";
        }
    }
    public void setRemotePort(int port) {
        remotePort = port;
    }

    public int getRemotePort() {
        return remotePort;
    }

    public boolean isOpen() {
        return isOpened;
    }

    public void openSocket() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (isOpened) {
                        return;
                    }
                    mSocket = new Socket();
                    SocketAddress socketAddress = new InetSocketAddress(remoteIP, remotePort);
                    mSocket.connect(socketAddress, 5000);
                    mSocket.setSoTimeout(1000);//timeout for read
                    mSocket.setKeepAlive(true);
                    outputStream = mSocket.getOutputStream();
                    inputStream = mSocket.getInputStream();
                    isOpened = true;
                    mHandler.sendEmptyMessage(HANDLE_OPEN_SUCCESS);
                    Log.d(TAG, "openSocket OK! remoteIP="+mSocket.getRemoteSocketAddress().toString());
                } catch (SocketTimeoutException e) {
                    Log.e(TAG, "openSocket timeout!");
                    mHandler.sendEmptyMessage(HANDLE_OPEN_TIMEOUT);
                    closeSocket();
                } catch (Exception e) {
                    Log.e(TAG, "openSocket error.e=" + e.toString());
                    mHandler.sendEmptyMessage(HANDLE_OPEN_FAILED);
                    closeSocket();
                }
            }
        }).start();

    }

    public void send(final String data) {
        if (data.isEmpty() || outputStream == null) return;

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d(TAG, "send:" + data);
                    outputStream.write(data.getBytes());
                } catch (Exception e) {
                    mHandler.sendEmptyMessage(HANDLE_SEND_ERROR);
                    Log.e(TAG, "send error.e=" + e.toString());
                }
            }
        });
        t.start();
    }

    public void setOnTCPEventListener(OnTCPEventListener listener) {
        eventListener = listener;
    }
    public void setOnReceiveListener(OnReceivedListener listener) {
        mListener = listener;
    }

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLE_RECV_MSG:
                    if (mListener != null) mListener.onReceived(TCPHelper.this, receivedMsg);
                    break;
                case HANDLE_OPEN_SUCCESS:
                    if (eventListener != null) eventListener.onTcpEvent(TCPHelper.this,TCP_EVENT.TCP_OPEN_SUCCESS);
                    break;
                case HANDLE_CLOSE_SUCCESS:
                    if (eventListener != null) eventListener.onTcpEvent(TCPHelper.this,TCP_EVENT.TCP_CLOSE_SUCCESS);
                    break;
                case HANDLE_OPEN_TIMEOUT:
                    if (eventListener != null) eventListener.onTcpEvent(TCPHelper.this,TCP_EVENT.TCP_OPEN_TIMEOUT);
                    break;
                case HANDLE_OPEN_FAILED:
                    if (eventListener != null) eventListener.onTcpEvent(TCPHelper.this,TCP_EVENT.TCP_OPEN_FAILED);
                    break;
                case HANDLE_SEND_ERROR:
                    if (eventListener != null) eventListener.onTcpEvent(TCPHelper.this,TCP_EVENT.TCP_SEND_ERROR);
                    break;
                case HANDLE_RECV_ERROR:
                    if (eventListener != null) eventListener.onTcpEvent(TCPHelper.this,TCP_EVENT.TCP_RECV_ERROR);
                    break;
                case HANDLE_BREAK_OFF:
                    if (eventListener != null) eventListener.onTcpEvent(TCPHelper.this,TCP_EVENT.TCP_BREAK_OFF);
                    break;
                case HANDLE_UNKNOWN_ERROR:
                    if (eventListener != null) eventListener.onTcpEvent(TCPHelper.this,TCP_EVENT.TCP_UNKNOWN_ERROR);
                    break;
            }
        }
    };

    public void startReceiveData() {
        receiveThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "startReceiveData ok.");
                isStartRecv = true;
                byte[] datas = new byte[512];
                while (isStartRecv) {
                    try {
                        int len = inputStream.read(datas);
                        if(len > 0) {
                            receivedMsg = new String(datas, 0, len);
                            mHandler.sendEmptyMessage(HANDLE_RECV_MSG);
                            //java.util.Arrays.fill(datas, (byte) 0);
                            Log.d(TAG, "receiveThread:" + receivedMsg);
                        }else if(len == -1){
                            Log.e(TAG, "receiveThread: read -1");
                            mHandler.sendEmptyMessage(HANDLE_BREAK_OFF);
                            Thread.sleep(100);
                            //break;
                        }
                    } catch (SocketTimeoutException e) {
                        //超时，继续接受
                        Log.d(TAG, "receiveThread: is waiting...!");
                    } catch (Exception e) {
                        //异常，可能是网络中断了
                        Log.e(TAG, "receiveThread error.e=" + e.toString());
                        mHandler.sendEmptyMessage(HANDLE_BREAK_OFF);
                        break;
                    }
                }
                isStartRecv = false;
                receiveThread = null;
                Log.d(TAG, "receiveThread: end!");
            }
        });
        receiveThread.start();
    }

    public void stopReceiveData() {
        isStartRecv = false;
    }

    public void stopReceiveDataSync() {
        if (!isStartRecv) return;
        isStartRecv = false;
        try {
            if (receiveThread != null && receiveThread.isAlive()) {
                receiveThread.join(2000);
                receiveThread = null;
            }
        } catch (Exception e) {
            Log.e(TAG, "stopReceiveData error.e=" + e.toString());
            mHandler.sendEmptyMessage(HANDLE_UNKNOWN_ERROR);
        }
    }

    public void closeSocket() {
        new Thread(){
            @Override
            public void run() {
                stopReceiveDataSync();
                try {
                    if (mSocket != null && !mSocket.isClosed()) {
                        mSocket.close();
                        mSocket = null;
                    }
                    if (outputStream != null) {
                        outputStream.close();
                        outputStream = null;
                    }
                    if (inputStream != null) {
                        inputStream.close();
                        inputStream = null;
                    }
                    isOpened = false;
                    mHandler.sendEmptyMessage(HANDLE_CLOSE_SUCCESS);
                } catch (Exception e) {
                    Log.e(TAG, "closeSocket error.e=" + e.toString());
                    mHandler.sendEmptyMessage(HANDLE_UNKNOWN_ERROR);
                }
            }
        }.start();
    }

    public interface OnReceivedListener {
        void onReceived(TCPHelper tcpHelper, String data);
    }

    public enum TCP_EVENT{
        TCP_OPEN_SUCCESS,
        TCP_CLOSE_SUCCESS,
        TCP_OPEN_FAILED,
        TCP_OPEN_TIMEOUT,
        TCP_SEND_ERROR,
        TCP_RECV_ERROR,
        TCP_BREAK_OFF,
        TCP_UNKNOWN_ERROR
    }
    public interface OnTCPEventListener {
        void onTcpEvent(TCPHelper tcpHelper, TCP_EVENT e);
    }
}

