package com.alphine.team4.carlife.ui.setting.SocketHelper;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

public class TCPServerHelper {
    private final String TAG = "TCPServerHelper";
    private static final int THREAD_JOIN_TIME = 2000;

    private int localPort = 7000;
    private ServerSocket mServerSocket = null;
    private List<TCPHelper> acceptSocketList = new ArrayList<>();
    private OnAcceptListener onAcceptListener = null;
    private Thread listenThread = null;
    private boolean isListenStart = false;
    private OnEventListener eventListener = null;

    private final int HANDLE_OPEN_SUCCESS= 101;
    private final int HANDLE_CLOSE_SUCCESS= 111;
    private final int HANDLE_OPEN_FAILED= 102;
    private final int HANDLE_ACCEPT_ERROR= 104;
    private final int HANDLE_UNKNOWN_ERROR= 105;

    public TCPServerHelper() {
    }

    public TCPServerHelper(int localPort) {
        this.localPort = localPort;
    }

    public void setLocalPort(int Port) {
        this.localPort = Port;
    }

    public int getLocalPortPort() {
        return localPort;
    }

    public boolean isListenStart() {
        return isListenStart;
    }

    public void listenStart() {
        if(isListenStart)return;
        listenThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "listenThread: Begin!");
                try {
                    mServerSocket = new ServerSocket(localPort);
                    mServerSocket.setSoTimeout(1000);
                } catch (IOException e) {
                    mHandler.sendEmptyMessage(HANDLE_OPEN_FAILED);
                    e.printStackTrace();
                    return;
                }
                mHandler.sendEmptyMessage(HANDLE_OPEN_SUCCESS);
                isListenStart = true;
                Log.d(TAG, "listenThread: open success!");
                while (isListenStart) {
                    try {
                        Socket socket = mServerSocket.accept();
                        TCPHelper tcpHelper = new TCPHelper(socket);
                        acceptSocketList.add(tcpHelper);
//                        tcpHelper.startReceiveData();
                        if(onAcceptListener!=null) onAcceptListener.onAccepted(tcpHelper);
                        Log.d(TAG, "listenThread: accept new client: remote addr="
                                +socket.getInetAddress().getHostAddress());
                    } catch (SocketTimeoutException e) {
                        Log.d(TAG, "listenThread: is waiting connect...");
                    } catch (IOException e) {
                        mHandler.sendEmptyMessage(HANDLE_ACCEPT_ERROR);
                        e.printStackTrace();
                    }
                }
                try {
                    mServerSocket.close();
                    mHandler.sendEmptyMessage(HANDLE_CLOSE_SUCCESS);
                } catch (IOException e) {
                    mHandler.sendEmptyMessage(HANDLE_UNKNOWN_ERROR);
                    e.printStackTrace();
                }
                Log.d(TAG, "listenThread: End!");
            }
        });
        listenThread.start();
    }

    public void listenStop(){
        isListenStart = false;
    }

    public void listenStopSync(){
        if(!isListenStart)return;
        isListenStart = false;
        if(listenThread!=null && listenThread.isAlive()){
            try {
                listenThread.join(THREAD_JOIN_TIME);
            } catch (InterruptedException e) {
                mHandler.sendEmptyMessage(HANDLE_UNKNOWN_ERROR);
                e.printStackTrace();
            }
        }
    }

    public void listenRestart(){
        new Thread(){
            @Override
            public void run() {
                dropAllClient();
                listenStopSync();
                listenStart();
            }
        }.start();
    }

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLE_OPEN_SUCCESS:
                    if (eventListener != null) eventListener.onEvent(EVENT.OPEN_SUCCESS);
                    break;
                case HANDLE_CLOSE_SUCCESS:
                    if (eventListener != null) eventListener.onEvent(EVENT.CLOSE_SUCCESS);
                    break;
                case HANDLE_OPEN_FAILED:
                    if (eventListener != null) eventListener.onEvent(EVENT.OPEN_FAILED);
                    break;
                case HANDLE_ACCEPT_ERROR:
                    if (eventListener != null) eventListener.onEvent(EVENT.ACCEPT_ERROR);
                    break;
                case HANDLE_UNKNOWN_ERROR:
                    if (eventListener != null) eventListener.onEvent(EVENT.UNKNOWN_ERROR);
                    break;
            }
        }
    };

    public void dropClient(TCPHelper tcpClient){
        if(tcpClient.isOpen()){
            acceptSocketList.remove(tcpClient);
            tcpClient.stopReceiveData();
            tcpClient.closeSocket();
        }
    }
    public void dropClient(String remoteIp, int remotePort){
        for (TCPHelper tcpClient:acceptSocketList
        ) {
            if(tcpClient.getRemoteIP().equals(remoteIp) &&
                    tcpClient.getRemotePort()==remotePort){
                dropClient(tcpClient);
            }
        }
    }

    public void dropAllClient(){
        for (TCPHelper tcpClient : acceptSocketList
        ) {
            dropClient(tcpClient);
        }
    }

    public void sendToAll(String data) {
        if (data.isEmpty() || acceptSocketList.size() == 0) return;
        for (TCPHelper tcp:acceptSocketList
        ) {
            tcp.send(data);
        }
    }

    public void sendTo(String remoteIp, int remotePort, String data) {
        if (data.isEmpty() || acceptSocketList.size() == 0) return;
        for (TCPHelper tcpClient:acceptSocketList
        ) {
            if(tcpClient.getRemoteIP().equals(remoteIp) &&
                    tcpClient.getRemotePort()==remotePort){
                tcpClient.send(data);
            }
        }
    }

    public void setOnAcceptListener(OnAcceptListener listener) {
        onAcceptListener = listener;
    }
    public void setOnEventListener(OnEventListener listener) {
        eventListener = listener;
    }
    public interface OnAcceptListener {
        void onAccepted(TCPHelper tcpClient);
    }
    public enum EVENT{
        OPEN_SUCCESS,
        CLOSE_SUCCESS,
        OPEN_FAILED,
        ACCEPT_ERROR,
        UNKNOWN_ERROR,
    }
    public interface OnEventListener {
        void onEvent(EVENT e);
    }
}

