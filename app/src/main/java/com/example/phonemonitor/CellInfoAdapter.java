package com.example.phonemonitor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class CellInfoAdapter extends BaseAdapter {

    private List<BeanCellInfo> beanCellInfos;
    private Context context;

    public CellInfoAdapter(List<BeanCellInfo> beanCellInfos, Context context) {
        this.beanCellInfos = beanCellInfos;
        this.context = context;
    }

    @Override
    public int getCount() {
        return beanCellInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_cell_info, parent, false);
        }
        TextView rsrpText = convertView.findViewById(R.id.rsrpView);
        TextView mccText = convertView.findViewById(R.id.mccView);
        TextView mncText = convertView.findViewById(R.id.mncView);
        TextView pciText = convertView.findViewById(R.id.pciView);
        TextView tacText = convertView.findViewById(R.id.tacView);
        TextView connectionText = convertView.findViewById(R.id.connectionView);
        rsrpText.setText("RSRP value: " + String.valueOf(beanCellInfos.get(position).cellRSRP));
        mccText.setText("Cell Mcc: " + beanCellInfos.get(position).cellMcc);
        mncText.setText("Cell Mnc: " + beanCellInfos.get(position).cellMnc);
        pciText.setText("Cell Pci: " + String.valueOf(beanCellInfos.get(position).cellPci));
        tacText.setText("Cell Tac: " + String.valueOf(beanCellInfos.get(position).cellTac));
        connectionText.setText("Connection type: " + beanCellInfos.get(position).connectionType);
        return convertView;
    }
}
