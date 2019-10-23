package io.dotconnect.signaling.observer;

import io.dotconnect.signaling.callJni.SipMessage;

public class Call {
    public static final String COUNTERPART = "counterpart";
    public static final String CALL_KEY = "callKey";
    public static final String STATUS_CODE = "statusCode";
    public static final String CAUSE = "cause";
    public static final String REASON = "reason";
    public static final String MESSAGE = "message";
    public static final String CALL_TYPE = "callType";
    public static final String METHOD = "method";
    public static final String TEAM_ID = "teamId";
    public static final String SDP = "sdp";
    public static final String REMOTE_TRACK_COUNT = "trackCount";

    private String counterpart, callKey, reason, message, callType, method, teamId, sdp;
    private int statusCode, cause, remoteTrackCount;

    public Call(SipMessage sipMessage) {
        if (sipMessage!=null) {
            this.counterpart = sipMessage.getFromId();
            this.callKey = sipMessage.getCallKey();
            this.statusCode = sipMessage.getStatusCode();
            this.cause = sipMessage.getCause();
            this.reason = sipMessage.getReason();
            this.message = sipMessage.getMessage();
            this.callType = sipMessage.getCallType();
            this.method = sipMessage.getMethod();
            this.teamId = sipMessage.getTeamId();
            this.sdp = sipMessage.getSdp();
            this.remoteTrackCount = sipMessage.getRemoteVideoCount();
        }
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

    public String getCallType() {
        return callType;
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
