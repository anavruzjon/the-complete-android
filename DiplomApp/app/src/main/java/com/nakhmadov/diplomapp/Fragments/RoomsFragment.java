package com.nakhmadov.diplomapp.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nakhmadov.diplomapp.Adapter.RoomAdapter;
import com.nakhmadov.diplomapp.CreateRoomActivity;
import com.nakhmadov.diplomapp.FlashphonerConferenceActivity;
import com.nakhmadov.diplomapp.Model.Room;
import com.nakhmadov.diplomapp.Model.User;
import com.nakhmadov.diplomapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vc.TrueConfSDK;
import com.vc.data.enums.tUserPresStatus;
import com.vc.interfaces.TrueConfListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class RoomsFragment extends Fragment {


    FirebaseUser firebaseUser;
    DatabaseReference reference;
    String username;
    String roomId;
    RecyclerView roomsRecyclerView;
    List<Room> rooms;

    TrueConfListener mTrueConfListener;

    public RoomsFragment() {
        // Required empty public constructor
    }


    private View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            int position = viewHolder.getAdapterPosition();


            Room room = rooms.get(position);
            roomId = room.getRoomId();

            if (room.getServerName().equals("Flashphoner")) {
                Intent intent = new Intent(getContext(), FlashphonerConferenceActivity.class);
                intent.putExtra("serverName", room.getServerName());
                intent.putExtra("urlAddress", room.getUrlAddress());
                intent.putExtra("roomId", room.getRoomId());
                intent.putExtra("username", username);
                Objects.requireNonNull(getContext()).startActivity(intent);
            } else {
                //TODO HERE IS TRUE CONF LOGIC

                connectToTrueConf(room.getUrlAddress());
            }
            //getContext().startActivity(intent);
        }
    };

    private void connectToTrueConf(String urlAddress) {
        if (TrueConfSDK.getInstance().checkServer(urlAddress).length() == 0) {
            Toast.makeText(getContext(), "Не удалось подключится", Toast.LENGTH_SHORT).show();
        }

        TrueConfSDK.getInstance().start(getContext(), urlAddress, true);
        TrueConfSDK.getInstance().logout();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                assert user != null;
                username = user.getUsername();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        View view = inflater.inflate(R.layout.fragment_rooms, container, false);
        roomsRecyclerView = view.findViewById(R.id.roomsRecyclerView);
        roomsRecyclerView.setHasFixedSize(true);
        roomsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FloatingActionButton createRoomFAB = (FloatingActionButton) view.findViewById(R.id.createRoomFAB);
        createRoomFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Objects.requireNonNull(getContext()).startActivity(new Intent(getContext(), CreateRoomActivity.class));
            }
        });

        rooms = new ArrayList<>();
        rooms.add(new Room("Flashphoner", "wss://3.16.22.196:8443", "room-21478e"));
        rooms.add(new Room("Flashphoner", "wss://3.16.22.196:8443", "room-a79fda"));
        rooms.add(new Room("Flashphoner", "wss://3.16.22.196:8443", "room-c6d773"));
        rooms.add(new Room("Flashphoner", "wss://3.16.22.196:8443", "room-fac13a"));
        rooms.add(new Room("Flashphoner", "wss://3.16.22.196:8443", "room-a2e009"));
        rooms.add(new Room("Flashphoner", "wss://3.16.22.196:8443", "room-ab65b5"));
        rooms.add(new Room("Trueconf", "192.168.137.1", "9726724706"));
        rooms.add(new Room("Trueconf", "192.168.137.1", "5822496378"));
        rooms.add(new Room("Trueconf", "192.168.137.1", "0898614363"));
        rooms.add(new Room("Trueconf", "192.168.137.1", "8588496114"));

        RoomAdapter adapter = new RoomAdapter(getContext(), rooms);
        roomsRecyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(onItemClickListener);

        initTrueConfListener();

        return view;

    }


    @Override
    public void onResume() {
        super.onResume();
        TrueConfSDK.getInstance().addTrueconfListener(mTrueConfListener);
    }


    public void onPause() {
        TrueConfSDK.getInstance().removeTrueconfListener(mTrueConfListener);
        super.onPause();
    }

    private void initTrueConfListener() {

        mTrueConfListener = new TrueConfListener() {
            @Override
            public void onServerStatus(boolean b, String s, int i) {
                Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //onConnect();
                    }
                });
            }

            @Override
            public void onLogin(boolean b, String s) {
                Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });

            }

            @Override
            public void onLogout() {
                Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //onLoggedOut();
                    }
                });
            }

            @Override
            public void onServerStateChanged() {
                Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onConnectServerStatusChanged();
                    }
                });

            }

            @Override
            public void onConferenceStart() {

            }

            @Override
            public void onConferenceEnd() {

            }

            @Override
            public void onInvite(String s, String s1) {

            }

            @Override
            public void onAccept(String s, String s1) {

            }

            @Override
            public void onReject(String s, String s1) {

            }

            @Override
            public void onRejectTimeout(String s, String s1) {

            }

            @Override
            public void onUserStatusUpdate(String s, tUserPresStatus tUserPresStatus) {

            }

        };
    }

    private void onConnectServerStatusChanged() {
        if (TrueConfSDK.getInstance().isConnectedToServer()) {

            TrueConfSDK.getInstance().loginAs(username, username, true, true);
            if (TrueConfSDK.getInstance().isLoggedIn()){
                TrueConfSDK.getInstance().joinConf(roomId);
            }
        } else {
            //Toast.makeText(getContext(), "Не удалось подключится к серверу", Toast.LENGTH_SHORT).show();
        }
    }
}
