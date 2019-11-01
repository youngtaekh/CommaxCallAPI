package io.dotconnect.android;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import io.dotconnect.android.enum_class.CallState;
import io.dotconnect.android.observer.CallInfo;
import io.dotconnect.android.observer.ConnectAction;
import io.dotconnect.android.observer.MessageInfo;
import io.dotconnect.android.enum_class.MessageType;
import io.dotconnect.android.util.AuthenticationUtil;
import io.dotconnect.android.view.ConnectView;
import io.dotconnect.signaling.callJni.CallCore;
import io.dotconnect.signaling.observer.SignalingAction;
import io.dotconnect.signaling.observer.SignalingObserver;
import org.webrtc.RendererCommon;

import static io.dotconnect.android.util.Configuration.APP_NAME;

public class ConnectManager {
    private static final String TAG = "ConnectManager";
    private static ConnectManager instance;

    private CallManager callManager;
    private CallInfo callInfo;
//    private Call call;

    private Handler coreStopHandler;
    private Runnable coreStopRunnable = () -> {
        CallCore.getInstance().stop();
        release();
        ConnectAction.getInstance().onUnRegistrationSuccessObserver();
    };

    private ConnectManager() {
        SignalingAction.getInstance().add(registrationObserver);
        SignalingAction.getInstance().add(messageObserver);
        SignalingAction.getInstance().add(callObserver);

        callManager = CallManager.getInstance();
    }

    public static ConnectManager getInstance() {
        if (instance == null) {
            instance = new ConnectManager();
        }
        return instance;
    }

    private void release() {
        Register.getInstance().release();
        instance = null;
        SignalingAction.getInstance().delete(registrationObserver);
        SignalingAction.getInstance().delete(messageObserver);
        SignalingAction.getInstance().delete(callObserver);
    }

    //Register
    /**
     *
     * @param context
     * @param userId
     * @param appId
     * @param accessToken
     * @param fcmToken Firebase Push Token
     */
    public void startRegistration(Context context, String userId, String appId, String accessToken, String fcmToken, String domain) {
        Register.getInstance().start(context, userId, appId, accessToken, fcmToken, domain);
    }

    public void stopRegistration() {
        Register register = Register.getInstance();
        if (register!=null)
            Register.getInstance().stop();
        if (coreStopHandler==null) {
            coreStopHandler = new Handler();
            coreStopHandler.postDelayed(coreStopRunnable, 1000);
        }
    }

    /**
     *
     * @return check app is registered
     */
    public boolean isRegistered() {
        return Register.getInstance().isRegistered();
    }

    //Message
    private int sendMessage(Context context, String target, String teamId, String message,
                           String chatType, String chatId, MessageType messageType) {
        return new Message().sendMessage(target, teamId, message, AuthenticationUtil.getUUID(context), chatType, chatId, messageType);
    }

    private int sendFile(Context context, String target, String teamId, String message, String chatType,
                        String chatId, MessageType messageType, String fileType, String fileUrl) {
        return new Message().sendFile(target, teamId, message, chatType, AuthenticationUtil.getUUID(context), chatId, messageType, fileType, fileUrl);
    }

    //Call
    private void call(Context context, String target, String teamId) {
        Call call = callManager.get();
        if (call!=null) {
            call.setConfig(target, teamId);
            call.call(context);
        }
    }

    private void videoCall(Context context, String target, String teamId) {
        Call call = callManager.get();
        if (call!=null) {
            call.setConfig(target, teamId);
            call.videoCall(context);
        }
    }

    private void screenCall(Context context, Intent data, String target, String teamId) {
        Call call = callManager.get();
        if (call!=null) {
            call.setConfig(target, teamId);
            call.screenCall(context, data);
        }
    }

    private void accept(Context context) {
        Call call = callManager.get();
        if (call!=null) {
            call.acceptCall(context, callInfo.getSdp());
        }
    }

    /**
     * accept call with video
     * @param context
     */
    public int acceptVideoCall(Context context, ConnectView cvFullView, ConnectView cvSmallView) {
        Call call = callManager.get();
        if (call!=null && callInfo!=null && call.getCallState()==CallState.incoming) {
            Log.d(APP_NAME, "acceptVideoCall");
            call.setCallState(CallState.incomingConnectTry);
            initView(cvFullView, cvSmallView);
            call.acceptVideoCall(context, callInfo.getSdp());
            return 0;
        }

        return -1;
    }

    public int acceptVideoCall(Context context, ConnectView cvFullView) {
        Call call = callManager.get();
        if (call!=null && callInfo!=null && call.getCallState()==CallState.incoming) {
            Log.d(APP_NAME, "acceptVideoCall");
            call.setCallState(CallState.incomingConnectTry);
            initView(cvFullView);
            call.acceptVideoCall(context, callInfo.getSdp());
            return 0;
        }

        return -1;
    }

    private void acceptScreenCall(Context context, Intent data) {
        Call call = callManager.get();
        if (call!=null) {
            call.acceptScreenCall(context, callInfo.getSdp(), data);
        }
    }

    /**
     * if call state is calling, hangup the call
     * else reject the call
     */
    public void end() {
        Call call = callManager.get();
        if (call!=null && call.getCallState()!=CallState.endTry) {
            call.setCallState(CallState.endTry);
            if (call.getCallState() == CallState.calling)
                hangup();
            else if (call.getCallState() == CallState.incoming)
                reject();
            else
                cancel();
        }
    }

    private void cancel() {
        Call call = callManager.get();
        if (call!=null) {
            call.cancel();
        }
    }

    private void hangup() {
        Call call = callManager.get();
        if (call!=null) {
            call.hangup();
        }
    }

    private void reject() {
        Call call = callManager.get();
        if (call!=null) {
            call.reject();
            callManager.remove();
        }
    }

    private void initView(ConnectView cvFullView, ConnectView cvSmallView) {
        Call call = callManager.get();
        call.setVideoView(cvFullView, cvSmallView);
        call.initView();
    }

    private void initView(ConnectView cvFullView) {
        Call call = callManager.get();
        call.setVideoView(cvFullView, null);
        call.initView();
    }

    /**
     * change video between ConnectView
     * @param swap
     */
    private void swapCamera(boolean swap) {
        Call call = callManager.get();
        call.swapCamera(swap);
    }

    public void setScaleType(RendererCommon.ScalingType scaleType) {
        Call call = callManager.get();
        call.setScaleType(scaleType);
    }

    private SignalingObserver.RegistrationObserver registrationObserver = new SignalingObserver.RegistrationObserver() {
        @Override
        public void onRegistrationSuccess() {
            Log.d(TAG, "onRegistrationSuccess");
            ConnectAction.getInstance().onRegistrationSuccessObserver();
        }

        @Override
        public void onRegistrationFailure() {
            Log.d(TAG, "onRegistrationFailure");
            ConnectAction.getInstance().onRegistrationFailureObserver();
        }

        @Override
        public void onUnRegistrationSuccess() {
            Log.d(TAG, "onUnRegistrationSuccess");
            if (coreStopHandler!=null) {
                coreStopHandler.removeCallbacks(coreStopRunnable);
                coreStopHandler = null;
            }
            CallCore.getInstance().stop();
            release();
            ConnectAction.getInstance().onUnRegistrationSuccessObserver();
        }

        @Override
        public void onSocketClosure() {
            Log.d(TAG, "onSocketClosure");
            ConnectAction.getInstance().onSocketClosureObserver();
        }
    };

    private SignalingObserver.MessageObserver messageObserver = new SignalingObserver.MessageObserver() {
        @Override
        public void onMessageSendSuccess(io.dotconnect.signaling.observer.Message message) {
            Log.d(TAG, "onMessageSendSuccess");
            ConnectAction.getInstance().onMessageSendSuccessObserver(new MessageInfo(message));
        }

        @Override
        public void onMessageSendFailure(io.dotconnect.signaling.observer.Message message) {
            Log.d(TAG, "onMessageSendFailure");
            ConnectAction.getInstance().onMessageSendFailureObserver(new MessageInfo(message));
        }

        @Override
        public void onMessageArrival(io.dotconnect.signaling.observer.Message message) {
            Log.d(TAG, "onMessageArrival");
            ConnectAction.getInstance().onMessageArrivalObserver(new MessageInfo(message));
        }
    };

    private SignalingObserver.CallObserver callObserver = new SignalingObserver.CallObserver() {
        @Override
        public void onIncomingCall(io.dotconnect.signaling.observer.Call call) {
            Log.d(TAG, "onIncomingCall");
            ConnectManager.this.callInfo = new CallInfo(call);
            ConnectAction.getInstance().onIncomingCallObserver(callInfo);
            callManager.add(new Call(callInfo.getCounterpart(), callInfo.getTeamId()));
            callManager.get().setCallState(CallState.incoming);
        }

        @Override
        public void onOutgoingCall(io.dotconnect.signaling.observer.Call call) {
            Log.d(TAG, "onOutgoingCall");
            ConnectAction.getInstance().onOutgoingCallObserver(new CallInfo(call));
            callManager.get().setCallState(CallState.sending);
        }

        @Override
        public void onUpdate(io.dotconnect.signaling.observer.Call call) {
            Log.d(TAG, "onUpdate");
            ConnectAction.getInstance().onUpdateObserver(new CallInfo(call));
        }

        @Override
        public void onEarlyMedia(io.dotconnect.signaling.observer.Call call) {
            Log.d(TAG, "onEarlyMedia");
            ConnectAction.getInstance().onEarlyMediaObserver(new CallInfo(call));
        }

        @Override
        public void onOutgoingCallConnected(io.dotconnect.signaling.observer.Call call) {
            Log.d(TAG, "onOutgoingCallConnected");
            callManager.get().setRemoteDescription(call.getSdp());
            ConnectAction.getInstance().onOutgoingCallConnectedObserver(new CallInfo(call));
            callManager.get().setCallState(CallState.calling);
        }

        @Override
        public void onIncomingCallConnected(io.dotconnect.signaling.observer.Call call) {
            Log.d(TAG, "onIncomingCallConnected");
            ConnectAction.getInstance().onIncomingCallConnectedObserver(new CallInfo(call));
            callManager.get().setCallState(CallState.calling);
        }

        @Override
        public void onFailure(io.dotconnect.signaling.observer.Call call) {
            Log.d(TAG, "onFailure");
            ConnectAction.getInstance().onFailureObserver(new CallInfo(call));
        }

        @Override
        public void onTerminated(io.dotconnect.signaling.observer.Call call) {
            Log.d(TAG, "onTerminated");
            ConnectAction.getInstance().onTerminatedObserver(new CallInfo(call));
            callManager.get().setCallState(CallState.idle);
            if (callManager.get()!=null)
                callManager.get().disconnect();
            callManager.remove();
        }

        @Override
        public void onBusyOnIncomingCall(io.dotconnect.signaling.observer.Call call) {
            Log.d(TAG, "onBusyOnIncomingCall");
            ConnectAction.getInstance().onBusyOnIncomingCallObserver(new CallInfo(call));
        }

        @Override
        public void onCancelCallBefore180(io.dotconnect.signaling.observer.Call call) {
            Log.d(TAG, "onCancelCallBefore180");
            ConnectAction.getInstance().onCancelCallBefore180Observer(new CallInfo(call));
        }
    };
}