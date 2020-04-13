package io.dotconnect.callapi

import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import io.dotconnect.api.ConnectManager
import io.dotconnect.api.observer.ApiCallInfo
import io.dotconnect.api.observer.ApiMessageInfo
import io.dotconnect.api.observer.ConnectAction
import io.dotconnect.api.observer.ConnectObserver
import kotlinx.android.synthetic.main.activity_call.*
import org.webrtc.RendererCommon

class CallActivity : AppCompatActivity(), ConnectObserver.CallObserver,
    ConnectObserver.MessageObserver, ConnectObserver.PeerConnectionObserver {

    private var incoming = false
    private var target: String? = null
    private var appId: String? = null
    private var deviceId: String? = null
    private var speaker = false
    private var calling = false
    private var scaleType = RendererCommon.ScalingType.SCALE_ASPECT_FIT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call)

        ConnectAction.getInstance().add(this as ConnectObserver.CallObserver)
        ConnectAction.getInstance().add(this as ConnectObserver.MessageObserver)
        ConnectAction.getInstance().add(this as ConnectObserver.PeerConnectionObserver)

        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val intent = intent
        incoming = intent.getBooleanExtra("incoming", false)
        target = intent.getStringExtra("target")
        appId = intent.getStringExtra("appId")

        if (!incoming) {
            tvStatus.text = "CCTV"
            deviceId = intent.getStringExtra("deviceId")
            ConnectManager.getInstance().requestCctv(this, target, appId, deviceId, cvFullView, "")
        }

        tvEnd.setOnClickListener {
            ConnectManager.getInstance().end()
        }

        tvAccept.setOnClickListener {
            ConnectManager.getInstance().acceptCall(this, cvFullView)
        }

        tvSet.setOnClickListener {
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

        audioManager.mode = AudioManager.MODE_IN_COMMUNICATION
        audioManager.isSpeakerphoneOn = speaker
    }

    override fun onDestroy() {
        super.onDestroy()
        ConnectAction.getInstance().delete(this as ConnectObserver.CallObserver)
        ConnectAction.getInstance().delete(this as ConnectObserver.MessageObserver)
        ConnectAction.getInstance().delete(this as ConnectObserver.PeerConnectionObserver)
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

    override fun onMessageSendSuccess(apiMessageInfo: ApiMessageInfo?) {
        Log.d("CallActivity", apiMessageInfo.toString())
    }

    override fun onMessageSendFailure(apiMessageInfo: ApiMessageInfo?) {}

    override fun onMessageArrival(apiMessageInfo: ApiMessageInfo?) {
        Log.d("CallActivity", apiMessageInfo.toString())
    }

    override fun onPeerConnectionFailed() {
        Log.d("CallActivity", "onFailed")
    }

    override fun onPeerConnectionConnected() {
        Log.d("CallActivity", "onConnected")
    }

    override fun onPeerConnectionClosed() {
        Log.d("CallActivity", "onClosed")
    }

    override fun onPeerConnectionDisconnected() {
        Log.d("CallActivity", "onDisconnected")
    }

    override fun onPeerConnectionError(description: String?) {
        Log.d("CallActivity", "onError - $description")
    }
}
