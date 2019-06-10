package com.nakhmadov.messageapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nakhmadov.messageapp.ConferenceActivity;
import com.nakhmadov.messageapp.Fragments.RoomsFragment;
import com.nakhmadov.messageapp.MainActivity;
import com.nakhmadov.messageapp.Model.User;
import com.nakhmadov.messageapp.R;

import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomHolder> {

    Context mContext;
    List<String> mRooms;
    private View.OnClickListener mOnItemClickListener;

    public RoomAdapter(Context mContext, List<String> mRooms) {
        this.mContext = mContext;
        this.mRooms = mRooms;
    }

    @NonNull
    @Override
    public RoomHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.room_item, viewGroup, false);
        return new RoomHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomHolder roomHolder, int i) {
        roomHolder.roomId.setText(mRooms.get(i));

    }

    @Override
    public int getItemCount() {
        return mRooms.size();
    }

    public void setOnItemClickListener(View.OnClickListener itemClickListener) {
        mOnItemClickListener = itemClickListener;
    }

    public class RoomHolder extends RecyclerView.ViewHolder {
        TextView roomId;

        public RoomHolder(@NonNull View itemView) {
            super(itemView);
            roomId = itemView.findViewById(R.id.roomId);

            itemView.setTag(this);
            itemView.setOnClickListener(mOnItemClickListener);
        }
    }
}
