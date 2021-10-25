package com.example.phonemonitor;

import android.telephony.CellInfo;
import android.telephony.CellInfoLte;

public class CellInfoBean {

    String cellType;
    int cellRSRP;
    int cellRSRQ;
    String cellMcc;
    String cellMnc;
    int cellPci;
    int cellTac;

    public CellInfoBean (CellInfo cellInfo) {
        if (cellInfo instanceof CellInfoLte){
            this.setCellType("LTE cell");
            this.setCellRSRP(((CellInfoLte) cellInfo).getCellSignalStrength().getRsrp());
            this.setCellRSRQ(((CellInfoLte) cellInfo).getCellSignalStrength().getRsrq());
            this.setCellMcc(((CellInfoLte) cellInfo).getCellIdentity().getMccString());
            this.setCellMnc(((CellInfoLte) cellInfo).getCellIdentity().getMncString());
            this.setCellPci(((CellInfoLte) cellInfo).getCellIdentity().getPci());
            this.setCellTac(((CellInfoLte) cellInfo).getCellIdentity().getTac());
        }

         //Due to this project only aims to LTE network at this stage, other cell types are not monitored
        //The following code is for further 5G network monitoring
//        else if (cellInfo instanceof CellInfoNr) {
//            this.setCellType("Nr cell");
//            this.setCellRSRP(0);
//            this.setCellRSRQ(0);
//            this.setCellMcc("Null");
//            this.setCellMnc("Null");
//            this.setCellPci(0);
//            this.setCellTac(0);
//        }
//        else if (cellInfo instanceof CellInfoGsm){
//            this.setCellType("Gsm cell");
//            this.setCellRSRP(0);
//            this.setCellRSRQ(0);
//            this.setCellMcc("Null");
//            this.setCellMnc("Null");
//            this.setCellPci(0);
//            this.setCellTac(0);
//        }
//        else if (cellInfo instanceof CellInfoCdma){
//            this.setCellType("Cdma cell");
//            this.setCellRSRP(0);
//            this.setCellRSRQ(0);
//            this.setCellMcc("Null");
//            this.setCellMnc("Null");
//            this.setCellPci(0);
//            this.setCellTac(0);
//        }
//        else if (cellInfo instanceof CellInfoWcdma) {
//            this.setCellType("Wcdma cell");
//            this.setCellRSRP(0);
//            this.setCellRSRQ(0);
//            this.setCellMcc("Null");
//            this.setCellMnc("Null");
//            this.setCellPci(0);
//            this.setCellTac(0);
//        }
//        else if (cellInfo instanceof CellInfoTdscdma) {
//            this.setCellType("Tdscdma cell");
//            this.setCellRSRP(0);
//            this.setCellRSRQ(0);
//            this.setCellMcc("Null");
//            this.setCellMnc("Null");
//            this.setCellPci(0);
//            this.setCellTac(0);
//        }
    }

    public String getCellType() { return cellType; }

    public void setCellType(String cellType) { this.cellType = cellType; }

    public int getCellRSRP() {
        return cellRSRP;
    }

    public void setCellRSRP(int cellRSRP) {
        this.cellRSRP = cellRSRP;
    }

    public int getCellRSRQ() {
        return cellRSRQ;
    }

    public void setCellRSRQ(int cellRSRQ) {
        this.cellRSRQ = cellRSRQ;
    }

    public String getCellMcc() {
        return cellMcc;
    }

    public void setCellMcc(String cellMcc) {
        this.cellMcc = cellMcc;
    }

    public String getCellMnc() {
        return cellMnc;
    }

    public void setCellMnc(String cellMnc) {
        this.cellMnc = cellMnc;
    }

    public int getCellPci() {
        return cellPci;
    }

    public void setCellPci(int cellPci) {
        this.cellPci = cellPci;
    }

    public int getCellTac() {
        return cellTac;
    }

    public void setCellTac(int cellTac) {
        this.cellTac = cellTac;
    }
}
