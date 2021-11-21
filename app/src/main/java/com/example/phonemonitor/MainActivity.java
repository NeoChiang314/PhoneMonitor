package com.example.phonemonitor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static MainActivity instance;
    public static MainActivity getInstance() {
        return instance;
    }
    ListView listView;
    DataByTimeAdapter dataByTimeAdapter;
    TextView dataRecordedText;

    // Variables for monitor
    public static final int FINE_LOCATION_REQUEST = 0;
    public static final int EXTERNAL_STORAGE = 1;
    ToggleButton toggleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
//        Toast.makeText(MainActivity.this, "Create", Toast.LENGTH_SHORT).show();
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.dataByTime);
        dataRecordedText = findViewById(R.id.dataRecordedView);
        toggleButton = findViewById(R.id.cellInfoToggleButton);


        if (MainService.getInstance() == null) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, MainService.class);
            startService(intent);
        }
        else {
            setToggleButton();
        }

        updateDatabaseCount();
    }

    @Override
    protected void onStart(){
        super.onStart();
//        Toast.makeText(MainActivity.this, "Start", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume(){
        super.onResume();
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
        instance = null;
//        cellInfoBeans.clear();
//        Toast.makeText(MainActivity.this, "Destroy", Toast.LENGTH_SHORT).show();
    }

    public void setToggleButton(){
        if (!MainService.getInstance().isCellInfoMonitorOpen()) {
            toggleButton.setChecked(false);
            toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked){
                    if(isChecked){
                        MainService.getInstance().startCellInfoMonitoring();
                    }
                    else{
                        MainService.getInstance().stopCellInfoMonitoring();
                    }
                }
            });
        }
        else{
            toggleButton.setChecked(true);
            listView.setVisibility(View.VISIBLE);
            toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked){
                    if(isChecked){
                        MainService.getInstance().startCellInfoMonitoring();
                    }
                    else{
                        MainService.getInstance().stopCellInfoMonitoring();
                    }
                }
            });
        }
    }

    public void updateDataByTimeView(){
        if (dataByTimeAdapter == null){
            dataByTimeAdapter = new DataByTimeAdapter(MainService.getInstance().dataByTimeBeans, this);
            listView.setAdapter(dataByTimeAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener (){
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    MainService.getInstance().setPosition(position);
                    startActivity(new Intent(MainActivity.this, DataUnitActivity.class));
                }
            });
        }
        updateDatabaseCount();
        dataByTimeAdapter.notifyDataSetChanged();
    }

    private void updateDatabaseCount() {
        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
//        long[] count = new long[2];
//        count[0] = DatabaseUtils.queryNumEntries(db, FeedReaderContract.FeedEntry.TABLE_NAME_4C3S);
//        count[1] = DatabaseUtils.queryNumEntries(db, FeedReaderContract.FeedEntry.TABLE_NAME_2C3S);

        long count = DatabaseUtils.queryNumEntries(db, FeedReaderContract.FeedEntry.TABLE_NAME_CELLINFOS);
        db.close();
//        dataRecordedText.setText("4C3S data recorded: " + String.valueOf(count[0]) + "\n"
//                + "2C3S data recorded: " + String.valueOf(count[1]));
        dataRecordedText.setText("Database recorded: " + String.valueOf(count));
    }

    public void startCellInfoActivity(View view){
        startActivity(new Intent(this, CellInfoActivity.class));
    }

    public void clearList(View view) {
        MainService.getInstance().clearList();
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_LOCATION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Fine location permission granted", Toast.LENGTH_SHORT).show();
                MainService.getInstance().startCellInfoMonitoring();
            }
            else {
                Toast.makeText(MainActivity.this, "Fine location permission rejected", Toast.LENGTH_SHORT).show();
                toggleButton.setChecked(false);
            }
        }
        else if (requestCode == EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "External storage permission granted", Toast.LENGTH_SHORT).show();
                MainService.getInstance().startCellInfoMonitoring();
            }
            else {
                Toast.makeText(MainActivity.this, "External storage permission rejected", Toast.LENGTH_SHORT).show();
                toggleButton.setChecked(false);
            }
        }
    }
}