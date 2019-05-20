package com.example.parkyoungcheol.littletigersinit.Navigation.SNS;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import java.util.List;

public class ArmsgFragment extends Fragment implements View.OnClickListener {

    private FirebaseDatabase mFirebaseDb;
    private DatabaseReference mARMessageRef;

    private ListView m_oListView = null;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View armsgView = inflater.inflate(R.layout.fragment_armsg, container, false);

        mFirebaseDb = FirebaseDatabase.getInstance();
        mARMessageRef = mFirebaseDb.getReference("ARMessages");


        ArrayList<ArmsgData> oData = new ArrayList<ArmsgData>();
        ArmsgData oItem = new ArmsgData();
        mARMessageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                oData.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ArmsgData bbs = snapshot.getValue(ArmsgData.class); // 컨버팅되서 Bbs로........
                    m_oListView = (ListView) armsgView.findViewById(R.id.listView);
                    ArmsgListAdapter oAdapter = new ArmsgListAdapter(oData);
                    oAdapter.addItem(bbs.getLabel(), bbs.getLatitude(), bbs.getLongitude());
                    m_oListView.setAdapter(oAdapter);


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        return armsgView;
    }

    public void onClick(View v) {
        View oParentView = (View) v.getParent(); // 부모의 View를 가져온다. 즉, 아이템 View임.
        TextView oTextTitle = (TextView) oParentView.findViewById(R.id.textTitle);
        String position = (String) oParentView.getTag();

        AlertDialog.Builder oDialog = new AlertDialog.Builder(getActivity());

        String strMsg = "선택한 아이템의 position 은 " + position + " 입니다.\nTitle 텍스트 :" + oTextTitle.getText();
        oDialog.setMessage(strMsg)
                .setPositiveButton("확인", null)
                .setCancelable(false) // 백버튼으로 팝업창이 닫히지 않도록 한다.
                .show();
    }

}