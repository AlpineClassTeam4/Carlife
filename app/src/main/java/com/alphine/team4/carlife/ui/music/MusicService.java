package com.alphine.team4.carlife.ui.music;

import android.animation.ObjectAnimator;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.FileObserver;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.animation.RotateAnimation;
import android.widget.SeekBar;


import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.alphine.team4.carlife.R;
import com.alphine.team4.carlife.musicaidl;
import com.alphine.team4.carlife.ui.music.activity.PlayActivity;
import com.alphine.team4.carlife.ui.music.utils.Common;

import java.io.IOException;

public class MusicService extends Service {
    public MusicService() {
    }

    SeekBar sbProgress;
    private static int serviceposition;
    private int totaltime;
    MediaPlayer mediaPlayer;
    private int i = 0;
    private int playMode = 0;
    private int buttonWhich = 0;
    private int musicWhich =0;
    private boolean isStop;
    private RotateAnimation rotateAnimation = null;
    private RotateAnimation rotateAnimation2 = null;

    public Thread dataThread;
    static String servicepath,nochangeservicepath;
    boolean play;

    //Handler实现向主线程进行传值
//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            sbProgress.setProgress((int) (msg.what));
//            tvCurrenttime.setText(formatTime(msg.what));
//        }
//    };
    
    @Override
    public void onCreate() {
        super.onCreate();
        play=true;
        final int[] i = {0};
        mediaPlayer = new MediaPlayer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);

    }

    private void prevAndnextplaying(String path) {
        servicepath = path;
        if (path!=null){
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(path);
                mediaPlayer.prepare();                   // 准备
                mediaPlayer.start();                        // 启动
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if(!mediaPlayer.isPlaying()){
                            setPlayMode();
                        }
                    }
                });
            } catch (IllegalArgumentException | SecurityException | IllegalStateException
                    | IOException e) {
                e.printStackTrace();
            }
        }
//        if (musicWhich == 0){
//            tvTotaltime.setText(formatTime(Common.musicList.get(serviceposition).length));
//            sbProgress.setMax(Common.musicList.get(serviceposition).length);
//        }else {
//            tvTotaltime.setText(formatTime(Common.dbmusicList.get(serviceposition).length));
//            sbProgress.setMax(Common.dbmusicList.get(serviceposition).length);
//        }

//        MusicThread musicThread = new MusicThread();                                         //启动线程
//        new Thread(musicThread).start();
    }

    private void preparemusic(String path) {
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        if (musicWhich == 0){
//            tvTotaltime.setText(formatTime(Common.musicList.get(serviceposition).length));
//            sbProgress.setMax(Common.musicList.get(serviceposition).length);
//        }else {
//            tvTotaltime.setText(formatTime(Common.dbmusicList.get(serviceposition).length));
//            sbProgress.setMax(Common.dbmusicList.get(serviceposition).length);
//        }

//        MusicThread musicThread = new MusicThread();                                         //启动线程
//        new Thread(musicThread).start();
    }
    //创建一个类MusicThread实现Runnable接口，实现多线程
//    class MusicThread implements Runnable {
//
//        @Override
//        public void run() {
//            if (musicWhich == 0){
//                while (!isStop && Common.musicList.get(serviceposition) != null) {
//                    try {
//                        //让线程睡眠1000毫秒
//                        Thread.sleep(2000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    //放送给Handler现在的运行到的时间，进行ui更新
//                    handler.sendEmptyMessage(mediaPlayer.getCurrentserviceposition());
//                }
//            }else {
//                while (true) {
//                    try {
//                        //让线程睡眠1000毫秒
//                        Thread.sleep(2000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    //放送给Handler现在的运行到的时间，进行ui更新
//                    handler.sendEmptyMessage(mediaPlayer.getCurrentserviceposition());
//                }
//            }
//
//        }
//    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void setPlayMode(){
        if (musicWhich == 0){
            if (playMode == 0)//全部循环
            {
                if (serviceposition == Common.musicList.size() - 1)//默认循环播放
                {
                    serviceposition = 0;// 第一首
                    mediaPlayer.reset();
                    prevAndnextplaying(Common.musicList.get(serviceposition).path);

                } else {
                    serviceposition++;
                    mediaPlayer.reset();
                    prevAndnextplaying(Common.musicList.get(serviceposition).path);
                }
            } else if (playMode == 1)//单曲循环
            {
                //serviceposition不需要更改
                mediaPlayer.reset();
                prevAndnextplaying(Common.musicList.get(serviceposition).path);
            } else if (playMode == 2)//随机
            {
                serviceposition = (int) (Math.random() * Common.musicList.size());//随机播放
                mediaPlayer.reset();
                prevAndnextplaying(Common.musicList.get(serviceposition).path);
            }
        }else {
            if (playMode == 0)//全部循环
            {
                if (serviceposition == Common.dbmusicList.size() - 1)//默认循环播放
                {
                    serviceposition = 0;// 第一首
                    mediaPlayer.reset();
                    prevAndnextplaying(Common.dbmusicList.get(serviceposition).path);

                } else {
                    serviceposition++;
                    mediaPlayer.reset();
                    prevAndnextplaying(Common.dbmusicList.get(serviceposition).path);
                }
            } else if (playMode == 1)//单曲循环
            {
                //serviceposition不需要更改
                mediaPlayer.reset();
                prevAndnextplaying(Common.dbmusicList.get(serviceposition).path);
            } else if (playMode == 2)//随机
            {
                serviceposition = (int) (Math.random() * Common.dbmusicList.size());//随机播放
                mediaPlayer.reset();
                prevAndnextplaying(Common.dbmusicList.get(serviceposition).path);
            }
        }
    }

    private static final String TAG = "MusicService";
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return new musicaidl.Stub() {

            @Override
            public void setMusicPath(String path) throws RemoteException {
                Log.e(TAG, "!!!!!!!!!setMusicPath: path="+path);
                preparemusic(path);
            }

            @Override
            public void musicstart() throws RemoteException {
                mediaPlayer.start();
            }

            @Override
            public void musicstop() throws RemoteException {
                mediaPlayer.stop();
            }

            @Override
            public void musicpause() throws RemoteException {
                mediaPlayer.pause();
            }


            @Override
            public void musicnext(String path) throws RemoteException {
                prevAndnextplaying(path);
            }

            @Override
            public void musicprev(String path) throws RemoteException {
                prevAndnextplaying(path);
            }

            @Override
            public void musicseekto(int progress) throws RemoteException {
                mediaPlayer.seekTo(progress);
            }

            @Override
            public boolean isPlaying() throws RemoteException {
                return mediaPlayer.isPlaying();
            }

            @Override
            public int getPlayingPosition() throws RemoteException {
                return mediaPlayer.getCurrentPosition();
            }
        };
    }

//    public static class MyBinder extends Binder{
//
//        public void setposition(String path){
//            servicepath = path;
//        }
//    }

//    class TestFileObserver extends FileObserver {
//        public TestFileObserver(String path) {
//            super(path,FileObserver.ALL_EVENTS);
//        }
//        @Override
//        public void onEvent(int event, @Nullable String path) {
//            // 如果文件修改了 打印出文件相对监听文件夹的位置
//            if(event==FileObserver.MODIFY){
//                Log.d("edong",path);
//                nochangeservicepath = path;
//            }
//        }
//    }


    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
