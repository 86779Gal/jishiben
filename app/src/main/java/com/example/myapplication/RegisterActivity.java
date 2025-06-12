package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    private ImageView fanhui;
    private EditText username, password, passwordagain, phone;
    private Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fanhui = findViewById(R.id.fanhui);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        passwordagain = findViewById(R.id.passwordagain);
        phone = findViewById(R.id.phone);
        register = findViewById(R.id.register);

        fanhui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleRegistration();
            }
        });
    }

    private void handleRegistration() {
        String usernameText = username.getText().toString().trim();
        String passwordText = password.getText().toString().trim();
        String confirmPassword = passwordagain.getText().toString().trim();
        String phoneText = phone.getText().toString().trim();

        // 验证输入
        if (TextUtils.isEmpty(usernameText)) {
            username.setError("用户名不能为空");
            return;
        }

        if (TextUtils.isEmpty(passwordText)) {
            password.setError("密码不能为空");
            return;
        }

        if (!passwordText.equals(confirmPassword)) {
            passwordagain.setError("两次输入的密码不匹配");
            return;
        }

        if (!isValidPhone(phoneText)) {
            phone.setError("请输入有效的手机号");
            return;
        }

        // 检查用户名是否已存在
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        if (dbHelper.checkUsernameExists(usernameText)) {
            username.setError("用户名已存在");
            return;
        }

        // 注册用户
        long userId = dbHelper.addUser(usernameText, passwordText, phoneText);
        if (userId != -1) {
            Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "注册失败", Toast.LENGTH_SHORT).show();
        }
    }

    // 简单的手机号验证
    private boolean isValidPhone(String phone) {
        return!TextUtils.isEmpty(phone) && phone.length() == 11;
    }
}