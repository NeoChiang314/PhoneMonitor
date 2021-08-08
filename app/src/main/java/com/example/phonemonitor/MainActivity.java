package com.example.phonemonitor;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.CellInfo;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static MainActivity instance;
    public static final int FINE_LOCATION_REQUEST = 1;
    List<BeanCellInfo> beanCellInfos = new ArrayList<>();
    ListView listView;

//    static int tempCellRSRP;
//    static String tempCellMcc;
//    static String tempCellMnc;
//    static int tempCellPci;
//    static int tempCellTac;
//    static int tempCount;
//    static String tempConnectionType;
//    TextView rsrpText, mccText, mncText, pciText, tacText, countText, connectionText;

    public List<BeanCellInfo> getBeanCellInfos() {
        return beanCellInfos;
    }

    public void setBeanCellInfos(List<BeanCellInfo> beanCellInfos) {
        this.beanCellInfos = beanCellInfos;
    }

    public static MainActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(MainActivity.this, "Create", Toast.LENGTH_SHORT).show();
        instance = this;
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.cellInfo);

//        rsrpText = findViewById(R.id.rsrpView);
//        mccText = findViewById(R.id.mccView);
//        mncText = findViewById(R.id.mncView);
//        pciText = findViewById(R.id.pciView);
//        tacText = findViewById(R.id.tacView);
//        countText = findViewById(R.id.countView);
//        connectionText = findViewById(R.id.connectionView);

        if (CellInfoService.getInstance() == null) {
//            rsrpText.setText("RSRP value: null");
//            mccText.setText("Cell Mcc: null");
//            mncText.setText("Cell Mnc: null");
//            pciText.setText("Cell Pci: null");
//            tacText.setText("Cell Tac: null");
//            connectionText.setText("Connection type: null");
//            countText.setText("Changed times: 0");
            Intent intent = new Intent();
            intent.setClass(this, CellInfoService.class);
            this.startService(intent);
        }
        else{
//            rsrpText.setText("RSRP value: " + String.valueOf(tempCellRSRP));
//            mccText.setText("Cell Mcc: " + tempCellMcc);
//            mncText.setText("Cell Mnc: " + tempCellMnc);
//            pciText.setText("Cell Pci: " + String.valueOf(tempCellPci));
//            tacText.setText("Cell Tac: " + String.valueOf(tempCellTac));
//            connectionText.setText("Connection type: " + tempConnectionType);
//            countText.setText("Change times: " + String.valueOf(tempCount));
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
        Toast.makeText(MainActivity.this, "Start", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause(){
        super.onPause();
        Toast.makeText(MainActivity.this, "Pause", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        Toast.makeText(MainActivity.this, "Restart", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beanCellInfos.clear();
        instance = null;
        Toast.makeText(MainActivity.this, "Destroy", Toast.LENGTH_SHORT).show();
    }

    public void showCellData (List<BeanCellInfo> beanCellInfos) {

        listView.setAdapter(new CellInfoAdapter(beanCellInfos, this));

//        this.tempCellRSRP = rsrp;
//        this.tempCellMcc = mcc;
//        this.tempCellMnc = mnc;
//        this.tempCellPci = pci;
//        this.tempCellTac = tac;
//        this.tempCount = count;
//        if(connection == CellInfo.CONNECTION_NONE){
//            this.tempConnectionType = "Not a serving cell";
//        }
//        else if(connection == CellInfo.CONNECTION_PRIMARY_SERVING){
//            this.tempConnectionType = "Primary serving cell";
//        }
//        else if(connection == CellInfo.CONNECTION_SECONDARY_SERVING){
//            this.tempConnectionType = "Secondary serving cell";
//        }
//        else{
//            this.tempConnectionType = "Unknown";
//        }
//
//        this.rsrpText.setText("RSRP value: " + String.valueOf(rsrp));
//        this.mccText.setText("Cell Mcc: " + mcc);
//        this.mncText.setText("Cell Mnc: " + mnc);
//        this.pciText.setText("Cell Pci: " + String.valueOf(pci));
//        this.tacText.setText("Cell Tac: " + String.valueOf(tac));
//        this.connectionText.setText("Connection type: " + tempConnectionType);
//        this.countText.setText("Change times: " + String.valueOf(count));
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_LOCATION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Fine Location Permission Granted", Toast.LENGTH_SHORT).show();
                CellInfoService.getInstance().getGeneralCellInfo();
            } else {
                Toast.makeText(MainActivity.this, "Fine Location Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}