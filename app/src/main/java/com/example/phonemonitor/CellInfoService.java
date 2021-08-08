package com.example.phonemonitor;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.core.app.ActivityCompat;
import androidx.annotation.Nullable;
import android.Manifest;
import android.content.pm.PackageManager;
import android.telephony.SignalStrength;
import android.content.Context;
import android.telephony.CellInfo;
import android.telephony.CellInfoLte;
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
    List<BeanCellInfo> beanCellInfos = new ArrayList<>();
    List<BeanCellInfo> lastBeanCellInfos = new ArrayList<>();

    public List<BeanCellInfo> getLastBeanCellInfos() {
        return lastBeanCellInfos;
    }

//    int cellRSRP;
//    String cellMcc;
//    String cellMnc;
//    int cellPci;
//    int cellTac;
//    int count;
//    int connection;

    public static CellInfoService getInstance () {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        count = 0;
        myPhoneStateListener = new MyPhoneStateListener();
        instance = this;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).listen(myPhoneStateListener, MyPhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        cellInfoList.clear();
        beanCellInfos.clear();
        instance = null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private class MyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
//            count ++;
            getGeneralCellInfo();
        }
    }

    public void getGeneralCellInfo() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.getInstance(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MainActivity.FINE_LOCATION_REQUEST);
        }
        else{
            beanCellInfos.clear();
            try{
                cellInfoList = this.telephonyManager.getAllCellInfo();
                for (CellInfo cellInfo : cellInfoList) {
//                    if(cellInfo.getCellConnectionStatus() == CellInfo.CONNECTION_PRIMARY_SERVING || cellInfo.getCellConnectionStatus() == CellInfo.CONNECTION_SECONDARY_SERVING){
                        if (cellInfo instanceof CellInfoLte) {
                            BeanCellInfo beanCellInfo = new BeanCellInfo();
                            beanCellInfo.setCellRSRP(((CellInfoLte) cellInfo).getCellSignalStrength().getRsrp());
                            beanCellInfo.setCellMcc(((CellInfoLte) cellInfo).getCellIdentity().getMccString());
                            beanCellInfo.setCellMnc(((CellInfoLte) cellInfo).getCellIdentity().getMncString());
                            beanCellInfo.setCellPci(((CellInfoLte) cellInfo).getCellIdentity().getPci());
                            beanCellInfo.setCellTac(((CellInfoLte) cellInfo).getCellIdentity().getTac());
                            beanCellInfo.setConnectionCode(((CellInfoLte) cellInfo).getCellConnectionStatus());
                            beanCellInfos.add(beanCellInfo);
//                            cellRSRP = ((CellInfoLte) cellInfo).getCellSignalStrength().getRsrp();
//                            cellMcc = ((CellInfoLte) cellInfo).getCellIdentity().getMccString();
//                            cellMnc = ((CellInfoLte) cellInfo).getCellIdentity().getMncString();
//                            cellPci = ((CellInfoLte) cellInfo).getCellIdentity().getPci();
//                            cellTac = ((CellInfoLte) cellInfo).getCellIdentity().getTac();
//                            connection = cellInfo.getCellConnectionStatus();
                        }
//                    }
                }
                instance.lastBeanCellInfos = beanCellInfos;
                MainActivity.getInstance().showCellData(beanCellInfos);
            } catch (Exception e) {
                Toast.makeText(CellInfoService.this, "Phone states measurement failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}