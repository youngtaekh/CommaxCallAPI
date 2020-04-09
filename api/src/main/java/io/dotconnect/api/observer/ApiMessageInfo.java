package io.dotconnect.api.observer;

import io.dotconnect.api.enum_class.MessageDetail;
import io.dotconnect.api.enum_class.MessageType;
import io.dotconnect.signaling.observer.SignalingMessageInfo;

public class ApiMessageInfo {

    private String senderEmail, message, messageId;
    private MessageType messageType;
    private MessageDetail messageDetail;

    public ApiMessageInfo(SignalingMessageInfo signalingMessageInfo) {
        this.senderEmail = signalingMessageInfo.getSenderEmail();
        this.message = signalingMessageInfo.getMessage();
        this.messageType = signalingMessageInfo.getMessageType();
        this.messageId = signalingMessageInfo.getMessageId();
        this.messageDetail = signalingMessageInfo.getMessageDetail();
    }

    public String getSenderEmail() {
        return senderEmail;
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

    public MessageDetail getMessageDetail() {
        return messageDetail;
    }

    @Override
    public String toString() {
        return "ApiMessageInfo{" +
                "senderEmail='" + senderEmail + '\'' +
                ", message='" + message + '\'' +
                ", messageId='" + messageId + '\'' +
                ", messageType=" + messageType +
                ", messageDetail=" + messageDetail +
                '}';
    }
}
