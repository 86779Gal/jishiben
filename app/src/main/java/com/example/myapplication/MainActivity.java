package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private ImageView add;
    private ListView listView;
    private NoteAdapter adapter;
    private DatabaseHelper dbHelper;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        add = findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                startActivity(intent);
            }
        });

        listView = findViewById(R.id.listview);
        dbHelper = new DatabaseHelper(this);

        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        username = preferences.getString("username", "");

        // 初始加载数据
        loadNotes();

        EditText searchBox = findViewById(R.id.search_box);
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchText = s.toString();
                Cursor searchCursor = dbHelper.searchNotesByUsernameAndText(username, searchText);
                adapter.changeCursor(searchCursor);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Cursor cursor = adapter.getCursor();
            if (cursor.moveToPosition(position)) {
                @SuppressLint("Range") String noteId = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NOTE_ID));
                @SuppressLint("Range") String content = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NOTE_CONTENT));
                @SuppressLint("Range") String time = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NOTE_TIME));

                Intent intent = new Intent(MainActivity.this, EditNoteActivity.class);
                intent.putExtra("note_id", noteId);
                intent.putExtra("username", username);
                intent.putExtra("content", content);
                intent.putExtra("time", time);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 当Activity恢复可见时，重新加载数据
        loadNotes();
    }

    // 在MainActivity的loadNotes方法中传递用户名给适配器
    private void loadNotes() {
        Cursor cursor = dbHelper.getAllNotesByUsername(username);
        if (adapter == null) {
            adapter = new NoteAdapter(this, cursor, username); // 传递用户名
            listView.setAdapter(adapter);
        } else {
            adapter.changeCursor(cursor);
        }
    }


}