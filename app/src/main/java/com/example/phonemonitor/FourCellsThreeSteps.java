//This is the data class for 4 cells and 3 time steps

package com.example.phonemonitor;

import java.util.ArrayList;
import java.util.List;

public class FourCellsThreeSteps {
    int currentRSRP;
    int[][] RSRP = new int[4][3];
    int[][] RSRQ = new int[4][3];
    double[] longitude = new double[3];
    double[] latitude = new double[3];

    public FourCellsThreeSteps(List<DataByTimeBean> dataByTimeBeans, int position) {
        currentRSRP = dataByTimeBeans.get(position).getCellInfoBean(0).getCellRSRP();
        for (int i = 0; i < 3; i++){
            for (int k = 0; k < 4; k++){
                RSRP[k][i] = dataByTimeBeans.get(position-(i+1)).getCellInfoBean(k).getCellRSRP();
                RSRQ[k][i] = dataByTimeBeans.get(position-(i+1)).getCellInfoBean(k).getCellRSRQ();
            }
            longitude[i] = dataByTimeBeans.get(position-(i+1)).getLongitude();
            latitude[i] = dataByTimeBeans.get(position-(i+1)).getLatitude();
        }
    }
}
