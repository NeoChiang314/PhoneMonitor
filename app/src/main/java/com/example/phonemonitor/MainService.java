package com.example.phonemonitor;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.CellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.ArrayList;
import java.util.List;

public class MainService extends Service {

    //Variables for data recording
    //
    //
    //
    public static final String CHANNEL_ID = "1";
    int position;
    public static MainService instance;
    List<DataByTimeBean> dataByTimeBeans = new ArrayList<>();
    final Handler handler = new Handler();
    final int delay = 5000; // 5000 milliseconds == 5 second


    //Variables for cell info monitor
    //
    //
    //
    Boolean cellInfoMonitorStatus;
    TelephonyManager telephonyManager;
    MyPhoneStateListener myPhoneStateListener;
    List<CellInfo> cellInfoList = new ArrayList<>();
    List<CellInfoBean> cellInfoBeans = new ArrayList<>();


    //Getters and setters
    //
    //
    //
    public static MainService getInstance() {
        return instance;
    }
    public int getPosition() {
        return position;
    }
    public void setPosition(int position) {
        this.position = position;
    }
    public Boolean isCellInfoMonitorOpen() {
        return cellInfoMonitorStatus;
    }

    @Override
    public void onCreate() {
        createNotificationChannel();
        super.onCreate();
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        instance = this;
        cellInfoMonitorStatus = false;
//        startForegroundService();
        startDataRecording();

//        SQLiteOpenHelper helper = MySQLiteOpenHelper.getInstance(this);
//        SQLiteDatabase writableDatabase = helper.getWritableDatabase();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        instance = null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

//    public void setCellInfoBeans (List<CellInfoBean> cellInfoBeans){
//        this.cellInfoBeans = cellInfoBeans;
//    }


    //Methods for service basic functions
    //
    //
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.setShowBadge(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void startForegroundService(){
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.notification_icon))
                .setContentTitle("test")
                .setContentText("test")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
                .setAutoCancel(false);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        Notification notification = builder.build();
        notificationManager.notify(001, notification);

        startForeground(001, notification);
    }


    //Methods for data recording
    //
    //
    //
    public void startDataRecording(){
        handler.postDelayed(new Runnable() {
            public void run() {
                if (isCellInfoMonitorOpen() && (GpsService.getInstance() != null)) {
                    updateDataByTimeList();
                }
                handler.postDelayed(this, delay);
            }
        }, delay);
    }

    public void updateDataByTimeList() {
        DataByTimeBean dataByTimeBean = new DataByTimeBean(GpsService.getInstance().getLongitude(), GpsService.getInstance().getLatitude(), cellInfoBeans);
        dataByTimeBean.setPosition(dataByTimeBeans.size()+1);
        dataByTimeBeans.add(dataByTimeBean);
//        insert(dataByTimeBean);
        if (MainActivity.getInstance() != null) {
            MainActivity.getInstance().updateDataByTimeView();
        }
    }

    public void insert (DataByTimeBean dataByTimeBean) {
        SQLiteOpenHelper helper = MySQLiteOpenHelper.getInstance(this);
        SQLiteDatabase writableDatabase = helper.getWritableDatabase();

        if (writableDatabase.isOpen()) {
            CellInfoBean maxCellInfoBean = getMaxSignalCell(dataByTimeBean);
            String sql = "insert into data (time, longitude, latitude, RSRP, RSRQ, PCI) values (" + "'" + dataByTimeBean.getCurrentTime() + "','" + dataByTimeBean.getLongitude() + "','" + dataByTimeBean.getLatitude()
                    + "','" + maxCellInfoBean.getCellRSRP() + "','" + maxCellInfoBean.getCellRSRQ() + "','" + maxCellInfoBean.getCellPci() +  "')";
            writableDatabase.execSQL(sql);
        }
        writableDatabase.close();
    }


    //Methods for cell info monitor
    //
    //
    //
    public void startCellInfoMonitoring(){
        myPhoneStateListener = new MyPhoneStateListener();
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).listen(myPhoneStateListener, MyPhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        cellInfoMonitorStatus = true;
    }

    public void stopCellInfoMonitoring(){
        ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).listen(myPhoneStateListener, MyPhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        cellInfoMonitorStatus = false;
    }

    public class MyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            getGeneralCellInfo();
        }
    }

    public void getGeneralCellInfo() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            stopCellInfoMonitoring();
            ActivityCompat.requestPermissions(CellInfoActivity.getInstance(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, CellInfoActivity.FINE_LOCATION_REQUEST);
        }
        else{
            cellInfoBeans.clear();
            try{
                cellInfoList = this.telephonyManager.getAllCellInfo();
                for (CellInfo cellInfo : cellInfoList) {
                    CellInfoBean cellInfoBean = new CellInfoBean();
                    cellInfoBean.loadCellInfo(cellInfo);
                    cellInfoBeans.add(cellInfoBean);
                }
//                lastCellInfoBeans = cellInfoBeans;

//                if (GpsService.getInstance() != null) {
//                    MainService.getInstance().updateDataByTimeList(new DataByTimeBean(GpsService.getInstance().longitude, GpsService.getInstance().latitude));
//                }
//                MainService.getInstance().setCellInfoBeans(cellInfoBeans);

                if (CellInfoActivity.getInstance() != null) {
                    CellInfoActivity.getInstance().setCellInfoBeans(cellInfoBeans);
                    CellInfoActivity.getInstance().showCellData(cellInfoBeans);
                }
            } catch (Exception e) {
                Toast.makeText(MainService.this, "Phone states measurement failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public CellInfoBean getMaxSignalCell (DataByTimeBean dataByTimeBean){
        int maxRSRP = -1000;
        int maxRSRQ = -1000;
        CellInfoBean maxCellInfoBean = null;
//        int maxIndex = 0;
//        int index = 0;
        for (CellInfoBean cellInfoBean: dataByTimeBean.cellInfoBeans){
            if (cellInfoBean.getCellRSRP() > maxRSRP){
//                maxIndex = index;
                maxRSRP = cellInfoBean.getCellRSRP();
                maxRSRQ = cellInfoBean.getCellRSRQ();
                maxCellInfoBean = cellInfoBean;
            }
            else if (cellInfoBean.getCellRSRP() == maxRSRP){
                if (cellInfoBean.getCellRSRQ() > maxRSRQ){
//                    maxIndex = index;
                    maxRSRP = cellInfoBean.getCellRSRP();
                    maxRSRQ = cellInfoBean.getCellRSRQ();
                    maxCellInfoBean = cellInfoBean;
                }
            }
//            index ++;
        }
//        return dataByTimeBean.cellInfoBeans.get(maxIndex);
        return maxCellInfoBean;
    }
}


