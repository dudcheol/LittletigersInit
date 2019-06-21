package com.example.parkyoungcheol.littletigersinit.Navigation.SNS;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.parkyoungcheol.littletigersinit.Model.ContentDTO;
import com.example.parkyoungcheol.littletigersinit.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserDetailViewFragment extends Fragment {
    FirebaseUser user;
    FirebaseFirestore firestore;

    TextView userId, like, explain;
    ImageView userProfileImage, detailImage;

    public UserDetailViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_detail_view, container, false);

        userId = view.findViewById(R.id.detailviewitem_profile_textview);
        like = view.findViewById(R.id.detailviewitem_favoritecounter_textview);
        explain = view.findViewById(R.id.detailviewitem_explain_textview);
        userProfileImage = view.findViewById(R.id.detailviewitem_profile_image);
        detailImage = view.findViewById(R.id.detailviewitem_imageview_content);

        user = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        String uid = user.getUid();
        String imageUrl=null;
        if(getArguments()!=null) {
            imageUrl = getArguments().getString("imageUrl");
        }

        firestore
                .collection("images")
                .whereEqualTo("imageUrl",imageUrl)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        // 데이터 가져오는 작업이 잘 동작했을 때
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot documentSnapshot:task.getResult()){
                                ContentDTO contentDTO = documentSnapshot.toObject(ContentDTO.class);
                                userId.setText(contentDTO.getUserId());
                                like.setText(contentDTO.getFavoriteCount());
                                explain.setText(contentDTO.getExplain());
                                Glide.with(view)
                                        .load(contentDTO.getImageUrl())
                                        .into(userProfileImage);
                            }
                        }
                        // 가져오는거 에러났을 때
                        else {
                            getActivity().onBackPressed();
                            Toast.makeText(getActivity(), "에러!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        return view;
    }

}
