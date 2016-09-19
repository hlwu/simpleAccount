package com.example.myaccount;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class addbillActivity extends Activity implements OnClickListener{
    private static final int SHOW_DATAPICK= 0; 
    private static final int DATE_DIALOG_ID = 1;  
    private static final int SHOW_TIMEPICK = 2;
    private final int INCOME = 1;
    private final int PAY = 2;
    private EditText et_money, et_date, et_remark, et_name;
    private Button btn_select_date, btn_save, btn_addagain;
    private RadioGroup rg;
    private RadioButton rb1, rb2;
    private Spinner spinner;
    private ArrayAdapter<AccountType> arrayadapter;
    private billDBHelper billDBH;

    private int mYear;  
    private int mMonth;
    private int mDay; 
    private String date;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addbill);

        billDBH = new billDBHelper(this);
        et_money = (EditText) findViewById(R.id.et_money);
        et_date = (EditText) findViewById(R.id.et_date);
        et_remark = (EditText) findViewById(R.id.et_remark);
        et_name = (EditText) findViewById(R.id.et_name);
        btn_select_date = (Button) findViewById(R.id.btn_select_date);
        btn_save = (Button) findViewById(R.id.btn_save);
        btn_save.setOnClickListener(this);
        btn_addagain = (Button) findViewById(R.id.btn_addagain);
        btn_addagain.setOnClickListener(this);
        rg = (RadioGroup) findViewById(R.id.radioGroup1);
        rb1 = (RadioButton) findViewById(R.id.rbpay);
        rb2 = (RadioButton) findViewById(R.id.rbincome);
        rg.check(R.id.rbpay);
        arrayadapter = new ArrayAdapter<AccountType>(this,android.R.layout.simple_spinner_item,getAccountTypeList());
        arrayadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner = (Spinner) findViewById(R.id.spinType);
        spinner.setAdapter(arrayadapter);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                    int arg2, long arg3) {
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }});
        spinner.setVisibility(View.VISIBLE);

        btn_select_date.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View arg0) {
                Message msg = new Message();   
                msg.what = addbillActivity.SHOW_DATAPICK;  
                addbillActivity.this.dateandtimeHandler.sendMessage(msg); 
            }
        });
        setDateTime(); 
        et_money.addTextChangedListener(new TextWatcher() {
            public boolean inChanged = false;
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
            }
            @Override
            public void afterTextChanged(Editable arg0) {
                if(inChanged){
                    return;
                }
                String s = arg0.toString();
                inChanged = true;
                String str = "";
                int k = s.length();
                for(int i = 0; i < k; i++){
                    if(s.charAt(i) == '.'){
                        if(i == 0){
                            str += "0";
                        }
                        int j = s.length() - i - 1;
                        if(j > 2){
                            k -= j - 2;
                        }
                    }
                    str += s.charAt(i);
                }
                et_money.setText(str);
                et_money.setSelection(et_money.length());
                inChanged = false;
            }
        });
    }

    @Override
    public void onClick(View arg0) {
        switch(arg0.getId()){
        case R.id.btn_save:{
            saveBill();
            break;
        }
        case R.id.btn_addagain:{
            save_and_again();
            break;
        }
        }
    }

    public void save_and_again(){
        if(et_money.getText().toString().equals("") || et_date.getText().toString().equals("") || rg.getCheckedRadioButtonId() == -1 || et_name.getText().toString().equals("")){
            final AlertDialog b = new AlertDialog.Builder(addbillActivity.this).setMessage("请确保名称和金额信息已经填写!").show();
            Handler h = new Handler();
            h.postDelayed(new Runnable(){
                @Override
                public void run(){
                    b.dismiss();
                }
            }, 1000);
        }
        else{
            String date = et_date.getText().toString().replace("-", "");
            String money = "";
            if(et_money.getText().charAt(et_money.getText().length()-1) == '.'){
                for(int i = 0; i < et_money.getText().length() - 1; i++){
                    money += et_money.getText().charAt(i);
                }
            }
            else money = et_money.getText().toString();
            if(rg.getCheckedRadioButtonId() == R.id.rbincome)
                billDBH.addItem(INCOME, money, spinner.getSelectedItem().toString(), date, et_remark.getText().toString(), et_name.getText().toString());
            else billDBH.addItem(PAY, money, spinner.getSelectedItem().toString(), date, et_remark.getText().toString(), et_name.getText().toString());
            finish();
            Intent intent = new Intent(addbillActivity.this, addbillActivity.class);
            startActivity(intent);
            Toast.makeText(addbillActivity.this, "新一笔帐添加成功~", Toast.LENGTH_SHORT).show();
        }
    }

    public void saveBill(){
        if(et_money.getText().toString().equals("") || et_date.getText().toString().equals("") || rg.getCheckedRadioButtonId() == -1 || et_name.getText().toString().equals("")){
            final AlertDialog b = new AlertDialog.Builder(addbillActivity.this).setMessage("请确保名称和金额信息已经填写!").show();
            Handler h = new Handler();
            h.postDelayed(new Runnable(){
                @Override
                public void run(){
                    b.dismiss();
                }
            }, 1000);
        }
        else{
            String date = et_date.getText().toString().replace("-", "");
            String money = "";
            if(et_money.getText().charAt(et_money.getText().length()-1) == '.'){
                for(int i = 0; i < et_money.getText().length() - 1; i++){
                    money += et_money.getText().charAt(i);
                }
            }
            else money = et_money.getText().toString();
            if(rg.getCheckedRadioButtonId() == R.id.rbincome)
                billDBH.addItem(INCOME, money, spinner.getSelectedItem().toString(), date, et_remark.getText().toString(), et_name.getText().toString());
            else billDBH.addItem(PAY, money, spinner.getSelectedItem().toString(), date, et_remark.getText().toString(), et_name.getText().toString());
            finish();
            Toast.makeText(addbillActivity.this, "新一笔帐添加成功~", Toast.LENGTH_SHORT).show();
        }
    }

    Handler dateandtimeHandler = new Handler() {
        @Override  
        public void handleMessage(Message msg) {  
            switch (msg.what) {  
            case addbillActivity.SHOW_DATAPICK:  
                showDialog(DATE_DIALOG_ID);  
                break; 
            }  
        }  
    }; 

    @Override
    protected Dialog onCreateDialog(int id){
        switch (id) {
        case DATE_DIALOG_ID:
            return new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);
        }
        return null;
    }
    
    @Override  
    protected void onPrepareDialog(int id, Dialog dialog) {  
        switch (id) {  
        case DATE_DIALOG_ID:  
            ((DatePickerDialog) dialog).updateDate(mYear, mMonth, mDay);  
            break;
        }
    }  

    private void setDateTime(){
        final Calendar c = Calendar.getInstance();  
        mYear = c.get(Calendar.YEAR);  
        mMonth = c.get(Calendar.MONTH);  
        mDay = c.get(Calendar.DAY_OF_MONTH); 
        String month = String.valueOf((mMonth + 1) < 10 ? "0" + (mMonth + 1) : (mMonth + 1));
        String day = String.valueOf((mDay < 10) ? "0" + mDay : mDay);
        date = String.valueOf(mYear) +month + day;

        updateDateDisplay(et_date); 
    }

    private void updateDateDisplay(EditText showDate){
        String month = String.valueOf((mMonth + 1) < 10 ? "0" + (mMonth + 1) : (mMonth + 1));
        String day = String.valueOf((mDay < 10) ? "0" + mDay : mDay);
        String d = String.valueOf(mYear) +month + day;
        if(Integer.valueOf(d) > Integer.valueOf(date)){
            Toast.makeText(addbillActivity.this, "注意：选择的时间大于实际时间！！", Toast.LENGTH_SHORT).show();
        }
        showDate.setText(new StringBuilder().append(mYear).append("-")
                .append((mMonth + 1) < 10 ? "0" + (mMonth + 1) : (mMonth + 1)).append("-")
                .append((mDay < 10) ? "0" + mDay : mDay)); 
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {  
        public void onDateSet(DatePicker view, int year, int monthOfYear,  
                int dayOfMonth) {  
            mYear = year;  
            mMonth = monthOfYear;  
            mDay = dayOfMonth;  

            updateDateDisplay(et_date);
        }  
    }; 

    public List<AccountType>  getAccountTypeList(){
        List<AccountType> accountTypes = new ArrayList<AccountType>();    
        categoryDBHelper categoryDB = new categoryDBHelper(getApplicationContext());

        Cursor cursor = categoryDB.queryType();
        if(cursor.getCount() == 0)
            Toast.makeText(addbillActivity.this, "目前没有可用的帐务类别，请先设置帐务类别", Toast.LENGTH_SHORT).show();
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
    public boolean onKeyDown(int keyCode,KeyEvent event){
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

