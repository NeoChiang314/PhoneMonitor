package com.example.phonemonitor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import java.util.ArrayList;
import java.util.List;

public class CellInfoActivity extends AppCompatActivity {

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
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked){
                if(isChecked){
                    Intent intent = new Intent();
                    intent.setClass(CellInfoActivity.this, CellInfoService.class);
                    startService(intent);
                }
                else{
                    Intent intent = new Intent();
                    intent.setClass(CellInfoActivity.this, CellInfoService.class);
                    stopService(intent);
                }
            }
        });

//        Intent intent = new Intent();
//        intent.setClass(this, CellInfoService.class);
//        startService(intent);

//        if (CellInfoService.getInstance() == null) {
//            Intent intent = new Intent();
//            intent.setClass(this, CellInfoService.class);
//            this.startService(intent);
//        }
//        else{
//            listView.setAdapter(new CellInfoAdapter(CellInfoService.getInstance().getLastCellInfoBeans(), this));
//        }
    }

    @Override
    protected void onStart(){
        super.onStart();
//        Toast.makeText(MainActivity.this, "Start", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause(){
        super.onPause();
//        Toast.makeText(MainActivity.this, "Pause", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onRestart(){
        super.onRestart();
//        Toast.makeText(MainActivity.this, "Restart", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        cellInfoBeans.clear();
//        instance = null;
//        Toast.makeText(MainActivity.this, "Destroy", Toast.LENGTH_SHORT).show();
    }

    public void showCellData (List<CellInfoBean> cellInfoBeans) {
        listView.setAdapter(new CellInfoAdapter(cellInfoBeans, this));
    }
}
