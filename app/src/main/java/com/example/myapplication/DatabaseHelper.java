package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // 数据库信息
    private static final String DATABASE_NAME = "notepad.db";
    private static final int DATABASE_VERSION = 2;

    // 用户表信息
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "_id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_PHONE = "phone";

    //日记表信息
    // 日记表信息
    public static final String TABLE_NOTES = "notes";
    public static final String COLUMN_NOTE_ID = "_id";
    public static final String COLUMN_NOTE_USERNAME = "username"; // 关联用户
    public static final String COLUMN_NOTE_CONTENT = "content";
    public static final String COLUMN_NOTE_TIME = "time";

    // 创建用户表的SQL语句
    private static final String CREATE_TABLE_USERS =
            "CREATE TABLE " + TABLE_USERS + " (" +
                    COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_USERNAME + " TEXT UNIQUE NOT NULL, " +
                    COLUMN_PASSWORD + " TEXT NOT NULL, " +
                    COLUMN_PHONE + " TEXT UNIQUE);";

    // 创建日记表的SQL语句
    private static final String CREATE_TABLE_NOTES =
            "CREATE TABLE " + TABLE_NOTES + " (" +
                    COLUMN_NOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NOTE_USERNAME + " TEXT NOT NULL, " +
                    COLUMN_NOTE_CONTENT + " TEXT NOT NULL, " +
                    COLUMN_NOTE_TIME + " TEXT NOT NULL, " +
                    "FOREIGN KEY(" + COLUMN_NOTE_USERNAME + ") REFERENCES " +
                    TABLE_USERS + "(" + COLUMN_USERNAME + ") ON DELETE CASCADE);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建用户表和日记表
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_NOTES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 升级数据库时删除旧表并重创建
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // 添加用户
    public long addUser(String username, String password, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_PHONE, phone);

        long id = db.insert(TABLE_USERS, null, values);
        db.close();
        return id;
    }

    // 检查用户名是否存在
    public boolean checkUsernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {COLUMN_USER_ID};
        String selection = COLUMN_USERNAME + " =?";
        String[] selectionArgs = {username};

        Cursor cursor = db.query(
                TABLE_USERS,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        db.close();

        return exists;
    }

    //登陆获取用户名密码
    @SuppressLint("Range")
    public String getPasswordByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {COLUMN_PASSWORD};
        String selection = COLUMN_USERNAME + " = ?";
        String[] selectionArgs = {username};

        Cursor cursor = db.query(
                TABLE_USERS,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        String password = null;
        if (cursor.moveToFirst()) {
            password = cursor.getString(cursor.getColumnIndex(COLUMN_PASSWORD));
        }

        cursor.close();
        db.close();
        return password;
    }

    //  addNote 方法，添加日记
    public long addNote(String username, String content, String time) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTE_USERNAME, username);
        values.put(COLUMN_NOTE_CONTENT, content);
        values.put(COLUMN_NOTE_TIME, time);

        long id = db.insert(TABLE_NOTES, null, values);
        db.close();
        return id;
    }

    // 获取指定用户的所有记录
    @SuppressLint("Range")
    public Cursor getAllNotesByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {COLUMN_NOTE_ID, COLUMN_NOTE_CONTENT, COLUMN_NOTE_TIME};
        String selection = COLUMN_NOTE_USERNAME + " =?";
        String[] selectionArgs = {username};
        String sortOrder = COLUMN_NOTE_TIME + " DESC";

        return db.query(TABLE_NOTES, projection, selection, selectionArgs, null, null, sortOrder);
    }

    // 根据用户名和搜索文本搜索日记
    public Cursor searchNotesByUsernameAndText(String username, String searchText) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_NOTE_USERNAME + " =? AND " + COLUMN_NOTE_CONTENT + " LIKE?";
        String[] selectionArgs = {username, "%" + searchText + "%"};
        String[] projection = {COLUMN_NOTE_ID, COLUMN_NOTE_CONTENT, COLUMN_NOTE_TIME};
        String sortOrder = COLUMN_NOTE_TIME + " DESC";

        return db.query(TABLE_NOTES, projection, selection, selectionArgs, null, null, sortOrder);
    }

    // 更新日记
    public int updateNote(String noteId, String newContent, String newTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTE_CONTENT, newContent);
        values.put(COLUMN_NOTE_TIME, newTime);

        String selection = COLUMN_NOTE_ID + " =?";
        String[] selectionArgs = {noteId};

        int rowsUpdated = db.update(TABLE_NOTES, values, selection, selectionArgs);
        db.close();
        return rowsUpdated;
    }

    // 在DatabaseHelper类中添加删除方法
    public int deleteNote(String noteId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = COLUMN_NOTE_ID + " = ?";
        String[] selectionArgs = {noteId};
        int rowsDeleted = db.delete(TABLE_NOTES, selection, selectionArgs);
        db.close();
        return rowsDeleted;
    }
}