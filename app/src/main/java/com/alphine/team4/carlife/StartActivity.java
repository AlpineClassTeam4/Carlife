package com.alphine.team4.carlife;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.alphine.team4.carlife.ui.login.LoginSystemActivity;
import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.List;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

//        new Handler().postDelayed(new Runnable() {
//            @Override public void run() {
//                if (!StartActivity.this.isFinishing()) {
//                    Intent intent = new Intent(StartActivity.this, LoginSystemActivity.class);//3秒之后跳转到主界面执行
//                    startActivity(intent);
//                    StartActivity.this.finish();
//                }
//            }
//        }, 3000);

        List images = new ArrayList();
        images.add(R.drawable.start);
        images.add(R.drawable.a1);

        Banner banner = (Banner) findViewById(R.id.banner_start);
        //设置图片加载器
        banner.setImageLoader(new StartImageLoader());
        //设置图片集合
        banner.setImages(images);
        //banner设置方法全部调用完毕时最后调用
        banner.start();

        //增加点击事件
        banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                Intent i = new Intent(StartActivity.this,LoginSystemActivity.class);
                startActivity(i);
            }
        });
    }
}
