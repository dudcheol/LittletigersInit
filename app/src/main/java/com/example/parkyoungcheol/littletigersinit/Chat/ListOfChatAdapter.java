package com.example.parkyoungcheol.littletigersinit.Chat;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.parkyoungcheol.littletigersinit.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListOfChatAdapter extends RecyclerView.Adapter<ListOfChatAdapter.ChatHolder> {
    private ArrayList<Chat> mChatList;
    private String mChatId;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseDatabase mFirebaseDb;
    private DatabaseReference mChatMemeberRef;
    private FirebaseUser mFirebaseUser;

    private SimpleDateFormat sdf = new SimpleDateFormat("MM/dd\naa hh:mm");
    private ChatFragment mChatFragment;

    public ListOfChatAdapter() {
        mChatList = new ArrayList<>();
    }

    public void setFragment(ChatFragment chatFragment) {
        this.mChatFragment = chatFragment;
    }

    public void addItem(Chat chat) {
        mChatList.add(chat);
        notifyDataSetChanged();
    }

    public void removeItem(Chat chat) {
        int position = getItemPosition(chat.getChatId());
        if (position > -1) {
            mChatList.remove(position);
            notifyDataSetChanged();
        }
    }

    public void updateItem(Chat chat) {
        int changedItemPosition = getItemPosition(chat.getChatId());
        if (changedItemPosition > -1) {
            mChatList.set(changedItemPosition, chat);
            notifyItemChanged(changedItemPosition);
        }
    }

    public Chat getItem(int position) {
        return this.mChatList.get(position);
    }

    private int getItemPosition(String chatId) {
        int position = 0;
        for (Chat currItem : mChatList) {
            if (currItem.getChatId().equals(chatId)) {
                return position;
            }
            position++;
        }
        return -1;
    }

    public Chat getItem(String chatId) {
        return getItem(getItemPosition(chatId));
    }

    @Override
    public ChatHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_chat_item, parent, false);
        return new ChatHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatHolder holder, int position) {
        final Chat item = getItem(position);
        mFirebaseDb = FirebaseDatabase.getInstance();
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mChatId = item.getChatId();
        mChatMemeberRef = mFirebaseDb.getReference("chat_members").child(mChatId);

        mChatMemeberRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int cnt = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.getKey().contains(mFirebaseUser.getUid())) {

                    }
                    else {
                        cnt++;
                        db.collection("profileImages").whereGreaterThanOrEqualTo("image", "a").get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                //int cnt = 0;
                                                if (document.getData().get("image").toString().contains(snapshot.getKey())) {
                                                    Glide.with(holder.itemView)
                                                            .load(document.getData().get("image").toString())
                                                            .apply(RequestOptions.circleCropTransform())
                                                            .into(holder.chatThumbnailView);
                                                    break;
                                                }
                                                else {
                                                    Glide.with(holder.itemView)
                                                            .load("https://i.imgur.com/jCxAEpA.jpg")
                                                            .apply(RequestOptions.circleCropTransform())
                                                            .into(holder.chatThumbnailView);
                                                }

                                            }
                                        }
                                    }
                                });
                    }
                    if(cnt>1){
                        Glide.with(holder.itemView)
                                .load("https://i.imgur.com/jCxAEpA.jpg")
                                .apply(RequestOptions.circleCropTransform())
                                .into(holder.chatThumbnailView);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (item.getLastMessage() != null) {

            if (item.getLastMessage().getMessageType() == Message.MessageType.TEXT) {
                holder.lastMessageView.setText(item.getLastMessage().getMessageText());
            } else if (item.getLastMessage().getMessageType() == Message.MessageType.LOCATION){
                holder.lastMessageView.setText("(위치)");
            } else if (item.getLastMessage().getMessageType() == Message.MessageType.PHOTO) {
                holder.lastMessageView.setText("(사진)");
            } else if (item.getLastMessage().getMessageType() == Message.MessageType.EXIT) {
                holder.lastMessageView.setText(String.format("%s님이 방에서 나가셨습니다.", item.getLastMessage().getMessageUser().getName()));
            }

            holder.lastMessageDateView.setText(sdf.format(item.getLastMessage().getMessageDate()));
        }


        holder.titleView.setText(item.getTitle());
        holder.rootView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mChatFragment != null) {
                    mChatFragment.leaveChat(item);
                }
                return true;
            }
        });

        if (item.getTotalUnreadCount() > 0) {
            holder.totalUnreadCountView.setText(String.valueOf(item.getTotalUnreadCount()));
            holder.totalUnreadCountView.setVisibility(View.VISIBLE);
        } else {
            holder.totalUnreadCountView.setVisibility(View.GONE);
            holder.totalUnreadCountView.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return mChatList.size();
    }

    public static class ChatHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.thumb)
        RoundedImageView chatThumbnailView;

        @BindView(R.id.title)
        TextView titleView;

        @BindView(R.id.lastmessage)
        TextView lastMessageView;

        @BindView(R.id.totalUnreadCount)
        TextView totalUnreadCountView;

        @BindView(R.id.lastMsgDate)
        TextView lastMessageDateView;

        @BindView(R.id.rootView)
        LinearLayout rootView;


        public ChatHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}


