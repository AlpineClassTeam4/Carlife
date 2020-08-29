package com.alphine.team4.carlife.ui.dashboard.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alphine.team4.carlife.R;
import com.alphine.team4.carlife.ui.dashboard.utils.Common;
import com.alphine.team4.carlife.ui.dashboard.utils.Music;

import java.util.List;

public class DBMusicAdapter extends BaseAdapter {

    //定义两个属性，用List集合存放Music类
    private Context context;
    private List<Music> dbmusicList;

    //创建MusicAdapter的构造方法，在LogicFragment需要调用MusicAdapter的构造方法来创建适配器
    public DBMusicAdapter(Context context, List<Music> dbmusicList) {
        this.context = context;
        this.dbmusicList = dbmusicList;
    }
    @Override
    public int getCount() { return Common.dbmusicList.size();}

    @Override
    public Object getItem(int position) { return null;}

    @Override
    public long getItemId(int position) { return 0;}

    /**
     * 在getView（）方法中是实现对模板的绑定，赋值
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //声明View和ViewHolder的对象
        View view = null;
        DBMusicAdapter.ViewHolder viewHolder = null;
        //缓存原理，程序运行到这里判断convertView是否为空
        if (convertView == null) {
            //绑定行布局文件，就是绑定我们需要适配的模板
            view = LayoutInflater.from(context).inflate(R.layout.item_music_listview, null);
            //实例化ViewHolder
            viewHolder = new DBMusicAdapter.ViewHolder();
            viewHolder.tvTitle = view.findViewById(R.id.tv_title);
            viewHolder.tvArtist = view.findViewById(R.id.tv_artist);
            viewHolder.ivCover = view.findViewById(R.id.iv_cover);
            //viewHolder.isPlayingView = view.findViewById(R.id.musicitem_playing_v);
            view.setTag(viewHolder);

        } else {
            view = convertView;
            viewHolder = (DBMusicAdapter.ViewHolder) view.getTag();
        }
        //赋值 准确的是绑定赋值的中介
        viewHolder.tvTitle.setText(Common.dbmusicList.get(position).title);
        viewHolder.tvArtist.setText(Common.dbmusicList.get(position).artist);
        if (Common.dbmusicList.get(position).albumBip != null){
            viewHolder.ivCover.setImageBitmap(Common.dbmusicList.get(position).albumBip);
        }else {
            viewHolder.ivCover.setImageResource(R.drawable.play_page_default_cover);
        }
        //viewHolder.ivCover.setImageBitmap(Common.musicList.get(position).albumBip);

//        if (Common.musicList.get(position).isPlaying) {
//            viewHolder.isPlayingView.setVisibility(View.VISIBLE);
//        } else {
//            viewHolder.isPlayingView.setVisibility(View.INVISIBLE);
//        }
        return view;
    }

    //创建一个类ViewHolder，用来存放music_item.xml中的控件
    class ViewHolder {
        TextView tvTitle;
        TextView tvArtist;
        ImageView ivCover;
        View isPlayingView;
    }
}
