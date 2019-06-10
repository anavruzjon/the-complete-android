package com.nakhmadov.messageapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nakhmadov.messageapp.MessageActivity;
import com.nakhmadov.messageapp.Model.Chat;
import com.nakhmadov.messageapp.Model.User;
import com.nakhmadov.messageapp.R;

import org.w3c.dom.Text;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUsers;
    private boolean isChat;

    String theLastMessage;

    public UserAdapter(Context mContext, List<User> mUsers, boolean isChat) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.isChat = isChat;


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, viewGroup, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        final User user = mUsers.get(i);
        viewHolder.username.setText(user.getUsername());
        viewHolder.usernameIsChat.setText(user.getUsername());
        if (user.getImageURL().equals("default")) {
            viewHolder.profileImage.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(mContext).load(user.getImageURL()).into(viewHolder.profileImage);
        }

        if (isChat) {
            lasMessage(user.getId(), viewHolder.lastMessage);
        } else {
            viewHolder.lastMessage.setVisibility(View.GONE);
        }

        if (isChat) {
            if (user.getStatus().equals("online")) {
                viewHolder.imageOn.setVisibility(View.VISIBLE);
                viewHolder.imageOff.setVisibility(View.GONE);
            } else {
                viewHolder.imageOn.setVisibility(View.GONE);
                viewHolder.imageOff.setVisibility(View.VISIBLE);
            }
            viewHolder.usernameIsChat.setVisibility(View.VISIBLE);
            viewHolder.username.setVisibility(View.GONE);
        } else {
            viewHolder.usernameIsChat.setVisibility(View.GONE);
            viewHolder.username.setVisibility(View.VISIBLE);
            viewHolder.imageOn.setVisibility(View.GONE);
            viewHolder.imageOff.setVisibility(View.GONE);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra("userId", user.getId());
                intent.putExtra("isChat", isChat);
                mContext.startActivity(intent);
            }
        });

    }

    private void lasMessage(final String userId, final TextView lastMessage) {
        theLastMessage = "default";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userId)
                            || chat.getReceiver().equals(userId) && chat.getSender().equals(firebaseUser.getUid())) {
                        theLastMessage = chat.getMessage();
                    }
                }

                switch (theLastMessage) {
                    case "default":
                        lastMessage.setText("No messages");
                        break;
                    default:
                        lastMessage.setText(theLastMessage);
                        break;
                }

                theLastMessage = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView username;
        public ImageView profileImage;
        private ImageView imageOn;
        private ImageView imageOff;
        private TextView lastMessage;
        private TextView usernameIsChat;


        public ViewHolder(View itemView) {
            super(itemView);


            username = itemView.findViewById(R.id.username);
            usernameIsChat = itemView.findViewById(R.id.usernameIsChat);
            profileImage = itemView.findViewById(R.id.profileImage);
            imageOn = itemView.findViewById(R.id.imageOn);
            imageOff = itemView.findViewById(R.id.imageOff);
            lastMessage = itemView.findViewById(R.id.lastMessage);
        }
    }


}
