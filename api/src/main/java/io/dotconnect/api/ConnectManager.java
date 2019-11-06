package io.dotconnect.api;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import io.dotconnect.api.enum_class.CallState;
import io.dotconnect.api.observer.ApiCallInfo;
import io.dotconnect.api.observer.ApiMessageInfo;
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

import static io.dotconnect.api.enum_class.CallState.ACCEPT_PENDING;
import static io.dotconnect.api.enum_class.CallState.INCOMING_CONNECT_READY;
import static io.dotconnect.api.util.APIConfiguration.APP_NAME;

public class ConnectManager {
    private static final String TAG = "ConnectManager";
    private static ConnectManager instance;

    private CallManager callManager;
    private io.dotconnect.api.observer.ApiCallInfo ApiCallInfo;
//    private SignalingCallInfo call;

    private Handler coreStopHandler;
    private Runnable coreStopRunnable = () -> {
        CallCore.getInstance().stop();
        release();
        ConnectAction.getInstance().onUnRegistrationSuccessObserver();
    };

    private boolean endBlock = false;
    private Handler endBlockHandler;
    private Runnable endBlockRunnable = () -> endBlock = false;

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
        Log.d(TAG, "createCall() callManager.size() is " + callManager.size());
        Call call = new Call();
        callManager.add(call);
        return call;
    }

    private void call(Context context, String target, String teamId) {
        Call call;
        if (callManager.size() == 0) {
            call = createCall();
            call.setContext(context);
            call.setConfig(target, teamId);
            call.call();
        }
    }

    private void videoCall(Context context, String target, String teamId, ConnectView cvFullView, ConnectView cvSmallView) {
        Call call;
        if (callManager.size() == 0) {
            call = createCall();
            call.setContext(context);
            call.setConfig(target, teamId);
            initView(cvFullView, cvSmallView);
            call.videoCall();
        }
    }

    private void screenCall(Context context, Intent data, String target, String teamId) {
        Call call;
        if (callManager.size() == 0) {
            call = createCall();
            call.setContext(context);
            call.setConfig(target, teamId);
            call.screenCall(data);
        }
    }

    private void acceptCall(Context context) {
        Log.d(APP_NAME, "acceptCall(Context)");
        acceptCall(context, null, null, null);
    }

    public void acceptCall(Context context, ConnectView cvFullView) {
        Log.d(APP_NAME, "acceptCall(Context, ConnectView)");
        acceptCall(context, cvFullView, null, null);
    }

    private void acceptCall(Context context, ConnectView cvFullView, ConnectView cvSmallView) {
        Log.d(APP_NAME, "acceptCall(Context, ConnectView, ConnectView)");
        acceptCall(context, cvFullView, cvSmallView, null);
    }

    private void acceptCall(Context context, ConnectView cvFullView, ConnectView cvSmallView, Intent data) {
        Log.d(APP_NAME, "acceptCall(Context, ConnectView, ConnectView, Intent)");
        Call call;
        if (callManager.size() == 0) {
            call = createCall();
            call.setCallState(ACCEPT_PENDING);
            Log.d(TAG, "acceptCall() callSize is 0 callState is " + call.getCallState());
            call.setContext(context);
        } else {
            call = callManager.get();
            Log.d(TAG, "acceptCall() callSize is " + callManager.size() + " callState is " + call.getCallState());
            if (call.getCallState()==INCOMING_CONNECT_READY) {
                call.setCallState(ACCEPT_PENDING);
                call.setContext(context);
                initView(cvFullView, cvSmallView);
                switch (call.getApiCallInfo().getCallType()) {
                    case One_Audio:
                        call.acceptCall();
                        break;
                    case One_Video:
                        call.acceptVideoCall();
                        break;
                    case One_Screencast:
                        call.acceptScreenCall(data);
                        break;
                    default:
                        break;
                }
            }
        }
    }

//    /**
//     *
//     * @param context
//     * @param cvFullView
//     * @param cvSmallView
//     * @return
//     */
//    public int acceptVideoCall(Context context, ConnectView cvFullView, ConnectView cvSmallView) {
//        Log.d(APP_NAME, "acceptVideoCall(Context, ConnectView, ConnectView)");
//
//        Call call;
//        if (callManager.size() == 0) {
//            call = createCall();
//            call.setCallState(CallState.ACCEPT_PENDING);
//        } else {
//            call = callManager.get();
//        }
//
//        Call call = callManager.get();
//        if (call!=null && ApiCallInfo !=null && call.getCallState()==CallState.incoming) {
//            call.setCallState(CallState.incomingConnectTry);
//            initView(cvFullView, cvSmallView);
//            call.acceptVideoCall(context, ApiCallInfo.getSdp());
//            return 0;
//        }
//
//        return -1;
//    }
//
//    public int acceptVideoCall(Context context, ConnectView cvFullView) {
//        Log.d(APP_NAME, "acceptVideoCall(Context, ConnectView)");
//        Call call = callManager.get();
//        if (call!=null && ApiCallInfo !=null && call.getCallState()==CallState.incoming) {
//            call.setCallState(CallState.incomingConnectTry);
//            initView(cvFullView);
//            call.acceptVideoCall(context, ApiCallInfo.getSdp());
//            return 0;
//        }
//
//        return -1;
//    }
//
//    private void acceptScreenCall(Context context, Intent data) {
//        Call call = callManager.get();
//        if (call!=null) {
//            call.acceptScreenCall(context, ApiCallInfo.getSdp(), data);
//        }
//    }

    private boolean isEndBlock() {
        if (endBlock)
            return true;
        endBlock = true;
        if (endBlockHandler == null)
            endBlockHandler = new Handler();
        endBlockHandler.postDelayed(endBlockRunnable, 1000);
        return false;
    }

    /**
     * if call state is calling, hangup the call
     * else reject the call
     */
    public void end() {
        if (isEndBlock())
            return;
        Call call;
        if (callManager.size()==0) {
            call = createCall();
            call.setCallState(CallState.REJECT_PENDING);
            Log.d(TAG, "end() callSize is " + callManager.size() + " callState is " + call.getCallState());
        } else {
            call = callManager.get();
            Log.d(TAG, "end() callSize is " + callManager.size() + " callState is " + call.getCallState());
            if (call.getCallState()!=CallState.END_PENDING && call.getCallState()!=CallState.REJECT_PENDING) {
                if (call.getCallState() == CallState.CONNECTED) {
                    call.setCallState(CallState.END_PENDING);
                    hangup();
                } else if (call.getCallState() == CallState.INCOMING_CONNECT_READY) {
                    call.setCallState(CallState.END_PENDING);
                    reject();
                } else {
                    call.setCallState(CallState.END_PENDING);
                    cancel();
                }
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
            io.dotconnect.api.observer.ApiCallInfo apiCallInfo = new ApiCallInfo();
            apiCallInfo.setStatusCode(603);
            apiCallInfo.setMessage("Reject");
            ConnectAction.getInstance().onTerminatedObserver(apiCallInfo);
            if (callManager.get()!=null) {
                callManager.get().setCallState(CallState.IDLE);
                callManager.get().disconnect();
            }
            callManager.remove();
            Log.d(TAG, "reject() callSize is " + callManager.size());
        }
    }

    private void initView(ConnectView cvFullView, ConnectView cvSmallView) {
        Call call = callManager.get();
        call.setVideoView(cvFullView, cvSmallView);
        call.initView();
    }

    /**
     * change video between ConnectView
     * @param swap
     */
    private void swapCamera(boolean swap) {
        Call call = callManager.get();
        if (call!=null)
            call.swapCamera(swap);
    }

    public void setScaleType(RendererCommon.ScalingType scaleType) {
        Call call = callManager.get();
        if (call!=null)
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
            ConnectAction.getInstance().onMessageSendSuccessObserver(new ApiMessageInfo(signalingMessageInfo));
        }

        @Override
        public void onMessageSendFailure(SignalingMessageInfo signalingMessageInfo) {
            Log.d(TAG, "onMessageSendFailure");
            ConnectAction.getInstance().onMessageSendFailureObserver(new ApiMessageInfo(signalingMessageInfo));
        }

        @Override
        public void onMessageArrival(SignalingMessageInfo signalingMessageInfo) {
            Log.d(TAG, "onMessageArrival");
            ConnectAction.getInstance().onMessageArrivalObserver(new ApiMessageInfo(signalingMessageInfo));
        }
    };

    private SignalingObserver.CallObserver callObserver = new SignalingObserver.CallObserver() {
        @Override
        public void onIncomingCall(SignalingCallInfo signalingCallInfo) {
            Log.d(TAG, "onIncomingCall");

            Call call;
            if (callManager.size()==0) {
                call = createCall();
                call.setApiCallInfo(new ApiCallInfo(signalingCallInfo));
                call.setCallState(CallState.INCOMING_CONNECT_READY);
                Log.d(TAG, "onIncomingCall() callSize is " + callManager.size() + " callState is " + call.getCallState());
            } else {
                call = callManager.get();
                Log.d(TAG, "onIncomingCall() callSize is " + callManager.size() + " callState is " + call.getCallState());
                call.setApiCallInfo(new ApiCallInfo(signalingCallInfo));
                if (call.getCallState() == ACCEPT_PENDING) {
                    call.setCallState(CallState.INCOMING_CONNECT_READY);
                    acceptCall(call.getContext(), call.getCvFullView(), call.getCvSmallView());
                } else if (call.getCallState() == CallState.REJECT_PENDING) {
                    reject();
                }
            }

            ConnectAction.getInstance().onIncomingCallObserver(call.getApiCallInfo());
        }

        @Override
        public void onOutgoingCall(SignalingCallInfo signalingCallInfo) {
            Log.d(TAG, "onOutgoingCall");
            ConnectAction.getInstance().onOutgoingCallObserver(new ApiCallInfo(signalingCallInfo));
            callManager.get().setCallState(CallState.OUTGOING_CONNECT_READY);
        }

        @Override
        public void onUpdate(SignalingCallInfo signalingCallInfo) {
            Log.d(TAG, "onUpdate");
            ConnectAction.getInstance().onUpdateObserver(new ApiCallInfo(signalingCallInfo));
        }

        @Override
        public void onEarlyMedia(SignalingCallInfo signalingCallInfo) {
            Log.d(TAG, "onEarlyMedia");
            ConnectAction.getInstance().onEarlyMediaObserver(new ApiCallInfo(signalingCallInfo));
        }

        @Override
        public void onOutgoingCallConnected(SignalingCallInfo signalingCallInfo) {
            Log.d(TAG, "onOutgoingCallConnected");
            callManager.get().setRemoteDescription(signalingCallInfo.getSdp());
            ConnectAction.getInstance().onOutgoingCallConnectedObserver(new ApiCallInfo(signalingCallInfo));
            callManager.get().setCallState(CallState.CONNECTED);
        }

        @Override
        public void onIncomingCallConnected(SignalingCallInfo signalingCallInfo) {
            Log.d(TAG, "onIncomingCallConnected");
            ConnectAction.getInstance().onIncomingCallConnectedObserver(new ApiCallInfo(signalingCallInfo));
            callManager.get().setCallState(CallState.CONNECTED);
        }

        @Override
        public void onFailure(SignalingCallInfo signalingCallInfo) {
            Log.d(TAG, "onFailure");
            ConnectAction.getInstance().onFailureObserver(new ApiCallInfo(signalingCallInfo));
        }

        @Override
        public void onTerminated(SignalingCallInfo signalingCallInfo) {
            Log.d(TAG, "onTerminated");
            ConnectAction.getInstance().onTerminatedObserver(new ApiCallInfo(signalingCallInfo));
            if (callManager.get()!=null) {
                callManager.get().setCallState(CallState.IDLE);
                callManager.get().disconnect();
            }
            callManager.remove();
            Log.d(TAG, "onTerminated() callSize is " + callManager.size());
        }

        @Override
        public void onBusyOnIncomingCall(SignalingCallInfo signalingCallInfo) {
            Log.d(TAG, "onBusyOnIncomingCall");
            ConnectAction.getInstance().onBusyOnIncomingCallObserver(new ApiCallInfo(signalingCallInfo));
        }

        @Override
        public void onCancelCallBefore180(SignalingCallInfo signalingCallInfo) {
            Log.d(TAG, "onCancelCallBefore180");
            ConnectAction.getInstance().onCancelCallBefore180Observer(new ApiCallInfo(signalingCallInfo));
        }
    };
}