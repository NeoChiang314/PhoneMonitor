package com.example.phonemonitor;

import android.app.Service;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.IBinder;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MainService extends Service {

    int position;
    public static MainService instance;
    List<DataByTimeBean> dataByTimeBeans = new ArrayList<>();
    List<CellInfoBean> cellInfoBeans = new ArrayList<>();

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

//        SQLiteOpenHelper helper = MySQLiteOpenHelper.getInstance(this);
//        SQLiteDatabase writableDatabase = helper.getWritableDatabase();
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

//    public void setCellInfoBeans (List<CellInfoBean> cellInfoBeans){
//        this.cellInfoBeans = cellInfoBeans;
//    }

    public void updateDataByTimeList() {
        DataByTimeBean dataByTimeBean = new DataByTimeBean(GpsService.getInstance().longitude, GpsService.getInstance().latitude, CellInfoService.getInstance().getLastCellInfoBeans());
        dataByTimeBean.setPosition(dataByTimeBeans.size()+1);
        dataByTimeBeans.add(dataByTimeBean);
        insert(dataByTimeBean);
        if (MainActivity.getInstance() != null) {
            MainActivity.getInstance().updateDataByTimeView();
        }
    }

    public void insert (DataByTimeBean dataByTimeBean) {
        SQLiteOpenHelper helper = MySQLiteOpenHelper.getInstance(this);
        SQLiteDatabase writableDatabase = helper.getWritableDatabase();

        if (writableDatabase.isOpen()) {
            CellInfoBean maxCellInfoBean = getMaxSignalCell(dataByTimeBean);
            String sql = "insert into data (time, longitude, latitude, RSRP, RSRQ, PCI) values (" + "'" + dataByTimeBean.getCurrentTime() + "','" + dataByTimeBean.getLongitude() + "','" + dataByTimeBean.getLatitude()
                    + "','" + maxCellInfoBean.getCellRSRP() + "','" + maxCellInfoBean.getCellRSRQ() + "','" + maxCellInfoBean.getCellPci() +  "')";
            writableDatabase.execSQL(sql);
        }
        writableDatabase.close();
    }

    public CellInfoBean getMaxSignalCell (DataByTimeBean dataByTimeBean){
        int maxRSRP = -1000;
        int maxRSRQ = -1000;
        CellInfoBean maxCellInfoBean = null;
//        int maxIndex = 0;
//        int index = 0;
        for (CellInfoBean cellInfoBean: dataByTimeBean.cellInfoBeans){
            if (cellInfoBean.getCellRSRP() > maxRSRP){
//                maxIndex = index;
                maxRSRP = cellInfoBean.getCellRSRP();
                maxRSRQ = cellInfoBean.getCellRSRQ();
                maxCellInfoBean = cellInfoBean;
            }
            else if (cellInfoBean.getCellRSRP() == maxRSRP){
                if (cellInfoBean.getCellRSRQ() > maxRSRQ){
//                    maxIndex = index;
                    maxRSRP = cellInfoBean.getCellRSRP();
                    maxRSRQ = cellInfoBean.getCellRSRQ();
                    maxCellInfoBean = cellInfoBean;
                }
            }
//            index ++;
        }
//        return dataByTimeBean.cellInfoBeans.get(maxIndex);
        return maxCellInfoBean;
    }
}
