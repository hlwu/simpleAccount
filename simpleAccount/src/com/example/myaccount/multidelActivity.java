package com.example.myaccount;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myaccount.multidelAdapter.ViewHolder;

public class multidelActivity extends Activity implements OnClickListener{
    private Button btncancal, btnsure;
    private ListView lv_multi_del;
    private TextView tv_for_multi_id;
    private CheckBox cb_multi_del;
    private billDBHelper billDB;
    private multidelAdapter multideladapter;
    private Cursor cursor;
    private List<String> selectId;
    private final String date_min = "0";
    private final String date_max = "999999999";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multi_del);

        btncancal = (Button) findViewById(R.id.multi_cancal);
        btncancal.setOnClickListener(this);
        btnsure = (Button) findViewById(R.id.multi_sure);
        btnsure.setOnClickListener(this);
        lv_multi_del = (ListView) findViewById(R.id.lv_multi_del);
        cb_multi_del = (CheckBox) findViewById(R.id.cb_multi_del);
        tv_for_multi_id = (TextView) findViewById(R.id.tv_for_multi_id);
        billDB = new billDBHelper(this);
        Bundle bundle = getIntent().getExtras();
        search(bundle.getString("start").replace("-", ""), bundle.getString("end").replace("-", ""), bundle.getString("spin"));
        selectId = new ArrayList<String>();
        lv_multi_del.setOnItemClickListener(new ListView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
                ViewHolder vh = (ViewHolder) arg1.getTag();
                if(vh.cb.isChecked()){
                    cursor.moveToPosition(arg2);
                    selectId.remove(String.valueOf(cursor.getInt(0)));
                }
                else {
                    cursor.moveToPosition(arg2);
                    selectId.add(String.valueOf(cursor.getInt(0)));
                }
                vh.cb.toggle();
                multideladapter.getIsSelected().put(arg2, vh.cb.isChecked());
            }
        });
    }
    @Override
    public void onClick(View arg0) {
        switch(arg0.getId()){
        case R.id.multi_cancal:{
            finish();
            break;
        }
        case R.id.multi_sure: {
            if(selectId != null){
                if(selectId.size() == 0){
                    Toast.makeText(this, "没有选中任何选项", Toast.LENGTH_SHORT).show();
                }
                else{
                    AlertDialog.Builder builder = new Builder(this);
                    builder.setTitle("确定删除选中账单吗？");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            deleteselected();
                        }
                    });
                    builder.setNegativeButton("取消", null);
                    builder.create().show();
                }
            }
            else {
                Toast.makeText(this, "没有选中任何选项", Toast.LENGTH_SHORT).show();
            }
            break;
        }
        }
    }

    public void deleteselected(){
        for(int i = 0; i < selectId.size(); i++){
            Log.d("aaaaaaaaaaaaaa", String.valueOf(i));
            billDB.deleteByid(Integer.valueOf(selectId.get(i)));
            finish();
        }
    }

    public void search(String d1, String d2, String category){
        if(d1.equals("开始时间") && d2.equals("结束时间")){
            if(category.equals("全部")){
                cursor = billDB.queryByDate(date_min, date_max);
                multideladapter = new multidelAdapter(multidelActivity.this, cursor);
                lv_multi_del.setAdapter(multideladapter);
            }
            else {
                cursor = billDB.queryByDateAndCategory(date_min, date_max, category);
                multideladapter = new multidelAdapter(multidelActivity.this, cursor);
                lv_multi_del.setAdapter(multideladapter);
            }
        }
        if(!d1.equals("开始时间") && d2.equals("结束时间")){
            String date1 = d1.replace("-", "");
            if(category.equals("全部")){
                cursor = billDB.queryByDate(date1, date_max);
                multideladapter = new multidelAdapter(multidelActivity.this, cursor);
                lv_multi_del.setAdapter(multideladapter);
            }
            else{
                cursor = billDB.queryByDateAndCategory(date1, date_max, category);
                multideladapter = new multidelAdapter(multidelActivity.this, cursor);
                lv_multi_del.setAdapter(multideladapter);
            }
        }
        else if(d1.equals("开始时间") && !d2.equals("结束时间")){
            String date2 = d2.replace("-", "");
            if(category.equals("全部")){
                cursor = billDB.queryByDate(date_min, date2);
                multideladapter = new multidelAdapter(multidelActivity.this, cursor);
                lv_multi_del.setAdapter(multideladapter);
            }
            else{
                cursor = billDB.queryByDateAndCategory(date_min, date2, category);
                multideladapter = new multidelAdapter(multidelActivity.this, cursor);
                lv_multi_del.setAdapter(multideladapter);
            }
        }
        else if(!d1.equals("开始时间") && !d2.equals("结束时间")){
            String date1 = d1.replace("-", "");
            String date2 = d2.replace("-", "");
            if(Integer.valueOf(date1) <= Integer.valueOf(date2)){
                if(category.equals("全部")){
                    cursor = billDB.queryByDate(date1, date2);
                    multideladapter = new multidelAdapter(multidelActivity.this, cursor);
                    lv_multi_del.setAdapter(multideladapter);
                }
                else{
                    cursor = billDB.queryByDateAndCategory(date1, date2, category);
                    multideladapter = new multidelAdapter(multidelActivity.this, cursor);
                    lv_multi_del.setAdapter(multideladapter);
                }
            }
            else {
                Toast.makeText(multidelActivity.this, "您选择的时间段不正确，请确保开始日期小于结束日期", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
