//This is the data class for 4 cells and 3 time steps

package com.example.phonemonitor;

import java.util.List;

public class FourCellsThreeSteps {
    int currentRSRP;
    int[][] RSRP = new int[4][3];
    int[][] RSRQ = new int[4][3];
    double[] longitude = new double[3];
    double[] latitude = new double[3];

    public int getCurrentRSRP() {
        return currentRSRP;
    }

    public int getRSRP(int cell, int step) {
        return RSRP[cell][step];
    }

    public int getRSRQ(int cell, int step) {
        return RSRQ[cell][step];
    }

    public double getLongitude(int step) {
        return longitude[step];
    }

    public double getLatitude(int step) {
        return latitude[step];
    }

    public FourCellsThreeSteps(List<DataByTimeBean> dataByTimeBeans, int position) {
        currentRSRP = dataByTimeBeans.get(position).getCellInfoBean(0).getCellRSRP();
        for (int s = 0; s < 3; s++){
            for (int c = 0; c < 4; c++){
                RSRP[c][s] = dataByTimeBeans.get(position-(s+1)).getCellInfoBean(c).getCellRSRP();
                RSRQ[c][s] = dataByTimeBeans.get(position-(s+1)).getCellInfoBean(c).getCellRSRQ();
            }
            longitude[s] = dataByTimeBeans.get(position-(s+1)).getLongitude();
            latitude[s] = dataByTimeBeans.get(position-(s+1)).getLatitude();
        }
    }
}
