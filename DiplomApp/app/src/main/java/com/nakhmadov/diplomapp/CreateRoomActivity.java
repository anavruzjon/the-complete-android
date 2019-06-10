package com.nakhmadov.diplomapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.flashphoner.fpwcsapi.Flashphoner;
import com.flashphoner.fpwcsapi.bean.Connection;
import com.flashphoner.fpwcsapi.room.Room;
import com.flashphoner.fpwcsapi.room.RoomManager;
import com.flashphoner.fpwcsapi.room.RoomManagerEvent;
import com.flashphoner.fpwcsapi.room.RoomManagerOptions;
import com.flashphoner.fpwcsapi.session.Stream;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nakhmadov.diplomapp.Model.User;
import com.vc.TrueConfSDK;
import com.vc.data.enums.tUserPresStatus;
import com.vc.interfaces.TrueConfListener;

import org.webrtc.SurfaceViewRenderer;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

public class CreateRoomActivity extends AppCompatActivity {

    RadioGroup serversRadioGroup;
    RadioButton flashphonerRadio;
    RadioButton trueconfRadio;

    EditText serverURLAddressTextInput;
    TextView connectionFlashphonerStatusText;
    TextView connectionTrueConfStatusText;
    Button connectToServerButton;

    EditText roomIdTextInput;
    Button enterRoomButton;

    TrueConfListener mTrueConfListener;
    boolean isConnectedFlashphoner;



    FirebaseUser firebaseUser;
    DatabaseReference reference;

    private RoomManager roomManager;

    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);


        TrueConfSDK.getInstance().logout();
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

        serversRadioGroup = (RadioGroup) findViewById(R.id.serversRadioGroup);
        flashphonerRadio = (RadioButton) findViewById(R.id.flashphonerRadio);
        trueconfRadio = (RadioButton) findViewById(R.id.trueconfRadio);

        serverURLAddressTextInput = (EditText) findViewById(R.id.serverURLAddressTextInput);
        connectionTrueConfStatusText = (TextView) findViewById(R.id.connectTrueConfToServerStatusText);
        connectionFlashphonerStatusText = (TextView) findViewById(R.id.connectFlashponerToServerStatusText);
        connectToServerButton = (Button) findViewById(R.id.connectToServerButton);

        roomIdTextInput = (EditText) findViewById(R.id.roomIdTextInput);
        enterRoomButton = (Button) findViewById(R.id.enterRoomButton);


        connectToServerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String urlString = serverURLAddressTextInput.getText().toString();
                if (TextUtils.isEmpty(urlString)) {
                    Toast.makeText(CreateRoomActivity.this, "Пожалуйста заполните все поля", Toast.LENGTH_SHORT).show();
                } else {
                    if (serversRadioGroup.getCheckedRadioButtonId() == R.id.flashphonerRadio) {
                        connectToFlashphoner(urlString);

                    } else if (serversRadioGroup.getCheckedRadioButtonId() == R.id.trueconfRadio) {
                        connectToTrueConf(urlString);
                    }
                }

                View currentFocus = getCurrentFocus();
                if (currentFocus != null) {
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });


        enterRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String roomIdString = roomIdTextInput.getText().toString();


                if (serversRadioGroup.getCheckedRadioButtonId() == R.id.flashphonerRadio) {
                    if (TextUtils.isEmpty(roomIdString)) {
                        Toast.makeText(CreateRoomActivity.this, "Пожалуйста заполните поле", Toast.LENGTH_SHORT).show();
                    } else
                        enterRoomToFlashphoner(roomIdString);


                } else if (serversRadioGroup.getCheckedRadioButtonId() == R.id.trueconfRadio) {
                    if (TextUtils.isEmpty(roomIdString)) {
                        Toast.makeText(CreateRoomActivity.this, "Пожалуйста заполните поле", Toast.LENGTH_SHORT).show();
                    } else
                        enterRoomToTrueConf(roomIdString);
                }

                View currentFocus = getCurrentFocus();
                if (currentFocus != null) {
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }

        });
        initTrueConfListener();

    }

    private void enterRoomToFlashphoner(String roomIdString) {

        Intent intent = new Intent(CreateRoomActivity.this, FlashphonerConferenceActivity.class);
        intent.putExtra("urlAddress", serverURLAddressTextInput.getText().toString());
        intent.putExtra("username", username);
        intent.putExtra("roomId", roomIdString);
        startActivity(intent);
    }

    private void enterRoomToTrueConf(String roomIdString) {
        if (TrueConfSDK.getInstance().isLoggedIn() && TrueConfSDK.getInstance().isConnectedToServer()) {

            TrueConfSDK.getInstance().joinConf(roomIdString);
        } else {
            Toast.makeText(this, "is Loggin: " + String.valueOf(TrueConfSDK.getInstance().isLoggedIn()) + "is Connect: " + String.valueOf(TrueConfSDK.getInstance().isConnectedToServer()), Toast.LENGTH_SHORT).show();

        }
    }

    public void connectToFlashphoner(String serverURL) {
        Flashphoner.init(this);
        //TODO login with firebse user;
        RoomManagerOptions roomManagerOptions = new RoomManagerOptions(serverURL, "userx");
        roomManager = Flashphoner.createRoomManager(roomManagerOptions);

        roomManager.on(new RoomManagerEvent() {
            @Override
            public void onConnected(Connection connection) {
                isConnectedFlashphoner = true;
                connectionFlashphonerStatusText.setText("Flashphoner: Подключено!");
            }

            @Override
            public void onDisconnection(Connection connection) {
                isConnectedFlashphoner = false;
                connectionFlashphonerStatusText.setText("Flashphoner: Не подключено!");

            }
        });

    }

    public void connectToTrueConf(String serverURL) {

        if (TrueConfSDK.getInstance().checkServer(serverURL).length() == 0) {
            Toast.makeText(CreateRoomActivity.this, "Не удалось подключится", Toast.LENGTH_SHORT).show();
        }

        TrueConfSDK.getInstance().start(CreateRoomActivity.this, serverURL, true);
        TrueConfSDK.getInstance().logout();
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
                /*getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onConnect();
                    }
                });*/
            }

            @Override
            public void onLogin(boolean b, String s) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });

            }

            @Override
            public void onLogout() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //onLoggedOut();
                    }
                });
            }

            @Override
            public void onServerStateChanged() {
                runOnUiThread(new Runnable() {
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
            connectionTrueConfStatusText.setText("TrueConf: Подключено!");

            TrueConfSDK.getInstance().loginAs(username, username, true, true);
            if (TrueConfSDK.getInstance().isLoggedIn()) {}

        } else {
            connectionTrueConfStatusText.setText("TrueConf: Не подключено!");
        }
    }


    private class ParticipantView {

        SurfaceViewRenderer surfaceViewRenderer;
        TextView login;

        public ParticipantView(SurfaceViewRenderer surfaceViewRenderer, TextView login) {
            this.surfaceViewRenderer = surfaceViewRenderer;
            this.login = login;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        roomManager.disconnect();
    }
}
