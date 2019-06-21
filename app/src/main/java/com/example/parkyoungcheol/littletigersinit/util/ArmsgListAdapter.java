package com.example.parkyoungcheol.littletigersinit.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.parkyoungcheol.littletigersinit.Chat.LocationMessage;
import com.example.parkyoungcheol.littletigersinit.Chat.Message;
import com.example.parkyoungcheol.littletigersinit.Chat.PhotoMessage;
import com.example.parkyoungcheol.littletigersinit.Chat.TextMessage;
import com.example.parkyoungcheol.littletigersinit.Model.ArmsgData;
import com.example.parkyoungcheol.littletigersinit.Navigation.AR.AR_navigationActivity;
import com.example.parkyoungcheol.littletigersinit.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ArmsgListAdapter extends RecyclerView.Adapter<ArmsgListAdapter.ViewHolder>
{
    private List<ArmsgData> mDataset;
    private Context mContext;

    public ArmsgListAdapter(Context mContext, List<ArmsgData> mBoardList) {
        this.mContext = mContext;
        this.mDataset = mBoardList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView oTextTitle;
        TextView oTextDate;
        Button oBtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            oTextTitle = (TextView) itemView.findViewById(R.id.textTitle);
            oTextDate = (TextView) itemView.findViewById(R.id.textDate);
            oBtn = (Button) itemView.findViewById(R.id.btnSelector);
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listview_item,viewGroup,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        ArmsgData data  = mDataset.get(i);
        Activity mActivity = (Activity)viewHolder.itemView.getContext();

        viewHolder.oTextTitle.setText(data.getLabel());
        viewHolder.oTextDate.setText(data.getAddress());
        viewHolder.oBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String longitude = Double.toString(data.getLongitude());
                String latitude = Double.toString(data.getLongitude());


                Intent intent = new Intent(v.getContext(), AR_navigationActivity.class);
                intent.putExtra("dest_lon_X_from_armessage", String.valueOf(longitude));
                intent.putExtra("dest_lat_Y_from_armessage", String.valueOf(latitude));
                intent.putExtra("dest_label_from_armessage", String.valueOf(data.getLabel()));

                mContext.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                mActivity.overridePendingTransition(R.anim.push_up_in,R.anim.non_anim);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}