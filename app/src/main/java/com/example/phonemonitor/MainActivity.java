package com.example.phonemonitor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
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

public class MainActivity extends AppCompatActivity {

    public static final int FINE_LOCATION_REQUEST = 1;
    TelephonyManager telephonyManager;
    int cellRSRP;
    String cellMcc;
    String cellMnc;
    int cellPci;
    int cellTac;
    List<CellInfo> cellInfoList;
    TextView rsrpText, mccText, mncText, pciText, tacText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rsrpText = findViewById(R.id.rsrpView);
        mccText = findViewById(R.id.mccView);
        mncText = findViewById(R.id.mncView);
        pciText = findViewById(R.id.pciView);
        tacText = findViewById(R.id.tacView);

        Log.d("Create", "success");
        MyPhoneStateListener myPhoneStateListener = new MyPhoneStateListener();
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).listen(myPhoneStateListener, MyPhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

        rsrpText.setText("RSRP value: null");
        mccText.setText("Cell Mcc: null");
        mncText.setText("Cell Mnc: null");
        pciText.setText("Cell Pci: null");
        tacText.setText("Cell Tac: null");
    }

    private class MyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            getGeneralCellInfo();
            super.onSignalStrengthsChanged(signalStrength);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_LOCATION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Fine Location Permission Granted", Toast.LENGTH_SHORT).show();
                getGeneralCellInfo();
            } else {
                Toast.makeText(MainActivity.this, "Fine Location Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void getGeneralCellInfo () {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_REQUEST);
        } else {
            try {
                cellInfoList = telephonyManager.getAllCellInfo();
                for (CellInfo cellInfo : cellInfoList) {
                    if (cellInfo instanceof CellInfoLte) {
                        cellRSRP = ((CellInfoLte) cellInfo).getCellSignalStrength().getRsrp();
                        cellMcc = ((CellInfoLte) cellInfo).getCellIdentity().getMccString();
                        cellMnc = ((CellInfoLte) cellInfo).getCellIdentity().getMncString();
                        cellPci = ((CellInfoLte) cellInfo).getCellIdentity().getPci();
                        cellTac = ((CellInfoLte) cellInfo).getCellIdentity().getTac();
                        rsrpText.setText("RSRP value: " + String.valueOf(cellRSRP));
                        mccText.setText("Cell Mcc: " + cellMcc);
                        mncText.setText("Cell Mnc: " + cellMnc);
                        pciText.setText("Cell Pci: " + String.valueOf(cellPci));
                        tacText.setText("Cell Tac: " + String.valueOf(cellTac));
                    }
                }
            } catch (Exception e) {
                Log.d("SignalStrength", "Fail" + e);
            }
        }
    }
}