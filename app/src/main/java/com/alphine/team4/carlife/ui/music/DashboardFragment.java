package com.alphine.team4.carlife.ui.music;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.alphine.team4.carlife.R;
import com.alphine.team4.carlife.ui.music.activity.CollectActivity;
import com.alphine.team4.carlife.ui.music.activity.MusicActivity;

public class DashboardFragment extends Fragment implements View.OnClickListener {

    private DashboardViewModel dashboardViewModel;
    ImageView ivFrgmusic,ivFrglike;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        final TextView textView = root.findViewById(R.id.text_dashboard);
        root.findViewById(R.id.frg_music_imgv).setOnClickListener(this);
        root.findViewById(R.id.frg_like_imgv).setOnClickListener(this);
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.frg_music_imgv:
                Intent musicintent = new Intent(getActivity(),MusicActivity.class);
                Intent serviceintent = new Intent(getActivity(),MusicService.class);
                musicintent.putExtra("Intent",serviceintent);
                startActivity(musicintent);
                getActivity().startService(serviceintent);
                break;
            case R.id.frg_like_imgv:
                startActivity(new Intent(getActivity(), CollectActivity.class));
                break;
            default:
                break;
        }
    }
}