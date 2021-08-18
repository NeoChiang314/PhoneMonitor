package com.example.phonemonitor;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {
    private static SQLiteOpenHelper instance;
    public static synchronized SQLiteOpenHelper getInstance(Context context){
        if (instance == null) {
            instance = new MySQLiteOpenHelper(context, "DataDB.db", null, 1);
        }
        return instance;
    }

    public MySQLiteOpenHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table data(_id integer primary key autoincrement, time text, longitude text, latitude text, RSRP text, RSRQ text, PCI text)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
