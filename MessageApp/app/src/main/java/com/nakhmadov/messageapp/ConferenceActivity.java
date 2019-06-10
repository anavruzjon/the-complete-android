package com.nakhmadov.messageapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class ConferenceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conference);
        String roomId = getIntent().getStringExtra("roomId");
        Toast.makeText(this, roomId, Toast.LENGTH_SHORT).show();
    }
}
