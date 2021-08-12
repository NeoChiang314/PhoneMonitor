package com.example.phonemonitor;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MainService extends Service {

    int position;
    public static MainService instance;
    List<DataByTimeBean> dataByTimeBeans = new ArrayList<>();

    public static MainService getInstance() {
        return instance;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        instance = null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void updateDataByTimeList(DataByTimeBean dataByTimeBean) {
        dataByTimeBean.setPosition(dataByTimeBeans.size()+1);
        dataByTimeBeans.add(dataByTimeBean);
        if (MainActivity.getInstance() != null) {
            MainActivity.getInstance().updateDataByTimeView();
        }
    }
}
