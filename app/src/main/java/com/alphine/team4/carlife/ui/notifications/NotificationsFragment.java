package com.alphine.team4.carlife.ui.notifications;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.alphine.team4.carlife.R;
import com.google.android.material.navigation.NavigationView;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;


public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;
    private int dt = 4;
    private TextView tv_dt;
    private Runnable runnable;
    private Timer timer;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);


        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        tv_dt = (TextView) getActivity().findViewById(R.id.tv_dt);
        tv_dt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timer.cancel();
                startMainActivity();
            }
        });
        countDown();


    }
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    tv_dt.setText("点击跳过 " + dt+"s");
                    dt--;
                    if(dt<0){
                        //关闭定时器
                        timer.cancel();
                        startMainActivity();
                    }
            }
        }
    };

    private void countDown(){
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(1);
            }
        };
        //开启计时器，时间间隔为1000ms
        timer.schedule(timerTask,1,1000);
    }


    private void startMainActivity() {
        startActivity(new
                Intent(getActivity(),MainActivity.class));
        //关闭当前页面
        //requireActivity().finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
