package com.example.phonemonitor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.CompoundButton;

import java.util.ArrayList;
import java.util.List;

public class CellInfoActivity extends AppCompatActivity {

    public static final int FINE_LOCATION_REQUEST = 1;
    private static CellInfoActivity instance;
    List<CellInfoBean> cellInfoBeans = new ArrayList<>();

    ListView listView;
    CellInfoAdapter cellInfoAdapter;

    TextView longitudeText, latitudeText;

    public static CellInfoActivity getInstance() {
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.activity_cell_info);
        listView = findViewById(R.id.cellInfo);
        longitudeText = findViewById(R.id.longitudeView);
        latitudeText = findViewById(R.id.latitudeView);

        if (MainService.getInstance().isCellInfoMonitorOpen()) {
            showGpsData(MainService.getInstance().longitude, MainService.getInstance().latitude);
            showCellData(MainService.getInstance().cellInfoBeans);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

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
//        cellInfoBeans.clear();
        instance = null;
//        Toast.makeText(CellInfoActivity.this, "Destroy", Toast.LENGTH_SHORT).show();
    }

    public void showCellData (List<CellInfoBean> cellInfoBeans) {
        cellInfoAdapter = new CellInfoAdapter(cellInfoBeans, this);
        listView.setAdapter(cellInfoAdapter);
//        Toast.makeText(CellInfoActivity.this, "Show data", Toast.LENGTH_SHORT).show();
    }

    public void showGpsData (double longitude, double latitude) {
        longitudeText.setText("Longitude:\n" + String.valueOf(longitude));
        latitudeText.setText("Latitude:\n" + String.valueOf(latitude));
    }
}
