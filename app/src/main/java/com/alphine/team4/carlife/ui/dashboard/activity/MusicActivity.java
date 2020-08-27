package com.alphine.team4.carlife.ui.dashboard.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alphine.team4.carlife.R;
import com.alphine.team4.carlife.ui.dashboard.adapter.MusicPagerAdapter;
import com.alphine.team4.carlife.ui.dashboard.fragment.LocalMusicFragment;
import com.alphine.team4.carlife.ui.dashboard.fragment.OnlineMusicFragment;
import com.alphine.team4.carlife.ui.dashboard.utils.Music;

import java.util.ArrayList;
import java.util.List;

public class MusicActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvLocalmusic,tvOnlinemusic;
    ImageView ivSearch,ivBack;
    ViewPager viewPager;
    List<Music> mlist;
    //MusicAdapter musicAdapter;

    //将Fragment放入List集合中，存放fragment对象
    private List<Fragment> fragmentList = new ArrayList<>();
    ListView mlistView;

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

        //设置适配器
//        mlist = LocalmusicUtils.getmusic(this);
//        musicAdapter = new MusicAdapter(this, mlist);
//        musicAdapter.notifyDataSetChanged();
//        mlistView.setAdapter(musicAdapter);


        //音乐列表点击事件
//        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent();
//                intent.setClass(MusicActivity.this,PlayActivity.class);
//                intent.putExtra("position",String.valueOf(position));
//                startActivity(intent);
//            }
//        });

    }

    private void bangdingID(){
        tvLocalmusic = findViewById(R.id.tv_localmusic);
        tvOnlinemusic = findViewById(R.id.tv_onlinemusic);
        ivSearch = findViewById(R.id.iv_search);
        ivBack = findViewById(R.id.iv_back);
        viewPager = findViewById(R.id.music_viewpager);
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
                break;
        }
    }

//    class MusicAdapter extends BaseAdapter {
//
//        Context context;
//        List<Music> mlist;
//
//        public MusicAdapter(MusicActivity musicActivity, List<Music> mlist) {
//            this.context = musicActivity;
//            this.mlist = mlist;
//        }
//        @Override
//        public int getCount() {
//            return mlist.size();
//        }
//
//        @Override
//        public Object getItem(int i) {
//            return mlist.get(i);
//        }
//
//        @Override
//        public long getItemId(int i) {
//            return i;
//        }
//
//        @Override
//        public View getView(int i, View view, ViewGroup viewGroup) {
//
//            Musicholder musicholder;
//
//            if (view == null) {
//                musicholder = new Musicholder();
//                view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_listview, null);
//
//                musicholder.iv_cover = view.findViewById(R.id.iv_cover);
//                musicholder.tv_title = view.findViewById(R.id.tv_title);
//                musicholder.tv_artist = view.findViewById(R.id.tv_artist);
//                //musicholder.tv_duration = view.findViewById(R.id.tv_duration);
//
//                view.setTag(musicholder);
//
//            } else {
//                musicholder = (Musicholder) view.getTag();
//            }
//
//            musicholder.iv_cover.setImageResource(R.drawable.default_cover);
//            musicholder.tv_title.setText(mlist.get(i).music.toString());
//            musicholder.tv_artist.setText(mlist.get(i).singer.toString());
//            String time = LocalmusicUtils.formatTime(mlist.get(i).duration);
//
//           // musicholder.tv_duration.setText(time);
//            // musicholder.tv_position.setText(i + 1 + "");
//
//            return view;
//        }
//
//        class Musicholder {
//            TextView  tv_title, tv_artist, tv_duration;
//            ImageView iv_cover;
//        }
//
//    }
}
