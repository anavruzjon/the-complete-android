package com.nakhmadov.messageapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nakhmadov.messageapp.MessageActivity;
import com.nakhmadov.messageapp.Model.Chat;
import com.nakhmadov.messageapp.Model.User;
import com.nakhmadov.messageapp.R;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;

    private Context mContext;
    private List<Chat> mChat;
    private String imageURL;

    FirebaseUser firebaseUser;

    public MessageAdapter(Context mContext, List<Chat> mChat, String imageURL) {
        this.mContext = mContext;
        this.mChat = mChat;
        this.imageURL = imageURL;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = null;
        if (i == MSG_TYPE_LEFT) {
            view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, viewGroup, false);
        } else if (i == MSG_TYPE_RIGHT) {
            view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, viewGroup, false);

        }

        return new MessageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder viewHolder, int i) {
        Chat chat = mChat.get(i);

        viewHolder.showMessage.setText(chat.getMessage());
       /* if (imageURL.equals("default")) {
            viewHolder.profileImage.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(mContext).load(imageURL).into(viewHolder.profileImage);
        }*/

        if (i == mChat.size() - 1) {
            if (chat.isSeen()) {
                viewHolder.textSeen.setText("Seen");
            } else {
                viewHolder.textSeen.setText("Delivered");
            }
        } else {
            viewHolder.textSeen.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView showMessage;
        public ImageView profileImage;
        public TextView textSeen;

        public ViewHolder(View itemView) {
            super(itemView);

            showMessage = itemView.findViewById(R.id.showMessage);
            //profileImage = itemView.findViewById(R.id.profileImage);
            textSeen = itemView.findViewById(R.id.textSeen);
        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getSender().equals(firebaseUser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}