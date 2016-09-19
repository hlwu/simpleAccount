package com.example.myaccount;

import java.math.BigDecimal;
import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener{
    private billDBHelper billDB;
    private TextView tv_income_today, tv_income_week, tv_income_month, tv_pay_today, tv_pay_week, tv_pay_month, tv_today_day, tv1, tv2, tv3;
    private LinearLayout ll1, ll2, ll3;
    private CalendarUtil cu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button b1 = (Button) findViewById(R.id.btnnewpay);
        b1.setOnClickListener(this);
        Button b2 = (Button) findViewById(R.id.btnsearch);
        b2.setOnClickListener(this);
        Button b3 = (Button) findViewById(R.id.btnset);
        b3.setOnClickListener(this);
        billDB = new billDBHelper(this);
        tv1 = (TextView) findViewById(R.id.tv_show_today);
        tv2 = (TextView) findViewById(R.id.tv_show_week);
        tv3 = (TextView) findViewById(R.id.tv_show_month);
        tv_income_today = (TextView) findViewById(R.id.tv_show_day_income);
        tv_pay_today = (TextView) findViewById(R.id.tv_show_day_pay);
        tv_income_week = (TextView) findViewById(R.id.tv_show_week_income);
        tv_pay_week = (TextView) findViewById(R.id.tv_show_week_pay);
        tv_income_month = (TextView) findViewById(R.id.tv_show_month_income);
        tv_pay_month = (TextView) findViewById(R.id.tv_show_month_pay);
        tv_today_day = (TextView) findViewById(R.id.tv_show_today_day);
        ll1 = (LinearLayout) findViewById(R.id.ll_1);
        ll2 = (LinearLayout) findViewById(R.id.ll_2);
        ll3 = (LinearLayout) findViewById(R.id.ll_3);
        ll1.setOnClickListener(this);
        ll2.setOnClickListener(this);
        ll3.setOnClickListener(this);
        
        setTextViewText();
        cu = new CalendarUtil();
        tv1.setText(cu.getToday());
        tv2.setText(cu.getMondayOFWeek()+"至"+cu.getCurrentWeekday());
        tv3.setText(cu.getFirstDayOfMonth()+"至"+cu.getDefaultDay());
        setDayIncomeAndPayWhenAllCategoryAndSelectedDate(cu.getToday().replace("-", ""), cu.getToday().replace("-", ""));
        setWeekIncomeAndPayWhenAllCategoryAndSelectedDate(cu.getMondayOFWeek().replace("-", ""), cu.getCurrentWeekday().replace("-", ""));
        setMonthIncomeAndPayWhenAllCategoryAndSelectedDate(cu.getFirstDayOfMonth().replace("-", ""), cu.getDefaultDay().replace("-", ""));
        tv_today_day.setText(cu.getTodayDay()); 
//        Log.d("aaaaaaaaaaaaaaaaaaaaaaaaa", cu.getCurrentWeekday());
    }
    
    public void setDayIncomeAndPayWhenAllCategoryAndSelectedDate(String s1, String s2){
        BigDecimal income = new BigDecimal("0");
        BigDecimal pay = new BigDecimal("0");
        Cursor cursor1 = billDB.queryIncomeByDate(s1, s2);
        if(cursor1.getCount() != 0){
            cursor1.moveToFirst();
            BigDecimal bi = new BigDecimal(cursor1.getString(2));
            income = income.add(bi);
            while(cursor1.moveToNext()){
                BigDecimal bi1 = new BigDecimal(cursor1.getString(2));
                income = income.add(bi1);
            }
            tv_income_today.setText(income.toString());
            cursor1.close();
        }
        else tv_income_today.setText("0");
        Cursor cursor2 = billDB.queryPayByDate(s1, s2);
        if(cursor2.getCount() != 0){
            cursor2.moveToFirst();
            BigDecimal bi = new BigDecimal(cursor2.getString(2));
            pay = pay.add(bi);
            while(cursor2.moveToNext()){
                BigDecimal bi1 = new BigDecimal(cursor2.getString(2));
                pay = pay.add(bi1);
            }
            tv_pay_today.setText(pay.toString());
        }
        else tv_pay_today.setText("0");
        cursor2.close();
    }
    
    public void setWeekIncomeAndPayWhenAllCategoryAndSelectedDate(String s1, String s2){
        BigDecimal income = new BigDecimal("0");
        BigDecimal pay = new BigDecimal("0");
        Cursor cursor1 = billDB.queryIncomeByDate(s1, s2);
        if(cursor1.getCount() != 0){
            cursor1.moveToFirst();
            BigDecimal bi = new BigDecimal(cursor1.getString(2));
            income = income.add(bi);
            while(cursor1.moveToNext()){
                BigDecimal bi1 = new BigDecimal(cursor1.getString(2));
                income = income.add(bi1);
            }
            tv_income_week.setText(income.toString());
            cursor1.close();
        }
        else tv_income_week.setText("0");
        Cursor cursor2 = billDB.queryPayByDate(s1, s2);
        if(cursor2.getCount() != 0){
            cursor2.moveToFirst();
            BigDecimal bi = new BigDecimal(cursor2.getString(2));
            pay = pay.add(bi);
            while(cursor2.moveToNext()){
                BigDecimal bi1 = new BigDecimal(cursor2.getString(2));
                pay = pay.add(bi1);
            }
            tv_pay_week.setText(pay.toString());
        }
        else tv_pay_week.setText("0");
        cursor2.close();
    }

    public void setMonthIncomeAndPayWhenAllCategoryAndSelectedDate(String s1, String s2){
        BigDecimal income = new BigDecimal("0");
        BigDecimal pay = new BigDecimal("0");
        Cursor cursor1 = billDB.queryIncomeByDate(s1, s2);
        if(cursor1.getCount() != 0){
            cursor1.moveToFirst();
            BigDecimal bi = new BigDecimal(cursor1.getString(2));
            income = income.add(bi);
            while(cursor1.moveToNext()){
                BigDecimal bi1 = new BigDecimal(cursor1.getString(2));
                income = income.add(bi1);
            }
            tv_income_month.setText(income.toString());
            cursor1.close();
        }
        else tv_income_month.setText("0");
        Cursor cursor2 = billDB.queryPayByDate(s1, s2);
        if(cursor2.getCount() != 0){
            cursor2.moveToFirst();
            BigDecimal bi = new BigDecimal(cursor2.getString(2));
            pay = pay.add(bi);
            while(cursor2.moveToNext()){
                BigDecimal bi1 = new BigDecimal(cursor2.getString(2));
                pay = pay.add(bi1);
            }
            tv_pay_month.setText(pay.toString());
        }
        else tv_pay_month.setText("0");
        cursor2.close();
    }
    
    public void setTextViewText(){
        billDBHelper billDB = new billDBHelper(this);
        TextView tv_income = (TextView) findViewById(R.id.tv_month_income);
        TextView tv_pay = (TextView) findViewById(R.id.tv_month_pay);
        TextView tv_over = (TextView) findViewById(R.id.tv_month_over);
        final Calendar c = Calendar.getInstance();  
        int mYear = c.get(Calendar.YEAR);  
        int mMonth = c.get(Calendar.MONTH);  
        int mDay = c.get(Calendar.DAY_OF_MONTH); 
        String month = (mMonth+1)<10 ? "0"+String.valueOf(mMonth+1) : String.valueOf(mMonth+1);
        String day = mDay<10 ? "0"+String.valueOf(mDay) : String.valueOf(mDay);
        String lastmonth = (mMonth)<10 ? "0"+String.valueOf(mMonth) : String.valueOf(mMonth);
        String date_today = String.valueOf(mYear) + month + day;
        String date_last_month = String.valueOf(mYear) + lastmonth + day;
        Cursor cursor1 = billDB.queryIncomeByDate(date_last_month, date_today);
        BigDecimal income = new BigDecimal("0");
        BigDecimal pay = new BigDecimal("0");
        if(cursor1.getCount() != 0){
            cursor1.moveToFirst();
            BigDecimal bi = new BigDecimal(cursor1.getString(2));
            income = income.add(bi);
            while(cursor1.moveToNext()){
                BigDecimal bi1 = new BigDecimal(cursor1.getString(2));
                income = income.add(bi1);
            }
            tv_income.setText(income.toString());
            cursor1.close();
        }
        else tv_income.setText("0");
        Cursor cursor2 = billDB.queryPayByDate(date_last_month, date_today);
        if(cursor2.getCount() != 0){
            cursor2.moveToFirst();
            BigDecimal bi = new BigDecimal(cursor2.getString(2));
            pay = pay.add(bi);
            while(cursor2.moveToNext()){
                BigDecimal bi1 = new BigDecimal(cursor2.getString(2));
                pay = pay.add(bi1);
            }
            tv_pay.setText(pay.toString());
        }
        else tv_pay.setText("0");
        BigDecimal over = new BigDecimal("0");
        over = income.subtract(pay);
        tv_over.setText(over.toString());
    }
    
    @Override
    public void onClick(View arg0) {
        switch(arg0.getId()){
        case R.id.btnnewpay: {
            Intent intent = new Intent();
            intent.setClass(this, addbillActivity.class);
            startActivity(intent);
            break;
        }
        case R.id.btnsearch: {
            Intent intent = new Intent();
            intent.setClass(this, searchActivity.class);
            startActivity(intent);
            break;
        }
        case R.id.btnset: {
            Intent intent = new Intent();
            intent.setClass(this, setActivity.class);
            startActivity(intent);
            break;
        }
        case R.id.ll_1:{
            Intent intent = new Intent();
            intent.setClass(this, searchActivity.class);
            intent.putExtra("start", cu.getToday());
            intent.putExtra("end", cu.getToday());
            intent.putExtra("spin", "全部");
            startActivity(intent);
            break;
        }
        case R.id.ll_2:{
            Intent intent = new Intent();
            intent.setClass(this, searchActivity.class);
            intent.putExtra("start", cu.getMondayOFWeek());
            intent.putExtra("end", cu.getCurrentWeekday());
            intent.putExtra("spin", "全部");
            startActivity(intent);
            break;
        }
        case R.id.ll_3:{
            Intent intent = new Intent();
            intent.setClass(this, searchActivity.class);
            intent.putExtra("start", cu.getFirstDayOfMonth());
            intent.putExtra("end", cu.getDefaultDay());
            intent.putExtra("spin", "全部");
            startActivity(intent);
            break;
        }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!isExit) {
                isExit = true;
                Toast.makeText(getApplicationContext(), "再按一次退出程序",
                        Toast.LENGTH_SHORT).show();
                mHandler.sendEmptyMessageDelayed(0, 2000);
            } else {
                finish();
                System.exit(0);
            }
        }
        return false;
    }
    boolean isExit = false;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };
    
    @Override
    public void onRestart(){
        super.onRestart();
        setTextViewText();
        tv1.setText(cu.getToday());
        tv2.setText(cu.getMondayOFWeek()+"至"+cu.getCurrentWeekday());
        tv3.setText(cu.getFirstDayOfMonth()+"至"+cu.getDefaultDay());
        setDayIncomeAndPayWhenAllCategoryAndSelectedDate(cu.getToday().replace("-", ""), cu.getToday().replace("-", ""));
        setWeekIncomeAndPayWhenAllCategoryAndSelectedDate(cu.getMondayOFWeek().replace("-", ""), cu.getCurrentWeekday().replace("-", ""));
        setMonthIncomeAndPayWhenAllCategoryAndSelectedDate(cu.getFirstDayOfMonth().replace("-", ""), cu.getDefaultDay().replace("-", ""));
        tv_today_day.setText(cu.getTodayDay()); 
    }
    
}
