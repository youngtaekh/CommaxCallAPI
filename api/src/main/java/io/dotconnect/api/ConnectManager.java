package io.dotconnect.api;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.webrtc.RendererCommon;

import io.dotconnect.api.enum_class.CallState;
import io.dotconnect.api.enum_class.MessageDetail;
import io.dotconnect.api.enum_class.MessageType;
import io.dotconnect.api.observer.ApiCallInfo;
import io.dotconnect.api.observer.ApiMessageInfo;
import io.dotconnect.api.observer.ConnectAction;
import io.dotconnect.api.view.ConnectView;
import io.dotconnect.p2p.observer.P2PAction;
import io.dotconnect.p2p.observer.P2PObserver;
import io.dotconnect.signaling.callJni.CallCore;
import io.dotconnect.signaling.observer.SignalingAction;
import io.dotconnect.signaling.observer.SignalingCallInfo;
import io.dotconnect.signaling.observer.SignalingMessageInfo;
import io.dotconnect.signaling.observer.SignalingObserver;

import static io.dotconnect.api.enum_class.CallState.ACCEPT_PENDING;
import static io.dotconnect.api.enum_class.CallState.CCTV;
import static io.dotconnect.api.enum_class.CallState.INCOMING_CONNECT_READY;
import static io.dotconnect.api.util.APIConfiguration.APP_NAME;
import static io.dotconnect.api.util.APIConfiguration.DOMAIN;

public class ConnectManager {
    private static final String TAG = "ConnectManager - ";
    private static ConnectManager instance;

    private CallManager callManager;
    private String deviceId;

    private Handler coreStopHandler;
    private Runnable coreStopRunnable = () -> {
        CallCore.getInstance().stop();
        release();
        ConnectAction.getInstance().onUnRegistrationSuccessObserver();
    };

    private Boolean endBlock;
    private Handler endBlockHandler;
    private Runnable endBlockRunnable = ()->endBlock = false;

    private ConnectManager() {
        endBlock = false;
        SignalingAction.getInstance().add(registrationObserver);
        SignalingAction.getInstance().add(messageObserver);
        SignalingAction.getInstance().add(callObserver);
        P2PAction.getInstance().add(p2PObserver);

        callManager = CallManager.getInstance();
    }

    public static ConnectManager getInstance() {
        if (instance == null) {
            instance = new ConnectManager();
        }
        return instance;
    }

    private void release() {
        Log.d(APP_NAME, TAG + "release");
        if (callManager.get()!=null) {
            callManager.get().setCallState(CallState.IDLE);
            callManager.get().disconnect();
        }
        Register.getInstance().release();
        instance = null;
        SignalingAction.getInstance().delete(registrationObserver);
        SignalingAction.getInstance().delete(messageObserver);
        SignalingAction.getInstance().delete(callObserver);
        P2PAction.getInstance().delete(p2PObserver);
    }

    //Register
    public void deviceRegistration(String deviceId, String userId, String appId, String accessToken, String fcmToken) {
        Register.getInstance().deviceCheck(deviceId, userId, appId, accessToken, fcmToken);
    }

    public void deviceUnRegistration(String deviceId, String accessToken) {
        Register.getInstance().deviceUnRegistration(deviceId, accessToken);
    }

    /**
     *
     * @param context application context
     * @param userId user id
     * @param appId app id
     * @param accessToken access token
     * @param fcmToken Firebase Push Token
     */
    public void startRegistration(Context context, String deviceId, String userId, String appId, String accessToken, String fcmToken) {
        Register.getInstance().start(context, deviceId, userId, appId, accessToken, fcmToken);
    }

    public void startRegistration(Context context, String deviceId, String userId, String appId, String accessToken, String fcmToken, String outboundProxy) {
        Register.getInstance().start(context, deviceId, userId, appId, accessToken, fcmToken, outboundProxy);
    }

    public void startRegistration(Context context, String deviceId, String userId, String appId,
                                  String accessToken, String fcmToken, String domain, String outboundProxy) {
        Register.getInstance().start(context, deviceId, userId, appId, accessToken, fcmToken, domain, outboundProxy);
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

    public void networkChange(Context context) {
//        String networkType = NetworkUtil.getNetworkType(context);
//        String ipAddress = NetworkUtil.getIPAddress(networkType);
//        CallCore.getInstance().applyNetworkChange(networkType, ipAddress, "");
    }

    //SignalingMessageInfo
    public int sendMessageToGroup(String targetGroupId, String appId, String message, String deviceId) {
        String targetEmail = String.format("sip:%s@%s.%s",targetGroupId, appId, DOMAIN);
        return new Message().sendMessage(targetEmail, message, deviceId, MessageType.group);
    }

    public int sendMessageToUserId(String targetUserId, String appId, String message, String deviceId) {
        String targetEmail = String.format("sip:%s@%s.%s",targetUserId, appId, DOMAIN);
        return new Message().sendMessage(targetEmail, message, deviceId, MessageType.userId);
    }

    public int sendMessageToDeviceId(String targetDeviceId, String appId, String message, String deviceId) {
        String targetEmail = String.format("sip:%s@%s.%s",targetDeviceId, appId, DOMAIN);
        return new Message().sendMessage(targetEmail, message, deviceId, MessageType.uuid);
    }

    public void requestCctv(Context context, String targetWallPadId, String appId, String deviceId,
                            ConnectView cvFullView, String message) {
        String targetEmail = String.format("sip:%s@%s.%s",targetWallPadId, appId, DOMAIN);
        this.deviceId = deviceId;
        Call call;
        if (callManager.size() == 0) {
            call = createCall();
            call.setContext(context);
            call.setConfig(targetEmail);
            call.setCallState(CCTV);
            call.setMessageId(new Message().getMessageId(deviceId));
            initView(cvFullView);
            call.setInit();
            new Message().sendMessage(targetEmail, message, deviceId, call.getMessageId(), MessageType.cctv, MessageDetail.request);
        }
    }

    public int requestControl(String targetWallPadId, String appId, String deviceId, String requestBody) {
        String targetEmail = String.format("sip:%s@%s.%s",targetWallPadId, appId, DOMAIN);
        return new Message().sendMessage(targetEmail, requestBody, deviceId, MessageType.control);
    }

    //SignalingCallInfo

    private Call createCall() {
        Log.d(APP_NAME, TAG + "createCall()");
        Call call = new Call();
        callManager.add(call);
        return call;
    }

    private void call(Context context, String target) {
        Call call;
        if (callManager.size() == 0) {
            call = createCall();
            call.setContext(context);
            call.setConfig(target);
            call.call();
        }
    }

    private void acceptCall(Context context) {
        Log.d(APP_NAME, TAG + "acceptCall(Context)");
        acceptCall(context, null);
    }

    public void acceptCall(Context context, ConnectView cvFullView) {
        Log.d(APP_NAME, TAG + "acceptCall(Context, ConnectView)");
        Call call;
        if (callManager.size() == 0) {
            call = createCall();
            call.setCallState(ACCEPT_PENDING);
            call.setContext(context);
            initView(cvFullView);
            call.setInit();
        } else {
            call = callManager.get();
            if (call.getCallState()==INCOMING_CONNECT_READY) {
                call.setCallState(ACCEPT_PENDING);
                call.setContext(context);
                if (!call.isInit())
                    initView(cvFullView);
                call.acceptCall();
            }
        }
    }

    private boolean isEndBlock() {
        if (endBlock)
            return true;
        endBlock = true;
        if (endBlockHandler == null)
            endBlockHandler = new Handler(Looper.getMainLooper());
        endBlockHandler.postDelayed(endBlockRunnable, 1000);
        return false;
    }

    /**
     * if call state is calling, hangup the call
     * else reject the call
     */
    public void end() {
        if (isEndBlock()) {
            return;
        }
        Call call;
        if (callManager.size()==0) {
            call = createCall();
            call.setCallState(CallState.REJECT_PENDING);
        } else {
            call = callManager.get();
            if (call.getCallState() == CallState.CCTV) {
                ApiCallInfo apiCallInfo = new ApiCallInfo();
                ConnectAction.getInstance().onTerminatedObserver(apiCallInfo.makeOK());
                call.end();
                disconnect();
                callManager.remove();
            }
            if (call.getCallState()!=CallState.END_PENDING && call.getCallState()!=CallState.REJECT_PENDING) {
                if (call.getCallState() == CallState.CONNECTED) {
                    call.setCallState(CallState.END_PENDING);
                    hangup();
                } else if (call.getCallState() == CallState.INCOMING_CONNECT_READY
                        || call.getCallState() == ACCEPT_PENDING) {
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
            ApiCallInfo apiCallInfo = new ApiCallInfo();
            ConnectAction.getInstance().onTerminatedObserver(apiCallInfo.makeReject());
            disconnect();
            callManager.remove();
        }
    }

    private void initView(ConnectView cvFullView) {
        Log.d(APP_NAME, TAG + "initView(ConnectView)");
        Call call = callManager.get();
        call.setVideoView(cvFullView);
        call.initView();
    }

    public void setScaleType(RendererCommon.ScalingType scaleType) {
        Call call = callManager.get();
        if (call!=null)
            call.setScaleType(scaleType);
    }

    private void disconnect() {
        if (callManager.get()!=null) {
            callManager.get().setCallState(CallState.IDLE);
            callManager.get().disconnect();
        }
    }

    private SignalingObserver.RegistrationObserver registrationObserver = new SignalingObserver.RegistrationObserver() {
        @Override
        public void onRegistrationSuccess() {
            Log.d(APP_NAME, TAG + "onRegistrationSuccess");
            ConnectAction.getInstance().onRegistrationSuccessObserver();
        }

        @Override
        public void onRegistrationFailure() {
            Log.d(APP_NAME, TAG + "onRegistrationFailure");
            ConnectAction.getInstance().onRegistrationFailureObserver();
        }

        @Override
        public void onUnRegistrationSuccess() {
            Log.d(APP_NAME, TAG + "onUnRegistrationSuccess");
            if (coreStopHandler!=null) {
                coreStopHandler.removeCallbacks(coreStopRunnable);
                coreStopHandler = null;
            }
            CallCore.getInstance().stop();
            ConnectAction.getInstance().onUnRegistrationSuccessObserver();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            release();
        }

        @Override
        public void onSocketClosure() {
            Log.d(APP_NAME, TAG + "onSocketClosure");
            ConnectAction.getInstance().onSocketClosureObserver();
        }
    };

    private SignalingObserver.MessageObserver messageObserver = new SignalingObserver.MessageObserver() {
        @Override
        public void onMessageSendSuccess(SignalingMessageInfo signalingMessageInfo) {
            Log.d(APP_NAME, TAG + "onMessageSendSuccess");
            ConnectAction.getInstance().onMessageSendSuccessObserver(new ApiMessageInfo(signalingMessageInfo));
        }

        @Override
        public void onMessageSendFailure(SignalingMessageInfo signalingMessageInfo) {
            Log.d(APP_NAME, TAG + "onMessageSendFailure");
            ConnectAction.getInstance().onMessageSendFailureObserver(new ApiMessageInfo(signalingMessageInfo));
        }

        @Override
        public void onMessageArrival(SignalingMessageInfo signalingMessageInfo) {
            Log.d(APP_NAME, TAG + "onMessageArrival");
            if (signalingMessageInfo.getMessageDetail() == MessageDetail.offer
                    && MessageType.cctv == signalingMessageInfo.getMessageType()) {
                if (callManager.get()!=null) {

                    callManager.get().setApiMessageInfo(new ApiMessageInfo(signalingMessageInfo));
                    callManager.get().requestCctv(deviceId);
                }
            }
            ConnectAction.getInstance().onMessageArrivalObserver(new ApiMessageInfo(signalingMessageInfo));
        }
    };

    private SignalingObserver.CallObserver callObserver = new SignalingObserver.CallObserver() {
        @Override
        public void onIncomingCall(SignalingCallInfo signalingCallInfo) {
            Log.d(APP_NAME, TAG + "onIncomingCall");

            Call call;
            if (callManager.size()==0) {
                call = createCall();
                call.setApiCallInfo(new ApiCallInfo(signalingCallInfo));
                call.setCallState(CallState.INCOMING_CONNECT_READY);
            } else {
                call = callManager.get();
                call.setApiCallInfo(new ApiCallInfo(signalingCallInfo));
                if (call.getCallState() == CallState.REJECT_PENDING) {
                    reject();
                }
            }

            ConnectAction.getInstance().onIncomingCallObserver(call.getApiCallInfo());
        }

        @Override
        public void onCallConnected(SignalingCallInfo signalingCallInfo) {
            Log.d(APP_NAME, TAG + "onCallConnected");
            ConnectAction.getInstance().onCallConnectedObserver(new ApiCallInfo(signalingCallInfo));
            callManager.get().setCallState(CallState.CONNECTED);
        }

        @Override
        public void onFailure(SignalingCallInfo signalingCallInfo) {
            Log.d(APP_NAME, TAG + "onFailure");
            ConnectAction.getInstance().onFailureObserver(new ApiCallInfo(signalingCallInfo));
        }

        @Override
        public void onTerminated(SignalingCallInfo signalingCallInfo) {
            Log.d(APP_NAME, TAG + "onTerminated");
            ConnectAction.getInstance().onTerminatedObserver(new ApiCallInfo(signalingCallInfo));
            disconnect();
            callManager.remove();
        }

        @Override
        public void onOffer(SignalingCallInfo signalingCallInfo) {
            if (callManager.size()!=0) {
                Call call = callManager.get();
                ApiCallInfo callInfo = call.getApiCallInfo();
                callInfo.setSdp(signalingCallInfo.getSdp());
                if (call.getCallState() == ACCEPT_PENDING) {
                    call.setCallState(CallState.INCOMING_CONNECT_READY);
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(() -> acceptCall(call.getContext()));
                }
            }
        }

        @Override
        public void onBusyOnIncomingCall(SignalingCallInfo signalingCallInfo) {
            Log.d(APP_NAME, TAG + "onBusyOnIncomingCall");
        }

        @Override
        public void onCancelCallBefore180(SignalingCallInfo signalingCallInfo) {
            Log.d(APP_NAME, TAG + "onCancelCallBefore180");
        }
    };

    private P2PObserver p2PObserver = new P2PObserver() {
        @Override
        public void onConnected() {
            Log.d(APP_NAME, TAG + "onConnected");
            ConnectAction.getInstance().onConnectedObserver();
        }

        @Override
        public void onDisconnected() {
            Log.d(APP_NAME, TAG + "onDisconnected");
            ConnectAction.getInstance().onDisconnectedObserver();
        }

        @Override
        public void onFailed() {
            Log.d(APP_NAME, TAG + "onFailed");
            ConnectAction.getInstance().onFailedObserver();
        }

        @Override
        public void onClosed() {
            Log.d(APP_NAME, TAG + "onClosed");
            ConnectAction.getInstance().onClosedObserver();
        }

        @Override
        public void onError(String description) {
            Log.d(APP_NAME, TAG + "onError - " + description);
            ConnectAction.getInstance().onErrorObserver(description);
        }
    };
}
