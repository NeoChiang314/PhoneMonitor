package com.example.phonemonitor;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static MainActivity instance;
    public static MainActivity getInstance() {
        return instance;
    }
    ListView listView;
    DataByTimeAdapter dataByTimeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Toast.makeText(MainActivity.this, "Create", Toast.LENGTH_SHORT).show();
        instance = this;
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.dataByTime);

        if (MainService.getInstance() == null) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, MainService.class);
            startService(intent);
        }
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

    public void updateDataByTimeView(DataByTimeBean dataByTimeBean){
        if (dataByTimeAdapter == null){
            dataByTimeAdapter = new DataByTimeAdapter(MainService.getInstance().dataByTimeBeans, this);
            listView.setAdapter(dataByTimeAdapter);
        }
        else {
            dataByTimeAdapter.add(dataByTimeBean);
        }
    }

//    public void showCellData (List<CellInfoBean> cellInfoBeans) {
//        listView.setAdapter(new CellInfoAdapter(cellInfoBeans, this));
//    }

    public void startCellInfoActivity(View view){
        startActivity(new Intent(this, CellInfoActivity.class));
    }

    public void startGpsActivity(View view){
        startActivity(new Intent(this, GpsActivity.class));
    }
}