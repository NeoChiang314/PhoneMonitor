package com.example.phonemonitor;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static MainActivity instance;
    public static MainActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Toast.makeText(MainActivity.this, "Create", Toast.LENGTH_SHORT).show();
        instance = this;
        setContentView(R.layout.activity_main);

//        listView = findViewById(R.id.cellInfo);
//
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

//    public void showCellData (List<CellInfoBean> cellInfoBeans) {
//        listView.setAdapter(new CellInfoAdapter(cellInfoBeans, this));
//    }

    public void startCellInfoActivity(View view){
        startActivity(new Intent(this, CellInfoActivity.class));
    }
}