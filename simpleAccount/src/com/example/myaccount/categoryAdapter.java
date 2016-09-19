package com.example.myaccount;

import com.example.myaccount.budgetAdapter.ViewHolder;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class categoryAdapter extends BaseAdapter {
    private Context context;
    private Cursor cursor;
    private LayoutInflater inflater;
    private int selectedIndex = -1;
    private String s;
    private int i;
    
    public categoryAdapter(Context context, Cursor cursor){
        this.context = context;
        this.cursor = cursor;
        inflater = LayoutInflater.from(context);
    }
    
    @Override
    public int getCount() {
        return cursor.getCount();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public String getMoney(){
        return this.s;
    }
    
    public void setMoney(String s){
        this.s = s;
    }
    
    public void setId(int i){
        this.i = i;
    }
    
    public int getId(){
        return this.i;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holde = null;
        if(convertView == null){
        holde = new ViewHolder();
        convertView = (LinearLayout) inflater.inflate(R.layout.list_category_item, null);
        holde.textView = (TextView) convertView.findViewById(R.id.list_category_tv);
        convertView.setTag(holde);
        }
        else holde = (ViewHolder) convertView.getTag();
        
        cursor.moveToPosition(position);
//        if(holde.textView == null)
//        Log.d("6555555", cursor.getString(1));
        holde.textView.setText(cursor.getString(1));
        return convertView;
    }
    
    public class ViewHolder {
        TextView textView;
//        EditText editText;
    }

}
