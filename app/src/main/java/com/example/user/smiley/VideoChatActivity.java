package com.example.user.smiley;

import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoCapturerAndroid;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoRendererGui;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import me.kevingleason.pnwebrtc.PnPeer;
import me.kevingleason.pnwebrtc.PnRTCClient;
import me.kevingleason.pnwebrtc.PnRTCListener;

public class VideoChatActivity extends AppCompatActivity {

    private PeerConnectionFactory pcFactory;

    private PnRTCClient pnRTCClient;
    private VideoSource localVideoSource;

    public static final String VIDEO_TRACK_ID = "videoPN";
    public static final String AUDIO_TRACK_ID = "audioPN";
    public static final String LOCAL_MEDIA_STREAM_ID = "localStreamPN";

    private GLSurfaceView videoView;

    private VideoRenderer.Callbacks localRender;
    private VideoRenderer.Callbacks remoteRender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_chat);

        //init the class's configs.
        PeerConnectionFactory.initializeAndroidGlobals(
                this,  // Context
                true,  // Audio Enabled
                true,  // Video Enabled
                true,  // Hardware Acceleration Enabled
                null); // Render EGL Context

        pcFactory = new PeerConnectionFactory(); //create the object.
        //init the client - should research about PUB_KEY, SUB_KEY of pubnub service.
        this.pnRTCClient = new PnRTCClient("demo", "demo", "User");
        //this.pnRTCClient = new PnRTCClient(Constants.PUB_KEY, Constants.SUB_KEY, this.username);

        //returns the number of cameras on device.
        int camNumber = VideoCapturerAndroid.getDeviceCount();
        String frontFacingCam = VideoCapturerAndroid.getNameOfFrontFacingDevice(); //front camera
        String backFacingCam = VideoCapturerAndroid.getNameOfBackFacingDevice(); //back camera - selfie.

        //creates an object that captures video.
        VideoCapturer capturer = VideoCapturerAndroid.create(frontFacingCam); //can be change to back camera.

        // attaches the local Video Source to the camera capturer and the client's video constraints (video settings that can be changed.)
        localVideoSource = pcFactory.createVideoSource(capturer, this.pnRTCClient.videoConstraints());
        //creates a track(RESEARCH), takes ID(a string that can be set), and localVideoSource.
        VideoTrack localVideoTrack = pcFactory.createVideoTrack(VIDEO_TRACK_ID, localVideoSource);

        //same process but for audio.
        AudioSource audioSource = pcFactory.createAudioSource(this.pnRTCClient.audioConstraints());
        AudioTrack localAudioTrack = pcFactory.createAudioTrack(AUDIO_TRACK_ID, audioSource);

        //making and object that can present the capture.
        //GLSurfaceView - XML component.
        this.videoView = (GLSurfaceView) findViewById(R.id.videoView);
        VideoRendererGui.setView(videoView, null);

       //renderer - can present. (RESEARCH)
        remoteRender = VideoRendererGui.create(0, 0, 100, 100, VideoRendererGui.ScalingType.SCALE_ASPECT_FILL, false);
        localRender = VideoRendererGui.create(0, 0, 100, 100, VideoRendererGui.ScalingType.SCALE_ASPECT_FILL, true);

        //mediaStream - streams data of audio and video to dest.
        MediaStream mediaStream = pcFactory.createLocalMediaStream(LOCAL_MEDIA_STREAM_ID);

        // addition of the video and audio tracks to the media stream.
        mediaStream.addTrack(localVideoTrack);
        mediaStream.addTrack(localAudioTrack);


        //attaches our functions to client.
        this.pnRTCClient.attachRTCListener(new myRTCListener());
        //attaches our mediaStream to client.
        this.pnRTCClient.attachLocalMediaStream(mediaStream);

        //calls to a user (named "Kevin")
        this.pnRTCClient.listenOn("Kevin");
        //number of connections.
        this.pnRTCClient.setMaxConnections(1);

    }

    private class myRTCListener extends PnRTCListener {
        @Override
        public void onLocalStream(final MediaStream localStream) {
            VideoChatActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    localStream.videoTracks.get(0).addRenderer(new VideoRenderer(localRender));
                }
            });
        }

        @Override
        public void onAddRemoteStream(final MediaStream remoteStream, final PnPeer peer) {
            // Handle remote stream added
        }

        @Override
        public void onMessage(PnPeer peer, Object message) {
            /// Handle Message
        }

        @Override
        public void onPeerConnectionClosed(PnPeer peer) {
            // Quit back to MainActivity
            Intent intent = new Intent(VideoChatActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void message(View v)
    {
        Toast.makeText(VideoChatActivity.this, "Hello!", Toast.LENGTH_SHORT).show();
    }

}
