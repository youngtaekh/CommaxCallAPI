package io.dotconnect.api;

import android.content.Context;

import org.webrtc.RendererCommon;

import io.dotconnect.api.enum_class.CallState;
import io.dotconnect.api.enum_class.MessageType;
import io.dotconnect.api.observer.ApiCallInfo;
import io.dotconnect.api.util.AuthenticationUtil;
import io.dotconnect.api.view.ConnectView;
import io.dotconnect.p2p.P2PManager;
import io.dotconnect.p2p.SDPListener;
import io.dotconnect.signaling.callJni.CallCore;

class Call {
    enum SDPType {
        call,
        acceptCall,
        acceptVideoCall,
        cctv
    }

    private Context mContext;
    private String callId;
    private SDPType sdpType;
    private CallState callState;
    private ApiCallInfo apiCallInfo;
    private P2PManager p2pManager;
    private String target, deviceId;
    private boolean isInit;

    private SDPListener listener = new SDPListener() {
        @Override
        public void onLocalDescription(String localSDP) {
            switch (Call.this.sdpType) {
                case acceptCall:
                case acceptVideoCall:
                    CallCore.getInstance().acceptCall(localSDP);
                    break;
                case cctv:
                    new Message().sendMessage(target, localSDP, deviceId, MessageType.cctv);
                    break;
            }
        }
    };

    Call(){
        this.callId = AuthenticationUtil.getEncryptedHashId(String.valueOf(System.currentTimeMillis()));
        this.callState = CallState.IDLE;
        this.isInit = false;
    }

    void setConfig(String target) {
        this.target = target;
    }

    void call() {
        sdpType = SDPType.call;
        getSDP(mContext, true, false, false, null);
    }

    void acceptCall() {
        sdpType = SDPType.acceptCall;
        getSDP(mContext, true, false, false, this.apiCallInfo.getSdp());
    }

    void acceptVideoCall() {
        sdpType = SDPType.acceptVideoCall;
        getSDP(mContext, true, true, false, this.apiCallInfo.getSdp());
    }

    void requestCctv(String deviceId) {
        this.deviceId = deviceId;
        sdpType = SDPType.cctv;
        callState = CallState.CCTV;
        getSDP(mContext, false, true, true, null);
    }

    void cancel() {
        CallCore.getInstance().cancelCall();
        if (p2pManager!=null)
            p2pManager.disconnect();
    }

    void hangup() {
        CallCore.getInstance().hangupCall();
        if (p2pManager!=null)
            p2pManager.disconnect();
    }

    void reject() {
        CallCore.getInstance().rejectCall();
        if (p2pManager!=null)
            p2pManager.disconnect();
    }

    void disconnect() {
        if (p2pManager!=null) {
            p2pManager.disconnect();
            p2pManager = null;
        }
    }

    void initView() {
        if (p2pManager==null)
            p2pManager = new P2PManager();
        p2pManager.initRenderer();
    }

    void setVideoView(ConnectView cvFullView, ConnectView cvSmallView) {
        if (p2pManager==null)
            p2pManager = new P2PManager();
        p2pManager.setRenderer(cvFullView, cvSmallView);
    }

    void setRemoteDescription(String description) {
        if (p2pManager!=null)
            p2pManager.setRemoteDescription(description);
    }

    void setScaleType(RendererCommon.ScalingType scaleType) {
        if (p2pManager!=null)
            p2pManager.setScaleType(scaleType);
    }

    private void getSDP(Context context, boolean video, String remoteSDP) {
        getSDP(context, true, video, true, remoteSDP);
    }

    private void getSDP(Context context, boolean audio, boolean video, boolean videoRecvOnly, String remoteSDP) {
        boolean offer = remoteSDP==null;
        p2pManager.setParameters(context, audio, video, videoRecvOnly);
        p2pManager.startCall(context, offer, remoteSDP, listener);
    }

    Context getContext() {
        return mContext;
    }

    void setContext(Context context) {
        this.mContext = context;
    }

    String getCallId() {
        return callId;
    }

    void setCallId(String callId) {
        this.callId = callId;
    }

    CallState getCallState() {
        return callState;
    }

    void setCallState(CallState callState) {
        this.callState = callState;
    }

    ApiCallInfo getApiCallInfo() {
        return apiCallInfo;
    }

    void setApiCallInfo(ApiCallInfo apiCallInfo) {
        this.apiCallInfo = apiCallInfo;
    }

    boolean isInit() {
        return isInit;
    }

    void setInit() {
        isInit = true;
    }
}
