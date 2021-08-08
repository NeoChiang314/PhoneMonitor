package com.example.phonemonitor;

import android.telephony.CellInfo;

public class BeanCellInfo {

    int cellRSRP;
    String cellMcc;
    String cellMnc;
    int cellPci;
    int cellTac;
//    int count;
    int connectionCode;
    String connectionType;

    public int getCellRSRP() {
        return cellRSRP;
    }

    public void setCellRSRP(int cellRSRP) {
        this.cellRSRP = cellRSRP;
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

//    public int getCount() {
//        return count;
//    }
//
//    public void setCount(int count) {
//        this.count = count;
//    }

    public int getConnectionCode() {
        return connectionCode;
    }

    public void setConnectionCode(int connectionCode) {
        this.connectionCode = connectionCode;
        if(connectionCode == CellInfo.CONNECTION_NONE){
            this.connectionType = "Not a serving cell";
        }
        else if(connectionCode == CellInfo.CONNECTION_PRIMARY_SERVING){
            this.connectionType = "Primary serving cell";
        }
        else if(connectionCode == CellInfo.CONNECTION_SECONDARY_SERVING){
            this.connectionType = "Secondary serving cell";
        }
        else{
            this.connectionType = "Unknown";
        }
    }

    public String getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(String connectionType) {
        this.connectionType = connectionType;
    }
}
