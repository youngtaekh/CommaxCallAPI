package io.dotconnect.api;

import android.content.Context;
import android.content.Intent;
import io.dotconnect.api.enum_class.CallState;
import io.dotconnect.api.observer.APICallInfo;
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

    private String callId;
    private SDPType sdpType;
    private CallState callState;
    private APICallInfo APICallInfo;
    private P2PManager p2pManager;
    private String target, teamId, reason;
    private int cause;
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
    }

    Call(String target, String teamId) {
        this.target = target;
        this.teamId = teamId;
    }

    void setConfig(String target, String teamId) {
        this.target = target;
        this.teamId = teamId;
    }

    void call(Context context) {
        sdpType = SDPType.call;
        getSDP(context, false, false, null, null);
    }

    void reasonCall(Context context, String reason, int cause) {
        this.reason = reason;
        this.cause = cause;
        sdpType = SDPType.reasonCall;
        getSDP(context, false, false, null, null);
    }

    void pstnCall(Context context) {
        sdpType = SDPType.pstnCall;
        getSDP(context, false, false, null, null);
    }

    void videoCall(Context context) {
        sdpType = SDPType.videoCall;
        getSDP(context, true, false, null, null);
    }

    void screenCall(Context context, Intent data) {
        sdpType = SDPType.screenCall;
        getSDP(context, true, false, null, data);
    }

    void dataChannelCall(Context context) {
        sdpType = SDPType.call;
        getSDP(context, false, true, null, null);
    }

    void acceptCall(Context context, String remoteSDP) {
        sdpType = SDPType.acceptCall;
        getSDP(context, false, false, remoteSDP, null);
    }

    void acceptVideoCall(Context context, String remoteSDP) {
        sdpType = SDPType.acceptVideoCall;
        getSDP(context, true, false, remoteSDP, null);
    }

    void acceptScreenCall(Context context, String remoteSDP, Intent data) {
        sdpType = SDPType.acceptVideoCall;
        getSDP(context, true, false, remoteSDP, data);
    }

    void acceptDataChannelCall(Context context, String remoteSDP) {
        sdpType = SDPType.acceptCall;
        getSDP(context, false, true, remoteSDP, null);
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
        boolean offer = remoteSDP==null;
        boolean screen = data!=null;
        p2pManager.setParameters(context, video, screen, true, dataChannel, data);
        p2pManager.startCall(context, offer, remoteSDP, listener);
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

    public APICallInfo getAPICallInfo() {
        return APICallInfo;
    }

    public void setAPICallInfo(APICallInfo APICallInfo) {
        this.APICallInfo = APICallInfo;
    }
}
