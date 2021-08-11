package com.example.phonemonitor;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MainService extends Service {

    public static MainService instance;
    List<DataByTimeBean> dataByTimeBeans = new ArrayList<>();
    List<DataByTimeBean> updateDataByTimeBeans = new ArrayList<>();

    public static MainService getInstance() {
        return instance;
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
        dataByTimeBeans.add(dataByTimeBean);

//        if (MainActivity.getInstance() != null) {
//            MainActivity.getInstance().updateDataByTimeView(dataByTimeBean);
//        }

        if (MainActivity.getInstance() != null) {
            if (updateDataByTimeBeans != null) {
                for (DataByTimeBean dataByTimeBean1 : updateDataByTimeBeans){
                    MainActivity.getInstance().updateDataByTimeView(dataByTimeBean1);
                }
                updateDataByTimeBeans.clear();
            }
            MainActivity.getInstance().updateDataByTimeView(dataByTimeBean);
        }
        else {
            updateDataByTimeBeans.add(dataByTimeBean);
        }

    }
}
