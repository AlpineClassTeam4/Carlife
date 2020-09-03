package com.alphine.team4.carlife.ui.music.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alphine.team4.carlife.R;
import com.alphine.team4.carlife.ui.music.MusicService;
import com.alphine.team4.carlife.ui.music.adapter.MusicPagerAdapter;
import com.alphine.team4.carlife.ui.music.fragment.LocalMusicFragment;
import com.alphine.team4.carlife.ui.music.fragment.OnlineMusicFragment;

import java.util.ArrayList;
import java.util.List;

public class MusicActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvLocalmusic,tvOnlinemusic;
    ImageView ivSearch,ivBack;
    ViewPager viewPager;

    //将Fragment放入List集合中，存放fragment对象
    private List<Fragment> fragmentList = new ArrayList<>();

    @Override
    protected void onStart() {
        super.onStart();
        //绑定服务
        Intent serviceintent = new Intent(this,MusicService.class);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //解绑
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        //绑定id
        bangdingID();
        //设置监听
        jianting();
        //创建fragment对象
        LocalMusicFragment localMusicFragment = new LocalMusicFragment();
        OnlineMusicFragment onlineMusicFragment = new OnlineMusicFragment();
        //将fragment对象添加到fragmentList中
        fragmentList.add(localMusicFragment);
        fragmentList.add(onlineMusicFragment);
        //通过MusicPagerAdapter类创建musicPagerAdapter的适配器，下面我将添加MusicPagerAdapter类的创建方法
        MusicPagerAdapter musicPagerAdapter = new MusicPagerAdapter(getSupportFragmentManager(), fragmentList);
        //viewPager绑定适配器
        viewPager.setAdapter(musicPagerAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        tvLocalmusic.setTextColor(getResources().getColor(R.color.colorWhite));
                        tvOnlinemusic.setTextColor(getResources().getColor(R.color.grey));
                        break;
                    case 1:
                        tvOnlinemusic.setTextColor(getResources().getColor(R.color.colorWhite));
                        tvLocalmusic.setTextColor(getResources().getColor(R.color.grey));
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void bangdingID(){
        tvLocalmusic = findViewById(R.id.tv_localmusic);
        tvOnlinemusic = findViewById(R.id.tv_onlinemusic);
        ivSearch = findViewById(R.id.iv_search);
        ivBack = findViewById(R.id.iv_back);
        viewPager = findViewById(R.id.music_viewpager);
        tvLocalmusic.setTextColor(getResources().getColor(R.color.colorWhite));
    }

    private void jianting(){
        tvLocalmusic.setOnClickListener(this);
        tvOnlinemusic.setOnClickListener(this);
        ivSearch.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        viewPager.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_localmusic:
                viewPager.setCurrentItem(0);
                break;
            case R.id.tv_onlinemusic:
                viewPager.setCurrentItem(1);
                break;
            case R.id.iv_search:
                break;
            case R.id.iv_back:
                this.finish();
                break;
        }
    }
}
