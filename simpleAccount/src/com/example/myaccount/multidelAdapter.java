package com.example.myaccount;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

public class multidelAdapter extends BaseAdapter {
    private Context context;
    private Cursor cursor;
    private LayoutInflater inflater;
    private int selectedIndex = -1;
    private static HashMap isSelected;

    public multidelAdapter(Context context, Cursor cursor){
        this.context = context;
        this.cursor = cursor;
        inflater = LayoutInflater.from(context);
        isSelected = new HashMap();
        for(int i = 0; i < cursor.getCount(); i++){
            getIsSelected().put(i, false);
        }
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holde = null;
        if(convertView == null){
            holde = new ViewHolder();
            convertView = (LinearLayout) inflater.inflate(R.layout.list_multi_del_item, null);
            holde.tv_for_id = (TextView) convertView.findViewById(R.id.tv_for_multi_id);
            holde.cb = (CheckBox) convertView.findViewById(R.id.cb_multi_del);
            holde.tv_category = (TextView) convertView.findViewById(R.id.tv_multi_category);
            holde.tv_money = (TextView) convertView.findViewById(R.id.tv_multi_money);
            holde.tv_name = (TextView) convertView.findViewById(R.id.tv_multi_name);
            //        StringBuffer sb = new StringBuffer();
            holde.tv_date = (TextView) convertView.findViewById(R.id.tv_multi_date);

            convertView.setTag(holde);
        }
        else 
            holde = (ViewHolder) convertView.getTag();

        cursor.moveToPosition(position);
        holde.tv_for_id.setText(String.valueOf(cursor.getInt(0)));
        holde.tv_category.setText(cursor.getString(3));
        holde.tv_money.setText(cursor.getString(2));
        holde.tv_name.setText(cursor.getString(6));
        String s = cursor.getString(4).substring(0, 4) + "-" + cursor.getString(4).substring(4, 6) + "-" + cursor.getString(4).substring(6, 8);
        holde.tv_date.setText(s);
        if(cursor.getInt(1) == 1)
            holde.tv_money.setTextColor(Color.rgb(103, 161, 59));
        else 
            holde.tv_money.setTextColor(Color.rgb(255, 0, 0));

        holde.cb.setChecked((Boolean) getIsSelected().get(position));
        return convertView;
    }

    public static HashMap getIsSelected() {
        return isSelected;
    }

    public static void setIsSelected(HashMap isSelected) {
        multidelAdapter.isSelected = isSelected;
    }

    public class ViewHolder {
        TextView tv_for_id;
        TextView tv_for_choice;
        TextView tv_category;
        TextView tv_money;
        TextView tv_remark;
        TextView tv_date;
        TextView tv_name;
        CheckBox cb;
    }
}
