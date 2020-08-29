package com.alphine.team4.carlife.ui.dashboard.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alphine.team4.carlife.R;
import com.alphine.team4.carlife.ui.dashboard.DBHelper.DBHelper;
import com.alphine.team4.carlife.ui.dashboard.utils.BlurUtil;
import com.alphine.team4.carlife.ui.dashboard.utils.Common;
import com.alphine.team4.carlife.ui.dashboard.utils.MergeImage;
import com.alphine.team4.carlife.ui.dashboard.utils.Music;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PlayActivity extends AppCompatActivity implements View.OnClickListener {
    TextView tvTitle,tvArtist;
    TextView tvCurrenttime,tvTotaltime;
    ImageView ivBack,ivModel,ivPrev,ivPlay,ivNext,ivLike;
    ImageView ivBg,ivDisc,ivNeedle;
    SeekBar sbProgress;
    private int position;
    private int totaltime;
    MediaPlayer mediaPlayer;
    private int i = 0;
    private int playMode = 0;
    private int buttonWhich = 0;
    private int musicWhich = 0;
    private boolean isStop;
    private ObjectAnimator objectAnimator = null;
    private RotateAnimation rotateAnimation = null;
    private RotateAnimation rotateAnimation2 = null;
    DBHelper dbHelper;
    public String title;//歌曲名
    public String artist;//歌手
    public int length;//歌曲时间长度
    //public long size;//歌曲所占空间大小
    public String path;//歌曲地址
    public String album;//专辑名
    public Bitmap albumBip;//专辑图片

    private static final String TAG="123";

    //Handler实现向主线程进行传值
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
            sbProgress.setProgress((int) (msg.what));
            tvCurrenttime.setText(formatTime(msg.what));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        bingID();
        dbHelper = new DBHelper(this);
        dbHelper.open();                            //打开数据库
        Intent intent = getIntent();                                                    //通过getIntent()方法实现intent信息的获取
        position = intent.getIntExtra("position", 0);            //获取position
        musicWhich = intent.getIntExtra("musicWhich",0);         //获取音乐来源  数据库/媒体库
        mediaPlayer = new MediaPlayer();
        if(musicWhich == 0){
            title = Common.musicList.get(position).title;
            artist = Common.musicList.get(position).artist;
            path = Common.musicList.get(position).path;
            albumBip = Common.musicList.get(position).albumBip;
        }else {
            title = Common.dbmusicList.get(position).title;
            artist = Common.dbmusicList.get(position).artist;
            path = Common.dbmusicList.get(position).path;
            albumBip = Common.dbmusicList.get(position).albumBip;
        }
        prevAndnextplaying(Common.musicList.get(position).path);
        sbProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {               //seekbar设置监听，实现指哪放到哪
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    //prevAndnext() 实现页面的展现
    private void prevAndnextplaying(String path) {
        isStop = false;
        mediaPlayer.reset();
        tvTitle.setText(Common.musicList.get(position).title);
        tvArtist.setText(Common.musicList.get(position).artist);
        ivPlay.setImageResource(R.drawable.ic_play_btn_pause);
        //检查数据库信息，更新like按钮状态
        String inputTitle = Common.musicList.get(position).title;
        String inputArtist = Common.musicList.get(position).artist;
        if (dbHelper.checkTitleArtist(inputTitle,inputArtist)){
            //数据存在
            ivLike.setImageResource(R.drawable.ic_play_btn_like_press);
        }else {
            //数据不存在
            ivLike.setImageResource(R.drawable.ic_play_btn_like);
        }


        if (Common.musicList.get(position).albumBip != null) {
            Bitmap bgbm = BlurUtil.doBlur(Common.musicList.get(position).albumBip, 10, 5);//将专辑虚化
            ivBg.setImageBitmap(bgbm);                                    //设置虚化后的专辑图片为背景
            Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.play_page_disc);//BitmapFactory.decodeResource用于根据给定的资源ID从指定的资源文件中解析、创建Bitmap对象。
            Bitmap bm = MergeImage.mergeThumbnailBitmap(bitmap1, Common.musicList.get(position).albumBip);//将专辑图片放到圆盘中
            ivDisc.setImageBitmap(bm);
        } else {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.play_page_default_bg);
            ivBg.setImageBitmap(bitmap);
            Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.play_page_disc);
            Bitmap bm = MergeImage.mergeThumbnailBitmap(bitmap1, bitmap);
            ivDisc.setImageBitmap(bm);
        }
        try {
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();                   // 准备
            mediaPlayer.start();                        // 启动
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if(!mediaPlayer.isPlaying()){
                        setPlayMode();
                    }

                }
            });
        } catch (IllegalArgumentException | SecurityException | IllegalStateException
                | IOException e) {
            e.printStackTrace();
        }

        tvTotaltime.setText(formatTime(Common.musicList.get(position).length));
        sbProgress.setMax(Common.musicList.get(position).length);

        MusicThread musicThread = new MusicThread();                                         //启动线程
        new Thread(musicThread).start();

        //实例化，设置旋转对象
        objectAnimator = ObjectAnimator.ofFloat(ivDisc, "rotation", 0f, 360f);
        //设置转一圈要多长时间
        objectAnimator.setDuration(18000);
        //设置旋转速率
        objectAnimator.setInterpolator(new LinearInterpolator());
        //设置循环次数 -1为一直循环
        objectAnimator.setRepeatCount(-1);
        //设置转一圈后怎么转
        objectAnimator.setRepeatMode(ValueAnimator.RESTART);
        objectAnimator.start();

        rotateAnimation = new RotateAnimation(-25f, 0f, Animation.RELATIVE_TO_SELF, 0.3f, Animation.RELATIVE_TO_SELF, 0.1f);
        rotateAnimation.setDuration(500);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setRepeatCount(0);
        rotateAnimation.setFillAfter(true);
        rotateAnimation.setStartOffset(50);
        ivNeedle.setAnimation(rotateAnimation);
        rotateAnimation.cancel();

        rotateAnimation2 = new RotateAnimation(0f, -25f, Animation.RELATIVE_TO_SELF, 0.3f, Animation.RELATIVE_TO_SELF, 0.1f);
        rotateAnimation2.setDuration(500);
        rotateAnimation2.setInterpolator(new LinearInterpolator());
        rotateAnimation2.setRepeatCount(0);
        rotateAnimation2.setFillAfter(true);
        ivNeedle.setAnimation(rotateAnimation2);
        rotateAnimation2.cancel();
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void setPlayMode(){
        if (playMode == 0)//全部循环
        {
            if (position == Common.musicList.size() - 1)//默认循环播放
            {
                position = 0;// 第一首
                mediaPlayer.reset();
                objectAnimator.pause();
                ivNeedle.startAnimation(rotateAnimation2);
                prevAndnextplaying(Common.musicList.get(position).path);

            } else {
                position++;
                mediaPlayer.reset();
                objectAnimator.pause();
                ivNeedle.startAnimation(rotateAnimation2);
                prevAndnextplaying(Common.musicList.get(position).path);
            }
        } else if (playMode == 1)//单曲循环
        {
            //position不需要更改
            mediaPlayer.reset();
            objectAnimator.pause();
            ivNeedle.startAnimation(rotateAnimation2);
            prevAndnextplaying(Common.musicList.get(position).path);
        } else if (playMode == 2)//随机
        {
            position = (int) (Math.random() * Common.musicList.size());//随机播放
            mediaPlayer.reset();
            objectAnimator.pause();
            ivNeedle.startAnimation(rotateAnimation2);
            prevAndnextplaying(Common.musicList.get(position).path);
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void setBtnMode() {
        if (playMode == 0)//全部循环
        {
            if (position == Common.musicList.size() - 1)//默认循环播放
            {
                if (buttonWhich == 1) {
                    position--;
                    mediaPlayer.reset();
                    objectAnimator.pause();
                    ivNeedle.startAnimation(rotateAnimation2);
                    prevAndnextplaying(Common.musicList.get(position).path);
                } else if (buttonWhich == 2) {
                    position = 0;// 第一首
                    mediaPlayer.reset();
                    objectAnimator.pause();
                    ivNeedle.startAnimation(rotateAnimation2);
                    prevAndnextplaying(Common.musicList.get(position).path);
                }
            } else if (position == 0) {
                if (buttonWhich == 1) {
                    position = Common.musicList.size() - 1;
                    mediaPlayer.reset();
                    objectAnimator.pause();
                    ivNeedle.startAnimation(rotateAnimation2);
                    prevAndnextplaying(Common.musicList.get(position).path);
                } else if (buttonWhich == 2) {
                    position++;
                    mediaPlayer.reset();
                    objectAnimator.pause();
                    ivNeedle.startAnimation(rotateAnimation2);
                    prevAndnextplaying(Common.musicList.get(position).path);
                }
            }else {
                if(buttonWhich ==1){
                    position--;
                    mediaPlayer.reset();
                    objectAnimator.pause();
                    ivNeedle.startAnimation(rotateAnimation2);
                    prevAndnextplaying(Common.musicList.get(position).path);

                }else if(buttonWhich ==2){
                    position++;
                    mediaPlayer.reset();
                    objectAnimator.pause();
                    ivNeedle.startAnimation(rotateAnimation2);
                    prevAndnextplaying(Common.musicList.get(position).path);
                }
            }
        } else if (playMode == 1)//单曲循环
        {
            //position不需要更改
            mediaPlayer.reset();
            objectAnimator.pause();
            ivNeedle.startAnimation(rotateAnimation2);
            prevAndnextplaying(Common.musicList.get(position).path);
        } else if (playMode == 2)//随机
        {
            position = (int) (Math.random() * Common.musicList.size());//随机播放
            mediaPlayer.reset();
            objectAnimator.pause();
            ivNeedle.startAnimation(rotateAnimation2);
            prevAndnextplaying(Common.musicList.get(position).path);
        }
    }

    //格式化数字
    private String formatTime(int length) {
        Date date = new Date(length);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");    //规定固定的格式
        String totaltime = simpleDateFormat.format(date);
        return totaltime;
    }

    //加入收藏/取消收藏
    public void setCollect(){
        String inputTitle = Common.musicList.get(position).title;
        String inputArtist = Common.musicList.get(position).artist;
        if (dbHelper.checkTitleArtist(inputTitle,inputArtist)){
            //数据存在，信息从数据库删除
            boolean ret;
            ret = dbHelper.deleteMusic(Common.musicList.get(position).title,
                    Common.musicList.get(position).artist);
            if (!ret){
                return;
            }else {
                Toast.makeText(this,"💔已取消喜欢",Toast.LENGTH_SHORT).show();
                ivLike.setImageResource(R.drawable.ic_play_btn_like);
            }
        }else {
            //数据不存在，信息保存到数据库
            boolean ret;
            ret = dbHelper.insertMusic(Common.musicList.get(position).title,
                    Common.musicList.get(position).artist,
                    Common.musicList.get(position).album,
                    Common.musicList.get(position).albumId,
                    Common.musicList.get(position).length,
                    Common.musicList.get(position).size,
                    Common.musicList.get(position).artist);
            if(!ret){
                return;
            }else {
                Toast.makeText(this,"❤已添加到我喜欢的音乐",Toast.LENGTH_SHORT).show();
                ivLike.setImageResource(R.drawable.ic_play_btn_like_press);
            }
        }
    }

    //绑定ID，设置监听
    private void bingID(){
        ivBack = findViewById(R.id.iv_play_back);
        ivModel = findViewById(R.id.iv_model);
        ivPlay = findViewById(R.id.iv_play);
        ivPrev = findViewById(R.id.iv_prev);
        ivNext = findViewById(R.id.iv_next);
        ivLike = findViewById(R.id.iv_like);
        ivBg = findViewById(R.id.music_bg_imgv);
        ivDisc = findViewById(R.id.music_disc_imagv);
        ivNeedle = findViewById(R.id.music_needle_imag);
        tvTitle = findViewById(R.id.tv_title);
        tvArtist = findViewById(R.id.tv_artist);
        tvCurrenttime = findViewById(R.id.tv_current_time);
        tvTotaltime = findViewById(R.id.tv_total_time);
        sbProgress = findViewById(R.id.sb_progress);
        ivBack.setOnClickListener(this);
        ivModel.setOnClickListener(this);
        ivPlay.setOnClickListener(this);
        ivPrev.setOnClickListener(this);
        ivNext.setOnClickListener(this);
        ivLike.setOnClickListener(this);

    }

    //onClick（）点击监听
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_play_back:
                this.finish();
                break;
            case R.id.iv_model:
                i++;
                if (i % 3 == 1) {
                    Toast.makeText(PlayActivity.this, "单曲循环", Toast.LENGTH_SHORT).show();
                    playMode = 1;
                    //loop.setText("单曲循环");
                    ivModel.setImageResource(R.drawable.ic_play_btn_one);
                }
                if (i % 3 == 2) {
                    Toast.makeText(PlayActivity.this, "随机播放", Toast.LENGTH_SHORT).show();
                    playMode = 2;
                    //loop.setText("随机播放");
                    ivModel.setImageResource(R.drawable.ic_play_btn_shuffle);
                }
                if (i % 3 == 0) {
                    Toast.makeText(PlayActivity.this, "列表循环", Toast.LENGTH_SHORT).show();
                    playMode = 0;
                    //loop.setText("列表循环");
                    ivModel.setImageResource(R.drawable.ic_play_btn_loop);
                }
                break;
            case R.id.iv_prev:
                buttonWhich = 1;
                setBtnMode();
                break;
            case R.id.iv_next:
                buttonWhich = 2;
                setBtnMode();
                break;
            case R.id.iv_play:
                if (mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    objectAnimator.pause();
                    ivNeedle.startAnimation(rotateAnimation2);
                    ivPlay.setImageResource(R.drawable.ic_play_btn_play);
                }else {
                    mediaPlayer.start();
                    objectAnimator.resume();
                    ivNeedle.startAnimation(rotateAnimation);
                    ivPlay.setImageResource(R.drawable.ic_play_btn_pause);
                }
                break;
            case R.id.iv_like:
                setCollect();
            default:
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        for (Music music : Common.musicList
        ) {
            music.isPlaying = false;
        }
        Common.musicList.get(position).isPlaying = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        i = 0;
        isStop = false;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }
    //创建一个类MusicThread实现Runnable接口，实现多线程
    class MusicThread implements Runnable {

        @Override
        public void run() {
            while (!isStop && Common.musicList.get(position) != null) {
                try {
                    //让线程睡眠1000毫秒
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //放送给Handler现在的运行到的时间，进行ui更新
                handler.sendEmptyMessage(mediaPlayer.getCurrentPosition());

            }
        }
    }
}
