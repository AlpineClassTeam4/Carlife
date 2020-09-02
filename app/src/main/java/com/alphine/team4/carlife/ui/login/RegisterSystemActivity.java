package com.alphine.team4.carlife.ui.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alphine.team4.carlife.R;
import com.alphine.team4.carlife.ui.login.toolsBeans.DBHelper;
import com.alphine.team4.carlife.ui.login.toolsBeans.LoginBean;

public class RegisterSystemActivity extends AppCompatActivity {

    EditText editText_RegEmail;
    EditText editText_RegPassword;
    EditText editText_RegConfirmPassword;
    EditText editText_RegNickname;
    EditText editText_RegAge;

    Button button_ConfirmReg;
    Button button_ExitReg;

    DBHelper dbHelper = new DBHelper(this);

    private static final String TAG = "pwd";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_system);

        editText_RegEmail = findViewById(R.id.editTextTextEmailAddress);
        editText_RegPassword = findViewById(R.id.editTextTextPassword);
        editText_RegConfirmPassword = findViewById(R.id.editTextTextPassword2);
        editText_RegNickname = findViewById(R.id.editTextTextPersonName);
        editText_RegAge = findViewById(R.id.editTextNumber);
        button_ConfirmReg = findViewById(R.id.button);
        button_ExitReg = findViewById(R.id.button2);

        button_ConfirmReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean b;
                LoginBean loginBean = new LoginBean();
                loginBean.email = editText_RegEmail.getText();
                loginBean.password = editText_RegPassword.getText().toString();
                loginBean.nickname = editText_RegNickname.getText().toString();
                loginBean.age = Integer.parseInt(editText_RegAge.getText().toString());

                String pwd1 = editText_RegConfirmPassword.getText().toString();
                String pwd2 = editText_RegPassword.getText().toString();

                Log.d(TAG, "pwd1: "+pwd1);
                Log.d(TAG, "pwd2: "+pwd2);
                Log.d(TAG, "onClick: "+loginBean.password);

                if (pwd1.equals(pwd2)) {
                    dbHelper.openDB();
                    b = dbHelper.insertUserinfo(loginBean);

                    if (!b){
                        Toast.makeText(RegisterSystemActivity.this,"数据库操作失败",Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(RegisterSystemActivity.this,"账号注册成功",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterSystemActivity.this,LoginSystemActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Toast.makeText(RegisterSystemActivity.this,"两次输入密码不一致",Toast.LENGTH_SHORT).show();
                }
            }
        });

        button_ExitReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterSystemActivity.this,LoginSystemActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
