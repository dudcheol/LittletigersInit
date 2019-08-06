package com.example.parkyoungcheol.littletigersinit.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.parkyoungcheol.littletigersinit.Model.ArmsgData;
import com.example.parkyoungcheol.littletigersinit.Model.ContentDTO;
import com.example.parkyoungcheol.littletigersinit.Navigation.AR.AR_MyMessageActivity;
import com.example.parkyoungcheol.littletigersinit.Navigation.AR.AR_navigationActivity;
import com.example.parkyoungcheol.littletigersinit.Navigation.AR.ARmessageActivity;
import com.example.parkyoungcheol.littletigersinit.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Transaction;
import com.google.firebase.firestore.DocumentReference;
import com.sackcentury.shinebuttonlib.ShineButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArmsgListAdapter extends RecyclerView.Adapter<ArmsgListAdapter.ViewHolder>
{
    private List<ArmsgData> mDataset;
    private Context mContext;
    private FirebaseDatabase mFirebaseDb;
    private AlertDialog.Builder alertDialogBuilder;
    private Activity mActivity;
    private TextView oTextTitle;
    private TextView oTextDate;
    private Button oBtn;
    private TextView likeCnt;
    private ShineButton likeBtn;
    private String currentUid;

    public ArmsgListAdapter(Context mContext, List<ArmsgData> mBoardList) {
        this.mContext = mContext;
        this.mDataset = mBoardList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
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

        currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mFirebaseDb = FirebaseDatabase.getInstance();
        mActivity = (Activity) v.getContext();

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        oTextTitle.setText(mDataset.get(i).getLabel());
        oTextDate.setText(mDataset.get(i).getAddress());
        oBtn.setText(mDataset.get(i).getDistance().toString() + "km");
        likeCnt.setText(mDataset.get(i).getLikecnt()+"");

        // 좋아요 버튼 초기 설정 (하트 채워져있는거 여부 결정)
        if(mDataset.get(i).getLikelist().contains(currentUid)){
            likeBtn.setChecked(true);
        }else{
            likeBtn.setChecked(false);
        }

        // 좋아요 버튼 클릭 시
        likeBtn.setOnClickListener(v -> {
            favoriteEvent(i);
            //notifyItemChanged(i);
        });

        // 길안내 버튼 눌렀을 시시
        oBtn.setOnClickListener(v -> {
            ArmsgData armsgData = mDataset.get(i);

            alertDialogBuilder = new AlertDialog.Builder(mActivity);

            alertDialogBuilder.setTitle("AR 메시지 내용");
            alertDialogBuilder
                    .setMessage(armsgData.getLabel())
                    .setCancelable(true)
                    .setPositiveButton("취소",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                    .setNegativeButton("삭제하기",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mFirebaseDb.getReference().child("ARMessages").child(armsgData.getKey()).removeValue()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(mActivity, "삭제 성공", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(mActivity, "삭제 실패", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }
                            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        });
    }


    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    // 좋아요 이벤트 기능
    private void favoriteEvent(int i){
        DatabaseReference tsDoc = mFirebaseDb.getReference("ARMessages");

        if (mDataset.get(i).getLikelist().contains(currentUid)) {
            Log.v("어댑터 좋아요 테스트","좋아요 취소");
            mDataset.get(i).setLikecnt(mDataset.get(i).getLikecnt()-1);
            ArrayList<String> changeData = mDataset.get(i).getLikelist();
            changeData.remove(currentUid);
            mDataset.get(i).setLikelist(changeData);
        } else {
            Log.v("어댑터 좋아요 테스트","좋아요 등록");
            mDataset.get(i).setLikecnt(mDataset.get(i).getLikecnt()+1);
            ArrayList<String> changeData = mDataset.get(i).getLikelist();
            changeData.add(currentUid);
            mDataset.get(i).setLikelist(changeData);
        }

        Map<String,Object> updates = new HashMap<>();
        updates.put(mDataset.get(i).getKey(),mDataset.get(i));
        tsDoc.updateChildren(updates);
    }
}