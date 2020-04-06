package io.dotconnect.signaling.observer;

import io.dotconnect.signaling.callJni.SipMessage;

public class SignalingCallInfo {

    private String counterpart, reason, message, method, sdp;
    private int statusCode, cause;

    public SignalingCallInfo(SipMessage sipMessage) {
        if (sipMessage!=null) {
            this.counterpart = sipMessage.getFromId();
            this.statusCode = sipMessage.getStatusCode();
            this.cause = sipMessage.getCause();
            this.reason = sipMessage.getReason();
            this.message = sipMessage.getMessage();
            this.method = sipMessage.getMethod();
            this.sdp = sipMessage.getSdp();
        }
    }

    public String getCounterpart() {
        return counterpart;
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

    public String getSdp() {
        return sdp;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public int getCause() {
        return cause;
    }
}
