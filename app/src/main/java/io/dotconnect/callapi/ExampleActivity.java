package io.dotconnect.callapi;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import io.dotconnect.android.ConnectManager;
import io.dotconnect.android.observer.CallInfo;
import io.dotconnect.android.observer.ConnectAction;
import io.dotconnect.android.observer.ConnectObserver;
import io.dotconnect.android.view.ConnectView;

public class ExampleActivity extends AppCompatActivity implements ConnectObserver.CallObserver {
    private static final String TAG = "ExampleActivity";

    private boolean incoming, video, screen, swap, speaker;
    private String target, teamId;
    private TextView tvStatus;
    private ConnectView cvFullView, cvSmallView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        ConnectAction.getInstance().add(this);

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        Intent intent = getIntent();
        incoming = intent.getBooleanExtra("incoming", false);
        video = intent.getBooleanExtra("video", false);
        screen = intent.getBooleanExtra("screen", false);
        target = intent.getStringExtra("target");
        teamId = intent.getStringExtra("teamId");

        tvStatus = findViewById(R.id.tvStatus);
        TextView tvEnd = findViewById(R.id.tvEnd);
        TextView tvCancel = findViewById(R.id.tvCancel);
        TextView tvHangup = findViewById(R.id.tvHangup);
        TextView tvReject = findViewById(R.id.tvReject);
        TextView tvAccept = findViewById(R.id.tvAccept);
        TextView tvSet = findViewById(R.id.tvSet);
        TextView tvSpeaker = findViewById(R.id.tvSpeaker);

        cvFullView = findViewById(R.id.cvFullView);
        cvSmallView = findViewById(R.id.cvSmallView);

        if (!incoming) {
            tvStatus.setText("Sending");
//            initView();
            if (screen) {
//                startScreenCapture();
            } else if (video) {
//                ConnectManager.getInstance().videoCall(this, target, teamId);
            } else {
//                ConnectManager.getInstance().call(this, target, teamId);
            }
        }

        tvEnd.setOnClickListener(view -> {
            ConnectManager.getInstance().end();
            finish();
        });

//        tvCancel.setOnClickListener(view ->  {
//            ConnectManager.getInstance().cancel();
//            finish();
//        });

//        tvHangup.setOnClickListener(view ->  {
//            ConnectManager.getInstance().hangup();
//            finish();
//        });

//        tvReject.setOnClickListener(view ->  {
//            ConnectManager.getInstance().reject();
//            finish();
//        });

        tvAccept.setOnClickListener(view ->  {
//            initView();
            ConnectManager.getInstance().acceptVideoCall(this, cvFullView);
//            if (screen) startScreenCapture();
//            else if (video) ConnectManager.getInstance().acceptVideoCall(this);
//            else    ConnectManager.getInstance().accept(this);
        });

        tvSet.setOnClickListener(view ->  {
//            ConnectManager.getInstance().swapCamera(swap);
//            swap = !swap;
        });

        tvSpeaker.setOnClickListener(view ->  {
            if (audioManager!=null)
                audioManager.setSpeakerphoneOn(speaker);
            speaker = !speaker;
        });

        if (audioManager!=null) {
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            audioManager.setSpeakerphoneOn(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        ConnectAction.getInstance().delete(this);
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (audioManager!=null)
            audioManager.setMode(AudioManager.MODE_NORMAL);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            ConnectManager connectManager = ConnectManager.getInstance();
//            if (incoming)
//                connectManager.acceptScreenCall(this, data);
//            else
//                connectManager.screenCall(this, data, target, teamId);
        }
    }

    @Override
    public void onIncomingCall(CallInfo callInfo) {

    }

//    @Override
//    public void onOutgoingCall(CallInfo callInfo) {
//
//    }
//
//    @Override
//    public void onUpdate(CallInfo callInfo) {
//
//    }
//
//    @Override
//    public void onEarlyMedia(CallInfo callInfo) {
//
//    }
//
//    @Override
//    public void onOutgoingCallConnected(CallInfo callInfo) {
//        runOnUiThread(() -> tvStatus.setText("Calling"));
//    }

    @Override
    public void onIncomingCallConnected(CallInfo callInfo) {
        runOnUiThread(() -> tvStatus.setText("Calling"));
    }

    @Override
    public void onFailure(CallInfo callInfo) {

    }

    @Override
    public void onTerminated(CallInfo callInfo) {
        finish();
    }

//    @Override
//    public void onBusyOnIncomingCall(CallInfo callInfo) {
//
//    }
//
//    @Override
//    public void onCancelCallBefore180(CallInfo callInfo) {
//
//    }

    @TargetApi(21)
    private void startScreenCapture() {
        MediaProjectionManager manager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        if (manager!=null)
            startActivityForResult(manager.createScreenCaptureIntent(), 0);
    }

//    private void initView() {
//        ConnectManager.getInstance().initView(cvFullView, cvSmallView);
//    }
}
