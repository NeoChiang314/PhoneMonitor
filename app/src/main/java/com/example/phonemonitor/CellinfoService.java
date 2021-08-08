package com.example.phonemonitor;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.core.app.ActivityCompat;

import androidx.annotation.Nullable;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SignalStrength;
import android.util.Log;
import android.widget.TextView;
import android.content.Context;
import android.telephony.CellInfo;
import android.telephony.CellInfoLte;
import android.telephony.TelephonyManager;
import android.telephony.PhoneStateListener;
import android.widget.Toast;

import java.util.List;

public class CellinfoService extends Service {

    public static CellinfoService instance = null;
    TelephonyManager telephonyManager;
    MyPhoneStateListener myPhoneStateListener;
    int cellRSRP;
    String cellMcc;
    String cellMnc;
    int cellPci;
    int cellTac;
    int count;
    List<CellInfo> cellInfoList;

    public static CellinfoService getInstance (){
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        count = 0;
        myPhoneStateListener = new MyPhoneStateListener();
        instance = this;
//        CellinfoService.MyPhoneStateListener myPhoneStateListener = new CellinfoService.MyPhoneStateListener();
//        this.telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//        ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).listen(myPhoneStateListener, CellinfoService.MyPhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).listen(myPhoneStateListener, MyPhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        return super.onStartCommand(intent, flags, startId);
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
            count ++;
            Toast.makeText(CellinfoService.this, "Signal strength changed", Toast.LENGTH_SHORT).show();
            getGeneralCellInfo();
        }
    }

    public void getGeneralCellInfo() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.getInstance(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MainActivity.FINE_LOCATION_REQUEST);
        }
        else{
            try{
                cellInfoList = this.telephonyManager.getAllCellInfo();
                for (CellInfo cellInfo : cellInfoList) {
//                    if(cellInfo.getCellConnectionStatus() == CellInfo.CONNECTION_PRIMARY_SERVING || cellInfo.getCellConnectionStatus() ==CellInfo.CONNECTION_SECONDARY_SERVING){
                        if (cellInfo instanceof CellInfoLte) {
                            cellRSRP = ((CellInfoLte) cellInfo).getCellSignalStrength().getRsrp();
                            cellMcc = ((CellInfoLte) cellInfo).getCellIdentity().getMccString();
                            cellMnc = ((CellInfoLte) cellInfo).getCellIdentity().getMncString();
                            cellPci = ((CellInfoLte) cellInfo).getCellIdentity().getPci();
                            cellTac = ((CellInfoLte) cellInfo).getCellIdentity().getTac();
                            MainActivity.getInstance().showCellData(cellRSRP, cellMcc, cellMnc, cellPci, cellTac, count);
                        }
//                    }
                }
            } catch (Exception e) {
                Toast.makeText(CellinfoService.this, "Phone states measurement failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
