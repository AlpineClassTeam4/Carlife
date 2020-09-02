package com.alphine.team4.carlife.ui.music.fragment;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alphine.team4.carlife.R;
import com.alphine.team4.carlife.ui.music.WyyMusic;
import com.alphine.team4.carlife.ui.music.adapter.WyyMusicAdapter;
import com.alphine.team4.carlife.ui.music.utils.RequestSongInfoInterceptor;

import com.lzx.starrysky.StarrySky;
import com.lzx.starrysky.StarrySkyConfig;
import com.lzx.starrysky.provider.SongInfo;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class OnlineMusicFragment extends Fragment implements WyyMusic.OnWyyListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "MainActivity";
    RecyclerView recyclerView;
    WyyMusicAdapter WyymusicAdapter;
    WyyMusic wyyMusic;
    SwipeRefreshLayout refreshLayout;

    public OnlineMusicFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_online_music, container, false);

        //初始化网易云数据
        wyyMusic = new WyyMusic(getActivity(), getActivity().getApplication());
        wyyMusic.setWyyListener(this);
        wyyMusic.login();

        //初始化列表视图
        WyymusicAdapter = new WyyMusicAdapter(getActivity(), wyyMusic.getPlayList());
        recyclerView = view.findViewById(R.id.rvPlaylist);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);//线性布局
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(WyymusicAdapter);

        //设置下拉更新
        refreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        refreshLayout.setHorizontalFadingEdgeEnabled(true);
        refreshLayout.setOnRefreshListener(this);

        //初始化播放器
        StarrySkyConfig config = new StarrySkyConfig
                .Builder()
                .addInterceptor(new RequestSongInfoInterceptor())
                .build();
        StarrySky.init(getActivity().getApplication(), config, null);
        return view;
    }

    @Override
    public void onRefresh() {
        if(wyyMusic.getPlaylistCount() > 1){
            wyyMusic.requestNextPlayList();
        }
    }

    @Override
    public void onLoginResult(boolean ok) {
        if(ok){
            //请求列表
            wyyMusic.requestSongList(0);
        }else {
            Toast.makeText(getActivity(),"登录失败！",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onGotPlaylist(List<SongInfo> list) {
        Log.d(TAG, "onGetPlayList: " + list.size());
        if(refreshLayout.isRefreshing())refreshLayout.setRefreshing(false);//停止刷新显示
        if(list == null){
            Toast.makeText(getActivity(),"网络错误，加载失败！",Toast.LENGTH_SHORT).show();
            return;
        }
        StarrySky.with().updatePlayList(list);
        WyymusicAdapter.setSongList(list);
        WyymusicAdapter.notifyDataSetChanged();
    }
}
