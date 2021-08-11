package com.example.phonemonitor;

import java.util.Calendar;
import java.util.List;

public class DataByTimeBean {

    String currentTime;
    double longitude;
    double latitude;
    List<CellInfoBean> cellInfoBeans;

    public DataByTimeBean(double longitude, double latitude){
        setTime();
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public void setTime(){
        this.currentTime = String.valueOf(Calendar.getInstance().get(Calendar.YEAR)) + "/" + String.valueOf(Calendar.getInstance().get(Calendar.MONTH) + 1) + "/" + String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        + "\n" + String.valueOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) + ":" + String.valueOf(Calendar.getInstance().get(Calendar.MINUTE)) + ":" + String.valueOf(Calendar.getInstance().get(Calendar.SECOND))) ;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(int longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(int latitude) {
        this.latitude = latitude;
    }
}