package io.dotconnect.api;

import android.content.Context;
import android.content.Intent;
import io.dotconnect.api.enum_class.CallState;
import io.dotconnect.api.observer.ApiCallInfo;
import io.dotconnect.api.util.AuthenticationUtil;
import io.dotconnect.api.view.ConnectView;
import io.dotconnect.p2p.P2PManager;
import io.dotconnect.p2p.SDPListener;
import io.dotconnect.signaling.callJni.CallCore;
import org.webrtc.RendererCommon;

public class Call {
    enum SDPType {
        call,
        reasonCall,
        pstnCall,
        videoCall,
        screenCall,
        acceptCall,
        acceptVideoCall,
        acceptScreenCall
    }

    private Context mContext;
    private String callId;
    private SDPType sdpType;
    private CallState callState;
    private ApiCallInfo apiCallInfo;
    private P2PManager p2pManager;
    private String target, teamId, reason;
    private int cause;
    private boolean isInit;

    private SDPListener listener = new SDPListener() {
        @Override
        public void onLocalDescription(String localSDP) {
            switch (Call.this.sdpType) {
                case call:
                    CallCore.getInstance().makeCall(target, teamId, localSDP);
                    break;
                case reasonCall:
                    CallCore.getInstance().makeCallWithReason(target, teamId, localSDP, reason, cause);
                    break;
                case pstnCall:
                    CallCore.getInstance().makePSTNCall(target, teamId, localSDP);
                    break;
                case videoCall:
                    CallCore.getInstance().makeVideoCall(target, teamId, localSDP);
                    break;
                case screenCall:
                    CallCore.getInstance().makeVideoCall(target, teamId, localSDP);
                    break;
                case acceptCall:
                    CallCore.getInstance().acceptCall(localSDP);
                    break;
                case acceptVideoCall:
                case acceptScreenCall:
                    CallCore.getInstance().acceptCall(localSDP);
                    break;
            }
        }
    };

    Call(){
        this.callId = AuthenticationUtil.getEncryptedHashId(String.valueOf(System.currentTimeMillis()));
        this.callState = CallState.IDLE;
        this.isInit = false;
    }

    Call(String target, String teamId) {
        this.callId = AuthenticationUtil.getEncryptedHashId(String.valueOf(System.currentTimeMillis()));
        this.callState = CallState.IDLE;
        this.isInit = false;
        this.target = target;
        this.teamId = teamId;
    }

    void setConfig(String target, String teamId) {
        this.target = target;
        this.teamId = teamId;
    }

    void call() {
        sdpType = SDPType.call;
        getSDP(mContext, false, false, null, null);
    }

    void reasonCall(String reason, int cause) {
        this.reason = reason;
        this.cause = cause;
        sdpType = SDPType.reasonCall;
        getSDP(mContext, false, false, null, null);
    }

    void pstnCall() {
        sdpType = SDPType.pstnCall;
        getSDP(mContext, false, false, null, null);
    }

    void videoCall() {
        sdpType = SDPType.videoCall;
        getSDP(mContext, true, false, false, null, null);
    }

    void screenCall(Intent data) {
        sdpType = SDPType.screenCall;
        getSDP(mContext, true, false, null, data);
    }

    void dataChannelCall() {
        sdpType = SDPType.call;
        getSDP(mContext, false, true, null, null);
    }

    void acceptCall() {
        sdpType = SDPType.acceptCall;
        getSDP(mContext, false, false, this.apiCallInfo.getSdp(), null);
    }

    void acceptVideoCall() {
        sdpType = SDPType.acceptVideoCall;
        getSDP(mContext, true, false, this.apiCallInfo.getSdp(), null);
    }

    void acceptScreenCall(Intent data) {
        sdpType = SDPType.acceptVideoCall;
        getSDP(mContext, true, false, apiCallInfo.getSdp(), data);
    }

    void acceptDataChannelCall() {
        sdpType = SDPType.acceptCall;
        getSDP(mContext, false, true, apiCallInfo.getSdp(), null);
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

    void swapCamera(boolean swap) {
        if (p2pManager!=null)
            p2pManager.setSwappedFeeds(swap);
    }

    void setScaleType(RendererCommon.ScalingType scaleType) {
        if (p2pManager!=null)
            p2pManager.setScaleType(scaleType);
    }

    private void getSDP(Context context, boolean video, boolean dataChannel, String remoteSDP, Intent data) {
        getSDP(context, video, true, dataChannel, remoteSDP, data);
    }

    private void getSDP(Context context, boolean video, boolean videoRecvOnly, boolean dataChannel, String remoteSDP, Intent data) {
        boolean offer = remoteSDP==null;
        boolean screen = data!=null;
        p2pManager.setParameters(context, video, screen, videoRecvOnly, dataChannel, data);
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

    public ApiCallInfo getApiCallInfo() {
        return apiCallInfo;
    }

    public void setApiCallInfo(ApiCallInfo apiCallInfo) {
        this.apiCallInfo = apiCallInfo;
    }

    public boolean isInit() {
        return isInit;
    }

    public void setInit(boolean init) {
        isInit = init;
    }
}
