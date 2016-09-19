package com.example.myaccount;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class categoryDBHelper extends SQLiteOpenHelper {
    private final static int DATABASE_VERSION = 1;
    private final static String DATABASE_NAME = "myAccount.db";

    public categoryDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql1 = "CREATE TABLE [m_category] ("
                + "[id] INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "[category_name] VARCHAR(20) NOT NULL ON CONFLICT FAIL,"
                + "[budget_money] VARCHAR(20) NOT NULL ON CONFLICT FAIL,"
                + "[acturl_money] VARCHAR(20) NOT NULL ON CONFLICT FAIL,"
                + "[last_money] VARCHAR(20) NOT NULL ON CONFLICT FAIL)";
        db.execSQL(sql1);
        String sql2 =  "CREATE TABLE [m_bill] ("
                + "[id] INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "[bill_choice] INTEGER(2) NOT NULL ON CONFLICT FAIL,"
                + "[bill_money] VARCHAR(20) NOT NULL ON CONFLICT FAIL,"
                + "[bill_category] VARCHAR(20) NOT NULL ON CONFLICT FAIL,"
                + "[bill_date] VARCHAR(20) NOT NULL ON CONFLICT FAIL,"
                + "[bill_remark] VARCHAR(200) NOT NULL ON CONFLICT FAIL,"
                + "[bill_name] VARCHAR(200) NOT NULL ON CONFLICT FAIL)";
        db.execSQL(sql2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
    }

    public void addItem(String item){
        String sql = "insert into m_category (category_name, budget_money, acturl_money, last_money) values(?,?,?,?)";
        Object[] args = new Object[]{item,"0","0","0"};
        execSQL(sql, args);
    }

    public void editItem(int id, String category_name){
        String sql = "update m_category set category_name = '" + category_name + "' where id = '" + id + "'";
        execSQL(sql);
    }

    public void deleteItem(String item){
        String sql = "delete from m_category where category_name = '" + item + "'";
        execSQL(sql);
    }

    public boolean isExist_Category(String category){
        String sql = "select * from m_category where category_name = '" + category + "'";
        Cursor c = query(sql, null);
        if(c.getCount() == 0)
            return false;
        else return true;
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

    public Cursor queryType()
    {
        SQLiteDatabase db = getWritableDatabase();
        String strSql = "select id,category_name from m_category order by id";
        Cursor c = db.rawQuery(strSql, null);
        return c;
    }
}
