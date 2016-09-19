package com.example.myaccount;

import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;

public class setByTabBar extends ActivityGroup{
    private TabHost tabHost;  
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.tab);  
          
        //加载底部Tab布局   
        LinearLayout tab1=(LinearLayout)LayoutInflater.from(this).inflate(R.layout.action_item, null);  
        ImageView icon1=(ImageView)tab1.findViewById(R.id.icon);  
        icon1.setBackgroundResource(R.drawable.ic_launcher);  
//        TextView title1=(TextView)tab1.findViewById(R.id.title);  
//        title1.setText("Activity1");  
          
        LinearLayout tab2=(LinearLayout)LayoutInflater.from(this).inflate(R.layout.action_item, null);  
        ImageView icon2=(ImageView)tab2.findViewById(R.id.icon);  
        icon2.setBackgroundResource(R.drawable.ic_launcher);  
//        TextView title2=(TextView)tab2.findViewById(R.id.title);  
//        title2.setText("Activity2");  
          
        // 加载TabSpec   
        tabHost = (TabHost) findViewById(R.id.view_tab_host);  
        tabHost.setup(getLocalActivityManager());  
  
        TabSpec ts1 = tabHost.newTabSpec("Activity1");  
        ts1.setIndicator(tab1);//这句话就是设置每个小tab显示的内容   
        //第一个Activity   
        ts1.setContent(new Intent(this, setActivity.class));  
        tabHost.addTab(ts1);  
      
        TabSpec ts2 = tabHost.newTabSpec("Activity2");  
        ts2.setIndicator(tab2);  
        //第二个Activity   
        ts2.setContent(new Intent(this, setbudgetActivity.class));  
        tabHost.addTab(ts2);  
  
        //设置标签栏背景图片   
        TabWidget tw = tabHost.getTabWidget();  
    }  
}
