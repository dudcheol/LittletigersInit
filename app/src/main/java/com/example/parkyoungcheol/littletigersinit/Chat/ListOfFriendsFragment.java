package com.example.parkyoungcheol.littletigersinit.Chat;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.parkyoungcheol.littletigersinit.R;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListOfFriendsFragment extends Fragment {

    @BindView(R.id.search_area) // Butter knife는 private 하면 접근 불가
            LinearLayout mSearchArea;

    @BindView(R.id.edtContent)
    EditText edtEmail;

    @BindView(R.id.friendRecyclerView)
    RecyclerView mRecyclerView;

    private FirebaseUser mFirebaseUser;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDB;

    private DatabaseReference mFriendsDBRef;
    private DatabaseReference mUserDBRef;

    private ListOfFriendAdapter mListOfFriendAdapter;

    private FirebaseAnalytics mFirebaseAnalytics;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View listOfFriendView = inflater.inflate(R.layout.fragment_list_of_friends,container, false);
        ButterKnife.bind(this, listOfFriendView);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser(); // 인증된 유저의 정보

        mFirebaseDB = FirebaseDatabase.getInstance();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
        mFriendsDBRef = mFirebaseDB.getReference("users").child(mFirebaseUser.getUid()).child("friends");
        mUserDBRef = mFirebaseDB.getReference("users");

        // 1. realtime database에서 나의 친구목록을 리스너를 통하여 데이터를 가져옴
        addFriendListener();

        mListOfFriendAdapter = new ListOfFriendAdapter();
        mRecyclerView.setAdapter(mListOfFriendAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // 3. 아이템별로 (친구) 클릭이벤트를 주어서 선택한 친구와 대화를 할 수 있도록 함
        mRecyclerView.addOnItemTouchListener(new RecyclerViewItemClickListener(getContext(), new RecyclerViewItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                final User friend = mListOfFriendAdapter.getItem(position); // 정보소실방지 final

                if (mListOfFriendAdapter.getSelectionMode() == ListOfFriendAdapter.UNSELECTION_MODE) {
                    Snackbar.make(view, friend.getName() + "님과 대화를 하시겠습니까?", Snackbar.LENGTH_LONG).setAction("예", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent chatIntent = new Intent(getActivity(), ChatActivity.class);
                            chatIntent.putExtra("uid", friend.getUid());
                            startActivityForResult(chatIntent,ChatFragment.JOIN_ROOM_REQUEST_CODE);
                        }
                    }).show();
                } else {
                    friend.setSelection(friend.isSelection() ? false : true);
                    int selectedUsersCount = mListOfFriendAdapter.getSelectionUsersCount();

                    Snackbar.make(view, selectedUsersCount + "명과 대화를 하시겠습니까?", Snackbar.LENGTH_LONG).setAction("예", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent chatIntent = new Intent(getActivity(), ChatActivity.class);
                            chatIntent.putExtra("uids", mListOfFriendAdapter.getSelectedUids());
                            startActivityForResult(chatIntent,ChatFragment.JOIN_ROOM_REQUEST_CODE);
                        }
                    }).show();
                }
            }
        }));

        return listOfFriendView;
    }

    public void toggleSearchBar(){
        mSearchArea.setVisibility(mSearchArea.getVisibility() == View.VISIBLE ? View.GONE:View.VISIBLE);
    }

    public void toggleSelectionMode(){
        mListOfFriendAdapter
                .setSelectionMode(mListOfFriendAdapter.getSelectionMode() == mListOfFriendAdapter.SELECTION_MODE ? mListOfFriendAdapter.UNSELECTION_MODE :
                        mListOfFriendAdapter.SELECTION_MODE);
    }

    @OnClick(R.id.findBtn)
    void addFriend(){
        // 1. 입력된 이메일을 가져옴
        final String inputEmail = edtEmail.getText().toString(); // toString() 으로 null 검사 완료
        // 2. 이메일이 입력되지 않았다면 이메일을 입력해주시라는 메시지를 띄워줌
        if(inputEmail.isEmpty()){
            Snackbar.make(mSearchArea,"이메일을 입력해주세요.",Snackbar.LENGTH_SHORT).show();
            return;
        }
        // 3. 자기 자신을 친구로 등록할 수 없기 때문에 FirebaseUser의 email이 입력한 이메일과 같다며, 자기자신 등록 불가 메세지를 띄워줌
        if(inputEmail.equals(mFirebaseUser.getEmail())){
            Snackbar.make(mSearchArea,"자기자신은 친구로 등록할 수 없습니다.",Snackbar.LENGTH_SHORT).show();
            return;
        }
        // 4. 이메일이 정상이라면 나의 정보를 조회하여 이미 등록된 친구인지를 판단
        mFriendsDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> friendsIterable = dataSnapshot.getChildren(); // "users/UID/friends/" + "1" + "2" + "3"
                Iterator<DataSnapshot> friendsIterator = friendsIterable.iterator();

                while (friendsIterator.hasNext()) {
                    User user = friendsIterator.next().getValue(User.class);

                    if (user.getEmail().equals(inputEmail)) {
                        Snackbar.make(mSearchArea, "이미 등록된 친구입니다.", Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                }

                // 5. users db에 존재 하지 않는 이메일이라면, 가입하지 않은 친구라는 메세지를 띄워줌
                mUserDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> userIterator = dataSnapshot.getChildren().iterator();
                        int userCount = (int) dataSnapshot.getChildrenCount(); // long -> int
                        int loopCount = 1;

                        while (userIterator.hasNext()) {
                            final User currentUser = userIterator.next().getValue(User.class); // while 루프가 끝나면 만료되기 때문에 final로 막아줌

                            if (inputEmail.equals(currentUser.getEmail())) {
                                // 친구 등록 로직
                                // 6. users/{myuid}/friends/{someone_uid}/firebasePush/상대 정보를 등록하고 // 나를 친구의 상대방에 등록
                                mFriendsDBRef.push().setValue(currentUser, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        // 7. users/{someone_uid}/friends/{myuid}/상대 정보를 등록하고
                                        // 나의 정보를 가져옴
                                        mUserDBRef.child(mFirebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                User user = dataSnapshot.getValue(User.class);
                                                mUserDBRef.child(currentUser.getUid()).child("friends").push().setValue(user);
                                                Snackbar.make(mSearchArea, "친구등록이 완료되었습니다.", Snackbar.LENGTH_SHORT).show();

                                                Bundle bundle = new Bundle();
                                                bundle.putString("me",mFirebaseUser.getEmail());
                                                bundle.putString("friend",currentUser.getEmail());
                                                mFirebaseAnalytics.logEvent("addFriend",bundle); // 분석을 위함
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                });
                            } else {
                                // 총 사용자의 명수 == loopCount
                                if (loopCount++ >= userCount) {
                                    Snackbar.make(mSearchArea, "가입을 하지 않은 친구입니다.", Snackbar.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                            // 등록된 사용자가 없다는 메시지를 출력
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        // * 승락을 추가 예정
    }

    private void addFriendListener() {
        mFriendsDBRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                // 2. 가져온 데이터를 통해서 recyclerview의 어답터에 아이템을 추가 시켜줌
                User friend = dataSnapshot.getValue(User.class);
                drawUI(friend);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void drawUI(User friend){
        mListOfFriendAdapter.addItem(friend);


    }

}
