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
import io.dotconnect.api.util.NetworkUtil;
import io.dotconnect.api.view.ConnectView;
import io.dotconnect.signaling.callJni.CallCore;
import io.dotconnect.signaling.observer.SignalingAction;
import io.dotconnect.signaling.observer.SignalingCallInfo;
import io.dotconnect.signaling.observer.SignalingMessageInfo;
import io.dotconnect.signaling.observer.SignalingObserver;

import static io.dotconnect.api.enum_class.CallState.ACCEPT_PENDING;
import static io.dotconnect.api.enum_class.CallState.INCOMING_CONNECT_READY;
import static io.dotconnect.api.util.APIConfiguration.APP_NAME;
import static io.dotconnect.api.util.APIConfiguration.DOMAIN;

public class ConnectManager {
    private static final String TAG = "ConnectManager";
    private static ConnectManager instance;

    private CallManager callManager;

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

        callManager = CallManager.getInstance();
    }

    public static ConnectManager getInstance() {
        if (instance == null) {
            instance = new ConnectManager();
        }
        return instance;
    }

    private void release() {
        Log.d(TAG, "release");
        if (callManager.get()!=null) {
            callManager.get().setCallState(CallState.IDLE);
            callManager.get().disconnect();
        }
        Register.getInstance().release();
        instance = null;
        SignalingAction.getInstance().delete(registrationObserver);
        SignalingAction.getInstance().delete(messageObserver);
        SignalingAction.getInstance().delete(callObserver);
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
        String networkType = NetworkUtil.getNetworkType(context);
        String ipAddress = NetworkUtil.getIPAddress(networkType);
        CallCore.getInstance().applyNetworkChange(networkType, ipAddress, "");
    }

    //SignalingMessageInfo
    public int sendMessageToGroup(String targetGroupId, String message, String deviceId) {
        String targetEmail = String.format("sip:%s@%s",targetGroupId, DOMAIN);
        return new Message().sendMessage(targetEmail, message, deviceId, MessageType.group, MessageDetail.valueOf(""));
    }

    public int sendMessageToGroup(String targetGroupId, String message, String deviceId, String domain) {
        String targetEmail = String.format("sip:%s@%s",targetGroupId, domain);
        return new Message().sendMessage(targetEmail, message, deviceId, MessageType.group, MessageDetail.valueOf(""));
    }

    public int sendMessageToUserId(String targetUserId, String message, String deviceId) {
        String targetEmail = String.format("sip:%s@%s",targetUserId, DOMAIN);
        return new Message().sendMessage(targetEmail, message, deviceId, MessageType.userId, MessageDetail.valueOf(""));
    }

    public int sendMessageToUserId(String targetUserId, String message, String deviceId, String domain) {
        String targetEmail = String.format("sip:%s@%s",targetUserId, domain);
        return new Message().sendMessage(targetEmail, message, deviceId, MessageType.userId, MessageDetail.valueOf(""));
    }

    public int sendMessageToDeviceId(String targetDeviceId, String message, String deviceId) {
        String targetEmail = String.format("sip:%s@%s",targetDeviceId, DOMAIN);
        return new Message().sendMessage(targetEmail, message, deviceId, MessageType.uuid, MessageDetail.valueOf(""));
    }

    public int sendMessageToDeviceId(String targetDeviceId, String message, String deviceId, String domain) {
        String targetEmail = String.format("sip:%s@%s",targetDeviceId, domain);
        return new Message().sendMessage(targetEmail, message, deviceId, MessageType.uuid, MessageDetail.valueOf(""));
    }

    public void requestCctv(Context context, String targetWallPadId, String deviceId, ConnectView cvFullView) {
        requestCctv(context, targetWallPadId, deviceId, DOMAIN, cvFullView);
    }

    public void requestCctv(Context context, String targetWallPadId, String deviceId, String domain, ConnectView cvFullView) {
        String targetEmail = String.format("sip:%s@%s",targetWallPadId, domain);

        new Message().sendMessage(targetEmail, "", deviceId, MessageType.cctv, MessageDetail.cctvRequest);
//        Call call;
//        if (callManager.size() == 0) {
//            call = createCall();
//            call.setContext(context);
//            call.setConfig(targetEmail);
//            initView(cvFullView, null);
//            call.requestCctv(deviceId);
//        }
    }

    public int requestControl(String targetWallPadId, String deviceId, String requestBody) {
        String targetEmail = String.format("sip:%s@%s",targetWallPadId, DOMAIN);
        return new Message().sendMessage(targetEmail, requestBody, deviceId, MessageType.control, MessageDetail.valueOf(""));
    }

    public int requestControl(String targetWallPadId, String deviceId, String domain, String requestBody) {
        String targetEmail = String.format("sip:%s@%s",targetWallPadId, domain);
        return new Message().sendMessage(targetEmail, requestBody, deviceId, MessageType.control, MessageDetail.valueOf(""));
    }

    //SignalingCallInfo

    private Call createCall() {
        Log.d(TAG, "createCall() callManager.size() is " + callManager.size());
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
        Log.d(APP_NAME, "acceptCall(Context)");
        acceptCall(context, null, null);
    }

    public void acceptCall(Context context, ConnectView cvFullView) {
        Log.d(APP_NAME, "acceptCall(Context, ConnectView)");
        acceptCall(context, cvFullView, null);
    }

    private void acceptCall(Context context, ConnectView cvFullView, ConnectView cvSmallView) {
        Log.d(APP_NAME, "acceptCall(Context, ConnectView, ConnectView)");
        Call call;
        if (callManager.size() == 0) {
            call = createCall();
            call.setCallState(ACCEPT_PENDING);
            Log.d(TAG, "acceptCall() callSize is 0 callState is " + call.getCallState());
            call.setContext(context);
            initView(cvFullView, cvSmallView);
            call.setInit();
        } else {
            call = callManager.get();
            Log.d(TAG, "acceptCall() callSize is " + callManager.size() + " callState is " + call.getCallState());
            if (call.getCallState()==INCOMING_CONNECT_READY) {
                call.setCallState(ACCEPT_PENDING);
                call.setContext(context);
                if (!call.isInit())
                    initView(cvFullView, cvSmallView);
                switch (call.getApiCallInfo().getCallType()) {
                    case One_Audio:
                        call.acceptCall();
                        break;
                    case One_Video:
                        call.acceptVideoCall();
                        break;
                    default:
                        break;
                }
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
            Log.d(TAG, "end() callSize is 0 callState is " + call.getCallState());
        } else {
            call = callManager.get();
            Log.d(TAG, "end() callSize is " + callManager.size() + " callState is " + call.getCallState());if (call.getCallState() == CallState.CCTV) {
                io.dotconnect.api.observer.ApiCallInfo apiCallInfo = new ApiCallInfo();
                apiCallInfo.setStatusCode(200);
                apiCallInfo.setMessage("OK");
                ConnectAction.getInstance().onTerminatedObserver(apiCallInfo);
                if (callManager.get()!=null) {
                    callManager.get().setCallState(CallState.IDLE);
                    callManager.get().disconnect();
                }
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
        Log.d(TAG, "initView()");
        Call call = callManager.get();
        call.setVideoView(cvFullView, cvSmallView);
        call.initView();
    }

    public void setScaleType(RendererCommon.ScalingType scaleType) {
        Call call = callManager.get();
        if (call!=null)
            call.setScaleType(scaleType);
    }

    public void disconnect() {
        if (callManager.get()!=null) {
            callManager.get().setCallState(CallState.IDLE);
            callManager.get().disconnect();
        }
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
                if (call.getCallState() == CallState.REJECT_PENDING) {
                    reject();
                }
            }

            ConnectAction.getInstance().onIncomingCallObserver(call.getApiCallInfo());
        }

        @Override
        public void onCallConnected(SignalingCallInfo signalingCallInfo) {
            Log.d(TAG, "onCallConnected");
            ConnectAction.getInstance().onCallConnectedObserver(new ApiCallInfo(signalingCallInfo));
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
        public void onOffer(SignalingCallInfo signalingCallInfo) {
            if (callManager.size()!=0) {
                Call call = callManager.get();
                ApiCallInfo callInfo = call.getApiCallInfo();
                callInfo.setSdp(signalingCallInfo.getSdp());
                Log.d(TAG, "onOffer() callSize is " + callManager.size() + " callState is " + call.getCallState());
                if (call.getCallState() == ACCEPT_PENDING) {
                    call.setCallState(CallState.INCOMING_CONNECT_READY);
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(() -> acceptCall(call.getContext()));
                }
            }
        }

        @Override
        public void onBusyOnIncomingCall(SignalingCallInfo signalingCallInfo) {
            Log.d(TAG, "onBusyOnIncomingCall");
        }

        @Override
        public void onCancelCallBefore180(SignalingCallInfo signalingCallInfo) {
            Log.d(TAG, "onCancelCallBefore180");
        }
    };
}
