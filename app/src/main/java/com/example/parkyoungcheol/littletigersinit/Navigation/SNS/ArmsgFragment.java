package com.example.parkyoungcheol.littletigersinit.Navigation.SNS;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.parkyoungcheol.littletigersinit.Chat.MessageListAdapter;
import com.example.parkyoungcheol.littletigersinit.Model.ArmsgData;
import com.example.parkyoungcheol.littletigersinit.R;
import com.example.parkyoungcheol.littletigersinit.util.ArmsgListAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArmsgFragment extends Fragment  {
    private static List<ArmsgData> mBoardList;
    private FirebaseDatabase mFirebaseDb;
    private DatabaseReference mARMessageRef;

    private RecyclerView m_oListView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public ArmsgFragment(){

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View armsgView = inflater.inflate(R.layout.fragment_armsg, container, false);

        mFirebaseDb = FirebaseDatabase.getInstance();
        mARMessageRef = mFirebaseDb.getReference("ARMessages");

        mBoardList =  new ArrayList<>();
        mARMessageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mBoardList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ArmsgData bbs = snapshot.getValue(ArmsgData.class); // 컨버팅되서 Bbs로........
                    mBoardList.add(bbs);


                }

                Collections.reverse(mBoardList);
                m_oListView = (RecyclerView) armsgView.findViewById(R.id.listView);
                mAdapter = new ArmsgListAdapter(mBoardList);
                mAdapter.notifyDataSetChanged();
                mLayoutManager = new LinearLayoutManager(armsgView.getContext());
                m_oListView.setLayoutManager(mLayoutManager);
                m_oListView.setAdapter(mAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        return armsgView;
    }

}