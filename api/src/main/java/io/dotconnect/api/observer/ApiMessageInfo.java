package io.dotconnect.api.observer;

import io.dotconnect.api.enum_class.MessageType;
import io.dotconnect.signaling.observer.SignalingMessageInfo;

public class ApiMessageInfo {

    private String message, messageId;
    private MessageType messageType;

    public ApiMessageInfo(SignalingMessageInfo signalingMessageInfo) {
        this.message = signalingMessageInfo.getMessage();
        this.messageType = signalingMessageInfo.getMessageType();
        this.messageId = signalingMessageInfo.getMessageId();
    }

    public String getMessage() {
        return message;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public String getMessageId() {
        return messageId;
    }
}
