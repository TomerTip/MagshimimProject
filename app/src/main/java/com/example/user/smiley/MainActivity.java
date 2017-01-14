package com.example.user.smiley;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.webrtc.MediaConstraints;
import org.webrtc.PeerConnectionFactory;

import me.kevingleason.pnwebrtc.PnSignalingParams;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }
}
