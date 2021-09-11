package com.example.phonemonitor;

import java.util.List;

public class TwoCellsThreeSteps {

    int currentRSRP;
    int[][] RSRP = new int[2][3];
    int[][] RSRQ = new int[2][3];
    double[] longitude = new double[3];
    double[] latitude = new double[3];

    public int getCurrentRSRP() { return currentRSRP; }

    public int getRSRP(int cell, int step) { return RSRP[cell][step]; }

    public int getRSRQ(int cell, int step) { return RSRQ[cell][step]; }

    public double getLongitude(int step) { return longitude[step]; }

    public double getLatitude(int step) { return latitude[step]; }

    public TwoCellsThreeSteps(List<DataByTimeBean> dataByTimeBeans, int position){
        currentRSRP = dataByTimeBeans.get(position).getCellInfoBean(0).getCellRSRP();
        for (int s = 0; s < 3; s++){
            for (int c = 0; c < 2; c++){
                RSRP[c][s] = dataByTimeBeans.get(position-(s+1)).getCellInfoBean(c).getCellRSRP();
                RSRQ[c][s] = dataByTimeBeans.get(position-(s+1)).getCellInfoBean(c).getCellRSRQ();
            }
            longitude[s] = dataByTimeBeans.get(position-(s+1)).getLongitude();
            latitude[s] = dataByTimeBeans.get(position-(s+1)).getLatitude();
        }
    }
}
