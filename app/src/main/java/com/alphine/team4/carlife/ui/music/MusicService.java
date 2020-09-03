package com.alphine.team4.carlife.ui.music;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.animation.RotateAnimation;
import android.widget.SeekBar;

import com.alphine.team4.carlife.musicaidl;

import java.io.IOException;

public class MusicService extends Service {
    public MusicService() {
    }

    MediaPlayer mediaPlayer;
    boolean play;

    
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
        if (path!=null){

            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(path);
                mediaPlayer.prepare();                   // 准备
                mediaPlayer.start();                        // 启动
            } catch (IllegalArgumentException | SecurityException | IllegalStateException
                    | IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void preparemusic(String path) {
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final String TAG = "MusicService";
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return new musicaidl.Stub() {

            @Override
            public void setMusicPath(String path) throws RemoteException {
                prevAndnextplaying(path);
                try {
                    mediaPlayer.setDataSource(path);
                    Log.e("TAG", "prevAndnextplaying: 1   "+path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void musicstart() throws RemoteException {
                mediaPlayer.start();
                Log.e("TAG", "prevAndnextplaying: 2");
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
            public void musicreset() throws RemoteException {
                mediaPlayer.reset();
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

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
