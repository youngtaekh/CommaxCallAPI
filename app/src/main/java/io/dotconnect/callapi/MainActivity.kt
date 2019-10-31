package io.dotconnect.callapi

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.iid.FirebaseInstanceId
import io.dotconnect.android.ConnectManager
import io.dotconnect.android.Register.DOMAIN
import io.dotconnect.android.observer.CallInfo
import io.dotconnect.android.observer.ConnectAction
import io.dotconnect.android.observer.ConnectObserver
import io.dotconnect.android.observer.MessageInfo
import io.dotconnect.android.enum_class.CallType
import io.dotconnect.android.enum_class.MessageType
import io.dotconnect.android.util.AuthenticationUtil
import io.dotconnect.android.util.ConnectServer
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class MainActivity : AppCompatActivity(), ConnectObserver.RegistrationObserver,
    ConnectObserver.MessageObserver, ConnectObserver.CallObserver {
    val ACTION_INCOMING_CALL = "incomingCall"
    val ACTION_CANCEL_CALL = "cancelCall"
    val ACTION_FCM_TOKEN = "actionFCMToken"
    val COUNTERPART_ACCOUNT = "counterpartAccount"

    val appId = "testAppId"
    private var mContext:Context = this

    //Note8
    private val email = "note8"
    private val password = "aaaaaa"
    private val deviceId = "11E9D7B9-F4D5-4ACF-B6B9-C690775FA272"
    //Note4
//    private val email = "note4"
//    private val password = "aaaaaa"
//    private val deviceId = "3CFA370E-F7EB-49CF-A18C-E2FF86A4A87A"

    private val teamId = "vltest"

    //Note8
    private val callTarget = "note4"
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

    override fun onMessageSendSuccess(message: MessageInfo?) {
        LogAndToast("onMessageSendSuccess")
    }

    override fun onMessageSendFailure(message: MessageInfo?) {
        LogAndToast("onMessageSendFailure")
    }

    override fun onMessageArrival(message: MessageInfo?) {
        LogAndToast("onMessageArrival")
        val handler = Handler(Looper.getMainLooper())
        val runnable = Runnable { tvMessage.text = message?.message ?: ""}
        handler.post(runnable)
        ConnectManager.getInstance().sendMessage(this, messageTarget, teamId, message?.messageSeq.toString(), chatType, chatId, MessageType.read)
    }

    override fun onOutgoingCall(callInfo: CallInfo?) {}

    override fun onIncomingCall(callInfo: CallInfo?) {
        LogAndToast("onIncomingCall")
        val intent = Intent(this, CallActivity::class.java)
        intent.putExtra("video", callInfo?.callType == CallType.One_Video || callInfo?.callType == CallType.One_Audio_Screencast)
        intent.putExtra("screen", callInfo?.callType == CallType.One_Audio_Screencast)
        intent.putExtra("incoming", true)
        startActivity(intent)
    }

    override fun onUpdate(callInfo: CallInfo?) {}

    override fun onEarlyMedia(callInfo: CallInfo?) {}

    override fun onOutgoingCallConnected(callInfo: CallInfo?) {}

    override fun onIncomingCallConnected(callInfo: CallInfo?) {}

    override fun onFailure(callInfo: CallInfo?) {}

    override fun onTerminated(callInfo: CallInfo?) {}

    override fun onBusyOnIncomingCall(callInfo: CallInfo?) {}

    override fun onCancelCallBefore180(callInfo: CallInfo?) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        FirebaseApp.initializeApp(this)

        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA
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

        tvRegister.setOnClickListener { getAccessToken() }
        tvUnregister.setOnClickListener { ConnectManager.getInstance().stopRegistration() }

        tvSend.setOnClickListener {
            val message = etMessage.text.toString()
            ConnectManager.getInstance().sendMessage(this, messageTarget, teamId, message, chatType, chatId, MessageType.one)
        }

        tvCall.setOnClickListener {
            val intent = Intent(this, CallActivity::class.java)
            intent.putExtra("video", false)
            val target = "sip:" + AuthenticationUtil.makeSHA256(etTarget.text.toString() + appId) + "@" + appId + DOMAIN
            intent.putExtra("target", target)
            intent.putExtra("teamId", etTeamId.text.toString())
            startActivity(intent)
        }

        tvVideo.setOnClickListener {
            val intent = Intent(this, CallActivity::class.java)
            intent.putExtra("video", true)
            val target = "sip:" + AuthenticationUtil.makeSHA256(etTarget.text.toString() + appId) + "@" + appId + DOMAIN
            intent.putExtra("target", target)
            intent.putExtra("teamId", etTeamId.text.toString())
            startActivity(intent)
        }

        tvScreen.setOnClickListener {
            val intent = Intent(this, CallActivity::class.java)
            intent.putExtra("video", true)
            intent.putExtra("screen", true)
            val target = "sip:" + AuthenticationUtil.makeSHA256(etTarget.text.toString() + appId) + "@" + appId + DOMAIN
            intent.putExtra("target", target)
            intent.putExtra("teamId", etTeamId.text.toString())
            startActivity(intent)
        }
        if (ACTION_INCOMING_CALL == intent.action) {
            Log.d(TAG, "onIncomingCall")

            getAccessToken()
        }

        val intentFilter = IntentFilter()
        intentFilter.addAction(ACTION_CANCEL_CALL)

        registerReceiver(broadcastReceiver, intentFilter)
    }

    override fun onNewIntent(intent: Intent) {
        Log.d(TAG, "onNewIntent")
        if (ACTION_INCOMING_CALL == intent.action) {
            Log.d(TAG, "onIncomingCall")

            getAccessToken()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ConnectAction.getInstance().delete(this as ConnectObserver.RegistrationObserver)
        ConnectAction.getInstance().delete(this as ConnectObserver.MessageObserver)
        ConnectAction.getInstance().delete(this as ConnectObserver.CallObserver)

        unregisterReceiver(broadcastReceiver)
    }

    private fun getAccessToken() {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("userId", etEmail.text.toString())
            GetAccessToken().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, jsonObject.toString())

        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    @SuppressLint("StaticFieldLeak")
    private inner class GetAccessToken : AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg params: String): String {
            Log.d(TAG, params[0])
            return ConnectServer.POST("/apps/$appId/users", params[0], null)
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            Log.d(TAG, result)
            getFCMToken(result)
        }
    }

    private fun getFCMToken(accessToken: String) {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                Log.d(TAG, task.result?.token)
                ConnectManager.getInstance()
                    .startRegistration(mContext, etEmail.text.toString(), appId, accessToken, task.result?.token)

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
