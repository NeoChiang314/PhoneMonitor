package com.example.phonemonitor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.Intent;
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

import java.security.Provider;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static MainActivity instance;
    public static final int FINE_LOCATION_REQUEST = 1;
//    TelephonyManager telephonyManager;
//    int tempCellRSRP;
//    String tempCellMcc;
//    String tempCellMnc;
//    int tempCellPci;
//    int tempCellTac;
//    List<CellInfo> cellInfoList;
    TextView rsrpText, mccText, mncText, pciText, tacText, countText;

    public static MainActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.activity_main);
        rsrpText = findViewById(R.id.rsrpView);
        mccText = findViewById(R.id.mccView);
        mncText = findViewById(R.id.mncView);
        pciText = findViewById(R.id.pciView);
        tacText = findViewById(R.id.tacView);
        countText = findViewById(R.id.countView);

//        MyPhoneStateListener myPhoneStateListener = new MyPhoneStateListener();
//        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//        ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).listen(myPhoneStateListener, MyPhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

        if (CellinfoService.getInstance() == null) {
            rsrpText.setText("RSRP value: null");
            mccText.setText("Cell Mcc: null");
            mncText.setText("Cell Mnc: null");
            pciText.setText("Cell Pci: null");
            tacText.setText("Cell Tac: null");
            Intent intent = new Intent();
            intent.setClass(this, CellinfoService.class);
            this.startService(intent);
        }
        else{
            rsrpText.setText("RSRP value: " + String.valueOf(CellinfoService.getInstance().cellRSRP));
            mccText.setText("Cell Mcc: " + CellinfoService.getInstance().cellMcc);
            mncText.setText("Cell Mnc: " + CellinfoService.getInstance().cellMnc);
            pciText.setText("Cell Pci: " + String.valueOf(CellinfoService.getInstance().cellPci));
            tacText.setText("Cell Tac: " + String.valueOf(CellinfoService.getInstance().cellTac));
            countText.setText("Change times: " + String.valueOf(CellinfoService.getInstance().count));
        }
    }

//    private class MyPhoneStateListener extends PhoneStateListener {
//        @Override
//        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
//            super.onSignalStrengthsChanged(signalStrength);
//            getGeneralCellInfo();
//        }
//    }

//    public void getGeneralCellInfo () {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_REQUEST);
//        } else {
//            try {
//                cellInfoList = telephonyManager.getAllCellInfo();
//                for (CellInfo cellInfo : cellInfoList) {
//                    if (cellInfo instanceof CellInfoLte) {
//                        cellRSRP = ((CellInfoLte) cellInfo).getCellSignalStrength().getRsrp();
//                        cellMcc = ((CellInfoLte) cellInfo).getCellIdentity().getMccString();
//                        cellMnc = ((CellInfoLte) cellInfo).getCellIdentity().getMncString();
//                        cellPci = ((CellInfoLte) cellInfo).getCellIdentity().getPci();
//                        cellTac = ((CellInfoLte) cellInfo).getCellIdentity().getTac();
//                    }
//                }
//            } catch (Exception e) {
//                Toast.makeText(this, "Phone states measurement failed", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

    public void showCellData (CellinfoService instance) {
        rsrpText.setText("RSRP value: " + String.valueOf(instance.cellRSRP));
        mccText.setText("Cell Mcc: " + instance.cellMcc);
        mncText.setText("Cell Mnc: " + instance.cellMnc);
        pciText.setText("Cell Pci: " + String.valueOf(instance.cellPci));
        tacText.setText("Cell Tac: " + String.valueOf(instance.cellTac));
        countText.setText("Change times: " + String.valueOf(instance.count));
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_LOCATION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Fine Location Permission Granted", Toast.LENGTH_SHORT).show();
                CellinfoService.getInstance().getGeneralCellInfo();
            } else {
                Toast.makeText(MainActivity.this, "Fine Location Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}