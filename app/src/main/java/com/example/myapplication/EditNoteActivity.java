package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EditNoteActivity extends AppCompatActivity {

    private EditText editNoteContent;
    private TextView editNoteTime;
    private Button btnSaveNote;
    private DatabaseHelper dbHelper;
    private String noteId;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_edit_note);

        editNoteContent = findViewById(R.id.edit_note_content);
        editNoteTime = findViewById(R.id.edit_note_time);
        btnSaveNote = findViewById(R.id.btn_save_note);
        dbHelper = new DatabaseHelper(this);

        Intent intent = getIntent();
        noteId = intent.getStringExtra("note_id");
        username = intent.getStringExtra("username");

        String content = intent.getStringExtra("content");
        String time = intent.getStringExtra("time");

        editNoteContent.setText(content);
        editNoteTime.setText(time);

        btnSaveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newContent = editNoteContent.getText().toString().trim();
                String newTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

                if (TextUtils.isEmpty(newContent)) {
                    Toast.makeText(EditNoteActivity.this, "内容不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                int rowsUpdated = dbHelper.updateNote(noteId, newContent, newTime);
                if (rowsUpdated > 0) {
                    Toast.makeText(EditNoteActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditNoteActivity.this, "更新失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}