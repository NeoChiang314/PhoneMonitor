package com.example.phonemonitor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class DataByTimeAdapter extends BaseAdapter {

    private List<DataByTimeBean> dataByTimeBeans;
    private Context context;

    public DataByTimeAdapter(List<DataByTimeBean> dataByTimeBeans, Context context) {
        this.dataByTimeBeans = dataByTimeBeans;
        this.context = context;
    }

    @Override
    public int getCount() {
        return dataByTimeBeans.size();
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

        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_data_by_time, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.dataTimeList = (TextView) convertView.findViewById(R.id.dataTimeList);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.dataTimeList.setText(dataByTimeBeans.get(position).currentTime);
        return convertView;
    }

    private class ViewHolder{
        TextView dataTimeList;
    }
}
