package com.example.assignment_4.Model;

import com.example.assignment_4.R;

import java.lang.reflect.Field;

public class HourItem {
    private String day,time,icon,type,temp;

    public HourItem(String day, String time, String icon, String type, String temp) {
        this.day = day;
        this.time = time;
        this.icon = icon;
        this.type = type;
        this.temp = temp;
    }

    public String getTemp() {
        return temp;
    }
    public String getDay() {
        return day;
    }

    public String getTime() {
        return time;
    }

    public String getIcon() {
        return icon;
    }

    public String getType() {
        return type;
    }

    public static int getId(String s) {
        try {
            Field idField = R.drawable.class.getDeclaredField(s.replace("-", "_"));
            return idField.getInt(idField);
        } catch (Exception e) {
            return 0;
        }
    }

}
