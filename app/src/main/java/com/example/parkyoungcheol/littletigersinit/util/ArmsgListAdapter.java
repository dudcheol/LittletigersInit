package com.example.parkyoungcheol.littletigersinit.util;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.parkyoungcheol.littletigersinit.Model.ArmsgData;
import com.example.parkyoungcheol.littletigersinit.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ArmsgListAdapter extends RecyclerView.Adapter<ArmsgListAdapter.ViewHolder>
{
    private List<ArmsgData> mDataset;
    private Context mContext;
    private FirebaseUser mFirebaseUser;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDb;

    public ArmsgListAdapter(Context mContext, List<ArmsgData> mBoardList) {
        this.mContext = mContext;
        this.mDataset = mBoardList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView oTextTitle;
        TextView oTextDate;
        Button oBtn;
        TextView likeCnt;
        ImageView likeBtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            oTextTitle = (TextView) itemView.findViewById(R.id.textTitle);
            oTextDate = (TextView) itemView.findViewById(R.id.textDate);
            oBtn = (Button) itemView.findViewById(R.id.btnSelector);
            likeCnt = (TextView) itemView.findViewById(R.id.favorite_count);
            likeBtn = itemView.findViewById(R.id.favorite_btn);
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.armsg_list_item,viewGroup,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mFirebaseDb = FirebaseDatabase.getInstance();

        ArmsgData data  = mDataset.get(i);
        Activity mActivity = (Activity)viewHolder.itemView.getContext();

        viewHolder.oTextTitle.setText(data.getLabel());
        viewHolder.oTextDate.setText(data.getAddress());
        viewHolder.oBtn.setText(data.getDistance().toString()+"km");
        viewHolder.likeCnt.setText(data.getLikecnt()+"");
        viewHolder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mActivity, "좋아요누름", Toast.LENGTH_SHORT).show();

                mFirebaseDb.getReference().child("ARMessages").child(data.getKey()).child("likelist").push().setValue(data.getUid());


            }
        });
    }


    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}