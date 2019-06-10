package com.nakhmadov.diplomapp.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nakhmadov.diplomapp.Model.Room;
import com.nakhmadov.diplomapp.R;

import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomHolder> {

    Context mContext;
    List<Room> mRooms;
    private View.OnClickListener mOnItemClickListener;

    public RoomAdapter(Context mContext, List<Room> mRooms) {
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
        Room room = mRooms.get(i);
        roomHolder.roomId.setText(room.getRoomId());
        roomHolder.serverName.setText(room.getServerName());
        roomHolder.URLAddress.setText(room.getUrlAddress());

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
        TextView serverName;
        TextView URLAddress;

        public RoomHolder(@NonNull View itemView) {
            super(itemView);
            roomId = itemView.findViewById(R.id.roomId);
            serverName = itemView.findViewById(R.id.serverName);
            URLAddress = itemView.findViewById(R.id.urlAddress);

            itemView.setTag(this);
            itemView.setOnClickListener(mOnItemClickListener);
        }
    }
}
