package com.example.myaccount;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class setActivity extends Activity implements OnClickListener{
    private ListView listView;
    private categoryDBHelper categoryDB;
    private categoryAdapter categoryadapter;
    private Cursor cursor;
    private int selectedCategory = -1;
    private final int EDIT_ID = 0;
    private final int DELETE_ID = 1;
    private int id = 0;
    private String category_n = "";
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setpage);

        listView = (ListView) findViewById(R.id.lv_category);
        categoryDB = new categoryDBHelper(this);
        cursor = categoryDB.query("select * from m_category ", null);
        categoryadapter = new categoryAdapter(this, cursor);
        listView.setAdapter(categoryadapter);
        Button b1 = (Button) findViewById(R.id.btn_add_category);
        b1.setOnClickListener(this);
        listView.setOnItemClickListener(new ListView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
                selectedCategory = arg2;                                        //arg2参数表示选中的item的id
            }
        });
        listView.setOnCreateContextMenuListener(new OnCreateContextMenuListener(){
            @Override
            public void onCreateContextMenu(ContextMenu arg0, View arg1,
                    ContextMenuInfo arg2) {
                arg0.setHeaderTitle("操作");     
                arg0.add(0, EDIT_ID, 0, "编辑");   
                arg0.add(0, DELETE_ID, 0, "删除");   
            }});
        listView.setOnItemLongClickListener(new OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                    int arg2, long arg3) {
                selectedCategory = arg2;
                cursor.moveToPosition(arg2);
                id = cursor.getInt(0);
                category_n = cursor.getString(1);
                return false;
            }});
    }

    @Override  
    public boolean onContextItemSelected(MenuItem item) {   
        switch(item.getItemId()){
        case EDIT_ID: {
            showEditCategoryDialog();
            return true;
        }case DELETE_ID: {
            showDelcategoryDialog();
            return true;
        }
        }
        return super.onContextItemSelected(item);   
    } 

    @Override
    public void onClick(View arg0) {
        switch(arg0.getId()){
        case R.id.btn_add_category:{
            showAddcategoryDialog();
            break;
        }
        }
    }

    public void showEditCategoryDialog(){
        billDBHelper bDB = new billDBHelper(setActivity.this);
        cursor.moveToPosition(selectedCategory);
        if(!bDB.isExist_searchByCategory(cursor.getString(1))){
            AlertDialog.Builder builder = new Builder(this);
            builder.setTitle("编辑消费类别:   " + category_n);
            LinearLayout additem = (LinearLayout) getLayoutInflater().inflate(R.layout.addcategory_dialog, null);
            builder.setView(additem);
            final AlertDialog dialog = builder.create();
            dialog.setButton(AlertDialog.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    EditText et1 = (EditText) dialog.findViewById(R.id.category_name);
                    if(et1.getText().toString().equals("")){
                        final AlertDialog b = new AlertDialog.Builder(setActivity.this).setMessage("输入信息不完整!").show();
                        Handler h = new Handler();
                        h.postDelayed(new Runnable(){
                            @Override
                            public void run(){
                                b.dismiss();
                            }
                        }, 1000);
                    }
                    else{
                        String s = et1.getText().toString().replace(" ", "");
                        if(!s.equals("")){
                            if(categoryDB.isExist_Category(et1.getText().toString())){
                                Toast.makeText(setActivity.this, "已经保存有此命名的消费类型，添加失败", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                categoryDB.editItem(id, et1.getText().toString());
                                finish();
                                Intent intent = new Intent(setActivity.this, setActivity.class);
                                startActivity(intent);
                                Toast.makeText(setActivity.this, "消费类别编辑成功~", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else Toast.makeText(setActivity.this, "此消费类别的新名称不能全为空格", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                }
            });
            dialog.show();
        }
        else{
            AlertDialog.Builder builder = new Builder(this);
            builder.setTitle("此类别已存有账单，编辑后会同时修改账单信息，确认编辑吗？");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    final billDBHelper billDB = new billDBHelper(setActivity.this);
                    final Cursor mcursor = billDB.queryByCategory(category_n);
                    if(mcursor.getCount() == 0){
                        Toast.makeText(setActivity.this, "出现逻辑错误，正常情况下不应出现此Toast！！", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        AlertDialog.Builder builder = new Builder(setActivity.this);
                        builder.setTitle("编辑消费类别:   " + category_n);
                        LinearLayout additem = (LinearLayout) getLayoutInflater().inflate(R.layout.addcategory_dialog, null);
                        builder.setView(additem);
                        final AlertDialog dialog = builder.create();
                        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                EditText et1 = (EditText) dialog.findViewById(R.id.category_name);
                                if(et1.getText().toString().equals("")){
                                    final AlertDialog b = new AlertDialog.Builder(setActivity.this).setMessage("输入信息为空!").show();
                                    Handler h = new Handler();
                                    h.postDelayed(new Runnable(){
                                        @Override
                                        public void run(){
                                            b.dismiss();
                                        }
                                    }, 1000);
                                }
                                else{
                                    String s = et1.getText().toString().replace(" ", "");
                                    if(!s.equals("")){
                                        if(categoryDB.isExist_Category(et1.getText().toString())){
                                            Toast.makeText(setActivity.this, "已经保存有此命名的消费类型，添加失败", Toast.LENGTH_SHORT).show();
                                        }
                                        else{
                                            mcursor.moveToFirst();
                                            billDB.updateCategoryById(mcursor.getInt(0), et1.getText().toString());
                                            while(mcursor.moveToNext()){
                                                billDB.updateCategoryById(mcursor.getInt(0), et1.getText().toString());
                                            }
                                            categoryDB.editItem(id, et1.getText().toString());
                                            finish();
                                            Intent intent = new Intent(setActivity.this, setActivity.class);
                                            startActivity(intent);
                                            Toast.makeText(setActivity.this, "消费类别编辑成功~", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    else Toast.makeText(setActivity.this, "此消费类别的新名称不能全为空格", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                            }
                        });
                        dialog.show();
                    }
                }
            });
            builder.setNegativeButton("取消", null);
            builder.create().show();
        }
    }

    public void showDelcategoryDialog(){
        if(selectedCategory != -1){
            billDBHelper bDB = new billDBHelper(setActivity.this);
            cursor.moveToPosition(selectedCategory);
            if(!bDB.isExist_searchByCategory(cursor.getString(1))){
                AlertDialog.Builder builder = new Builder(this);
                builder.setTitle("确定删除此消费类别吗？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent intent = new Intent(setActivity.this, setActivity.class);
                        categoryDB.deleteItem(cursor.getString(1));
                        finish();
                        setActivity.this.startActivity(intent);
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.create().show();
            }
            else{
                Toast.makeText(setActivity.this, "此消费类型中已存有数据,不能删除", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(setActivity.this, "尚未选中任何消费类型", Toast.LENGTH_SHORT).show();
        }
    }

    public void showAddcategoryDialog(){
        AlertDialog.Builder builder = new Builder(this);
        builder.setTitle("添加消费类别");
        LinearLayout additem = (LinearLayout) getLayoutInflater().inflate(R.layout.addcategory_dialog, null);
        builder.setView(additem);
        final AlertDialog dialog = builder.create();
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                EditText et1 = (EditText) dialog.findViewById(R.id.category_name);
                if(et1.getText().toString().equals("")){
                    final AlertDialog b = new AlertDialog.Builder(setActivity.this).setMessage("输入信息不完整!").show();
                    Handler h = new Handler();
                    h.postDelayed(new Runnable(){
                        @Override
                        public void run(){
                            b.dismiss();
                        }
                    }, 1000);
                }
                else{
                    String s = et1.getText().toString().replace(" ", "");
                    if(!s.equals("")){
                        if(categoryDB.isExist_Category(et1.getText().toString().replace(" ", ""))){
                            Toast.makeText(setActivity.this, "已经保存有此命名的消费类型，添加失败", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            categoryDB.addItem(et1.getText().toString());
                            finish();
                            Intent intent = new Intent(setActivity.this, setActivity.class);
                            startActivity(intent);
                            Toast.makeText(setActivity.this, "消费类别添加成功~", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else Toast.makeText(setActivity.this, "新加的消费类型的名称不能全为空格", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });
        dialog.show();
    }
}
