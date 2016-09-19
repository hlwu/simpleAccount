package com.example.myaccount;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class modifybillActivity extends Activity implements OnClickListener{
    private static final int SHOW_DATAPICK= 0; 
    private static final int DATE_DIALOG_ID = 1;  
    private static final int SHOW_TIMEPICK = 2;
    private EditText et_money, et_date, et_remark, et_name;
    private Button btn_select_date, btn_modify;
    private RadioGroup rg;
    private RadioButton rb1, rb2;
    private Spinner spinner;
    private ArrayAdapter<AccountType> arrayadapter;
    private billDBHelper billDBH;
    private int id;
    private String start,end;
    private int spin;
    private final int INCOME = 1;
    private final int PAY = 2;

    private int mYear;  
    private int mMonth;
    private int mDay; 
    private String date;
    
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_bill);

        billDBH = new billDBHelper(this);
        et_money = (EditText) findViewById(R.id.et_money_modify);
        et_date = (EditText) findViewById(R.id.et_date_modify);
        et_remark = (EditText) findViewById(R.id.et_remark_modify);
        et_name = (EditText) findViewById(R.id.et_name_modify);
        btn_select_date = (Button) findViewById(R.id.btn_select_date_modify);
        btn_modify = (Button) findViewById(R.id.btn_modify);
        btn_modify.setOnClickListener(this);
        rg = (RadioGroup) findViewById(R.id.rg_modify);
        rb1 = (RadioButton) findViewById(R.id.rbpay_modify);
        rb2 = (RadioButton) findViewById(R.id.rbincome_modify);
        arrayadapter = new ArrayAdapter<AccountType>(this,android.R.layout.simple_spinner_item,getAccountTypeList());
        arrayadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner = (Spinner) findViewById(R.id.spinType_modify);
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
                msg.what = modifybillActivity.SHOW_DATAPICK;  
                modifybillActivity.this.dateandtimeHandler.sendMessage(msg); 
            }
        });

        setDateTime(); 

        Bundle bundle = getIntent().getExtras();
        et_money.setText(bundle.getString("money"));
        String s = bundle.getString("date").substring(0, 4) + "-" + bundle.getString("date").substring(4, 6) + "-" + bundle.getString("date").substring(6, 8);
        et_date.setText(s);
        et_remark.setText(bundle.getString("remark"));
        et_name.setText(bundle.getString("name"));
        if(bundle.getInt("choice") == INCOME)
            rg.check(R.id.rbincome_modify);
        else rg.check(R.id.rbpay_modify);
        for(int i = 0; i < spinner.getCount(); i++){
            if(bundle.getString("category").equals(spinner.getItemAtPosition(i).toString())){
                spinner.setSelection(i);
            }
        }
        id = bundle.getInt("id");
        spin=bundle.getInt("spin");
        start=bundle.getString("start");
        end=bundle.getString("end");
        
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
        case R.id.btn_modify:{
            modifyBill();
            break;
        }
        }
    }
    
    public void modifyBill(){
        if(et_money.getText().toString().equals("") || et_date.getText().toString().equals("") || rg.getCheckedRadioButtonId() == -1 || et_name.getText().toString().equals("")){
            final AlertDialog b = new AlertDialog.Builder(modifybillActivity.this).setMessage("请确保名称和金额信息已经填写!").show();
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
            if(rg.getCheckedRadioButtonId() == R.id.rbincome_modify)
                billDBH.modify(INCOME, money, spinner.getSelectedItem().toString(), date, et_remark.getText().toString(), id, et_name.getText().toString());
            else billDBH.modify(PAY, money, spinner.getSelectedItem().toString(), date, et_remark.getText().toString(), id, et_name.getText().toString());
//            finish();                                         //这三行是保存后返回主界面的代码
//            Intent intent = new Intent(modifybillActivity.this, MainActivity.class);
//            startActivity(intent);
            Intent intent = new Intent(modifybillActivity.this, searchActivity.class);               //这几行是保存后返回查询页面的代码
            intent.putExtra("spinner", spin);
            intent.putExtra("start", start);
            intent.putExtra("end", end);
            finish();
            startActivity(intent);
            Toast.makeText(modifybillActivity.this, "账单更新成功~", Toast.LENGTH_SHORT).show();
        }
    }

    Handler dateandtimeHandler = new Handler() {
        @Override  
        public void handleMessage(Message msg) {  
            switch (msg.what) {  
            case modifybillActivity.SHOW_DATAPICK:  
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
            Toast.makeText(modifybillActivity.this, "注意：选择的时间大于实际时间！！", Toast.LENGTH_SHORT).show();
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

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String strTypeName = cursor.getString(1);
            AccountType accountType = new AccountType();  
            accountType.TypeId = id+"";
            accountType.TypeName = strTypeName;
            accountTypes.add(accountType); 
        }
        cursor.close();
        return accountTypes;
    }

    @Override
    public boolean onKeyDown(int keyCode,KeyEvent event){
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(modifybillActivity.this, searchActivity.class);
            intent.putExtra("spinner", spin);
            intent.putExtra("start", start);
            intent.putExtra("end", end);
            finish();
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
