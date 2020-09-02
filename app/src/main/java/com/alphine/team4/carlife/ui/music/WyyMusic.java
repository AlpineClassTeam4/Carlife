package com.alphine.team4.carlife.ui.music;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.lzx.starrysky.provider.SongInfo;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.DBCookieStore;
import com.lzy.okgo.https.HttpsUtils;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;
import com.lzy.okgo.model.Response;



import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import okhttp3.OkHttpClient;

import static com.alphine.team4.carlife.ui.music.utils.RequestSongInfoInterceptor.BASE_URL;

public class WyyMusic {
    private static final String TAG = "WywMusic";
    private static final String WYY_ACCOUNT = "15542303785";
    private static final String WYY_PASSWORD = "honey.16";
    String uid = null;
    private List<SongInfo> playList = new ArrayList<>();
    private Context context;
    private Application application;
    private boolean isLogin = false;
    private OnWyyListener wyyListener;
    private int playlistCount;
    private int currentPlaylistsIndex;
    private static WyyMusic instance;

    public WyyMusic(Context context, Application application) {
        this.context = context;
        this.application = application;
        this.instance = this;
    }
    public static WyyMusic getInstance() {
        return instance;
    }

    public void login(){
        initOKGO();
        loginCheck();
    }

    private void initOKGO() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //log相关
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("OkGo");
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);        //log打印级别，决定了log显示的详细程度
        loggingInterceptor.setColorLevel(Level.INFO);                               //log颜色级别，决定了log在控制台显示的颜色
        builder.addInterceptor(loggingInterceptor);                                 //添加OkGo默认debug日志

        //超时时间设置，默认60秒
        builder.readTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);      //全局的读取超时时间
        builder.writeTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);     //全局的写入超时时间
        builder.connectTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);   //全局的连接超时时间

        //自动管理cookie
        builder.cookieJar(new CookieJarImpl(new DBCookieStore(context)));              //使用数据库保持cookie，如果cookie不过期，则一直有效

        //https相关设置
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory();
        builder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);

        // 其他统一的配置
        // 详细说明看GitHub文档：https://github.com/jeasonlzy/okhttp-OkGo
        OkGo.getInstance().init(application)                           //必须调用初始化
                .setOkHttpClient(builder.build())               //建议设置OkHttpClient，不设置会使用默认的
                .setCacheMode(CacheMode.NO_CACHE)               //全局统一缓存模式，默认不使用缓存，可以不传
                .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)   //全局统一缓存时间，默认永不过期，可以不传
                .setRetryCount(1);                               //全局统一超时重连次数，默认为三次，那么最差的情况会请求4次(一次原始请求，三次重连请求)，不需要可以设置为0
    }

    private void loginCheck(){
        String phone = WYY_ACCOUNT;
        String password = WYY_PASSWORD;
        OkGo.<String>get(BASE_URL + "/login/cellphone?phone=" + phone + "&password=" + password).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                String jsonData = response.body();
                JSONObject json = JSON.parseObject(jsonData);
                int code = json.getInteger("code");
                if (code == 200){
                    uid = json.getJSONObject("account").getLong("id").toString();
                    Log.d(TAG, "onSuccess: 登录成功！");
                    isLogin = true;
                    wyyListener.onLoginResult(true);
                }else{
                    Log.d(TAG, "onSuccess: 登录失败" + code);
                    Toast.makeText(context, "登录失败", Toast.LENGTH_SHORT).show();
                    wyyListener.onLoginResult(false);
                }
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                wyyListener.onLoginResult(false);
            }
        });
    }

    public int getPlaylistCount() {
        return playlistCount;
    }

    public void setWyyListener(OnWyyListener wyyListener) {
        this.wyyListener = wyyListener;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public List<SongInfo> getPlayList() {
        return playList;
    }

    public void requestNextPlayList(){
        requestSongList((currentPlaylistsIndex +1)%playlistCount);
    }
    private WyyPlaylist wyyPlaylist;
    public void requestPlayList(){
        OkGo.<String>get(BASE_URL + "/user/playlist?uid=" + uid).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                String jsonData = response.body();
                Log.d(TAG, "requestPlayList: json="+jsonData);
                Gson gson = new Gson();
                wyyPlaylist = gson.fromJson(jsonData, WyyPlaylist.class);
                if (wyyPlaylist!=null && wyyPlaylist.getCode() == 200){
                    playlistCount = wyyPlaylist.getPlaylist().size();
                    Log.d(TAG, "requestPlayList: playlistCount="+playlistCount);
                    requestSongList(currentPlaylistsIndex);
                }else{
                    Log.d(TAG, "requestPlayList: 获取歌单失败");
                    wyyListener.onGotPlaylist(null);
                }
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                Log.e(TAG, "requestPlayList:onError: " + response.toString() );
                wyyListener.onGotPlaylist(null);
            }
        });
    }
    public void requestSongList(int index) {
        if (wyyPlaylist == null) {
            requestPlayList();
            return;
        }

        if (index < 0) index = 0;
        else if (index > wyyPlaylist.getPlaylist().size())
            index = wyyPlaylist.getPlaylist().size();
        long listId = wyyPlaylist.getPlaylist().get(index).getId();

        currentPlaylistsIndex = index;
        OkGo.<String>get(BASE_URL + "/playlist/detail?id=" + listId).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                String jsonData = response.body();
                Log.d(TAG, "requestSongList: json=" + jsonData);
                Gson gson = new Gson();
                MySongList songList = gson.fromJson(jsonData, MySongList.class);

                if (songList != null && songList.getCode() == 200) {
                    playList.clear();
                    playList = songList.getSongInfoList();
                    wyyListener.onGotPlaylist(playList);
                    Log.d(TAG, "requestSongList: playList=" + playList.size());
                } else {
                    Log.e(TAG, "requestSongList: 获取歌曲失败");
                    wyyListener.onGotPlaylist(null);
                }
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                Log.e(TAG, "requestSongList:onError: " + response.toString());
                wyyListener.onGotPlaylist(null);
            }
        });
    }

        public interface OnWyyListener{
        void onLoginResult(boolean ok);
        void onGotPlaylist(List<SongInfo> list);
    }
}
