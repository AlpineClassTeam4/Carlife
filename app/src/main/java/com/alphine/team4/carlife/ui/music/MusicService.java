package com.alphine.team4.carlife.ui.music;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class MusicService extends Service {
    public MusicService() {
    }

    public Thread dataThread;
    @Override
    public void onCreate() {
        super.onCreate();
        dataThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true)
                {
                    try {
                        Thread.sleep(5000);
                        Log.e("TAG", "run: "+"!!!!!!!!!");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        });
        dataThread.start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return iBinder;
    }

    public class MyBinder extends Binder{


    }
    MyBinder iBinder = new MyBinder();

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
