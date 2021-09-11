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
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
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
    NotificationCompat.Builder builder;
    NotificationManagerCompat notificationManager;
    Notification notification;
    int position;
    int consecutiveNum;

    public static MainService instance;
    List<DataByTimeBean> dataByTimeBeans = new ArrayList<>();
    final Handler handler = new Handler();
    final int delay = 5000; // 5000 milliseconds == 5 second
    FeedReaderDbHelper dbHelper;


    //Variables for cell info monitor
    //
    //
    //
    Boolean cellInfoMonitorStatus;
    TelephonyManager telephonyManager;
    MyPhoneStateListener myPhoneStateListener;
    List<CellInfo> cellInfoList = new ArrayList<>();
    List<CellInfoBean> cellInfoBeans = new ArrayList<>();


    //Variables for gps monitor
    //
    //
    //
    Boolean gpsMonitorStatus;
    public double longitude, latitude;
    LocationManager locationManager;
    MyLocationListener myLocationListener;


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
    public Boolean isGpsMonitorOpen() {return gpsMonitorStatus; }

    @Override
    public void onCreate() {
        createNotificationChannel();
        consecutiveNum = 0;
        super.onCreate();
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        instance = this;
        cellInfoMonitorStatus = false;
        gpsMonitorStatus = false;
        startForegroundService();
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

    public void startForegroundService(){
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_signal)
//                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.notification_icon))
                .setContentTitle("Please start cell monitoring")
                .setContentText("")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
//                .setSilent(true)
                .setAutoCancel(false);

        notificationManager = NotificationManagerCompat.from(this);
        notification = builder.build();
        notificationManager.notify(001, notification);

        startForeground(001, notification);
    }

    public void updateNotification() {
        builder.setContentTitle("RSRP: " + String.valueOf(cellInfoBeans.get(0).getCellRSRP()));
        builder.setContentText("Serving cell TAC: " + String.valueOf(cellInfoBeans.get(0).getCellTac()));
        notification = builder.build();
        notificationManager.notify(001, notification);
    }

    public void clearList(){
        dataByTimeBeans.clear();
        if (MainActivity.getInstance() != null) {
            MainActivity.getInstance().updateDataByTimeView();
        }
    }


    //Methods for data recording
    //
    //
    //
    public void startDataRecording(){
        handler.postDelayed(new Runnable() {
            public void run() {
                if (isCellInfoMonitorOpen() && isGpsMonitorOpen()) {
                    updateDataByTimeList();
                    recordFourCellsThreeSteps();
                    recordTwoCellsThreeSteps();

                    //Tester for isRecordable method
//                    if (isRecordable(dataByTimeBeans, (dataByTimeBeans.size()-1), 4, 3)){
//                        Toast.makeText(MainService.this, "Recorded", Toast.LENGTH_SHORT).show();
//                    }
//                    else {
//                        Toast.makeText(MainService.this, "Record failed", Toast.LENGTH_SHORT).show();
//                    }
                    //

                }
                handler.postDelayed(this, delay);
            }
        }, delay);
    }

    private void updateDataByTimeList() {
        DataByTimeBean dataByTimeBean = new DataByTimeBean(longitude, latitude, cellInfoBeans, consecutiveNum);
        dataByTimeBean.setPosition(dataByTimeBeans.size()+1);
        dataByTimeBeans.add(dataByTimeBean);
        if (MainActivity.getInstance() != null) {
            MainActivity.getInstance().updateDataByTimeView();
        }
    }

    public void removeData(int position) {
        dataByTimeBeans.remove(position);
        if (MainActivity.getInstance() != null) {
            MainActivity.getInstance().updateDataByTimeView();
        }
    }

    private Boolean isRecordable (List<DataByTimeBean> dataByTimeBeans, int position, int cells, int steps) {

        //Check is the dataByTime size bigger than the steps and positions
        if ((dataByTimeBeans.size() < (position+1)) || (position < steps)) {
            return false;
            // dataByTime size is smaller than the steps required, cannot record
        }
        else {
            //dataByTime size checked OK
            //load temp dataByTime by steps
            List<DataByTimeBean> tempDataByTimeBeans = new ArrayList<>((steps+1));
            for (int i = steps; i >= 0; i--){
                tempDataByTimeBeans.add(dataByTimeBeans.get(position - i));
            }

            //Check are the all cellInfo sizes bigger than the cells number required
            int[] Tac = new int[cells];
            int consecutiveCheck = tempDataByTimeBeans.get(0).getConsecutiveNum();
            int i = 0;
            for (DataByTimeBean tempDataByTimeBean : tempDataByTimeBeans){
                if (tempDataByTimeBean.getConsecutiveNum() != consecutiveCheck) {
                    return false;
                    //Data is not consecutive, cannot record
                }
                else if (tempDataByTimeBean.getCellInfoBeans().size() < cells) {
                    return false;
                    //One of the cellInfo size is smaller than the cells number required, cannot record
                }
                else {
                    // CellInfo size is OK, than check are the cells' TAC the same
                    if (i == 0 ) {
                        //for the first dataByTime, load its Tac values
                        for (int k = 0; k < cells; k++) {
                            Tac[k] = tempDataByTimeBean.getCellInfoBean(k).getCellTac();
                        }
                    }
                    else {
                        for (int k = 0; k < cells; k++) {
                            if (Tac[k] != tempDataByTimeBean.getCellInfoBean(k).getCellTac()){
                                return false;
                                //One of the cell tac is changed, cannot record
                            }
                        }
                    }
                    i ++;
                }
            }
            return true;
        }
    }

    private void recordFourCellsThreeSteps (){
        if (isRecordable(dataByTimeBeans, (dataByTimeBeans.size()-1), 4,3)){
            FourCellsThreeSteps fourCellsThreeSteps = new FourCellsThreeSteps(dataByTimeBeans, (dataByTimeBeans.size()-1));
            insertFourCellsThreeSteps(fourCellsThreeSteps);
        }
    }

    private void recordTwoCellsThreeSteps (){
        if (isRecordable(dataByTimeBeans, (dataByTimeBeans.size()-1), 2,3)){
            TwoCellsThreeSteps twoCellsThreeSteps = new TwoCellsThreeSteps(dataByTimeBeans, (dataByTimeBeans.size()-1));
            insertTwoCellsThreeSteps(twoCellsThreeSteps);
        }
    }

    private void insertFourCellsThreeSteps (FourCellsThreeSteps fourCellsThreeSteps) {
        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(this);

        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FeedEntry.COLUMN_CURRENT_RSRP, fourCellsThreeSteps.getCurrentRSRP());
        for (int s = 0; s <= 2; s++) {
            for (int c = 0; c <= 3; c++) {
                values.put(FeedReaderContract.FeedEntry.COLUMN_RSRP[c][s], fourCellsThreeSteps.getRSRP(c,s));
                values.put(FeedReaderContract.FeedEntry.COLUMN_RSRQ[c][s], fourCellsThreeSteps.getRSRQ(c,s));
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
                values.put(FeedReaderContract.FeedEntry.COLUMN_RSRP[c][s], twoCellsThreeSteps.getRSRP(c,s));
                values.put(FeedReaderContract.FeedEntry.COLUMN_RSRQ[c][s], twoCellsThreeSteps.getRSRQ(c,s));
            }
            values.put(FeedReaderContract.FeedEntry.COLUMN_LONGITUDE[s], twoCellsThreeSteps.getLongitude(s));
            values.put(FeedReaderContract.FeedEntry.COLUMN_LATITUDE[s], twoCellsThreeSteps.getLatitude(s));
        }

        // Insert the new row, returning the primary key value of the new row
        long lastRowId = db.insert(FeedReaderContract.FeedEntry.TABLE_NAME_2C3S, null, values);
        db.close();
    }

//    public long getTableCount() {
//        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(this);
//        SQLiteDatabase db = dbHelper.getReadableDatabase();
//        long count = DatabaseUtils.queryNumEntries(db, FeedReaderContract.FeedEntry.TABLE_NAME_4C3S);
//        db.close();
//        return count;
//    }


    //Methods for cell info monitor
    //
    //
    //
    public void startCellInfoMonitoring(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CellInfoActivity.getInstance(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, CellInfoActivity.FINE_LOCATION_REQUEST);
        }
        else{
            consecutiveNum ++;
            myPhoneStateListener = new MyPhoneStateListener();
            telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            telephonyManager.listen(myPhoneStateListener, MyPhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
            cellInfoMonitorStatus = true;
        }
    }

    public void stopCellInfoMonitoring(){
        telephonyManager.listen(myPhoneStateListener, MyPhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
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
                cellInfoList = new ArrayList<>(telephonyManager.getAllCellInfo());
                for (CellInfo cellInfo : cellInfoList) {
                    CellInfoBean cellInfoBean = new CellInfoBean(cellInfo);
                    cellInfoBeans.add(cellInfoBean);
                }
                updateNotification();
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


    //Methods for gps monitor
    //
    //
    //
    public void startGpsMonitoring(){
        myLocationListener = new MyLocationListener();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(GpsActivity.getInstance(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, GpsActivity.FINE_LOCATION_REQUEST);
        }
        else{
            consecutiveNum ++;
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, myLocationListener);
            gpsMonitorStatus = true;
        }
    }

    public void stopGpsMonitoring(){
        locationManager.removeUpdates(myLocationListener);
        gpsMonitorStatus = false;
    }

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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            stopGpsMonitoring();
            ActivityCompat.requestPermissions(GpsActivity.getInstance(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, GpsActivity.FINE_LOCATION_REQUEST);
        }
        else{
            longitude = loc.getLongitude();
            latitude = loc.getLatitude();

            if (GpsActivity.getInstance() != null) {
                GpsActivity.getInstance().showGpsData(longitude, latitude);
            }
        }
    }




    //Other methods
    //
    //
    //
}


