package com.alphine.team4.carlife.ui.music.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alphine.team4.carlife.R;
import com.alphine.team4.carlife.ui.music.DBHelper.DBHelper;
import com.alphine.team4.carlife.ui.music.adapter.DBMusicAdapter;
import com.alphine.team4.carlife.ui.music.utils.Common;
import com.alphine.team4.carlife.ui.music.utils.Music;

import java.util.List;


public class CollectActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvTitleCol;
    ImageView ivBackCol;
    ListView listView;
    DBHelper dbHelper;
    private DBMusicAdapter adapter;
    private List<Music> dbmusicList;
    private int musicWhich = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);

        bindID();
        dbHelper.initListView();
        adapter = new DBMusicAdapter(this, dbmusicList);        //创建MusicAdapter的对象，实现自定义适配器的创建
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (Music m : Common.dbmusicList
                ) {
                    m.isPlaying = false;
                }
                Common.dbmusicList.get(position).isPlaying = true;
                //更新界面
                adapter.notifyDataSetChanged();
                //intent实现页面的跳转，获取当前的activity， MusicActivity.class将要调转的activity
                Intent intent = new Intent(CollectActivity.this, PlayActivity.class);
                //使用putExtra（）传值
                intent.putExtra("position", position);
                intent.putExtra("musicWhich",musicWhich);
                startActivity(intent);
            }
        });
    }

    public void bindID(){
        tvTitleCol = findViewById(R.id.col_title_tv);
        ivBackCol = findViewById(R.id.col_back_imgv);
        listView = findViewById(R.id.col_like_lv);
        ivBackCol.setOnClickListener(this);
        dbHelper = new DBHelper(this);
        dbHelper.open();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.col_back_imgv:
                this.finish();
                break;
        }
    }
}
