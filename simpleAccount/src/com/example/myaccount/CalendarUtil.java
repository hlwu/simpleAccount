package com.example.myaccount;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import android.util.Log;

public class CalendarUtil {
    private int weeks = 0;// 用来全局控制 上一周，本周，下一周的周数变化  
    private int MaxDate; // 一月最大天数  
    private int MaxYear; // 一年最大天数  
    private int getMondayPlus() {  
        Calendar cd = Calendar.getInstance();  
        // 获得今天是一周的第几天，星期日是第一天，星期二是第二天......  
        int dayOfWeek = cd.get(Calendar.DAY_OF_WEEK) - 1; // 因为按中国礼拜一作为第一天所以这里减1  
        if (dayOfWeek == 1) {  
            return 0;  
        } else {  
            return 1 - dayOfWeek;  
        }  
    }  
    public String getTodayDay(){
        GregorianCalendar currentDate = new GregorianCalendar();  
        int d = currentDate.get(Calendar.DAY_OF_MONTH);
        return String.valueOf(d);  
    }
    //获得本周一日期
    public String getMondayOFWeek() {  
        weeks = 0;  
        int mondayPlus = this.getMondayPlus();  
        Calendar currentDate = Calendar.getInstance();
        currentDate.add(Calendar.DATE, mondayPlus);  
        Date monday = currentDate.getTime();  
        DateFormat df =new SimpleDateFormat("yyyy-MM-dd");  
        String preMonday = df.format(monday);  
        return preMonday;  
    } 
    //获得本周末日期
    public String getCurrentWeekday() {  
        weeks = 0;  
        int mondayPlus = this.getMondayPlus();  
        GregorianCalendar currentDate = new GregorianCalendar();  
        currentDate.add(GregorianCalendar.DATE, mondayPlus + 6);  
        Date monday = currentDate.getTime();  
        DateFormat df =new SimpleDateFormat("yyyy-MM-dd");  
        String preMonday = df.format(monday);  
        return preMonday;  
    }  
    //获得本月最后一天
    public String getDefaultDay() {  
        String str = "";  
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
        Calendar lastDate = Calendar.getInstance();  
        lastDate.set(Calendar.DATE, 1);// 设为当前月的1号  
        lastDate.add(Calendar.MONTH, 1);// 加一个月，变为下月的1号  
        lastDate.add(Calendar.DATE, -1);// 减去一天，变为当月最后一天  
        str = sdf.format(lastDate.getTime());  
        return str;  
    } 
    //获得本月第一天
    public String getFirstDayOfMonth() {  
        String str = "";  
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
        Calendar lastDate = Calendar.getInstance();  
        lastDate.set(Calendar.DATE, 1);// 设为当前月的1号  
        str = sdf.format(lastDate.getTime());  
        return str;  
    } 
    public String getToday(){
        GregorianCalendar currentDate = new GregorianCalendar();  
        Date today = currentDate.getTime();  
        DateFormat df =new SimpleDateFormat("yyyy-MM-dd");  
        String preMonday = df.format(today);  
        return preMonday; 
    }
    //获得本年第一天
    public String getCurrentYearFirst() {  
        int yearPlus = this.getYearPlus();  
        GregorianCalendar currentDate = new GregorianCalendar();  
        currentDate.add(GregorianCalendar.DATE, yearPlus);  
        Date yearDay = currentDate.getTime();  
        DateFormat df =new SimpleDateFormat("yyyy-MM-dd");  
        String preYearDay = df.format(yearDay);  
        return preYearDay;   
    }  
    //获得本年最后一天
    public String getCurrentYearEnd() {  
        Date date = new Date();  
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");// 可以方便地修改日期格式  
        String years = dateFormat.format(date);  
        return years + "-12-31";  
    }  
    private int getYearPlus() {  
        Calendar cd = Calendar.getInstance();  
        int yearOfNumber = cd.get(Calendar.DAY_OF_YEAR);// 获得当天是一年中的第几天  
        cd.set(Calendar.DAY_OF_YEAR, 1);// 把日期设为当年第一天  
        cd.roll(Calendar.DAY_OF_YEAR, -1);// 把日期回滚一天。  
        int MaxYear = cd.get(Calendar.DAY_OF_YEAR);  
        if (yearOfNumber == 1) {  
            return -MaxYear;  
        } else {  
            return 1 - yearOfNumber;  
        }  
    }  
}
