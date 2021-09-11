package com.example.phonemonitor;

import android.provider.BaseColumns;

public final class FeedReaderContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private FeedReaderContract() {}

    public static final String SQL_CREATE_ENTRIES_4C3S =
            "CREATE TABLE " + FeedEntry.TABLE_NAME_4C3S + " (" +
                    FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedEntry.COLUMN_CURRENT_RSRP + " INTEGER," +
                    FeedEntry.COLUMN_RSRP[0][0] + " INTEGER," +
                    FeedEntry.COLUMN_RSRP[1][0] + " INTEGER," +
                    FeedEntry.COLUMN_RSRP[2][0] + " INTEGER," +
                    FeedEntry.COLUMN_RSRP[3][0] + " INTEGER," +
                    FeedEntry.COLUMN_RSRP[0][1] + " INTEGER," +
                    FeedEntry.COLUMN_RSRP[1][1] + " INTEGER," +
                    FeedEntry.COLUMN_RSRP[2][1] + " INTEGER," +
                    FeedEntry.COLUMN_RSRP[3][1] + " INTEGER," +
                    FeedEntry.COLUMN_RSRP[0][2] + " INTEGER," +
                    FeedEntry.COLUMN_RSRP[1][2] + " INTEGER," +
                    FeedEntry.COLUMN_RSRP[2][2] + " INTEGER," +
                    FeedEntry.COLUMN_RSRP[3][2] + " INTEGER," +
                    FeedEntry.COLUMN_RSRQ[0][0] + " INTEGER," +
                    FeedEntry.COLUMN_RSRQ[1][0] + " INTEGER," +
                    FeedEntry.COLUMN_RSRQ[2][0] + " INTEGER," +
                    FeedEntry.COLUMN_RSRQ[3][0] + " INTEGER," +
                    FeedEntry.COLUMN_RSRQ[0][1] + " INTEGER," +
                    FeedEntry.COLUMN_RSRQ[1][1] + " INTEGER," +
                    FeedEntry.COLUMN_RSRQ[2][1] + " INTEGER," +
                    FeedEntry.COLUMN_RSRQ[3][1] + " INTEGER," +
                    FeedEntry.COLUMN_RSRQ[0][2] + " INTEGER," +
                    FeedEntry.COLUMN_RSRQ[1][2] + " INTEGER," +
                    FeedEntry.COLUMN_RSRQ[2][2] + " INTEGER," +
                    FeedEntry.COLUMN_RSRQ[3][2] + " INTEGER," +
                    FeedEntry.COLUMN_LONGITUDE[0] + " INTEGER," +
                    FeedEntry.COLUMN_LONGITUDE[1] + " INTEGER," +
                    FeedEntry.COLUMN_LONGITUDE[2] + " INTEGER," +
                    FeedEntry.COLUMN_LATITUDE[0] + " INTEGER," +
                    FeedEntry.COLUMN_LATITUDE[1] + " INTEGER," +
                    FeedEntry.COLUMN_LATITUDE[2] + " INTEGER)";

    public static final String SQL_CREATE_ENTRIES_2C3S =
            "CREATE TABLE " + FeedEntry.TABLE_NAME_2C3S + " (" +
                    FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedEntry.COLUMN_CURRENT_RSRP + " INTEGER," +
                    FeedEntry.COLUMN_RSRP[0][0] + " INTEGER," +
                    FeedEntry.COLUMN_RSRP[1][0] + " INTEGER," +
                    FeedEntry.COLUMN_RSRP[0][1] + " INTEGER," +
                    FeedEntry.COLUMN_RSRP[1][1] + " INTEGER," +
                    FeedEntry.COLUMN_RSRP[0][2] + " INTEGER," +
                    FeedEntry.COLUMN_RSRP[1][2] + " INTEGER," +
                    FeedEntry.COLUMN_RSRQ[0][0] + " INTEGER," +
                    FeedEntry.COLUMN_RSRQ[1][0] + " INTEGER," +
                    FeedEntry.COLUMN_RSRQ[0][1] + " INTEGER," +
                    FeedEntry.COLUMN_RSRQ[1][1] + " INTEGER," +
                    FeedEntry.COLUMN_RSRQ[0][2] + " INTEGER," +
                    FeedEntry.COLUMN_RSRQ[1][2] + " INTEGER," +
                    FeedEntry.COLUMN_LONGITUDE[0] + " INTEGER," +
                    FeedEntry.COLUMN_LONGITUDE[1] + " INTEGER," +
                    FeedEntry.COLUMN_LONGITUDE[2] + " INTEGER," +
                    FeedEntry.COLUMN_LATITUDE[0] + " INTEGER," +
                    FeedEntry.COLUMN_LATITUDE[1] + " INTEGER," +
                    FeedEntry.COLUMN_LATITUDE[2] + " INTEGER)";

    public static final String SQL_DELETE_ENTRIES_4C3S =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME_4C3S;

    public static final String SQL_DELETE_ENTRIES_2C3S =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME_2C3S;

    /* Inner class that defines the table contents */
    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME_4C3S = "Data_4Cells_3Steps";
        public static final String TABLE_NAME_2C3S = "Data_2Cells_3Steps";
        public static final String COLUMN_CURRENT_RSRP = "Current_RSRP";

        public static final String[][] COLUMN_RSRP = new String[][]{
                {"RSRP_0_0", "RSRP_0_1", "RSRP_0_2"},
                {"RSRP_1_0", "RSRP_1_1", "RSRP_1_2"},
                {"RSRP_2_0", "RSRP_2_1", "RSRP_2_2"},
                {"RSRP_3_0", "RSRP_3_1", "RSRP_3_2"}};

        public static final String[][] COLUMN_RSRQ = new String[][]{
                {"RSRQ_0_0", "RSRQ_0_1", "RSRQ_0_2"},
                {"RSRQ_1_0", "RSRQ_1_1", "RSRQ_1_2"},
                {"RSRQ_2_0", "RSRQ_2_1", "RSRQ_2_2"},
                {"RSRQ_3_0", "RSRQ_3_1", "RSRQ_3_2"}};

        public static final String[] COLUMN_LONGITUDE = new String[] {"Longitude_0" , "Longitude_1" , "Longitude_2"};
        public static final String[] COLUMN_LATITUDE = new String[] {"Latitude_0" , "Latitude_1" , "Latitude_2"};
    }
}