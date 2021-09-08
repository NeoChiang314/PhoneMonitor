package com.example.phonemonitor;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DataUnitActivity extends AppCompatActivity {

    DataByTimeBean dataByTimeBean;
    CellInfoAdapter cellInfoAdapter;
    TextView dataTimeText, dataLongitudeText, dataLatitudeText, servingRSRPText, servingTACText;
    ListView cellInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Toast.makeText(MainActivity.this, "Create", Toast.LENGTH_SHORT).show();
        dataByTimeBean = MainService.getInstance().dataByTimeBeans.get(MainService.getInstance().getPosition());

        setContentView(R.layout.data_unit);
        dataTimeText = findViewById(R.id.dataTime);
        dataLongitudeText = findViewById(R.id.dataLongitudeView);
        dataLatitudeText = findViewById(R.id.dataLatitudeView);
        servingRSRPText = findViewById(R.id.servingRSRPView);
        servingTACText = findViewById(R.id.servingTACView);
        cellInfo = findViewById(R.id.dataCellInfo);

        dataTimeText.setText(dataByTimeBean.currentTime);
        dataLongitudeText.setText("Longitude:\n" + String.valueOf(dataByTimeBean.longitude));
        dataLatitudeText.setText("Latitude:\n" + String.valueOf(dataByTimeBean.latitude));
        servingRSRPText.setText("Serving cell RSRP: " + String.valueOf(dataByTimeBean.getCellInfoBean(0).getCellRSRP()));
        servingTACText.setText("Cell's TAC: " + String.valueOf(dataByTimeBean.getCellInfoBean(0).getCellTac()));
        cellInfoAdapter = new CellInfoAdapter(dataByTimeBean.cellInfoBeans, this);
        cellInfo.setAdapter(cellInfoAdapter);
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
