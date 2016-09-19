package com.example.myaccount;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class billDBHelper extends SQLiteOpenHelper {
    private final static int DATABASE_VERSION = 1;
    private final static String DATABASE_NAME = "myAccount.db";

    public billDBHelper(Context context){
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

    public void addItem(int choice, String money, String category, String date, String remark, String name){
        String sql = "insert into m_bill (bill_choice, bill_money, bill_category, bill_date, bill_remark, bill_name) values(?,?,?,?,?,?)";
        Object[] args = new Object[]{choice,money,category,date,remark,name};
        execSQL(sql, args);
    }

    public Cursor queryByCategory(String category){
        String sql = "select * from m_bill where bill_category = '" + category + "'  order by bill_date desc";
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(sql, null);
    }

    public Cursor queryByDate(String d1, String d2){
        String sql = "select * from m_bill where bill_date >= " + d1 + " and bill_date <= " + d2 + "  order by bill_date desc";
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(sql, null);
    }

    public Cursor queryByDateAndCategory(String d1, String d2, String category){
        String sql = "select * from m_bill where bill_date >= " + d1 + " and bill_date <= " + d2 + " and bill_category = '" + category + "'  order by bill_date desc";
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(sql, null);
    }

    public Cursor queryAll(){
        String sql = "select * from m_bill order by bill_date desc";
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(sql, null);
    }

    public Cursor queryIncomeAll(){
        String sql = "select * from m_bill where bill_choice = 1 order by bill_date desc";
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(sql, null);
    }

    public Cursor queryIncomeBycategory(String category){
        String sql = "select * from m_bill where bill_choice = 1 and bill_category = '" + category + "'  order by bill_date desc";
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(sql, null);
    }

    public Cursor queryIncomeByDate(String d1, String d2){
        String sql = "select * from m_bill where bill_choice = 1 and bill_date >= " + d1
                + " and bill_date <= " + d2 + "  order by bill_date desc";
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(sql, null);
    }

    public Cursor queryIncomeByDateAndCategory(String d1, String d2, String category){
        String sql = "select * from m_bill where bill_choice = 1 and ( bill_date >= " + d1
                + " and bill_date <= " + d2 + " ) and bill_category = '" + category + "'  order by bill_date desc";
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(sql, null);
    }

    public Cursor queryPayAll(){
        String sql = "select * from m_bill where bill_choice = 2  order by bill_date desc";
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(sql, null);
    }

    public Cursor queryPayBycategory(String category){
        String sql = "select * from m_bill where bill_choice = 2 and bill_category = '" + category + "'  order by bill_date desc";
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(sql, null);
    }

    public Cursor queryPayByDate(String d1, String d2){
        String sql = "select * from m_bill where bill_choice = 2 and bill_date >= " + d1
                + " and bill_date <= " + d2 + "  order by bill_date desc";
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(sql, null);
    }

    public Cursor queryPayByDateAndCategory(String d1, String d2, String category){
        String sql = "select * from m_bill where bill_choice = 2 and ( bill_date >= " + d1
                + " and bill_date <= " + d2 + " ) and bill_category = '" + category + "'  order by bill_date desc";
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(sql, null);
    }

    public void deleteByid(int id){
        String sql = "delete from m_bill where id = '" + id +"'";
        execSQL(sql);
    }

    public boolean isExist_searchByCategory(String category){
        String sql = "select * from m_bill where bill_category = '" + category + "'";
        Cursor c = query(sql, null);
        if(c.getCount() == 0)
            return false;
        else return true;
    }

    public void updateCategoryById(int id, String category){
        String sql = "update m_bill set bill_category = '" + category + "' where id = '" + id +"'";
        execSQL(sql);
    }

    public void modify(int choice, String money, String category, String date, String remark, int id, String name){
        String sql = "update m_bill set bill_choice = '" + choice
                + "', bill_money = '" + money + "', bill_category = '" + category
                + "', bill_date = '" + date + "', bill_remark = '" + remark + "', bill_name = '" 
                + name + "' where id = '" + id + "'";
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
