package com.example.parkyoungcheol.littletigersinit.Navigation.SNS;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.parkyoungcheol.littletigersinit.MainActivity;
import com.example.parkyoungcheol.littletigersinit.Model.AlarmDTO;
import com.example.parkyoungcheol.littletigersinit.Model.ContentDTO;
import com.example.parkyoungcheol.littletigersinit.Navigation.AR.UnityPlayerActivity;
import com.example.parkyoungcheol.littletigersinit.R;
import com.example.parkyoungcheol.littletigersinit.util.FcmPush;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sackcentury.shinebuttonlib.ShineButton;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.Nullable;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserDetailViewFragment extends Fragment {
    // 현재 로그인되어 있는 유저 정보
    FirebaseUser user;
    FirebaseFirestore firestore;
    String currentUid=null;

    // 지금 보고있는 게시글을 게시한 유저의 uid
    String destinationUid = null;

    // 지금 보고있는 게시글의 이미지 url
    String imageUrl = null;

    // 게시글의 uid
    String contentUid = null;

    private TextView userId, like, explain;
    private ImageView userProfileImage, detailImage;
    private ShineButton likeBtn;
    Button deleteBtn;
    int likeCount=0;
    int cnt=0;

    private ContentDTO contentDTO;
    FcmPush fcmPush;

    public UserDetailViewFragment() {
        // Required empty public constructor
    }

    private ProgressDialog pDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_detail_view, container, false);

        userId = view.findViewById(R.id.detailviewitem_profile_textview);
        like = view.findViewById(R.id.detailviewitem_favoritecounter_textview);
        explain = view.findViewById(R.id.detailviewitem_explain_textview);
        userProfileImage = view.findViewById(R.id.detailviewitem_profile_image);
        detailImage = view.findViewById(R.id.detailviewitem_imageview_content);
        likeBtn = view.findViewById(R.id.detailviewitem_favorite_imageview);
        deleteBtn=view.findViewById(R.id.deleteBtn);

        user = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
        fcmPush = new FcmPush();

        if(getArguments()!=null) {
            imageUrl = getArguments().getString("imageUrl");
            destinationUid = getArguments().getString("uid");
            contentUid = getArguments().getString("contentUid");
        }
        currentUid=FirebaseAuth.getInstance().getCurrentUser().getUid();


        // 메인액티비티 이미지 변경
        MainActivity mainActivity = (MainActivity)getActivity();
        ImageView mainImage =  mainActivity.findViewById(R.id.toolbar_title_image);
        ImageView ARbutton = mainActivity.findViewById(R.id.ARmessageBtn);
        ImageView ARbutton2 = mainActivity.findViewById(R.id.ARbtn);
        ImageView chatbtn = mainActivity.findViewById(R.id.ChatBtn);
        ImageView backBtn = mainActivity.findViewById(R.id.toolbar_btn_back);
        TextView mainText = mainActivity.findViewById(R.id.toolbar_username);
        BottomNavigationView navigationView = mainActivity.findViewById(R.id.bottom_navigation);

        mainImage.setVisibility(View.GONE);
        ARbutton.setVisibility(View.GONE);
        ARbutton2.setVisibility(View.GONE);
        chatbtn.setVisibility(View.GONE);
        backBtn.setVisibility(View.VISIBLE);
        mainText.setVisibility(View.VISIBLE);

        backBtn.setOnClickListener(v->{
            navigationView.setSelectedItemId(R.id.action_home);
        });

        ImageView commentBtn = view.findViewById(R.id.detailviewitem_comment_imageview);
        commentBtn.setOnClickListener(v->{
            Intent intent = new Intent(getActivity(), CommentActivity.class);
            intent.putExtra("contentUid", contentUid);
            intent.putExtra("destinationUid", destinationUid);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_right,R.anim.non_anim);
        });

        deleteBtn.setVisibility(View.VISIBLE);

        // 내 게시글일 경우 삭제버튼이 보이게 하고
        if(currentUid!=null && currentUid.equals(destinationUid)){
            deleteBtn.setVisibility(View.VISIBLE);
        }else{
            // 내 게시글이 아닐 경우 삭제버튼 안보이게함
            deleteBtn.setVisibility(View.GONE);
        }

        // 게시글 삭제
        deleteBtn.setOnClickListener(v->{
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

            alertDialogBuilder.setTitle("<!>");
            alertDialogBuilder
                    .setMessage("정말 삭제할건가요?")
                    .setCancelable(false)
                    .setPositiveButton("예",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    pDialog = new ProgressDialog(getActivity());
                                    // Showing progress dialog before making http request
                                    pDialog.setMessage("AR Loading...");
                                    pDialog.show();
                                    //파이어베이스에서 삭제
                                    firestore
                                            .collection("images")
                                            .document(contentUid)
                                            .delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(mainActivity, "삭제 성공", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(mainActivity, "삭제 실패, 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                    //로딩메시지제거
                                    if (pDialog != null) {
                                        pDialog.dismiss();
                                        pDialog = null;
                                    }
                                    navigationView.setSelectedItemId(R.id.action_account);
                                }
                            })
                    .setNegativeButton("아니요",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        });


        // 프로필 이미지 가져오기
        firestore
                .collection("profileImages")
                .document(destinationUid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().get("image") != null) {
                            String url = task.getResult().get("image").toString();
                            Glide.with(view)
                                    .load(url)
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(userProfileImage);
                        } else {
                            userProfileImage.setImageResource(R.drawable.ic_account);
                        }
                    } else {
                        getActivity().onBackPressed();
                        Toast.makeText(getActivity(), "다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                    }
                });


        // 디테일한 정보 가져오기
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
                                contentDTO = documentSnapshot.toObject(ContentDTO.class);
                                userId.setText(contentDTO.getUserId());
                                like.setText(contentDTO.getFavoriteCount()+"명");
                                likeCount=contentDTO.getFavoriteCount();
                                explain.setText(contentDTO.getExplain());
                                Glide.with(view)
                                        .load(contentDTO.getImageUrl())
                                        .into(detailImage);
                                mainText.setText(contentDTO.getUserId()+"님의 게시글");
                            }

                            // 좋아요 버튼 설정
                            if (contentDTO.getFavorites().containsKey(currentUid)) {
                                likeBtn.setChecked(true);
                                cnt=0;
                            }else{
                                likeBtn.setChecked(false);
                                cnt=1;
                            }
                        }
                        // 가져오는거 에러났을 때
                        else {
                            getActivity().onBackPressed();
                            Toast.makeText(getActivity(), "다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        // 좋아요 이벤트
        likeBtn.setOnClickListener(v -> {
            favoriteEvent();
        });

        // 좋아요 카운트 이벤트 발생시
        firestore
                .collection("images")
                .document(contentUid)
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        return;
                    }

                    if (snapshot != null && snapshot.exists()) {
                        Log.v("좋아요 수 테스트",snapshot.getData().toString());
                        ContentDTO contentDTO = snapshot.toObject(ContentDTO.class);
                        like.setText(contentDTO.getFavoriteCount()+"명");
                    } else {

                    }
                });

        return view;
    }

    // 좋아요 알림
    void favoriteAlarm(String destinationUid){
        AlarmDTO alarmDTO = new AlarmDTO();
        alarmDTO.setDestinationUid(destinationUid);
        alarmDTO.setUserId(user.getEmail());
        alarmDTO.setUid(user.getUid());
        alarmDTO.setKind(0);
        alarmDTO.setTimestamp(System.currentTimeMillis());

        FirebaseFirestore.getInstance().collection("alarms").document().set(alarmDTO);
        String message = user.getEmail() + "님이 좋아요를 눌렀습니다.";
        fcmPush.sendMessage(destinationUid, "알림 메시지 입니다.",message);
    }

    // 좋아요 이벤트 기능
    private void favoriteEvent(){
        Log.v("좋아요 테스트","contentUid: "+contentUid);
        DocumentReference tsDoc = firestore.collection("images").document(contentUid);
        firestore.runTransaction(transaction -> {
            String uid = currentUid;
            ContentDTO contentDTO = transaction.get(tsDoc).toObject(ContentDTO.class);

            if (contentDTO.getFavorites().containsKey(uid)) {
                Log.v("좋아요 테스트1","좋아요 취소");
                contentDTO.setFavoriteCount(contentDTO.getFavoriteCount() - 1);
                contentDTO.getFavorites().remove(uid);
            } else {
                Log.v("좋아요 테스트2","좋아요 등록");
                contentDTO.setFavoriteCount(contentDTO.getFavoriteCount() + 1);
                HashMap<String,Boolean> hashMap = new HashMap<String,Boolean>();
                hashMap.put(uid,true);
                contentDTO.setFavorites(hashMap);
                favoriteAlarm(destinationUid);
            }
            transaction.set(tsDoc, contentDTO);
            return null;
        });
    }

}
