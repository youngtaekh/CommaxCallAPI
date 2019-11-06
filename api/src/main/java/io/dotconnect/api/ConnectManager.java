package io.dotconnect.api;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import io.dotconnect.api.enum_class.CallState;
import io.dotconnect.api.observer.APICallInfo;
import io.dotconnect.api.observer.APIMessageInfo;
import io.dotconnect.api.observer.ConnectAction;
import io.dotconnect.api.enum_class.MessageType;
import io.dotconnect.api.util.AuthenticationUtil;
import io.dotconnect.api.view.ConnectView;
import io.dotconnect.signaling.callJni.CallCore;
import io.dotconnect.signaling.observer.SignalingAction;
import io.dotconnect.signaling.observer.SignalingCallInfo;
import io.dotconnect.signaling.observer.SignalingMessageInfo;
import io.dotconnect.signaling.observer.SignalingObserver;
import org.webrtc.RendererCommon;

import static io.dotconnect.api.util.APIConfiguration.APP_NAME;

public class ConnectManager {
    private static final String TAG = "ConnectManager";
    private static ConnectManager instance;

    private CallManager callManager;
    private APICallInfo APICallInfo;
//    private SignalingCallInfo call;

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
    public void deviceRegistration(Context context, String accessToken, String fcmToken) {
        Register.getInstance().deviceCheck(context, accessToken, fcmToken);
    }

    //TODO : REST develop
    public void deviceUnRegistration(Context context, String accessToken) {
        Register.getInstance().deviceUnRegistration(context, accessToken);
    }

    /**
     *
     * @param context
     * @param userId
     * @param appId
     * @param accessToken
     * @param fcmToken Firebase Push Token
     */
    public void startRegistration(Context context, String userId, String appId, String accessToken, String fcmToken) {
        Register.getInstance().start(context, userId, appId, accessToken, fcmToken);
    }

    public void startRegistration(Context context, String userId, String appId, String accessToken, String fcmToken, String domain) {
        Register.getInstance().start(context, userId, appId, accessToken, fcmToken, domain);
    }

    public void startRegistration(Context context, String userId, String appId,
                                  String accessToken, String fcmToken, String domain, String outboundProxy) {
        Register.getInstance().start(context, userId, appId, accessToken, fcmToken, domain, outboundProxy);
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

    //SignalingMessageInfo
    private int sendMessage(Context context, String target, String teamId, String message,
                           String chatType, String chatId, MessageType messageType) {
        return new Message().sendMessage(target, teamId, message, AuthenticationUtil.getUUID(context), chatType, chatId, messageType);
    }

    private int sendFile(Context context, String target, String teamId, String message, String chatType,
                        String chatId, MessageType messageType, String fileType, String fileUrl) {
        return new Message().sendFile(target, teamId, message, chatType, AuthenticationUtil.getUUID(context), chatId, messageType, fileType, fileUrl);
    }

    //SignalingCallInfo

    private Call createCall() {
        Call call = new Call();
        callManager.add(call);
        return call;
    }

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
            call.acceptCall(context, APICallInfo.getSdp());
        }
    }

    /**
     * accept call with video
     * @param context
     */
    public int acceptVideoCall(Context context, ConnectView cvFullView, ConnectView cvSmallView) {
        Log.d(APP_NAME, "acceptVideoCall(Context, ConnectView, ConnectView)");

//        Call call;
//        if (callManager.size() == 0) {
//            call = createCall();
//            call.setCallState(CallState.ACCEPT_PENDING);
//        } else {
//            call = callManager.get();
//        }

        Call call = callManager.get();
        if (call!=null && APICallInfo !=null && call.getCallState()==CallState.incoming) {
            call.setCallState(CallState.incomingConnectTry);
            initView(cvFullView, cvSmallView);
            call.acceptVideoCall(context, APICallInfo.getSdp());
            return 0;
        }

        return -1;
    }

    public int acceptVideoCall(Context context, ConnectView cvFullView) {
        Log.d(APP_NAME, "acceptVideoCall(Context, ConnectView)");
        Call call = callManager.get();
        if (call!=null && APICallInfo !=null && call.getCallState()==CallState.incoming) {
            call.setCallState(CallState.incomingConnectTry);
            initView(cvFullView);
            call.acceptVideoCall(context, APICallInfo.getSdp());
            return 0;
        }

        return -1;
    }

    private void acceptScreenCall(Context context, Intent data) {
        Call call = callManager.get();
        if (call!=null) {
            call.acceptScreenCall(context, APICallInfo.getSdp(), data);
        }
    }

    /**
     * if call state is calling, hangup the call
     * else reject the call
     */
    public void end() {
        Call call = callManager.get();
        if (call!=null && call.getCallState()!=CallState.endTry) {
            if (call.getCallState() == CallState.calling) {
                call.setCallState(CallState.endTry);
                hangup();
            } else if (call.getCallState() == CallState.incoming) {
                call.setCallState(CallState.endTry);
                reject();
            } else {
                call.setCallState(CallState.endTry);
                cancel();
            }
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
        public void onMessageSendSuccess(SignalingMessageInfo signalingMessageInfo) {
            Log.d(TAG, "onMessageSendSuccess");
            ConnectAction.getInstance().onMessageSendSuccessObserver(new APIMessageInfo(signalingMessageInfo));
        }

        @Override
        public void onMessageSendFailure(SignalingMessageInfo signalingMessageInfo) {
            Log.d(TAG, "onMessageSendFailure");
            ConnectAction.getInstance().onMessageSendFailureObserver(new APIMessageInfo(signalingMessageInfo));
        }

        @Override
        public void onMessageArrival(SignalingMessageInfo signalingMessageInfo) {
            Log.d(TAG, "onMessageArrival");
            ConnectAction.getInstance().onMessageArrivalObserver(new APIMessageInfo(signalingMessageInfo));
        }
    };

    private SignalingObserver.CallObserver callObserver = new SignalingObserver.CallObserver() {
        @Override
        public void onIncomingCall(SignalingCallInfo signalingCallInfo) {
            Log.d(TAG, "onIncomingCall");



            ConnectManager.this.APICallInfo = new APICallInfo(signalingCallInfo);
            ConnectAction.getInstance().onIncomingCallObserver(APICallInfo);
            callManager.add(new Call(APICallInfo.getCounterpart(), APICallInfo.getTeamId()));
            callManager.get().setCallState(CallState.incoming);
        }

        @Override
        public void onOutgoingCall(SignalingCallInfo signalingCallInfo) {
            Log.d(TAG, "onOutgoingCall");
            ConnectAction.getInstance().onOutgoingCallObserver(new APICallInfo(signalingCallInfo));
            callManager.get().setCallState(CallState.sending);
        }

        @Override
        public void onUpdate(SignalingCallInfo signalingCallInfo) {
            Log.d(TAG, "onUpdate");
            ConnectAction.getInstance().onUpdateObserver(new APICallInfo(signalingCallInfo));
        }

        @Override
        public void onEarlyMedia(SignalingCallInfo signalingCallInfo) {
            Log.d(TAG, "onEarlyMedia");
            ConnectAction.getInstance().onEarlyMediaObserver(new APICallInfo(signalingCallInfo));
        }

        @Override
        public void onOutgoingCallConnected(SignalingCallInfo signalingCallInfo) {
            Log.d(TAG, "onOutgoingCallConnected");
            callManager.get().setRemoteDescription(signalingCallInfo.getSdp());
            ConnectAction.getInstance().onOutgoingCallConnectedObserver(new APICallInfo(signalingCallInfo));
            callManager.get().setCallState(CallState.calling);
        }

        @Override
        public void onIncomingCallConnected(SignalingCallInfo signalingCallInfo) {
            Log.d(TAG, "onIncomingCallConnected");
            ConnectAction.getInstance().onIncomingCallConnectedObserver(new APICallInfo(signalingCallInfo));
            callManager.get().setCallState(CallState.calling);
        }

        @Override
        public void onFailure(SignalingCallInfo signalingCallInfo) {
            Log.d(TAG, "onFailure");
            ConnectAction.getInstance().onFailureObserver(new APICallInfo(signalingCallInfo));
        }

        @Override
        public void onTerminated(SignalingCallInfo signalingCallInfo) {
            Log.d(TAG, "onTerminated");
            ConnectAction.getInstance().onTerminatedObserver(new APICallInfo(signalingCallInfo));
            callManager.get().setCallState(CallState.idle);
            if (callManager.get()!=null)
                callManager.get().disconnect();
            callManager.remove();
        }

        @Override
        public void onBusyOnIncomingCall(SignalingCallInfo signalingCallInfo) {
            Log.d(TAG, "onBusyOnIncomingCall");
            ConnectAction.getInstance().onBusyOnIncomingCallObserver(new APICallInfo(signalingCallInfo));
        }

        @Override
        public void onCancelCallBefore180(SignalingCallInfo signalingCallInfo) {
            Log.d(TAG, "onCancelCallBefore180");
            ConnectAction.getInstance().onCancelCallBefore180Observer(new APICallInfo(signalingCallInfo));
        }
    };
}
