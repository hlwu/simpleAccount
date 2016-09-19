package com.example.myaccount;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class budgetDBHelper extends SQLiteOpenHelper {

    private final static int DATABASE_VERSION = 1;
    private final static String DATABASE_NAME = "myAccount.db";

    public budgetDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE [m_budget] ("
                + "[id] INTEGER(3) PRIMARY KEY IDENTITY(0,1),"
                + "[budget_name] VARCHAR(20) NOT NULL ON CONFLICT FAIL,"
                + "[money] VARCHAR(20) NOT NULL ON CONFLICT FAIL)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
    }

    public void addItem(String item, String money){
        Log.d("wtf", "bbbbbbbbbbbbbbbbbbbbbbbb");
        String sql = "insert into m_budget (budget_name, money) values(?,?)";
        Object[] args = new Object[]{item, money};
        execSQL(sql, args);
    }

    public void updatebudget(int id, String money){
        String sql = "update m_budget set money = " + money 
                + "where id = " + id;
        execSQL(sql);
    }

    public void execSQL(String sql){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sql);
    }

    public void execSQL(String sql, Object[] args){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sql, args);
    }

    public Cursor query(String sql, String[] args){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, args);
        return cursor;
    }

}
