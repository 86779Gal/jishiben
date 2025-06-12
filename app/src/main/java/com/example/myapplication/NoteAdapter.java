package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class NoteAdapter extends BaseAdapter {
    private Context context;
    private Cursor cursor;
    private String currentUsername; // 当前用户名，用于权限校验

    public NoteAdapter(Context context, Cursor cursor, String username) {
        this.context = context;
        this.cursor = cursor;
        this.currentUsername = username;
    }

    @Override
    public int getCount() {
        return cursor.getCount();
    }

    @SuppressLint("Range")
    @Override
    public Object getItem(int position) {
        if (cursor.moveToPosition(position)) {
            return cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NOTE_CONTENT));
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_list, parent, false);
            holder = new ViewHolder();
            holder.contentTextView = convertView.findViewById(R.id.text);
            holder.timeTextView = convertView.findViewById(R.id.time);
            holder.deleteButton = convertView.findViewById(R.id.shanchu);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (cursor.moveToPosition(position)) {
            @SuppressLint("Range") String content = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NOTE_CONTENT));
            @SuppressLint("Range") String time = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NOTE_TIME));
            @SuppressLint("Range") final String noteId = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NOTE_ID));

            holder.contentTextView.setText(content);
            holder.timeTextView.setText(time);

            // 删除按钮点击事件（带确认对话框）
            holder.deleteButton.setOnClickListener(v -> showDeleteConfirmation(noteId));
        }

        return convertView;
    }

    // 删除确认对话框
    private void showDeleteConfirmation(String noteId) {
        new AlertDialog.Builder(context)
                .setTitle("删除记录")
                .setMessage("确认要删除这条记录吗？")
                .setPositiveButton("删除", (dialog, which) -> performDelete(noteId))
                .setNegativeButton("取消", null)
                .show();
    }

    // 执行删除操作
    private void performDelete(String noteId) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        dbHelper.deleteNote(noteId);
        dbHelper.close();

        // 刷新列表数据
        Cursor newCursor = dbHelper.getAllNotesByUsername(currentUsername);
        changeCursor(newCursor);
    }

    public void changeCursor(Cursor newCursor) {
        if (cursor != null) {
            cursor.close();
        }
        cursor = newCursor;
        notifyDataSetChanged();
    }

    public Cursor getCursor() {
        return cursor;
    }

    // ViewHolder模式优化性能
    static class ViewHolder {
        TextView contentTextView;
        TextView timeTextView;
        Button deleteButton;
    }
}