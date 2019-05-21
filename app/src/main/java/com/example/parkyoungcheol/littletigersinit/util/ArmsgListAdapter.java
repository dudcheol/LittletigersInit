package com.example.parkyoungcheol.littletigersinit.util;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.parkyoungcheol.littletigersinit.Model.ArmsgData;
import com.example.parkyoungcheol.littletigersinit.R;

import java.util.ArrayList;

public class ArmsgListAdapter extends BaseAdapter
{
    LayoutInflater inflater = null;
    private ArrayList<ArmsgData> m_oData = null;
    private int nListCnt = 0;

    public ArmsgListAdapter(ArrayList<ArmsgData> _oData)
    {
        m_oData = _oData;
        nListCnt = m_oData.size();
    }

    @Override
    public int getCount()
    {
        Log.i("TAG", "getCount");
        return nListCnt;
    }

    public void addItem(String label, Double latitude, Double longitude)
    {
        ArmsgData item = new ArmsgData();

        item.setLabel(label);
        item.setLatitude(latitude);
        item.setLongitude(longitude);
        m_oData.add(item);
    }
    @Override
    public Object getItem(int position)
    {
        return m_oData.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            final Context context = parent.getContext();
            if (inflater == null)
            {
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            convertView = inflater.inflate(R.layout.listview_item, parent, false);
        }

        TextView oTextTitle = (TextView) convertView.findViewById(R.id.textTitle);
        TextView oTextDate = (TextView) convertView.findViewById(R.id.textDate);
        Button oBtn = (Button) convertView.findViewById(R.id.btnSelector);

        ArmsgData listViewItem  = m_oData.get(position);

        oTextTitle.setText(listViewItem.getLabel());
        oTextDate.setText(listViewItem.getLatitude() + " , " + listViewItem.getLongitude());


        convertView.setTag(""+position);
        return convertView;
    }
}