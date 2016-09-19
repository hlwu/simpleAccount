package com.example.myaccount;

import android.content.Context;
import android.database.Cursor;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class budgetAdapter extends BaseAdapter {
    private Context context;
    private Cursor cursor;
    private LayoutInflater inflater;
    private LinearLayout layout;
    private int selectedIndex = -1;
    private String s;
    private int i;
    
    public budgetAdapter(Context context, Cursor cursor){
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
        cursor.moveToPosition(position);
        final ViewHolder holde = new ViewHolder();
        layout = (LinearLayout) inflater.inflate(R.layout.listitem, null);
        holde.textView = (TextView) layout.findViewById(R.id.listitem_tv);
        holde.textView.setText(cursor.getString(1));
        holde.editText = (EditText) layout.findViewById(R.id.listitem_et);
        holde.editText.setText(cursor.getString(2));
        return layout;
    }
    
    public class ViewHolder {
        TextView textView;
        EditText editText;
    }

}
