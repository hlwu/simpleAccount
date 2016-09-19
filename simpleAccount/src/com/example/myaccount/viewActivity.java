package com.example.myaccount;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class viewActivity extends Activity {
    private TextView tv_choice, tv_money, tv_category, tv_date, tv_remark, tv_name;
    private final int INCOME = 1;
    private final int PAY = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view);
        
        tv_money = (TextView) findViewById(R.id.tv_view_money);
        tv_choice = (TextView) findViewById(R.id.tv_view_choice);
        tv_category = (TextView) findViewById(R.id.tv_view_category);
        tv_date = (TextView) findViewById(R.id.tv_view_date);
        tv_remark = (TextView) findViewById(R.id.tv_view_remark);
        tv_name = (TextView) findViewById(R.id.tv_view_name);
        
        Bundle bundle = getIntent().getExtras();
        tv_money.setText(bundle.getString("money") + "   ￥");
        String s = bundle.getString("date").substring(0, 4) + "-" + bundle.getString("date").substring(4, 6) + "-" + bundle.getString("date").substring(6, 8);
        tv_date.setText(s);
        tv_remark.setText(bundle.getString("remark"));
        tv_name.setText(bundle.getString("name"));
        if(bundle.getInt("choice") == INCOME)
            tv_choice.setText("收入");
        else tv_choice.setText("支出");
        tv_category.setText(bundle.getString("category"));

    }

}
