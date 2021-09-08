package com.example.phonemonitor;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import androidx.core.app.ActivityCompat;
import androidx.annotation.Nullable;
import android.Manifest;
import android.content.pm.PackageManager;
import android.telephony.SignalStrength;
import android.content.Context;
import android.telephony.CellInfo;
import android.telephony.TelephonyManager;
import android.telephony.PhoneStateListener;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class CellInfoService extends Service {

    public static CellInfoService instance = null;
    TelephonyManager telephonyManager;
    MyPhoneStateListener myPhoneStateListener;
    List<CellInfo> cellInfoList = new ArrayList<>();

    public List<CellInfoBean> getCellInfoBeans() {
        return cellInfoBeans;
    }

    List<CellInfoBean> cellInfoBeans = new ArrayList<>();
//    List<CellInfoBean> lastCellInfoBeans = new ArrayList<>();
//    final int delay = 1000;
//    final Handler handler = new Handler();

//    public List<CellInfoBean> getLastCellInfoBeans() {
//        return lastCellInfoBeans;
//    }

    public static CellInfoService getInstance () {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        Toast.makeText(CellInfoService.this, "Create", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
//        Toast.makeText(CellInfoService.this, "Start", Toast.LENGTH_SHORT).show();
        instance = this;
//        myPhoneStateListener = new MyPhoneStateListener();
//        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//        ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).listen(myPhoneStateListener, MyPhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

//        handler.postDelayed(new Runnable() {
//            public void run() {
//                getGeneralCellInfo();
//                handler.postDelayed(this, delay);
//            }
//        }, delay);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
//        Toast.makeText(CellInfoService.this, "Destroy", Toast.LENGTH_SHORT).show();
//        ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).listen(myPhoneStateListener, MyPhoneStateListener.LISTEN_NONE);
        cellInfoList.clear();
        cellInfoBeans.clear();
        instance = null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public class MyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            getGeneralCellInfo();
        }
    }

    public void getGeneralCellInfo() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).listen(myPhoneStateListener, MyPhoneStateListener.LISTEN_NONE);
            ActivityCompat.requestPermissions(CellInfoActivity.getInstance(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, CellInfoActivity.FINE_LOCATION_REQUEST);
        }
        else{
            cellInfoBeans.clear();
            try{
                cellInfoList = this.telephonyManager.getAllCellInfo();
                for (CellInfo cellInfo : cellInfoList) {
                    CellInfoBean cellInfoBean = new CellInfoBean();
                    cellInfoBean.loadCellInfo(cellInfo);
                    cellInfoBeans.add(cellInfoBean);
                }
//                lastCellInfoBeans = cellInfoBeans;

//                if (GpsService.getInstance() != null) {
//                    MainService.getInstance().updateDataByTimeList(new DataByTimeBean(GpsService.getInstance().longitude, GpsService.getInstance().latitude));
//                }
//                MainService.getInstance().setCellInfoBeans(cellInfoBeans);

                if (CellInfoActivity.getInstance() != null) {
                    CellInfoActivity.getInstance().setCellInfoBeans(cellInfoBeans);
                    CellInfoActivity.getInstance().showCellData(cellInfoBeans);
                }
            } catch (Exception e) {
                Toast.makeText(CellInfoService.this, "Phone states measurement failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
