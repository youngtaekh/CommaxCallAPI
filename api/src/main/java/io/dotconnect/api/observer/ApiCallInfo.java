package io.dotconnect.api.observer;

import io.dotconnect.api.enum_class.CallType;
import io.dotconnect.signaling.observer.SignalingCallInfo;

public class ApiCallInfo {

    private String counterpart, callKey, reason, message, method, teamId, sdp;
    private CallType callType;
    private int statusCode, cause, remoteTrackCount;

    public ApiCallInfo() {}

    public ApiCallInfo(SignalingCallInfo signalingCallInfo) {
        this.counterpart = signalingCallInfo.getCounterpart();
        this.callKey = signalingCallInfo.getCallKey();
        this.statusCode = signalingCallInfo.getStatusCode();
        this.cause = signalingCallInfo.getCause();
        this.reason = signalingCallInfo.getReason();
        this.message = signalingCallInfo.getMessage();
        if (signalingCallInfo.getCallType()!=null)
            this.callType = CallType.valueOf(signalingCallInfo.getCallType());
        this.method = signalingCallInfo.getMethod();
        this.teamId = signalingCallInfo.getTeamId();
        this.sdp = signalingCallInfo.getSdp();
        this.remoteTrackCount = signalingCallInfo.getRemoteTrackCount();
    }

    public String getCounterpart() {
        return counterpart;
    }

    public String getCallKey() {
        return callKey;
    }

    public String getReason() {
        return reason;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getMethod() {
        return method;
    }

    public String getTeamId() {
        return teamId;
    }

    public String getSdp() {
        return sdp;
    }

    public CallType getCallType() {
        return callType;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public int getCause() {
        return cause;
    }

    public int getRemoteTrackCount() {
        return remoteTrackCount;
    }
}
