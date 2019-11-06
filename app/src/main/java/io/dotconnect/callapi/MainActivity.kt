package io.dotconnect.callapi

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import io.dotconnect.api.ConnectManager
import io.dotconnect.api.enum_class.CallType
import io.dotconnect.api.observer.ApiCallInfo
import io.dotconnect.api.observer.ConnectAction
import io.dotconnect.api.observer.ConnectObserver
import io.dotconnect.api.observer.ApiMessageInfo
import io.dotconnect.api.util.AuthenticationUtil
import io.dotconnect.api.util.ConnectServer
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity(), ConnectObserver.RegistrationObserver,
    ConnectObserver.MessageObserver, ConnectObserver.CallObserver {

    val ACTION_INCOMING_CALL = "incomingCall"
    val ACTION_CANCEL_CALL = "cancelCall"
    val ACTION_FCM_TOKEN = "actionFCMToken"
    val COUNTERPART_ACCOUNT = "counterpartAccount"

    val appId = "testappid"
    private val DOMAIN = "commax.dot-connect.io"
    private val OUTBOUND_PROXY = "commax.dot-connect.io"
    private var mContext:Context = this

    //Note8
    private val email = "youngtaek"
    private val password = "aaaaaa"
    private val deviceId = "11E9D7B9-F4D5-4ACF-B6B9-C690775FA272"
    //Note4
//    private val email = "note4"
//    private val password = "aaaaaa"
//    private val deviceId = "3CFA370E-F7EB-49CF-A18C-E2FF86A4A87A"

    private val teamId = "vltest"

    //Note8
    private val callTarget = "youngtaek"
    //Note4
//    private val callTarget = "note8"

    private val messageTarget = "sip:27094d15477333d60fe64ed43668d018@voiceloco.com"
    private val chatId = "27094d15477333d60fe64ed43668d018"
    private val chatType = "chat"
    private var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            if (ACTION_CANCEL_CALL == intent.action) {
                Log.d(TAG, "callCancel")
            }
        }
    }

    private val TAG = "MainActivity"

    fun LogAndToast(message: String) {
        runOnUiThread { Toast.makeText(this, message, Toast.LENGTH_SHORT).show() }
        Log.d(TAG, message)
    }
    override fun onDeviceRegistrationSuccess() {
        LogAndToast("onDeviceRegistrationSuccess")
    }

    override fun onDeviceUnRegistrationSuccess() {
        LogAndToast("onDeviceUnRegistrationSuccess")
    }

    override fun onRegistrationSuccess() {
        LogAndToast("onRegistrationSuccess")
    }

    override fun onRegistrationFailure() {
        LogAndToast("onRegistrationFailure")
    }

    override fun onUnRegistrationSuccess() {
        LogAndToast("onUnRegistrationSuccess")
    }

    override fun onSocketClosure() {
        LogAndToast("onSocketClosure")
    }

    override fun onMessageSendSuccess(ApiMessage: ApiMessageInfo?) {
        LogAndToast("onMessageSendSuccess")
    }

    override fun onMessageSendFailure(ApiMessage: ApiMessageInfo?) {
        LogAndToast("onMessageSendFailure")
    }

    override fun onMessageArrival(ApiMessage: ApiMessageInfo?) {
        LogAndToast("onMessageArrival")
//        val handler = Handler(Looper.getMainLooper())
//        val runnable = Runnable { tvMessage.text = ApiMessage?.ApiMessage ?: ""}
//        handler.post(runnable)
//        ConnectManager.getInstance().sendMessage(this, messageTarget, teamId, ApiMessage?.messageSeq.toString(), chatType, chatId, MessageType.read)
    }

//    override fun onOutgoingCall(callInfo: ApiCallInfo?) {}

    override fun onIncomingCall(ApiCallInfo: ApiCallInfo?) {
        LogAndToast("onIncomingCall")
        val intent = Intent(this, CallActivity::class.java)
        intent.putExtra("video", ApiCallInfo?.callType == CallType.One_Video || ApiCallInfo?.callType == CallType.One_Audio_Screencast)
        intent.putExtra("screen", ApiCallInfo?.callType == CallType.One_Audio_Screencast)
        intent.putExtra("incoming", true)
        startActivity(intent)
    }

//    override fun onUpdate(callInfo: ApiCallInfo?) {}
//
//    override fun onEarlyMedia(callInfo: ApiCallInfo?) {}
//
//    override fun onOutgoingCallConnected(callInfo: ApiCallInfo?) {}

    override fun onIncomingCallConnected(ApiCallInfo: ApiCallInfo?) {}

    override fun onFailure(ApiCallInfo: ApiCallInfo?) {}

    override fun onTerminated(ApiCallInfo: ApiCallInfo?) {
//        runOnUiThread { ConnectManager.getInstance().stopRegistration() }
    }

//    override fun onBusyOnIncomingCall(callInfo: ApiCallInfo?) {}
//
//    override fun onCancelCallBefore180(callInfo: ApiCallInfo?) {}

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

        ConnectAction.getInstance().add(this as ConnectObserver.RegistrationObserver)
        ConnectAction.getInstance().add(this as ConnectObserver.MessageObserver)
        ConnectAction.getInstance().add(this as ConnectObserver.CallObserver)

        etEmail.setText(email)
        etPassword.setText(password)
        etDeviceId.setText(deviceId)
        etTarget.setText(callTarget)
        etTeamId.setText(teamId)

        tvRegister.setOnClickListener { getAccessToken(2) }
        tvUnregister.setOnClickListener { ConnectManager.getInstance().stopRegistration() }
        tvDeviceRegister.setOnClickListener { getAccessToken(0) }
        tvDeviceUnregister.setOnClickListener { getAccessToken(1) }

        tvSend.setOnClickListener {
//            val message = etMessage.text.toString()
//            ConnectManager.getInstance().sendMessage(this, messageTarget, teamId, message, chatType, chatId, MessageType.one)
        }

        tvCall.setOnClickListener {
            val intent = Intent(this, CallActivity::class.java)
            intent.putExtra("video", false)
//            val target = "sip:" + AuthenticationUtil.makeSHA256(etTarget.text.toString() + appId) + "@" + appId + DOMAIN
            intent.putExtra("target", etTarget.text.toString())
            intent.putExtra("teamId", etTeamId.text.toString())
            startActivity(intent)
        }

        tvVideo.setOnClickListener {
            val intent = Intent(this, CallActivity::class.java)
            intent.putExtra("video", true)
            val target = "sip:" + AuthenticationUtil.makeSHA256(etTarget.text.toString() + appId) + "@" + appId + "." + DOMAIN
            intent.putExtra("target", target)
            intent.putExtra("teamId", etTeamId.text.toString())
            startActivity(intent)
        }

        tvScreen.setOnClickListener {
            val intent = Intent(this, CallActivity::class.java)
            intent.putExtra("video", true)
            intent.putExtra("screen", true)
//            val target = "sip:" + AuthenticationUtil.makeSHA256(etTarget.text.toString() + appId) + "@" + appId + DOMAIN
            intent.putExtra("target", etTarget.text.toString())
            intent.putExtra("teamId", etTeamId.text.toString())
            startActivity(intent)
        }
        if (ACTION_INCOMING_CALL == intent.action) {
            Log.d(TAG, "onIncomingCall")

            getAccessToken(2)
        }

        val intentFilter = IntentFilter()
        intentFilter.addAction(ACTION_CANCEL_CALL)

        registerReceiver(broadcastReceiver, intentFilter)
    }

    override fun onNewIntent(intent: Intent) {
        Log.d(TAG, "onNewIntent")
        if (ACTION_INCOMING_CALL == intent.action) {
            Log.d(TAG, "onIncomingCall")

            getAccessToken(2)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ConnectAction.getInstance().delete(this as ConnectObserver.RegistrationObserver)
        ConnectAction.getInstance().delete(this as ConnectObserver.MessageObserver)
        ConnectAction.getInstance().delete(this as ConnectObserver.CallObserver)

        unregisterReceiver(broadcastReceiver)
    }

    private fun getAccessToken(action: Int) {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("userId", etEmail.text.toString())
            GetAccessToken(action).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, jsonObject.toString())

        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    @SuppressLint("StaticFieldLeak")
    private inner class GetAccessToken(var action: Int) : AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg params: String): String {
            Log.d(TAG, params[0])
            return ConnectServer.POST("/apps/$appId/users", params[0], null, "testApiKey")
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            Log.d(TAG, result)
            getFCMToken(result, action)
        }
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
                    0 -> ConnectManager.getInstance().deviceRegistration(mContext, accessToken, task.result?.token)
                    1 -> ConnectManager.getInstance().deviceUnRegistration(mContext, accessToken)
                    else -> ConnectManager.getInstance()
                        .startRegistration(
                            mContext,
                            etEmail.text.toString(),
                            appId,
                            accessToken,
                            task.result?.token
                            , DOMAIN
                            , OUTBOUND_PROXY
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
