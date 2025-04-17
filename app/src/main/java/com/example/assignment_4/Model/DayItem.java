package com.example.assignment_4.Model;

import com.example.assignment_4.R;

import java.lang.reflect.Field;

public class DayItem {
    private String daytime,hightp,lawtp,con,prep,uv,mortemp,noontemp,evetemp,nighttemp,icon;
    private double temp;

    public DayItem(String daytime, String hightp, String lawtp, String con, String prep, String uv, String mortemp, String noontemp, String evetemp, String nighttemp, String icon, double temp) {
        this.daytime = daytime;
        this.hightp = hightp;
        this.lawtp = lawtp;
        this.con = con;
        this.prep = prep;
        this.uv = uv;
        this.mortemp = mortemp;
        this.noontemp = noontemp;
        this.evetemp = evetemp;
        this.nighttemp = nighttemp;
        this.icon = icon;
        this.temp = temp;
    }

    public String getDaytime() {
        return daytime;
    }

    public String getHightp() {
        return hightp;
    }

    public String getLawtp() {
        return lawtp;
    }

    public String getCon() {
        return con;
    }

    public String getPrep() {
        return "(" + prep + "% precip.)" ;
    }

    public String getUv() {
        return "UV Index: " + uv;
    }

    public String getMortemp() {
        return mortemp;
    }

    public String getNoontemp() {
        return noontemp;
    }

    public String getEvetemp() {
        return evetemp;
    }

    public String getNighttemp() {
        return nighttemp;
    }

    public String getIcon() {
        return icon;
    }

    public double getTemp() {
        return temp;
    }

    public int getId(String s) {
        try {
            Field idField = R.drawable.class.getDeclaredField(icon.replace("-", "_"));
            return idField.getInt(idField);
        } catch (Exception e) {
            return 0;
        }
    }

}
