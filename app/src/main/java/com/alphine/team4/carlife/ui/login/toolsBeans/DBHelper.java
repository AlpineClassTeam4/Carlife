package com.alphine.team4.carlife.ui.login.toolsBeans;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBHelper {
    private static final String TAG = "DBHelper";
    public SQLiteDatabase db;
    Context context;

    public DBHelper(Context c) {
        context = c;
    }

    public boolean openDB(){
        String path = context.getFilesDir() + "/"+"user_info.db";
        db = SQLiteDatabase.openOrCreateDatabase(path,null);

        //创建表格
        String sql = "create table if not exists user_info" +
                "(user_email varchar(50) primary key," +
                "user_password varchar(50)," +
                "user_nickname varchar(20)," +
                "user_age int)";
        try {
            db.execSQL(sql);
            return true;
        }catch (Exception e){
            Log.e(TAG, "open: error"+e.toString());
            e.printStackTrace();
            return false;
        }
    }

    public boolean insertUserinfo(LoginBean loginBean){
        if (db != null && db.isOpen()){
            ContentValues values = new ContentValues();
            //准备插入的数据
            values.put("user_email", String.valueOf(loginBean.email));
            values.put("user_password",loginBean.password);
            values.put("user_nickname",loginBean.nickname);
            values.put("user_age",loginBean.age);

            //进行数据库插入
            long c = db.insert("user_info",null,values);


            if(c > 0)return true;
            else return false;
        }else {
            return false;
        }
    }

    public boolean checkNamePassword(String email, String password){
        Cursor c = db.query("user_info",//表名
                null,//要显示的内容
                "user_email=? and user_password=?",
                new String[]{email,password},
                null,
                null,
                null);
        if(c.getCount() > 0)return true;
        else return false;
    }

    public boolean insertUserImageid(int imgid){
        if (db != null && db.isOpen()){
            ContentValues values = new ContentValues();
            //准备插入的数据
            values.put("user_imgid", String.valueOf(imgid));
            //进行数据库插入
            long c = db.insert("user_info",null,values);

            if(c > 0)return true;
            else return false;
        }else {
            return false;
        }
    }
}
