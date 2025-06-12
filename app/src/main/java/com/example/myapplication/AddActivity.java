package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AddActivity extends AppCompatActivity {

    private ImageView fanhui;
    private EditText noteEditText;
    private Button finishButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        fanhui = findViewById(R.id.fanhui);
        noteEditText = findViewById(R.id.note);
        finishButton = findViewById(R.id.finish);

        fanhui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNoteToDatabase();
            }
        });
    }

    private void addNoteToDatabase() {
        String content = noteEditText.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "请输入要记录的内容", Toast.LENGTH_SHORT).show();
            return;
        }

        // 获取当前登录的用户名
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String username = preferences.getString("username", "");
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "未获取到用户名", Toast.LENGTH_SHORT).show();
            return;
        }

        // 获取当前时间
        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        long noteId = dbHelper.addNote(username, content, currentTime);
        if (noteId != -1) {
            Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
            noteEditText.setText(""); // 清空编辑框
        } else {
            Toast.makeText(this, "添加失败", Toast.LENGTH_SHORT).show();
        }
    }
}