package com.example.phonemonitor;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DataUnitActivity extends AppCompatActivity {

    DataByTimeBean dataByTimeBean;
    CellInfoAdapter cellInfoAdapter;
    TextView dataTimeText, dataLongitudeText, dataLatitudeText;
    ListView cellInfoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Toast.makeText(MainActivity.this, "Create", Toast.LENGTH_SHORT).show();
        dataByTimeBean = MainService.getInstance().dataByTimeBeans.get(MainService.getInstance().getPosition());

        setContentView(R.layout.data_unit);
        dataTimeText = findViewById(R.id.dataTime);
        dataLongitudeText = findViewById(R.id.dataLongitudeView);
        dataLatitudeText = findViewById(R.id.dataLatitudeView);
        cellInfoList = findViewById(R.id.dataCellInfo);

        dataTimeText.setText(dataByTimeBean.currentTime);
        dataLongitudeText.setText(String.valueOf(dataByTimeBean.longitude));
        dataLatitudeText.setText(String.valueOf(dataByTimeBean.latitude));
        cellInfoAdapter = new CellInfoAdapter(dataByTimeBean.cellInfoBeans, this);
        cellInfoList.setAdapter(cellInfoAdapter);
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
    }
}
