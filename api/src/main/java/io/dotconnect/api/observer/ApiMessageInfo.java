package io.dotconnect.api.observer;

import io.dotconnect.signaling.observer.SignalingMessageInfo;

public class ApiMessageInfo {

    private String message, chatId, messageType, fileType, fileUrl,
            messageId, messageDate, teamId, chatType;
    private int messageSeq;

    public ApiMessageInfo(SignalingMessageInfo signalingMessageInfo) {
        this.message = signalingMessageInfo.getMessage();
        this.messageType = signalingMessageInfo.getMessageType();
        this.messageId = signalingMessageInfo.getMessageId();
        this.messageDate = signalingMessageInfo.getMessageDate();
    }

    public String getMessage() {
        return message;
    }

    public String getMessageType() {
        return messageType;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getMessageDate() {
        return messageDate;
    }
}
