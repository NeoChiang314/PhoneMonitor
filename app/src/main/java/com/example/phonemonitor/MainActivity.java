package com.example.phonemonitor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    public static MainActivity instance;
    public static MainActivity getInstance() {
        return instance;
    }
    ListView listView;
    DataByTimeAdapter dataByTimeAdapter;
    TextView dataRecordedText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Toast.makeText(MainActivity.this, "Create", Toast.LENGTH_SHORT).show();
        instance = this;
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.dataByTime);
        dataRecordedText = findViewById(R.id.dataRecordedView);


        if (MainService.getInstance() == null) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, MainService.class);
            startService(intent);
        }
        updateDatabaseCount();
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
        instance = null;
//        Toast.makeText(MainActivity.this, "Destroy", Toast.LENGTH_SHORT).show();
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
        long[] count = new long[2];
        count[0] = DatabaseUtils.queryNumEntries(db, FeedReaderContract.FeedEntry.TABLE_NAME_4C3S);
        count[1] = DatabaseUtils.queryNumEntries(db, FeedReaderContract.FeedEntry.TABLE_NAME_2C3S);
        db.close();
        dataRecordedText.setText("4C3S data recorded: " + String.valueOf(count[0]) + "\n"
                + "2C3S data recorded: " + String.valueOf(count[1]));
    }


    public void startCellInfoActivity(View view){
        startActivity(new Intent(this, CellInfoActivity.class));
    }

    public void startGpsActivity(View view){
        startActivity(new Intent(this, GpsActivity.class));
    }

    public void clearList(View view) {
        MainService.getInstance().clearList();
    }
}