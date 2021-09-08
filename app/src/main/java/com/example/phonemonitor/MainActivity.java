package com.example.phonemonitor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;
import android.database.sqlite.*;

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

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, MainService.CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("test")
                .setContentText("test")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
                .setAutoCancel(false);

        Notification notification = builder.build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(001, notification);
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
        dataByTimeAdapter.notifyDataSetChanged();
    }

    public void startCellInfoActivity(View view){
        startActivity(new Intent(this, CellInfoActivity.class));
    }

    public void startGpsActivity(View view){
        startActivity(new Intent(this, GpsActivity.class));
    }
}