package com.example.parkyoungcheol.littletigersinit.util;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.parkyoungcheol.littletigersinit.Model.DataSet;
import com.example.parkyoungcheol.littletigersinit.R;

import java.util.List;

public class CustomListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<DataSet> DataList;

    public CustomListAdapter(Activity activity, List<DataSet> dataitem) {
        this.activity = activity;
        this.DataList = dataitem;
    }

    @Override
    public int getCount() {
        return DataList.size();
    }

    @Override
    public Object getItem(int location) {
        return DataList.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_row, null);

        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView link = (TextView) convertView.findViewById(R.id.link);
        TextView roadAddress = (TextView) convertView.findViewById(R.id.roadAddress);
        TextView mapx = (TextView) convertView.findViewById(R.id.mapx);
        TextView mapy = (TextView) convertView.findViewById(R.id.mapy);

        DataSet m = DataList.get(position);
        title.setText(m.getTitle());
        link.setText(String.valueOf(m.getLink()));
        roadAddress.setText(String.valueOf(m.getRoadAddress()));
        mapx.setText(String.valueOf(m.getMapx()));
        mapy.setText(String.valueOf(m.getMapy()));

        return convertView;
    }
}
