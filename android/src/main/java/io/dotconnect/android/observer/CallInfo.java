package io.dotconnect.android.observer;

import io.dotconnect.android.enum_class.CallType;
import io.dotconnect.signaling.observer.Call;

public class CallInfo {
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

    private String counterpart, callKey, reason, message, method, teamId, sdp;
    private CallType callType;
    private int statusCode, cause, remoteTrackCount;

    public CallInfo(Call call) {
        this.counterpart = call.getCounterpart();
        this.callKey = call.getCallKey();
        this.statusCode = call.getStatusCode();
        this.cause = call.getCause();
        this.reason = call.getReason();
        this.message = call.getMessage();
        if (call.getCallType()!=null)
            this.callType = CallType.valueOf(call.getCallType());
        this.method = call.getMethod();
        this.teamId = call.getTeamId();
        this.sdp = call.getSdp();
        this.remoteTrackCount = call.getRemoteTrackCount();
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

    public CallType getCallType() {
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
