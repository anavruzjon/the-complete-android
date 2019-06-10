package com.nakhmadov.messageapp.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nakhmadov.messageapp.Adapter.RoomAdapter;
import com.nakhmadov.messageapp.Adapter.UserAdapter;
import com.nakhmadov.messageapp.ConferenceActivity;
import com.nakhmadov.messageapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class RoomsFragment extends Fragment {


    RecyclerView roomsRecyclerView;
    List<String> roomIds;


    public RoomsFragment() {
        // Required empty public constructor
    }


    private View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            int position = viewHolder.getAdapterPosition();

            Intent intent = new Intent(getContext(), ConferenceActivity.class);
            intent.putExtra("roomId", roomIds.get(position));
            getContext().startActivity(intent);
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_rooms, container, false);
        roomsRecyclerView = view.findViewById(R.id.roomsRecyclerView);
        roomsRecyclerView.setHasFixedSize(true);
        roomsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        roomIds = new ArrayList<>();
        roomIds.add("room-b1ad70");
        roomIds.add("room-21478e");
        roomIds.add("room-a79fda");
        roomIds.add("room-c6d773");
        roomIds.add("room-fac13a");
        roomIds.add("room-a2e009");
        roomIds.add("room-ab65b5");
        roomIds.add("room-62a7ca");
        roomIds.add("room-7fa799");

        RoomAdapter adapter = new RoomAdapter(getContext(), roomIds);
        roomsRecyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(onItemClickListener);


        return view;

    }

}
