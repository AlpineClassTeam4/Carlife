package com.alphine.team4.carlife.ui.dashboard.DBHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.util.Log;

import com.alphine.team4.carlife.ui.dashboard.utils.Common;
import com.alphine.team4.carlife.ui.dashboard.utils.Music;

public class DBHelper {
    private static final String TAG = "DBHelper";
    SQLiteDatabase db;
    Context context;

    public DBHelper(Context context){this.context = context;}
    public boolean open(){
        String path = context.getFilesDir()+"/"+"music_info.db";
        db = SQLiteDatabase.openOrCreateDatabase(path,null);

        //创建表格music_info
        String sql = "create table if not exists music_info"+
                "(title varchar(50) primary key, artist varchar(50),album varchar(50), " +
                "albumID int,length int,size bigint,path varchar(100))";

        try {
            db.execSQL(sql);
            return true;
        }catch (Exception e){
            Log.e(TAG, "open: error"+e.toString());
            e.printStackTrace();
            return false;
        }
    }

    //向表格music_info插入信息
    public boolean insertMusic(String title,String artist,String album,int albumID,int length,long size,String path) {
        if (db != null && db.isOpen()) {
            ContentValues values = new ContentValues();
            values.put("title", title);
            values.put("artist", artist);
            values.put("album", album);
            values.put("albumID", albumID);
            values.put("length", length);
            values.put("size", size);
            values.put("path", path);

            //进行数据插入
            long c = db.insert("music_info", null, values);
            if (c > 0) return true;
            else return false;
        } else {
            return false;
        }
    }

    //删除表格music_info信息
    public boolean deleteMusic(String title,String artist){
        if (db!=null && db.isOpen()){
            //进行数据删除
            long c = db.delete("music_info","title=? and artist=?",new String[]{title,artist});
            if (c > 0) return true;
            else return false;
        }else{
            return false;
        }
    }

    //查询表中数据
    public boolean checkTitleArtist(String title,String artist){
        Cursor c = db.query("music_info",//表名
                null,//要显示的列名，相当于select后面的内容
                "title=? and artist=?",//相当于where后面的内容
                new String[]{title,artist},//替代selection中问号内容
                null,null,null);
        if (c.getCount()>0) return true;
        else return false;
    }

    public void getCursor(){
         db.query("music_info", null,null,
                null,null,null,null);

    }

    //获取数据库音乐列表
    public void initListView(){
        Common.dbmusicList.clear();
        //获取游标
        Cursor cursor = db.query("music_info", null,null,
                null,null,null,null);
        //游标归零
        if(cursor.moveToFirst()){
            do {
                String title = cursor.getString(cursor.getColumnIndex("title"));            //获取歌名
                String artist = cursor.getString(cursor.getColumnIndex("artist"));         //获取歌唱者
                String album = cursor.getString(cursor.getColumnIndex("album"));           //获取专辑名
                int albumID = cursor.getInt(cursor.getColumnIndex("albumID"));            //获取专辑图片id
                int length = cursor.getInt(cursor.getColumnIndex("length"));             //获取时长
                long size = cursor.getLong(cursor.getColumnIndex("size"));                 //获取歌曲大小
                String path = cursor.getString(cursor.getColumnIndex("path"));             //获取歌曲地址
                //创建Music对象，并赋值
                Music music = new Music();
                music.length = length;
                music.title = title;
                music.artist = artist;
                music.album = album;
                music.size = size;
                music.path = path;
                //music.albumBip = getAlbumArt(albumID);
                //将music放入musicList集合中
                Common.dbmusicList.add(music);

            }while (cursor.moveToNext());
        }
        cursor.close();
    }
}
