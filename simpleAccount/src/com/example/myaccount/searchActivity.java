package com.example.myaccount;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class searchActivity extends Activity implements OnClickListener{
    private static final int SHOW_DATAPICK= 0; 
    private static final int DATE_DIALOG_ID1 = 1;  
    private static final int DATE_DIALOG_ID2 = 3; 
    private static final int SHOW_TIMEPICK = 2;
    private static final int MUTI_CHOICE_DIALOG = 4;
    private static final String date_max = "999999999";
    private static final String date_min = "0";
    private int mYear1, mYear2;  
    private int mMonth1, mMonth2;
    private int mDay1, mDay2; 
    private String date;
    private Spinner spinner;
    private ArrayAdapter<AccountType> arrayadapter;
    private ListView lv_show_bill;
    private billDBHelper billDB;
    private billAdapter billadapter;
    private Cursor cursor;
    private Button b1, b2, b4, b5;
    private TextView tv_income, tv_pay;
    private int tag = 0;                                //用于按类型查询是判断是否需要同时按时间查询,0为不需要，1为需要
    private int selectedId = -1;
    private int id;
    private Menu menu;
    private String[] s;
    private boolean[] b;
    private int[] multiId;
    private String btnStartText, btnEndText;
    private int spin_selectedId;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        tv_income = (TextView) findViewById(R.id.tv_show_income);
        tv_pay = (TextView) findViewById(R.id.tv_show_pay);
        b1 = (Button) findViewById(R.id.btn_date_start);
        b1.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View arg0) {
                Message msg = new Message();   
                msg.what = searchActivity.SHOW_DATAPICK;  
                searchActivity.this.dateandtimeHandler1.sendMessage(msg); 
            }
        });
        b2 = (Button) findViewById(R.id.btn_date_end);
        b2.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View arg0) {
                Message msg = new Message();   
                msg.what = searchActivity.SHOW_DATAPICK;  
                searchActivity.this.dateandtimeHandler2.sendMessage(msg); 
            }
        });
        b4 = (Button) findViewById(R.id.btn_refresh_search);
        b4.setOnClickListener(this);
        b5 = (Button) findViewById(R.id.btn_multidel);
        b5.setOnClickListener(this);
        arrayadapter = new ArrayAdapter<AccountType>(this,R.layout.my_show_spinner_text,getAccountTypeList());
        arrayadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner = (Spinner) findViewById(R.id.spin_search);
        spinner.setAdapter(arrayadapter);
        spinner.setVisibility(View.VISIBLE);
        lv_show_bill = (ListView) findViewById(R.id.lv_show_bill);
        billDB = new billDBHelper(this);
        cursor = billDB.query("select * from m_bill order by bill_date desc", null);
        if(cursor.getCount() == 0)
            Toast.makeText(searchActivity.this, "目前尚无账单记录！", Toast.LENGTH_SHORT).show();
        billadapter = new billAdapter(this, cursor);
        lv_show_bill.setAdapter(billadapter);
        lv_show_bill.setOnItemClickListener(new ListView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
                Intent intent = new Intent(searchActivity.this,viewActivity.class);
                cursor.moveToPosition(arg2);
                intent.putExtra("id", cursor.getInt(0));
                intent.putExtra("choice", cursor.getInt(1));
                intent.putExtra("money", cursor.getString(2));
                intent.putExtra("category", cursor.getString(3));
                intent.putExtra("date", cursor.getString(4));
                intent.putExtra("remark", cursor.getString(5));
                intent.putExtra("name", cursor.getString(6));
                searchActivity.this.startActivity(intent);
            }
        });
        lv_show_bill.setOnItemLongClickListener(new OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                    int arg2, long arg3) {
                selectedId = arg2;
                cursor.moveToPosition(arg2);
                id = cursor.getInt(0);
                return false;
            }});
        lv_show_bill.setOnCreateContextMenuListener(new OnCreateContextMenuListener(){
            @Override
            public void onCreateContextMenu(ContextMenu arg0, View arg1,
                    ContextMenuInfo arg2) {
                arg0.setHeaderTitle("操作");     
                arg0.add(0, 0, 0, "编辑");   
                arg0.add(0, 1, 0, "删除");   
            }});
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            spinner.setSelection(bundle.getInt("spinner"));
            b1.setText(bundle.getString("start"));
            b2.setText(bundle.getString("end"));
            if(bundle.getString("start").equals("开始时间") && bundle.getString("end").equals("结束时间")){
                String category = spinner.getSelectedItem().toString();
                cursor = billDB.queryByCategory(category);
                billAdapter billadapter1 = new billAdapter(searchActivity.this, cursor);
                lv_show_bill.setAdapter(billadapter1);
                setIncomeAndPay();
            }
            else{
                search(bundle.getString("start"), bundle.getString("end"), spinner.getSelectedItem().toString());
            }
        }
        spinner.setOnItemSelectedListener(new OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                    int arg2, long arg3) {
                if(tag == 0){
                    if(!spinner.getSelectedItem().toString().equals("全部")){          //未选时间时，选择非"全部"类别
                        String category = spinner.getSelectedItem().toString();
                        cursor = billDB.queryByCategory(category);
                        billAdapter billadapter1 = new billAdapter(searchActivity.this, cursor);
                        lv_show_bill.setAdapter(billadapter1);
                        setIncomeAndPayWhenNoAllCategoryAndNoDate(category);
                    }
                    else{                                                                                   //未选择时间时，选择“全部”类别
                        cursor = billDB.queryAll();
                        billAdapter billadapter1 = new billAdapter(searchActivity.this, cursor);
                        lv_show_bill.setAdapter(billadapter1);
                        setIncomeAndPayWhenAllCategoryAndNoDate();
                    }
                }
                else{                                                                                 
                    if(!spinner.getSelectedItem().toString().equals("全部")){        //选择时间时，选择非“全部”类别
                        if(!b1.getText().toString().equals("开始时间") && !b2.getText().toString().equals("结束时间")){
                            String date1 = b1.getText().toString().replace("-", "");
                            String date2 = b2.getText().toString().replace("-", "");
                            cursor = billDB.queryByDateAndCategory(date1, date2, spinner.getSelectedItem().toString());
                            billAdapter billadapter1 = new billAdapter(searchActivity.this, cursor);
                            lv_show_bill.setAdapter(billadapter1);
                            setIncomeAndPayWhenNoAllCategoryAndSelectedDate(date1, date2, spinner.getSelectedItem().toString());
                        }
                        else if(!b1.getText().toString().equals("开始时间") && b2.getText().toString().equals("结束时间")){
                            String date1 = b1.getText().toString().replace("-", "");
                            cursor = billDB.queryByDateAndCategory(date1, date_max, spinner.getSelectedItem().toString());
                            billAdapter billadapter1 = new billAdapter(searchActivity.this, cursor);
                            lv_show_bill.setAdapter(billadapter1);
                            setIncomeAndPayWhenNoAllCategoryAndSelectedDate(date1, date_max, spinner.getSelectedItem().toString());
                        }
                        else if(b1.getText().toString().equals("开始时间") && !b2.getText().toString().equals("结束时间")){
                            String date2 = b2.getText().toString().replace("-", "");
                            cursor = billDB.queryByDateAndCategory(date_min, date2, spinner.getSelectedItem().toString());
                            billAdapter billadapter1 = new billAdapter(searchActivity.this, cursor);
                            lv_show_bill.setAdapter(billadapter1);
                            setIncomeAndPayWhenNoAllCategoryAndSelectedDate(date_min, date2, spinner.getSelectedItem().toString());
                        }
                    }
                    else{                                                                                 //选择时间时，选择“全部”类别
                        if(!b1.getText().toString().equals("开始时间") && !b2.getText().toString().equals("结束时间")){
                            String date1 = b1.getText().toString().replace("-", "");
                            String date2 = b2.getText().toString().replace("-", "");
                            cursor = billDB.queryByDate(date1, date2);
                            billAdapter billadapter1 = new billAdapter(searchActivity.this, cursor);
                            lv_show_bill.setAdapter(billadapter1);
//                            setIncomeAndPayWhenNoAllCategoryAndSelectedDate(date1, date2, spinner.getSelectedItem().toString());
                            setIncomeAndPay();
                        }
                        else if(!b1.getText().toString().equals("开始时间") && b2.getText().toString().equals("结束时间")){
                            String date1 = b1.getText().toString().replace("-", "");
                            cursor = billDB.queryByDate(date1, date_max);
                            billAdapter billadapter1 = new billAdapter(searchActivity.this, cursor);
                            lv_show_bill.setAdapter(billadapter1);
                            setIncomeAndPayWhenAllCategoryAndSelectedDate(date1, date_max);
                        }
                        else if(b1.getText().toString().equals("开始时间") && !b2.getText().toString().equals("结束时间")){
                            String date2 = b2.getText().toString().replace("-", "");
                            cursor = billDB.queryByDate(date_min, date2);
                            billAdapter billadapter1 = new billAdapter(searchActivity.this, cursor);
                            lv_show_bill.setAdapter(billadapter1);
                            setIncomeAndPayWhenAllCategoryAndSelectedDate(date_min, date2);
                        }
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }});

        final Calendar c = Calendar.getInstance();
        mYear1 = c.get(Calendar.YEAR);  
        mMonth1 = c.get(Calendar.MONTH);  
        mDay1 = c.get(Calendar.DAY_OF_MONTH);
        mYear2 = c.get(Calendar.YEAR);  
        mMonth2 = c.get(Calendar.MONTH);  
        mDay2 = c.get(Calendar.DAY_OF_MONTH);
        String month = String.valueOf((mMonth2 + 1) < 10 ? "0" + (mMonth2 + 1) : (mMonth2 + 1));
        String day = String.valueOf((mDay2 < 10) ? "0" + mDay2 : mDay1);
        date = String.valueOf(mYear2) +month + day;

        setIncomeAndPay();
    }

    @Override  
    public boolean onContextItemSelected(MenuItem item) {   
        if(item.getItemId() == 0){
            Cursor cursor1 = billDB.query("select * from m_bill ", null);
            Intent intent = new Intent(searchActivity.this,modifybillActivity.class);
            finish();
            cursor1.moveToPosition(selectedId);
            intent.putExtra("id", cursor.getInt(0));
            intent.putExtra("choice", cursor.getInt(1));
            intent.putExtra("money", cursor.getString(2));
            intent.putExtra("category", cursor.getString(3));
            intent.putExtra("date", cursor.getString(4));
            intent.putExtra("remark", cursor.getString(5));
            intent.putExtra("spin", (int)spinner.getSelectedItemId());
            intent.putExtra("start", b1.getText().toString());
            intent.putExtra("end", b2.getText().toString());
            intent.putExtra("name", cursor.getString(6));
            searchActivity.this.startActivity(intent);
        }
        else if(item.getItemId() == 1) {
            delete_bill();
        }
        return super.onContextItemSelected(item);   
    } 

    //没选时间/全部类别时， 收入和支出
    public void setIncomeAndPayWhenAllCategoryAndNoDate(){
        BigDecimal income = new BigDecimal("0");
        BigDecimal pay = new BigDecimal("0");
        Cursor cursor1 = billDB.queryIncomeAll();
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
        Cursor cursor2 = billDB.queryPayAll();
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
        cursor2.close();
    }

    //选择时间/全部类别时， 收入和支出
    public void setIncomeAndPayWhenAllCategoryAndSelectedDate(String s1, String s2){
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
            tv_income.setText(income.toString());
            cursor1.close();
        }
        else tv_income.setText("0");
        Cursor cursor2 = billDB.queryPayByDate(s1, s2);
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
        cursor2.close();
    }

    //选择时间/非全部类别，  收入和支出
    public void setIncomeAndPayWhenNoAllCategoryAndSelectedDate(String s1, String s2, String category){
        BigDecimal income = new BigDecimal("0");
        BigDecimal pay = new BigDecimal("0");
        Cursor cursor1 = billDB.queryIncomeByDateAndCategory(s1, s2, category);
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
        Cursor cursor2 = billDB.queryPayByDateAndCategory(s1, s2, category);
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
        cursor2.close();
    }

    //未选时间/非全部类别， 收入和支出
    public void setIncomeAndPayWhenNoAllCategoryAndNoDate(String category){
        BigDecimal income = new BigDecimal("0");
        BigDecimal pay = new BigDecimal("0");
        Cursor cursor1 = billDB.queryIncomeBycategory(category);
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
        Cursor cursor2 = billDB.queryPayBycategory(category);
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
        cursor2.close();
    }

    Handler dateandtimeHandler1 = new Handler() {
        @Override  
        public void handleMessage(Message msg) {  
            switch (msg.what) {  
            case searchActivity.SHOW_DATAPICK:  
                showDialog(DATE_DIALOG_ID1);  
                break; 
            }  
        }  
    }; 
    Handler dateandtimeHandler2 = new Handler() {
        @Override  
        public void handleMessage(Message msg) {  
            switch (msg.what) {  
            case searchActivity.SHOW_DATAPICK:  
                showDialog(DATE_DIALOG_ID2);  
                break; 
            }  
        }  
    }; 

    @Override
    protected Dialog onCreateDialog(int id){
        switch (id) {
        case DATE_DIALOG_ID1:
            return new DatePickerDialog(this, mDateSetListener1, mYear1, mMonth1, mDay1);
        case DATE_DIALOG_ID2:
            return new DatePickerDialog(this, mDateSetListener2, mYear2, mMonth2, mDay2);
        case MUTI_CHOICE_DIALOG:  
            return selectMultiChoice();
        }
        return null;
    }

    public Dialog selectMultiChoice(){
        Dialog dialog = null;
        Builder builder = new AlertDialog.Builder(this);  
        builder.setTitle("选择要删除的条目");  
        builder.setMultiChoiceItems(s, b, new DialogInterface.OnMultiChoiceClickListener() {  
            @Override  
            public void onClick(DialogInterface dialogInterface,   
                    int which, boolean isChecked) {  
                b[which] = isChecked;  
            }  
        } );  
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {  
            @Override  
            public void onClick(DialogInterface dialogInterface, int which) {  
                int z = 0;
                for(int x = 0; x < b.length; x++){
                    if(b[x] == true)
                        z++;
                }
                if(z > 0){
                    AlertDialog.Builder builder = new Builder(searchActivity.this);
                    builder.setTitle("确认删除选中账单吗？");
                    final AlertDialog dialog1 = builder.create();
                    dialog1.setButton(AlertDialog.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            for(int k = 0; k < b.length; k++){
                                if(b[k] == true){
                                    billDB.deleteByid(multiId[k]);
                                }
                            }
                            cursor = billDB.query("select * from m_bill ", null);
                            billadapter = new billAdapter(searchActivity.this, cursor);
                            lv_show_bill.setAdapter(billadapter);
                            Toast.makeText(searchActivity.this, "选中账单删除成功~", Toast.LENGTH_SHORT).show();
                        }
                    });
                    dialog1.show();
                }
                else
                    Toast.makeText(searchActivity.this, "未选中任何账单", Toast.LENGTH_SHORT).show();
            }
        });  
        builder.setNegativeButton("取消", null);
        dialog = builder.create();  
        return dialog;
    }

    @Override  
    protected void onPrepareDialog(int id, Dialog dialog) {  
        switch (id) {  
        case DATE_DIALOG_ID1:  
            ((DatePickerDialog) dialog).updateDate(mYear1, mMonth1, mDay1);  
            break;
        case DATE_DIALOG_ID2:  
            ((DatePickerDialog) dialog).updateDate(mYear2, mMonth2, mDay2);  
            break;
        }
    }  

    private void updateDateDisplay1(Button showDate){
        if(!b2.getText().toString().equals("结束时间")){
            String month = (mMonth1 + 1) < 10 ? "0" + String.valueOf((mMonth1 + 1)) : String.valueOf((mMonth1 + 1));
            String day = (mDay1 < 10) ? "0" + String.valueOf(mDay1) : String.valueOf(mDay1);
            String date1 = mYear1 + month + day;
            String d2 = b2.getText().toString().replace("-", "");
            if(Integer.valueOf(date1) <= Integer.valueOf(d2)){
                if(Integer.valueOf(date1) > Integer.valueOf(date)){
                    Toast.makeText(searchActivity.this, "注意：选择的开始时间大于实际时间！！", Toast.LENGTH_SHORT).show();
                }
                showDate.setText(new StringBuilder().append(mYear1).append("-")
                        .append((mMonth1 + 1) < 10 ? "0" + (mMonth1 + 1) : (mMonth1 + 1)).append("-")
                        .append((mDay1 < 10) ? "0" + mDay1 : mDay1)); 
                search(b1.getText().toString(), b2.getText().toString(), spinner.getSelectedItem().toString());
            }
            else{
                Toast.makeText(searchActivity.this, "请确保开始时间小于结束时间", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            String month = (mMonth1 + 1) < 10 ? "0" + String.valueOf((mMonth1 + 1)) : String.valueOf((mMonth1 + 1));
            String day = (mDay1 < 10) ? "0" + String.valueOf(mDay1) : String.valueOf(mDay1);
            String date1 = mYear1 + month + day;
            if(Integer.valueOf(date1) > Integer.valueOf(date)){
                Toast.makeText(searchActivity.this, "注意：选择的开始时间大于实际时间！！", Toast.LENGTH_SHORT).show();
            }
            showDate.setText(new StringBuilder().append(mYear1).append("-")
                    .append((mMonth1 + 1) < 10 ? "0" + (mMonth1 + 1) : (mMonth1 + 1)).append("-")
                    .append((mDay1 < 10) ? "0" + mDay1 : mDay1)); 
            search(b1.getText().toString(), b2.getText().toString(), spinner.getSelectedItem().toString());
        }
    }

    private void updateDateDisplay2(Button showDate){
        if(!b1.getText().toString().equals("开始时间")){
            String month = (mMonth2 + 1) < 10 ? "0" + String.valueOf((mMonth2 + 1)) : String.valueOf((mMonth2 + 1));
            String day = (mDay2 < 10) ? "0" + String.valueOf(mDay2) : String.valueOf(mDay2);
            String date2 = mYear2 + month + day;
            if(Integer.valueOf(date2) >= Integer.valueOf(b1.getText().toString().replace("-", ""))){
                if(Integer.valueOf(date2) > Integer.valueOf(date)){
                    Toast.makeText(searchActivity.this, "注意：选择的结束时间大于实际时间！！", Toast.LENGTH_SHORT).show();
                }
                showDate.setText(new StringBuilder().append(mYear2).append("-")
                        .append((mMonth2 + 1) < 10 ? "0" + (mMonth2 + 1) : (mMonth2 + 1)).append("-")
                        .append((mDay2 < 10) ? "0" + mDay2 : mDay2)); 
                search(b1.getText().toString(), b2.getText().toString(), spinner.getSelectedItem().toString());
            }
            else Toast.makeText(searchActivity.this, "请确保开始时间小于结束时间", Toast.LENGTH_SHORT).show();
        }
        else{
            String month = (mMonth2 + 1) < 10 ? "0" + String.valueOf((mMonth2 + 1)) : String.valueOf((mMonth2 + 1));
            String day = (mDay2 < 10) ? "0" + String.valueOf(mDay2) : String.valueOf(mDay2);
            String date1 = mYear2 + month + day;
            if(Integer.valueOf(date1) > Integer.valueOf(date)){
                Toast.makeText(searchActivity.this, "注意：选择的结束时间大于实际时间！！", Toast.LENGTH_SHORT).show();
            }
            showDate.setText(new StringBuilder().append(mYear2).append("-")
                    .append((mMonth2 + 1) < 10 ? "0" + (mMonth2 + 1) : (mMonth2 + 1)).append("-")
                    .append((mDay2 < 10) ? "0" + mDay2 : mDay2)); 
            search(b1.getText().toString(), b2.getText().toString(), spinner.getSelectedItem().toString());
        }
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener1 = new DatePickerDialog.OnDateSetListener() {  
        public void onDateSet(DatePicker view, int year, int monthOfYear,  
                int dayOfMonth) {  
            mYear1 = year;  
            mMonth1 = monthOfYear;  
            mDay1 = dayOfMonth;  
            updateDateDisplay1(b1);
        }  
    }; 
    private DatePickerDialog.OnDateSetListener mDateSetListener2 = new DatePickerDialog.OnDateSetListener() {  
        public void onDateSet(DatePicker view, int year, int monthOfYear,  
                int dayOfMonth) {  
            mYear2 = year;  
            mMonth2 = monthOfYear;  
            mDay2 = dayOfMonth;  
            updateDateDisplay2(b2);
        }  
    }; 

    public List<AccountType>  getAccountTypeList(){
        List<AccountType> accountTypes = new ArrayList<AccountType>();    
        categoryDBHelper categoryDB = new categoryDBHelper(getApplicationContext());
        Cursor cursor = categoryDB.queryType();
        AccountType at = new AccountType();  
        at.TypeId = String.valueOf(0);
        at.TypeName = "全部";
        accountTypes.add(at); 
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String strTypeName = cursor.getString(1);
            AccountType accountType = new AccountType();  
            accountType.TypeId = id+"";
            accountType.TypeName = strTypeName;
            accountTypes.add(accountType); 
        }
        return accountTypes;
    }

    @Override
    public void onClick(View arg0) {
        switch(arg0.getId()){
        case R.id.btn_refresh_search:{
            spinner.setSelection(0);
            b1.setText("开始时间");
            b2.setText("结束时间");
            cursor = billDB.queryAll();
            billAdapter billadapter1 = new billAdapter(searchActivity.this, cursor);
            lv_show_bill.setAdapter(billadapter1);
            setIncomeAndPayWhenAllCategoryAndNoDate();
            tag = 0;
            break;
        }
        case R.id.btn_multidel:{
            spin_selectedId = (int) spinner.getSelectedItemId();
            btnStartText = b1.getText().toString();
            btnEndText = b2.getText().toString();
            Intent intent = new Intent(searchActivity.this, multidelActivity.class);
            intent.putExtra("spin", spinner.getSelectedItem().toString());
            intent.putExtra("start", btnStartText);
            intent.putExtra("end", btnEndText);
            startActivity(intent);
        }
        }
    }

    public void search(String d1, String d2, String category){
        if(d1.equals("开始时间") && d2.equals("结束时间")){
            if(category.equals("全部")){
                cursor = billDB.queryByDate(date_min, date_max);
                billAdapter billadapter1 = new billAdapter(searchActivity.this, cursor);
                lv_show_bill.setAdapter(billadapter1);
                setIncomeAndPayWhenAllCategoryAndSelectedDate(date_min, date_max);
            }
            else {
                cursor = billDB.queryByDateAndCategory(date_min, date_max, category);
                billAdapter billadapter1 = new billAdapter(searchActivity.this, cursor);
                lv_show_bill.setAdapter(billadapter1);
                setIncomeAndPayWhenNoAllCategoryAndSelectedDate(date_min, date_max, category);
            }
        }
        if(!d1.equals("开始时间") && d2.equals("结束时间")){
            String date1 = d1.replace("-", "");
            tag = 1;
            if(category.equals("全部")){
                cursor = billDB.queryByDate(date1, date_max);
                billAdapter billadapter1 = new billAdapter(searchActivity.this, cursor);
                lv_show_bill.setAdapter(billadapter1);
                setIncomeAndPayWhenAllCategoryAndSelectedDate(date1, date_max);
            }
            else{
                cursor = billDB.queryByDateAndCategory(date1, date_max, category);
                billAdapter billadapter1 = new billAdapter(searchActivity.this, cursor);
                lv_show_bill.setAdapter(billadapter1);
                setIncomeAndPayWhenNoAllCategoryAndSelectedDate(date1, date_max, category);
            }
        }
        else if(d1.equals("开始时间") && !d2.equals("结束时间")){
            String date2 = d2.replace("-", "");
            tag = 1;
            if(category.equals("全部")){
                cursor = billDB.queryByDate(date_min, date2);
                billAdapter billadapter1 = new billAdapter(searchActivity.this, cursor);
                lv_show_bill.setAdapter(billadapter1);
                setIncomeAndPayWhenAllCategoryAndSelectedDate(date_min, date2);
            }
            else{
                cursor = billDB.queryByDateAndCategory(date_min, date2, category);
                billAdapter billadapter1 = new billAdapter(searchActivity.this, cursor);
                lv_show_bill.setAdapter(billadapter1);
                setIncomeAndPayWhenNoAllCategoryAndSelectedDate(date_min, date2, category);
            }
        }
        else if(!d1.equals("开始时间") && !d2.equals("结束时间")){
            String date1 = d1.replace("-", "");
            String date2 = d2.replace("-", "");
            if(Integer.valueOf(date1) <= Integer.valueOf(date2)){
                tag = 1;
                if(category.equals("全部")){
                    cursor = billDB.queryByDate(date1, date2);
                    billAdapter billadapter1 = new billAdapter(searchActivity.this, cursor);
                    lv_show_bill.setAdapter(billadapter1);
                    setIncomeAndPayWhenAllCategoryAndSelectedDate(date1, date2);
                }
                else{
                    cursor = billDB.queryByDateAndCategory(date1, date2, category);
                    billAdapter billadapter1 = new billAdapter(searchActivity.this, cursor);
                    lv_show_bill.setAdapter(billadapter1);
                    setIncomeAndPayWhenNoAllCategoryAndSelectedDate(date1, date2, category);
                }
            }
            else {
                Toast.makeText(searchActivity.this, "您选择的时间段不正确，请确保开始日期小于结束日期", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void delete_bill(){
        AlertDialog.Builder builder = new Builder(this);
        builder.setTitle("确认删除此条账单吗？");
        final AlertDialog dialog = builder.create();
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                billDB.deleteByid(id);
                search(b1.getText().toString().replace("-", ""), b2.getText().toString().replace("-", ""), spinner.getSelectedItem().toString());
                Toast.makeText(searchActivity.this, "账单删除成功~", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });
        dialog.show();
    }

    public void setIncomeAndPay(){
        if(spinner.getSelectedItemId() == 0 && b1.getText().toString().equals("开始时间") && b2.getText().toString().equals("结束时间"))
            setIncomeAndPayWhenAllCategoryAndNoDate();
        else if(spinner.getSelectedItemId() != 0 && b1.getText().toString().equals("开始时间") && b2.getText().toString().equals("结束时间"))
            setIncomeAndPayWhenNoAllCategoryAndNoDate(spinner.getSelectedItem().toString());
        else if(spinner.getSelectedItemId() == 0 && !b1.getText().toString().equals("开始时间") && b2.getText().toString().equals("结束时间"))
            setIncomeAndPayWhenAllCategoryAndSelectedDate(b1.getText().toString().replace("-", ""), date_max);
        else if(spinner.getSelectedItemId() == 0 && b1.getText().toString().equals("开始时间") && !b2.getText().toString().equals("结束时间"))
            setIncomeAndPayWhenAllCategoryAndSelectedDate(date_min, b2.getText().toString().replace("-", ""));
        else if(spinner.getSelectedItemId() == 0 && !b1.getText().toString().equals("开始时间") && !b2.getText().toString().equals("结束时间"))
            setIncomeAndPayWhenAllCategoryAndSelectedDate(b1.getText().toString().replace("-", ""), b2.getText().toString().replace("-", ""));
        else if(spinner.getSelectedItemId() != 0 && !b1.getText().toString().equals("开始时间") && b2.getText().toString().equals("结束时间"))
            setIncomeAndPayWhenNoAllCategoryAndSelectedDate(b1.getText().toString().replace("-", ""), date_max, spinner.getSelectedItem().toString());
        else if(spinner.getSelectedItemId() != 0 && b1.getText().toString().equals("开始时间") && !b2.getText().toString().equals("结束时间"))
            setIncomeAndPayWhenNoAllCategoryAndSelectedDate(date_min, b2.getText().toString().replace("-", ""), spinner.getSelectedItem().toString());
        else if(spinner.getSelectedItemId() != 0 && !b1.getText().toString().equals("开始时间") && !b2.getText().toString().equals("结束时间"))
            setIncomeAndPayWhenNoAllCategoryAndSelectedDate(b1.getText().toString().replace("-", ""), b2.getText().toString().replace("-", ""), spinner.getSelectedItem().toString());
    }

    @Override
    public boolean onKeyDown(int keyCode,KeyEvent event){
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onRestart(){
        super.onRestart();
        search(b1.getText().toString().replace("-", ""), b2.getText().toString().replace("-", ""), spinner.getSelectedItem().toString());
    }
}
