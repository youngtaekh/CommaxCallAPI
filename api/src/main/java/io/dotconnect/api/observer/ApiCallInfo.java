package io.dotconnect.api.observer;

import android.util.Log;

import io.dotconnect.signaling.observer.SignalingCallInfo;

public class ApiCallInfo {

    private String counterpart, reason, message, method, sdp;
    private int statusCode, cause;

    public ApiCallInfo() {}

    public ApiCallInfo(SignalingCallInfo signalingCallInfo) {
        this.counterpart = signalingCallInfo.getCounterpart();
        this.statusCode = signalingCallInfo.getStatusCode();
        this.cause = signalingCallInfo.getCause();
        this.reason = signalingCallInfo.getReason();
        this.message = signalingCallInfo.getMessage();
        this.method = signalingCallInfo.getMethod();
        this.sdp = signalingCallInfo.getSdp();
    }

    public String getCounterpart() {
        return counterpart;
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

    public void setSdp(String sdp) {
        Log.d("ApiCallInfo", "this.sdp.length : " + this.sdp.length());
        Log.d("ApiCallInfo", "sdp.length : " + sdp.length());
        if (this.sdp.length() < sdp.length())
            this.sdp = sdp;
    }

    public String getSdp() {
        return sdp;
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
}
