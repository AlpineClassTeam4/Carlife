package com.alphine.team4.carlife.ui.music.utils;

import android.os.Message;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.lzx.starrysky.intercept.InterceptorCallback;
import com.lzx.starrysky.intercept.StarrySkyInterceptor;
import com.lzx.starrysky.provider.SongInfo;
import com.lzx.starrysky.utils.MainLooper;
import com.lzy.okgo.OkGo;

import java.io.IOException;

import okhttp3.Response;

public class RequestSongInfoInterceptor implements StarrySkyInterceptor {

    public static final String BASE_URL = "http://levistar.cn:64000";
    private static final String TAG = "RequestSongInfoIntercep";

    @Override
    public void process(SongInfo songInfo, MainLooper mainLooper, InterceptorCallback interceptorCallback) {
        Log.d(TAG, "process: ");
        if (songInfo == null){
            Message msg = new Message();
            msg.what = 2;
            mainLooper.sendMessage(msg);
            interceptorCallback.onInterrupt(new RuntimeException("songInfo is null"));
            return;
        }
        if (songInfo.getSongUrl().isEmpty()) {
            try {
                Response response = OkGo.<String>get(BASE_URL + "/song/url?id=" + songInfo.getSongId()).execute();
                assert response.body() != null;
                String jsonData = response.body().string();
                Log.d(TAG, "process: " + jsonData);
                String url = JSON.parseObject(jsonData).getJSONArray("data").getJSONObject(0).getString("url");
                songInfo.setSongUrl(url);
                interceptorCallback.onContinue(songInfo);
            } catch (IOException e) {
                Message msg = new Message();
                msg.what = 2;
                mainLooper.sendMessage(msg);
                e.printStackTrace();
                interceptorCallback.onInterrupt(e);
            }
        } else {
            Message msg = new Message();
            msg.what = 2;
            mainLooper.sendMessage(msg);
            interceptorCallback.onContinue(songInfo);
        }
    }
}
