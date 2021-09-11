package com.example.phonemonitor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.os.Bundle;
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
    CellInfoAdapter cellInfoAdapter;

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

        if (!MainService.getInstance().isCellInfoMonitorOpen()) {
            toggleButton.setChecked(false);
            toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked){
                    if(isChecked){
                        MainService.getInstance().startCellInfoMonitoring();
                        listView.setVisibility(View.VISIBLE);
                    }
                    else{
                        MainService.getInstance().stopCellInfoMonitoring();
                        listView.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }
        else{
            toggleButton.setChecked(true);
            listView.setVisibility(View.VISIBLE);
            showCellData(MainService.getInstance().cellInfoBeans);
            toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked){
                    if(isChecked){
                        MainService.getInstance().startCellInfoMonitoring();
                        listView.setVisibility(View.VISIBLE);
                    }
                    else{
                        MainService.getInstance().stopCellInfoMonitoring();
                        listView.setVisibility(View.INVISIBLE);
                    }
                }
            });
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
        cellInfoBeans.clear();
        instance = null;
//        Toast.makeText(CellInfoActivity.this, "Destroy", Toast.LENGTH_SHORT).show();
    }

    public void showCellData (List<CellInfoBean> cellInfoBeans) {
            cellInfoAdapter = new CellInfoAdapter(cellInfoBeans, this);
            listView.setAdapter(cellInfoAdapter);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_LOCATION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(CellInfoActivity.this, "Fine location permission granted", Toast.LENGTH_SHORT).show();
                MainService.getInstance().startCellInfoMonitoring();
            }
            else {
                Toast.makeText(CellInfoActivity.this, "Fine location permission rejected", Toast.LENGTH_SHORT).show();
                toggleButton.setChecked(false);
            }
        }
    }
}
