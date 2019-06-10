package com.nakhmadov.trueconfclient;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.vc.TrueConfSDK;
import com.vc.data.enums.tUserPresStatus;
import com.vc.interfaces.TrueConfListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlaceHolderFragment extends Fragment {

    EditText serverHostnameTextInput;
    EditText usernameTextInput;
    EditText passwordTextInput;
    EditText roomIdTextInput;
    TextView serverStatusText;
    TextView loginStatusText;
    Button connectToServerButton;
    Button loginButton;
    Button enterRoomButton;

    TrueConfListener mTrueConfListener;

    private final View.OnClickListener loginClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            //hideSoftInput();

            if (TextUtils.isEmpty(usernameTextInput.getText()) || TextUtils.isEmpty(passwordTextInput.getText())) {

                Toast.makeText(getContext(), "username and password must be filled", Toast.LENGTH_SHORT).show();

            } else {
                if (TrueConfSDK.getInstance().isLoggedIn()) {
                    onLoggedIn();
                } else {
                    String login = usernameTextInput.getText().toString().trim();
                    String pass = passwordTextInput.getText().toString().trim();
                    TrueConfSDK.getInstance().loginAs(login, pass, true, true);
                }
            }

        }
    };

    public PlaceHolderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        serverHostnameTextInput = view.findViewById(R.id.serverHostnameTextInput);
        usernameTextInput = view.findViewById(R.id.usernameTextInput);
        passwordTextInput = view.findViewById(R.id.passwordTextInput);
        roomIdTextInput = view.findViewById(R.id.roomIdTextInput);
        serverStatusText = view.findViewById(R.id.serverStatusText);
        loginStatusText = view.findViewById(R.id.loginStatus);
        connectToServerButton = view.findViewById(R.id.connectToServerButton);
        loginButton = view.findViewById(R.id.loginButton);
        enterRoomButton = view.findViewById(R.id.enterRoomButton);

        connectToServerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hostnameString = serverHostnameTextInput.getText().toString();
                connectToServer(hostnameString);

            }
        });


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usernameString = usernameTextInput.getText().toString().trim();
                String passwordString = passwordTextInput.getText().toString().trim();

                //usernameString = "user3";
                //passwordSting = "user3";
                //TODO check username and password
                TrueConfSDK.getInstance().loginAs(usernameString, passwordString, true, true);
            }
        });


        enterRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO check for empty
                TrueConfSDK.getInstance().joinConf(roomIdTextInput.getText().toString());
            }
        });
        return view;
    }


    public void onConnect() {
        if (TrueConfSDK.getInstance().isConnectedToServer()) {
            serverStatusText.setText("Connected");
            serverStatusText.setTextColor(Color.GREEN);
            usernameTextInput.setVisibility(View.VISIBLE);
            passwordTextInput.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.VISIBLE);
        } else {
            serverStatusText.setText("Disconnected");
            serverStatusText.setTextColor(Color.RED);
            usernameTextInput.setVisibility(View.INVISIBLE);
            passwordTextInput.setVisibility(View.INVISIBLE);
            loginButton.setVisibility(View.INVISIBLE);
            loginStatusText.setVisibility(View.INVISIBLE);
            roomIdTextInput.setVisibility(View.INVISIBLE);
            enterRoomButton.setVisibility(View.INVISIBLE);
        }

    }

    public void onLoggedIn() {
        loginStatusText.setTextColor(Color.GREEN);
        loginStatusText.setVisibility(View.VISIBLE);
        loginStatusText.setText("Logged in");
        roomIdTextInput.setVisibility(View.VISIBLE);
        enterRoomButton.setVisibility(View.VISIBLE);
    }

    public void onLoggedOut() {
        loginStatusText.setTextColor(Color.RED);
        loginStatusText.setVisibility(View.VISIBLE);
        loginStatusText.setText("Not logged in");
        roomIdTextInput.setVisibility(View.INVISIBLE);
        enterRoomButton.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initTrueConfListener();
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

    private boolean connectToServer(String server) {

        if (TextUtils.isEmpty(server)) return false;

        if (TrueConfSDK.getInstance().checkServer(server).length() == 0) {
            Toast.makeText(getContext(), "Failed to connect", Toast.LENGTH_SHORT).show();
            return false;
        }

        TrueConfSDK.getInstance().start(getContext(), server, true);
        return true;
    }



    //todo temporary solution. interface will be segregated on smaller parts
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
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onLoggedIn();
                    }
                });

            }

            @Override
            public void onLogout() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onLoggedOut();
                    }
                });
            }

            @Override
            public void onServerStateChanged() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onConnect();
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
}
