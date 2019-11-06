package io.dotconnect.android;

import android.content.Context;
import android.content.Intent;
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

public class ConnectManager {
    private static final String TAG = "ConnectManager";
    private static ConnectManager instance;

    private CallManager callManager;
    private CallInfo callInfo;
//    private Call call;

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
    public void startRegistration(Context context, String id, String appId, String accessToken, String fcmToken) {
        Register.getInstance().start(context, id, appId, accessToken, fcmToken);
    }

    public void stopRegistration() {
        Register register = Register.getInstance();
        if (register!=null)
            Register.getInstance().stop();
    }

    //Message
    public int sendMessage(Context context, String target, String teamId, String message,
                           String chatType, String chatId, MessageType messageType) {
        return new Message().sendMessage(target, teamId, message, AuthenticationUtil.getUUID(context), chatType, chatId, messageType);
    }

    public int sendFile(Context context, String target, String teamId, String message, String chatType,
                        String chatId, MessageType messageType, String fileType, String fileUrl) {
        return new Message().sendFile(target, teamId, message, chatType, AuthenticationUtil.getUUID(context), chatId, messageType, fileType, fileUrl);
    }

    //Call
    public void call(Context context, String target, String teamId) {
        Call call = callManager.get();
        if (call!=null) {
            call.setConfig(target, teamId);
            call.call(context);
        }
    }

    public void videoCall(Context context, String target, String teamId) {
        Call call = callManager.get();
        if (call!=null) {
            call.setConfig(target, teamId);
            call.videoCall(context);
        }
    }

    public void screenCall(Context context, Intent data, String target, String teamId) {
        Call call = callManager.get();
        if (call!=null) {
            call.setConfig(target, teamId);
            call.screenCall(context, data);
        }
    }

    public void accept(Context context) {
        Call call = callManager.get();
        if (call!=null) {
            call.acceptCall(context, callInfo.getSdp());
        }
    }

    public void acceptVideoCall(Context context) {
        Call call = callManager.get();
        if (call!=null) {
            call.acceptVideoCall(context, callInfo.getSdp());
        }
    }

    public void acceptScreenCall(Context context, Intent data) {
        Call call = callManager.get();
        if (call!=null) {
            call.acceptScreenCall(context, callInfo.getSdp(), data);
        }
    }

    public void end() {
        Call call = callManager.get();
        if (call!=null) {
            if (call.getCallState() == CallState.calling)
                hangup();
            else
                cancel();
        } else {
            reject();
        }
    }

    public void cancel() {
        Call call = callManager.get();
        if (call!=null) {
            call.cancel();
        }
    }

    public void hangup() {
        Call call = callManager.get();
        if (call!=null) {
            call.hangup();
        }
    }

    public void reject() {
        CallCore.getInstance().rejectCall();
    }

    public void initView() {
        Call call = callManager.get();
        if (call!=null) {
            call.initView();
        }
    }

    public void setVideoView(ConnectView cvFullView, ConnectView cvSmallView) {
        Call call = callManager.get();
        if (call == null) {
            call = new Call();
            callManager.add(call);
        }
        call.setVideoView(cvFullView, cvSmallView);
    }

    public void swapCamera(boolean swap) {
        Call call = callManager.get();
        call.swapCamera(swap);
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
            CallCore callCore = CallCore.getInstance();
            callCore.stop();
            release();
            ConnectAction.getInstance().onUnRegistrationSuccessObserver();
        }

        @Override
        public void onSocketClosure() {
            Log.d(TAG, "onSocketClosure");
            release();
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
