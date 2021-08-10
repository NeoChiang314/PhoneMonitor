package com.example.phonemonitor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.CompoundButton;

import java.util.ArrayList;
import java.util.List;

public class CellInfoActivity extends AppCompatActivity {

    public static final int FINE_LOCATION_REQUEST = 1;
    public static CellInfoActivity instance;
    List<CellInfoBean> cellInfoBeans = new ArrayList<>();
    ListView listView;
    ToggleButton toggleButton;

    public List<CellInfoBean> getCellInfoBeans() {
        return cellInfoBeans;
    }

    public void setCellInfoBeans(List<CellInfoBean> cellInfoBeans) {
        this.cellInfoBeans = cellInfoBeans;
    }

    public static CellInfoActivity getInstance() {
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.activity_cell_info);
        listView = findViewById(R.id.cellInfo);
        toggleButton = findViewById(R.id.cellInfoToggleButton);



//        Intent intent = new Intent();
//        intent.setClass(this, CellInfoService.class);
//        startService(intent);

        if (CellInfoService.getInstance() == null) {
            toggleButton.setChecked(false);
            toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked){
                    if(isChecked){
                        Intent intent = new Intent();
                        intent.setClass(CellInfoActivity.this, CellInfoService.class);
                        startService(intent);
                        listView.setVisibility(View.VISIBLE);
                    }
                    else{
                        Intent intent = new Intent();
                        intent.setClass(CellInfoActivity.this, CellInfoService.class);
                        stopService(intent);
                        listView.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }
        else{
            toggleButton.setChecked(true);
            showCellData(CellInfoService.getInstance().getLastCellInfoBeans());
            toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked){
                    if(isChecked){
                        Intent intent = new Intent();
                        intent.setClass(CellInfoActivity.this, CellInfoService.class);
                        Toast.makeText(CellInfoActivity.this, "Service started", Toast.LENGTH_SHORT).show();
                        startService(intent);
                    }
                    else{
                        Intent intent = new Intent();
                        intent.setClass(CellInfoActivity.this, CellInfoService.class);
                        Toast.makeText(CellInfoActivity.this, "Service stopped", Toast.LENGTH_SHORT).show();
                        stopService(intent);
                        listView.clearAnimation();
                    }
                }
            });
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

//        savedInstanceState.putBoolean("MyBoolean", true);
//        savedInstanceState.putDouble("myDouble", 1.9);
//        savedInstanceState.putInt("MyInt", 1);
//        savedInstanceState.putString("MyString", "Welcome back to Android");
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

//        boolean myBoolean = savedInstanceState.getBoolean("MyBoolean");
//        double myDouble = savedInstanceState.getDouble("myDouble");
//        int myInt = savedInstanceState.getInt("MyInt");
//        String myString = savedInstanceState.getString("MyString");
    }

    @Override
    protected void onStart(){
        super.onStart();
//        Toast.makeText(CellInfoActivity.this, "Start", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause(){
        super.onPause();
//        Toast.makeText(CellInfoActivity.this, "Pause", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onRestart(){
        super.onRestart();
//        Toast.makeText(CellInfoActivity.this, "Restart", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cellInfoBeans.clear();
        instance = null;
//        Toast.makeText(CellInfoActivity.this, "Destroy", Toast.LENGTH_SHORT).show();
    }

    public void showCellData (List<CellInfoBean> cellInfoBeans) {
        listView.setAdapter(new CellInfoAdapter(cellInfoBeans, this));
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_LOCATION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(CellInfoActivity.this, "Fine location permission granted", Toast.LENGTH_SHORT).show();
                ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).listen(CellInfoService.getInstance().myPhoneStateListener, CellInfoService.MyPhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
            }
            else {
                Toast.makeText(CellInfoActivity.this, "Fine location permission rejected", Toast.LENGTH_SHORT).show();
                toggleButton.setChecked(false);
            }
        }
    }
}
