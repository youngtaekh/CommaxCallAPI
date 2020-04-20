package io.dotconnect.callapi

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import io.dotconnect.api.ConnectManager
import io.dotconnect.api.enum_class.MessageDetail
import io.dotconnect.api.observer.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), ConnectObserver.RegistrationObserver,
    ConnectObserver.MessageObserver, ConnectObserver.CallObserver {

    val ACTION_INCOMING_CALL = "incomingCall"
    val ACTION_CANCEL_CALL = "cancelCall"
    val ACTION_FCM_TOKEN = "actionFCMToken"
    val COUNTERPART_ACCOUNT = "counterpartAccount"

    val appId = "testappid"
    private val DOMAIN = "commax.dotconnect-api.io"
    private val OUTBOUND_PROXY = "commax.dotconnect-api.io"
    private var outboundProxyAddress:String? = null
    private var mContext:Context = this

    //Note8
    private val accessToken = "Wkc5MFkyOXViVzgzMjAxOS0xMS0wOCAwNDo1MzowMg=="
    private val userId = "ZG90Y29ubW83"
    private val deviceId = "777777"
    //Note4
//    private val accessToken = "Wkc5MFkyOXViVzh4MjAxOS0xMS0wOCAwNDozODozOA=="
//    private val userId = "ZG90Y29ubW8x"
//    private val deviceId = "333333"

    //Note8
    private val callTarget = "wallpadtest"
    //Note4
//    private val callTarget = "100000"

    private var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            if (ACTION_CANCEL_CALL == intent.action) {
                Log.d(TAG, "callCancel")
            }
        }
    }

    private val TAG = "MainActivity"

    private fun logAndToast(message: String) {
        runOnUiThread { Toast.makeText(this, message, Toast.LENGTH_SHORT).show() }
        Log.d(TAG, message)
    }

    override fun onDeviceRegistrationSuccess() {
        logAndToast("onDeviceRegistrationSuccess")
    }

    override fun onDeviceUnRegistrationSuccess() {
        logAndToast("onDeviceUnRegistrationSuccess")
    }

    override fun onRegistrationSuccess() {
        logAndToast("onRegistrationSuccess")
    }

    override fun onRegistrationFailure() {
        logAndToast("onRegistrationFailure")
    }

    override fun onUnRegistrationSuccess() {
        logAndToast("onUnRegistrationSuccess")
    }

    override fun onSocketClosure() {
        logAndToast("onSocketClosure")
    }

    override fun onMessageSendSuccess(apiMessageInfo: ApiMessageInfo?) {
        logAndToast("onMessageSendSuccess")
        Log.d(TAG, apiMessageInfo.toString())
    }

    override fun onMessageSendFailure(apiMessageInfo: ApiMessageInfo?) {
        logAndToast("onMessageSendFailure")
    }

    override fun onMessageArrival(apiMessageInfo: ApiMessageInfo?) {
        logAndToast("onMessageArrival")
        Log.d(TAG, apiMessageInfo.toString())
    }

    override fun onCctvList(cctvList: MutableList<CctvInfo>?) {
        logAndToast("onCctvList")
        if (cctvList!!.size != 0) {
            runOnUiThread { etCctvSource.setText(cctvList[0].key) }
        }
        var i=0
        while (i < cctvList.size) {
            Log.d(TAG, cctvList[i].key)
            i++
        }

    }

    override fun onIncomingCall(ApiCallInfo: ApiCallInfo?) {
        logAndToast("onIncomingCall")
        val intent = Intent(this, CallActivity::class.java)
        intent.putExtra("incoming", true)
        startActivity(intent)
    }

    override fun onIncomingCallConnected(ApiCallInfo: ApiCallInfo?) {}

    override fun onFailure(ApiCallInfo: ApiCallInfo?) {}

    override fun onTerminated(ApiCallInfo: ApiCallInfo?) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        FirebaseApp.initializeApp(this)

        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.RECORD_AUDIO
            ),
            0
        )

        etEmail.setText(userId)
        etDeviceId.setText(deviceId)
        etTarget.setText(callTarget)

        tvRegister.setOnClickListener { getFCMToken(accessToken, 2) }
        tvUnregister.setOnClickListener { ConnectManager.getInstance().stopRegistration() }
        tvDeviceRegister.setOnClickListener { getFCMToken(accessToken, 0) }
        tvDeviceUnregister.setOnClickListener { getFCMToken(accessToken, 1) }
        tvMessageGroup.setOnClickListener {
            ConnectManager.getInstance().sendMessageToGroup("100000", appId, "asdf", etDeviceId.text.toString())
        }
        tvMessageUser.setOnClickListener {
            ConnectManager.getInstance().sendMessageToUserId("ZG90Y29ubW82", appId, "asdf", etDeviceId.text.toString())
        }
        tvMessageDevice.setOnClickListener {
            ConnectManager.getInstance().sendMessageToDeviceId("666666", appId, "666666", etDeviceId.text.toString())
        }
        tvSendCctv.setOnClickListener {
            val intent = Intent(this, CallActivity::class.java)
            intent.putExtra("video", false)
            intent.putExtra("target", etTarget.text.toString())
            intent.putExtra("appId", appId)
            intent.putExtra("deviceId", etDeviceId.text.toString())
            intent.putExtra("source", etCctvSource.text.toString())
            startActivity(intent)
        }
        tvSendControl.setOnClickListener {
            ConnectManager.getInstance().requestControl(etTarget.text.toString(), appId, etDeviceId.text.toString(), "Open the door")
        }

        tvCctvList.setOnClickListener {
            ConnectManager.getInstance().requestCctvList(etTarget.text.toString(), appId, etDeviceId.text.toString())
        }

        if (ACTION_INCOMING_CALL == intent.action) {
            Log.d(TAG, "onIncomingCall")

            getFCMToken(accessToken, 2)
        }

        val intentFilter = IntentFilter()
        intentFilter.addAction(ACTION_CANCEL_CALL)

        registerReceiver(broadcastReceiver, intentFilter)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.d(TAG, "onNewIntent")
        if (ACTION_INCOMING_CALL == intent.action) {
            Log.d(TAG, "onIncomingCall")

            getFCMToken(accessToken, 2)
        }
    }

    override fun onResume() {
        super.onResume()

        ConnectAction.getInstance().add(this as ConnectObserver.RegistrationObserver)
        ConnectAction.getInstance().add(this as ConnectObserver.MessageObserver)
        ConnectAction.getInstance().add(this as ConnectObserver.CallObserver)
    }

    override fun onPause() {
        super.onPause()
        ConnectAction.getInstance().delete(this as ConnectObserver.RegistrationObserver)
        ConnectAction.getInstance().delete(this as ConnectObserver.MessageObserver)
        ConnectAction.getInstance().delete(this as ConnectObserver.CallObserver)
    }

    override fun onDestroy() {
        super.onDestroy()

        unregisterReceiver(broadcastReceiver)
    }

    private fun getFCMToken(accessToken: String, action: Int) {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                Log.d(TAG, task.result?.token)
                when (action) {
                    0 -> ConnectManager.getInstance().deviceRegistration(etDeviceId.text.toString(), userId, appId, accessToken, task.result?.token)
                    1 -> ConnectManager.getInstance().deviceUnRegistration(etDeviceId.text.toString(), accessToken)
                    else -> ConnectManager.getInstance()
                        .startRegistration(
                            mContext,
                            etDeviceId.text.toString(),
                            etEmail.text.toString(),
                            appId,
                            accessToken,
                            task.result?.token,
                            DOMAIN,
                            OUTBOUND_PROXY
                        )
                }
            })
    }

    companion object {
        @JvmField
        var ACTION_CANCEL_CALL: String = "cancelCall"
        @JvmField
        var COUNTERPART_ACCOUNT: String = "counterpartAccount"
        @JvmField
        var ACTION_INCOMING_CALL: String = "incomingCall"
    }
}
