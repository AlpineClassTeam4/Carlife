package com.alphine.team4.carlife.ui.discover;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import com.alphine.team4.carlife.R;
import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerListener;
import java.util.ArrayList;
import java.util.List;



public class DiscoverFragment extends Fragment {

    private DiscoverViewModel discoverViewModel;
    private MediaPlayer mp,op;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        discoverViewModel =
                ViewModelProviders.of(this).get(DiscoverViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        mp = MediaPlayer.create(this.getActivity(),R.raw.meng);
        mp.start();
        return root;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        List images = new ArrayList();
        images.add(R.drawable.a1);
        images.add(R.drawable.a8);
        images.add(R.drawable.a7);


        Banner banner = (Banner) getActivity().findViewById(R.id.banner);
        //设置图片加载器
        banner.setImageLoader(new GlideImageLoader());
        //设置图片集合
        banner.setImages(images);
        //banner设置方法全部调用完毕时最后调用
        banner.start();

        //增加点击事件
        banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                Intent i = new Intent(getActivity(),MainActivity.class);
                startActivity(i);
                mp.stop();
                op = MediaPlayer.create(getActivity(),R.raw.timi);
                op.start();
                //Toast.makeText(getActivity(), "position"+position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mp.stop();

    }
}
