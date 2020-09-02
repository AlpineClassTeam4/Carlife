package com.alphine.team4.carlife.ui.music.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alphine.team4.carlife.R;
import com.alphine.team4.carlife.ui.music.activity.SongPlayActivity;
import com.bumptech.glide.Glide;
import com.lzx.starrysky.StarrySky;
import com.lzx.starrysky.provider.SongInfo;

import java.util.List;

public class WyyMusicAdapter extends RecyclerView.Adapter<WyyMusicAdapter.MyViewHolder> {

    private Context context;
    private List<SongInfo> songList;
    private OnPlayStateChagedListener chagedListener;

    public WyyMusicAdapter(Context context, List<SongInfo> songList) {
        this.context = context;
        this.songList = songList;
    }

    public void setChagedListener(OnPlayStateChagedListener chagedListener) {
        this.chagedListener = chagedListener;
    }

    public void setSongList(List<SongInfo> songList) {
        this.songList = songList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_song, parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bindData(position);
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    private static final String TAG = "WyyMusicAdapter";
    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView ivCover,ivPlay;
        TextView tvTitle,tvArtist,tvLength;
        SongInfo info;

        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            ivCover = itemView.findViewById(R.id.song_cover_imgv);
            ivPlay = itemView.findViewById(R.id.song_play_imgv);
            tvTitle = itemView.findViewById(R.id.song_title_tv);
            tvArtist = itemView.findViewById(R.id.song_artist_tv);
            tvLength = itemView.findViewById(R.id.song_length_tv);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, SongPlayActivity.class);
                    i.putExtra("songid",info);
                    context.startActivity(i);
                }
            });
            ivPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(StarrySky.with().isCurrMusicIsPlaying(info.getSongId())){
                        StarrySky.with().pauseMusic();
                        ivPlay.setImageResource(R.drawable.ic_play_bar_btn_play);
                        if(chagedListener!=null)chagedListener.onChaged(false);
                    }else {
                        StarrySky.with().playMusicById(info.getSongId());
                        ivPlay.setImageResource(R.drawable.ic_play_bar_btn_pause);
                        if(chagedListener!=null)chagedListener.onChaged(true);
                    }
                    Log.d(TAG, "btnPlayStart:onClick: song id = "+info.getSongId());
                }
            });
        }

        public void bindData(int position){
            info = songList.get(position);
            tvTitle.setText(info.getSongName());
            tvArtist.setText(info.getArtist());
            tvLength.setText(String.valueOf(info.getDuration()));
            Glide.with(context).load(info.getSongCover()).into(ivCover);
            if(StarrySky.with().isCurrMusicIsPlaying(info.getSongId()))
                ivPlay.setImageResource(R.drawable.ic_play_bar_btn_pause);
            else
                ivPlay.setImageResource(R.drawable.ic_play_bar_btn_play);
        }
    }

    public interface OnPlayStateChagedListener{
        void onChaged(boolean isplaying);
    }
}
