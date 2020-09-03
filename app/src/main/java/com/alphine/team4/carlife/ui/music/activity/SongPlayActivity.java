package com.alphine.team4.carlife.ui.music.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.alphine.team4.carlife.R;
import com.bumptech.glide.Glide;
import com.lzx.starrysky.StarrySky;
import com.lzx.starrysky.control.OnPlayerEventListener;
import com.lzx.starrysky.provider.SongInfo;

import java.util.Timer;
import java.util.TimerTask;

public class SongPlayActivity extends AppCompatActivity {

    private static final String TAG ="SongPlayActivity" ;
    private String songId;
    private SongInfo songInfo;
    ImageView ivCover,ivPlay,ivPrev,ivNext,ivBack;
    TextView tvTitle,tvArtist;
    SeekBar seekBar;

    Timer seekTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_play);

        ivBack = findViewById(R.id.sp_back_imgv);
        ivCover = findViewById(R.id.sp_cover_imgv);
        ivPlay = findViewById(R.id.sp_play_imgv);
        ivPrev = findViewById(R.id.sp_prev_imgv);
        ivNext = findViewById(R.id.sp_next_imgv);
        tvTitle = findViewById(R.id.sp_title_tv);
        tvArtist = findViewById(R.id.sp_artist_tv);
        seekBar = findViewById(R.id.seekBar);

        Intent i = getIntent();
        Log.e(TAG, "onCreate: "+i);
        songInfo = i.getParcelableExtra("songid");
        Log.e(TAG, "onCreate: "+i.getParcelableExtra("songid"));
        songId = songInfo.getSongId();
        Log.d(TAG, "onCreate: songid="+songId);

        seekTimer = new Timer();
        seekTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                seekBar.setProgress((int) StarrySky.with().getPlayingPosition()/1000);
            }
        },1000,1000);

        ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(StarrySky.with().isCurrMusicIsPlaying(songId)){
                    StarrySky.with().pauseMusic();
                    ivPlay.setImageResource(R.drawable.ic_play_btn_play);
                }else {
                    StarrySky.with().playMusicById(songId);
                    ivPlay.setImageResource(R.drawable.ic_play_btn_pause);
                }
            }
        });

        ivPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StarrySky.with().skipToPrevious();
            }
        });

        ivNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StarrySky.with().skipToNext();
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        StarrySky.with().addPlayerEventListener(eventListener);
        if(!StarrySky.with().isCurrMusicIsPlaying(songId)){
            StarrySky.with().playMusicById(songId);
        }
        updateView();

    }
    private void updateView() {
        if(songInfo == null)return;
        ivPlay.setImageResource(R.drawable.ic_play_btn_pause);
        tvTitle.setText(songInfo.getSongName());
        tvArtist.setText(songInfo.getArtist());
        Glide.with(this).load(songInfo.getSongCover()).into(ivCover);
        seekBar.setMax((int)songInfo.getDuration()/1000);
    }

    OnPlayerEventListener eventListener = new OnPlayerEventListener() {
        @Override
        public void onMusicSwitch(SongInfo songInfo) {
            Log.d(TAG, "onMusicSwitch: ");
            SongPlayActivity.this.songInfo = songInfo;
            updateView();
        }

        @Override
        public void onPlayerStart() {
            Log.d(TAG, "onPlayerStart: ");
            updateView();
        }

        @Override
        public void onPlayerPause() {
            Log.d(TAG, "onPlayerPause: ");
        }

        @Override
        public void onPlayerStop() {

        }

        @Override
        public void onPlayCompletion(SongInfo songInfo) {

        }

        @Override
        public void onBuffering() {

        }

        @Override
        public void onError(int i, String s) {

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        seekTimer.cancel();
        StarrySky.with().removePlayerEventListener(eventListener);
    }
}
