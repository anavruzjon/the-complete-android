package com.nakhmadov.diplomapp;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.flashphoner.fpwcsapi.Flashphoner;
import com.flashphoner.fpwcsapi.bean.Connection;
import com.flashphoner.fpwcsapi.bean.Data;
import com.flashphoner.fpwcsapi.bean.StreamStatus;
import com.flashphoner.fpwcsapi.layout.PercentFrameLayout;
import com.flashphoner.fpwcsapi.room.Message;
import com.flashphoner.fpwcsapi.room.Participant;
import com.flashphoner.fpwcsapi.room.Room;
import com.flashphoner.fpwcsapi.room.RoomEvent;
import com.flashphoner.fpwcsapi.room.RoomManager;
import com.flashphoner.fpwcsapi.room.RoomManagerEvent;
import com.flashphoner.fpwcsapi.room.RoomManagerOptions;
import com.flashphoner.fpwcsapi.room.RoomOptions;
import com.flashphoner.fpwcsapi.session.RestAppCommunicator;
import com.flashphoner.fpwcsapi.session.Stream;
import com.flashphoner.fpwcsapi.session.StreamOptions;
import com.flashphoner.fpwcsapi.session.StreamStatusEvent;
//import com.flashphoner.wcsexample.conference.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nakhmadov.diplomapp.Model.User;

import org.webrtc.RendererCommon;
import org.webrtc.SurfaceViewRenderer;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;


public class FlashphonerConferenceActivity extends AppCompatActivity {


    private static final int PUBLISH_REQUEST_CODE = 100;
    FirebaseUser firebaseUser;
    DatabaseReference reference;

    String urlAddress;
    String roomId;
    String username;
    String connectTag;
    String joinTag;
    String publishTag;

    // Объявление элементов интерфейса
    private Button mJoinButton;
    private TextView mParticipant1Name;
    private TextView mParticipant2Name;
    private TextView mParticipant3Name;
    private TextView mParticipant4Name;
    private Button mPublishButton;
    private Switch mMuteAudio;
    private Switch mMuteVideo;

    private RoomManager roomManager;
    private Room room;
    private SurfaceViewRenderer localRenderer;
    private Queue<ParticipantView> freeViews = new LinkedList<>();
    private Map<String, ParticipantView> busyViews = new ConcurrentHashMap<>();
    private Stream stream;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashphoner_conference);

        // Получение данных комнаты
        Intent intent = getIntent();
        roomId = intent.getStringExtra("roomId");
        username = intent.getStringExtra("username");
        urlAddress = intent.getStringExtra("urlAddress");

        Flashphoner.init(this);

        // Если еще нет подключения, то подключаемся к серверу
        if (connectTag == null || connectTag.equals(getResources().getString(R.string.action_disconnect))) {


            RoomManagerOptions roomManagerOptions = new RoomManagerOptions(urlAddress, username);
            roomManager = Flashphoner.createRoomManager(roomManagerOptions);
            roomManager.on(new RoomManagerEvent() {
                // Когда подключены к серверу
                @Override
                public void onConnected(final Connection connection) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            enterRoom();
                        }
                    });
                }
                // Когда отключились от сервера
                @Override
                public void onDisconnection(final Connection connection) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            connectTag = getResources().getString(R.string.action_connect);
                            joinTag = getResources().getString(R.string.action_join);
                            publishTag = getResources().getString(R.string.action_publish);
                            mPublishButton.setEnabled(false);

                            Iterator<Map.Entry<String, ParticipantView>> i = busyViews.entrySet().iterator();
                            while (i.hasNext()) {
                                Map.Entry<String, ParticipantView> e = i.next();
                                e.getValue().login.setText("Пусто");
                                e.getValue().surfaceViewRenderer.release();
                                i.remove();
                                freeViews.add(e.getValue());
                            }

                        }
                    });
                }
            });

        }


        mPublishButton = (Button) findViewById(R.id.publish_button);

        // Обработчик нажатия на кнопку транслировать видео
        mPublishButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (publishTag == null || publishTag.equals(getResources().getString(R.string.action_publish))) {
                    ActivityCompat.requestPermissions(FlashphonerConferenceActivity.this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA},  PUBLISH_REQUEST_CODE);
                } else {

                    mPublishButton.setEnabled(false);
                    room.unpublish();
                }

            }
        });
        // Обработчик нажатия на изменения состояни
        mMuteAudio = (Switch) findViewById(R.id.mute_audio);
        mMuteAudio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    stream.muteAudio();
                } else {
                    stream.unmuteAudio();
                }
            }
        });

        mMuteVideo = (Switch) findViewById(R.id.mute_video);
        mMuteVideo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    stream.muteVideo();
                } else {
                    stream.unmuteVideo();
                }
            }
        });


        localRenderer = (SurfaceViewRenderer) findViewById(R.id.local_video_view);
        PercentFrameLayout localRenderLayout = (PercentFrameLayout) findViewById(R.id.local_video_layout);
        localRenderLayout.setPosition(0, 0, 100, 100);
        localRenderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        localRenderer.setMirror(true);
        localRenderer.requestLayout();


        SurfaceViewRenderer remote1Render = (SurfaceViewRenderer) findViewById(R.id.remote1_video_view);
        PercentFrameLayout remote1RenderLayout = (PercentFrameLayout) findViewById(R.id.remote1_video_layout);
        remote1RenderLayout.setPosition(0, 0, 100, 100);
        remote1Render.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        remote1Render.setMirror(false);
        remote1Render.requestLayout();

        SurfaceViewRenderer remote2Render = (SurfaceViewRenderer) findViewById(R.id.remote2_video_view);
        PercentFrameLayout remote2RenderLayout = (PercentFrameLayout) findViewById(R.id.remote2_video_layout);
        remote2RenderLayout.setPosition(0, 0, 100, 100);
        remote2Render.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        remote2Render.setMirror(false);
        remote2Render.requestLayout();


        SurfaceViewRenderer remote3Render = (SurfaceViewRenderer) findViewById(R.id.remote3_video_view);
        PercentFrameLayout remote3RenderLayout = (PercentFrameLayout) findViewById(R.id.remote3_video_layout);
        remote3RenderLayout.setPosition(0, 0, 100, 100);
        remote3Render.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        remote3Render.setMirror(false);
        remote3Render.requestLayout();

        SurfaceViewRenderer remote4Render = (SurfaceViewRenderer) findViewById(R.id.remote4_video_view);
        PercentFrameLayout remote4RenderLayout = (PercentFrameLayout) findViewById(R.id.remote4_video_layout);
        remote4RenderLayout.setPosition(0, 0, 100, 100);
        remote4Render.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        remote4Render.setMirror(false);
        remote4Render.requestLayout();


        mParticipant1Name = (TextView) findViewById(R.id.participant1_name);
        freeViews.add(new ParticipantView(remote1Render, mParticipant1Name));
        mParticipant2Name = (TextView) findViewById(R.id.participant2_name);
        freeViews.add(new ParticipantView(remote2Render, mParticipant2Name));
        mParticipant3Name = (TextView) findViewById(R.id.participant3_name);
        freeViews.add(new ParticipantView(remote3Render, mParticipant3Name));
        mParticipant4Name = (TextView) findViewById(R.id.participant4_name);
        freeViews.add(new ParticipantView(remote4Render, mParticipant4Name));

    }

    public void enterRoom() {

        if (joinTag == null || joinTag.equals(getResources().getString(R.string.action_join))) {

            RoomOptions roomOptions = new RoomOptions();
            roomOptions.setName(roomId);
            room = roomManager.join(roomOptions);
            room.on(new RoomEvent() {
                @Override
                public void onState(final Room room) {
                    if (room.getParticipants().size() >= 5) {
                        room.leave(null);
                        runOnUiThread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(FlashphonerConferenceActivity.this, "Комната заполнена", Toast.LENGTH_SHORT).show();
                                    }
                                }
                        );
                        return;
                    }


                    for (final Participant participant : room.getParticipants()) {

                        final ParticipantView participantView = freeViews.poll();
                        if (participantView != null) {

                            busyViews.put(participant.getName(), participantView);

                            runOnUiThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            participant.play(participantView.surfaceViewRenderer);
                                            participantView.login.setText(participant.getName());
                                        }
                                    }
                            );
                        }
                    }
                    runOnUiThread(
                            new Runnable() {
                                @Override
                                public void run() {
                                    joinTag = getResources().getString(R.string.action_leave);
                                    mPublishButton.setEnabled(true);

                                }
                            }
                    );
                }

                @Override
                public void onJoined(final Participant participant) {

                    final ParticipantView participantView = freeViews.poll();
                    if (participantView != null) {
                        runOnUiThread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        participantView.login.setText(participant.getName());
                                        Toast.makeText(FlashphonerConferenceActivity.this, participant.getName() + " присоединился", Toast.LENGTH_LONG).show();
                                    }
                                }
                        );
                        busyViews.put(participant.getName(), participantView);
                    }
                }

                @Override
                public void onLeft(final Participant participant) {

                    final ParticipantView participantView = busyViews.remove(participant.getName());
                    if (participantView != null) {
                        runOnUiThread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(FlashphonerConferenceActivity.this, participant + " покинул комнату", Toast.LENGTH_SHORT).show();
                                        participantView.login.setText("Пусто");
                                        participantView.surfaceViewRenderer.release();
                                    }
                                }
                        );
                        freeViews.add(participantView);
                    }
                }

                @Override
                public void onPublished(final Participant participant) {

                    final ParticipantView participantView = busyViews.get(participant.getName());
                    if (participantView != null) {
                        runOnUiThread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        participant.play(participantView.surfaceViewRenderer);
                                    }
                                }
                        );
                    }
                }

                @Override
                public void onFailed(Room room, final String info) {
                    room.leave(null);
                    runOnUiThread(
                            new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(FlashphonerConferenceActivity.this, info, Toast.LENGTH_LONG).show();

                                }
                            }
                    );
                }

                @Override
                public void onMessage(Message message) {

                }


            });

        } else {
            final Runnable action = new Runnable() {
                @Override
                public void run() {
                    joinTag = getResources().getString(R.string.action_join);

                    mPublishButton.setEnabled(false);

                    Iterator<Map.Entry<String, ParticipantView>> i = busyViews.entrySet().iterator();
                    while (i.hasNext()) {
                        Map.Entry<String, ParticipantView> e = i.next();
                        e.getValue().login.setText("Пусто");
                        e.getValue().surfaceViewRenderer.release();
                        i.remove();
                        freeViews.add(e.getValue());
                    }
                }
            };

            room.leave(new RestAppCommunicator.Handler() {
                @Override
                public void onAccepted(Data data) {
                    runOnUiThread(action);
                }

                @Override
                public void onRejected(Data data) {
                    runOnUiThread(action);
                }
            });
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PUBLISH_REQUEST_CODE: {
                if (grantResults.length == 0 ||
                        grantResults[0] != PackageManager.PERMISSION_GRANTED ||
                        grantResults[1] != PackageManager.PERMISSION_GRANTED) {

                } else {
                    mPublishButton.setEnabled(false);

                    StreamOptions streamOptions = new StreamOptions();
                    stream = room.publish(localRenderer, streamOptions);

                    stream.on(new StreamStatusEvent() {
                        @Override
                        public void onStreamStatus(final Stream stream, final StreamStatus streamStatus) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (StreamStatus.PUBLISHING.equals(streamStatus)) {
                                        mPublishButton.setText(R.string.action_stop);
                                        publishTag = getResources().getString(R.string.action_stop);
                                        mMuteAudio.setEnabled(true);
                                        mMuteVideo.setEnabled(true);
                                    } else {
                                        mPublishButton.setText(R.string.action_publish);
                                        publishTag = getResources().getString(R.string.action_publish);
                                        mMuteAudio.setEnabled(false);
                                        mMuteAudio.setChecked(false);
                                        mMuteVideo.setEnabled(false);
                                        mMuteVideo.setChecked(false);
                                        FlashphonerConferenceActivity.this.stream = null;
                                    }
                                    if (joinTag == null || joinTag.equals(getResources().getString(R.string.action_join))){
                                        mPublishButton.setEnabled(false);
                                    } else {
                                        mPublishButton.setEnabled(true);
                                    }
                                }
                            });
                        }
                    });
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (roomManager != null) {
            roomManager.disconnect();
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

}
