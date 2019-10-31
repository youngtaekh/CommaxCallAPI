package io.dotconnect.android;

import android.content.Context;
import android.content.Intent;
import io.dotconnect.android.enum_class.CallState;
import io.dotconnect.android.view.ConnectView;
import io.dotconnect.p2p.P2PManager;
import io.dotconnect.p2p.SDPListener;
import io.dotconnect.signaling.callJni.CallCore;

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

    Call(){}

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
        P2PManager.getInstance().disconnect();
    }

    void hangup() {
        CallCore.getInstance().hangupCall();
        P2PManager.getInstance().disconnect();
    }

    void reject() {
        CallCore.getInstance().rejectCall();
        P2PManager.getInstance().disconnect();
    }

    void disconnect() {
        P2PManager.getInstance().disconnect();
    }

    void initView() {
        P2PManager.getInstance().initRenderer();
    }

    void setVideoView(ConnectView cvFullView, ConnectView cvSmallView) {
        P2PManager.getInstance().setRenderer(cvFullView, cvSmallView);
    }

    void setRemoteDescription(String description) {
        P2PManager.getInstance().setRemoteDescription(description);
    }

    void swapCamera(boolean swap) {
        P2PManager.getInstance().setSwappedFeeds(swap);
    }

    private void getSDP(Context context, boolean video, boolean dataChannel, String remoteSDP, Intent data) {
        boolean offer = remoteSDP==null;
        boolean screen = data!=null;
        P2PManager.getInstance().setParameters(context, video, screen, dataChannel, data);
        P2PManager.getInstance().startCall(context, offer, remoteSDP, listener);
    }

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public CallState getCallState() {
        return callState;
    }

    public void setCallState(CallState callState) {
        this.callState = callState;
    }
}
