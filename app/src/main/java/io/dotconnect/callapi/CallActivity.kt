package io.dotconnect.callapi

import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import io.dotconnect.api.ConnectManager
import io.dotconnect.api.observer.ApiCallInfo
import io.dotconnect.api.observer.ConnectAction
import io.dotconnect.api.observer.ConnectObserver
import kotlinx.android.synthetic.main.activity_call.*
import org.webrtc.RendererCommon

class CallActivity : AppCompatActivity(), ConnectObserver.CallObserver {

    private var incoming = false
    private var target: String? = null
    private var deviceId: String? = null
    private var speaker = false
    private var calling = false
    private var scaleType = RendererCommon.ScalingType.SCALE_ASPECT_FIT

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

        if (!incoming) {
            tvStatus.text = "CCTV"
            deviceId = intent.getStringExtra("deviceId")
            ConnectManager.getInstance().requestCctv(this, target, deviceId, cvFullView)
//            ConnectManager.getInstance().videoCall(this, target, "", cvFullView, cvSmallView)
//            when {
//                screen -> startScreenCapture()
//                video -> ConnectManager.getInstance().videoCall(this, target, teamId)
//                else -> ConnectManager.getInstance().call(this, target, teamId)
//            }
        }

        tvEnd.setOnClickListener {
            Log.d("Debug", 0.toString())
            ConnectManager.getInstance().end()
            Log.d("Debug", 4.toString())
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
            ConnectManager.getInstance().acceptCall(this, cvFullView)
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

    override fun onIncomingCall(ApiCallInfo: ApiCallInfo?) {}

    override fun onIncomingCallConnected(ApiCallInfo: ApiCallInfo?) {
        calling = true
        runOnUiThread { tvStatus.text = "Calling" }
    }

    override fun onFailure(ApiCallInfo: ApiCallInfo?) {}

    override fun onTerminated(ApiCallInfo: ApiCallInfo?) {
        finish()
    }
}
