package com.example.parkyoungcheol.littletigersinit.Chat;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.parkyoungcheol.littletigersinit.Navigation.AR.AR_navigationActivity;
import com.example.parkyoungcheol.littletigersinit.R;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.MessageViewHolder> {
    // VIew 가 사용 될 때

    private ArrayList<Message> mMessageList;

    private SimpleDateFormat messageDateFormat = new SimpleDateFormat("MM/dd a\n hh:mm");

    private String userId;

    private Context mContext;

    public String longitude= "";
    public String latitude= "";
    public String longitude2= "";
    public String latitude2= "";

    public MessageListAdapter(Context mContext, ArrayList<Message> mMessageList){
        mMessageList = new ArrayList<>();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.mContext = mContext;
        this.mMessageList = mMessageList;
    }
    public void addItem(Message item) {
        mMessageList.add(item);
        notifyDataSetChanged();
    }

    public void updateItem(Message item) {
        int position = getItemPosition(item.getMessageId());
        if (position < 0) {
            return;
        }
        mMessageList.set(position, item);
        notifyItemChanged(position);
    }

    public void clearItem() {
        mMessageList.clear();
    }

    private int getItemPosition(String messageId) {
        int position = 0;
        for (Message message : mMessageList) {
            if (message.getMessageId().equals(messageId)) {
                return position;
            }
            position++;
        }
        return -1;
    }

    public Message getItem(int position) {
        return mMessageList.get(position);
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_message_item, parent, false);
        // view 를 이용한 뷰홀더 리턴
        return new MessageViewHolder(view);
    }


    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        // 전달받은 뷰 홀터를 이용한 뷰 구현
        Message item = getItem(position);

        TextMessage textMessage = null;
        PhotoMessage photoMessage = null;
        LocationMessage locationMessage = null;


        if (item instanceof TextMessage) {
            textMessage = (TextMessage) item;
        } else if (item instanceof PhotoMessage) {
            photoMessage = (PhotoMessage) item;
        } else if (item instanceof LocationMessage){
            locationMessage = (LocationMessage) item;
        }

        // 내가 보낸 메세지 인지, 받은 메세지 인지 판별 합니다.

        if (userId.equals(item.getMessageUser().getUid())) {
            // 내가 보낸 메시지 구현
            // 텍스트 메세지 인지 포토 메세지 인지 구별
            if (item.getMessageType() == Message.MessageType.TEXT) {
                holder.sendTxt.setText(textMessage.getMessageText());
                holder.sendTxt.setVisibility(View.VISIBLE);
                holder.sendImage.setVisibility(View.GONE);
                holder.sendLocatoin.setVisibility(View.GONE);

            } else if (item.getMessageType() == Message.MessageType.PHOTO) {
                Glide.with(holder.sendArea)
                        .load(photoMessage.getPhotoUrl())
                        .into(holder.sendImage);

                holder.sendTxt.setVisibility(View.GONE);
                holder.sendImage.setVisibility(View.VISIBLE);
                holder.sendLocatoin.setVisibility(View.GONE);
            } else if (item.getMessageType() == Message.MessageType.LOCATION) {
                holder.sendTxt.setText(locationMessage.getLocationText());

                holder.sendTxt.setVisibility(View.VISIBLE);
                holder.sendImage.setVisibility(View.GONE);
                holder.sendLocatoin.setVisibility(View.GONE);

            }

            if (item.getUnreadCount() > 0) {
                holder.sendUnreadCount.setText(String.valueOf(item.getUnreadCount()));
            } else {
                holder.sendUnreadCount.setText("");
            }
            holder.sendDate.setText(messageDateFormat.format(item.getMessageDate()));
            holder.yourArea.setVisibility(View.GONE);
            holder.sendArea.setVisibility(View.VISIBLE);
            holder.exitArea.setVisibility(View.GONE);

        } else {
            // 상대방이 보낸 경우
            if (item.getMessageType() == Message.MessageType.TEXT) {
                holder.rcvTextView.setText(textMessage.getMessageText());
                holder.rcvTextView.setVisibility(View.VISIBLE);
                holder.rcvImage.setVisibility(View.GONE);
                holder.rcvLocation.setVisibility(View.GONE);

            } else if (item.getMessageType() == Message.MessageType.PHOTO) {
                Glide
                        .with(holder.yourArea)
                        .load(photoMessage.getPhotoUrl())
                        .into(holder.rcvImage);

                holder.rcvTextView.setVisibility(View.GONE);
                holder.rcvImage.setVisibility(View.VISIBLE);
                holder.rcvLocation.setVisibility(View.GONE);
            } else if (item.getMessageType() == Message.MessageType.LOCATION){
                holder.rcvTextView.setText(locationMessage.getLocationText());
                if(locationMessage.getLocationText().equals("위치 전송 실패")){
                    holder.rcvLocation.setVisibility(View.GONE);
                    holder.rcvTextView.setVisibility(View.VISIBLE);
                    holder.rcvImage.setVisibility(View.GONE);
                }
                else {
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Message item = getItem(position);

                            TextMessage textMessage = null;
                            PhotoMessage photoMessage = null;
                            LocationMessage locationMessage = null;




                            if (item instanceof TextMessage) {
                                textMessage = (TextMessage) item;
                            } else if (item instanceof PhotoMessage) {
                                photoMessage = (PhotoMessage) item;
                            } else if (item instanceof LocationMessage) {
                                locationMessage = (LocationMessage) item;
                            }

                            if (locationMessage.getLongitude().equals("")) {
                                longitude = "";
                                latitude = "";
                                Toast.makeText(mContext, "position =" + position + " ", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(v.getContext(), AR_navigationActivity.class);
                                intent.putExtra("destination", String.valueOf(longitude + " , " + latitude));
                                mContext.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                            } else {
                                longitude = locationMessage.getLongitude();
                                latitude = locationMessage.getLatitude();
                                Toast.makeText(mContext, "position =" + position + " ", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(v.getContext(), AR_navigationActivity.class);
                                intent.putExtra("destination", String.valueOf(longitude + " , " + latitude));
                                mContext.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                            }
                        }
                    });
                    holder.rcvLocation.setVisibility(View.GONE);
                    holder.rcvTextView.setVisibility(View.VISIBLE);
                    holder.rcvImage.setVisibility(View.GONE);
                }
/*                holder.rcvLocation.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Message item = getItem(position);

                            TextMessage textMessage = null;
                            PhotoMessage photoMessage = null;
                            LocationMessage locationMessage = null;


                            if (item instanceof TextMessage) {
                                textMessage = (TextMessage) item;
                            } else if (item instanceof PhotoMessage) {
                                photoMessage = (PhotoMessage) item;
                            } else if (item instanceof LocationMessage) {
                                locationMessage = (LocationMessage) item;
                            }

                            if (locationMessage.getLongitude().equals("")) {
                                longitude = "";
                                latitude = "";
                                Toast.makeText(mContext, "position =" + position + " ", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(v.getContext(), AR_navigationActivity.class);
                                intent.putExtra("destination", String.valueOf(longitude + " , " + latitude));
                                mContext.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                            } else {
                                longitude = locationMessage.getLongitude();
                                latitude = locationMessage.getLatitude();
                                Toast.makeText(mContext, "position =" + position + " ", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(v.getContext(), AR_navigationActivity.class);
                                intent.putExtra("destination", String.valueOf(longitude + " , " + latitude));
                                mContext.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                            }
                        }
                    });*/

            } else if (item.getMessageType() == Message.MessageType.EXIT) {
                // #이름 님이 방에서 나가셨습니다.
                holder.exitTextView.setText(String.format("%s님이 방에서 나가셨습니다.", item.getMessageUser().getName()));
            }

            if (item.getUnreadCount() > 0) {
                holder.rcvUnreadCount.setText(String.valueOf(item.getUnreadCount()));
            } else {
                holder.rcvUnreadCount.setText("");
            }

            if (item.getMessageUser().getProfileUrl() != null) {
                Glide
                        .with(holder.yourArea)
                        .load(item.getMessageUser().getProfileUrl())
                        .into(holder.rcvProfileView);
            }

            if (item.getMessageType() == Message.MessageType.EXIT) {
                holder.yourArea.setVisibility(View.GONE);
                holder.sendArea.setVisibility(View.GONE);
                holder.exitArea.setVisibility(View.VISIBLE);
            } else {
                holder.rcvDate.setText(messageDateFormat.format(item.getMessageDate()));
                holder.yourArea.setVisibility(View.VISIBLE);
                holder.sendArea.setVisibility(View.GONE);
            }
        }

        longitude = "";
        latitude = "";
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.yourChatArea)
        LinearLayout yourArea;

        @BindView(R.id.myChatArea)
        LinearLayout sendArea;

        @BindView(R.id.exitArea)
        LinearLayout exitArea;

        @BindView(R.id.rcvProfile)
        RoundedImageView rcvProfileView;

        @BindView(R.id.rcvTxt)
        TextView rcvTextView;

        @BindView(R.id.exitTxt)
        TextView exitTextView;

        @BindView(R.id.rcvImage)
        ImageView rcvImage;

        @BindView(R.id.rcvLocation)
        ImageButton rcvLocation;

        @BindView(R.id.rcvUnreadCount)
        TextView rcvUnreadCount;

        @BindView(R.id.rcvDate)
        TextView rcvDate;

        @BindView(R.id.sendUnreadCount)
        TextView sendUnreadCount;

        @BindView(R.id.sendDate)
        TextView sendDate;

        @BindView(R.id.sendTxt)
        TextView sendTxt;

        @BindView(R.id.sendImage)
        ImageView sendImage;

        @BindView(R.id.sendLocation)
        ImageButton sendLocatoin;

        public MessageViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }


}