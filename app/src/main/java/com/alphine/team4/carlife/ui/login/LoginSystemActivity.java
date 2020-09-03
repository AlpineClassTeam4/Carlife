package com.alphine.team4.carlife.ui.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alphine.team4.carlife.R;
import com.alphine.team4.carlife.ui.home.HomeFragment;
import com.alphine.team4.carlife.ui.login.toolsBeans.DBHelper;

public class LoginSystemActivity extends AppCompatActivity {

    private static final String TAG = "MainDate";
    EditText editText_email;
    EditText editText_password;

    Button button_login;
    Button button_register;

    String nickName;

    DBHelper dbHelper = new DBHelper(this);
    SharedPreferences preferences;

    String temporary_user_email;
    String temporary_user_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_system);

        editText_email = findViewById(R.id.editText_SignEmail);
        editText_password = findViewById(R.id.editText_Password);
        button_login = findViewById(R.id.button_login);
        button_register = findViewById(R.id.button_register);

        preferences = getSharedPreferences("user_date",MODE_PRIVATE);

        temporary_user_email = preferences.getString("user_email",null);
        temporary_user_password = preferences.getString("user_password",null);
        dbHelper.openDB();

        Intent intent = getIntent();

        String time_email;
        String time_password;

        time_email = intent.getStringExtra("logout_email");
        time_password = intent.getStringExtra("logout_password");

        SharedPreferences preferences1 = getSharedPreferences("time_user",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences1.edit();//获取编辑器
        editor.putString("time_user_email",time_email);//保存用户名
        editor.putString("time_user_password",time_password);//保存密码
        editor.commit();

        editText_email.setText(preferences1.getString("time_user_email",""));
        editText_password.setText(preferences1.getString("time_user_password",""));

        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input_email = editText_email.getText().toString();
                String input_password = editText_password.getText().toString();

                dbHelper.openDB();
                if (dbHelper.checkNamePassword(input_email,input_password)){
                    Intent i = new Intent(LoginSystemActivity.this,com.alphine.team4.carlife.MainActivity.class);
                    i.putExtra("email",input_email);
                    preferences = getSharedPreferences("user_date",MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();//获取编辑器
                    editor.putString("user_email",input_email);//保存用户名
                    editor.putString("user_password",input_password);//保存密码
                    editor.commit();

                    Cursor cursor = dbHelper.db.query("user_info",null,"user_email=?",
                            new String[]{input_email},
                            null,null,null,null);
                    while (cursor.moveToNext()){
                        nickName = cursor.getString(cursor.getColumnIndex("user_nickname"));
                    }
                    Log.d(TAG, "onCreate: "+nickName);
                    startActivity(i);
                    finish();
                    Toast.makeText(LoginSystemActivity.this,nickName+" 已登录",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(LoginSystemActivity.this,"用户名或密码错误",Toast.LENGTH_SHORT).show();
                }
            }
        });

        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginSystemActivity.this,RegisterSystemActivity.class);
                startActivity(i);
                finish();
            }
        });

        if(temporary_user_email != null && temporary_user_password != null) {
            if (dbHelper.checkNamePassword(temporary_user_email,temporary_user_password)){
//                Intent intent = new Intent(MainActivity.this,HomeActivity.class);
//                intent.putExtra("email",temporary_user_email);
                Cursor cursor = dbHelper.db.query("user_info",null,"user_email=?",
                        new String[]{temporary_user_email},
                        null,null,null,null);
                while (cursor.moveToNext()){
                    nickName = cursor.getString(cursor.getColumnIndex("user_nickname"));
                }
//                startActivity(intent);
                editText_email.setText(temporary_user_email);
                editText_password.setText(temporary_user_password);
                Toast.makeText(LoginSystemActivity.this,nickName+" 已自动登录",Toast.LENGTH_SHORT).show();
                button_login.performClick();
            }
        }
    }
}