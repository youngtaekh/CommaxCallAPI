package io.dotconnect.callapi

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.dotconnect.android.ConnectManager
import io.dotconnect.android.observer.CallInfo
import io.dotconnect.android.observer.ConnectAction
import io.dotconnect.android.observer.ConnectObserver
import kotlinx.android.synthetic.main.activity_call.*
import org.webrtc.RendererCommon

class CallActivity : AppCompatActivity(), ConnectObserver.CallObserver {

    private var incoming = false
    private var target: String? = null
//    private var teamId: String? = null
    private var swap = false
    private var speaker = false
    private var calling = false
    private var scaleType = RendererCommon.ScalingType.SCALE_ASPECT_FIT

    private var mediaProjectionPermissionResultData: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call)

        ConnectAction.getInstance().add(this)

        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val intent = intent
        incoming = intent.getBooleanExtra("incoming", false)
//        val video:Boolean = intent.getBooleanExtra("video", false)
//        val screen:Boolean = intent.getBooleanExtra("screen", false)
        target = intent.getStringExtra("target")
//        teamId = intent.getStringExtra("teamId")

        if (!incoming) {
            tvStatus.text = "Sending"
//            initView()
//            when {
//                screen -> startScreenCapture()
//                video -> ConnectManager.getInstance().videoCall(this, target, teamId)
//                else -> ConnectManager.getInstance().call(this, target, teamId)
//            }
        }

        tvEnd.setOnClickListener {
            if(!calling)
                ConnectManager.getInstance().stopRegistration()
            ConnectManager.getInstance().end()
            finish()
        }

//        tvCancel.setOnClickListener {
//            ConnectManager.getInstance().cancel()
//            finish()
//        }

//        tvHangup.setOnClickListener {
//            ConnectManager.getInstance().hangup()
//            finish()
//        }
//
//        tvReject.setOnClickListener {
//            ConnectManager.getInstance().reject()
//            finish()
//        }

        tvAccept.setOnClickListener {
//            initView()
            ConnectManager.getInstance().acceptVideoCall(this, cvFullView)
//            when {
//                screen -> startScreenCapture()
//                video -> ConnectManager.getInstance().acceptVideoCall(this)
//                else -> ConnectManager.getInstance().accept(this)
//            }
        }

        tvSet.setOnClickListener {
//            ConnectManager.getInstance().swapCamera(swap)
//            swap = !swap
            scaleType = when (scaleType) {
                RendererCommon.ScalingType.SCALE_ASPECT_FIT -> RendererCommon.ScalingType.SCALE_ASPECT_FILL
                RendererCommon.ScalingType.SCALE_ASPECT_FILL -> RendererCommon.ScalingType.SCALE_ASPECT_BALANCED
                else -> RendererCommon.ScalingType.SCALE_ASPECT_FIT
            }
            ConnectManager.getInstance().setScaleType(scaleType)
        }

        tvSpeaker.setOnClickListener {
            speaker = !speaker
            audioManager.isSpeakerphoneOn = speaker
        }

//        tvAcceptAudio.setOnClickListener {
//            initView()
//            ConnectManager.getInstance().accept(this)
//        }
//
//        tvAcceptVideo.setOnClickListener {
//            initView()
//            ConnectManager.getInstance().acceptVideoCall(this)
//        }

        audioManager.mode = AudioManager.MODE_IN_COMMUNICATION
        audioManager.isSpeakerphoneOn = speaker
    }

    override fun onDestroy() {
        super.onDestroy()
        ConnectAction.getInstance().delete(this)
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.mode = AudioManager.MODE_NORMAL
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0) {
            mediaProjectionPermissionResultData = data
//            val connectManager = ConnectManager.getInstance()
//            if (incoming)
//                connectManager.acceptScreenCall(this, mediaProjectionPermissionResultData)
//            else
//                connectManager.screenCall(this, mediaProjectionPermissionResultData, target, teamId)
        }
    }

    override fun onIncomingCall(callInfo: CallInfo?) {}

//    override fun onOutgoingCall(callInfo: CallInfo?) {}
//
//    override fun onUpdate(callInfo: CallInfo?) {}
//
//    override fun onEarlyMedia(callInfo: CallInfo?) {}
//
//    override fun onOutgoingCallConnected(callInfo: CallInfo?) {
//        runOnUiThread { tvStatus.text = "Calling" }
//    }

    override fun onIncomingCallConnected(callInfo: CallInfo?) {
        calling = true
        runOnUiThread { tvStatus.text = "Calling" }
    }

    override fun onFailure(callInfo: CallInfo?) {}

    override fun onTerminated(callInfo: CallInfo?) {
        finish()
    }

//    override fun onBusyOnIncomingCall(callInfo: CallInfo?) {}
//
//    override fun onCancelCallBefore180(callInfo: CallInfo?) {}

    @TargetApi(21)
    private fun startScreenCapture() {
        val mediaProjectionManager = application.getSystemService(
            Context.MEDIA_PROJECTION_SERVICE
        ) as MediaProjectionManager
        startActivityForResult(
            mediaProjectionManager.createScreenCaptureIntent(), 0
        )
    }

//    private fun initView() {
//        ConnectManager.getInstance().initView(cvFullView)
//    }
}
