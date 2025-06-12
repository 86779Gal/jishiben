package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 初始化视图
        etUsername = findViewById(R.id.username);
        etPassword = findViewById(R.id.password);
        btnLogin = findViewById(R.id.login);
        tvRegister = findViewById(R.id.register);

        // 初始化数据库帮助类
        dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // 设置注册按钮点击事件
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 创建隐式 Intent，指定动作
                Intent intent = new Intent("com.example.notepad.ACTION_REGISTER");

                // 检查是否有应用能处理该 Intent
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, "无法找到注册界面", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // 设置登录按钮点击事件
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // 验证输入
        if (TextUtils.isEmpty(username)) {
            etUsername.setError("请输入用户名");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("请输入密码");
            return;
        }

        // 检查用户是否存在并验证密码
        if (validateUser(username, password)) {
            // 登录成功
            Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();

            // 保存登录状态
            saveLoginStatus(true, username);

            // 跳转到主界面
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            // 登录失败
            Toast.makeText(this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateUser(String username, String password) {
        // 通过getPasswordByUsername方法获取存储的密码
        String storedPassword = dbHelper.getPasswordByUsername(username);
        if (storedPassword == null) {
            return false;
        }
        return password.equals(storedPassword);
    }

    private void saveLoginStatus(boolean isLoggedIn, String username) {
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("is_logged_in", isLoggedIn);
        editor.putString("username", username);
        editor.apply();
    }
}