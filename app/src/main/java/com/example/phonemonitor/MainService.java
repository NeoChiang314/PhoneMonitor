package com.example.phonemonitor;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.CellInfo;
import android.telephony.CellInfoLte;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
    NotificationCompat.Builder builder;
    NotificationManagerCompat notificationManager;
    Notification notification;
    int position;
    int consecutiveNum;

    private static MainService instance;
    List<DataByTimeBean> dataByTimeBeans = new ArrayList<>();
    final Handler handlerCellUpdating = new Handler();
    final int delayCellUpdating = 2000;
    FeedReaderDbHelper dbHelper;
    Runnable cellInfoRunnable;


    //Variables for cell info monitor
    //
    //
    //
    Boolean cellInfoMonitorStatus;
    TelephonyManager telephonyManager;
    MyCellInfoCallBack myCellInfoCallBack = new MyCellInfoCallBack();
    List<CellInfoBean> cellInfoBeans = new ArrayList<>();


    //Variables for gps monitor
    //
    //
    //
    public double longitude, latitude;
    LocationManager locationManager;
    MyLocationListener myLocationListener = new MyLocationListener();


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

//    public Boolean isGpsMonitorOpen() {
//        return gpsMonitorStatus;
//    }

    @Override
    public void onCreate() {
//        Toast.makeText(MainService.this, "Created", Toast.LENGTH_SHORT).show();
        super.onCreate();
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        instance = this;
        cellInfoMonitorStatus = false;
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        createNotificationChannel();
        startForegroundService();
        consecutiveNum = 0;
        MainActivity.getInstance().setToggleButton();

        cellInfoRunnable = new Runnable() {
            public void run() {
                if (ActivityCompat.checkSelfPermission(MainService.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    stopCellInfoMonitoring();
                    ActivityCompat.requestPermissions(MainActivity.getInstance(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MainActivity.FINE_LOCATION_REQUEST);
                    return;
                }
                else {
                    telephonyManager.requestCellInfoUpdate(getMainExecutor(), myCellInfoCallBack);
//                    Toast.makeText(MainService.this, "Request update", Toast.LENGTH_SHORT).show();
                    updateDataByTimeList();
                }

                if ((ActivityCompat.checkSelfPermission(MainService.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) ||
                        (ActivityCompat.checkSelfPermission(MainService.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                    stopCellInfoMonitoring();
                    ActivityCompat.requestPermissions(MainActivity.getInstance(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, MainActivity.EXTERNAL_STORAGE);
                    return;
                }
                else{
//                    recordFourCellsThreeSteps();
//                    recordTwoCellsThreeSteps();
                    recordCellInfos();
                }
                handlerCellUpdating.postDelayed(this, delayCellUpdating);
//                Toast.makeText(MainService.this, "Runnable finish", Toast.LENGTH_SHORT).show();
            }
        };

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        instance = null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    //Methods for service basic functions
    //
    //
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.setSound(null, null);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void startForegroundService() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_signal)
                .setContentTitle("Please start cell monitoring")
                .setContentText("")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(false);

        notificationManager = NotificationManagerCompat.from(this);
        notification = builder.build();
        notificationManager.notify(001, notification);

        startForeground(001, notification);
    }

    public void updateNotification() {
        builder.setContentTitle("RSRP: " + String.valueOf(cellInfoBeans.get(0).getCellRSRP()));
        builder.setContentText("Serving cell PCI: " + String.valueOf(cellInfoBeans.get(0).getCellPci()));
        notification = builder.build();
        notificationManager.notify(001, notification);
    }

    public void clearList() {
        dataByTimeBeans.clear();
        if (MainActivity.getInstance() != null) {
            MainActivity.getInstance().updateDataByTimeView();
        }
    }


    //Methods for data recording
    //
    //
    //
    private void updateDataByTimeList() {
        DataByTimeBean dataByTimeBean = new DataByTimeBean(longitude, latitude, cellInfoBeans, consecutiveNum);
        dataByTimeBean.setPosition(dataByTimeBeans.size() + 1);
        dataByTimeBeans.add(dataByTimeBean);
        if (MainActivity.getInstance() != null) {
            MainActivity.getInstance().updateDataByTimeView();
//            Toast.makeText(MainService.this, "Update list", Toast.LENGTH_SHORT).show();
        }
    }

    public void removeData(int position) {
        dataByTimeBeans.remove(position);
        if (MainActivity.getInstance() != null) {
            MainActivity.getInstance().updateDataByTimeView();
        }
    }

    private Boolean isRecordable(List<DataByTimeBean> dataByTimeBeans, int position, int cells, int steps) {

        //Check is the dataByTime size bigger than the steps and positions
        if ((dataByTimeBeans.size() < (position + 1)) || (position < steps)) {
            return false;
            // dataByTime size is smaller than the steps required, cannot record
        } else {
            //dataByTime size checked OK
            //load temp dataByTime by steps
            List<DataByTimeBean> tempDataByTimeBeans = new ArrayList<>((steps + 1));
            for (int i = steps; i >= 0; i--) {
                tempDataByTimeBeans.add(dataByTimeBeans.get(position - i));
            }

            //Check are the all cellInfo sizes bigger than the cells number required
            int[] Pci = new int[cells];
            int consecutiveCheck = tempDataByTimeBeans.get(0).getConsecutiveNum();
            int i = 0;
            for (DataByTimeBean tempDataByTimeBean : tempDataByTimeBeans) {
                if (tempDataByTimeBean.getConsecutiveNum() != consecutiveCheck) {
                    return false;
                    //Data is not consecutive, cannot record
                } else if (tempDataByTimeBean.getCellInfoBeans().size() < cells) {
                    return false;
                    //One of the cellInfo size is smaller than the cells number required, cannot record
                } else {
                    // CellInfo size is OK, than check are the cells' TAC the same
                    if (i == 0) {
                        //for the first dataByTime, load its Tac values
                        for (int k = 0; k < cells; k++) {
                            Pci[k] = tempDataByTimeBean.getCellInfoBean(k).getCellPci();
                        }
                    } else {
                        for (int k = 0; k < cells; k++) {
                            if (Pci[k] != tempDataByTimeBean.getCellInfoBean(k).getCellPci()) {
                                return false;
                                //One of the cell Pci is changed, cannot record
                            }
                        }
                    }
                    i++;
                }
            }
            return true;
        }
    }

    private void recordFourCellsThreeSteps() {
        if (isRecordable(dataByTimeBeans, (dataByTimeBeans.size() - 1), 4, 3)) {
            FourCellsThreeSteps fourCellsThreeSteps = new FourCellsThreeSteps(dataByTimeBeans, (dataByTimeBeans.size() - 1));
            insertFourCellsThreeSteps(fourCellsThreeSteps);
        }
    }

    private void recordTwoCellsThreeSteps() {
        if (isRecordable(dataByTimeBeans, (dataByTimeBeans.size() - 1), 2, 3)) {
            TwoCellsThreeSteps twoCellsThreeSteps = new TwoCellsThreeSteps(dataByTimeBeans, (dataByTimeBeans.size() - 1));
            insertTwoCellsThreeSteps(twoCellsThreeSteps);
        }
    }

    private void recordCellInfos() {
        if (dataByTimeBeans.size() > 0 && dataByTimeBeans.get(dataByTimeBeans.size()-1).cellInfoBeans.size() > 2) {
            insertCellInfos(dataByTimeBeans.get(dataByTimeBeans.size()-1));
        }
    }

    private void insertFourCellsThreeSteps(FourCellsThreeSteps fourCellsThreeSteps) {
        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(this);

        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FeedEntry.COLUMN_CURRENT_RSRP, fourCellsThreeSteps.getCurrentRSRP());
        for (int s = 0; s <= 2; s++) {
            for (int c = 0; c <= 3; c++) {
                values.put(FeedReaderContract.FeedEntry.COLUMN_RSRP[c][s], fourCellsThreeSteps.getRSRP(c, s));
                values.put(FeedReaderContract.FeedEntry.COLUMN_RSRQ[c][s], fourCellsThreeSteps.getRSRQ(c, s));
            }
            values.put(FeedReaderContract.FeedEntry.COLUMN_LONGITUDE[s], fourCellsThreeSteps.getLongitude(s));
            values.put(FeedReaderContract.FeedEntry.COLUMN_LATITUDE[s], fourCellsThreeSteps.getLatitude(s));
        }

        // Insert the new row, returning the primary key value of the new row
        long lastRowId = db.insert(FeedReaderContract.FeedEntry.TABLE_NAME_4C3S, null, values);
        db.close();
    }

    private void insertTwoCellsThreeSteps(TwoCellsThreeSteps twoCellsThreeSteps) {
        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(this);

        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(FeedReaderContract.FeedEntry.COLUMN_CURRENT_RSRP, twoCellsThreeSteps.getCurrentRSRP());
        for (int s = 0; s <= 2; s++) {
            for (int c = 0; c <= 1; c++) {
                values.put(FeedReaderContract.FeedEntry.COLUMN_RSRP[c][s], twoCellsThreeSteps.getRSRP(c, s));
                values.put(FeedReaderContract.FeedEntry.COLUMN_RSRQ[c][s], twoCellsThreeSteps.getRSRQ(c, s));
            }
            values.put(FeedReaderContract.FeedEntry.COLUMN_LONGITUDE[s], twoCellsThreeSteps.getLongitude(s));
            values.put(FeedReaderContract.FeedEntry.COLUMN_LATITUDE[s], twoCellsThreeSteps.getLatitude(s));
        }

        // Insert the new row, returning the primary key value of the new row
        long lastRowId = db.insert(FeedReaderContract.FeedEntry.TABLE_NAME_2C3S, null, values);
        db.close();
    }

    private void insertCellInfos(DataByTimeBean dataByTimeBean) {
        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(this);

        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        for (int i = 0; i < 3; i++){
            values.put(FeedReaderContract.FeedEntry.RSRP[i], dataByTimeBean.getCellInfoBeans().get(i).getCellRSRP());
            values.put(FeedReaderContract.FeedEntry.RSRQ[i], dataByTimeBean.getCellInfoBeans().get(i).getCellRSRQ());
            values.put(FeedReaderContract.FeedEntry.PCI[i], dataByTimeBean.getCellInfoBeans().get(i).getCellPci());
            values.put(FeedReaderContract.FeedEntry.TAC[i], dataByTimeBean.getCellInfoBeans().get(i).getCellTac());
        }
        values.put("Longitude", dataByTimeBean.getLongitude());
        values.put("Latitude", dataByTimeBean.getLatitude());

        // Insert the new row, returning the primary key value of the new row
        long lastRowId = db.insert(FeedReaderContract.FeedEntry.TABLE_NAME_CELLINFOS, null, values);
        db.close();
    }


    //Methods for cell info monitor
    //
    //
    //
    public void startCellInfoMonitoring() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.getInstance(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MainActivity.FINE_LOCATION_REQUEST);
            return;
        }
        if ((ActivityCompat.checkSelfPermission(MainService.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) ||
                (ActivityCompat.checkSelfPermission(MainService.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(MainActivity.getInstance(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, MainActivity.EXTERNAL_STORAGE);
            return;
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        consecutiveNum ++;
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 200, 1, myLocationListener);
        cellInfoRunnable.run();
        consecutiveNum++;
        cellInfoMonitorStatus = true;
    }

    public void stopCellInfoMonitoring(){
        handlerCellUpdating.removeCallbacks(cellInfoRunnable);
        locationManager.removeUpdates(myLocationListener);
        cellInfoMonitorStatus = false;
    }

    public class MyCellInfoCallBack extends TelephonyManager.CellInfoCallback {
        @Override
        public void onCellInfo(@NonNull List<CellInfo> cellInfo) {
//            Toast.makeText(MainService.this, "Callback", Toast.LENGTH_SHORT).show();
            getGeneralCellInfo(cellInfo);
        }

        @Override
        public void onError(int errorCode, Throwable detail){
            Toast.makeText(MainService.this, "CallbackError", Toast.LENGTH_SHORT).show();
        }
    }

    public void getGeneralCellInfo(List<CellInfo> cellInfoList) {
        cellInfoBeans.clear();
        for (CellInfo tempCellInfo : cellInfoList) {
            if (tempCellInfo instanceof CellInfoLte){
                CellInfoBean cellInfoBean = new CellInfoBean(tempCellInfo);
                cellInfoBeans.add(cellInfoBean);
            }
        }
        updateNotification();
//        Toast.makeText(MainService.this, "Show data", Toast.LENGTH_SHORT).show();

        if (CellInfoActivity.getInstance() != null) {
            CellInfoActivity.getInstance().showCellData(cellInfoBeans);
//            Toast.makeText(MainService.this, "Show data", Toast.LENGTH_SHORT).show();
        }
    }


    //Methods for gps monitor
    //
    //
    //
    public class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged (Location loc){
//            Toast.makeText(GpsService.this, "Location updated", Toast.LENGTH_SHORT).show();
            getLocationInfo(loc);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onProviderDisabled(String provider) {}
    }

    public void getLocationInfo (Location loc) {
        longitude = loc.getLongitude();
        latitude = loc.getLatitude();

        if (CellInfoActivity.getInstance() != null) {
//            Toast.makeText(MainService.this, "update", Toast.LENGTH_SHORT).show();
            CellInfoActivity.getInstance().showGpsData(longitude, latitude);
        }
    }
}