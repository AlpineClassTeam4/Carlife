package com.alphine.team4.carlife.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import com.alphine.team4.carlife.R;
import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerListener;
import java.util.ArrayList;
import java.util.List;



public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);



        notificationsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {


            }
        });


        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        List images = new ArrayList();
        images.add(R.drawable.a1);
        images.add(R.drawable.a2);
        images.add(R.drawable.a4);
        images.add(R.drawable.a5);
        images.add(R.drawable.a6);


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
                //Toast.makeText(getActivity(), "position"+position, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
